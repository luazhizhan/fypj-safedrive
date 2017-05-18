package com.fypj.icreative.adapter;

import android.content.Context;
import android.graphics.Color;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.fypj.icreative.R;
import com.fypj.icreative.controller.ChartController;
import com.fypj.icreative.model.TripModel;
import com.github.mikephil.charting.charts.CombinedChart;
import com.github.mikephil.charting.charts.HorizontalBarChart;
import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.components.Legend;
import com.github.mikephil.charting.components.LimitLine;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.CombinedData;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.formatter.YAxisValueFormatter;

import java.util.ArrayList;
import java.util.List;

public class ChartAdapter extends RecyclerView.Adapter<ChartAdapter.ViewHolder> {
    public static final int SAFETY_INDEX = 0;
    public static final int DISTANCE_TRAVELLED = 1;
    private Context context;
    private String[] mDataSet;
    private int[] mDataSetTypes;
    private List<TripModel> tripModelList;
    private String filterType;
    private ChartController chartController;
    private List<List<TripModel>> sortedTripModelList;

    public ChartAdapter(Context context, String[] dataSet, int[] dataSetTypes, List<TripModel> tripModelList, String filterType) {
        this.context = context;
        this.mDataSet = dataSet;
        this.mDataSetTypes = dataSetTypes;
        this.tripModelList = tripModelList;
        this.filterType = filterType;
        chartController = new ChartController(context);
        sortedTripModelList = new ArrayList<List<TripModel>>();
        if (!tripModelList.isEmpty()) {
            sortedTripModelList = chartController.getSortedTripModelList(tripModelList, filterType);
        }
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v;
        if (viewType == SAFETY_INDEX) {
            v = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.chart_safety_index, parent, false);
            return new SafetyIndexViewHolder(v);
        } else  {
            v = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.chart_distance_travelled_card, parent, false);
            return new DistanceTravelledViewHolder(v);
        }
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        switch (holder.getItemViewType()) {
            case SAFETY_INDEX:
                SafetyIndexViewHolder safertIndexViewHolder = (SafetyIndexViewHolder) holder;
                safertIndexViewHolder.chartSafetyIndexHrznBarChart.clear();
                if (!sortedTripModelList.isEmpty()) {
                    List<TripModel> tripModelInsuranceAmtList = new ArrayList<>();
                    for (List<TripModel> tripModelList : sortedTripModelList) {
                        if (!tripModelList.isEmpty()) {
                            tripModelInsuranceAmtList.add(tripModelList.get(tripModelList.size() - 1));
                        }
                    }
                    BarData barData = chartController.setChartSafetyIndexHrznBarChartData(tripModelInsuranceAmtList);
                    safertIndexViewHolder.chartSafetyIndexHrznBarChart.setData(barData);
                    safertIndexViewHolder.chartSafetyIndexHrznBarChart.invalidate();
                }
                break;
            case DISTANCE_TRAVELLED:
                DistanceTravelledViewHolder distanceTravelledViewHolder = (DistanceTravelledViewHolder) holder;
                distanceTravelledViewHolder.chartDistanceTravelledChart.clear();
                if (!sortedTripModelList.isEmpty()) {
                    CombinedData combinedData = chartController.setChartDistanceTravelledChartChartData(sortedTripModelList, filterType);
                    distanceTravelledViewHolder.chartDistanceTravelledChart.setData(combinedData);
                    distanceTravelledViewHolder.chartDistanceTravelledChart.invalidate();
                }
                break;
        }
    }

    @Override
    public int getItemViewType(int position) {
        return mDataSetTypes[position];
    }

    @Override
    public int getItemCount() {
        return mDataSet.length;
    }

    public void dataChange(List<TripModel> data, String filterType) {
        this.tripModelList = data;
        if (!tripModelList.isEmpty()) {
            sortedTripModelList = chartController.getSortedTripModelList(tripModelList, filterType);
        }
        else{
            sortedTripModelList = new ArrayList<List<TripModel>>();
        }
        this.filterType = filterType;
        notifyDataSetChanged();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        public ViewHolder(View itemView) {
            super(itemView);
        }
    }

    public class SafetyIndexViewHolder extends ViewHolder {
        private HorizontalBarChart chartSafetyIndexHrznBarChart;
        private TextView chartSafetyIndexHrznBarTextView;

        public SafetyIndexViewHolder(View itemView) {
            super(itemView);
            chartSafetyIndexHrznBarTextView = (TextView) itemView.findViewById(R.id.chartSafetyIndexHrznBarTextView);
            chartSafetyIndexHrznBarChart = (HorizontalBarChart) itemView.findViewById(R.id.chartSafetyIndexHrznBarChart);
            chartSafetyIndexHrznBarChart.setDrawBarShadow(false);
            chartSafetyIndexHrznBarChart.setDrawValueAboveBar(true);
            chartSafetyIndexHrznBarChart.setDescription("");
            chartSafetyIndexHrznBarChart.setNoDataTextDescription("No data found");
            chartSafetyIndexHrznBarChart.getLegend().setTextColor(Color.WHITE);

            XAxis xl = chartSafetyIndexHrznBarChart.getXAxis();
            xl.setPosition(XAxis.XAxisPosition.BOTTOM);
            xl.setDrawAxisLine(true);
            xl.setDrawGridLines(true);
            xl.setGridLineWidth(0.3f);
            xl.setTextColor(Color.WHITE);

            YAxis yLeft = chartSafetyIndexHrznBarChart.getAxisLeft();
            yLeft.setDrawAxisLine(true);
            yLeft.setDrawGridLines(true);
            yLeft.setGridLineWidth(0.3f);
            yLeft.setTextColor(Color.WHITE);

            YAxis yRight = chartSafetyIndexHrznBarChart.getAxisRight();
            yRight.setDrawAxisLine(true);
            yRight.setDrawGridLines(false);
            yRight.setTextColor(Color.WHITE);

            chartSafetyIndexHrznBarChart.animateY(2500);
            Legend l = chartSafetyIndexHrznBarChart.getLegend();
            l.setPosition(Legend.LegendPosition.BELOW_CHART_LEFT);
            l.setFormSize(8f);
        }
    }

    public class DistanceTravelledViewHolder extends ViewHolder {
        private CombinedChart chartDistanceTravelledChart;
        private TextView chartDistanceTravelledTextView;

        public DistanceTravelledViewHolder(View itemView) {
            super(itemView);
            chartDistanceTravelledTextView = (TextView) itemView.findViewById(R.id.chartDistanceTravelledTextView);
            chartDistanceTravelledChart = (CombinedChart) itemView.findViewById(R.id.chartDistanceTravelledChart);
            chartDistanceTravelledChart.setDescription("");
            chartDistanceTravelledChart.setNoDataTextDescription("No data found");
            chartDistanceTravelledChart.setDrawBarShadow(false);
            chartDistanceTravelledChart.setDrawOrder(new CombinedChart.DrawOrder[]{
                    CombinedChart.DrawOrder.BAR, CombinedChart.DrawOrder.BUBBLE, CombinedChart.DrawOrder.CANDLE, CombinedChart.DrawOrder.LINE, CombinedChart.DrawOrder.SCATTER
            });
            chartDistanceTravelledChart.getLegend().setTextColor(Color.WHITE);

            YAxis rightAxis = chartDistanceTravelledChart.getAxisRight();
            rightAxis.setDrawGridLines(true);
            rightAxis.setAxisMinValue(0f); // this replaces setStartAtZero(true)
            rightAxis.setValueFormatter(new YAxisValueFormatter() {
                @Override
                public String getFormattedValue(float value, YAxis yAxis) {
                    return ChartController.kmPerHrFormat.format(value) + "km";
                }
            });
            rightAxis.setTextColor(Color.WHITE);

            YAxis leftAxis = chartDistanceTravelledChart.getAxisLeft();
            leftAxis.setDrawGridLines(true);
            leftAxis.setAxisMinValue(0f); // this replaces setStartAtZero(true)
            leftAxis.setTextColor(Color.WHITE);

            XAxis xAxis = chartDistanceTravelledChart.getXAxis();
            xAxis.setPosition(XAxis.XAxisPosition.BOTTOM);
            xAxis.setTextColor(Color.WHITE);
            xAxis.setDrawGridLines(false);
        }
    }
}
