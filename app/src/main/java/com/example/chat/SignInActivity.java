package com.example.chat;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class SignInActivity extends AppCompatActivity {
    private static final String TAG = "SignInActivity";

    private FirebaseAuth auth;
    private EditText emailEditTExt;
    private EditText passwordEditText;
    private EditText nameEditText;
    private TextView togleLoginSignUpTextView;
    private Button loginSingInButton;
    private boolean loginModeActive;
    private EditText repeatPassowrdEditText;
    FirebaseDatabase database;
    DatabaseReference usersDatabaseReference;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_in);
        auth = FirebaseAuth.getInstance();

        database = FirebaseDatabase.getInstance();
        usersDatabaseReference = database.getReference().child("users");
        repeatPassowrdEditText = findViewById(R.id.repeatPasswordEditText);
        emailEditTExt = findViewById(R.id.emailEditText);
        passwordEditText = findViewById(R.id.passwordEditText);
        nameEditText = findViewById(R.id.nameEditText);
        togleLoginSignUpTextView = findViewById(R.id.togleLoginSignUpTextView);
        loginSingInButton = findViewById(R.id.loginSignUpButton);
        loginSingInButton.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                loginSignUpUser(emailEditTExt.getText().toString().trim(), passwordEditText.getText().toString().trim());
            }
        });


        if (auth.getCurrentUser() != null) {
            startActivity(new Intent(SignInActivity.this, UserListActivity.class));
        }


    }

    private void loginSignUpUser(String email, String password) {

        if (loginModeActive) {
            if (passwordEditText.getText().toString().trim().length() < 7) {
                Toast.makeText(this, "Password mast be at least 7 characters", Toast.LENGTH_LONG).show();
            } else if (emailEditTExt.getText().toString().trim().equals("")) {
                Toast.makeText(this, "Please enter your Email", Toast.LENGTH_LONG).show();
            } else {
                auth.signInWithEmailAndPassword(email, password)
                        .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                            @Override
                            public void onComplete(@NonNull Task<AuthResult> task) {
                                if (task.isSuccessful()) {
                                    // Sign in success, update UI with the signed-in user's information
                                    Log.d(TAG, "signInWithEmail:success");
                                    FirebaseUser user = auth.getCurrentUser();
                                    Intent intent = new Intent(SignInActivity.this, UserListActivity.class);
                                    intent.putExtra("userName",nameEditText.getText().toString().trim() );
                                    startActivity(intent);

                                    // updateUI(user);
                                } else {
                                    // If sign in fails, display a message to the user.
                                    Log.w(TAG, "signInWithEmail:failure", task.getException());
                                    Toast.makeText(SignInActivity.this, "Authentication failed.",
                                            Toast.LENGTH_SHORT).show();
                                    // updateUI(null);
                                    // ...
                                }

                                // ...
                            }
                        });
            }


        } else {

            if (!passwordEditText.getText().toString().trim().equals(repeatPassowrdEditText.getText().toString().trim())) {
                Toast.makeText(this, "Password dont match", Toast.LENGTH_LONG).show();
            } else if (passwordEditText.getText().toString().trim().length() < 7) {
                Toast.makeText(this, "Password mast be at least 7 characters", Toast.LENGTH_LONG).show();
            } else if (emailEditTExt.getText().toString().trim().equals("")) {
                Toast.makeText(this, "Please enter your Email", Toast.LENGTH_LONG).show();
            } else {

                auth.createUserWithEmailAndPassword(email, password)
                        .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                            @Override
                            public void onComplete(@NonNull Task<AuthResult> task) {
                                if (task.isSuccessful()) {
                                    // Sign in success, update UI with the signed-in user's information
                                    Log.d(TAG, "createUserWithEmail:success");
                                    FirebaseUser user = auth.getCurrentUser();
                                    createUser(user);
                                    Intent intent = new Intent(SignInActivity.this, UserListActivity.class);
                                    intent.putExtra("userName",nameEditText.getText().toString().trim() );
                                    startActivity(intent);
                                    // updateUI(user);
                                } else {
                                    // If sign in fails, display a message to the user.
                                    Log.w(TAG, "createUserWithEmail:failure", task.getException());
                                    Toast.makeText(SignInActivity.this, "Authentication failed.",
                                            Toast.LENGTH_SHORT).show();
                                    // updateUI(null);
                                }

                            }
                        });

            }


        }


    }

    private void createUser(FirebaseUser firebaseUser) {

        User user = new User();
        user.setId(firebaseUser.getUid());
        user.setEmail(firebaseUser.getEmail());
        user.setName(nameEditText.getText().toString().trim());
        usersDatabaseReference.push().setValue(user);
    }

    public void toggleLoginMode(View view) {
        if (loginModeActive) {
            loginModeActive = false;
            loginSingInButton.setText("Sign up");
            togleLoginSignUpTextView.setText("Or, log In");
            repeatPassowrdEditText.setVisibility(View.VISIBLE);
        } else {
            loginModeActive = true;
            loginSingInButton.setText("Log in");
            togleLoginSignUpTextView.setText("Or, Sign up");
            repeatPassowrdEditText.setVisibility(View.GONE);
        }
    }
}
