package com.fypj.icreative.activity;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.location.Address;
import android.location.Geocoder;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.design.widget.TextInputLayout;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.fypj.icreative.R;
import com.fypj.icreative.controller.CarAccidentClaimController;
import com.fypj.icreative.dal.CarAccidentClaimDAL;
import com.fypj.icreative.dal.UserDAL;
import com.fypj.icreative.model.CarAccidentClaimModel;
import com.fypj.icreative.model.CarAccidentClaimPhotosModel;
import com.fypj.icreative.model.TripModel;
import com.fypj.icreative.model.UserModel;
import com.fypj.icreative.service.GPSService;
import com.fypj.icreative.utils.Utils;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.UploadTask;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;

public class CarAccidentClaimActivity extends BaseActivity {
    private static final int CAMERA_CAPTURE_IMAGE_REQUEST_CODE = 100;
    public static final int MEDIA_TYPE_IMAGE = 1;
    private Uri fileUri;
    private String userUID;
    private TripModel tripModel;
    private String startAddress, endAddress;
    private Utils utils = new Utils();
    private CarAccidentClaimController carAccidentClaimController;
    private CarAccidentClaimModel carAccidentClaimModel;
    private RelativeLayout carClaimParticularsLayout, carClaimNecessaryLayout;
    private ScrollView carClaimParticularsScrollView;
    private TextView carClaimUserTxtView;
    private Button carClaimNxtBtn;
    private Button carClaimSubmitBtn;
    private EditText carClaimFullNameEditTxt, carClaimNRICNumEditTxt, carClaimContactNumEditTxt, carClaimMotorRegNoEditTxt,
            carClaimInsuranceCompanyEditTxt, carClaimRemarksEditTxt;
    private TextInputLayout carClaimFullNameTxtInput;
    private TextInputLayout carClaimNRICNumTxtInput;
    private TextInputLayout carClaimContactNumTxtInput;
    private TextInputLayout carClaimMotorRegNoTxtInput;
    private TextInputLayout carClaimInsuranceCompanyTxtInput;
    private ImageButton carClaimPhotoImgBtn1, carClaimPhotoImgBtn2, carClaimPhotoImgBtn3,
            carClaimPhotoImgBtn4, carClaimPhotoImgBtn5, carClaimPhotoImgBtn6;
    private ImageButton updateCarClaimPhotoBtn = null;
    private CarAccidentClaimDAL carAccidentClaimDAL;
    private Long internetDate;
    private UserModel userModel;
    private double currentLat;
    private double currentLon;
    private GPSService gps;
    private CarAccidentClaimActivityReceiver carAccidentClaimActivityReceiver;
    private byte[] googleMapSnapShot;
    private HashMap<String, String> weatherHM;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_car_accident_claim);
        userUID = checkUserAuth();
        if (getIntent().hasExtra("tripModel")) {
            tripModel = new TripModel();
            tripModel = getIntent().getParcelableExtra("tripModel");
            startAddress = getIntent().getStringExtra("startAddress");
            endAddress = getIntent().getStringExtra("endAddress");
            googleMapSnapShot = getIntent().getByteArrayExtra("googleMapSnapShot");
            Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
            setSupportActionBar(toolbar);
            getSupportActionBar().setDisplayShowHomeEnabled(true);
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            TextView toolbarTitle = (TextView) findViewById(R.id.toolbarTitle);
            toolbarTitle.setText(utils.fromMiliSecToDateString(tripModel.getDateCreated(), "dd MMM yyyy hh:mm a"));
            Spinner toolbarFilterSpinner = (Spinner) findViewById(R.id.toolbarFilterSpinner);
            toolbarFilterSpinner.setVisibility(View.GONE);
            startGPS();
            carAccidentClaimController = new CarAccidentClaimController(getApplicationContext());
            carClaimParticularsLayout = (RelativeLayout) findViewById(R.id.carClaimParticularsLayout);
            carClaimNecessaryLayout = (RelativeLayout) findViewById(R.id.carClaimNecessaryLayout);
            carClaimParticularsScrollView = (ScrollView) findViewById(R.id.carClaimParticularsScrollView);
            carClaimUserTxtView = (TextView) findViewById(R.id.carClaimUserTxtView);
            Button carClaimAutoFillBtn = (Button) findViewById(R.id.carClaimAutoFillBtn);
            carClaimAutoFillBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    showProgressDialog("Filling in details...").show();
                    UserDAL userDAL = new UserDAL();
                    userDAL.getUserByUID(userUID).addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot dataSnapshot) {
                            userModel = new UserModel();
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
            carClaimFullNameEditTxt = (EditText) findViewById(R.id.carClaimFullNameEditTxt);
            carClaimContactNumEditTxt = (EditText) findViewById(R.id.carClaimContactNumEditTxt);
            carClaimMotorRegNoEditTxt = (EditText) findViewById(R.id.carClaimMotorRegNoEditTxt);
            carClaimInsuranceCompanyEditTxt = (EditText) findViewById(R.id.carClaimInsuranceCompanyEditTxt);
            carClaimNRICNumEditTxt = (EditText) findViewById(R.id.carClaimNRICNumEditTxt);
            carClaimFullNameTxtInput = (TextInputLayout) findViewById(R.id.carClaimFullNameTxtInput);
            carClaimNRICNumTxtInput = (TextInputLayout) findViewById(R.id.carClaimNRICNumTxtInput);
            carClaimContactNumTxtInput = (TextInputLayout) findViewById(R.id.carClaimContactNumTxtInput);
            carClaimMotorRegNoTxtInput = (TextInputLayout) findViewById(R.id.carClaimMotorRegNoTxtInput);
            carClaimInsuranceCompanyTxtInput = (TextInputLayout) findViewById(R.id.carClaimInsuranceCompanyTxtInput);
            carClaimNxtBtn = (Button) findViewById(R.id.carClaimNxtBtn);
            setCarClaimNxtBtnOnClickEvent();
            carClaimPhotoImgBtn1 = (ImageButton) findViewById(R.id.carClaimPhotoImgBtn1);
            carClaimPhotoImgBtn2 = (ImageButton) findViewById(R.id.carClaimPhotoImgBtn2);
            carClaimPhotoImgBtn3 = (ImageButton) findViewById(R.id.carClaimPhotoImgBtn3);
            carClaimPhotoImgBtn4 = (ImageButton) findViewById(R.id.carClaimPhotoImgBtn4);
            carClaimPhotoImgBtn5 = (ImageButton) findViewById(R.id.carClaimPhotoImgBtn5);
            carClaimPhotoImgBtn6 = (ImageButton) findViewById(R.id.carClaimPhotoImgBtn6);
            setImageBtnsOnClickEvent();
            carClaimRemarksEditTxt = (EditText) findViewById(R.id.carClaimRemarksEditTxt);
            TextInputLayout carClaimRemarksEditTxtInput = (TextInputLayout) findViewById(R.id.carClaimRemarksEditTxtInput);
            carClaimSubmitBtn = (Button) findViewById(R.id.carClaimSubmitBtn);
            setCarClaimSubmitBtnOnClickEvent();
        }
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        // save file url in bundle as it will be null on scren orientation changes
        outState.putParcelable("file_uri", fileUri);
    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        // get the file url
        fileUri = savedInstanceState.getParcelable("file_uri");
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == CAMERA_CAPTURE_IMAGE_REQUEST_CODE) {
            if (resultCode == RESULT_OK) {
                previewCapturedImage();
            } else if (resultCode == RESULT_CANCELED) {
                Toast.makeText(getApplicationContext(),
                        "User cancelled image capture", Toast.LENGTH_SHORT)
                        .show();
            } else {
                // failed to capture image
                Toast.makeText(getApplicationContext(),
                        "Sorry! Failed to capture image", Toast.LENGTH_SHORT)
                        .show();
            }
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        gps.stopUsingGPS();
        carAccidentClaimController.deleteFolder();
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
            carClaimFullNameEditTxt.setText(userModel.getFullName());
        }
        if (userModel.getNRIC() != null) {
            carClaimNRICNumEditTxt.setText(userModel.getNRIC());
        }
        if (userModel.getContactNum() != 0) {
            carClaimContactNumEditTxt.setText(String.valueOf(userModel.getContactNum()));
        }
        if (userModel.getMotorVehicleRegNo() != null) {
            carClaimMotorRegNoEditTxt.setText(userModel.getMotorVehicleRegNo());
        }
        if (userModel.getInsuranceCompany() != null) {
            carClaimInsuranceCompanyEditTxt.setText(userModel.getInsuranceCompany());
        }
    }

    private void setCarClaimNxtBtnOnClickEvent() {
        carClaimNxtBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (carClaimUserTxtView.getText().equals("(Self)")) {
                    if (validateForm()) {
                        String fullName = carClaimFullNameEditTxt.getText().toString();
                        String nric = carClaimNRICNumEditTxt.getText().toString();
                        long contactNum = Long.valueOf(carClaimContactNumEditTxt.getText().toString());
                        String motorVehicleRegNo = carClaimMotorRegNoEditTxt.getText().toString();
                        String insuranceCompany = carClaimInsuranceCompanyEditTxt.getText().toString();
                        carAccidentClaimModel = new CarAccidentClaimModel(fullName, nric, contactNum, motorVehicleRegNo, insuranceCompany);
                        clearParticularForm();
                        //carClaimAutoFillBtn.setVisibility(View.GONE);
                        carClaimUserTxtView.setText("(The Other Party)");
                        scrollTo(0);
                    }
                } else {
                    if (validateForm()) {
                        String fullName = carClaimFullNameEditTxt.getText().toString();
                        String nric = carClaimNRICNumEditTxt.getText().toString();
                        long contactNum = Long.valueOf(carClaimContactNumEditTxt.getText().toString());
                        String motorVehicleRegNo = carClaimMotorRegNoEditTxt.getText().toString();
                        String insuranceCompany = carClaimInsuranceCompanyEditTxt.getText().toString();
                        carAccidentClaimModel.setOpFullName(fullName);
                        carAccidentClaimModel.setOpNRIC(nric);
                        carAccidentClaimModel.setOpContactNum(contactNum);
                        carAccidentClaimModel.setOpMotorVehicleRegNo(motorVehicleRegNo);
                        carAccidentClaimModel.setOpInsuranceCompany(insuranceCompany);
                        clearParticularForm();
                        carClaimParticularsLayout.setVisibility(View.GONE);
                        carClaimNecessaryLayout.setVisibility(View.VISIBLE);
                    }

                }
            }
        });
    }

    private boolean validateForm() {
        if (carClaimFullNameEditTxt.getText().toString().trim().isEmpty()) {
            carClaimFullNameTxtInput.setError(getString(R.string.private_settlement_required_field));
            scrollTo(carClaimFullNameTxtInput.getTop());
            return false;
        } else {
            carClaimFullNameTxtInput.setErrorEnabled(false);
        }
        if (carClaimNRICNumEditTxt.getText().toString().trim().isEmpty()) {
            carClaimNRICNumTxtInput.setError(getString(R.string.private_settlement_required_field));
            scrollTo(carClaimNRICNumTxtInput.getTop());
            return false;
        } else {
            carClaimNRICNumTxtInput.setErrorEnabled(false);
        }
        if (carClaimContactNumEditTxt.getText().toString().trim().isEmpty()) {
            carClaimContactNumTxtInput.setError(getString(R.string.private_settlement_required_field));
            scrollTo(carClaimContactNumTxtInput.getTop());
            return false;
        } else {
            carClaimContactNumTxtInput.setErrorEnabled(false);
        }
        if (carClaimMotorRegNoEditTxt.getText().toString().trim().isEmpty()) {
            carClaimMotorRegNoTxtInput.setError(getString(R.string.private_settlement_required_field));
            scrollTo(carClaimMotorRegNoTxtInput.getTop());
            return false;
        } else {
            carClaimMotorRegNoTxtInput.setErrorEnabled(false);
        }
        if (carClaimInsuranceCompanyEditTxt.getText().toString().trim().isEmpty()) {
            carClaimInsuranceCompanyTxtInput.setError(getString(R.string.private_settlement_required_field));
            scrollTo(carClaimInsuranceCompanyTxtInput.getTop());
            return false;
        } else {
            carClaimInsuranceCompanyTxtInput.setErrorEnabled(false);
        }
        return true;
    }

    private void scrollTo(final int y) {
        carClaimParticularsScrollView.postDelayed(new Runnable() {
            @Override
            public void run() {
                carClaimParticularsScrollView.smoothScrollTo(0, y);
            }
        }, 0);
    }

    private void clearParticularForm() {
        carClaimFullNameEditTxt.setText("");
        carClaimNRICNumEditTxt.setText("");
        carClaimContactNumEditTxt.setText("");
        carClaimMotorRegNoEditTxt.setText("");
        carClaimInsuranceCompanyEditTxt.setText("");
    }

    private void setImageBtnsOnClickEvent() {
        carClaimPhotoImgBtn1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (carClaimPhotoImgBtn1.getTag().equals("noBitmap")) {
                    captureImage();
                } else {
                    createOnTouchDialog(carClaimPhotoImgBtn1);
                }
            }
        });
        carClaimPhotoImgBtn2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (carClaimPhotoImgBtn2.getTag().equals("noBitmap")) {
                    captureImage();
                } else {
                    createOnTouchDialog(carClaimPhotoImgBtn2);
                }
            }
        });
        carClaimPhotoImgBtn3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (carClaimPhotoImgBtn3.getTag().equals("noBitmap")) {
                    captureImage();
                } else {
                    createOnTouchDialog(carClaimPhotoImgBtn3);
                }
            }
        });
        carClaimPhotoImgBtn4.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (carClaimPhotoImgBtn4.getTag().equals("noBitmap")) {
                    captureImage();
                } else {
                    createOnTouchDialog(carClaimPhotoImgBtn4);
                }
            }
        });
        carClaimPhotoImgBtn5.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (carClaimPhotoImgBtn5.getTag().equals("noBitmap")) {
                    captureImage();
                } else {
                    createOnTouchDialog(carClaimPhotoImgBtn5);
                }
            }
        });
        carClaimPhotoImgBtn6.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (carClaimPhotoImgBtn6.getTag().equals("noBitmap")) {
                    captureImage();
                } else {
                    createOnTouchDialog(carClaimPhotoImgBtn6);
                }
            }
        });
    }

    private void captureImage() {
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        fileUri = carAccidentClaimController.getOutputMediaFileUri(MEDIA_TYPE_IMAGE);
        intent.putExtra(MediaStore.EXTRA_OUTPUT, fileUri);
        startActivityForResult(intent, CAMERA_CAPTURE_IMAGE_REQUEST_CODE);
    }

    private String getAddressFromLocation(double lat, double lon) {
        Geocoder geoCoder = new Geocoder(this, Locale.getDefault());
        String finalAddress = null;
        StringBuilder builder = new StringBuilder();
        try {
            List<Address> address = geoCoder.getFromLocation(lat, lon, 1);
            int maxLines = address.get(0).getMaxAddressLineIndex();
            for (int i = 0; i < maxLines; i++) {
                String addressStr = address.get(0).getAddressLine(i);
                builder.append(addressStr);
                builder.append(" ");
            }
            finalAddress = builder.toString(); //This is the complete address.
        } catch (IOException | NullPointerException ignored) {
        }
        return finalAddress;
    }

    private void previewCapturedImage() {
        try {
            showProgressDialog("Loading...").show();
            gps.getLocation();
            BitmapFactory.Options options = new BitmapFactory.Options();
            options.inSampleSize = 4;
            Bitmap bitmap = BitmapFactory.decodeFile(fileUri.getPath(), options);
            final Bitmap resizedBitmap = utils.getResizedBitmap(bitmap, bitmap.getWidth(), bitmap.getHeight(), 90);
            new AsyncTask<Void, Void, Long>() {
                @Override
                protected Long doInBackground(Void... params) {
                    return utils.getInternetTime();
                }

                @Override
                protected void onPostExecute(Long aVoid) {
                    super.onPostExecute(aVoid);
                    setBitmapToImageBtn(resizedBitmap, aVoid);
                    hideProgressDialog();
                }
            }.execute();
        } catch (NullPointerException ignore) {
        }
    }

    private void setBitmapToImageBtn(Bitmap bitmap, Long internetDate) {
        String location = getAddressFromLocation(currentLat, currentLon);
        if (updateCarClaimPhotoBtn != null) {
            updateCarClaimPhotoBtn.setImageBitmap(bitmap);
            updateCarClaimPhotoBtn.setTag("hasBitmap-" + fileUri + "-" + internetDate + "-" + location);
            updateCarClaimPhotoBtn = null;
            return;
        }
        if (carClaimPhotoImgBtn1.getTag().equals("noBitmap")) {
            carClaimPhotoImgBtn1.setImageBitmap(bitmap);
            carClaimPhotoImgBtn1.setTag("hasBitmap-" + fileUri + "-" + internetDate + "-" + location);
            return;
        }
        if (carClaimPhotoImgBtn2.getTag().equals("noBitmap")) {
            carClaimPhotoImgBtn2.setImageBitmap(bitmap);
            carClaimPhotoImgBtn2.setTag("hasBitmap-" + fileUri + "-" + internetDate + "-" + location);
            return;
        }
        if (carClaimPhotoImgBtn3.getTag().equals("noBitmap")) {
            carClaimPhotoImgBtn3.setImageBitmap(bitmap);
            carClaimPhotoImgBtn3.setTag("hasBitmap-" + fileUri + "-" + internetDate + "-" + location);
            return;
        }
        if (carClaimPhotoImgBtn4.getTag().equals("noBitmap")) {
            carClaimPhotoImgBtn4.setImageBitmap(bitmap);
            carClaimPhotoImgBtn4.setTag("hasBitmap-" + fileUri + "-" + internetDate + "-" + location);
            return;
        }
        if (carClaimPhotoImgBtn5.getTag().equals("noBitmap")) {
            carClaimPhotoImgBtn5.setImageBitmap(bitmap);
            carClaimPhotoImgBtn5.setTag("hasBitmap-" + fileUri + "-" + internetDate + "-" + location);
            return;
        }
        if (carClaimPhotoImgBtn6.getTag().equals("noBitmap")) {
            carClaimPhotoImgBtn6.setImageBitmap(bitmap);
            carClaimPhotoImgBtn6.setTag("hasBitmap-" + fileUri + "-" + internetDate + "-" + location);
            return;
        }
    }

    private List<CarAccidentClaimPhotosModel> getAllCarAccidentClaimPhotosModel() {
        List<CarAccidentClaimPhotosModel> CarAccidentClaimPhotosModelList = new ArrayList<>();
        if (!carClaimPhotoImgBtn1.getTag().equals("noBitmap")) {
            String[] stringArr = carClaimPhotoImgBtn1.getTag().toString().split("-");
            Uri fileUri = Uri.parse(stringArr[1]);
            String dateTaken = utils.fromMiliSecToDateString(Long.parseLong(stringArr[2]), "dd-MMM-yyyy hh:mm:ss a");
            String currentLocation = stringArr[3];
            CarAccidentClaimPhotosModel carAccidentClaimPhotosModel = new CarAccidentClaimPhotosModel(fileUri, dateTaken, currentLocation);
            CarAccidentClaimPhotosModelList.add(carAccidentClaimPhotosModel);
        }
        if (!carClaimPhotoImgBtn2.getTag().equals("noBitmap")) {
            String[] stringArr = carClaimPhotoImgBtn2.getTag().toString().split("-");
            Uri fileUri = Uri.parse(stringArr[1]);
            String dateTaken = utils.fromMiliSecToDateString(Long.parseLong(stringArr[2]), "dd-MMM-yyyy hh:mm:ss a");
            String currentLocation = stringArr[3];
            CarAccidentClaimPhotosModel carAccidentClaimPhotosModel = new CarAccidentClaimPhotosModel(fileUri, dateTaken, currentLocation);
            CarAccidentClaimPhotosModelList.add(carAccidentClaimPhotosModel);
        }
        if (!carClaimPhotoImgBtn3.getTag().equals("noBitmap")) {
            String[] stringArr = carClaimPhotoImgBtn3.getTag().toString().split("-");
            Uri fileUri = Uri.parse(stringArr[1]);
            String dateTaken = utils.fromMiliSecToDateString(Long.parseLong(stringArr[2]), "dd-MMM-yyyy hh:mm:ss a");
            String currentLocation = stringArr[3];
            CarAccidentClaimPhotosModel carAccidentClaimPhotosModel = new CarAccidentClaimPhotosModel(fileUri, dateTaken, currentLocation);
            CarAccidentClaimPhotosModelList.add(carAccidentClaimPhotosModel);
        }
        if (!carClaimPhotoImgBtn4.getTag().equals("noBitmap")) {
            String[] stringArr = carClaimPhotoImgBtn4.getTag().toString().split("-");
            Uri fileUri = Uri.parse(stringArr[1]);
            String dateTaken = utils.fromMiliSecToDateString(Long.parseLong(stringArr[2]), "dd-MMM-yyyy hh:mm:ss a");
            String currentLocation = stringArr[3];
            CarAccidentClaimPhotosModel carAccidentClaimPhotosModel = new CarAccidentClaimPhotosModel(fileUri, dateTaken, currentLocation);
            CarAccidentClaimPhotosModelList.add(carAccidentClaimPhotosModel);
        }
        if (!carClaimPhotoImgBtn5.getTag().equals("noBitmap")) {
            String[] stringArr = carClaimPhotoImgBtn5.getTag().toString().split("-");
            Uri fileUri = Uri.parse(stringArr[1]);
            String dateTaken = utils.fromMiliSecToDateString(Long.parseLong(stringArr[2]), "dd-MMM-yyyy hh:mm:ss a");
            String currentLocation = stringArr[3];
            CarAccidentClaimPhotosModel carAccidentClaimPhotosModel = new CarAccidentClaimPhotosModel(fileUri, dateTaken, currentLocation);
            CarAccidentClaimPhotosModelList.add(carAccidentClaimPhotosModel);
        }
        if (!carClaimPhotoImgBtn6.getTag().equals("noBitmap")) {
            String[] stringArr = carClaimPhotoImgBtn6.getTag().toString().split("-");
            Uri fileUri = Uri.parse(stringArr[1]);
            String dateTaken = utils.fromMiliSecToDateString(Long.parseLong(stringArr[2]), "dd-MMM-yyyy hh:mm:ss a");
            String currentLocation = stringArr[3];
            CarAccidentClaimPhotosModel carAccidentClaimPhotosModel = new CarAccidentClaimPhotosModel(fileUri, dateTaken, currentLocation);
            CarAccidentClaimPhotosModelList.add(carAccidentClaimPhotosModel);
        }
        return CarAccidentClaimPhotosModelList;
    }

    private void createOnTouchDialog(final ImageButton carClaimPhotoBtn) {
        CharSequence[] task = {"Update photo", "Delete Photo"};
        AlertDialog.Builder builder = new AlertDialog.Builder(CarAccidentClaimActivity.this);
        builder.setItems(task, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                if (which == 0) {
                    captureImage();
                    updateCarClaimPhotoBtn = carClaimPhotoBtn;
                } else {
                    carClaimPhotoBtn.setImageResource(R.mipmap.ic_add);
                    carClaimPhotoBtn.setTag("noBitmap");
                }
            }
        }).show();
    }

    private void setCarClaimSubmitBtnOnClickEvent() {
        carClaimSubmitBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showProgressDialog("Submitting Form...").show();
                if (!carClaimRemarksEditTxt.getText().toString().isEmpty()) {
                    carAccidentClaimModel.setRemarks(carClaimRemarksEditTxt.getText().toString());
                }
                carAccidentClaimModel.setDateTimeOfAccident(tripModel.getDateCreated());
                carAccidentClaimModel.setTripUID(tripModel.getUid());
                carAccidentClaimModel.setRepairShopInfo("null");
                FetchUrl fetchUrl = new FetchUrl();
                fetchUrl.execute();
            }
        });
    }

    private void insertCarAccidentClaimModel(CarAccidentClaimModel carAccidentClaimModel) {
        carAccidentClaimDAL.insertCarAccidentClaimModel(carAccidentClaimModel, userModel).addOnCompleteListener(new OnCompleteListener() {
            @Override
            public void onComplete(@NonNull Task task) {
                if (task.isSuccessful()) {
                    carAccidentClaimController.deleteFolder();
                    Intent i = new Intent(CarAccidentClaimActivity.this, MainActivity.class);
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

    private void startGPS() {
        gps = new GPSService(this);
        if (!gps.canGetLocation()) {
            gps.showSettingsAlert();
        } else {
            carAccidentClaimActivityReceiver = new CarAccidentClaimActivityReceiver();
            IntentFilter intentFilter = new IntentFilter();
            intentFilter.addAction(GPSService.GPS_TRACKER_ACTION);
            registerReceiver(carAccidentClaimActivityReceiver, intentFilter);
            Intent intent = new Intent(this,
                    GPSService.class);
            startService(intent);
        }
    }

    private class CarAccidentClaimActivityReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context arg0, Intent arg1) {
            currentLat = arg1.getDoubleExtra("latitude", 0.0);
            currentLon = arg1.getDoubleExtra("longitude", 0.0);
        }
    }

    private class FetchUrl extends AsyncTask<Void, Void, String> {

        @Override
        protected String doInBackground(Void... params) {
            String result = null;
            LatLng endPoint = new LatLng(tripModel.getEndLat(), tripModel.getEndLon());
            String baseURL = carAccidentClaimController.getWeatherForcastURL(endPoint,getResources().getString(R.string.open_weather_api_key));
            try {
                result = utils.downloadUrl(baseURL);
            } catch (IOException e) {
                e.printStackTrace();
            }
            return result;
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            try {
                JSONObject jObject = new JSONObject(s);
                HashMap<String, String> weatherHM = carAccidentClaimController.parseWeather(jObject);
                GenerateCarClaimFormAsync generateCarClaimFormAsync = new GenerateCarClaimFormAsync();
                generateCarClaimFormAsync.execute(weatherHM);
            } catch (JSONException ignored) {
            }
        }
    }

    public class GenerateCarClaimFormAsync extends AsyncTask<HashMap<String, String>, Void, CarAccidentClaimModel> {
        @Override
        protected CarAccidentClaimModel doInBackground(HashMap<String, String>... weatherHM) {
            List<CarAccidentClaimPhotosModel> carAccidentClaimPhotosModelList = getAllCarAccidentClaimPhotosModel();
            long internetTime = utils.getInternetTime();
            carAccidentClaimModel = carAccidentClaimController.generateCarAccidentForm(carAccidentClaimModel, tripModel, carAccidentClaimPhotosModelList, internetTime,
                    startAddress, endAddress, userModel.getSafetyIndex(), googleMapSnapShot, weatherHM[0]);
            return carAccidentClaimModel;
        }

        @Override
        protected void onPostExecute(final CarAccidentClaimModel carAccidentClaimModel) {
            super.onPostExecute(carAccidentClaimModel);
            carAccidentClaimDAL = new CarAccidentClaimDAL();
            carAccidentClaimDAL.insertCarAccidentClaimFormToStorage(carAccidentClaimModel).addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
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
                    insertCarAccidentClaimModel(carAccidentClaimModel);
                }
            });
        }
    }
}
