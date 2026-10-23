package com.dchoc.toolkit;

import com.dchoc.idead.Profiler;
import com.dchoc.imagefont.ImageFont;
import com.dchoc.iphonewrappers.DeviceWrapper;
import com.dchoc.iphonewrappers.iWrapper;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.EOFException;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Locale;
import java.util.Vector;
import javax.microedition.lcdui.GLGraphics;
import javax.microedition.lcdui.Graphics;
import javax.microedition.lcdui.Image;
import javax.microedition.lcdui.game.GameCanvas;
import javax.microedition.media.Manager;
import javax.microedition.media.Player;
import javax.microedition.media.PlayerListener;
import javax.microedition.rms.RecordStore;
import zlport.ios.Ios;

public class Toolkit extends GameCanvas implements PlayerListener, ResourceIDs {
   public static final int CHANNEL_VOLUME_MAX = 127;
   public static final int CHANNEL_VOLUME_MIN = 0;
   public static final int CONTINUOUS_LOOPING = -1;
   public static final int DONT_FORCE_WRITING = 0;
   public static final int EVENT_KEY_PRESSED = 0;
   public static final int EVENT_KEY_RELEASED = 1;
   public static final int EVENT_KEY_REPEATED = 2;
   public static final int EVENT_MOUSE_WHEEL_SCROLLED_DOWN = 4;
   public static final int EVENT_MOUSE_WHEEL_SCROLLED_UP = 5;
   public static final int EVENT_POINTER_DRAGGED = 2;
   public static final int EVENT_POINTER_PRESSED = 0;
   public static final int EVENT_POINTER_RELEASED = 1;
   public static final int EVENT_SOFT_KEY_PRESSED = 3;
   public static final int FORCE_WRITING = 1;
   public static final int FREE_RESOURCE_CACHE = 2;
   public static final int FREE_RESOURCE_FILES = 4;
   public static final int FREE_TEXT_CACHE = 1;
   public static final String GMG_JAD_PARAM = "DCHOC-GMG-";
   public static final String GMG_LINK_SEPERATOR = "!.!";
   public static final String GMG_URL_NOLINK = "nolink";
   public static final String GMG_URL_NONE = "none";
   public static final int INPUT_TYPE_LOCALIZED_TEXT = 4;
   public static final int INPUT_TYPE_NUMERIC = 2;
   public static final int INPUT_TYPE_PASSWORD = 5;
   public static final int INPUT_TYPE_PHONENUMBER = 3;
   public static final int INPUT_TYPE_TEXT = 1;
   public static final int INPUT_TYPE_USER_NAME = 0;
   public static final int KEY_ACTION = 12;
   public static final int KEY_ACTION_2 = 17;
   public static final int KEY_ACTION_3 = 18;
   public static final int KEY_ACTION_4 = 7;
   public static final int KEY_DOWN = 15;
   public static final int KEY_DOWN_LEFT = 14;
   public static final int KEY_DOWN_RIGHT = 16;
   public static final int KEY_LEFT = 11;
   public static final int KEY_RIGHT = 13;
   public static final int KEY_UP = 9;
   public static final int KEY_UP_LEFT = 8;
   public static final int KEY_UP_RIGHT = 10;
   public static final int LANGUAGE_NAME_INDEX = 0;
   public static final int MASK_RESOURCE_INDEX = 65535;
   public static final int MASTER_VOLUME_MAX = 100;
   public static final int MASTER_VOLUME_MIN = 0;
   public static final int NETWORK_USAGE_LOCATION_ID_CONNECTION_QUERY_LONG = 1;
   public static final int NETWORK_USAGE_LOCATION_ID_CONNECTION_QUERY_SHORT = 0;
   public static final int NETWORK_USAGE_LOCATION_ID_GET_MORE_GAMES_QUERY = 2;
   public static final int NOT_NUMBER_KEY = -1;
   public static final int NO_SOFT_KEY = -1;
   public static final int PROPERTY_COUNT = 22;
   public static final int PROPERTY_GMG_STATE_NONE_DEFINATION = 25;
   public static final int PROPERTY_ID_ = 9;
   public static final int PROPERTY_ID_ASSET_SERVER_URL = 21;
   public static final int PROPERTY_ID_CARRIER_ID = 2;
   public static final int PROPERTY_ID_DEMO_PLAY_COUNT = 12;
   public static final int PROPERTY_ID_DEMO_TIME_LIMIT = 11;
   public static final int PROPERTY_ID_DEMO_URL = 10;
   public static final int PROPERTY_ID_DEVICE_ID = 1;
   public static final int PROPERTY_ID_FEEDBACK_URL = 18;
   public static final int PROPERTY_ID_FREE_TRIAL_UPGRADE_URL = 20;
   public static final int PROPERTY_ID_GAME_SERVER_URL = 13;
   public static final int PROPERTY_ID_GET_MORE_GAMES_URL = 5;
   public static final int PROPERTY_ID_INGAME_ADVERTISING = 15;
   public static final int PROPERTY_ID_INITIAL_STARTUP_LANGUAGE = 7;
   public static final int PROPERTY_ID_ML_SERVER = 4;
   public static final int PROPERTY_ID_NETWORK_COST_TERMS_ID = 22;
   public static final int PROPERTY_ID_NUMERAL_COUNTRY_CODE = 8;
   public static final int PROPERTY_ID_PROMO_URL = 14;
   public static final int PROPERTY_ID_SMS_SHORT_CODE_URL = 19;
   public static final int PROPERTY_ID_USE_CHEAT_CODES = 16;
   public static final int PROPERTY_ID_USE_CHOCOLATE_CLUB = 23;
   public static final int PROPERTY_ID_USE_HYBRID_ML = 3;
   public static final int PROPERTY_ID_USE_MMS_TAF = 17;
   public static final int PROPERTY_ID_USE_PACKAGER_DROPPABLE_CONTENT = 24;
   public static final int PROPERTY_ID_USE_TELL_A_FRIEND = 6;
   public static final String PROPERTY_JAD_ATTRIBUTE = "DCHOC-";
   public static final int SELECT_SOFTKEY_INDEX = 1;
   public static final int SHIFT_FILE_ID = 16;
   public static final int SK_ID_ACCEPT = 26;
   public static final int SK_ID_BACK = 5;
   public static final int SK_ID_BUY = 20;
   public static final int SK_ID_BYE = 28;
   public static final int SK_ID_CANCEL = 11;
   public static final int SK_ID_CHANGE = 4;
   public static final int SK_ID_CONTINUE = 15;
   public static final int SK_ID_DISCARD = 25;
   public static final int SK_ID_DONE = 10;
   public static final int SK_ID_EDIT = 3;
   public static final int SK_ID_ERASE = 8;
   public static final int SK_ID_EXIT = 6;
   public static final int SK_ID_GET_IT = 24;
   public static final int SK_ID_GET_IT_BROWSE = 34;
   public static final int SK_ID_GO = 40;
   public static final int SK_ID_GO_TO_WAP = 31;
   public static final int SK_ID_GREENHOUSE = 39;
   public static final int SK_ID_HELP = 27;
   public static final int SK_ID_HIRE = 18;
   public static final int SK_ID_MANAGE = 17;
   public static final int SK_ID_MENU = 13;
   public static final int SK_ID_NEXT = 12;
   public static final int SK_ID_NO = 7;
   public static final int SK_ID_OK = 1;
   public static final int SK_ID_PAUSE = 9;
   public static final int SK_ID_PLACE = 22;
   public static final int SK_ID_PLAY = 32;
   public static final int SK_ID_POWER_UP = 38;
   public static final int SK_ID_QUIT = 35;
   public static final int SK_ID_RANKING = 37;
   public static final int SK_ID_REGISTER = 30;
   public static final int SK_ID_RESTART = 36;
   public static final int SK_ID_RETRY = 23;
   public static final int SK_ID_SAVE = 16;
   public static final int SK_ID_SELECT = 0;
   public static final int SK_ID_SEND = 21;
   public static final int SK_ID_SKIP = 14;
   public static final int SK_ID_START = 19;
   public static final int SK_ID_STATS = 33;
   public static final int SK_ID_SYNCHRONIZE = 29;
   public static final int SK_ID_YES = 2;
   public static final int SK_POSITION_BACK_KEY = 4;
   public static final int SK_POSITION_BY_TYPE = 0;
   public static final int SK_POSITION_LEFT = 1;
   public static final int SK_POSITION_MIDDLE = 2;
   public static final int SK_POSITION_RIGHT = 3;
   public static final int SK_TYPE_BACK = 2;
   public static final int SK_TYPE_EDIT = 1;
   public static final int SK_TYPE_ERASE = 4;
   public static final int SK_TYPE_EXIT = 3;
   public static final int SK_TYPE_OK = 0;
   public static final int SOUND_FORMAT_AMR_INDEX = 2;
   public static final int SOUND_FORMAT_ILLEGAL = -1;
   public static final int SOUND_FORMAT_MIDI_INDEX = 0;
   public static final int SOUND_FORMAT_MMF_INDEX = 5;
   public static final int SOUND_FORMAT_MP3_INDEX = 3;
   public static final int SOUND_FORMAT_OTT_INDEX = 4;
   public static final int SOUND_FORMAT_SPF_INDEX = 6;
   public static final int SOUND_FORMAT_WAV_INDEX = 1;
   public static final int SOUND_INGAME_VOLUME_DEFAULT = 3;
   public static final int SOUND_TYPE_FX = 0;
   public static final int SOUND_TYPE_MUSIC = 1;
   public static final int SOUND_VOLUME_MAX = 5;
   public static final int SOUND_VOLUME_OFF = 0;
   public static final int SOURCE_FILESYSTEM = 0;
   public static final int SOURCE_RECORDSTORE = 1;
   public static final int SOURCE_URL = 2;
   public static final int TOOLKIT_EVENT_KEY_PRESSED = 3;
   public static final int TOOLKIT_EVENT_PAUSE = 0;
   public static final int TOOLKIT_EVENT_RESUME = 1;
   public static final int TOOLKIT_EVENT_UPDATE_LOGIC = 2;
   private static final String PROPERTY_FILE_NAME = "p";
   private static final String RESOURCE_PACK_FILE_NAME = "r";
   private static final String[] SOUND_FILE_EXTENSIONS = new String[]{".mid", ".wav", ".amr", ".mp3"};
   private static final String[] SOUND_FORMATS = new String[]{"audio/midi", "audio/wav", "audio/amr", "audio/mpeg"};
   private static final String[] REPLACE_STRINGS = new String[]{"%U", "%0U", "%1U", "%2U", "%3U", "%4U", "%5U", "%6U", "%7U", "%8U", "%9U"};
   private static final int[] GAME_ACTIONS = new int[]{0, 50, 52, 0, 0, 54, 56, 0, 53};
   public static boolean isKeyPressed;
   public static boolean isKeyReleased;
   public static boolean isKeyRepeated;
   public static boolean lockScreenInterrupt;
   public static String smResourcePath = "";
   public static boolean sm_okToPaint;
   private static Toolkit sm_instance;
   private static DChocMIDlet sm_listener;
   private static Player sm_currentMusic;
   private static byte[] sm_loadedFileData;
   private static String sm_loadedFileName;
   private static Path sm_sharedMissionsDir;
   private static boolean sm_sharedMissionsResolved;
   private static boolean sm_sharedMissionsEnabled = true;
   private static int sm_dataSource;
   private static int[] sm_lastPressedKeys;
   private static int sm_lastPressedKeyIndex;
   private static String[] sm_properties;
   private static final java.util.Map<Integer, String> sm_textCache = new java.util.concurrent.ConcurrentHashMap<>();
   private static final java.util.Map<Integer, String> smCustomTexts = new java.util.concurrent.ConcurrentHashMap<>();
   private static byte[] smLangugeBytes;
   private static String[] sm_languageCode;
   private static String[][] sm_languageName;
   private static int sm_selectedLanguage;
   private static int[] sm_softKeyTids;
   private static String[] sm_softKeyLabels;
   private static int[] sm_softKeyTypes;
   private static int sm_leftSoftKey;
   private static int sm_rightSoftKey;
   private static int sm_leftSoftKeyType;
   private static int sm_rightSoftKeyType;
   private static boolean sm_settingAsCurrent;
   private static boolean sm_settingAnotherDisplayable;
   private static int sm_musicVolume;
   private static int sm_soundEffectVolume;
   private static ImageFont sm_softkeyImageFont;
   private static int sm_softkeyAreaHeight;
   private static IRenderingPlatform sm_renderingPlatform;
   private static IPaymentManager sm_paymentManager;
   private static IVirtualKeypad smKeypad;

