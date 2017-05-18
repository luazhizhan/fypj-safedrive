package com.fypj.icreative.activity;

import android.annotation.TargetApi;
import android.app.Activity;
import android.app.LoaderManager.LoaderCallbacks;
import android.content.Intent;
import android.content.Loader;
import android.database.Cursor;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.Snackbar;
import android.support.v4.content.ContextCompat;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.fypj.icreative.R;
import com.fypj.icreative.model.UserModel;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class LoginActivity extends BaseActivity {
    private UserModel userModel = new UserModel();
    private EditText loginEmailEditTxt;
    private EditText loginPasswordEditTxt;
    private Button loginEmailSignInBtn;
    private FirebaseAuth mAuth;
    private FirebaseAuth.AuthStateListener mAuthListener;
    private CoordinatorLayout coordinatorLayout;
    private Button loginRegisterBtn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        coordinatorLayout = (CoordinatorLayout) findViewById(R.id.activity_coordinatorLayout);
        mAuth = FirebaseAuth.getInstance();
        mAuthListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                FirebaseUser user = firebaseAuth.getCurrentUser();
                authStateChanged(user);
            }
        };
        loginEmailEditTxt = (EditText) findViewById(R.id.loginEmailEditTxt);
        loginPasswordEditTxt = (EditText) findViewById(R.id.loginPasswordEditTxt);
        loginEmailSignInBtn = (Button) findViewById(R.id.loginEmailSignInBtn);
        loginRegisterBtn = (Button) findViewById(R.id.loginRegisterBtn);
        setEmailLoginBtnOnClick();
    }

    @Override
    protected void onStart() {
        super.onStart();
        mAuth.addAuthStateListener(mAuthListener);
    }

    @Override
    public void onStop() {
        super.onStop();
        if (mAuthListener != null) {
            mAuth.removeAuthStateListener(mAuthListener);
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    public void authStateChanged(FirebaseUser user) {
        if (user != null) {
            String wefw = user.getUid();
            Intent i = new Intent(LoginActivity.this, MainActivity.class);
            startActivity(i);
            finish();
        }
    }

    private void setEmailLoginBtnOnClick() {
        loginEmailSignInBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                signIn();
            }
        });
    }

    private boolean validateForm() {
        boolean valid = true;

        String email = loginEmailEditTxt.getText().toString();
        if (TextUtils.isEmpty(email)) {
            loginEmailEditTxt.setError(getString(R.string.error_field_required));
            valid = false;
        } else {
            loginEmailEditTxt.setError(null);
        }

        String password = loginPasswordEditTxt.getText().toString();
        if (TextUtils.isEmpty(password)) {
            loginPasswordEditTxt.setError(getString(R.string.error_field_required));
            valid = false;
        } else {
            loginPasswordEditTxt.setError(null);
        }

        return valid;
    }

    @TargetApi(Build.VERSION_CODES.JELLY_BEAN)
    public void onSignInWithEmailAndPassword(Task<AuthResult> task) {
        if (!task.isSuccessful()) {
            Snackbar snackbar = Snackbar.make(coordinatorLayout, "Login Failed", Snackbar.LENGTH_SHORT);
            View snackBarView = snackbar.getView();
            snackBarView.setBackgroundColor(ContextCompat.getColor(getApplicationContext(), R.color.primary_dark));
            snackbar.show();
        } else {
            Intent i = new Intent(LoginActivity.this, MainActivity.class);
            startActivity(i);
            finish();
        }
    }

    private void signIn() {
        if (!validateForm()) {
            return;
        }
        showProgressDialog("Logging in...").show();
        userModel.setEmail(loginEmailEditTxt.getText().toString());
        userModel.setPassword(loginPasswordEditTxt.getText().toString());
        mAuth.signInWithEmailAndPassword(userModel.getEmail(), userModel.getPassword())
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        hideProgressDialog();
                        onSignInWithEmailAndPassword(task);
                    }
                });
    }

    private void setRegisterBtnOnClick() {
        Intent i = new Intent(LoginActivity.this, MainActivity.class);
        startActivity(i);
        finish();
    }
}

