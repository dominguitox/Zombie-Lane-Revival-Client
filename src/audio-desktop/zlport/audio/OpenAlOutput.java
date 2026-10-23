package zlport.audio;

import java.lang.System.Logger;
import java.lang.System.Logger.Level;
import java.nio.ByteBuffer;
import java.nio.ShortBuffer;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.function.Consumer;
import java.util.function.LongSupplier;
import org.lwjgl.BufferUtils;
import org.lwjgl.openal.AL;
import org.lwjgl.openal.AL10;
import org.lwjgl.openal.ALC;
import org.lwjgl.openal.ALC10;
import org.lwjgl.openal.ALCCapabilities;
import org.lwjgl.openal.ALCapabilities;
import org.lwjgl.openal.SOFTLoopback;
import org.lwjgl.openal.SOFTPauseDevice;
import zlport.android.media.AudioOutput;
import zlport.android.media.AudioOutput.Buffer;
import zlport.android.media.AudioOutput.Voice;

public final class OpenAlOutput implements AudioOutput, AutoCloseable {
   private static final Logger LOG = System.getLogger("zlport.audio");
   private static final Object AL_LOCK = new Object();
   private final long device;
   private final long context;
   private final boolean loopback;
   private final boolean canPause;
   private final List<Integer> buffers = new ArrayList<>();
   private final List<OpenAlOutput.AlVoice> voices = new ArrayList<>();
   private boolean closed;
   private boolean paused;
   private int errors;
   private final OpenAlOutput.ErrorLog errorLog = new OpenAlOutput.ErrorLog(() -> System.nanoTime() / 1000000L, message -> LOG.log(Level.WARNING, message));

   private OpenAlOutput(long device, long context, boolean loopback, boolean canPause) {
      this.device = device;
      this.context = context;
      this.loopback = loopback;
      this.canPause = canPause;
   }

   public static Optional<OpenAlOutput> open() {
      synchronized (AL_LOCK) {
         long dev = ALC10.alcOpenDevice((ByteBuffer)null);
         if (dev == 0L) {
            return Optional.empty();
         } else {
            ALCCapabilities caps = ALC.createCapabilities(dev);
            int[] attrs = caps.ALC_SOFT_HRTF ? new int[]{6546, 0, 0} : new int[]{0};
            long ctx = ALC10.alcCreateContext(dev, attrs);
            if (ctx == 0L) {
               ALC10.alcCloseDevice(dev);
               return Optional.empty();
            } else {
               return Optional.of(start(dev, ctx, caps, false));
            }
         }
      }
   }

   public static OpenAlOutput openLoopback(int sampleRate) {
      synchronized (AL_LOCK) {
         long dev = SOFTLoopback.alcLoopbackOpenDeviceSOFT((ByteBuffer)null);
         if (dev == 0L) {
            throw new IllegalStateException("OpenAL sem dispositivo loopback");
         } else {
            ALCCapabilities caps = ALC.createCapabilities(dev);
            long ctx = ALC10.alcCreateContext(dev, new int[]{6544, 5377, 6545, 5122, 4103, sampleRate, 6546, 0, 0});
            if (ctx == 0L) {
               ALC10.alcCloseDevice(dev);
               throw new IllegalStateException("OpenAL: contexto loopback recusado (" + sampleRate + " Hz)");
            } else {
               return start(dev, ctx, caps, true);
            }
         }
      }
   }

   private static OpenAlOutput start(long dev, long ctx, ALCCapabilities caps, boolean loopback) {
      ALC10.alcMakeContextCurrent(ctx);
      ALCapabilities al = AL.createCapabilities(caps);
      if (!al.AL_SOFT_direct_channels) {
         LOG.log(Level.WARNING, "OpenAL sem AL_SOFT_direct_channels: o som sai 4,5 dB mais baixo que no Android");
      }

      LOG.log(Level.INFO, (loopback ? "saída de som loopback: " : "saída de som: ") + ALC10.alcGetString(dev, 4101) + " (" + AL10.alGetString(45058) + ")");
      return new OpenAlOutput(dev, ctx, loopback, !loopback && caps.ALC_SOFT_pause_device);
   }

   private boolean current() {
      if (this.closed) {
         return false;
      }

      if (ALC10.alcGetCurrentContext() != this.context) {
         ALC10.alcMakeContextCurrent(this.context);
      }

      return true;
   }

   private void check(String what) {
      int e = AL10.alGetError();
      if (e != 0) {
         this.errors++;
         this.errorLog.error(e, what);
      } else {
         this.errorLog.tick();
      }
   }

   public int errors() {
      synchronized (AL_LOCK) {
         return this.errors;
      }
   }

   public Buffer load(short[] mono, int sampleRate) {
      short[] stereo = new short[Math.max(1, mono.length) * 2];

      for (int i = 0; i < mono.length; i++) {
         stereo[2 * i] = mono[i];
         stereo[2 * i + 1] = mono[i];
      }

      synchronized (AL_LOCK) {
         if (!this.current()) {
            return new OpenAlOutput.AlBuffer(0);
         }

         int b = AL10.alGenBuffers();
         AL10.alBufferData(b, 4355, stereo, sampleRate);
         this.check("alBufferData");
         this.buffers.add(b);
         return new OpenAlOutput.AlBuffer(b);
      }
   }

   public Voice newVoice() {
      synchronized (AL_LOCK) {
         if (!this.current()) {
            return new OpenAlOutput.AlVoice(0);
         }

         int source = AL10.alGenSources();
         AL10.alSourcei(source, 4147, 1);
         this.check("alGenSources");
         OpenAlOutput.AlVoice v = new OpenAlOutput.AlVoice(source);
         this.voices.add(v);
         return v;
      }
   }

