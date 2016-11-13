package eu.arrowhead.managementtool.fragments;


import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;

import eu.arrowhead.managementtool.R;

//This fragment is used to display the error messages that came from the server side.
public class ServerErrorDialog extends DialogFragment {

    public static final String TAG = "ServerErrorFragment";

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        LayoutInflater inflater = getActivity().getLayoutInflater();
        View title = inflater.inflate(R.layout.dialog_server_error_title, null);
        View content = inflater.inflate(R.layout.dialog_server_error, null);

        Bundle args = getArguments();
        Integer statusCode = args.getInt("status_code");
        String errorMessage = args.getString("error_message");

        TextView statusCodeTv = (TextView) content.findViewById(R.id.status_code);
        statusCodeTv.setText(String.valueOf(statusCode));
        TextView errorMessageTv = (TextView) content.findViewById(R.id.error_message);
        errorMessageTv.setText(errorMessage);

        builder.setCustomTitle(title);
        builder.setView(content)
                .setPositiveButton(R.string.dismiss, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int id) {
                        dialog.dismiss();
                    }
                });

        return builder.create();
    }

}