   public Toolkit() {
      super(false);
   }

   public static void create(DChocMIDlet listener) {
      if (sm_instance == null) {
         Toolkit instance = new Toolkit();
         instance.setFullScreenMode(true);
         sm_lastPressedKeys = new int[5];
         sm_lastPressedKeyIndex = -1;
         sm_instance = instance;
      }

      sm_listener = listener;
   }

   public static int getScreenWidth() {
      return sm_instance.getWidth();
   }

   public static int getScreenHeight() {
      return sm_instance.getHeight();
   }

   public static void setVisible(boolean visible) {
      sm_instance.setFullScreenMode(visible);
      if (visible) {
         sm_settingAnotherDisplayable = false;
         sm_settingAsCurrent = true;
         DChocMIDlet.setCurrent(sm_instance);
      } else {
         sm_settingAnotherDisplayable = true;
      }
   }

   public static void initialize() {
      sm_properties = new String[22];

      try {
         InputStream resource = Ios.platform().openResource(smResourcePath + "p");
         if (resource != null) {
            DataInputStream input = new DataInputStream(resource);

            try {
               while (true) {
                  int property = input.readByte();
                  sm_properties[property - 1] = input.readUTF();
               }
            } catch (EOFException exception) {
               System.err.println(exception);
               input.close();
            }
         }
      } catch (Exception exception) {
         System.err.println(exception);
      }

      Vector descriptions = new Vector();
      int language = 0;

      do {
         setSelectedLanguage(language);
         descriptions.addElement(getText(0));
         descriptions.addElement(getText(1));
         descriptions.addElement(null);
      } while (loadFile("l" + ++language + "_0"));

      sm_languageName = new String[language][2];
      sm_languageCode = new String[language];
      int preferredLanguage = 0;

      for (int i = 0; i < language; i++) {
         sm_languageName[i][0] = (String)descriptions.elementAt(i * 3);
         sm_languageCode[i] = (String)descriptions.elementAt(i * 3 + 1);
         if (sm_languageCode[i].equals(sm_properties[6])) {
            preferredLanguage = i;
         }

         sm_languageName[i][1] = (String)descriptions.elementAt(i * 3 + 2);
      }

      setSelectedLanguage(preferredLanguage);
      sm_softKeyTids = new int[40];
      sm_softKeyLabels = new String[40];
      sm_softKeyTypes = new int[40];
      sm_leftSoftKey = -1;
      sm_rightSoftKey = -1;
   }

