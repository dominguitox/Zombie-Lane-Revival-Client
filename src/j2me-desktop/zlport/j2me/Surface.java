package zlport.j2me;

public interface Surface {
   void setRenderer(Renderer var1);

   void requestRender();

   int displayWidth();

   default int refreshRate() {
      return 0;
   }

   void midletDestroyed();
}
