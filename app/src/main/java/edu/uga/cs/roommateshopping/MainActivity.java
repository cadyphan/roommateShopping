package edu.uga.cs.roommateshopping;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.DialogFragment;

public class MainActivity extends AppCompatActivity
        implements SigninDialogFragment.SignInDialogListener {

    public static final String DEBUG_TAG = "MainActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);

        Button signInButton = findViewById(R.id.SigninButton);
        Button registerButton = findViewById(R.id.Registerbutton);

        signInButton.setOnClickListener(new SignInButtonClickListener());
        registerButton.setOnClickListener(new RegisterButtonClickListener());
    }

    @Override
    public void signIn(String email, String password) {
        Log.d(DEBUG_TAG, "Mock sign-in with email: " + email);
        Toast.makeText(this, "Mock sign-in successful!", Toast.LENGTH_SHORT).show();

        // Directly navigate to ShoppingListActivity
        Intent intent = new Intent(MainActivity.this, ShoppingListActivity.class);
        startActivity(intent);
        finish();
    }

    private class RegisterButtonClickListener implements View.OnClickListener {
        @Override
        public void onClick(View view) {
            // Navigate to RegisterActivity
            Intent intent = new Intent(view.getContext(), RegisterActivity.class);
            view.getContext().startActivity(intent);
        }
    }

    private class SignInButtonClickListener implements View.OnClickListener {
        @Override
        public void onClick(View v) {
            // Show the mock SigninDialogFragment
            DialogFragment newFragment = new SigninDialogFragment();
            newFragment.show(getSupportFragmentManager(), "signinDialog");
        }
    }
}
