package developers.sd.notes;

import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.Typeface;
import android.media.MediaPlayer;
import android.media.MediaRecorder;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.content.FileProvider;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.KeyEvent;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.UUID;

public class NoteFragment extends Fragment {

    private static final String ARG_NOTE_ID = "note_id";
    private static final String DIALOG_DELETE = "DialogDelete";
    private static final String DIALOG_DELETE_FINAL = "DialogDeleteFinal";


    private static final int REQUEST_DELETE = 0;
    private static final int REQUEST_DELETE_FINAL = 23;
    private static final int REQUEST_PHOTO = 2;
    private static final int SELECT_PHOTO = 3;


    private boolean start = true;
    private boolean checkboxtext = false;
    int i = 1;
    private static String audioFilePath, galleryPhotoFilePath;
    private static MediaPlayer mediaPlayer = new MediaPlayer();
    private static MediaRecorder mediaRecorder = new MediaRecorder();

    private Note mNote;
    private EditText mTitleField;
    private EditText mSubjectField;
    private Button mDateButton;
    private CheckBox mSolvedCheckBox;
    private File mPhotoFile;

    private FloatingActionButton fab_add, fab_insert_photo, fab_insert_audio, fab_share, fab_insert_camera_photo, fab_color;
    private Animation fab_close, fab_open, rotate_backward, rotate_forward;
    private Boolean isFabOpen = false;
    private Boolean isFabOpen1 = false;
    private ImageView mPhotoView;
    private ImageView mPhotoView1;
    private TextView maudio;
    private ImageButton mplay, maudioshare;

    private FloatingActionButton color_aaa,color_ff0099cc,color_ff669900,color_ffff8800,color_ffcc0000;
    private FloatingActionButton color_FF303F9F;

    public static NoteFragment newInstance(UUID noteId) {
        Bundle args = new Bundle();
        args.putSerializable(ARG_NOTE_ID, noteId);
        NoteFragment fragment = new NoteFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        UUID noteId = (UUID) getArguments().getSerializable(ARG_NOTE_ID);
        mNote = NoteLab.get(getActivity()).getNote(noteId);
        mPhotoFile = NoteLab.get(getActivity()).getPhotoFile(mNote);
        this.setHasOptionsMenu(true);
    }

    @Override
    public void onPause() {
        if (mNote.getTitle().equals("Title for Note") & mNote.getSubject() == null & !fileExistsInSD(audioFilePath) & mNote.getColor().equals("#ffffff")) {
            NoteLab.get(getActivity()).deleteEmptyNote(mNote);
        } else { NoteLab.get(getActivity()).updateNote(mNote); }
        super.onPause();
    }

    @Override
    public void onResume() {
        super.onResume();
        NoteLab.get(getActivity()).updateNote(mNote);
    }

