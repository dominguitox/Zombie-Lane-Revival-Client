package zlport.launcher;

import java.util.List;
import java.util.Locale;
import java.util.Optional;

/** Languages shared by the launcher, the game settings record and the language prompt. */
public final class LauncherLanguage {
   public static final String PROPERTY = "zlport.language";
   public static final int ENGLISH = 0;
   public static final int FRENCH = 1;
   public static final int ITALIAN = 2;
   public static final int GERMAN = 3;
   public static final int SPANISH = 4;

   public record Language(String code, int index, String nativeName) {
      @Override
      public String toString() {
         return this.nativeName;
      }
   }

   private static final List<Language> LANGUAGES = List.of(
      new Language("en", ENGLISH, "English"),
      new Language("fr", FRENCH, "Français"),
      new Language("it", ITALIAN, "Italiano"),
      new Language("de", GERMAN, "Deutsch"),
      new Language("es", SPANISH, "Español")
   );

   private LauncherLanguage() {
   }

   public static List<Language> all() {
      return LANGUAGES;
   }

   public static Language fallback() {
      return LANGUAGES.get(ENGLISH);
   }

   public static Optional<Language> find(String raw) {
      if (raw == null) {
         return Optional.empty();
      }
      String code = raw.strip().toLowerCase(Locale.ROOT);
      int separator = code.indexOf('-');
      if (separator < 0) {
         separator = code.indexOf('_');
      }
      if (separator > 0) {
         code = code.substring(0, separator);
      }
      for (Language language : LANGUAGES) {
         if (language.code().equals(code)) {
            return Optional.of(language);
         }
      }
      return Optional.empty();
   }

   public static Optional<String> normalize(String raw) {
      return find(raw).map(Language::code);
   }

   public static Language systemDefault() {
      return find(Locale.getDefault().getLanguage()).orElse(fallback());
   }

   public static Optional<String> codeForIndex(int index) {
      for (Language language : LANGUAGES) {
         if (language.index() == index) {
            return Optional.of(language.code());
         }
      }
      return Optional.empty();
   }

   public static int indexForCode(String code) {
      return find(code).map(Language::index).orElse(-1);
   }
}
