package com.adham.firebaseproj;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.facebook.AccessToken;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.login.LoginManager;
import com.facebook.login.LoginResult;
import com.facebook.login.widget.LoginButton;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.facebook.FacebookSdk;
import com.facebook.appevents.AppEventsLogger;
import com.google.firebase.database.ValueEventListener;

import java.util.Arrays;

public class MainActivity extends AppCompatActivity {

    private static final String TAG = "tt";
    private FirebaseAuth mAuth;
    FirebaseUser currentUser;
    TextView tvDBName;
    EditText etname, etemail, etpassword, etmobile;
    String name, email, password, mobile;
    CallbackManager callbackManager;
    LoginButton loginButton;
    boolean loggedIn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        mAuth = FirebaseAuth.getInstance();
        etname = findViewById(R.id.etName);
        etemail = findViewById(R.id.etEmail);
        etpassword = findViewById(R.id.etPass);
        etmobile = findViewById(R.id.etMobile);
tvDBName = findViewById(R.id.tvDBName);

    //FB code to integrate authentication
    loginButton = findViewById(R.id.login_button);
        FacebookSdk.sdkInitialize(getApplicationContext());
        AppEventsLogger.activateApp(this);
    callbackManager = CallbackManager.Factory.create();
        loginButton.setReadPermissions("email");
    // Callback registration
        loginButton.registerCallback(callbackManager, new FacebookCallback<LoginResult>() {
        @Override
        public void onSuccess(LoginResult loginResult) {
            // App code
        }
        @Override
        public void onCancel() {
            // App code
        }
        @Override
        public void onError(FacebookException exception) {
            // App code
        }
    });

        LoginManager.getInstance().registerCallback(callbackManager,
                new FacebookCallback<LoginResult>() {
        @Override
        public void onSuccess(LoginResult loginResult) {
            // App code
        }
        @Override
        public void onCancel() {
            // App code
        }
        @Override
        public void onError(FacebookException exception) {
            // App code
        }
    });
    loggedIn = AccessToken.getCurrentAccessToken() == null;
}
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        callbackManager.onActivityResult(requestCode, resultCode, data);
        super.onActivityResult(requestCode, resultCode, data);
    }

    @Override
    public void onStart() {
        super.onStart();
        // Check if user is signed in (non-null) and update UI accordingly.
        currentUser = mAuth.getCurrentUser();
        updateUI(currentUser);
    }

    private void updateUI(FirebaseUser currentUser) {
    }

    public void RegUser(View view) {
        name = etname.getText().toString();
        email = etemail.getText().toString();
        password = etpassword.getText().toString();
        mobile = etmobile.getText().toString();
        mAuth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // Sign in success, update UI with the signed-in user's information
                            Log.d(TAG, "createUserWithEmail:success");
                            FirebaseUser user = mAuth.getCurrentUser();
                            updateUI(user);

                            user.sendEmailVerification();
                            // Write a message to the database
                            FirebaseDatabase database = FirebaseDatabase.getInstance();
                            DatabaseReference myRef = database.getReference("Users");
                            myRef.child(user.getUid()).child("Name").setValue(name);
                            myRef.child(user.getUid()).child("Mobile").setValue(mobile);
                            myRef.child(user.getUid()).addValueEventListener(new ValueEventListener() {
                                @Override
                                public void onDataChange(DataSnapshot dataSnapshot) {
                                    // This method is called once with the initial value and again
                                    // whenever data at this location is updated.

                                 //   mobile = dataSnapshot.child("Mobile").getValue().toString();
                                    name = dataSnapshot.child("Name").getValue().toString();
                                   // tvMobile.setText("Mobile :  " + mobile);
                                    tvDBName.setText("Hi  " + name);
                                   // tvEmail.setText("Email  :" + email);
                                    Log.d(TAG, "Value is: " + mobile);
                                }

                                @Override
                                public void onCancelled(DatabaseError error) {
                                    // Failed to read value
                                    Log.w(TAG, "Failed to read value.", error.toException());
                                }
                            });
                        } else {
                            // If sign in fails, display a message to the user.
                            Log.w(TAG, "createUserWithEmail:failure", task.getException());
                            Toast.makeText(MainActivity.this, "Authentication failed.",
                                    Toast.LENGTH_SHORT).show();
                            updateUI(null);
                        }
                        // ...
                    }
                });
    }

    public void LogUser(View view) {
        email = etemail.getText().toString();
        password = etpassword.getText().toString();
        mAuth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // Sign in success, update UI with the signed-in user's information
                            Log.d(TAG, "signInWithEmail:success");
                            FirebaseUser user = mAuth.getCurrentUser();
                            updateUI(user);
                            if (user.isEmailVerified()) {
                                startActivity(new Intent(MainActivity.this, HomeActivity.class));
                            } else {
//                                startActivity(new Intent(MainActivity.this, HomeActivity.class));
                                Toast.makeText(MainActivity.this, "Please Verify your Email", Toast.LENGTH_SHORT).show();
                            }
                        } else {
                            // If sign in fails, display a message to the user.
                            Log.w(TAG, "signInWithEmail:failure", task.getException());
                            Toast.makeText(MainActivity.this, "Authentication failed.",
                                    Toast.LENGTH_SHORT).show();
                            updateUI(null);
                        }
                        // ...
                    }
                });
    }

    public void restPass(View view) {
        email = etemail.getText().toString();
        mAuth.sendPasswordResetEmail(email);
        Toast.makeText(this, "Reset Password email sent , please check your email", Toast.LENGTH_SHORT).show();
    }

    public void LogFB(View view) {
        LoginManager.getInstance().logInWithReadPermissions(this, Arrays.asList("public_profile"));
    }
}
