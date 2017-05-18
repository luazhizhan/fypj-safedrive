package com.fypj.icreative.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.TextView;
import com.fypj.icreative.R;
import com.fypj.icreative.adapter.AllApprovedRepairShopAdapter;
import com.fypj.icreative.dal.ApprovedRepairShopDAL;
import com.fypj.icreative.dal.UserDAL;
import com.fypj.icreative.model.ApprovedRepairShopModel;
import com.fypj.icreative.model.TripModel;
import com.fypj.icreative.model.UserModel;
import com.fypj.icreative.utils.RecyclerItemClickListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;
import java.util.ArrayList;
import java.util.List;

public class AllApprovedRepairShopsActivity extends BaseActivity {
    private Spinner allApprovedRepairShopSpinner;
    private ArrayAdapter<String> spinnerArrAdapter;
    private String userUID;
    private UserModel userModel;
    private ApprovedRepairShopDAL approvedRepairShopDAL;
    private List<ApprovedRepairShopModel> approvedRepairShopModelList;
    private AllApprovedRepairShopAdapter recyclerAdapter;
    private RecyclerView mRecyclerView;
    private boolean firstLoad = true;
    private TripModel tripModel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_all_approved_repair_shops);
        if (getIntent().hasExtra("tripModel")) {
            showProgressDialog("Loading...").show();
            tripModel = new TripModel();
            tripModel = getIntent().getParcelableExtra("tripModel");
            Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
            setSupportActionBar(toolbar);
            getSupportActionBar().setDisplayShowHomeEnabled(true);
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            TextView toolbarTitle = (TextView) findViewById(R.id.toolbarTitle);
            toolbarTitle.setText("Approved Car Repair Shops");
            Spinner toolbarFilterSpinner = (Spinner) findViewById(R.id.toolbarFilterSpinner);
            toolbarFilterSpinner.setVisibility(View.GONE);
            userUID = checkUserAuth();
            allApprovedRepairShopSpinner = (Spinner) findViewById(R.id.allApprovedRepairShopSpinner);
            setListToAdapter();
            mRecyclerView = (RecyclerView) findViewById(R.id.allApprovedRepairShopRecycleView);
            mRecyclerView.setLayoutManager(new LinearLayoutManager(this));
            getUserModel();
            setOnTouchListenerRecyclerView();
        }
    }

    private void getUserModel() {
        UserDAL userDAL = new UserDAL();
        userDAL.getUserByUID(userUID).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                userModel = dataSnapshot.getValue(UserModel.class);
                userModel.setUid(dataSnapshot.getKey());
                approvedRepairShopDAL = new ApprovedRepairShopDAL();
                getApprovedRepairShopByRegion("North");
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
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

    private void setListToAdapter() {
        List<String> spinnerList = new ArrayList<>();
        spinnerList.add("North");
        spinnerList.add("South");
        spinnerList.add("East");
        spinnerList.add("West");
        spinnerArrAdapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_dropdown_item, spinnerList);
        allApprovedRepairShopSpinner.setAdapter(spinnerArrAdapter);
        setOnFilterItemSelectedListener();
    }

    private void setOnFilterItemSelectedListener() {
        allApprovedRepairShopSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                final String selectedItem = spinnerArrAdapter.getItem(position);
                showProgressDialog("Loading...").show();
                if (!firstLoad) {
                    getApprovedRepairShopByRegion(selectedItem);
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
    }

    private void getApprovedRepairShopByRegion(String region) {
        approvedRepairShopDAL.getApprovedRepairShopByRegion(userModel.getInsuranceCompany(), region)
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        approvedRepairShopModelList = new ArrayList<ApprovedRepairShopModel>();
                        if (dataSnapshot.hasChildren()) {
                            for (DataSnapshot approvedRepairShopsDss : dataSnapshot.getChildren()) {
                                ApprovedRepairShopModel approvedRepairShopModel = approvedRepairShopsDss.getValue(ApprovedRepairShopModel.class);
                                approvedRepairShopModel.setUid(approvedRepairShopsDss.getKey());
                                approvedRepairShopModelList.add(approvedRepairShopModel);
                            }
                        }
                        if (firstLoad) {
                            recyclerAdapter = new AllApprovedRepairShopAdapter(getApplicationContext(), approvedRepairShopModelList);
                            mRecyclerView.setAdapter(recyclerAdapter);
                            firstLoad = false;
                        } else {
                            recyclerAdapter.dataChange(approvedRepairShopModelList);
                        }
                        hideProgressDialog();
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                });
    }

    private void setOnTouchListenerRecyclerView() {
        mRecyclerView.addOnItemTouchListener(new RecyclerItemClickListener(this,
                new RecyclerItemClickListener.OnItemClickListener() {
                    @Override
                    public void onItemClick(View view, int position) {
                        ApprovedRepairShopModel approvedRepairShopModel = recyclerAdapter.getItem(position);
                        Intent intent = new Intent(AllApprovedRepairShopsActivity.this, SelectedRepairShopActivity.class);
                        intent.putExtra("tripModel", tripModel);
                        intent.putExtra("approvedRepairShopModel", approvedRepairShopModel);
                        startActivity(intent);
                    }
                }));
    }
}
