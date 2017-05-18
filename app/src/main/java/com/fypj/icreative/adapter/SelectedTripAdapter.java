package com.fypj.icreative.adapter;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.fypj.icreative.R;
import com.fypj.icreative.activity.AllApprovedRepairShopsActivity;
import com.fypj.icreative.activity.CarAccidentClaimActivity;
import com.fypj.icreative.activity.PrivateSettlementActivity;
import com.fypj.icreative.activity.SelectedTripActivity;
import com.fypj.icreative.activity.TripSafetyIndexDetailsActivity;
import com.fypj.icreative.controller.SafetyIndexController;
import com.fypj.icreative.dal.CarAccidentClaimDAL;
import com.fypj.icreative.dal.PrivateSettlementDAL;
import com.fypj.icreative.model.CarAccidentClaimModel;
import com.fypj.icreative.model.PrivateSettlementModel;
import com.fypj.icreative.model.TripModel;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.storage.FileDownloadTask;
import com.google.firebase.storage.OnProgressListener;

public class SelectedTripAdapter extends RecyclerView.Adapter<SelectedTripAdapter.ViewHolder> {
    public static final int DIRECTION = 0;
    public static final int SAFETY_INDEX = 1;
    public static final int DISTANCE = 2;
    public static final int AVG_SPEED = 3;
    public static final int VIGOROUS_TURN = 4;
    public static final int SPEEDING_COUNT = 5;
    public static final int GENERATE_PRIVATE_SETTLEMENT_FORM = 6;
    public static final int GENERATE_CAR_ACCIDENT_FORM = 7;
    public static final int DOWNLOAD_PRIVATE_SETTLEMENT_FORM = 8;
    public static final int DOWNLOAD_CAR_ACCIDENT_FORM = 9;
    public static final int ALL_APPROVED_REPAIR_SHOP = 10;
    private Context context;
    private SelectedTripActivity selectedTripActivity;
    private String[] mDataSet;
    private int[] mDataSetTypes;
    private TripModel tripModel;
    private PrivateSettlementModel privateSettlementModel;
    private CarAccidentClaimModel carAccidentClaimModel;
    private String startAddress;
    private String endAddress;
    private SafetyIndexController safetyIndexController;

