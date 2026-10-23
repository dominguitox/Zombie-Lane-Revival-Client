package zlport.android.ui;

import java.util.function.IntConsumer;

public interface DialogPresenter {
   void show(DialogPresenter.Box var1, IntConsumer var2);

   record Box(String title, String message, String positive, String negative, boolean cancelable) {
   }
}
