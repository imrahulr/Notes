package developers.sd.notes;

import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.CardView;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.util.SparseBooleanArray;
import android.view.ActionMode;
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
import java.util.ArrayList;
import java.util.List;

public class NoteListFragmentBin extends Fragment {

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

    private List<Note> del_Notes = new ArrayList<>();
    public boolean cab = true;

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

        View view = inflater.inflate(R.layout.fragment_note_list_bin, container, false);
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

    private class NoteHolder extends RecyclerView.ViewHolder implements View.OnClickListener, ActionMode.Callback {

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
        ActionMode actionMode;

        public NoteHolder(View itemView) {
            super(itemView);
            itemView.setOnClickListener(this);
            itemView.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View v) {
                    if (actionMode != null) {
                        return true;
                    }
                    cab = false;
                    actionMode = NoteListFragmentBin.this.getActivity().startActionMode(NoteHolder.this);
                    int id = getAdapterPosition();
                    myToggleSelection(id);
                    return true;
                }
            });
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
            mSolvedCheckBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                    mNote.setSolved(isChecked);
                    NoteLab.get(getActivity()).updateNote(mNote);
                }
            });

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
            if (cab == true) {
                Intent intent = NoteActivity.newIntent(getActivity(), mNote.getId());
                mNoteRecyclerView.invalidate();
                startActivity(intent);
            } else {
                int id = getAdapterPosition();
                myToggleSelection(id);
            }
        }

        private void myToggleSelection(int id) {
            mAdapter.toggleSelection(id);
            if (del_Notes.contains(mNote)) {
                del_Notes.remove(mNote);
            }
            else {
                del_Notes.add(mNote);
            }
        }

        @Override
        public boolean onCreateActionMode(ActionMode actionMode, Menu menu) {
            MenuInflater inflater = actionMode.getMenuInflater();
            inflater.inflate(R.menu.bin_coxtext_action_bar, menu);
            return true;
        }

        @Override
        public boolean onPrepareActionMode(ActionMode actionMode, Menu menu) {
            return false;
        }

        @Override
        public  boolean onActionItemClicked(ActionMode actionMode, MenuItem menuItem) {
            switch (menuItem.getItemId()) {
                case R.id.menu_item_delete_note:
                    List<Integer> selectedItemPositions1 = mAdapter.getSelectedItems();
                    Log.e("Long Press", "items" + selectedItemPositions1);
                    int currpos1;
                    NoteLab.get(getActivity()).deleteNotes(del_Notes);
                    for (int i = selectedItemPositions1.size() -1; i>=0; i--) {
                        currpos1 = selectedItemPositions1.get(i);
                        mAdapter.removeItem(currpos1);
                    }
                    actionMode.finish();
                    return true;
                case R.id.menu_item_restore_note:
                    List<Integer> selectedItemPositions2 = mAdapter.getSelectedItems();
                    Log.e("Long Press", "items" + selectedItemPositions2);
                    int currpos2;
                    NoteLab.get(getActivity()).restoreNotes(del_Notes);
                    for (int i = selectedItemPositions2.size() -1; i>=0; i--) {
                        currpos2 = selectedItemPositions2.get(i);
                        mAdapter.removeItem(currpos2);
                    }
                    actionMode.finish();
                    return true;
                default:
                    return false;
            }
        }

        @Override
        public void onDestroyActionMode(ActionMode actionMode) {
            cab = true;
            del_Notes.clear();
            this.actionMode = null;
            mAdapter.clearSelections();
        }

    }

    private class NoteAdapter extends RecyclerView.Adapter<NoteHolder> {

        private SparseBooleanArray selectedItems;

        public NoteAdapter(List<Note> notes) {
            mNotes = notes;
            selectedItems = new SparseBooleanArray();
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
            final Note note = mNotes.get(position);
            if(note.isDeleted()) {
                holder.bindNote(note);
                if (note.getColor() != "#FFFFFF") {
                    if (del_Notes.contains(note)) { holder.mCard.setCardBackgroundColor(getResources().getColor(R.color.list_item_selected)); }
                    else { holder.mCard.setCardBackgroundColor(Color.parseColor(note.getColor())); }
                }
            }
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

        public void toggleSelection (int position) {
            if (selectedItems.get(position, false)) {
                selectedItems.delete(position);
            } else {
                selectedItems.put(position, true);
            }
            notifyItemChanged(position);
        }

        public void clearSelections() {
            selectedItems.clear();
            notifyDataSetChanged();
        }

        public int getSelectedItemCount() { return  selectedItems.size(); }

        public List<Integer> getSelectedItems() {
            List<Integer> items = new ArrayList<>(selectedItems.size());
            for (int i=0; i<selectedItems.size(); i++) {
                items.add(selectedItems.keyAt(i));
            }
            return items;
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
        inflater.inflate(R.menu.fragment_note_list_bin, menu);
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

            case R.id.action_empty_bin:
                AlertDialog.Builder alertBuilder = new AlertDialog.Builder(getActivity());
                alertBuilder.setTitle(R.string.empty_bin)
                    .setPositiveButton(android.R.string.ok,
                            new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    NoteLab.get(getActivity()).emptyBin(mNotes);
                                    updateUI();
                                }
                            })
                    .setNegativeButton(android.R.string.cancel,
                            new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    dialog.dismiss();
                                }
                            })
                    .create();
                alertBuilder.show();
                return true;


            default:
                return super.onOptionsItemSelected(item);
        }
    }

    private void updateUI() {
        NoteLab noteLab = NoteLab.get(getActivity());
        List<Note> notes = noteLab.getNotesDeleted();
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
