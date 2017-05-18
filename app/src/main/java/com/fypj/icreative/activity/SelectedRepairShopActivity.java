package com.fypj.icreative.activity;

import android.Manifest;
import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.fypj.icreative.R;
import com.fypj.icreative.dal.CarAccidentClaimDAL;
import com.fypj.icreative.model.ApprovedRepairShopModel;
import com.fypj.icreative.model.TripModel;
import com.fypj.icreative.utils.Utils;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;

import java.io.IOException;
import java.util.List;

public class SelectedRepairShopActivity extends BaseActivity implements OnMapReadyCallback {

    private TripModel tripModel;
    private ApprovedRepairShopModel approvedRepairShopModel;
    private Button selectedRepairShopSetRepairShopBtn;
    private TextView selectedRepairAddressTxtView;
    private TextView selectedRepairTelTxtView;
    private TextView selectedRepairRemarksTxtViewTag;
    private TextView selectedRepairRemarksTxtView;
    private GoogleMap mMap;
    private GoogleApiClient mGoogleApiClient;
    private String userUID;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_selected_repair_shop);
         userUID = checkUserAuth();
        if (getIntent().hasExtra("tripModel") && getIntent().hasExtra("approvedRepairShopModel")) {
            showProgressDialog("Loading...").show();
            tripModel = new TripModel();
            tripModel = getIntent().getParcelableExtra("tripModel");
            approvedRepairShopModel = new ApprovedRepairShopModel();
            approvedRepairShopModel = getIntent().getParcelableExtra("approvedRepairShopModel");
            Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
            setSupportActionBar(toolbar);
            getSupportActionBar().setDisplayShowHomeEnabled(true);
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            TextView toolbarTitle = (TextView) findViewById(R.id.toolbarTitle);
            toolbarTitle.setText("Selected Repair Shop");
            Spinner toolbarFilterSpinner = (Spinner) findViewById(R.id.toolbarFilterSpinner);
            toolbarFilterSpinner.setVisibility(View.GONE);
            selectedRepairShopSetRepairShopBtn = (Button) findViewById(R.id.selectedRepairShopSetRepairShopBtn);
            selectedRepairShopSetRepairShopBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    AlertDialog.Builder builder = new AlertDialog.Builder(SelectedRepairShopActivity.this);
                    builder.setTitle("Set repair shop info");
                    builder.setMessage("Do you want to set this as the repair shop that you have went to/want to go to?");
                    builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.cancel();
                        }
                    });
                    builder.setPositiveButton("Okay", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            showProgressDialog("Updating...").show();
                            CarAccidentClaimDAL carAccidentClaimDAL = new CarAccidentClaimDAL();
                            String carRepairShopInfo = approvedRepairShopModel.getCompanyName() + ";" + approvedRepairShopModel.getAddress();
                            carAccidentClaimDAL.updateCarAccidentRepairShopInfo(carRepairShopInfo, tripModel.getUid(),userUID)
                                    .addOnCompleteListener(new OnCompleteListener() {
                                        @Override
                                        public void onComplete(@NonNull Task task) {
                                            hideProgressDialog();
                                            Toast.makeText(SelectedRepairShopActivity.this, "Accident claim has been updated", Toast.LENGTH_SHORT).show();
                                        }
                                    });
                        }
                    });
                    builder.show();
                }
            });
            selectedRepairAddressTxtView = (TextView) findViewById(R.id.selectedRepairAddressTxtView);
            selectedRepairTelTxtView = (TextView) findViewById(R.id.selectedRepairTelTxtView);
            selectedRepairRemarksTxtViewTag = (TextView) findViewById(R.id.selectedRepairRemarksTxtViewTag);
            selectedRepairRemarksTxtView = (TextView) findViewById(R.id.selectedRepairRemarksTxtView);
            setApprovedRepairShopsDetails();
            SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                    .findFragmentById(R.id.selectedRepairShopMap);
            mapFragment.getMapAsync(this);
        } else {
            Intent i = new Intent(SelectedRepairShopActivity.this, MainActivity.class);
            startActivity(i);
            finish();
        }
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        //Initialize Google Play Services
        if (android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (ContextCompat.checkSelfPermission(this,
                    Manifest.permission.ACCESS_FINE_LOCATION)
                    == PackageManager.PERMISSION_GRANTED) {
                buildGoogleApiClient();
                mMap.setMyLocationEnabled(false);
            }
        } else {
            buildGoogleApiClient();
            mMap.setMyLocationEnabled(false);
        }

        double selectedRepairShopLatitude = 0;
        double selectedRepairShopLongitude = 0;
        Geocoder geocoder = new Geocoder(this);
        try {
            List<Address> addresses = geocoder.getFromLocationName(approvedRepairShopModel.getAddress(), 1);
            if (addresses.size() > 0) {
                selectedRepairShopLatitude = addresses.get(0).getLatitude();
                selectedRepairShopLongitude = addresses.get(0).getLongitude();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        LatLng selectedRepairPoint = new LatLng(selectedRepairShopLatitude, selectedRepairShopLongitude);
        MarkerOptions selectedRepairMarkerOptions = new MarkerOptions();
        selectedRepairMarkerOptions.position(selectedRepairPoint);
        selectedRepairMarkerOptions.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_GREEN));
        selectedRepairMarkerOptions.title(approvedRepairShopModel.getCompanyName());
        selectedRepairMarkerOptions.snippet("Tel: " + approvedRepairShopModel.getTelNo());
        mMap.addMarker(selectedRepairMarkerOptions);
        mMap.moveCamera(CameraUpdateFactory.newLatLng(selectedRepairPoint));
        mMap.animateCamera(CameraUpdateFactory.zoomTo(15));
        hideProgressDialog();

    }

    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           String permissions[], int[] grantResults) {
        switch (requestCode) {
            case Utils.MY_PERMISSIONS_REQUEST_LOCATION: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    // permission was granted. Do the
                    // contacts-related task you need to do.
                    if (ContextCompat.checkSelfPermission(this,
                            Manifest.permission.ACCESS_FINE_LOCATION)
                            == PackageManager.PERMISSION_GRANTED) {

                        if (mGoogleApiClient == null) {
                            buildGoogleApiClient();
                        }
                        mMap.setMyLocationEnabled(true);
                    }
                } else {
                    // Permission denied, Disable the functionality that depends on this permission.
                    Toast.makeText(this, "permission denied", Toast.LENGTH_LONG).show();
                }
            }
            // other 'case' lines to check for other permissions this app might request.
            // You can add here other case statements according to your requirement.
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == android.R.id.home) {
            finish();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    protected synchronized void buildGoogleApiClient() {
        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .addApi(LocationServices.API)
                .build();
        mGoogleApiClient.connect();
    }

    private void setApprovedRepairShopsDetails() {
        selectedRepairAddressTxtView.setText(approvedRepairShopModel.getAddress());
        selectedRepairTelTxtView.setText(approvedRepairShopModel.getTelNo());
        if (approvedRepairShopModel.getRemarks().isEmpty()) {
            selectedRepairRemarksTxtView.setVisibility(View.GONE);
            selectedRepairRemarksTxtViewTag.setVisibility(View.GONE);
        } else {
            selectedRepairRemarksTxtView.setText(approvedRepairShopModel.getRemarks());
        }
    }

}
