package com.fypj.icreative.adapter;

import android.support.v4.app.FragmentActivity;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.fypj.icreative.R;
import com.fypj.icreative.controller.SafetyIndexController;
import com.fypj.icreative.utils.Utils;
import com.fypj.icreative.model.TripModel;

import java.util.Collections;
import java.util.List;


public class TripAdapter extends RecyclerView.Adapter<TripAdapter.ViewHolder> {
    private FragmentActivity context;
    List<TripModel> tripModelList = Collections.emptyList();
    Utils utils = new Utils();
    private SafetyIndexController safetyIndexController;

    public TripAdapter(FragmentActivity context, List<TripModel> data) {
        this.context = context;
        tripModelList = data;
        safetyIndexController = new SafetyIndexController();
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.trip_all_trips_card, parent, false);
        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        TripModel current = tripModelList.get(position);
        holder.tripsDateCreatedTxtView.setText(utils.fromMiliSecToDateString(current.getDateCreated(), "dd MMM yyyy hh:mm a"));
        int tripSafetyIndex = current.getTripSafetyIndex();
        holder.tripsSafetyTxtView.setText(String.valueOf(tripSafetyIndex));
        String banding = safetyIndexController.checkSafetyIndexBandingWithoutIndex(tripSafetyIndex);
        holder.tripBendingIndexTxtView.setText(banding);
    }

    @Override
    public int getItemCount() {
        return tripModelList.size();
    }

    public TripModel getItem(int position) {
        return tripModelList.get(position);
    }

    public void dataChange(List<TripModel> data) {
        this.tripModelList = data;
        notifyDataSetChanged();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        private TextView tripsDateCreatedTxtView;
        private TextView tripsSafetyTxtView;
        private TextView tripBendingIndexTxtView;

        public ViewHolder(View itemView) {
            super(itemView);
            tripsDateCreatedTxtView = (TextView) itemView.findViewById(R.id.tripsDateCreatedTxtView);
            tripsSafetyTxtView = (TextView) itemView.findViewById(R.id.tripsSafetyTxtView);
            tripBendingIndexTxtView = (TextView) itemView.findViewById(R.id.tripBendingIndexTxtView);
        }
    }
}
