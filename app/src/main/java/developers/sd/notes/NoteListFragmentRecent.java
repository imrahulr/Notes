package developers.sd.notes;

import android.app.SearchManager;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.GravityCompat;
import android.support.v4.view.MenuItemCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.CardView;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.TextView;

import java.io.File;
import java.util.List;

public class NoteListFragmentRecent extends Fragment {

    private RecyclerView mNoteRecyclerView;
    private NoteAdapter mAdapter;
    private List<Note> mNotes;
    private RecyclerView.LayoutManager mLayoutManager;
    private MenuItem mlayout_change;
    private MenuItem mTheme_change;
    private int incr = 1;
    public int incr1 = 1;
    private int menu_Item_No = 1;
    private RecyclerView.LayoutManager mlayoutview;
    private String themecardcolor;


    private final static String LAYOUT_NO = "layout_no";
    private final static String ARG_MENU_ITEM_NO = "menu_Item_No";
    private DrawerLayout drawer;


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_note_list_recent, container, false);
        mNoteRecyclerView = (RecyclerView) view
                .findViewById(R.id.note_recycler_view);

        mLayoutManager = new LinearLayoutManager(getActivity());
        mNoteRecyclerView.setLayoutManager(mLayoutManager);

        View v = getActivity().findViewById(R.id.drawer_layout);
        drawer = (DrawerLayout) v.findViewById(R.id.drawer_layout);

        final Toolbar tToolbar = (Toolbar) view.findViewById(R.id.tToolbar);
        ((AppCompatActivity) getActivity()).setSupportActionBar(tToolbar);
        ((AppCompatActivity) getActivity()).getSupportActionBar().setTitle(R.string.app_name);
        tToolbar.setNavigationIcon(R.drawable.ic_menu_white_24dp);
        updateUI();
        return view;
    }

    private class NoteHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        private Note mNote;
        private TextView mTitleTextView;
        private TextView mSubjectTextView;
        private TextView mDateTextView;
        private CheckBox mSolvedCheckBox;
        private CardView mCard;
        private ImageView mNoteImage, mAudioImage;
        private TextView mAudioText;
        private File mImageFile;
        private String maudioFileText;

        public NoteHolder(View itemView) {
            super(itemView);
            itemView.setOnClickListener(this);
            mTitleTextView = (TextView)
                    itemView.findViewById(R.id.list_item_note_title_text_view);
            mSubjectTextView = (TextView)
                    itemView.findViewById(R.id.list_item_note_subject_text_view);
            mDateTextView = (TextView)
                    itemView.findViewById(R.id.list_item_note_date_text_view);
            mSolvedCheckBox = (CheckBox)
                    itemView.findViewById(R.id.list_item_note_solved_check_box);
            mCard = (CardView) itemView.findViewById(R.id.card_view);
            mNoteImage = (ImageView) itemView.findViewById(R.id.note_image);
            mAudioImage = (ImageView) itemView.findViewById(R.id.note_audio_image);
            mAudioText = (TextView) itemView.findViewById(R.id.note_audio_text);
        }


        public void bindNote(Note note) {
            mNote = note;
            mTitleTextView.setText(mNote.getTitle());
            mSubjectTextView.setText(mNote.getSubject());
            mDateTextView.setText(mNote.getDate());
            mSolvedCheckBox.setChecked(mNote.isSolved());

            new LoadImageAsyncTask().execute(mNote);

            maudioFileText = getContext().getExternalFilesDir(Environment.DIRECTORY_MUSIC).getAbsolutePath() + "/" + mNote.getId() + ".mp3";
            if (fileExistsInSD(maudioFileText)) {
                mAudioText.setVisibility(View.VISIBLE);
                mAudioImage.setVisibility(View.VISIBLE);
            } else {
                mAudioText.setVisibility(View.GONE);
                mAudioImage.setVisibility(View.GONE);
            }
            if (mNote.getColor() != "#FFFFFF") {
                mCard.setCardBackgroundColor(Color.parseColor(mNote.getColor()));
            }
        }

        private class LoadImageAsyncTask extends AsyncTask<Note, Void, Note> {

            private File mImageFile;
            private Bitmap bitmap1, bitmap2;

            @Override
            protected Note doInBackground(Note...params) {
                Note note = params[0];
                mImageFile = NoteLab.get(getActivity()).getPhotoFile(note);
                if (mImageFile == null || !mImageFile.exists()) {
                    if(mNote.getImage() != null) {
                        bitmap1 = BitmapUtility.getImage(mNote.getImage());
                        int nh = (int) (bitmap1.getHeight() * (128.0 / bitmap1.getWidth()));
                        bitmap2 = Bitmap.createScaledBitmap(bitmap1, 128, nh, true);
                    }
                } else {
                    bitmap1 = PictureUtils.getScaledBitmap(mImageFile.getPath(), getActivity());
                    int nh = (int) (bitmap1.getHeight() * (128.0/bitmap1.getWidth()));
                    bitmap2 = Bitmap.createScaledBitmap(bitmap1, 128, nh, true);
                }
                return note;
            }

            @Override
            protected void onPostExecute(Note result) {
                if (mImageFile == null || !mImageFile.exists()) {
                    if(mNote.getImage() != null) {
                        mNoteImage.setVisibility(View.VISIBLE);
                        mNoteImage.setImageBitmap(BitmapUtility.getCircleBitmap(bitmap2));
                    } else { mNoteImage.setImageDrawable(null); }
                } else {
                    mNoteImage.setVisibility(View.VISIBLE);
                    mNoteImage.setImageBitmap(BitmapUtility.getCircleBitmap(bitmap2));
                }
            }
        }

        @Override
        public void onClick(View v) {
            Intent intent = NoteActivity.newIntent(getActivity(), mNote.getId());
            mNoteRecyclerView.invalidate();
            startActivity(intent);
        }
    }

    private class NoteAdapter extends RecyclerView.Adapter<NoteHolder> {


        public NoteAdapter(List<Note> notes) {
            mNotes = notes;
        }


        @Override
        public NoteHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            LayoutInflater layoutInflater = LayoutInflater.from(getActivity());
            View view = layoutInflater
                    .inflate(R.layout.list_item_note, parent, false);
            return new NoteHolder(view);
        }

        @Override
        public void onBindViewHolder(NoteHolder holder, int position) {
            final Note note = mNotes.get(getItemCount() - position - 1);
            holder.bindNote(note);
            if (note.getColor() != "#5a595b" && note.getColor() != "#FFFFFF") {
                holder.mCard.setCardBackgroundColor(Color.parseColor(note.getColor()));
            }
            holder.mSolvedCheckBox.setChecked(note.isSolved());
            holder.mSolvedCheckBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                    note.setSolved(isChecked);
                    NoteLab.get(getActivity()).updateNote(note);
                }
            });
        }


        @Override
        public int getItemCount() {
            return mNotes.size();
        }

        public void setNotes(List<Note> notes) {
            mNotes = notes;
        }

        public Note removeItem(int position) {
            final Note note = mNotes.remove(position);
            notifyItemRemoved(position);
            return note;
        }


        public void addItem(int position, Note note) {
            mNotes.add(position, note);
            notifyItemInserted(position);
        }

        public void moveItem(int fromPosition, int toPosition) {
            final Note note = mNotes.remove(fromPosition);
            mNotes.add(toPosition, note);
            notifyItemMoved(fromPosition, toPosition);
        }

        public void animateTo(List<Note> notes) {
            applyAndAnimateRemovals(notes);
            applyAndAnimateAdditions(notes);
            applyAndAnimateMovedItems(notes);
        }


        private void applyAndAnimateRemovals(List<Note> newnotes) {
            for (int i = mNotes.size() - 1; i >= 0; i--) {
                final Note note = mNotes.get(i);
                if (!newnotes.contains(note)) {
                    removeItem(i);
                }
            }
        }


        private void applyAndAnimateAdditions(List<Note> newnotes) {
            for (int i = 0, count = newnotes.size(); i < count; i++) {
                final Note note = newnotes.get(i);
                if (!mNotes.contains(note)) {
                    addItem(i, note);
                }
            }
        }

        private void applyAndAnimateMovedItems(List<Note> newnotes) {
            for (int toPosition = newnotes.size() - 1; toPosition >= 0; toPosition--) {
                final Note note = newnotes.get(toPosition);
                final int fromPosition = mNotes.indexOf(note);
                if (fromPosition >= 0 && fromPosition != toPosition) {
                    moveItem(fromPosition, toPosition);
                }
            }
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        updateUI();
    }

    @Override
    public void onPause() {
        super.onPause();
        updateUI();
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        inflater.inflate(R.menu.fragment_note_list_recent, menu);
        mlayout_change = menu.findItem(R.id.action_layout_change);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_layout_change:
                switch (incr) {
                    case 1:
                        mlayoutview = new StaggeredGridLayoutManager(2, 1);
                        mNoteRecyclerView.setLayoutManager(new StaggeredGridLayoutManager(2, 1));
                        mlayout_change.setIcon(getResources().getDrawable(R.drawable.ic_view_quilt_white_24dp));
                        incr = 2;
                        return true;

                    case 2:
                        mlayoutview = new StaggeredGridLayoutManager(3, 1);
                        mNoteRecyclerView.setLayoutManager(new StaggeredGridLayoutManager(3, 1));
                        mlayout_change.setIcon(getResources().getDrawable(R.drawable.ic_view_quilt_white_24dp));
                        incr = 3;
                        return true;

                    case 3:
                        mlayoutview = new LinearLayoutManager(getActivity());
                        mNoteRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
                        mlayout_change.setIcon(getResources().getDrawable(R.drawable.ic_view_stream_white_24dp));
                        incr = 1;
                        return true;

                    default: return true;
                }

            case android.R.id.home:
                drawer.openDrawer(GravityCompat.START);
                return true;


            default:
                return super.onOptionsItemSelected(item);
        }
    }




    public void refreshFragment()
    {
        Fragment currentFragment = getFragmentManager().findFragmentById(R.id.fragment_container);
        FragmentTransaction fragTransaction = getFragmentManager().beginTransaction();
        fragTransaction.detach(currentFragment);
        fragTransaction.attach(currentFragment);
        fragTransaction.commit();
        incr1=1;
        incr=1;
    }

    private void updateUI() {
        NoteLab noteLab = NoteLab.get(getActivity());
        List<Note> notes = noteLab.getNotes();
        if(mlayoutview!=null){mNoteRecyclerView.setLayoutManager(mlayoutview);}

        if (mAdapter == null) {
            mAdapter = new NoteAdapter(notes);
            mNoteRecyclerView.setAdapter(mAdapter);
        } else {
            mAdapter.setNotes(notes);
            mAdapter.notifyDataSetChanged();
        }
    }

    public boolean fileExistsInSD(String sFileName){
        java.io.File file = new java.io.File(sFileName);
        return file.exists();
    }
}
