package com.fypj.icreative.controller;

import android.content.Context;
import android.support.v4.content.ContextCompat;

import com.fypj.icreative.R;
import com.fypj.icreative.model.TripModel;
import com.fypj.icreative.utils.Utils;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.data.CombinedData;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.formatter.ValueFormatter;
import com.github.mikephil.charting.interfaces.datasets.IBarDataSet;
import com.github.mikephil.charting.interfaces.datasets.ILineDataSet;
import com.github.mikephil.charting.utils.ViewPortHandler;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

public class ChartController {
    private Utils utils = new Utils();
    public static final DecimalFormat moneyFormat = new DecimalFormat("###,###,##0.00");
    public static final DecimalFormat kmPerHrFormat = new DecimalFormat("###,###,##0");
    private Context context;

    public ChartController(Context context) {
        this.context = context;
    }

    public List<List<TripModel>> getSortedTripModelList(List<TripModel> tripModelList, String filterType) {
        List<List<TripModel>> sortedTripModelList = new ArrayList<List<TripModel>>();
        List<TripModel> TripModeLastFourDateList = new ArrayList<TripModel>();
        List<TripModel> TripModeLastThreeDateList = new ArrayList<TripModel>();
        List<TripModel> TripModeLastTwoDateList = new ArrayList<TripModel>();
        List<TripModel> TripModeLastDateList = new ArrayList<TripModel>();
        Long[] lastFourDateArr = {};
        switch (filterType) {
            case "DAY":
                lastFourDateArr = getLastFourDaysDate();
                break;
            case "WEEK":
                lastFourDateArr = getLastFourWeeksDate();
                break;
            case "MONTH":
                lastFourDateArr = getLastFourMonthsDate();
                break;
        }
        for (TripModel tripModel : tripModelList) {
            if (tripModel.getDateCreated() >= lastFourDateArr[0] && tripModel.getDateCreated() <= lastFourDateArr[1]) {
                TripModeLastFourDateList.add(tripModel);
            } else if (tripModel.getDateCreated() >= lastFourDateArr[1] && tripModel.getDateCreated() <= lastFourDateArr[2]) {
                TripModeLastThreeDateList.add(tripModel);
            } else if (tripModel.getDateCreated() >= lastFourDateArr[2] && tripModel.getDateCreated() <= lastFourDateArr[3]) {
                TripModeLastTwoDateList.add(tripModel);
            } else if (tripModel.getDateCreated() >= lastFourDateArr[3] && tripModel.getDateCreated() <= lastFourDateArr[4]) {
                TripModeLastDateList.add(tripModel);
            }
        }
        sortedTripModelList.add(TripModeLastFourDateList);
        sortedTripModelList.add(TripModeLastThreeDateList);
        sortedTripModelList.add(TripModeLastTwoDateList);
        sortedTripModelList.add(TripModeLastDateList);

        return sortedTripModelList;
    }

    private Long[] getLastFourDaysDate() {
        Calendar calendar = Calendar.getInstance();
        utils.setTimeToEndOfDay(calendar);
        calendar.add(Calendar.DAY_OF_MONTH, -4);
        long lastFourDay = calendar.getTimeInMillis();
        calendar.add(Calendar.DAY_OF_MONTH, 1);
        long lastThirdDay = calendar.getTimeInMillis();
        calendar.add(Calendar.DAY_OF_MONTH, 1);
        long lastSecondDay = calendar.getTimeInMillis();
        calendar.add(Calendar.DAY_OF_MONTH, 1);
        long lastDay = calendar.getTimeInMillis();
        calendar.add(Calendar.DAY_OF_MONTH, 1);
        long thisDay = calendar.getTimeInMillis();
        return new Long[]{lastFourDay, lastThirdDay, lastSecondDay, lastDay, thisDay};
    }

    private Long[] getLastFourWeeksDate() {
        Calendar calendar = Calendar.getInstance();
        utils.setTimeToEndOfDay(calendar);
        calendar.add(Calendar.WEEK_OF_YEAR, -4);
        long lastFourWk = calendar.getTimeInMillis();
        calendar.add(Calendar.WEEK_OF_YEAR, 1);
        long lastThirdWk = calendar.getTimeInMillis();
        calendar.add(Calendar.WEEK_OF_YEAR, 1);
        long lastSecondWk = calendar.getTimeInMillis();
        calendar.add(Calendar.WEEK_OF_YEAR, 1);
        long lastWk = calendar.getTimeInMillis();
        calendar.add(Calendar.WEEK_OF_YEAR, 1);
        long thisWk = calendar.getTimeInMillis();
        return new Long[]{lastFourWk, lastThirdWk, lastSecondWk, lastWk, thisWk};
    }

