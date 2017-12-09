package developers.sd.notes;

import android.content.Context;
import android.content.Intent;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.os.Bundle;

import java.util.UUID;

public class NoteActivity extends SingleFragmentActivity {

    private static final String EXTRA_NOTE_ID =
            "developers.sd.notes.note_id";

    public static Intent newIntent(Context packageContext, UUID noteId) {
        Intent intent = new Intent(packageContext, NoteActivity.class);
        intent.putExtra(EXTRA_NOTE_ID, noteId);
        return intent;
    }

    @Override
    protected Fragment createFragment() {
        UUID noteId = (UUID) getIntent()
                .getSerializableExtra(EXTRA_NOTE_ID);
        return NoteFragment.newInstance(noteId);
    }
}