   public static void setSelectedLanguage(int language) {
      sm_selectedLanguage = language;

      sm_textCache.clear();

      smLangugeBytes = null;
   }

   public static String[][] getLanguageDescriptions() {
      return sm_languageName;
   }

   public static String getSelectedLanguageCode() {
      return sm_languageCode[sm_selectedLanguage];
   }

   public static int getSelectedLanguageIndex() {
      return sm_selectedLanguage;
   }

   public static String getToolkitProperty(int property) {
      return sm_properties[property - 1];
   }

   public static void registerCustomText(int textId, String value) {
      if (textId > -3 || textId < Short.MIN_VALUE) {
         throw new IllegalArgumentException("Custom text id must be a reserved signed-short value below -2");
      }
      registerText(textId, value);
   }

   /**
    * Register a literal string for a text ID. Used by the JSON mission loader so
    * the text stored in {@code missions.json} is what the game shows without
    * depending on the packed language table. A value of null removes any
    * override for the ID.
    */
   public static void registerText(int textId, String value) {
      if (value == null) {
         smCustomTexts.remove(textId);
      } else {
         smCustomTexts.put(textId, value);
      }
   }

   /**
    * Read the shared {@code missions.json} from the missions directory, or
    * return null when it does not exist or cannot be read. The mission manager
    * uses this to load definitions from JSON before falling back to the packed
    * {@code r1} binary tables.
    */
   public static String loadSharedMissionsJson() {
      Path directory = sharedMissionsDirectory();
      if (directory == null) {
         return null;
      }
      Path file = directory.resolve("missions.json");
      if (!Files.isRegularFile(file)) {
         return null;
      }

      try {
         return new String(Files.readAllBytes(file), java.nio.charset.StandardCharsets.UTF_8);
      } catch (IOException exception) {
         System.err.println("Could not read " + file + ": " + exception);
         return null;
      }
   }

