package com.mobile2app.eventtracker;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;

/**
 * An activity that provides a registration screen for users.
 * <p>
 * This activity allows users to register by entering their email and password.
 * </p>
 *
 * @author Michael Gagujas
 * @since 2024-08-18
 */
public class RegisterActivity extends AppCompatActivity {

    private EditText email;
    private EditText password;
    private Button register;

    private FirebaseAuth auth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_register);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        email = findViewById(R.id.email);
        password = findViewById(R.id.password);
        register = findViewById(R.id.register);

        auth = FirebaseAuth.getInstance();

        /**
         * Sets an OnClickListener for the register button.
         */
        register.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String txt_email = email.getText().toString();
                String txt_password = password.getText().toString();

                if (TextUtils.isEmpty(txt_email) || TextUtils.isEmpty(txt_password)) {
                    Toast.makeText(RegisterActivity.this, "Empty credentials", Toast.LENGTH_SHORT).show();
                } else if (txt_password.length() < 6) {
                    Toast.makeText(RegisterActivity.this, "Password is too short!", Toast.LENGTH_SHORT).show();
                } else {
                    registerUser(txt_email, txt_password);
                }
            }


            /**
             * Registers a new user with the provided email and password.
             * <p>
             * This method uses Firebase Authentication to create a new user account. If the registration
             * is successful, a Toast message is displayed indicating "Registering user successful" and the
             * user is redirected to the MainActivity. If the registration fails, a Toast message is displayed
             * indicating the failure.
             * </p>
             *
             * @param email The email address entered by the user.
             * @param password The password entered by the user.
             */
            private void registerUser(String email, String password) {
                auth.createUserWithEmailAndPassword(email, password).addOnCompleteListener(RegisterActivity.this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if(task.isSuccessful()){
                            Toast.makeText(RegisterActivity.this, "Registering user successful", Toast.LENGTH_SHORT).show();
                            startActivity(new Intent(RegisterActivity.this , MainActivity.class));
                            finish();
                        } else {
                            Toast.makeText(RegisterActivity.this, "Registration failed!", Toast.LENGTH_SHORT).show();
                        }
                    }
                 });
            }
        });
    }
}