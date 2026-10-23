package zlport.android.ui;

import java.util.Objects;

public final class Dialogs {
   private static volatile DialogPresenter presenter;

   private Dialogs() {
   }

   public static void install(DialogPresenter p) {
      presenter = Objects.requireNonNull(p);
   }

   public static void uninstall() {
      presenter = null;
   }

   public static DialogPresenter presenter() {
      return presenter;
   }
}
