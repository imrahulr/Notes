package developers.sd.notes;

import android.app.Activity;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.AlertDialog;

import java.util.UUID;

public class DeleteDialog extends DialogFragment{

    private static final String ARG_DELETE_NOTE = "delete";
    public static final String EXTRA_DELETE =
            "com.sd.android.note.delete";


    public DeleteDialog newInstance(UUID Id) {
        Bundle args = new Bundle();
        args.putSerializable(ARG_DELETE_NOTE, Id);
        DeleteDialog fragment = new DeleteDialog();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {

        final UUID Id = (UUID) getArguments().getSerializable(ARG_DELETE_NOTE);


        return new AlertDialog.Builder(getActivity())
                .setTitle(R.string.delete_question)
                .setPositiveButton(android.R.string.ok,
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                Note c = new Note(Id);
                                    sendResult(Activity.RESULT_OK, false);
                                    getActivity().finish();
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
    }

    private void sendResult(int resultCode, Boolean deleted) {
        if (getTargetFragment() == null) {
            return; }
        Intent intent = new Intent();
        intent.putExtra(EXTRA_DELETE, deleted);
        getTargetFragment()
                .onActivityResult(getTargetRequestCode(), resultCode, intent);
    }

}
