package hendrawd.testfileobserver;

import android.annotation.TargetApi;
import android.app.Activity;
import android.content.ClipData;
import android.content.Intent;
import android.media.ExifInterface;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.FileObserver;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;

import com.nononsenseapps.filepicker.FilePickerActivity;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {

    private static final int FILE_CODE = 123;
    private static final String TAG = MainActivity.class.getSimpleName();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Log.i(TAG, "inside on create");
//        startFileObserver();
    }

    public void startPicker(View view) {
        Intent i = new Intent(this, FilePickerActivity.class);
        // Intent i = new Intent(Intent.ACTION_GET_CONTENT);

        i.putExtra(FilePickerActivity.EXTRA_ALLOW_MULTIPLE, false);
        i.putExtra(FilePickerActivity.EXTRA_ALLOW_CREATE_DIR, false);
        i.putExtra(FilePickerActivity.EXTRA_MODE, FilePickerActivity.MODE_FILE);

        i.putExtra(FilePickerActivity.EXTRA_START_PATH, Environment.getExternalStorageDirectory().getPath() + "/DCIM");

        startActivityForResult(i, FILE_CODE);
    }

    @TargetApi(Build.VERSION_CODES.JELLY_BEAN)
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == FILE_CODE && resultCode == Activity.RESULT_OK) {
            if (data.getBooleanExtra(FilePickerActivity.EXTRA_ALLOW_MULTIPLE, false)) {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
                    ClipData clip = data.getClipData();

                    if (clip != null) {
                        for (int i = 0; i < clip.getItemCount(); i++) {
                            Uri uri = clip.getItemAt(i).getUri();
                            modifyExifComment(uri);
                            showImage(uri);
                        }
                    }
                } else {
                    ArrayList<String> paths = data.getStringArrayListExtra
                            (FilePickerActivity.EXTRA_PATHS);

                    if (paths != null) {
                        for (String path : paths) {
                            Uri uri = Uri.parse(path);
                            modifyExifComment(uri);
                            showImage(uri);
                        }
                    }
                }

            } else {
                Uri uri = data.getData();
                modifyExifComment(uri);
                showImage(uri);
            }
        }
    }

    private void modifyExifComment(Uri uri) {
        ExifInterface exif;
        try {
            Log.i(TAG, "setting exif success!");
            Toast.makeText(this, "setting exif success!", Toast.LENGTH_SHORT).show();
            exif = new ExifInterface(uri.getPath());
            exif.setAttribute("UserComment", "this is a comment bhahahahahak!");
            exif.saveAttributes();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void showImage(Uri uri) {
        ImageView imageView = (ImageView) findViewById(R.id.imageView);
        imageView.setImageURI(uri);
    }

    private void startFileObserver() {
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
                    super.onEvent(event, path);
                }
            };
            rfo.startWatching();
        }
    }

}
