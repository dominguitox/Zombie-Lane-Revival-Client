package android.app;

import android.content.Context;
import android.os.Bundle;

public class Instrumentation {
   public Activity newActivity(Class<? extends Activity> clazz, Context context) throws ReflectiveOperationException {
      Activity activity = clazz.getDeclaredConstructor().newInstance();
      activity.attach(context);
      return activity;
   }

   public void callActivityOnCreate(Activity activity, Bundle icicle) {
      activity.onCreate(icicle);
   }

   public void callActivityOnResume(Activity activity) {
      activity.onResume();
   }

   public void callActivityOnPause(Activity activity) {
      activity.onPause();
   }

   public void callActivityOnDestroy(Activity activity) {
      activity.onDestroy();
   }
}
