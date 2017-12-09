package developers.sd.notes;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Environment;
import android.util.Log;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import developers.sd.notes.database.NoteBaseHelper;
import developers.sd.notes.database.NoteCursorWrapper;
import developers.sd.notes.database.NoteDbSchema.NoteTable;

public class NoteLab {

    private static NoteLab sNoteLab;
    private Context mContext;
    private SQLiteDatabase mDatabase;

    public void addNote(Note c) {
        ContentValues values = getContentValues(c);
        mDatabase.insert(NoteTable.NAME, null, values);
    }

    public void deleteNote(Note c)
    {
        ContentValues values = getContentValues(c);
        mDatabase.delete(NoteTable.NAME, "UUID =?", new String[]{c.getId().toString()});
    }

    public void deleteEmptyNote(Note c)
    {
        ContentValues values = getContentValues(c);
        mDatabase.delete(NoteTable.NAME, "UUID =?", new String[]{c.getId().toString()});
    }

    public void emptyBin(List<Note> mNotes)
    {
        NoteCursorWrapper cursor = queryNotes(null, null);
        try {
            cursor.moveToFirst();
            while (!cursor.isAfterLast()) {
                if(cursor.getNote().isDeleted())
                {mDatabase.delete(NoteTable.NAME, "UUID =?", new String[]{cursor.getNote().getId().toString()});}
                cursor.moveToNext();
            }
        } finally {
            cursor.close();
        }
    }

    public void deleteNotes(List<Note> mNotes) {
        for (int i = 0; i < mNotes.size(); i++) {
            if (mNotes.get(i).isDeleted()) {
                mDatabase.delete(NoteTable.NAME, "UUID =?", new String[]{mNotes.get(i).getId().toString()});
            }
            else {
                mNotes.get(i).setDeleted(true);
                updateNote(mNotes.get(i));
            }
        }
    }

    public void restoreNotes(List<Note> mNotes) {
        for (int i = 0; i < mNotes.size(); i++) {
            mNotes.get(i).setDeleted(false);
            updateNote(mNotes.get(i));
        }
    }

    public static NoteLab get(Context context) {
        if (sNoteLab == null) {
            sNoteLab = new NoteLab(context);
        }
        return sNoteLab;
    }

    private NoteLab(Context context) {
        mContext = context.getApplicationContext();
        mDatabase = new NoteBaseHelper(mContext)
                .getWritableDatabase();
//        mNotes = new ArrayList<>();
    }

    public List<Note> getNotes() {
        List<Note> notes = new ArrayList<>();
        NoteCursorWrapper cursor = queryNotes(null, null);
        try {
            cursor.moveToFirst();
            while (!cursor.isAfterLast()) {
                if(!cursor.getNote().isDeleted()){
                notes.add(cursor.getNote());}
                cursor.moveToNext();
            }
        } finally {
            cursor.close();
        }
        return notes;
    }

    public List<Note> getNotesFav() {
        List<Note> notes = new ArrayList<>();
        NoteCursorWrapper cursor = queryNotes(null, null);
        try {
            cursor.moveToFirst();
            while (!cursor.isAfterLast()) {
                if(cursor.getNote().isSolved() && !cursor.getNote().isDeleted())
                {notes.add(cursor.getNote());}
                cursor.moveToNext();
            }
        } finally {
            cursor.close();
        }
        return notes;
    }

    public List<Note> getNotesDeleted() {
        List<Note> notes = new ArrayList<>();
        NoteCursorWrapper cursor = queryNotes(null, null);
        try {
            cursor.moveToFirst();
            while (!cursor.isAfterLast()) {
                if(cursor.getNote().isDeleted())
                {notes.add(cursor.getNote());}
                cursor.moveToNext();
            }
        } finally {
            cursor.close();
        }
        return notes;
    }

    
    public Note getNote(UUID id) {
        NoteCursorWrapper cursor = queryNotes(
        NoteTable.Cols.UUID + " = ?",
                new String[] { id.toString() }
        );

        try {
            if (cursor.getCount() == 0) {
                return null;
            }
            cursor.moveToFirst();
            return cursor.getNote();
        } finally {
            cursor.close();
        }
    }

    public void updateNote(Note note) {
        String uuidString = note.getId().toString();
        ContentValues values = getContentValues(note);

        mDatabase.update(NoteTable.NAME, values,
                NoteTable.Cols.UUID + " = ?",
                new String[] { uuidString });
    }

    private static ContentValues getContentValues(Note note) {
        ContentValues values = new ContentValues();
        values.put(NoteTable.Cols.UUID, note.getId().toString());
        values.put(NoteTable.Cols.TITLE, note.getTitle());
        values.put(NoteTable.Cols.DATE, note.getDate());
        values.put(NoteTable.Cols.SOLVED, note.isSolved() ? 1 : 0);
        values.put(NoteTable.Cols.IMAGE, note.getImage());
        values.put(NoteTable.Cols.COLOR, note.getColor());
        values.put(NoteTable.Cols.DELETED, note.isDeleted() ? 1 : 0);
        values.put(NoteTable.Cols.SUBJECT, note.getSubject());
        return values;
    }

    private NoteCursorWrapper queryNotes(String whereClause, String[] whereArgs) {
        Cursor cursor = mDatabase.query(
        NoteTable.NAME,
                null, // Columns - null selects all columns
                whereClause,
                whereArgs,
                null, // groupBy
                null, // having
                null  // orderBy
        );
        return new NoteCursorWrapper(cursor);
    }

    public File getPhotoFile(Note note) {
        File externalFilesDir = mContext.getExternalFilesDir(Environment.DIRECTORY_PICTURES);
        if (externalFilesDir == null) {
            return null;
        }
        return new File(externalFilesDir, note.getPhotoFilename());
    }

}
