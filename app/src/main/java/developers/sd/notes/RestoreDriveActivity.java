package developers.sd.notes;

import android.content.Intent;
import android.content.IntentSender;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.drive.Drive;
import com.google.android.gms.drive.DriveApi;
import com.google.android.gms.drive.DriveContents;
import com.google.android.gms.drive.DriveFile;
import com.google.android.gms.drive.DriveFolder;
import com.google.android.gms.drive.DriveId;
import com.google.android.gms.drive.OpenFileActivityBuilder;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import developers.sd.notes.database.NoteBaseHelper;

public class RestoreDriveActivity extends BaseDemoActivity {

    private final static String TAG = "RestoreDriveActivity";
    private ProgressBar progressBar;
    private TextView textView;
    private static  final int REQUEST_CODE_SELECT = 102;
    private int i = 1;

    @Override
    public void onConnected(Bundle connectionHint) {
        super.onConnected(connectionHint);
        if (i == 1) {
            textView.setVisibility(View.GONE);
            progressBar.setVisibility(View.GONE);
            IntentSender intentSender = Drive.DriveApi.newOpenFileActivityBuilder()
                    .setMimeType(new String[]{DriveFolder.MIME_TYPE, "text/plain"})
                    .build(getGoogleApiClient());
            try {
                startIntentSenderForResult(intentSender, REQUEST_CODE_SELECT, null, 0, 0, 0);
            } catch (IntentSender.SendIntentException e) {
                Log.e(TAG, "Unable to send intent", e);
            }
        }
        if (i == 3) finish();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_drive);
        progressBar = (ProgressBar) findViewById(R.id.progressBar);
        textView = (TextView) findViewById(R.id.text1);
        textView.setText("Logging in to Drive");
        textView.setVisibility(View.VISIBLE);
        progressBar.setVisibility(View.VISIBLE);
    }

    @Override
    protected void onActivityResult(int requestCode, int resutCode, Intent data) {
        Log.e(TAG, "in onActivityResult()");
        switch (requestCode) {
            case REQUEST_CODE_SELECT:
                if (resutCode == RESULT_OK) {
                    i = 2;
                    DriveId driveId = data.getParcelableExtra(OpenFileActivityBuilder.EXTRA_RESPONSE_DRIVE_ID);
                    DriveFile file = driveId.asDriveFile();
                    downloadFromDrive(file);
                } else { finish(); }
        }
    }

    final private ResultCallback<DriveApi.DriveContentsResult> driveContentsCallback =
            new ResultCallback<DriveApi.DriveContentsResult>() {
                @Override
                public void onResult(DriveApi.DriveContentsResult result) {
                    if (!result.getStatus().isSuccess()) {
                        Log.e(TAG, "Failed123....");
                        showMessage("Error while trying to create new file contents");
                        return;
                    }

                    Log.e(TAG, "Donn123....");
                    final DriveContents contents = result.getDriveContents();
                    final InputStream inputStream = contents.getInputStream();

                    new Thread() {
                        @Override
                        public void run() {
                            try {
                                NoteBaseHelper db = new NoteBaseHelper(RestoreDriveActivity.this);
                                String inFileName = getApplicationContext().getDatabasePath(db.getDatabaseName()).getPath();
                                File file = new File(inFileName);
                                OutputStream outputStream = new FileOutputStream(file);
                                try {
                                    try {
                                        byte[] buffer = new byte[1024];
                                        int n;
                                        while ((n = inputStream.read(buffer)) != -1) {
                                            outputStream.write(buffer, 0, n);
                                        }
                                        outputStream.flush();
                                    } finally {
                                        outputStream.close();
                                    }
                                } catch (Exception e) {
                                    Log.e(TAG, e.getMessage());
                                    e.printStackTrace();
                                }
                            } catch (FileNotFoundException e) {
                                Log.e(TAG, e.getMessage());
                                e.printStackTrace();
                            } finally {
                                try {
                                    inputStream.close();
                                    textView.setText(R.string.done);
                                    finish();
                                } catch (IOException e) {
                                    Log.e(TAG, e.getMessage());
                                    e.printStackTrace();
                                }
                            }
                        }
                    }.start();
                    i = 3;
                }
    };

    private void downloadFromDrive(DriveFile file) {

       textView.setText(R.string.restoring);
       textView.setVisibility(View.VISIBLE);
       progressBar.setVisibility(View.VISIBLE);
       file.open(getGoogleApiClient(), DriveFile.MODE_READ_ONLY, null).setResultCallback(driveContentsCallback);

    }

}
