package android.content;

import android.net.Uri;
import android.os.Bundle;
import zlport.stubs.NotPorted;

public class Intent {
   private String mAction;
   private Uri mData;

   public Intent() {
      NotPorted.method("android.content.Intent#<init>()");
   }

   public Intent(String action, Uri uri) {
      this.mAction = action;
      this.mData = uri;
   }

   public Bundle getExtras() {
      NotPorted.method("android.content.Intent#getExtras()");
      return null;
   }

   public String getStringExtra(String name) {
      NotPorted.method("android.content.Intent#getStringExtra(String)");
      return null;
   }

   public Intent putExtra(String name, String value) {
      NotPorted.method("android.content.Intent#putExtra(String,String)");
      return null;
   }

   public Intent setComponent(ComponentName component) {
      NotPorted.method("android.content.Intent#setComponent(ComponentName)");
      return null;
   }

   @Override
   public String toString() {
      return "Intent { act=" + this.mAction + (this.mData == null ? "" : " dat=" + this.mData) + " }";
   }
}
