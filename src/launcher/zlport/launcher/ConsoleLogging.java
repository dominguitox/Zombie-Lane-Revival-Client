package zlport.launcher;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.logging.ConsoleHandler;
import java.util.logging.Formatter;
import java.util.logging.Handler;
import java.util.logging.Level;
import java.util.logging.LogManager;
import java.util.logging.LogRecord;
import java.util.logging.Logger;

final class ConsoleLogging {
   private static final DateTimeFormatter TIME = DateTimeFormatter.ofPattern("HH:mm:ss.SSS");

   private ConsoleLogging() {
   }

   static void install() {
      install(System.getProperty("zlport.log", "INFO"));
   }

   static void install(String level) {
      Level parsed = Level.parse(level);
      LogManager.getLogManager().reset();
      Logger root = Logger.getLogger("");
      Handler console = new ConsoleHandler();
      console.setLevel(parsed);
      console.setFormatter(new ConsoleLogging.LineFormatter());
      root.addHandler(console);
      root.setLevel(parsed);
   }

   static final class LineFormatter extends Formatter {
      @Override
      public String format(LogRecord r) {
         StringBuilder sb = new StringBuilder(160)
            .append(LocalTime.now().format(ConsoleLogging.TIME))
            .append(' ')
            .append(String.format("%-7s", r.getLevel().getName()))
            .append(" [")
            .append(Thread.currentThread().getName())
            .append("] ")
            .append(r.getLoggerName())
            .append(": ")
            .append(this.formatMessage(r))
            .append(System.lineSeparator());
         if (r.getThrown() != null) {
            StringWriter w = new StringWriter();
            r.getThrown().printStackTrace(new PrintWriter(w));
            sb.append(w);
         }

         return sb.toString();
      }
   }
}
