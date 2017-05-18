package com.fypj.icreative.activity;

import android.os.Build;
import android.support.annotation.IdRes;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.ImageButton;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.fypj.icreative.R;
import com.fypj.icreative.fragment.ChartFragment;
import com.fypj.icreative.fragment.ProfileFragment;
import com.fypj.icreative.fragment.StartDrivingFragment;
import com.fypj.icreative.fragment.TripFragment;
import com.roughike.bottombar.BottomBar;
import com.roughike.bottombar.OnMenuTabClickListener;

public class MainActivity extends BaseActivity {
    private BottomBar mBottomBar;
    private FragmentManager fm = getSupportFragmentManager();
    private FragmentTransaction ft;
    private boolean firstLoad = true;
    private Spinner toolbarFilterSpinner;
    private ImageButton toolbarSearchBtn;
    private TextView toolbarTitle;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        mBottomBar = BottomBar.attach(this, savedInstanceState);
        mBottomBar.noTabletGoodness();
        mBottomBar.noTopOffset();
        mBottomBar.setItems(R.menu.bottombar_menu);
        setOnMenuTabClickListener();
        mBottomBar.mapColorForTab(0, "#263238");
        mBottomBar.mapColorForTab(1, "#263238");
        mBottomBar.mapColorForTab(2, "#263238");
        mBottomBar.mapColorForTab(3, "#263238");
        toolbarFilterSpinner = (Spinner) findViewById(R.id.toolbarFilterSpinner);
        toolbarTitle = (TextView) findViewById(R.id.toolbarTitle);
    }

    public Spinner getToolbarFilterSpinner() {
        return toolbarFilterSpinner;
    }

    public void setTripFragmentToolbarControlsVisibility(boolean visible) {
        if (visible) {
            toolbarFilterSpinner.setVisibility(View.VISIBLE);
        } else {
            toolbarFilterSpinner.setVisibility(View.GONE);
        }
    }

    public void setToolBarTitle(String title) {
        toolbarTitle.setText(title);
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        mBottomBar.onSaveInstanceState(outState);
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
            this.finishAffinity();
        } else {
            finish();
        }
    }

    private void beingTranscation() {
        if (getIntent().hasExtra("FilledPrivateSettlementForm")) {
            TripFragment tripFragment = new TripFragment();
            fm = getSupportFragmentManager();
            ft = fm.beginTransaction();
            ft.add(R.id.main_container, tripFragment, "tripFragment");
            ft.commit();
            Toast.makeText(getApplicationContext(), "Form submitted successfully", Toast.LENGTH_SHORT).show();
        } else {
            StartDrivingFragment fragment = new StartDrivingFragment();
            fm = getSupportFragmentManager();
            ft = fm.beginTransaction();
            ft.add(R.id.main_container, fragment, "StartDrivingFragment");
            ft.commit();
        }
    }


    private void setOnMenuTabClickListener() {
        mBottomBar.setOnMenuTabClickListener(new OnMenuTabClickListener() {
            @Override
            public void onMenuTabSelected(@IdRes int menuItemId) {
                fm = getSupportFragmentManager();
                switch (menuItemId) {
                    case R.id.menu_speedometer:
                        if (!firstLoad) {
                            StartDrivingFragment startDrivingFragment = new StartDrivingFragment();
                            ft = fm.beginTransaction();
                            ft.replace(R.id.main_container, startDrivingFragment, "StartDrivingFragment");
                            ft.addToBackStack(null);
                            ft.commit();
                        } else {
                            beingTranscation();
                            firstLoad = false;
                        }
                        break;
                    case R.id.menu_trip:
                        TripFragment tripFragment = new TripFragment();
                        ft = fm.beginTransaction();
                        ft.replace(R.id.main_container, tripFragment, "tripFragment");
                        ft.addToBackStack(null);
                        ft.commit();
                        break;
                    case R.id.menu_chart:
                        ChartFragment chartFragment = new ChartFragment();
                        ft = fm.beginTransaction();
                        ft.replace(R.id.main_container, chartFragment, "chartFragment");
                        ft.addToBackStack(null);
                        ft.commit();
                        break;
                    case R.id.menu_profile:
//                        startActivity(new Intent(MainActivity.this, AllApprovedRepairShopsActivity.class));
                        ProfileFragment profileFragment = new ProfileFragment();
                        ft = fm.beginTransaction();
                        ft.replace(R.id.main_container, profileFragment, "profileFragment");
                        ft.addToBackStack(null);
                        ft.commit();
                        break;
                }
            }

            @Override
            public void onMenuTabReSelected(@IdRes int menuItemId) {

            }
        });
    }
}
