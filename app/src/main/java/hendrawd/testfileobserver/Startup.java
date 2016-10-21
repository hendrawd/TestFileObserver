package hendrawd.testfileobserver;

/**
 * @author hendrawd
 * @since 3/3/16
 */

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

public class Startup extends BroadcastReceiver {

    public Startup() {
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        // start your service here
        if (intent.getAction().equals(Intent.ACTION_BOOT_COMPLETED)) {
            context.startService(new Intent(context, FileObserverHolderService.class));
        }
    }

}