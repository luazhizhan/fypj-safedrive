package com.fypj.icreative.activity;


import android.content.Intent;
import android.graphics.Bitmap;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.TextInputLayout;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ScrollView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.fypj.icreative.R;
import com.fypj.icreative.controller.PrivateSettlementController;
import com.fypj.icreative.dal.PrivateSettlementDAL;
import com.fypj.icreative.dal.UserDAL;
import com.fypj.icreative.model.PrivateSettlementModel;
import com.fypj.icreative.model.TripModel;
import com.fypj.icreative.model.UserModel;
import com.fypj.icreative.utils.Utils;
import com.github.gcacace.signaturepad.views.SignaturePad;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.UploadTask;


public class PrivateSettlementActivity extends BaseActivity {
    private Spinner toolbarFilterSpinner;
    private ImageButton toolbarSearchBtn;
    private TextView toolbarTitle;
    private TripModel tripModel;
    private Utils util = new Utils();
    private TextView privateSettlePartyTxtView, privateSettleSignatureError;
    private EditText privateSettleFullNameEditTxt, privateSettleNRICNumEditTxt,
            privateSettleContactNumEditTxt, privateSettleMotorRegNoEditTxt, privateSettleInsuranceCompanyEditTxt, privateSettleCompensationAmtEditTxt;
    private TextInputLayout privateSettleFullNameTxtInput, privateSettleNRICNumTxtInput, privateSettleContactNumTxtInput, privateSettleMotorRegNoTxtInput,
            privateSettleInsuranceCompanyTxtInput, privateSettleCompensationAmtTxtInput;
    private SignaturePad mSignaturePad;
    private Button privateSettleAutoFillBtn, privateSettleNxtSubmitBtn, privateSettleClearSignatureBtn;
    private String userUID;
    private boolean isSigned = false;
    private Bitmap ppSignature;
    private Bitmap rpSignature;
    private PrivateSettlementModel privateSettlementModel;
    private String endAddress;
    private PrivateSettlementDAL privateSettlementDAL;
    private PrivateSettlementController privateSettlementController;
    private ScrollView privateSettleScrollView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_private_settlement);
        userUID = checkUserAuth();
        if (getIntent().hasExtra("tripModel")) {
            tripModel = new TripModel();
            tripModel = getIntent().getParcelableExtra("tripModel");
            endAddress = getIntent().getStringExtra("endAddress");
            Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
            setSupportActionBar(toolbar);
            getSupportActionBar().setDisplayShowHomeEnabled(true);
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            toolbarTitle = (TextView) findViewById(R.id.toolbarTitle);
            toolbarTitle.setText(util.fromMiliSecToDateString(tripModel.getDateCreated(), "dd MMM yyyy hh:mm a"));
            toolbarFilterSpinner = (Spinner) findViewById(R.id.toolbarFilterSpinner);
            toolbarFilterSpinner.setVisibility(View.GONE);
            privateSettleScrollView = (ScrollView) findViewById(R.id.privateSettleScrollView);
            privateSettleSignatureError = (TextView) findViewById(R.id.privateSettleSignatureError);
            privateSettleFullNameTxtInput = (TextInputLayout) findViewById(R.id.privateSettleFullNameTxtInput);
            privateSettleNRICNumTxtInput = (TextInputLayout) findViewById(R.id.privateSettleNRICNumTxtInput);
            privateSettleContactNumTxtInput = (TextInputLayout) findViewById(R.id.privateSettleContactNumTxtInput);
            privateSettleMotorRegNoTxtInput = (TextInputLayout) findViewById(R.id.privateSettleMotorRegNoTxtInput);
            privateSettleInsuranceCompanyTxtInput = (TextInputLayout) findViewById(R.id.privateSettleInsuranceCompanyTxtInput);
            privateSettleCompensationAmtTxtInput = (TextInputLayout) findViewById(R.id.privateSettleCompensationAmtTxtInput);
            privateSettlePartyTxtView = (TextView) findViewById(R.id.privateSettlePartyTxtView);
            privateSettleAutoFillBtn = (Button) findViewById(R.id.privateSettleAutoFillBtn);
            privateSettleFullNameEditTxt = (EditText) findViewById(R.id.privateSettleFullNameEditTxt);
            privateSettleContactNumEditTxt = (EditText) findViewById(R.id.privateSettleContactNumEditTxt);
            privateSettleMotorRegNoEditTxt = (EditText) findViewById(R.id.privateSettleMotorRegNoEditTxt);
            privateSettleInsuranceCompanyEditTxt = (EditText) findViewById(R.id.privateSettleInsuranceCompanyEditTxt);
            privateSettleCompensationAmtEditTxt = (EditText) findViewById(R.id.privateSettleCompensationAmtEditTxt);
            privateSettleNRICNumEditTxt = (EditText) findViewById(R.id.privateSettleNRICNumEditTxt);
            privateSettleClearSignatureBtn = (Button) findViewById(R.id.privateSettleClearSignatureBtn);
            privateSettleNxtSubmitBtn = (Button) findViewById(R.id.privateSettleNxtSubmitBtn);
            mSignaturePad = (SignaturePad) findViewById(R.id.privateSettleSignPad);
            privateSettleAutoFillBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    showProgressDialog("Filling in details...").show();
                    UserDAL userDAL = new UserDAL();
                    userDAL.getUserByUID(userUID).addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot dataSnapshot) {
                            UserModel userModel = new UserModel();
                            if (dataSnapshot.hasChildren()) {
                                userModel = dataSnapshot.getValue(userModel.getClass());
                                userModel.setUid(dataSnapshot.getKey());
                                autoFillForm(userModel);
                            }
                            hideProgressDialog();
                        }

                        @Override
                        public void onCancelled(DatabaseError databaseError) {

                        }

                    });
                }
            });
            privateSettleClearSignatureBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    mSignaturePad.clear();
                }
            });
            mSignaturePad.setOnSignedListener(new SignaturePad.OnSignedListener() {
                @Override
                public void onStartSigning() {

                }

                @Override
                public void onSigned() {
                    isSigned = true;
                }

                @Override
                public void onClear() {
                    isSigned = false;
                }
            });
            privateSettleNxtSubmitBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (privateSettleNxtSubmitBtn.getText().equals("Next")) {
                        boolean isFormValid = validateForm(true);
                        if (isFormValid) {
                            String fullName = privateSettleFullNameEditTxt.getText().toString();
                            String nric = privateSettleNRICNumEditTxt.getText().toString();
                            long contactNum = Long.valueOf(privateSettleContactNumEditTxt.getText().toString());
                            String motorVehicleRegNo = privateSettleMotorRegNoEditTxt.getText().toString();
                            String insuranceCompany = privateSettleInsuranceCompanyEditTxt.getText().toString();
                            double compensationAmt = Double.valueOf(privateSettleCompensationAmtEditTxt.getText().toString());
                            privateSettlementModel =
                                    new PrivateSettlementModel(fullName, nric, contactNum, motorVehicleRegNo, insuranceCompany, compensationAmt);
                            ppSignature = mSignaturePad.getTransparentSignatureBitmap();
                            privateSettleCompensationAmtTxtInput.setVisibility(View.GONE);
                            privateSettleCompensationAmtEditTxt.setVisibility(View.GONE);
                            privateSettlePartyTxtView.setText("(Receiving Party)");
                            privateSettleNxtSubmitBtn.setText("Submit");
                            clearForm();
                            privateSettlePartyTxtView.setTextColor(ContextCompat.getColor(getApplicationContext(),
                                    R.color.private_settlement_receving_party));
                            scrollTo(0);
                        }
                    } else {
                        boolean isFormValid = validateForm(false);
                        if (isFormValid) {
                            String fullName = privateSettleFullNameEditTxt.getText().toString();
                            String nric = privateSettleNRICNumEditTxt.getText().toString();
                            long contactNum = Long.valueOf(privateSettleContactNumEditTxt.getText().toString());
                            String motorVehicleRegNo = privateSettleMotorRegNoEditTxt.getText().toString();
                            String insuranceCompany = privateSettleInsuranceCompanyEditTxt.getText().toString();
                            privateSettlementModel.setRpFullName(fullName);
                            privateSettlementModel.setRpNRIC(nric);
                            privateSettlementModel.setRpContactNum(contactNum);
                            privateSettlementModel.setRpMotorVehicleRegNo(motorVehicleRegNo);
                            privateSettlementModel.setRpInsuranceCompany(insuranceCompany);
                            privateSettlementModel.setTripUID(tripModel.getUid());
                            rpSignature = mSignaturePad.getSignatureBitmap();
                            showProgressDialog("Submitting Form...").show();
                            privateSettlementController = new PrivateSettlementController();
                            privateSettlementModel.setDateTimeOfAccident(tripModel.getDateCreated());
                            new AsyncTask<Void, Void, Long>() {
                                @Override
                                protected Long doInBackground(Void... params) {
                                    return util.getInternetTime();
                                }

                                @Override
                                protected void onPostExecute(Long result) {
                                    super.onPostExecute(result);
                                    privateSettlementModel = privateSettlementController.generatePrivateSettlementForm(privateSettlementModel, endAddress, result, ppSignature, rpSignature);
                                    privateSettlementDAL = new PrivateSettlementDAL();
                                    privateSettlementDAL.insertPrivateSettlementFormToStorage(privateSettlementModel).addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
                                        @Override
                                        public void onProgress(UploadTask.TaskSnapshot taskSnapshot) {
                                            int bytesTransferred = (int) taskSnapshot.getBytesTransferred();
                                            int totalBytes = (int) taskSnapshot.getTotalByteCount();
                                            int progress = (100 * bytesTransferred) / totalBytes;
                                            setProgressDialogMessage("Submitting form... " + progress + "%");
                                        }
                                    }).addOnFailureListener(new OnFailureListener() {
                                        @Override
                                        public void onFailure(@NonNull Exception exception) {
                                            Toast.makeText(getApplicationContext(), exception.getMessage(), Toast.LENGTH_SHORT).show();
                                            hideProgressDialog();
                                        }
                                    }).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                                        @Override
                                        public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                                            insertPrivateSettlementModel(privateSettlementModel);
                                        }
                                    });
                                }
                            }.execute();
                        }
                    }
                }
            });
        } else {
            Intent i = new Intent(PrivateSettlementActivity.this, MainActivity.class);
            startActivity(i);
            finish();
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == android.R.id.home) {
            quitFillFormAlertDialog().show();
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onBackPressed() {
        quitFillFormAlertDialog().show();
    }

    private void autoFillForm(UserModel userModel) {
        if (userModel.getFullName() != null) {
            privateSettleFullNameEditTxt.setText(userModel.getFullName());
        }
        if (userModel.getNRIC() != null) {
            privateSettleNRICNumEditTxt.setText(userModel.getNRIC());
        }
        if (userModel.getContactNum() != 0) {
            privateSettleContactNumEditTxt.setText(String.valueOf(userModel.getContactNum()));
        }
        if (userModel.getMotorVehicleRegNo() != null) {
            privateSettleMotorRegNoEditTxt.setText(userModel.getMotorVehicleRegNo());
        }
        if (userModel.getInsuranceCompany() != null) {
            privateSettleInsuranceCompanyEditTxt.setText(userModel.getInsuranceCompany());
        }
    }

    private void scrollTo(final int y) {
        privateSettleScrollView.postDelayed(new Runnable() {
            @Override
            public void run() {
                privateSettleScrollView.smoothScrollTo(0, y);
            }
        }, 0);
    }

    private void clearForm() {
        privateSettleFullNameEditTxt.setText("");
        privateSettleNRICNumEditTxt.setText("");
        privateSettleContactNumEditTxt.setText("");
        privateSettleMotorRegNoEditTxt.setText("");
        privateSettleInsuranceCompanyEditTxt.setText("");
        privateSettleCompensationAmtEditTxt.setText("");
        mSignaturePad.clear();
    }

    private boolean validateForm(boolean isPayingParty) {
        if (privateSettleFullNameEditTxt.getText().toString().trim().isEmpty()) {
            privateSettleFullNameTxtInput.setError(getString(R.string.private_settlement_required_field));
            scrollTo(privateSettleFullNameTxtInput.getTop());
            return false;
        } else {
            privateSettleFullNameTxtInput.setErrorEnabled(false);
        }
        if (privateSettleNRICNumEditTxt.getText().toString().trim().isEmpty()) {
            privateSettleNRICNumTxtInput.setError(getString(R.string.private_settlement_required_field));
            scrollTo(privateSettleNRICNumTxtInput.getTop());
            return false;
        } else {
            privateSettleNRICNumTxtInput.setErrorEnabled(false);
        }
        if (privateSettleContactNumEditTxt.getText().toString().trim().isEmpty()) {
            privateSettleContactNumTxtInput.setError(getString(R.string.private_settlement_required_field));
            scrollTo(privateSettleContactNumTxtInput.getTop());
            return false;
        } else {
            privateSettleContactNumTxtInput.setErrorEnabled(false);
        }
        if (privateSettleMotorRegNoEditTxt.getText().toString().trim().isEmpty()) {
            privateSettleMotorRegNoTxtInput.setError(getString(R.string.private_settlement_required_field));
            scrollTo(privateSettleMotorRegNoTxtInput.getTop());
            return false;
        } else {
            privateSettleMotorRegNoTxtInput.setErrorEnabled(false);
        }
        if (privateSettleInsuranceCompanyEditTxt.getText().toString().trim().isEmpty()) {
            privateSettleInsuranceCompanyTxtInput.setError(getString(R.string.private_settlement_required_field));
            scrollTo(privateSettleInsuranceCompanyTxtInput.getTop());
            return false;
        } else {
            privateSettleInsuranceCompanyTxtInput.setErrorEnabled(false);
        }
        if (isPayingParty) {
            if (privateSettleCompensationAmtEditTxt.getText().toString().trim().isEmpty()) {
                privateSettleCompensationAmtTxtInput.setError(getString(R.string.private_settlement_required_field));
                scrollTo(privateSettleCompensationAmtTxtInput.getTop());
                return false;
            } else {
                privateSettleCompensationAmtTxtInput.setErrorEnabled(false);
            }
        }
        if (!isSigned) {
            privateSettleSignatureError.setVisibility(View.VISIBLE);
            scrollTo(mSignaturePad.getTop());
            return false;
        } else {
            privateSettleSignatureError.setVisibility(View.GONE);
        }
        return true;
    }

    private void insertPrivateSettlementModel(PrivateSettlementModel privateSettlementModel) {
        privateSettlementDAL.insertPrivateSettlementModel(privateSettlementModel).addOnCompleteListener(new OnCompleteListener() {
            @Override
            public void onComplete(@NonNull Task task) {
                if (task.isSuccessful()) {
                    Intent i = new Intent(PrivateSettlementActivity.this, MainActivity.class);
                    i.putExtra("FilledPrivateSettlementForm", true);
                    startActivity(i);
                    hideProgressDialog();
                } else {
                    Toast.makeText(getApplicationContext(), task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                    hideProgressDialog();
                }
            }
        });
    }
}