   public static String getText(int text) {
      if (text == -1) {
         return null;
      }

      if (text == -2) {
         return "";
      }

      String customText = smCustomTexts.get(text);
      if (customText != null) {
         return customText;
      }

      if (text == 4317) {
         return "Free Cash";
      }

      if (text == 4318) {
         return "Free Energy";
      }

      String result = null;

      try {
         result = sm_textCache.get(text);
         if (result == null) {
            if (smLangugeBytes == null) {
               loadFile("l" + sm_selectedLanguage + "_" + (text >>> 16));
               smLangugeBytes = sm_loadedFileData;
            }

            byte[] bytes = getResourceBytesFromLoadedFile(text & 65535, smLangugeBytes);
            DataInputStream input = new DataInputStream(new ByteArrayInputStream(bytes));
            result = input.readUTF();
            input.close();
         }
      } catch (Exception exception) {
         System.err.println(exception);
      }

      if (result != null) {
         sm_textCache.put(text, result);
      }

      return result;
   }

   public static byte[] getResourceBytes(int resource) {
      if (resource == -1) {
         return null;
      }

      int index = resource & 65535;
      if (resource >>> 16 != 0) {
         loadFile("r" + (resource >>> 16));
         return getResourceBytesFromLoadedFile(index, sm_loadedFileData);
      }

      byte[] result = null;

      try {
         InputStream input = Ios.platform().openResource(smResourcePath + Integer.toString(index));
         result = new byte[input.available()];
         int read = input.read(result, 0, result.length);

         while (read < result.length) {
            read += input.read(result, read, result.length - read);
         }

         input.close();
      } catch (IOException exception) {
         System.err.println(exception);
      }

      return result;
   }