    private Long[] getLastFourMonthsDate() {
        Calendar calendar = Calendar.getInstance();
        utils.setTimeToEndOfDay(calendar);
        calendar.add(Calendar.MONTH, -4);
        long lastFourMth = calendar.getTimeInMillis();
        calendar.add(Calendar.MONTH, 1);
        long lastThirdMth = calendar.getTimeInMillis();
        calendar.add(Calendar.MONTH, 1);
        long lastSecondMth = calendar.getTimeInMillis();
        calendar.add(Calendar.MONTH, 1);
        long lastMth = calendar.getTimeInMillis();
        calendar.add(Calendar.MONTH, 1);
        long thisMth = calendar.getTimeInMillis();
        return new Long[]{lastFourMth, lastThirdMth, lastSecondMth, lastMth, thisMth};
    }

    public BarData setChartSafetyIndexHrznBarChartData(List<TripModel> tripModelList) {
        ArrayList<BarEntry> yVals1 = new ArrayList<BarEntry>();
        ArrayList<String> xVals = new ArrayList<String>();
        for (int i = 0; i < tripModelList.size(); i++) {
            TripModel tripModel = tripModelList.get(i);
            String date = utils.fromMiliSecToDateString(tripModel.getDateCreated(), "dd MMM");
            xVals.add(date);
            yVals1.add(new BarEntry((float) tripModel.getCurrentTripSafetyIndex(), i));
        }
        BarDataSet set = new BarDataSet(yVals1, "Safety Index");
        set.setColor(ContextCompat.getColor(context, R.color.bar_chart_color));
        set.setDrawValues(true);
        set.setValueTextSize(10f);
        set.setValueTextColor(ContextCompat.getColor(context, R.color.primary_text));
        ArrayList<IBarDataSet> dataSets = new ArrayList<IBarDataSet>();
        dataSets.add(set);
        BarData data = new BarData(xVals, dataSets);
        data.setValueTextSize(10f);
        data.setValueTextColor(ContextCompat.getColor(context, R.color.primary_text));
        return data;
    }

    public CombinedData setChartDistanceTravelledChartChartData(List<List<TripModel>> weeklySortedTripModelList, String filterType) {
        List<TripModel> TripModeLastFourDateList = weeklySortedTripModelList.get(0);
        List<TripModel> TripModeLastThreeDateList = weeklySortedTripModelList.get(1);
        List<TripModel> TripModeLastTwoDateList = weeklySortedTripModelList.get(2);
        List<TripModel> TripModeLastDateList = weeklySortedTripModelList.get(3);
        Long[] lastFourDateArr = {};
        switch (filterType) {
            case "DAY":
                lastFourDateArr = getLastFourDaysDate();
                break;
            case "WEEK":
                lastFourDateArr = getLastFourWeeksDate();
                break;
            case "MONTH":
                lastFourDateArr = getLastFourMonthsDate();
                break;
        }
        String lastFourDate = utils.fromMiliSecToDateString(lastFourDateArr[0], "dd MMM");
        float lastFourWkDistance = 0;
        float lastFourWkSpeedingCount = 0;
        float lastFourWkSharpTurnCount = 0;
        for (TripModel tripModel : TripModeLastFourDateList) {
            lastFourWkDistance += tripModel.getDistanceTravelled() / 1000;
            lastFourWkSpeedingCount += tripModel.getSpeedingCount();
            lastFourWkSharpTurnCount += tripModel.getVigorousTurnCount();
        }

        String lastThreeDate = utils.fromMiliSecToDateString(lastFourDateArr[1], "dd MMM");
        float lastThreeWkDistance = 0;
        float lastThreeWkSpeedingCount = 0;
        float lastThreeWkSharpTurnCount = 0;
        for (TripModel tripModel : TripModeLastThreeDateList) {
            lastThreeWkDistance += tripModel.getDistanceTravelled() / 1000;
            lastThreeWkSpeedingCount += tripModel.getSpeedingCount();
            lastThreeWkSharpTurnCount += tripModel.getVigorousTurnCount();

        }

        String lastTwoDate = utils.fromMiliSecToDateString(lastFourDateArr[2], "dd MMM");
        float lastTwoWkDistance = 0;
        float lastTwoWkSpeedingCount = 0;
        float lastTwoWkSharpTurnCount = 0;
        for (TripModel tripModel : TripModeLastTwoDateList) {
            lastTwoWkDistance += tripModel.getDistanceTravelled() / 1000;
            lastTwoWkSpeedingCount += tripModel.getSpeedingCount();
            lastTwoWkSharpTurnCount += tripModel.getVigorousTurnCount();
        }

        String lastDate = utils.fromMiliSecToDateString(lastFourDateArr[3], "dd MMM");
        float lastWkDistance = 0;
        float lastWkSpeedingCount = 0;
        float lastWkSharpTurnCount = 0;
        for (TripModel tripModel : TripModeLastDateList) {
            lastWkDistance += tripModel.getDistanceTravelled() / 1000;
            lastWkSpeedingCount += tripModel.getSpeedingCount();
            lastWkSharpTurnCount += tripModel.getVigorousTurnCount();
        }

        String[] lastFourWkDatesArr = {lastFourDate, lastThreeDate, lastTwoDate, lastDate};
        ArrayList<BarEntry> barChartEntries = new ArrayList<BarEntry>();
        barChartEntries.add(new BarEntry(lastFourWkDistance, 0));
        barChartEntries.add(new BarEntry(lastThreeWkDistance, 1));
        barChartEntries.add(new BarEntry(lastTwoWkDistance, 2));
        barChartEntries.add(new BarEntry(lastWkDistance, 3));
        BarData barData = setBarChartDataForDistanceTravelledChart(barChartEntries);

        ArrayList<Entry> lineChartEntries = new ArrayList<Entry>();
        lineChartEntries.add(new Entry(lastFourWkSpeedingCount, 0));
        lineChartEntries.add(new Entry(lastThreeWkSpeedingCount, 1));
        lineChartEntries.add(new Entry(lastTwoWkSpeedingCount, 2));
        lineChartEntries.add(new Entry(lastWkSpeedingCount, 3));
        ArrayList<Entry> lineChartEntries2 = new ArrayList<Entry>();
        lineChartEntries2.add(new Entry(lastFourWkSharpTurnCount, 0));
        lineChartEntries2.add(new Entry(lastThreeWkSharpTurnCount, 1));
        lineChartEntries2.add(new Entry(lastTwoWkSharpTurnCount, 2));
        lineChartEntries2.add(new Entry(lastWkSharpTurnCount, 3));
        LineData lineData = setLineChartDataForDistanceTravelledChart(lineChartEntries, lineChartEntries2);


        CombinedData combinedData = new CombinedData(lastFourWkDatesArr);
        combinedData.setData(barData);
        combinedData.setData(lineData);
        return combinedData;
    }

