package com.fypj.icreative.fragment;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.TextView;

import com.fypj.icreative.R;
import com.fypj.icreative.activity.MainActivity;
import com.fypj.icreative.adapter.ChartAdapter;
import com.fypj.icreative.dal.TripDAL;
import com.fypj.icreative.model.TripModel;
import com.fypj.icreative.utils.Utils;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class ChartFragment extends Fragment {

    private MainActivity mainActivity;
    private String userUID;
    private RecyclerView mRecyclerView;
    private Utils utils;
    private long startDateTime;
    private long endDateTime;
    private TripDAL tripDAL;
    private List<TripModel> tripModelList;
    private TextView chartDisplayTypeTxtView;
    private String[] mDataset = {"SAFETY_INDEX", "DISTANCE_TRAVELLED"};
    private int mDatasetTypes[] = {ChartAdapter.SAFETY_INDEX, ChartAdapter.DISTANCE_TRAVELLED};
    private ChartAdapter chartAdapter;
    private String[] filterType = {"DAY", "WEEK", "MONTH"};
    private Spinner toolbarFilterSpinner;
    private ArrayAdapter<String> spinnerArrAdapter;

    public ChartFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        userUID = ((MainActivity) getActivity()).checkUserAuth();
        View layout = inflater.inflate(R.layout.fragment_chart, container, false);
        mainActivity = ((MainActivity) getActivity());
        mainActivity.showProgressDialog("Loading...").show();
        mainActivity.getSupportActionBar().show();
        mainActivity.setToolBarTitle("Graphs");
        setItemsInToolBar();
        mRecyclerView = (RecyclerView) layout.findViewById(R.id.chart_recyclerView);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        chartDisplayTypeTxtView = (TextView) layout.findViewById(R.id.chartDisplayTypeTxtView);
        utils = new Utils();
        setChartAdapter();
        return layout;
    }

    private void setItemsInToolBar() {
        List<String> spinnerList = new ArrayList<String>();
        spinnerList.add(filterType[0]);
        spinnerList.add(filterType[1]);
        spinnerList.add(filterType[2]);
        spinnerArrAdapter = new ArrayAdapter<String>(getActivity(), android.R.layout.simple_spinner_dropdown_item, spinnerList);
        toolbarFilterSpinner = mainActivity.getToolbarFilterSpinner();
        toolbarFilterSpinner.setAdapter(spinnerArrAdapter);
        toolbarFilterSpinner.setVisibility(View.VISIBLE);
        setOnFilterItemSelectedListener();
    }

    private void setChartAdapter() {
        startDateTime = utils.fromDateToUnixEpoch(utils.getFourDaysFromToday());
        endDateTime = utils.fromDateToUnixEpoch(utils.getEndOfTodayDate());
        tripDAL = new TripDAL();
        tripDAL.getTripByDateCreated(startDateTime, endDateTime, userUID).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                tripModelList = new ArrayList<TripModel>();
                if (dataSnapshot.hasChildren()) {
                    for (DataSnapshot tripModelDss : dataSnapshot.getChildren()) {
                        TripModel tripModel = tripModelDss.getValue(TripModel.class);
                        tripModel.setUid(tripModelDss.getKey());
                        tripModel.setUserUID(userUID);
                        tripModelList.add(tripModel);
                    }
                }
                String fStartDate = utils.fromMiliSecToDateString(startDateTime, "dd MMM");
                String fEndDate = utils.fromMiliSecToDateString(endDateTime, "dd MMM");
                chartDisplayTypeTxtView.setText(String.format("%s TO %s", fStartDate, fEndDate).toUpperCase());
                chartAdapter = new ChartAdapter(getActivity(), mDataset, mDatasetTypes, tripModelList, filterType[0]);
                mRecyclerView.setAdapter(chartAdapter);
                mainActivity.hideProgressDialog();
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    private void setOnFilterItemSelectedListener() {
        toolbarFilterSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                mainActivity.showProgressDialog("Loading...").show();
                final String selectedItem = spinnerArrAdapter.getItem(position);
                setFilterStartEndDate(selectedItem);
                tripDAL.getTripByDateCreated(startDateTime, endDateTime, userUID).addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        tripModelList = new ArrayList<TripModel>();
                        if (dataSnapshot.hasChildren()) {
                            for (DataSnapshot tripModelDss : dataSnapshot.getChildren()) {
                                TripModel tripModel = tripModelDss.getValue(TripModel.class);
                                tripModel.setUid(tripModelDss.getKey());
                                tripModel.setUserUID(userUID);
                                tripModelList.add(tripModel);
                            }
                        }
                        chartAdapter.dataChange(tripModelList, selectedItem);
                        String fStartDate = utils.fromMiliSecToDateString(startDateTime, "dd MMM");
                        String fEndDate = utils.fromMiliSecToDateString(endDateTime, "dd MMM");
                        chartDisplayTypeTxtView.setText(String.format("%s TO %s", fStartDate, fEndDate).toUpperCase());
                        mainActivity.hideProgressDialog();
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                });
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
    }

    private void setFilterStartEndDate(String selectedItem) {
        if (selectedItem.equals("DAY")) {
            startDateTime = utils.fromDateToUnixEpoch(utils.getFourDaysFromToday());
        } else if (selectedItem.equals("WEEK")) {
            startDateTime = utils.fromDateToUnixEpoch(utils.getFourWeeksFromToday());
        } else {
            startDateTime = utils.fromDateToUnixEpoch(utils.getFourMonthsFromToday());
        }
        endDateTime = utils.fromDateToUnixEpoch(utils.getEndOfTodayDate());
    }
}
