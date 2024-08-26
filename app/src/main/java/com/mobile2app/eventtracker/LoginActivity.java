package com.mobile2app.eventtracker;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.SignInButton;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.FirebaseApp;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.GoogleAuthProvider;

/**
 * Handles the Login and Sign Up process for users. Validates credentials
 * and stores the salt/hashed password of an account
 *
 * @author Michael Gagujas
 * @since 2024-08-18
 */
public class LoginActivity extends AppCompatActivity {

    private EditText emailInput;
    private EditText passwordInput;

    private FirebaseAuth auth;

    GoogleSignInClient googleSignInClient;

    /**
     * Registers an activity result launcher to handle the result of an activity.
     * This launcher is used to start an activity for a result and handle the result
     * when the activity finishes.
     */
    private final ActivityResultLauncher<Intent> activityResultLauncher = registerForActivityResult(new ActivityResultContracts.StartActivityForResult(), new ActivityResultCallback<ActivityResult>() {
        /**
         * Called when the launched activity returns a result.
         *
         * @param result The result returned by the launched activity.
         */
        @Override
        public void onActivityResult(ActivityResult result) {
            if (result.getResultCode() == RESULT_OK) {
                Task<GoogleSignInAccount> accountTask = GoogleSignIn.getSignedInAccountFromIntent(result.getData());
                try {
                    /**
                     * Retrieves the GoogleSignInAccount from the intent data.
                     *
                     * @return The GoogleSignInAccount object containing the sign-in information.
                     * @throws ApiException If the sign-in process fails.
                     */
                    GoogleSignInAccount signInAccount = accountTask.getResult(ApiException.class);

                    /**
                     * Authenticates a user using Google Sign-In credentials and handles the sign-in result.
                     *
                     * @param signInAccount The GoogleSignInAccount object containing the user's sign-in information.
                     */
                    AuthCredential authCredential = GoogleAuthProvider.getCredential(signInAccount.getIdToken(), null);
                    auth.signInWithCredential(authCredential).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                        /**
                         * Called when the sign-in task is complete.
                         *
                         * @param task The task representing the sign-in operation.
                         */
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            if(task.isSuccessful()) {
                                Toast.makeText(LoginActivity.this, "Signed in successfully", Toast.LENGTH_SHORT).show();
                                startActivity(new Intent(LoginActivity.this , MainActivity.class));
                                finish();
                            } else {
                                Toast.makeText(LoginActivity.this, "Failed to sign in: " + task.getException(), Toast.LENGTH_SHORT).show();
                            }
                        }
                    });
                } catch (ApiException e) {
                    e.printStackTrace();
                }
            }
        }
    });

    /**
     * Called when the activity is first created. This method is where you should do all of your normal static set up:
     * create views, bind data to lists, etc. This method also provides you with a Bundle containing the activity's previously
     * frozen state, if there was one.
     *
     * @param savedInstanceState If the activity is being re-initialized after previously being shut down then this Bundle contains the data it most recently supplied in onSaveInstanceState(Bundle). Note: Otherwise it is null.
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_login);

        FirebaseApp.initializeApp(this);

        GoogleSignInOptions options = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.client_id))
                .requestEmail()
                .build();
        googleSignInClient = GoogleSignIn.getClient(LoginActivity.this, options);

        auth = FirebaseAuth.getInstance();

        SignInButton signInButton = findViewById(R.id.buttonGoogleLogin);
        signInButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = googleSignInClient.getSignInIntent();
                activityResultLauncher.launch(intent);
            }
        });

        // Adjust layout based on system insets
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.login_screen), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        // Override auto-adjustment so status bar remains readable
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
        getWindow().clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        getWindow().setStatusBarColor(ContextCompat.getColor(this, R.color.white));


        emailInput = findViewById(R.id.username);
        passwordInput = findViewById(R.id.password);
        Button loginButton = findViewById(R.id.buttonLogin);
        Button signUpButton = findViewById(R.id.buttonSignUp);

        auth = FirebaseAuth.getInstance();

        // Verifies login credentials from user input
        loginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String txt_email = emailInput.getText().toString();
                String txt_password = passwordInput.getText().toString();

                if (txt_email.isEmpty()) {
                    emailInput.setError("Please enter your email");
                    emailInput.requestFocus();
                    return;
                }

                if (txt_password.isEmpty()) {
                    passwordInput.setError("Please enter your password");
                    passwordInput.requestFocus();
                    return;
                }
                loginUser(txt_email, txt_password);
            }
        });

        signUpButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(LoginActivity.this , RegisterActivity.class));
                finish();
            }
        });
    }

    /**
     * Logs in a user using the provided email and password.
     *
     * @param email    The email address of the user.
     * @param password The password of the user.
     */
    private void loginUser(String email, String password) {
        auth.signInWithEmailAndPassword(email, password)
                .addOnSuccessListener(new OnSuccessListener<AuthResult>() {
                    @Override
                    public void onSuccess(AuthResult authResult) {
                        Toast.makeText(LoginActivity.this, "Login Successful!", Toast.LENGTH_SHORT).show();
                        startActivity(new Intent(LoginActivity.this, MainActivity.class));
                        finish();
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(LoginActivity.this, "Invalid Credentials!", Toast.LENGTH_SHORT).show();
                    }
                });
    }

    /**
     * Hides the device's soft keyboard.
     *
     * @param view The view from which the window token will be retrieved to hide the keyboard.
     */
    private void hideKeyboard(View view) {
        InputMethodManager inputManager = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        if (inputManager != null) {
            // Hide the soft keyboard using the window token from the view
            inputManager.hideSoftInputFromWindow(view.getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
        }
    }


}