   public static boolean loadFile(String name) {
      if (name.equals(sm_loadedFileName)) {
         return true;
      }

      Profiler.startTimer(3);
      sm_loadedFileName = null;
      sm_loadedFileData = null;
      boolean loaded = false;

      try {
         if (sm_dataSource == 0) {
            sm_loadedFileData = new byte[4];
            InputStream input = openSharedPack(name);
            if (input == null) {
               input = Ios.platform().openResource(smResourcePath + name);
            }
            if (input != null) {
               input.read(sm_loadedFileData, 0, 4);
               int length = readPackedInt(sm_loadedFileData, 0);
               sm_loadedFileData = new byte[length];
               int offset = 4;

               while (offset < length) {
                  offset += input.read(sm_loadedFileData, offset, length - offset);
               }

               input.close();
               sm_loadedFileName = name;
               loaded = true;
            }
         }
      } catch (Exception exception) {
         System.err.println(exception);
      }

      Profiler.stopTimer(3);
      return loaded;
   }

   private static InputStream openSharedPack(String name) throws IOException {
      if (name.indexOf('/') >= 0 || name.indexOf('\\') >= 0) {
         return null;
      }

      Path directory = sharedMissionsDirectory();
      if (directory == null) {
         return null;
      }
      Path file = directory.resolve(name);
      return Files.isRegularFile(file) ? Files.newInputStream(file) : null;
   }

   /**
    * Enables or disables the shared missions directory. The LOCAL mode uses it
    * for editable mission packs; the SERVER mode disables it so the bundle and
    * the server are the only mission sources. See docs/missions-restore.md.
    */
   public static void setSharedMissionsEnabled(boolean enabled) {
      sm_sharedMissionsEnabled = enabled;
      sm_sharedMissionsResolved = false;
      sm_sharedMissionsDir = null;
   }

   private static Path sharedMissionsDirectory() {
      if (!sm_sharedMissionsEnabled) {
         return null;
      }
      if (!sm_sharedMissionsResolved) {
         sm_sharedMissionsResolved = true;
         String property = System.getProperty("zlport.ios.missions");
         if (property != null && !property.isBlank()) {
            sm_sharedMissionsDir = Path.of(property).toAbsolutePath().normalize();
         } else {
            String appData = System.getenv("APPDATA");
            Path root = appData != null && !appData.isBlank()
               ? Path.of(appData, "ZombieLane-1.4.4")
               : Path.of(System.getProperty("user.home"), ".zombielane-1.4.4");
            sm_sharedMissionsDir = root.resolve("missions");
         }
      }

      return sm_sharedMissionsDir;
   }

   private static int readPackedInt(byte[] bytes, int offset) {
      return (bytes[offset] & 0xFF) << 24 | (bytes[offset + 1] & 0xFF) << 16 | (bytes[offset + 2] & 0xFF) << 8 | bytes[offset + 3] & 0xFF;
   }

   private static byte[] getResourceBytesFromLoadedFile(int index, byte[] bytes) {
      int start = readPackedInt(bytes, 8 + index * 4);
      int end = index == readPackedInt(bytes, 4) - 1 ? bytes.length : readPackedInt(bytes, 12 + index * 4);
      byte[] result = new byte[end - start];
      System.arraycopy(bytes, start, result, 0, result.length);
      return result;
   }

