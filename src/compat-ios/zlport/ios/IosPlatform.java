package zlport.ios;

import com.dchoc.toolkit.FacebookResponse;
import com.dchoc.toolkit.IFacebookConnection;
import java.io.InputStream;
import java.util.List;
import java.util.Map;
import java.util.function.Consumer;
import java.util.function.IntConsumer;

public interface IosPlatform {
   InputStream openResource(String var1);

   String infoPlistString(String var1);

   boolean infoPlistBoolean(String var1);

   String preference(String var1);

   void setPreference(String var1, String var2);

   boolean synchronizePreferences();

   String deviceModel();

   int compareSystemVersion(String var1, int var2);

   boolean canOpenUrl(String var1);

   boolean isImageSourceAvailable(int var1);

   boolean classExists(String var1);

   String advertisingIdentifier();

   String countryCode();

   int networkInterfaceIndex(String var1);

   byte[] networkInterfaceMacAddress(int var1);

   float mainScreenHeight();

   Object window();

   float windowHeight(Object var1);

   Object createActivityIndicator(int var1);

   void addSubview(Object var1, Object var2);

   void bringSubviewToFront(Object var1, Object var2);

   void setActivityIndicatorCenter(Object var1, float var2, float var3);

   void startActivityIndicator(Object var1);

   void stopActivityIndicator(Object var1);

   void removeActivityIndicatorFromSuperview(Object var1);

   void releaseActivityIndicator(Object var1);

   void copyText(String var1);

   void showAlertView(String var1, String var2, String var3);

   void initializeCustomizer();

   void displayCustomizerPopup();

   int getNeedUpdate();

   void initializeAds(boolean var1, int var2, int var3, int var4, int var5, int var6);

   void logEvent(String var1, Map<String, String> var2);

   void logPlayerAction(Map<String, String> var1);

   boolean createCacheDirectory(String var1, boolean var2);

   boolean cacheFileExists(String var1);

   boolean writeCacheFileAtomically(String var1, byte[] var2);

   void removeAllCacheFiles();

   void playMovie(String var1);

   void resumeMovie();

   boolean isMovieFinished();

   IosTexture loadTexture(String var1, int var2);

   void unloadTexture(int var1);

   String stringByAddingPercentEscapes(String var1);

   void uploadRegistrationEvent(String var1);

   void cancelLocalNotifications();

   void registerForRemoteNotifications(int var1);

   boolean scheduleLocalNotification(double var1, String var3, Map<String, String> var4);

   boolean isGameCenterAvailable();

   void createGameCenterManager();

   void authenticateGameCenterUser();

   boolean isGameCenterPlayerAuthenticated();

   String gameCenterPlayerAlias();

   String gameCenterPlayerID();

   void loadGameCenterFriends(Consumer<List<String[]>> var1);

   void reportGameCenterScore(String var1, long var2);

   void submitGameCenterAchievement(String var1, double var2);

   boolean canSendTweet();

   void presentTweetComposer(String var1, String var2, IntConsumer var3);

   IFacebookConnection facebookConnection(String var1, String var2, Consumer<FacebookResponse> var3);

   void showPlayHavenContent(String var1, IntConsumer var2);

   void startChartboostSession();

   boolean isImpactSupported();

   boolean showImpactVideoAds();

   void hideAd();

   void showAd(int var1);

   boolean httpHeadHasResponse(String var1);

    Integer energyChargeTimeOverride();

    /** Boolean.TRUE when the launcher asks for infinite cash; null means no request. */
    Boolean cashCheatOverride();

    Float spawnMultiplierOverride();

   Long neighborIntervalOverride();

   List<LocalProfile> localProfiles();

   void logNativeMessage(String var1);
}
