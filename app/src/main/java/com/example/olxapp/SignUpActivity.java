package com.example.olxapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Switch;
import android.widget.Toast;

import com.example.olxapp.helper.FirebaseConfig;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;

public class SignUpActivity extends AppCompatActivity {

    private Button buttonAccess;
    private EditText emailField, passwordField;
    private Switch accessType;

    private FirebaseAuth auth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);

        setComponents();

        auth = FirebaseConfig.getFirebaseAuth();

        buttonAccess.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String email = emailField.getText().toString();
                String password = passwordField.getText().toString();

                if(accessType.isChecked()){

                auth.createUserWithEmailAndPassword(
                        email, password
                ).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if(task.isSuccessful()){

                            Toast.makeText(SignUpActivity.this, R.string.registration_completed, Toast.LENGTH_SHORT).show();

                        }else{
                            Toast.makeText(SignUpActivity.this, R.string.registration_error, Toast.LENGTH_SHORT).show();
                        }
                    }
                });

                }else{

                    auth.signInWithEmailAndPassword(
                            email, password
                    ).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            if(task.isSuccessful()){
                                Toast.makeText(SignUpActivity.this, R.string.logged_in_success, Toast.LENGTH_SHORT).show();
                                startActivity(new Intent(getApplicationContext(), ProductsActivity.class));

                            }else{
                                Toast.makeText(SignUpActivity.this, R.string.login_failed, Toast.LENGTH_SHORT).show();
                            }
                        }
                    });

                }

                if(!email.isEmpty() && !password.isEmpty()){

                }else{

                    Toast.makeText(SignUpActivity.this, R.string.fill_fields, Toast.LENGTH_SHORT).show();

                }
            }
        });
    }

    private void setComponents(){
        emailField = findViewById(R.id.editCadastroEmail);
        passwordField = findViewById(R.id.editCadastroSenha);
        buttonAccess = findViewById(R.id.buttonAcesso);
        accessType = findViewById(R.id.switchAcesso);
    }
}