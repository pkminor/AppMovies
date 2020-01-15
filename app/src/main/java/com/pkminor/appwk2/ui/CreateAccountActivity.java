package com.pkminor.appwk2.ui;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
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
import com.google.firebase.auth.UserProfileChangeRequest;
import com.pkminor.appwk2.R;

import butterknife.BindView;
import butterknife.ButterKnife;

public class CreateAccountActivity extends AppCompatActivity implements  View.OnClickListener {

    @BindView(R.id.createUserButton)
    Button mCreateUserButton;
    @BindView(R.id.nameEditText)
    EditText mNameEditText;
    @BindView(R.id.emailEditText) EditText mEmailEditText;
    @BindView(R.id.passwordEditText) EditText mPasswordEditText;
    @BindView(R.id.confirmPasswordEditText) EditText mConfirmPasswordEditText;
    @BindView(R.id.loginTextView)
    TextView mLoginTextView;

    private FirebaseAuth mAuth;
    private FirebaseAuth.AuthStateListener mAuthStateListener;

    private ProgressDialog mAuthprogressDialog;

    private String mName;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_account);
        ButterKnife.bind(this);

        mCreateUserButton.setOnClickListener(this);
        mLoginTextView.setOnClickListener(this);

        mAuth =  FirebaseAuth.getInstance();
        createAuthStateListener();
        createProgressDialog();
    }

    @Override
    protected void onStart() {
        super.onStart();
        mAuth.addAuthStateListener(mAuthStateListener);
    }

    @Override
    protected void onStop() {
        super.onStop();
        if(mAuthStateListener!=null)
            mAuth.removeAuthStateListener(mAuthStateListener);
    }

    @Override
    public void onClick(View v) {

        switch (v.getId()){

            case R.id.createUserButton:
                createNewUser();
                break;

            case R.id.loginTextView:
                Intent intent = new Intent(CreateAccountActivity.this,LoginActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(intent);
                finish();

                break;

            default:
                break;
        }
    }

    private void createNewUser(){
        final String name = mNameEditText.getText().toString().trim();
        final String email = mEmailEditText.getText().toString().trim();
        String password = mPasswordEditText.getText().toString().trim();
        String confirmPassword = mConfirmPasswordEditText.getText().toString().trim();

        Boolean inputOK = (isValidEmail(email) && isValidName(name) && isValidPassword(password,confirmPassword));

        if(!inputOK) return;

        mName=name;

        mAuthprogressDialog.show();
        mAuth.createUserWithEmailAndPassword(email,password)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {

                        mAuthprogressDialog.dismiss();
                        if (task.isSuccessful()){
                            Log.d("Auth: ","Authentication is successful");
                            createFirebaseUserProfile(task.getResult().getUser());
                        }else{
                            Toast.makeText(
                                    CreateAccountActivity.this,
                                    "Authenication Failed : "+task.getException().getMessage(),
                                    Toast.LENGTH_SHORT)
                                    .show();
                        }

                    }
                });


    }

    private void createAuthStateListener(){

        mAuthStateListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                final FirebaseUser user = firebaseAuth.getCurrentUser();

                if(user!=null){
                    Intent intent = new Intent(CreateAccountActivity.this,LoginActivity.class);
                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                    startActivity(intent);
                    finish();
                }
            }
        };
    }

    public void createFirebaseUserProfile(final FirebaseUser user){

        UserProfileChangeRequest addProfielName = new UserProfileChangeRequest.Builder()
                .setDisplayName(mName)
                .build();

        user.updateProfile(addProfielName)
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if(task.isSuccessful()){
                            Log.i("Update: ",user.getDisplayName());
                        }
                    }
                });
    }

    private void createProgressDialog(){
        mAuthprogressDialog =  new ProgressDialog(this);
        mAuthprogressDialog.setTitle("Loading...");
        mAuthprogressDialog.setMessage("Authenticating with firebase...");
        mAuthprogressDialog.setCancelable(true);
    }

    private boolean isValidEmail(String email) {
        boolean isGoodEmail =
                (email != null && android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches());
        if (!isGoodEmail) {
            mEmailEditText.setError("Please enter a valid email address");
            return false;
        }
        return isGoodEmail;
    }

    private boolean isValidName(String name) {
        if (name.equals("")) {
            mNameEditText.setError("Please enter your name");
            return false;
        }
        return true;
    }

    private boolean isValidPassword(String password, String confirmPassword) {
        if (password.length() < 6) {
            mPasswordEditText.setError("Please create a password containing at least 6 characters");
            return false;
        } else if (!password.equals(confirmPassword)) {
            mPasswordEditText.setError("Passwords do not match");
            return false;
        }
        return true;
    }
}
