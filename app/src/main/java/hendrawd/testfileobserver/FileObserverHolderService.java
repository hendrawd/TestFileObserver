package hendrawd.testfileobserver;

import android.app.NotificationManager;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.FileObserver;
import android.os.IBinder;
import android.support.v4.app.NotificationCompat;
import android.util.Log;

import java.io.File;
import java.util.ArrayList;

public class FileObserverHolderService extends Service {

    private static final String TAG = FileObserverHolderService.class.getSimpleName();

    //must keep the reference of recursive file observers, so it will not garbage collected
    private ArrayList<RecursiveFileObserver> recursiveFileObservers = new ArrayList<>();

    public FileObserverHolderService() {
    }

    @Override
    public IBinder onBind(Intent intent) {
        Log.i(TAG, "ibinder file observer service");
        return null;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Log.i(TAG, "on start file observer service");
        initFileObserver();
        return START_STICKY;
    }

    @Override
    public void onCreate() {
        Log.i(TAG, "on create file observer service");
        super.onCreate();
    }

    @Override
    public void onDestroy() {
        Log.i(TAG, "on destroy file observer service");
        for (RecursiveFileObserver rfo :
                recursiveFileObservers) {
            rfo.stopWatching();
        }
        recursiveFileObservers.clear();
        super.onDestroy();
    }

    private void initFileObserver() {
        if (!recursiveFileObservers.isEmpty()) {
            recursiveFileObservers.clear();
        }
        String paths[] = StorageUtil.getStorageDirectories();
        for (String aPath : paths) {
            String path = aPath + "/DCIM";
            File file = new File(path);
            if (!file.exists())
                continue;
            RecursiveFileObserver rfo = new RecursiveFileObserver(file.getAbsolutePath(), FileObserver.CREATE | FileObserver.MODIFY) {
                @Override
                public void onEvent(int event, String path) {
                    Log.i(TAG, "event triggered");
                    switch (event) {
                        case FileObserver.CREATE:
                            checkFileCreated(path);
                            break;
                        default:
                            super.onEvent(event, path);
                    }
                }
            };
            recursiveFileObservers.add(rfo);
            rfo.startWatching();
        }
    }

    private synchronized void checkFileCreated(String path) {
        Log.i(TAG, "inside check file created");
        //check if the path is a image file from the extension
        if (path.toLowerCase().endsWith(".png") ||
                path.toLowerCase().endsWith(".jpg") ||
                path.toLowerCase().endsWith(".jpeg")) {
            Log.i(TAG, "inside check file created - image file with desired extensions");
            if (!path.toLowerCase().contains("/.thumbnails/")) {
                Log.i(TAG, "inside check file created - not thumbnail");
                buildNotification("new file created", "new file created", 1);
            }
        }
    }

    private void buildNotification(String message, String bigTextMessage, int notifId) {
        NotificationCompat.Builder notificationBuilder = new NotificationCompat.Builder(this)
                .setContentTitle(getString(R.string.app_name))
                .setContentText(message)
                .setStyle(new NotificationCompat.BigTextStyle().bigText(bigTextMessage))
                .setAutoCancel(true)
                .setPriority(NotificationCompat.PRIORITY_MAX);

        NotificationManager notificationManager =
                (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        notificationManager.notify(
                notifId,
                notificationBuilder.build());
    }
}
