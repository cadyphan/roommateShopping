//package edu.uga.cs.roommateshopping;
//
//import android.content.Context;
//import android.os.Bundle;
//import android.view.LayoutInflater;
//import android.view.View;
//import android.widget.EditText;
//
//import androidx.annotation.NonNull;
//import androidx.appcompat.app.AlertDialog;
//import androidx.fragment.app.DialogFragment;
//
//public class SigninDialogFragment extends DialogFragment {
//    private EditText emailView;
//    private EditText passwordView;
//
//    public interface SignInDialogListener {
//        void signIn(String email, String password);
//    }
//
//    @NonNull
//    @Override
//    public android.app.Dialog onCreateDialog(Bundle savedInstanceState) {
//        LayoutInflater inflater = LayoutInflater.from(requireActivity());
//        final View layout = inflater.inflate(R.layout.signin, null);
//
//        emailView = layout.findViewById(R.id.userEmail);
//        passwordView = layout.findViewById(R.id.userPassword);
//
//        AlertDialog.Builder builder = new AlertDialog.Builder(requireActivity());
//        builder.setView(layout);
//        builder.setTitle("Sign In");
//        builder.setNegativeButton(android.R.string.cancel, (dialog, whichButton) -> dialog.dismiss());
//        builder.setPositiveButton(android.R.string.ok, (dialog, which) -> {
//            String email = emailView.getText().toString();
//            String password = passwordView.getText().toString();
//
//            SignInDialogListener listener = (SignInDialogListener) requireActivity();
//            if (listener != null) {
//                listener.signIn(email, password);
//            }
//            dismiss();
//        });
//
//        return builder.create();
//    }
//}

package edu.uga.cs.roommateshopping;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.DialogFragment;

public class SigninDialogFragment extends DialogFragment {
    private EditText emailView;
    private EditText passwordView;

    public interface SignInDialogListener {
        void signIn(String email, String password);
    }

    @NonNull
    @Override
    public android.app.Dialog onCreateDialog(Bundle savedInstanceState) {
        LayoutInflater inflater = LayoutInflater.from(requireActivity());
        final View layout = inflater.inflate(R.layout.signin, null);

        emailView = layout.findViewById(R.id.userEmail);
        passwordView = layout.findViewById(R.id.userPassword);

        AlertDialog dialog = new AlertDialog.Builder(requireActivity())
                .setTitle("Sign In")
                .setView(layout)
                .setNegativeButton(android.R.string.cancel, (dialogInterface, whichButton) -> dialogInterface.dismiss())
                .setPositiveButton(android.R.string.ok, (dialogInterface, which) -> {
                    String email = emailView.getText().toString();
                    String password = passwordView.getText().toString();

                    SignInDialogListener listener = (SignInDialogListener) getActivity();
                    if (listener != null) {
                        listener.signIn(email, password);
                    }
                })
                .create();

        dialog.getWindow().setSoftInputMode(android.view.WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_VISIBLE);
        return dialog;
    }
}



