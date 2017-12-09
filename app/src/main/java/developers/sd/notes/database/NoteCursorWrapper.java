package developers.sd.notes.database;

import android.database.Cursor;
import android.database.CursorWrapper;

import java.util.UUID;

import developers.sd.notes.Note;
import developers.sd.notes.database.NoteDbSchema.NoteTable;

public class NoteCursorWrapper extends CursorWrapper{
    public NoteCursorWrapper(Cursor cursor) {
        super(cursor);
    }

    public Note getNote() {
        String uuidString = getString(getColumnIndex(NoteTable.Cols.UUID));
        String title = getString(getColumnIndex(NoteTable.Cols.TITLE));
        String date = getString(getColumnIndex(NoteTable.Cols.DATE));
        int isSolved = getInt(getColumnIndex(NoteTable.Cols.SOLVED));
        byte[] image = getBlob(getColumnIndex(NoteTable.Cols.IMAGE));
        String color = getString(getColumnIndex(NoteTable.Cols.COLOR));
        int isDeleted = getInt(getColumnIndex(NoteTable.Cols.DELETED));
        String subject = getString(getColumnIndex(NoteTable.Cols.SUBJECT));

        Note note = new Note(UUID.fromString(uuidString));
        note.setTitle(title);
        note.setDate(date);
        note.setSolved(isSolved != 0);
        note.setImage(image);
        note.setColor(color);
        note.setDeleted(isDeleted != 0);
        note.setSubject(subject);
        return note;
    }
}