   public void pause() {
      synchronized (AL_LOCK) {
         if (!this.closed && this.canPause && !this.paused) {
            SOFTPauseDevice.alcDevicePauseSOFT(this.device);
            this.paused = true;
         }
      }
   }

   public void resume() {
      synchronized (AL_LOCK) {
         if (!this.closed && this.paused) {
            SOFTPauseDevice.alcDeviceResumeSOFT(this.device);
            this.paused = false;
         }
      }
   }

   public short[] render(int frames) {
      synchronized (AL_LOCK) {
         if (!this.loopback) {
            throw new IllegalStateException("render só no dispositivo loopback");
         }

         if (this.closed) {
            throw new IllegalStateException("saída de som fechada");
         }

         ShortBuffer out = BufferUtils.createShortBuffer(frames * 2);
         SOFTLoopback.alcRenderSamplesSOFT(this.device, out, frames);
         short[] samples = new short[frames * 2];
         out.get(samples);
         return samples;
      }
   }

   @Override
   public void close() {
      synchronized (AL_LOCK) {
         if (!this.closed) {
            this.current();

            for (OpenAlOutput.AlVoice v : this.voices) {
               if (!v.released) {
                  AL10.alSourceStop(v.source);
                  AL10.alDeleteSources(v.source);
                  v.released = true;
               }
            }

            for (int b : this.buffers) {
               AL10.alDeleteBuffers(b);
            }

            this.voices.clear();
            this.buffers.clear();
            ALC10.alcMakeContextCurrent(0L);
            ALC10.alcDestroyContext(this.context);
            ALC10.alcCloseDevice(this.device);
            this.closed = true;
            this.errorLog.flush();
         }
      }
   }

   private record AlBuffer(int id) implements Buffer {
   }

   private final class AlVoice implements Voice {
      private final int source;
      private boolean released;

      AlVoice(int source) {
         this.source = source;
         this.released = source == 0;
      }

      private boolean alive() {
         return !this.released && OpenAlOutput.this.current();
      }

      public void play(Buffer buffer, float gain, float rate, int loop) {
         if (buffer instanceof OpenAlOutput.AlBuffer b && b.id() != 0) {
            synchronized (OpenAlOutput.AL_LOCK) {
               if (this.alive()) {
                  AL10.alSourceStop(this.source);
                  AL10.alSourcei(this.source, 4105, 0);
                  if (loop > 0) {
                     AL10.alSourcei(this.source, 4103, 0);

                     for (int i = 0; i <= loop; i++) {
                        AL10.alSourceQueueBuffers(this.source, b.id());
                     }
                  } else {
                     AL10.alSourcei(this.source, 4103, loop < 0 ? 1 : 0);
                     AL10.alSourcei(this.source, 4105, b.id());
                  }

                  AL10.alSourcef(this.source, 4106, gain);
                  AL10.alSourcef(this.source, 4099, rate);
                  AL10.alSourcePlay(this.source);
                  OpenAlOutput.this.check("play");
               }
            }
         }
      }

      public void stop() {
         synchronized (OpenAlOutput.AL_LOCK) {
            if (this.alive()) {
               AL10.alSourceStop(this.source);
               OpenAlOutput.this.check("stop");
            }
         }
      }

      public boolean playing() {
         synchronized (OpenAlOutput.AL_LOCK) {
            return this.alive() && AL10.alGetSourcei(this.source, 4112) == 4114;
         }
      }

      public void setGain(float gain) {
         synchronized (OpenAlOutput.AL_LOCK) {
            if (this.alive()) {
               AL10.alSourcef(this.source, 4106, gain);
               OpenAlOutput.this.check("setGain");
            }
         }
      }

      public void setLooping(boolean looping) {
         synchronized (OpenAlOutput.AL_LOCK) {
            if (this.alive()) {
               AL10.alSourcei(this.source, 4103, looping ? 1 : 0);
               OpenAlOutput.this.check("setLooping");
            }
         }
      }

      public void release() {
         synchronized (OpenAlOutput.AL_LOCK) {
            if (this.alive()) {
               AL10.alSourceStop(this.source);
               AL10.alDeleteSources(this.source);
               OpenAlOutput.this.check("release");
            }

            this.released = true;
            OpenAlOutput.this.voices.remove(this);
         }
      }
   }

   static final class ErrorLog {
      static final long SUMMARY_INTERVAL_MS = 60000L;
      private final LongSupplier clockMs;
      private final Consumer<String> sink;
      private final Set<Integer> codes = new HashSet<>();
      private int unreported;
      private int lastCode;
      private String lastWhat;
      private long lastNotice;

      ErrorLog(LongSupplier clockMs, Consumer<String> sink) {
         this.clockMs = clockMs;
         this.sink = sink;
      }

      void error(int code, String what) {
         this.lastCode = code;
         this.lastWhat = what;
         if (this.codes.add(code)) {
            if (this.codes.size() == 1) {
               this.lastNotice = this.clockMs.getAsLong();
            }

            this.sink.accept("erro de OpenAL 0x" + Integer.toHexString(code) + " em " + what);
         } else {
            this.unreported++;
            this.tick();
         }
      }

      void tick() {
         if (this.unreported > 0 && this.clockMs.getAsLong() - this.lastNotice >= 60000L) {
            this.flush();
         }
      }

      void flush() {
         if (this.unreported != 0) {
            this.sink
               .accept(
                  this.unreported
                     + (this.unreported == 1 ? " erro" : " erros")
                     + " de OpenAL desde o último aviso (último: 0x"
                     + Integer.toHexString(this.lastCode)
                     + " em "
                     + this.lastWhat
                     + ")"
               );
            this.unreported = 0;
            this.lastNotice = this.clockMs.getAsLong();
         }
      }
   }
}
