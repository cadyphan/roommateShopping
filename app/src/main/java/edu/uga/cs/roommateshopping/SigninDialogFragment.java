package edu.uga.cs.roommateshopping;

import static android.app.PendingIntent.getActivity;

import android.app.Dialog;
import android.app.DialogFragment;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;

public class SigninDialogFragment extends DialogFragment {
    private EditText emailView;
    private EditText passwordView;

    public interface SignInDialogListener {
        void signIn( String email, String password );
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        LayoutInflater inflater = (LayoutInflater) getActivity().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        final View layout = inflater.inflate(R.layout.signin, getActivity().findViewById(R.id.root));

        emailView = layout.findViewById( R.id.userEmail );
        passwordView = layout.findViewById( R.id.userPassword );


        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setView(layout);

        builder.setTitle( "Sign In" );
        builder.setNegativeButton( android.R.string.cancel, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int whichButton) {
                dialog.dismiss();
            }
        });
        builder.setPositiveButton( android.R.string.ok, new SignInListener() );

        return builder.create();
    }

    private class SignInListener implements DialogInterface.OnClickListener {
        @Override
        public void onClick(DialogInterface dialog, int which) {
            String email = emailView.getText().toString();
            String password = passwordView.getText().toString();

            SigninDialogFragment.SignInDialogListener listener = (SigninDialogFragment.SignInDialogListener) getActivity();

            listener.signIn( email, password );

            dismiss();
        }
    }

}
