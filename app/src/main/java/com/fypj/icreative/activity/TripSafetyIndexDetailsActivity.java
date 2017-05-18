package com.fypj.icreative.activity;

import android.app.Activity;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.widget.Spinner;
import android.widget.TextView;

import com.fypj.icreative.R;
import com.fypj.icreative.controller.SafetyIndexController;
import com.fypj.icreative.model.TripModel;
import com.google.android.gms.vision.text.Text;

public class TripSafetyIndexDetailsActivity extends BaseActivity {
    private TextView tripSafetyIndexSafetyIndexTxtView;
    private TextView tripSafetyIndexDDistanceTxtView;
    private TextView tripSafetyIndexDistanceTxtView;
    private TextView tripSafetyIndexDAvgSpeedTxtView;
    private TextView tripSafetyIndexAvgSpeedTxtView;
    private TextView tripSafetyIndexDSpeedingTxtView;
    private TextView tripSafetyIndexSpeedingTxtView;
    private TextView tripSafetyIndexDSharpTurnTxtView;
    private TextView tripSafetyIndexSharpTurnTxtView;
    private TextView tripSafetyIndexResultTxtView;
    private TripModel tripModel;
    private SafetyIndexController safetyIndexController;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_trip_safety_index_details);
        checkUserAuth();
        if (getIntent().hasExtra("tripModel")) {
            showProgressDialog("Loading...").show();
            tripModel = new TripModel();
            tripModel = getIntent().getParcelableExtra("tripModel");
            Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
            setSupportActionBar(toolbar);
            getSupportActionBar().setDisplayShowHomeEnabled(true);
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            TextView toolbarTitle = (TextView) findViewById(R.id.toolbarTitle);
            toolbarTitle.setText("Trip's Safety Index Details");
            Spinner toolbarFilterSpinner = (Spinner) findViewById(R.id.toolbarFilterSpinner);
            toolbarFilterSpinner.setVisibility(View.GONE);
            safetyIndexController = new SafetyIndexController();
            tripSafetyIndexSafetyIndexTxtView = (TextView) findViewById(R.id.tripSafetyIndexSafetyIndexTxtView);
            tripSafetyIndexDDistanceTxtView = (TextView) findViewById(R.id.tripSafetyIndexDDistanceTxtView);
            tripSafetyIndexDistanceTxtView = (TextView) findViewById(R.id.tripSafetyIndexDistanceTxtView);
            tripSafetyIndexDAvgSpeedTxtView = (TextView) findViewById(R.id.tripSafetyIndexDAvgSpeedTxtView);
            tripSafetyIndexAvgSpeedTxtView = (TextView) findViewById(R.id.tripSafetyIndexAvgSpeedTxtView);
            tripSafetyIndexDSpeedingTxtView = (TextView) findViewById(R.id.tripSafetyIndexDSpeedingTxtView);
            tripSafetyIndexSpeedingTxtView = (TextView) findViewById(R.id.tripSafetyIndexSpeedingTxtView);
            tripSafetyIndexDSharpTurnTxtView = (TextView) findViewById(R.id.tripSafetyIndexDSharpTurnTxtView);
            tripSafetyIndexSharpTurnTxtView = (TextView) findViewById(R.id.tripSafetyIndexSharpTurnTxtView);
            tripSafetyIndexResultTxtView = (TextView) findViewById(R.id.tripSafetyIndexResultTxtView);
            setValueToText();
            hideProgressDialog();
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

    private void setValueToText() {
        int tripSafetyIndex = tripModel.getTripSafetyIndex();
        String safetyIndexWBending = null;
        if (tripSafetyIndex >= 80) {
            safetyIndexWBending = String.valueOf(tripSafetyIndex + "(Band 1)");
            tripSafetyIndexSafetyIndexTxtView.setTextColor(ContextCompat.getColor(getApplicationContext(), R.color.trip_safety_index_band_1));
        } else if (tripSafetyIndex >= 60) {
            safetyIndexWBending = String.valueOf(tripSafetyIndex + "(Band 2)");
            tripSafetyIndexSafetyIndexTxtView.setTextColor(ContextCompat.getColor(getApplicationContext(), R.color.trip_safety_index_band_2));
        } else if (tripSafetyIndex >= 40) {
            safetyIndexWBending = String.valueOf(tripSafetyIndex + "(Band 3)");
            tripSafetyIndexSafetyIndexTxtView.setTextColor(ContextCompat.getColor(getApplicationContext(), R.color.trip_safety_index_band_3));
        } else {
            safetyIndexWBending = String.valueOf(tripSafetyIndex + "(Band 4)");
            tripSafetyIndexSafetyIndexTxtView.setTextColor(ContextCompat.getColor(getApplicationContext(), R.color.trip_safety_index_band_4));
        }
        tripSafetyIndexSafetyIndexTxtView.setText(safetyIndexWBending);
        tripSafetyIndexResultTxtView.setText(safetyIndexWBending);

        tripSafetyIndexDDistanceTxtView.setText("Distance Risk (" + tripModel.getDistanceTravelled() + ")");
        int distanceRisk = safetyIndexController.distanceRiskCal(tripModel.getDistanceTravelled());
        tripSafetyIndexDistanceTxtView.setText(String.valueOf("(" + distanceRisk + ")"));

        tripSafetyIndexDAvgSpeedTxtView.setText("Average Speed Risk (" + tripModel.getAvgSpeed() + "km/h)");
        int avgSpeedRisk = safetyIndexController.avgSpeedRiskCal(tripModel.getAvgSpeed());
        tripSafetyIndexAvgSpeedTxtView.setText(String.valueOf("(" + avgSpeedRisk + ")"));

        tripSafetyIndexDSpeedingTxtView.setText("Speeding Risk (" + tripModel.getSpeedingCount() + " Count)");
        int speedingRisk = safetyIndexController.speedingRiskCal(tripModel.getSpeedingCount());
        tripSafetyIndexSpeedingTxtView.setText(String.valueOf("(" + speedingRisk + ")"));

        tripSafetyIndexDSharpTurnTxtView.setText("Sharp Turn Risk (" + tripModel.getVigorousTurnCount() + " Count)");
        int sharpTurnRisk = safetyIndexController.sharpTurnCal(tripModel.getVigorousTurnCount());
        tripSafetyIndexSharpTurnTxtView.setText(String.valueOf("(" + sharpTurnRisk + ")"));
    }


}
