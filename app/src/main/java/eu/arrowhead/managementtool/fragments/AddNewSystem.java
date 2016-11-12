package eu.arrowhead.managementtool.fragments;


import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;

import eu.arrowhead.managementtool.R;

public class AddNewSystem extends DialogFragment{

    public static final String TAG = "AddNewSystemFragment";

    public interface AddNewSystemListener {
        public void onSaveSystemButtonClicked(DialogFragment dialog);
    }

    AddNewSystem.AddNewSystemListener mListener;

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        LayoutInflater inflater = getActivity().getLayoutInflater();
        View title = inflater.inflate(R.layout.dialog_add_new_system_title, null);
        View content = inflater.inflate(R.layout.dialog_add_new_system, null);

        builder.setCustomTitle(title);
        builder.setView(content)
                .setPositiveButton(R.string.save, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int id) {
                        mListener.onSaveSystemButtonClicked(AddNewSystem.this);
                    }
                })
                .setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        dialog.cancel();
                    }
                });

        return builder.create();
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);

        Activity activity = getActivity();
        try {
            mListener = (AddNewSystem.AddNewSystemListener) activity;
        } catch (ClassCastException e) {
            throw new ClassCastException(activity.toString()
                    + " must implement AddNewSystemListener");
        }
    }
}
