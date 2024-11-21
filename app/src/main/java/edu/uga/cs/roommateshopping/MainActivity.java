package edu.uga.cs.roommateshopping;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.DialogFragment;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;

public class MainActivity extends AppCompatActivity
    implements SigninDialogFragment.SignInDialogListener {
    public static final String DEBUG_TAG = "MainActivity";

    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);

        Button signInButton = findViewById( R.id.SigninButton);
        Button registerButton = findViewById( R.id.Registerbutton);

        signInButton.setOnClickListener( new SignInButtonClickListener() );
        registerButton.setOnClickListener( new RegisterButtonClickListener() );
    }
    public void signIn( String email, String password )
    {
        mAuth = FirebaseAuth.getInstance();

        mAuth.signInWithEmailAndPassword( email, password )
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // Sign in success, update UI with the signed-in user's information
                            Log.d( DEBUG_TAG, "signInWithEmail:success" );
                            //FirebaseUser user = mAuth.getCurrentUser();
                            // after a successful sign in, start the job leads management activity
                            Intent intent = new Intent( MainActivity.this, ShoppingListActivity.class );
                            startActivity( intent );
                        }
                        else {
                            // If sign in fails, display a message to the user.
                            Log.d( DEBUG_TAG, "signInWithEmail:failure", task.getException() );
                            Toast.makeText( MainActivity.this, "Authentication failed.",
                                    Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }
    private class RegisterButtonClickListener implements View.OnClickListener {
        @Override
        public void onClick(View view) {
            // start the user registration activity
            Intent intent = new Intent(view.getContext(), RegisterActivity.class);
            view.getContext().startActivity(intent);
        }
    }
    private class SignInButtonClickListener implements View.OnClickListener {
        @Override
        public void onClick( View v ) {
            DialogFragment newFragment = new SigninDialogFragment();
            newFragment.show( getSupportFragmentManager(), null );
        }
    }
}