   public static DChocByteArray getResourceByteArray(String name) {
      DChocByteArray result = null;

      try {
         InputStream input = Ios.platform().openResource(smResourcePath + name);
         if (input != null) {
            ByteArrayOutputStream output = new ByteArrayOutputStream();

            for (int value = input.read(); value != -1; value = input.read()) {
               output.write(value);
            }

            input.close();
            result = DChocByteArray.createByteArrayForReading(output.toByteArray(), 0, output.size());
            output.close();
         }
      } catch (IOException exception) {
         System.err.println(exception);
      }

      return result;
   }

   public static DChocByteArray getResourceByteArray(int resource) {
      Profiler.startTimer(2);
      DChocByteArray result = null;
      if (resource != -1) {
         int index = resource & 65535;
         if (resource >>> 16 == 0) {
            result = getResourceByteArray(Integer.toString(index));
         } else {
            loadFile("r" + (resource >>> 16));
            int start = readPackedInt(sm_loadedFileData, 8 + index * 4);
            int end = index == readPackedInt(sm_loadedFileData, 4) - 1 ? sm_loadedFileData.length : readPackedInt(sm_loadedFileData, 12 + index * 4);
            result = DChocByteArray.createByteArrayForReading(sm_loadedFileData, start, end - start);
         }
      }

      Profiler.stopTimer(2);
      return result;
   }

   public static Image getImage(int resource) {
      throw new UnsupportedOperationException("sem código na 1.4.4: Toolkit.getImage");
   }

   public static byte[] readRecord(String name) {
      byte[] result = null;

      try {
         RecordStore store = RecordStore.openRecordStore(name, false);
         result = store.getRecord(1);
         store.closeRecordStore();
      } catch (Exception exception) {
         System.err.println(exception);
      }

      return result;
   }

   public static DChocByteArray readRecordByteArray(String name) {
      byte[] bytes = readRecord(name);
      return bytes == null ? null : DChocByteArray.createByteArrayForReading(bytes, 0, bytes.length);
   }

   public static void writeRecord(String name, byte[] bytes, int flags) {
      try {
         RecordStore.deleteRecordStore(name);
      } catch (Exception exception) {
         System.err.println(exception);
      }

      if (bytes != null) {
         try {
            RecordStore store = RecordStore.openRecordStore(name, true);
            store.addRecord(bytes, 0, bytes.length);
            store.closeRecordStore();
         } catch (Exception exception) {
            System.err.println(exception);
         }
      }
   }

   public static void writeRecordByteArray(String name, DChocByteArray data, int flags) {
      writeRecord(name, data == null ? null : data.getBytes(), flags);
   }

   public static Player createPlayer(int resource) {
      int extension = Integer.toUnsignedLong(resource - 3) < 5L ? 3 : 1;

      try {
         return Manager.createPlayer(smResourcePath + Integer.toString(resource) + SOUND_FILE_EXTENSIONS[extension]);
      } catch (Exception exception) {
         return null;
      }
   }

   public static Player playMidi(int resource, int loops, int volume, int type) {
      Player player = null;

      try {
         player = createPlayer(resource);
         player.setLoopCount(loops);
         player.start();
      } catch (Exception exception) {
         System.err.println(exception);
      }

      if (type == 1) {
         sm_currentMusic = player;
      }

      return player;
   }

   private static void stopMidi(Player player) {
      try {
         if (player != null) {
            player.close();
         }
      } catch (Exception exception) {
         System.err.println(exception);
      }
   }

   public static void stopCurrentMusic() {
      stopMidi(sm_currentMusic);
      sm_currentMusic = null;
   }

   public static void stopMusic() {
      iWrapper.stopMusic();
      sm_currentMusic = null;
   }

   public static boolean isPlayingMusic() {
      return sm_currentMusic != null;
   }

   public static void setMusicVolume(int volume) {
      sm_musicVolume = volume;
      if (volume == 0) {
         iWrapper.stopMusic();
         sm_currentMusic = null;
      }
   }

   public static int getMusicVolume() {
      return sm_musicVolume;
   }

   public static void setSoundEffectVolume(int volume) {
      sm_soundEffectVolume = volume;
   }

   public static int getSoundEffectVolume() {
      return sm_soundEffectVolume;
   }

   public static void pauseMusic() {
   }

