package com.fypj.icreative.activity;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.support.annotation.Nullable;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Toast;

import com.fypj.icreative.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

public class BaseActivity extends AppCompatActivity {
    private ProgressDialog mProgressDialog;

    public ProgressDialog showProgressDialog(String message) {
        if (mProgressDialog == null) {
            mProgressDialog = new ProgressDialog(this);
            mProgressDialog.setMessage(message);
            mProgressDialog.setIndeterminate(true);
            mProgressDialog.setCanceledOnTouchOutside(false);
        }
        else{
            mProgressDialog.setMessage(message);
        }
       return mProgressDialog;
    }

    public void setProgressDialogMessage(String message){
        mProgressDialog.setMessage(message);
    }

    public void hideProgressDialog() {
        if (mProgressDialog != null && mProgressDialog.isShowing()) {
            mProgressDialog.hide();
        }
    }

    public String checkUserAuth() {
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if (user == null) {
            Toast.makeText(getApplicationContext(), "Session Time out", Toast.LENGTH_SHORT).show();
            Intent i = new Intent(getApplicationContext(), LoginActivity.class);
            startActivity(i);
            return null;
        } else {
            return user.getUid();
        }
    }

    public AlertDialog.Builder quitFillFormAlertDialog() {
        return new AlertDialog.Builder(this)
                .setTitle("Exit this page")
                .setMessage("Are you sure you want to exit?")
                .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        finish();
                    }
                })
                .setNegativeButton(android.R.string.no, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.cancel();
                    }
                });
    }
}