    public SelectedTripAdapter(Context context, String[] dataSet, int[] dataSetTypes,
                               TripModel tripModel, PrivateSettlementModel privateSettlementModel, CarAccidentClaimModel carAccidentClaimModel, String startAddress, String endAddress) {
        this.context = context;
        selectedTripActivity = ((SelectedTripActivity) context);
        this.mDataSet = dataSet;
        this.mDataSetTypes = dataSetTypes;
        this.tripModel = tripModel;
        this.privateSettlementModel = privateSettlementModel;
        this.carAccidentClaimModel = carAccidentClaimModel;
        this.startAddress = startAddress;
        this.endAddress = endAddress;
        safetyIndexController = new SafetyIndexController();
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v;
        if (viewType == DIRECTION) {
            v = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.selected_trip_direction_card, parent, false);
            return new DirectionViewHolder(v);
        } else if (viewType == SAFETY_INDEX) {
            v = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.selected_trip_trip_safety_index, parent, false);
            return new SafetyIndexViewHolder(v);
        } else if (viewType == DISTANCE) {
            v = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.selected_trip_distance_card, parent, false);
            return new DistanceViewHolder(v);
        } else if (viewType == AVG_SPEED) {
            v = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.selected_trip_avg_speed_card, parent, false);
            return new AvgSpeedViewHolder(v);
        } else if (viewType == VIGOROUS_TURN) {
            v = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.selected_trip_sharp_turn, parent, false);
            return new VigorousTurnViewHolder(v);
        } else if (viewType == SPEEDING_COUNT) {
            v = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.selected_trip_speed_count, parent, false);
            return new SpeedingCountViewHolder(v);
        } else if (viewType == GENERATE_PRIVATE_SETTLEMENT_FORM) {
            v = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.selected_trip_fill_private_settlement, parent, false);
            return new GeneratePrivateSettlementFormViewHolder(v);
        } else if (viewType == GENERATE_CAR_ACCIDENT_FORM) {
            v = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.selected_trip_fill_car_accident, parent, false);
            return new GenerateCarAccidentFormViewHolder(v);
        } else if (viewType == DOWNLOAD_PRIVATE_SETTLEMENT_FORM) {
            v = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.selected_trip_private_settlement_file_download, parent, false);
            return new DownloadPrivateSettlementFormViewHolder(v);
        } else if (viewType == DOWNLOAD_CAR_ACCIDENT_FORM) {
            v = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.selected_trip_car_accident_file_download, parent, false);
            return new DownloadCarAccidentFormViewHolder(v);
        } else {
            v = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.selected_trip_all_approved_repair_shop, parent, false);
            return new AllApprovedRepairShopFormViewHolder(v);
        }
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        switch (holder.getItemViewType()) {
            case DIRECTION:
                DirectionViewHolder directionViewHolder = (DirectionViewHolder) holder;
                directionViewHolder.selectedTripFromTextView.setText(startAddress);
                directionViewHolder.selectedTripToTextView.setText(endAddress);
                break;
            case SAFETY_INDEX:
                SafetyIndexViewHolder safetyIndexViewHolder = (SafetyIndexViewHolder) holder;
                int tripSafetyIndex = tripModel.getTripSafetyIndex();
                String sTripSafetyIndex = String.valueOf(tripModel.getTripSafetyIndex());
                String bandingInfo = safetyIndexController.checkSafetyIndexBanding(tripSafetyIndex);
                safetyIndexViewHolder.selectedTripTripSafetyIndexTxtView.setText(bandingInfo);
                break;
            case DISTANCE:
                DistanceViewHolder distanceViewHolder = (DistanceViewHolder) holder;
                distanceViewHolder.selectedTripDistTxtView.setText(String.valueOf(tripModel.getDistanceTravelled() + "m"));
                break;
            case AVG_SPEED:
                AvgSpeedViewHolder avgSpeedViewHolder = (AvgSpeedViewHolder) holder;
                avgSpeedViewHolder.selectedTripAvgSpeedTxtView.setText(String.valueOf(tripModel.getAvgSpeed() + "km/h"));
                break;
            case VIGOROUS_TURN:
                VigorousTurnViewHolder vigorousTurnViewHolder = (VigorousTurnViewHolder) holder;
                vigorousTurnViewHolder.selectedTripVigorousTurnView.setText(String.valueOf(tripModel.getVigorousTurnCount() + " detected"));
                break;
            case SPEEDING_COUNT:
                SpeedingCountViewHolder speedingCountViewHolder = (SpeedingCountViewHolder) holder;
                speedingCountViewHolder.selectedTripSpeedCountTxtView.setText(String.valueOf(tripModel.getSpeedingCount() + " detected"));
                break;
            case GENERATE_PRIVATE_SETTLEMENT_FORM:
                GeneratePrivateSettlementFormViewHolder generateReportViewHolder = (GeneratePrivateSettlementFormViewHolder) holder;
                break;
            case GENERATE_CAR_ACCIDENT_FORM:
                GenerateCarAccidentFormViewHolder generateCarAccidentFormViewHolder = (GenerateCarAccidentFormViewHolder) holder;
                break;
            case DOWNLOAD_PRIVATE_SETTLEMENT_FORM:
                DownloadPrivateSettlementFormViewHolder downloadPrivateSettlementFormViewHolder = (DownloadPrivateSettlementFormViewHolder) holder;
                downloadPrivateSettlementFormViewHolder.privateSettlePrivateSettlementDownloadBtn.setText(privateSettlementModel.getFileName());
                final PrivateSettlementDAL privateSettlementDAL = new PrivateSettlementDAL();
                downloadPrivateSettlementFormViewHolder.privateSettlePrivateSettlementDownloadBtn.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        selectedTripActivity.showProgressDialog("Downloading form...").show();
                        privateSettlementDAL.downloadPrivateSettlementFrom(privateSettlementModel.getFileName()).addOnProgressListener(new OnProgressListener<FileDownloadTask.TaskSnapshot>() {
                            @Override
                            public void onProgress(FileDownloadTask.TaskSnapshot taskSnapshot) {
                                int bytesTransferred = (int) taskSnapshot.getBytesTransferred();
                                int totalBytes = (int) taskSnapshot.getTotalByteCount();
                                int progress = (100 * bytesTransferred) / totalBytes;
                                selectedTripActivity.setProgressDialogMessage("Downloading form... " + progress + "%");
                            }
                        }).addOnSuccessListener(new OnSuccessListener<FileDownloadTask.TaskSnapshot>() {
                            @Override
                            public void onSuccess(FileDownloadTask.TaskSnapshot taskSnapshot) {
                                Toast.makeText(context, "Download successfully", Toast.LENGTH_SHORT).show();
                                selectedTripActivity.hideProgressDialog();
                            }
                        }).addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {

                            }
                        });
                    }
                });
                break;
            case DOWNLOAD_CAR_ACCIDENT_FORM:
                DownloadCarAccidentFormViewHolder downloadCarAccidentFormViewHolder = (DownloadCarAccidentFormViewHolder) holder;
                downloadCarAccidentFormViewHolder.privateSettleCarAccidentDownloadBtn.setText(carAccidentClaimModel.getFileName());
                final CarAccidentClaimDAL carAccidentClaimDAL = new CarAccidentClaimDAL();
                downloadCarAccidentFormViewHolder.privateSettleCarAccidentDownloadBtn.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        selectedTripActivity.showProgressDialog("Downloading form... ").show();
                        carAccidentClaimDAL.downloadCarAccidentClaimFrom(carAccidentClaimModel.getFileName()).addOnProgressListener(new OnProgressListener<FileDownloadTask.TaskSnapshot>() {
                            @Override
                            public void onProgress(FileDownloadTask.TaskSnapshot taskSnapshot) {
                                int bytesTransferred = (int) taskSnapshot.getBytesTransferred();
                                int totalBytes = (int) taskSnapshot.getTotalByteCount();
                                int progress = (100 * bytesTransferred) / totalBytes;
                                selectedTripActivity.setProgressDialogMessage("Downloading form..." + progress + "%");
                            }
                        }).addOnSuccessListener(new OnSuccessListener<FileDownloadTask.TaskSnapshot>() {
                            @Override
                            public void onSuccess(FileDownloadTask.TaskSnapshot taskSnapshot) {
                                Toast.makeText(context, "Download successfully", Toast.LENGTH_SHORT).show();
                                selectedTripActivity.hideProgressDialog();
                            }
                        }).addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {

                            }
                        });
                    }
                });
                break;
            case ALL_APPROVED_REPAIR_SHOP:
                AllApprovedRepairShopFormViewHolder allApprovedRepairShopFormViewHolder = (AllApprovedRepairShopFormViewHolder) holder;
                break;
        }
    }

    @Override
    public long getItemId(int position) {
        return super.getItemId(position);
    }


    @Override
    public int getItemViewType(int position) {
        return mDataSetTypes[position];
    }

    @Override
    public int getItemCount() {
        return mDataSet.length;
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        public ViewHolder(View v) {
            super(v);
        }
    }


    public class DirectionViewHolder extends ViewHolder {
        private TextView selectedTripFromTextView;
        private TextView selectedTripToTextView;

        public DirectionViewHolder(View v) {
            super(v);
            selectedTripFromTextView = (TextView) v.findViewById(R.id.selectedTripFromTextView);
            selectedTripToTextView = (TextView) v.findViewById(R.id.selectedTripToTextView);
        }
    }

    public class SafetyIndexViewHolder extends ViewHolder {
        private TextView selectedTripTripSafetyIndexTxtView;
        private RelativeLayout selectedTripTripSafetyIndexLayout;
        public SafetyIndexViewHolder(View v) {
            super(v);
            selectedTripTripSafetyIndexTxtView = (TextView) v.findViewById(R.id.selectedTripTripSafetyIndexTxtView);
            selectedTripTripSafetyIndexLayout = (RelativeLayout)v.findViewById(R.id.selectedTripTripSafetyIndexLayout);
            selectedTripTripSafetyIndexLayout.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent i = new Intent(context, TripSafetyIndexDetailsActivity.class);
                    i.putExtra("tripModel", tripModel);
                    context.startActivity(i);
                }
            });
        }
    }

    public class DistanceViewHolder extends ViewHolder {
        private TextView selectedTripDistTxtView;

        public DistanceViewHolder(View v) {
            super(v);
            selectedTripDistTxtView = (TextView) v.findViewById(R.id.selectedTripDistTxtView);
        }
    }

    public class AvgSpeedViewHolder extends ViewHolder {
        private TextView selectedTripAvgSpeedTxtView;

        public AvgSpeedViewHolder(View v) {
            super(v);
            selectedTripAvgSpeedTxtView = (TextView) v.findViewById(R.id.selectedTripAvgSpeedTxtView);
        }
    }

    public class VigorousTurnViewHolder extends ViewHolder {
        private TextView selectedTripVigorousTurnView;

        public VigorousTurnViewHolder(View v) {
            super(v);
            selectedTripVigorousTurnView = (TextView) v.findViewById(R.id.selectedTripVigorousTurnView);
        }
    }

    public class SpeedingCountViewHolder extends ViewHolder {
        private TextView selectedTripSpeedCountTxtView;

        public SpeedingCountViewHolder(View v) {
            super(v);
            selectedTripSpeedCountTxtView = (TextView) v.findViewById(R.id.selectedTripSpeedCountTxtView);
        }
    }

    public class GeneratePrivateSettlementFormViewHolder extends ViewHolder {
        private Button selectedTripPrivateSettlementBtn;

        public GeneratePrivateSettlementFormViewHolder(View v) {
            super(v);
            selectedTripPrivateSettlementBtn = (Button) v.findViewById(R.id.selectedTripPrivateSettlementBtn);
            selectedTripPrivateSettlementBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent i = new Intent(context, PrivateSettlementActivity.class);
                    i.putExtra("tripModel", tripModel);
                    i.putExtra("endAddress", endAddress);
                    context.startActivity(i);
                }
            });
        }
    }

    public class GenerateCarAccidentFormViewHolder extends ViewHolder {
        private Button selectedTripCarAccidentBtn;

        public GenerateCarAccidentFormViewHolder(View v) {
            super(v);
            selectedTripCarAccidentBtn = (Button) v.findViewById(R.id.selectedTripCarAccidentBtn);
            selectedTripCarAccidentBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent i = new Intent(context, CarAccidentClaimActivity.class);
                    i.putExtra("tripModel", tripModel);
                    i.putExtra("startAddress", startAddress);
                    i.putExtra("googleMapSnapShot", tripModel.getGoogleMapSnapShot());
                    i.putExtra("endAddress", endAddress);
                    context.startActivity(i);
                }
            });
        }
    }

    public class DownloadPrivateSettlementFormViewHolder extends ViewHolder {
        private Button privateSettlePrivateSettlementDownloadBtn;

        public DownloadPrivateSettlementFormViewHolder(View v) {
            super(v);
            privateSettlePrivateSettlementDownloadBtn = (Button) v.findViewById(R.id.privateSettlePrivateSettlementDownloadBtn);
        }
    }

    public class DownloadCarAccidentFormViewHolder extends ViewHolder {
        private Button privateSettleCarAccidentDownloadBtn;

        public DownloadCarAccidentFormViewHolder(View v) {
            super(v);
            privateSettleCarAccidentDownloadBtn = (Button) v.findViewById(R.id.privateSettleCarAccidentDownloadBtn);
        }
    }

    public class AllApprovedRepairShopFormViewHolder extends ViewHolder {
        private Button selectedTripAllApprovedRepairShopBtn;

        public AllApprovedRepairShopFormViewHolder(View v) {
            super(v);
            selectedTripAllApprovedRepairShopBtn = (Button) v.findViewById(R.id.selectedTripAllApprovedRepairShopBtn);
            selectedTripAllApprovedRepairShopBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent i = new Intent(context, AllApprovedRepairShopsActivity.class);
                    i.putExtra("tripModel", tripModel);
                    context.startActivity(i);
                }
            });
        }
    }
}
