package com.fypj.icreative.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
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
import com.fypj.icreative.utils.RecyclerItemClickListener;
import com.fypj.icreative.utils.Utils;
import com.fypj.icreative.activity.MainActivity;
import com.fypj.icreative.activity.SelectedTripActivity;
import com.fypj.icreative.adapter.TripAdapter;
import com.fypj.icreative.dal.TripDAL;
import com.fypj.icreative.model.TripModel;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;


public class TripFragment extends Fragment {
    private RecyclerView mRecyclerView;
    private SwipeRefreshLayout mSwipeRefreshLayout;
    private List<TripModel> tripModelList = Collections.emptyList();
    private String userUID;
    private TripAdapter recyclerAdapter;
    private TextView tripsDateRangeTxtView;
    private MainActivity mainActivity;
    private TripDAL tripDAL;
    private long startDateTime;
    private long endDateTime;
    private Utils utils;
    private Spinner toolbarFilterSpinner;
    private ArrayAdapter<String> spinnerArrAdapter;

    public TripFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        mainActivity = ((MainActivity) getActivity());
        mainActivity.showProgressDialog("Loading...").show();
        final View layout = inflater.inflate(R.layout.fragment_trip, container, false);
        mainActivity.getSupportActionBar().show();
        mainActivity.setToolBarTitle("All Trips");
        setItemsInToolBar();
        userUID = ((MainActivity) getActivity()).checkUserAuth();
        mRecyclerView = (RecyclerView) layout.findViewById(R.id.trip_recyclerView);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        getTripByDateCreated();
        setOnTouchListenerRecyclerView();
        mSwipeRefreshLayout = (SwipeRefreshLayout) layout.findViewById(R.id.trip_swipeRefreshLayout);
        setOnRefreshListenerForRecyclerView();
        tripsDateRangeTxtView = (TextView) layout.findViewById(R.id.tripsDateRangeTxtView);
        tripsDateRangeTxtView.setText("TODAY");
        return layout;
    }

    private void setItemsInToolBar() {
        List<String> spinnerList = new ArrayList<String>();
        spinnerList.add("Today");
        spinnerList.add("Week");
        spinnerList.add("Month");
        spinnerArrAdapter = new ArrayAdapter<String>(getActivity(), android.R.layout.simple_spinner_dropdown_item, spinnerList);
        toolbarFilterSpinner = mainActivity.getToolbarFilterSpinner();
        toolbarFilterSpinner.setAdapter(spinnerArrAdapter);
        setOnFilterItemSelectedListener();
        mainActivity.setTripFragmentToolbarControlsVisibility(true);
    }

    private void setFilterStartEndDate(String selectedItem) {
        if (selectedItem.equals("Today")) {
            startDateTime = utils.fromDateToUnixEpoch(utils.getTodayDateWithoutTime());
            endDateTime = utils.fromDateToUnixEpoch(utils.getEndOfTodayDate());
        } else if (selectedItem.equals("Week")) {
            Date[] startEndDate = utils.getStartAndEndOfTheWeek();
            startDateTime = utils.fromDateToUnixEpoch(startEndDate[0]);
            endDateTime = utils.fromDateToUnixEpoch(startEndDate[1]);
        } else {
            Date[] startEndDate = utils.getStartAndEndOfMonth();
            startDateTime = utils.fromDateToUnixEpoch(startEndDate[0]);
            endDateTime = utils.fromDateToUnixEpoch(startEndDate[1]);
        }
    }

    private void setOnFilterItemSelectedListener(){
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
                        recyclerAdapter.dataChange(tripModelList);
                        if (!selectedItem.equals("Today")) {
                            String fStartDate = utils.fromMiliSecToDateString(startDateTime, "dd MMM");
                            String fEndDate = utils.fromMiliSecToDateString(endDateTime, "dd MMM");
                            tripsDateRangeTxtView.setText(String.format("%s TO %s", fStartDate, fEndDate).toUpperCase());
                        } else {
                            tripsDateRangeTxtView.setText("TODAY");
                        }
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

    private void getTripByDateCreated() {
        utils = new Utils();
        startDateTime = utils.fromDateToUnixEpoch(utils.getTodayDateWithoutTime());
        endDateTime = utils.fromDateToUnixEpoch(utils.getEndOfTodayDate());
        toolbarFilterSpinner.setSelection(0);
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
                recyclerAdapter = new TripAdapter(getActivity(), tripModelList);
                mRecyclerView.setAdapter(recyclerAdapter);
                ((MainActivity) getActivity()).hideProgressDialog();
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    private void setOnRefreshListenerForRecyclerView() {
        mSwipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                tripDAL.getTripByDateCreated(startDateTime, endDateTime, userUID).addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        if (dataSnapshot.hasChildren()) {
                            tripModelList = new ArrayList<TripModel>();
                            for (DataSnapshot tripModelDss : dataSnapshot.getChildren()) {
                                TripModel tripModel = tripModelDss.getValue(TripModel.class);
                                tripModel.setUid(tripModelDss.getKey());
                                tripModel.setUserUID(userUID);
                                tripModelList.add(tripModel);
                            }
                            recyclerAdapter.dataChange(tripModelList);
                        }
                        mSwipeRefreshLayout.setRefreshing(false);
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                });
            }
        });
    }

    private void setOnTouchListenerRecyclerView() {
        mRecyclerView.addOnItemTouchListener(new RecyclerItemClickListener(getActivity(),
                new RecyclerItemClickListener.OnItemClickListener() {
                    @Override
                    public void onItemClick(View view, int position) {
                        TripModel tripModel = recyclerAdapter.getItem(position);
                        Intent intent = new Intent(getActivity(), SelectedTripActivity.class);
                        intent.putExtra("tripModel", tripModel);
                        startActivity(intent);
                    }
                }));
    }
}