    @Override
    public void onStop() {
        if (mNote.getTitle().equals("Title for Note") & mNote.getSubject() == null & !fileExistsInSD(audioFilePath) & mNote.getColor().equals("#ffffff")) {
            NoteLab.get(getActivity()).deleteEmptyNote(mNote);
        } else { NoteLab.get(getActivity()).updateNote(mNote); }
        super.onStop();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View v = inflater.inflate(R.layout.fragment_note, container, false);
        updateColor(v);
        Toolbar tToolbar = (Toolbar) v.findViewById(R.id.tToolbar);
        ((AppCompatActivity) getActivity()).setSupportActionBar(tToolbar);
        ((AppCompatActivity) getActivity()).getSupportActionBar().setTitle(R.string.app_name);

        audioFilePath = getContext().getExternalFilesDir(Environment.DIRECTORY_MUSIC).getAbsolutePath() + "/" + mNote.getId() + ".mp3";
        galleryPhotoFilePath = getContext().getExternalFilesDir(Environment.DIRECTORY_PICTURES).getAbsolutePath() + "/" + mNote.getId() + ".jpeg";

        final PackageManager packageManager = getActivity().getPackageManager();

        fab_add = (FloatingActionButton) v.findViewById(R.id.fab_add);
        fab_add.setVisibility(View.VISIBLE);
        fab_add.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                InputMethodManager imm = (InputMethodManager) getActivity().getSystemService(Activity.INPUT_METHOD_SERVICE);
                if(imm.isAcceptingText())
                {
                    imm.hideSoftInputFromWindow(v.getWindowToken(), 0);}
                animateFAB();
            }
        });

        fab_insert_photo = (FloatingActionButton) v.findViewById(R.id.fab_insert_photo);
        fab_insert_photo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent();
                intent.setType("image/*");
                intent.setAction(Intent.ACTION_GET_CONTENT);
                startActivityForResult(Intent.createChooser(intent, "Select Picture"), SELECT_PHOTO);

            }
        });

        fab_insert_camera_photo = (FloatingActionButton) v.findViewById(R.id.fab_insert_camera_photo);
        final Intent captureImage = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        boolean canTakePhoto = mPhotoFile != null &&
                captureImage.resolveActivity(packageManager) != null;
        fab_insert_camera_photo.setEnabled(canTakePhoto);
        if (canTakePhoto) {
            Uri uri = FileProvider.getUriForFile(getContext(), "developers.sd.notes.provider", mPhotoFile);
            captureImage.putExtra(MediaStore.EXTRA_OUTPUT, uri);
        }
        fab_insert_camera_photo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivityForResult(captureImage, REQUEST_PHOTO);
            }
        });

        fab_insert_audio = (FloatingActionButton) v.findViewById(R.id.fab_insert_audio);
        fab_insert_audio.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                start = !start;
                if (start) {
                    try {
                        i++;
                        Toast.makeText(getContext(), "Started Recording.... Press again to Stop", Toast.LENGTH_LONG).show();
                        fab_insert_audio.setImageResource(R.drawable.ic_pause_white_24dp);
                        mediaRecorder = new MediaRecorder();
                        mediaRecorder.setAudioSource(MediaRecorder.AudioSource.MIC);
                        mediaRecorder.setOutputFormat(MediaRecorder.OutputFormat.MPEG_4);
                        mediaRecorder.setOutputFile(audioFilePath);
                        mediaRecorder.setAudioEncoder(MediaRecorder.AudioEncoder.AAC);
                        mediaRecorder.prepare();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    mediaRecorder.start();
                } else  {
                    try {
                        Toast.makeText(getContext(), "Stop", Toast.LENGTH_LONG).show();
                        fab_insert_audio.setImageResource(R.drawable.ic_audiotrack_white_24dp);
                        mediaRecorder.stop();
                        mediaRecorder.reset();
                        mediaRecorder.release();
                        updateaudio();
                    }
                    catch (Exception e)
                    {
                        e.printStackTrace();
                    }
                }
                start = !start;
            }
        });

        fab_share = (FloatingActionButton) v.findViewById(R.id.fab_share);
        fab_share.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mNote.getImage() != null | fileExistsInSD(mPhotoFile.toString())) {
                    ArrayList<Uri> uri = new ArrayList<>();
                    Intent i = new Intent(Intent.ACTION_SEND_MULTIPLE);
                    i.setType("image/jpeg");
                    i.putExtra(Intent.EXTRA_TEXT, getNoteData());
                    if (mNote.getImage() != null) {
                        uri.add(Uri.parse(galleryPhotoFilePath));
                        i.putExtra(Intent.EXTRA_STREAM, uri);

                    }
                    if (fileExistsInSD(mPhotoFile.toString())) {
                        uri.add(Uri.parse(mPhotoFile.toString()));
                        i.putExtra(Intent.EXTRA_STREAM, uri);
                        i.setType("image/jpeg");
                        i.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
                    }
                    i = Intent.createChooser(i, "Share Note:");
                    startActivity(i);
                }
                else {
                    Intent i = new Intent(Intent.ACTION_SEND);
                    i.setType("text/plain");
                    i.putExtra(Intent.EXTRA_TEXT, getNoteData());
                    i = Intent.createChooser(i, "Share Note:");
                    startActivity(i);
                }
            }
        });

        fab_color = (FloatingActionButton) v.findViewById(R.id.fab_note_color);
        fab_color.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                animateFAB_color();
            }
        });

        color_aaa = (FloatingActionButton) v.findViewById(R.id.color_aaa);
        color_aaa.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mNote.setColor("#FFFFFFFF");
                updateColor(v);
                refreshFragment();
            }
        });

        color_ff0099cc = (FloatingActionButton) v.findViewById(R.id.color_ff0099cc);
        color_ff0099cc.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mNote.setColor("#c4c6ec");
                updateColor(v);
                refreshFragment();
            }
        });

        color_ff669900 = (FloatingActionButton) v.findViewById(R.id.color_ff669900);
        color_ff669900.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mNote.setColor("#AED581");
                updateColor(v);
                refreshFragment();
            }
        });

        color_ffff8800 = (FloatingActionButton) v.findViewById(R.id.color_ffff8800);
        color_ffff8800.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mNote.setColor("#FFF8EC7A");
                updateColor(v);
                refreshFragment();
            }
        });

        color_ffcc0000 = (FloatingActionButton) v.findViewById(R.id.color_ffcc0000);
        color_ffcc0000.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mNote.setColor("#ffcdd2");
                updateColor(v);
                refreshFragment();
            }
        });

        color_FF303F9F = (FloatingActionButton) v.findViewById(R.id.color_FF303F9F);
        color_FF303F9F.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mNote.setColor("#BBDEFB");
                updateColor(v);
                refreshFragment();
            }
        });

        fab_open = AnimationUtils.loadAnimation(getActivity().getApplicationContext(), R.anim.fab_open);
        fab_close = AnimationUtils.loadAnimation(getActivity().getApplicationContext(), R.anim.fab_close);
        rotate_forward = AnimationUtils.loadAnimation(getActivity().getApplicationContext(), R.anim.rotate_forward);
        rotate_backward = AnimationUtils.loadAnimation(getActivity().getApplicationContext(), R.anim.rotate_backward);

        mPhotoView = (ImageView) v.findViewById(R.id.note_photo);
        mPhotoView1 = (ImageView) v.findViewById(R.id.note_photo1);

        new LoadAsyncTask().execute(mNote);

        if (fileExistsInSD(mPhotoFile.toString())) {
            mPhotoView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent fullScreenIntent = new Intent(v.getContext(), FullScreenImage.class);
                    fullScreenIntent.putExtra("Type", 1);
                    fullScreenIntent.putExtra("CameraFile", mPhotoFile.toString());
                    NoteFragment.this.startActivity(fullScreenIntent);
                }
            });
        }
        if (mNote.getImage() != null) {
            mPhotoView1.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent fullScreenIntent = new Intent(v.getContext(), FullScreenImage.class);
                    fullScreenIntent.putExtra("Type", 2);
                    fullScreenIntent.putExtra("GalleryPic", galleryPhotoFilePath);
//                    fullScreenIntent.putExtra("GalleryPic", mNote.getImage());

                    NoteFragment.this.startActivity(fullScreenIntent);
                }
            });
        }

        maudio = (TextView) v.findViewById(R.id.audio);
        maudio.setTypeface(null, Typeface.BOLD);
        mplay = (ImageButton) v.findViewById(R.id.play);
        maudioshare = (ImageButton) v.findViewById(R.id.audio_share);
        if (fileExistsInSD(audioFilePath)) {
            updateaudio();
        }

        mplay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                start = !start;
                if (start) {
                    try {
                        mplay.setImageResource(R.drawable.stop_24);
                        mediaPlayer = new MediaPlayer();
                        mediaPlayer.setDataSource(audioFilePath);
                        mediaPlayer.prepare();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    mediaPlayer.start();
                    mediaPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {

                        @Override
                        public void onCompletion(MediaPlayer mp) {
                            mplay.setImageResource(R.drawable.play_24);
                            start = !start;
                        }

                    });
                } else {
                    mplay.setImageResource(R.drawable.play_24);
                    mediaPlayer.stop();
                    mediaPlayer.reset();
                    mediaPlayer.release();
                }
            }
        });

        maudioshare.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Uri audiouri = Uri.parse(audioFilePath);
                Intent i = new Intent(Intent.ACTION_SEND);
                i.setType("audio/3gp");
                i.putExtra(Intent.EXTRA_STREAM, audiouri);
                startActivity(Intent.createChooser(i, "Share Audio:"));
            }
        });

        mTitleField = (EditText) v.findViewById(R.id.note_title);
        mTitleField.setText(mNote.getTitle());
        mTitleField.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(
                    CharSequence s, int start, int count, int after) {
                // This space intentionally left blank
            }

            @Override
            public void onTextChanged(
                    CharSequence s, int start, int before, int count) {
//                if(save){mNote.setTitle(s.toString());}
                mNote.setTitle(s.toString());
            }

            @Override
            public void afterTextChanged(Editable s) {
                // This one too
            }
        });

        mSubjectField = (EditText) v.findViewById(R.id.note_subject);
        mSubjectField.setText(mNote.getSubject());
        mSubjectField.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(
                    CharSequence s, int start, int count, int after) {
                // This space intentionally left blank
            }

            @Override
            public void onTextChanged(
                    CharSequence s, int start, int before, int count) {
                mNote.setSubject(s.toString());
            }

            @Override
            public void afterTextChanged(Editable s) {
                // This one too
            }
        });

        mDateButton = (Button) v.findViewById(R.id.note_date);
        mDateButton.setText(mNote.getDate());
        mDateButton.setEnabled(false);

        mSolvedCheckBox = (CheckBox) v.findViewById(R.id.note_solved);
        mSolvedCheckBox.setChecked(mNote.isSolved());
        if (mNote.isSolved()) {
            mSolvedCheckBox.setText(R.string.note_solved_label2);
            checkboxtext = true;
        }
        mSolvedCheckBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                checkboxtext = !checkboxtext;
                if (checkboxtext) {
                    mSolvedCheckBox.setText(R.string.note_solved_label2);
                } else {
                    mSolvedCheckBox.setText(R.string.note_solved_label1);
                }
                mNote.setSolved(isChecked);
            }
        });

        return v;
    }

    private class LoadAsyncTask extends AsyncTask<Note, Void, Note> {

        @Override
        protected Note doInBackground(Note...params) {
            Note note = params[0];
            return note;
        }

        @Override
        protected void onPostExecute(Note result) {
            updatePhotoView();
            updatePhotoView1();
        }
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        inflater.inflate(R.menu.fragment_note, menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.menu_item_delete_note:
                if(mNote.isDeleted()){
                    FragmentManager manager = getFragmentManager();
                    DeleteDialogFinal dialog = new DeleteDialogFinal().newInstance(mNote.getId());
                    dialog.setTargetFragment(NoteFragment.this, REQUEST_DELETE_FINAL);
                    dialog.show(manager, DIALOG_DELETE_FINAL);
                }
                else {
                    FragmentManager manager = getFragmentManager();
                    DeleteDialog dialog = new DeleteDialog().newInstance(mNote.getId());
                    dialog.setTargetFragment(NoteFragment.this, REQUEST_DELETE);
                    dialog.show(manager, DIALOG_DELETE);
                }
                return true;

            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {

        if (resultCode != Activity.RESULT_OK) {
            return;
        }
        if (requestCode == REQUEST_PHOTO) {
            updatePhotoView();
            refreshFragment();
        }
        if (requestCode == SELECT_PHOTO) {
            Uri uri = data.getData();
            try {
                Bitmap bitmap = MediaStore.Images.Media.getBitmap(getActivity().getContentResolver(), uri);
                Bitmap scaledBitmap = BitmapUtility.scaleDown(bitmap, 1000, true);
                mNote.setImage(BitmapUtility.getBytes(scaledBitmap));
                if(mNote.getImage() != null ) {
                    Bitmap bitmap1 = BitmapUtility.getImage(mNote.getImage());
                    mPhotoView1.setImageBitmap(bitmap1);
                }
                FileOutputStream gallery_fos = new FileOutputStream(galleryPhotoFilePath);
                gallery_fos.write(mNote.getImage());
                gallery_fos.close();
                refreshFragment();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        if(requestCode==REQUEST_DELETE) {
            mNote.setDeleted(true);
        }
        if(requestCode==REQUEST_DELETE_FINAL){
            NoteLab.get(getActivity()).deleteNote(mNote);
        }
    }

    public void animateFAB() {

        if (isFabOpen) {

            fab_add.startAnimation(rotate_backward);
            fab_insert_audio.startAnimation(fab_close);
            fab_insert_photo.startAnimation(fab_close);
            fab_share.startAnimation(fab_close);
            fab_color.startAnimation(fab_close);
            fab_insert_camera_photo.startAnimation(fab_close);
            fab_insert_audio.setClickable(false);
            fab_insert_photo.setClickable(false);
            fab_insert_camera_photo.setClickable(false);
            fab_share.setClickable(false);
            fab_color.setClickable(false);
            isFabOpen = false;

        } else {

            fab_add.startAnimation(rotate_forward);
            fab_insert_photo.startAnimation(fab_open);
            fab_insert_audio.startAnimation(fab_open);
            fab_share.startAnimation(fab_open);
            fab_color.startAnimation(fab_open);
            fab_insert_camera_photo.startAnimation(fab_open);
            fab_insert_photo.setClickable(true);
            fab_insert_audio.setClickable(true);
            fab_share.setClickable(true);
            fab_color.setClickable(true);
            fab_insert_camera_photo.setClickable(true);
            isFabOpen = true;
        }
    }

    public void animateFAB_color() {

        if (isFabOpen1) {
            color_aaa.startAnimation(fab_close);
            color_ff0099cc.startAnimation(fab_close);
            color_ff669900.startAnimation(fab_close);
            color_ffff8800.startAnimation(fab_close);
            color_ffcc0000.startAnimation(fab_close);
            color_FF303F9F.startAnimation(fab_close);
            color_aaa.setClickable(false);
            color_ff0099cc.setClickable(false);
            color_ff669900.setClickable(false);
            color_ffff8800.setClickable(false);
            color_ffcc0000.setClickable(false);
            color_FF303F9F.setClickable(false);
            isFabOpen1 = false;

        }
        else {
            color_aaa.startAnimation(fab_open);
            color_ff0099cc.startAnimation(fab_open);
            color_ff669900.startAnimation(fab_open);
            color_ffff8800.startAnimation(fab_open);
            color_ffcc0000.startAnimation(fab_open);
            color_FF303F9F.startAnimation(fab_open);
            color_aaa.setClickable(true);
            color_ff0099cc.setClickable(true);
            color_ff669900.setClickable(true);
            color_ffff8800.setClickable(true);
            color_ffcc0000.setClickable(true);
            color_FF303F9F.setClickable(true);
            isFabOpen1 = true;
        }
    }

    private String getNoteData() {
        return getString(R.string.note_share, mNote.getTitle(), mNote.getDate(),
                mNote.getSubject());
    }

    private void updatePhotoView() {
        if (mPhotoFile == null || !mPhotoFile.exists()) {
            mPhotoView.setImageDrawable(null);
        } else {
            Bitmap bitmap = PictureUtils.getScaledBitmap(mPhotoFile.getPath(), getActivity());
            mPhotoView.setImageBitmap(bitmap);
        }
    }

    private void updatePhotoView1(){
        if(mNote.getImage() != null) {
            Bitmap bitmap1 = BitmapUtility.getImage(mNote.getImage());
            mPhotoView1.setImageBitmap(bitmap1);
        }
        else{
            return;
        }
    }

    private void updateaudio() {
        mplay.setEnabled(true);
        mplay.setVisibility(View.VISIBLE);
        maudioshare.setEnabled(true);
        maudioshare.setVisibility(View.VISIBLE);
        maudio.setVisibility(View.VISIBLE);
        maudio.setText("Audio - " + mNote.getAudioName());
    }

    private void updateColor(View v){
        if(mNote.getColor()!=null) {
            View v1 = v.getRootView();
            v1.setBackgroundColor(Color.parseColor(mNote.getColor()));
        }
        else{
            View v1 = v.getRootView();
            v1.setBackgroundColor(Color.WHITE);
        }
    }

    public void refreshFragment()
    {
        Fragment currentFragment = getFragmentManager().findFragmentById(R.id.fragment_container);
        FragmentTransaction fragTransaction = getFragmentManager().beginTransaction();
        fragTransaction.detach(currentFragment);
        fragTransaction.attach(currentFragment);
        fragTransaction.commit();
        isFabOpen = false;
        isFabOpen1 = false;

    }

    public boolean fileExistsInSD(String sFileName){
        java.io.File file = new java.io.File(sFileName);
        return file.exists();
    }
}