   public static void preloadSound(int resource) {
   }

   public static void stopSoundEffect() {
   }

   public void playerUpdate(Player player, String event, Object data) {
   }

   public static String replaceSubstring(String text, String search, String replacement) {
      int index = text.indexOf(search);
      return index == -1 ? text : text.substring(0, index) + (replacement == null ? "" : replacement) + text.substring(index + search.length());
   }

   public static String replaceParameters(String text, String[] parameters) {
      if (parameters != null && parameters.length == 1) {
         return replaceSubstring(text, REPLACE_STRINGS[0], parameters[0]);
      }

      if (parameters != null) {
         for (int i = 0; i < parameters.length; i++) {
            text = replaceSubstring(text, REPLACE_STRINGS[i + 1], parameters[i]);
         }
      }

      return text;
   }

   public static int getSoftKeyType(int key) {
      return sm_softKeyTypes[key];
   }

   public static void removeAllSoftKeys() {
      sm_leftSoftKey = -1;
      sm_rightSoftKey = -1;
   }

   public static void setSoftKey(int key, int position) {
      int type = sm_softKeyTypes[key];
      if (position == 1) {
         assignSoftKey(key, type, true);
      } else if (position != 0) {
         assignSoftKey(key, type, false);
      } else {
         int previous;
         boolean left;
         if (Integer.toUnsignedLong(type) < 2L) {
            previous = sm_leftSoftKey;
            if (previous == -1) {
               assignSoftKey(key, type, true);
               return;
            }

            left = type < sm_leftSoftKeyType;
            if (sm_leftSoftKeyType <= type) {
               previous = -1;
            }
         } else {
            previous = sm_rightSoftKey;
            if (previous == -1) {
               assignSoftKey(key, type, false);
               return;
            }

            left = false;
            if (type <= sm_rightSoftKeyType) {
               previous = -1;
               left = true;
            }
         }

         if (previous != -1) {
            sm_leftSoftKey = -1;
            sm_rightSoftKey = -1;
            assignSoftKey(previous, sm_softKeyTypes[previous], !left);
         }

         assignSoftKey(key, type, left);
      }
   }

   private static void assignSoftKey(int key, int type, boolean left) {
      if (left) {
         sm_leftSoftKey = key;
         sm_leftSoftKeyType = type;
      } else {
         sm_rightSoftKey = key;
         sm_rightSoftKeyType = type;
      }
   }

   public static void setSoftkeyImageFont(ImageFont font) {
      sm_softkeyImageFont = font;
      if (font != null) {
         sm_softkeyAreaHeight = Math.max(sm_softkeyAreaHeight, font.getHeight() + 4);
      }
   }

   public static int getSoftKeyAreaHeight() {
      return 0;
   }

   public static int getToolkitGameAction(int key) {
      if (key == -6) {
         return 42;
      }

      int action = sm_instance.getGameAction(key);
      return action >= 0 && action <= 8 ? GAME_ACTIONS[action] : 0;
   }

   public static void toolkitEventOccurred(int event) {
      if (event == 0) {
         for (int i = 0; i <= sm_lastPressedKeyIndex; i++) {
            sm_listener.keyEventOccurred(sm_lastPressedKeys[i], 1);
         }

         sm_lastPressedKeyIndex = -1;
      }
   }

   protected void keyPressed(int key) {
      sm_lastPressedKeyIndex++;
      int length = sm_lastPressedKeys == null ? 0 : sm_lastPressedKeys.length;
      if (length <= sm_lastPressedKeyIndex) {
         int[] previous = sm_lastPressedKeys;
         sm_lastPressedKeys = new int[length + 1];
         if (previous != null) {
            System.arraycopy(previous, 0, sm_lastPressedKeys, 0, length);
         }
      }

      sm_lastPressedKeys[sm_lastPressedKeyIndex] = key;
      if (key != -7 && key != -6) {
         sm_listener.keyEventOccurred(key, 0);
      } else {
         int softKey = key == -7 ? sm_rightSoftKey : sm_leftSoftKey;
         if (softKey != -1) {
            sm_listener.keyEventOccurred(softKey, 3);
         }
      }
   }

   protected void keyReleased(int key) {
      int removed = 0;
      int last = sm_lastPressedKeyIndex;

      for (int i = 0; i <= last; i++) {
         if (sm_lastPressedKeys[i] == key) {
            removed++;
         }

         if (i + removed <= last) {
            sm_lastPressedKeys[i] = sm_lastPressedKeys[i + removed];
            last = sm_lastPressedKeyIndex;
         }
      }

      sm_lastPressedKeyIndex = last - removed;
      sm_listener.keyEventOccurred(key, 1);
   }

