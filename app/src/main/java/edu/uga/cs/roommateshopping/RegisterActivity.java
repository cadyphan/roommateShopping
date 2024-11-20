package edu.uga.cs.roommateshopping;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

public class RegisterActivity extends AppCompatActivity {
    private static final String DEBUG_TAG = "Register";

    private EditText emailEditText;
    private EditText passwordEditText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.register);

        emailEditText = findViewById( R.id.email );
        passwordEditText = findViewById( R.id.password );

        Button registerButton = findViewById(R.id.register);
        registerButton.setOnClickListener( new RegisterButtonClickListener() );
    }
    private class RegisterButtonClickListener implements View.OnClickListener {
        @Override
        public void onClick(View view) {
            final String email = emailEditText.getText().toString();
            final String password = passwordEditText.getText().toString();

            final FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();

            firebaseAuth.createUserWithEmailAndPassword(email, password)
                    .addOnCompleteListener(RegisterActivity.this, new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            if (task.isSuccessful()) {

                                Toast.makeText(getApplicationContext(),
                                        "Registered user: " + email,
                                        Toast.LENGTH_SHORT).show();

                                // Sign in success, update UI with the signed-in user's information
                                Log.d(DEBUG_TAG, "createUserWithEmail: success");

                                FirebaseUser user = firebaseAuth.getCurrentUser();

                                Intent intent = new Intent(RegisterActivity.this, JobLeadManagementActivity.class);
                                startActivity(intent);

                            } else {
                                Log.w(DEBUG_TAG, "createUserWithEmail: failure", task.getException());
                                Toast.makeText(RegisterActivity.this, "Registration failed.",
                                        Toast.LENGTH_SHORT).show();
                            }
                        }
                    });
        }
    }
}
