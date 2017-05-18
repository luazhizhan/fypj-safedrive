package com.fypj.icreative.activity;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageButton;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.fypj.icreative.R;
import com.fypj.icreative.adapter.SelectedTripAdapter;
import com.fypj.icreative.dal.CarAccidentClaimDAL;
import com.fypj.icreative.dal.PrivateSettlementDAL;
import com.fypj.icreative.model.CarAccidentClaimModel;
import com.fypj.icreative.model.PrivateSettlementModel;
import com.fypj.icreative.utils.Utils;
import com.fypj.icreative.controller.SelectedTripController;
import com.fypj.icreative.model.TripModel;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.PolylineOptions;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;

import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;


public class SelectedTripActivity extends BaseActivity implements OnMapReadyCallback {
    private GoogleMap mMap;
    private GoogleApiClient mGoogleApiClient;
    private SelectedTripController selectedTripController;
    private TripModel tripModel;
    private Utils util = new Utils();
    private MarkerOptions startPointMarkerOptions;
    private MarkerOptions endPointMarkerOptions;
    private SelectedTripAdapter selectedTripAdapter;
    private String[] mDataset = {"DIRECTION", "SAFETY_INDEX", "DISTANCE", "AVG_SPEED", "VIGOROUS_TURN", "SPEEDING_COUNT", "GENERATE_PRIVATE_SETTLEMENT_FORM", "GENERATE_CAR_ACCIDENT_FORM"};
    private int mDatasetTypes[] = {SelectedTripAdapter.DIRECTION, SelectedTripAdapter.SAFETY_INDEX, SelectedTripAdapter.DISTANCE,
            SelectedTripAdapter.AVG_SPEED, SelectedTripAdapter.VIGOROUS_TURN, SelectedTripAdapter.SPEEDING_COUNT, SelectedTripAdapter.GENERATE_PRIVATE_SETTLEMENT_FORM, SelectedTripAdapter.GENERATE_CAR_ACCIDENT_FORM};
    private RecyclerView mRecyclerView;
    private LinearLayoutManager mLayoutManager;
    private ImageButton toolbarSearchBtn;
    private Spinner toolbarFilterSpinner;
    private TextView toolbarTitle;
    private String startAddress = null;
    private String endAddress = null;
    private Bitmap mapSnapShot;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_selected_trip);
        if (getIntent().hasExtra("tripModel")) {
            showProgressDialog("Loading...").show();
            tripModel = new TripModel();
            tripModel = getIntent().getParcelableExtra("tripModel");
            Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
            setSupportActionBar(toolbar);
            getSupportActionBar().setDisplayShowHomeEnabled(true);
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            toolbarTitle = (TextView) findViewById(R.id.toolbarTitle);
            toolbarTitle.setText(util.fromMiliSecToDateString(tripModel.getDateCreated(), "dd MMM yyyy hh:mm a"));
            toolbarFilterSpinner = (Spinner) findViewById(R.id.toolbarFilterSpinner);
            toolbarFilterSpinner.setVisibility(View.GONE);
            selectedTripController = new SelectedTripController();
            mRecyclerView = (RecyclerView) findViewById(R.id.selectedTripRecyclerView);
            mLayoutManager = new LinearLayoutManager(SelectedTripActivity.this);
            mRecyclerView.setLayoutManager(mLayoutManager);
            SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                    .findFragmentById(R.id.selectedTrip_map);
            mapFragment.getMapAsync(this);
        } else {
            Intent i = new Intent(SelectedTripActivity.this, MainActivity.class);
            startActivity(i);
            finish();
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

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finish();
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

        LatLng startPoint = new LatLng(tripModel.getStartLat(), tripModel.getStartLon());
        startPointMarkerOptions = new MarkerOptions();
        startPointMarkerOptions.position(startPoint);
        startPointMarkerOptions.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_GREEN));
        startPointMarkerOptions.title("Starting Point");
        LatLng endPoint = new LatLng(tripModel.getEndLat(), tripModel.getEndLon());
        endPointMarkerOptions = new MarkerOptions();
        endPointMarkerOptions.position(endPoint);
        endPointMarkerOptions.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_VIOLET));
        endPointMarkerOptions.title("Ending Point");
        String url = selectedTripController.getUrl(startPoint, endPoint);
        FetchUrl FetchUrl = new FetchUrl();
        FetchUrl.execute(url);
        mMap.setTrafficEnabled(true);
        mMap.moveCamera(CameraUpdateFactory.newLatLng(endPoint));
        mMap.animateCamera(CameraUpdateFactory.zoomTo(15));
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

    protected synchronized void buildGoogleApiClient() {
        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .addApi(LocationServices.API)
                .build();
        mGoogleApiClient.connect();
    }


    private class FetchUrl extends AsyncTask<String, Void, String> {

        @Override
        protected String doInBackground(String... url) {
            // For storing data from web service
            String data = "";
            try {
                // Fetching the data from web service
                data = util.downloadUrl(url[0]);
                Log.d("Background Task data", data);
            } catch (Exception e) {
                Log.d("Background Task", e.toString());
            }
            return data;
        }

        @Override
        protected void onPostExecute(String result) {
            super.onPostExecute(result);
            ParserTask parserTask = new ParserTask();
            // Invokes the thread for parsing the JSON data
            parserTask.execute(result);
        }
    }

    /**
     * A class to parse the Google Places in JSON format
     */
    private class ParserTask extends AsyncTask<String, Integer, List<List<HashMap<String, String>>>> {

        // Parsing the data in non-ui thread
        @Override
        protected List<List<HashMap<String, String>>> doInBackground(String... jsonData) {

            JSONObject jObject;
            List<List<HashMap<String, String>>> routes = null;

            try {
                jObject = new JSONObject(jsonData[0]);
                Log.d("ParserTask", jsonData[0]);
                Log.d("ParserTask", selectedTripController.toString());

                // Starts parsing data
                routes = selectedTripController.parse(jObject);
                Log.d("ParserTask", "Executing routes");
                Log.d("ParserTask", routes.toString());

            } catch (Exception e) {
                Log.d("ParserTask", e.toString());
                e.printStackTrace();
            }
            return routes;
        }

        // Executes in UI thread, after the parsing process

        @Override
        protected void onPostExecute(List<List<HashMap<String, String>>> result) {
            ArrayList<LatLng> points;
            PolylineOptions lineOptions = null;

            // Traversing through all the routes
            for (int i = 0; i < result.size(); i++) {
                points = new ArrayList<>();
                lineOptions = new PolylineOptions();

                // Fetching i-th route
                List<HashMap<String, String>> path = result.get(i);


                // Fetching all the points in i-th route
                for (int j = 0; j < path.size(); j++) {
                    HashMap<String, String> point = path.get(j);
                    if (point.get("startAddress") != null) {
                        startAddress = point.get("startAddress");
                        endAddress = point.get("endAddress");
                        startPointMarkerOptions.snippet(startAddress);
                        endPointMarkerOptions.snippet(endAddress);
                        mMap.addMarker(startPointMarkerOptions);
                        mMap.addMarker(endPointMarkerOptions);
                    } else {
                        double lat = Double.parseDouble(point.get("lat"));
                        double lng = Double.parseDouble(point.get("lng"));
                        LatLng position = new LatLng(lat, lng);
                        points.add(position);
                    }
                }

                // Adding all the points in the route to LineOptions
                lineOptions.addAll(points);
                lineOptions.width(10);
                lineOptions.color(Color.RED);
            }
            // Drawing polyline in the Google Map for the i-th route
            if (lineOptions != null) {
                mMap.addPolyline(lineOptions);
            }
            mMap.setOnMapLoadedCallback(new GoogleMap.OnMapLoadedCallback() {
                @Override
                public void onMapLoaded() {
                    mMap.snapshot(new GoogleMap.SnapshotReadyCallback() {
                        @Override
                        public void onSnapshotReady(Bitmap bitmap) {
                            ByteArrayOutputStream photoByteArrayOutputStream = new ByteArrayOutputStream();
                            Bitmap resizedBitmap = util.getResizedBitmap(bitmap, 500, 350, 0);
                            resizedBitmap.compress(Bitmap.CompressFormat.PNG, 100, photoByteArrayOutputStream);
                            tripModel.setGoogleMapSnapShot(photoByteArrayOutputStream.toByteArray());
                            PrivateSettlementDAL privateSettlementDAL = new PrivateSettlementDAL();
                            privateSettlementDAL.getPrivateSettlementByTripUID(tripModel.getUid()).addListenerForSingleValueEvent(new ValueEventListener() {
                                @Override
                                public void onDataChange(DataSnapshot dataSnapshot) {
                                    if (dataSnapshot.hasChildren()) {
                                        PrivateSettlementModel privateSettlementModel = dataSnapshot.getValue(PrivateSettlementModel.class);
                                        privateSettlementModel.setUid(dataSnapshot.getKey());
                                        privateSettlementModel.setTripUID(dataSnapshot.getKey());
                                        mDataset = new String[]{"DIRECTION", "SAFETY_INDEX", "DISTANCE", "AVG_SPEED", "VIGOROUS_TURN", "SPEEDING_COUNT", "DOWNLOAD_PRIVATE_SETTLEMENT_FORM"};
                                        mDatasetTypes = new int[]{SelectedTripAdapter.DIRECTION, SelectedTripAdapter.SAFETY_INDEX, SelectedTripAdapter.DISTANCE,
                                                SelectedTripAdapter.AVG_SPEED, SelectedTripAdapter.VIGOROUS_TURN, SelectedTripAdapter.SPEEDING_COUNT, SelectedTripAdapter.DOWNLOAD_PRIVATE_SETTLEMENT_FORM};
                                        selectedTripAdapter = new SelectedTripAdapter(SelectedTripActivity.this, mDataset, mDatasetTypes, tripModel, privateSettlementModel, null, startAddress, endAddress);
                                        mRecyclerView.setAdapter(selectedTripAdapter);
                                        hideProgressDialog();
                                    } else {
                                        CarAccidentClaimDAL carAccidentClaimDAL = new CarAccidentClaimDAL();
                                        carAccidentClaimDAL.getCarAccidentClaimByTripUID(tripModel.getUid()).addListenerForSingleValueEvent(new ValueEventListener() {
                                            @Override
                                            public void onDataChange(DataSnapshot dataSnapshot) {
                                                if (dataSnapshot.hasChildren()) {
                                                    CarAccidentClaimModel carAccidentClaimModel = dataSnapshot.getValue(CarAccidentClaimModel.class);
                                                    carAccidentClaimModel.setUid(dataSnapshot.getKey());
                                                    carAccidentClaimModel.setTripUID(dataSnapshot.getKey());
                                                    mDataset = new String[]{"DIRECTION", "SAFETY_INDEX", "DISTANCE", "AVG_SPEED", "VIGOROUS_TURN", "SPEEDING_COUNT", "DOWNLOAD_CAR_ACCIDENT_FORM", "ALL_APPROVED_REPAIR_SHOP"};
                                                    mDatasetTypes = new int[]{SelectedTripAdapter.DIRECTION, SelectedTripAdapter.SAFETY_INDEX, SelectedTripAdapter.DISTANCE,
                                                            SelectedTripAdapter.AVG_SPEED, SelectedTripAdapter.VIGOROUS_TURN, SelectedTripAdapter.SPEEDING_COUNT, SelectedTripAdapter.DOWNLOAD_CAR_ACCIDENT_FORM, SelectedTripAdapter.ALL_APPROVED_REPAIR_SHOP};
                                                    selectedTripAdapter = new SelectedTripAdapter(SelectedTripActivity.this, mDataset, mDatasetTypes, tripModel, null, carAccidentClaimModel, startAddress, endAddress);
                                                } else {
                                                    selectedTripAdapter = new SelectedTripAdapter(SelectedTripActivity.this, mDataset, mDatasetTypes, tripModel, null, null, startAddress, endAddress);
                                                }
                                                mRecyclerView.setAdapter(selectedTripAdapter);
                                                hideProgressDialog();
                                            }

                                            @Override
                                            public void onCancelled(DatabaseError databaseError) {

                                            }
                                        });
                                    }
                                }

                                @Override
                                public void onCancelled(DatabaseError databaseError) {

                                }
                            });
                        }
                    });
                }
            });
        }
    }
}