    private LineData setLineChartDataForDistanceTravelledChart(ArrayList<Entry> lineChartEntries, ArrayList<Entry> lineChartEntries2) {
        LineDataSet set1 = new LineDataSet(lineChartEntries, "Speeding Count");
        set1.setColor(ContextCompat.getColor(context, R.color.line_chart_color));
        set1.setLineWidth(2.5f);
        set1.setCircleColor(ContextCompat.getColor(context, R.color.line_chart_circle_color));
        set1.setCircleRadius(5f);
        set1.setDrawValues(true);
        set1.setValueTextSize(10f);
        set1.setValueTextColor(ContextCompat.getColor(context, R.color.primary_text));
        set1.setAxisDependency(YAxis.AxisDependency.LEFT);


        LineDataSet set2 = new LineDataSet(lineChartEntries2, "Sharp Turn Count");
        set2.setColor(ContextCompat.getColor(context, R.color.line_chart_color2));
        set2.setLineWidth(2.5f);
        set2.setCircleColor(ContextCompat.getColor(context, R.color.line_chart_circle_color2));
        set2.setCircleRadius(5f);
        set2.setDrawValues(true);
        set2.setValueTextSize(10f);
        set2.setValueTextColor(ContextCompat.getColor(context, R.color.primary_text));
        set2.setAxisDependency(YAxis.AxisDependency.LEFT);

        LineData d = new LineData();
        d.addDataSet(set1);
        d.addDataSet(set2);
        return d;
    }

    private LineData setLineChartData2ForDistanceTravelledChart(ArrayList<Entry> lineChartEntries) {
        LineData d = new LineData();
        LineDataSet set = new LineDataSet(lineChartEntries, "Sharp Turn Count");
        set.setColor(ContextCompat.getColor(context, R.color.line_chart_color));
        set.setLineWidth(2.5f);
        set.setCircleColor(ContextCompat.getColor(context, R.color.line_chart_circle_color));
        set.setCircleRadius(5f);
        set.setDrawValues(true);
        set.setValueTextSize(10f);
        set.setValueTextColor(ContextCompat.getColor(context, R.color.primary_text));
        d.addDataSet(set);
        return d;
    }