   protected void keyRepeated(int key) {
      if (key != -7 && key != -6) {
         sm_listener.keyEventOccurred(key, 2);
      }
   }

   public void keyEventWithChars(int keyCode, int eventType, char[] chars) {
      throw new UnsupportedOperationException("sem código na 1.4.4: Toolkit.keyEventWithChars");
   }

   public void pointerPressed(int x, int y) {
      sm_listener.pointerEventOccurred(x, y, 0);
   }

   public void pointerPressed(int x, int y, Object pointer) {
      sm_listener.pointerEventOccurred(x, y, 0);
   }

   public void pointerDragged(int x, int y) {
      sm_listener.pointerEventOccurred(x, y, 2);
   }

   public void pointerDragged(int x, int y, Object pointer) {
      sm_listener.pointerEventOccurred(x, y, 2);
   }

   public void pointerReleased(int x, int y) {
      releasePointer(x, y);
   }

   public void pointerReleased(int x, int y, Object pointer) {
      releasePointer(x, y);
   }

   private static void releasePointer(int x, int y) {
      int fontHeight = sm_softkeyImageFont == null ? 0 : sm_softkeyImageFont.getHeight();
      if (sm_instance.getHeight() - fontHeight < y) {
         int leftWidth = sm_instance.getWidth();
         int rightWidth = sm_instance.getWidth();
         if (sm_leftSoftKey != -1 && x < leftWidth / 4) {
            sm_listener.keyEventOccurred(sm_leftSoftKey, 3);
         }

         if (sm_rightSoftKey != -1 && rightWidth * 3 / 4 < x) {
            sm_listener.keyEventOccurred(sm_rightSoftKey, 3);
         }
      }

      sm_listener.pointerEventOccurred(x, y, 1);
   }

   public static void doRepaint() {
      sm_okToPaint = true;
      sm_instance.repaint();
   }

   public void paint(Graphics graphics) {
      if (sm_okToPaint) {
         sm_listener.doDraw(graphics);
         sm_listener.doPostDraw(graphics);
      }
   }

   public void paint(GLGraphics graphics) {
      throw new UnsupportedOperationException("sem código na 1.4.4: Toolkit.paint(GLGraphics)");
   }

   protected void hideNotify() {
      if (!sm_settingAnotherDisplayable) {
         sm_listener.pauseApp();
      }
   }

   protected void showNotify() {
      if (sm_settingAsCurrent) {
         sm_settingAsCurrent = false;
      } else {
         try {
            sm_listener.startApp();
         } catch (Throwable throwable) {
            System.err.println(throwable);
         }
      }
   }

   public static void vibrate(int duration) {
      sm_listener.vibrate(duration);
   }

   public static IRenderingPlatform getRenderingPlatform() {
      if (sm_renderingPlatform == null) {
         sm_renderingPlatform = new DChocRenderingPlatform();
      }

      return sm_renderingPlatform;
   }

   public static IPaymentManager getUsedPaymentManager() {
      if (sm_paymentManager == null) {
         sm_paymentManager = new AppleAppStorePaymentManager();
      }

      return sm_paymentManager;
   }

   public static IVirtualKeypad getUsedVirtualKeypad() {
      if (smKeypad == null) {
         smKeypad = new DChocVirtualKeypad();
      }

      return smKeypad;
   }

   public static String getMacAddress() {
      if (Ios.platform().classExists("ASIdentifierManager")) {
         return DeviceWrapper.getAdvertisingId();
      } else {
         int index = Ios.platform().networkInterfaceIndex("en0");
         if (index == 0) {
            System.err.println("Error: if_nametoindex error");
            return null;
         } else {
            byte[] address = Ios.platform().networkInterfaceMacAddress(index);
            if (address == null) {
               System.err.println("Error: sysctl, take 1");
               return null;
            } else {
               return String.format(
                     Locale.ROOT,
                     "%02X:%02X:%02X:%02X:%02X:%02X",
                     address[0] & 255,
                     address[1] & 255,
                     address[2] & 255,
                     address[3] & 255,
                     address[4] & 255,
                     address[5] & 255
                  )
                  .toLowerCase(Locale.ROOT);
            }
         }
      }
   }
}
