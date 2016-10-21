package hendrawd.testfileobserver;

import android.app.Application;
import android.content.Intent;
import android.util.Log;

/**
 * @author hendrawd on 8/11/15.
 */
public class MainApplication extends Application {

    @Override
    public void onCreate() {
        super.onCreate();
        Log.v(getString(R.string.app_name), "inside oncreate application");
        startService(new Intent(MainApplication.this, FileObserverHolderService.class));
    }

}