    private BarData setBarChartDataForDistanceTravelledChart(ArrayList<BarEntry> barChartEntries) {
        BarData d = new BarData();
        BarDataSet set = new BarDataSet(barChartEntries, "Distance Travelled");
        set.setColor(ContextCompat.getColor(context, R.color.bar_chart_color));
        set.setDrawValues(true);
        set.setValueTextSize(10f);
        set.setValueTextColor(ContextCompat.getColor(context, R.color.primary_text));
        set.setValueFormatter(new ValueFormatter() {
            @Override
            public String getFormattedValue(float value, Entry entry, int dataSetIndex, ViewPortHandler viewPortHandler) {
                return kmPerHrFormat.format(value) + "km";
            }
        });
        d.addDataSet(set);
        return d;
    }

    public LineData setChartAverageSpeedChartData(List<List<TripModel>> weeklySortedTripModelList, String filterType) {
        List<TripModel> TripModeLastFourDateList = weeklySortedTripModelList.get(0);
        List<TripModel> TripModeLastThreeDateList = weeklySortedTripModelList.get(1);
        List<TripModel> TripModeLastTwoDateList = weeklySortedTripModelList.get(2);
        List<TripModel> TripModeLastDateList = weeklySortedTripModelList.get(3);
        Long[] lastFourDateArr = {};
        switch (filterType) {
            case "DAY":
                lastFourDateArr = getLastFourDaysDate();
                break;
            case "WEEK":
                lastFourDateArr = getLastFourWeeksDate();
                break;
            case "MONTH":
                lastFourDateArr = getLastFourMonthsDate();
                break;
        }
        String lastFourDate = utils.fromMiliSecToDateString(lastFourDateArr[0], "dd MMM");
        float lastFourSpeed = 0;
        int numOfLastFourData = 0;
        for (TripModel tripModel : TripModeLastFourDateList) {
            lastFourSpeed += tripModel.getAvgSpeed();
            numOfLastFourData++;
        }
        lastFourSpeed = lastFourSpeed / numOfLastFourData;

        String lastThreeDate = utils.fromMiliSecToDateString(lastFourDateArr[1], "dd MMM");
        float lastThreeSpeed = 0;
        int numOfLastThreeData = 0;
        for (TripModel tripModel : TripModeLastThreeDateList) {
            lastThreeSpeed += tripModel.getAvgSpeed();
            numOfLastThreeData++;
        }
        lastThreeSpeed = lastThreeSpeed / numOfLastThreeData;


        String lastTwoDate = utils.fromMiliSecToDateString(lastFourDateArr[2], "dd MMM");
        float lastTwoSpeed = 0;
        int numOfLastTwoData = 0;
        for (TripModel tripModel : TripModeLastTwoDateList) {
            lastTwoSpeed += tripModel.getAvgSpeed();
            numOfLastTwoData++;
        }
        lastTwoSpeed = lastTwoSpeed / numOfLastTwoData;


        String lastDate = utils.fromMiliSecToDateString(lastFourDateArr[3], "dd MMM");
        float lastSpeed = 0;
        int numOfLastData = 0;
        for (TripModel tripModel : TripModeLastDateList) {
            lastSpeed += tripModel.getAvgSpeed();
            numOfLastData++;
        }
        lastSpeed = lastSpeed / numOfLastData;

        String[] lastFourDatesArr = {lastFourDate, lastThreeDate, lastTwoDate, lastDate};
        ArrayList<Entry> yVals = new ArrayList<Entry>();
        yVals.add(new Entry(lastFourSpeed, 0));
        yVals.add(new Entry(lastThreeSpeed, 1));
        yVals.add(new Entry(lastTwoSpeed, 2));
        yVals.add(new Entry(lastSpeed, 3));
        LineDataSet lineDataSet = generateILineDSForAvgSpeedChart(yVals);
        LineData lineData = new LineData(lastFourDatesArr, lineDataSet);
        lineData.setValueTextSize(10f);
        return lineData;
    }

    private LineDataSet generateILineDSForAvgSpeedChart(ArrayList<Entry> yVals) {
        LineDataSet set1 = new LineDataSet(yVals, "Speed (km/h)");
        set1.setAxisDependency(YAxis.AxisDependency.LEFT);
        set1.setColor(ContextCompat.getColor(context, R.color.line_chart_color));
        set1.setLineWidth(2.5f);
        set1.setCircleColor(ContextCompat.getColor(context, R.color.line_chart_circle_color));
        set1.setCircleRadius(5f);
        set1.setDrawValues(true);
        set1.setValueTextSize(10f);
        set1.setValueFormatter(new ValueFormatter() {
            @Override
            public String getFormattedValue(float value, Entry entry, int dataSetIndex, ViewPortHandler viewPortHandler) {
                return kmPerHrFormat.format(value) + "km/h";
            }
        });
        set1.setValueTextColor(ContextCompat.getColor(context, R.color.primary_text));
        return set1;
    }
}
