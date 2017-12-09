package developers.sd.notes;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.google.android.gms.common.api.ResultCallback;

import com.google.android.gms.drive.Drive;
import com.google.android.gms.drive.DriveApi;
import com.google.android.gms.drive.DriveContents;
import com.google.android.gms.drive.DriveFolder;
import com.google.android.gms.drive.MetadataChangeSet;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.OutputStream;

import developers.sd.notes.database.NoteBaseHelper;

public class UploadToDriveActivity extends BaseDemoActivity {

    private final static String TAG = "UploadToDriveActivity";
    private ProgressBar progressBar;
    private TextView textView;

    @Override
    public void onConnected(Bundle connectionHint) {
        super.onConnected(connectionHint);
        Drive.DriveApi.newDriveContents(getGoogleApiClient()).setResultCallback(driveContentsCallback);
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_drive);
        progressBar = (ProgressBar) findViewById(R.id.progressBar);
        textView = (TextView) findViewById(R.id.text1);
    }

    final private ResultCallback<DriveApi.DriveContentsResult> driveContentsCallback =
            new ResultCallback<DriveApi.DriveContentsResult>() {
                @Override
                public void onResult(DriveApi.DriveContentsResult result) {
                    if (!result.getStatus().isSuccess()) {
                        showMessage("Error while trying to create new file contents");
                        return;
                    }
                    Log.e("Msg", "WritingStarted");

                    final DriveContents driveContents = result.getDriveContents();
                    new Thread() {
                        @Override
                        public void run() {
                            OutputStream outputStream = driveContents.getOutputStream();
                            FileInputStream inputStream = null;
                            try {
                                NoteBaseHelper db = new NoteBaseHelper(UploadToDriveActivity.this);
                                String inFileName = getApplicationContext().getDatabasePath(db.getDatabaseName()).getPath();
                                inputStream = new FileInputStream(inFileName);
                            } catch (FileNotFoundException e) {
                                Log.e("ERR", e.getMessage());
                                e.printStackTrace();
                            }

                            int n;
                            byte[] buffer = new byte[1024];
                            try {
                                if (inputStream != null) {
                                    Log.e("Msg", "Writing");
                                    while ((n = inputStream.read(buffer)) > 0) {
                                        outputStream.write(buffer, 0, n);
                                    }
                                    inputStream.close();
                                }
                            } catch (IOException e) {
                                Log.e("ERROR", e.getMessage());
                                e.printStackTrace();
                            }
                            MetadataChangeSet changeSet = new MetadataChangeSet.Builder().setTitle("Notebase.db" )
                                    .setMimeType("text/plain")
                                    .setStarred(true).build();
                            Drive.DriveApi.getRootFolder(getGoogleApiClient())
                                    .createFile(getGoogleApiClient(), changeSet, driveContents)
                                    .setResultCallback(fileCallBack);
                        }
                    }.start();

                }
            };


    final ResultCallback<DriveFolder.DriveFileResult> fileCallBack = new ResultCallback<DriveFolder.DriveFileResult>() {
        @Override
        public void onResult(DriveFolder.DriveFileResult driveFileResult) {

            if (!driveFileResult.getStatus().isSuccess()) {
                Log.e(TAG, "Error while trying to create the file");
                finish();
                return;
            }
            textView.setText(R.string.done);
            showMessage("Upload successful");
            finish();
        }
    };

}

