package com.fypj.icreative.adapter;


import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.fypj.icreative.R;
import com.fypj.icreative.model.ApprovedRepairShopModel;

import java.util.Collections;
import java.util.List;

public class AllApprovedRepairShopAdapter extends RecyclerView.Adapter<AllApprovedRepairShopAdapter.ViewHolder> {
    List<ApprovedRepairShopModel> approvedRepairShopModelList = Collections.emptyList();

    public AllApprovedRepairShopAdapter(Context context, List<ApprovedRepairShopModel> data) {
        Context context1 = context;
        approvedRepairShopModelList = data;
    }

    @Override
    public AllApprovedRepairShopAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.all_approved_repair_shop_card, parent, false);
        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(AllApprovedRepairShopAdapter.ViewHolder holder, int position) {
        ApprovedRepairShopModel current = approvedRepairShopModelList.get(position);
        holder.allApprovedRepairCompanyName.setText(current.getCompanyName());
        holder.allApprovedRepairAddress.setText(current.getAddress());
        holder.allApprovedRepairAddressTelNo.setText(current.getTelNo());
        if (!current.getRemarks().isEmpty()) {
            holder.allApprovedRepairAddressRemarks.setVisibility(View.VISIBLE);
            holder.allApprovedRepairAddressRemarksTag.setVisibility(View.VISIBLE);
            holder.allApprovedRepairAddressRemarks.setText(current.getRemarks());
        }
        else{
            holder.allApprovedRepairAddressRemarks.setVisibility(View.GONE);
            holder.allApprovedRepairAddressRemarksTag.setVisibility(View.GONE);
        }
    }

    @Override
    public int getItemCount() {
        return approvedRepairShopModelList.size();
    }

    public ApprovedRepairShopModel getItem(int position) {
        return approvedRepairShopModelList.get(position);
    }

    public void dataChange(List<ApprovedRepairShopModel> data) {
        this.approvedRepairShopModelList = data;
        notifyDataSetChanged();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        private TextView allApprovedRepairCompanyName;
        private TextView allApprovedRepairAddress;
        private TextView allApprovedRepairAddressTelNo;
        private TextView allApprovedRepairAddressRemarks;
        private TextView allApprovedRepairAddressRemarksTag;

        public ViewHolder(View itemView) {
            super(itemView);
            allApprovedRepairCompanyName = (TextView) itemView.findViewById(R.id.allApprovedRepairCompanyName);
            allApprovedRepairAddress = (TextView) itemView.findViewById(R.id.allApprovedRepairAddress);
            allApprovedRepairAddressTelNo = (TextView) itemView.findViewById(R.id.allApprovedRepairAddressTelNo);
            allApprovedRepairAddressRemarks = (TextView) itemView.findViewById(R.id.allApprovedRepairAddressRemarks);
            allApprovedRepairAddressRemarksTag = (TextView) itemView.findViewById(R.id.allApprovedRepairAddressRemarksTag);
        }
    }
}
