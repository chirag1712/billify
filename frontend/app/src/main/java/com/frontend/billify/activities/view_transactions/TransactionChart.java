package com.frontend.billify.activities.view_transactions;

import android.graphics.Color;
import android.util.Pair;

import com.frontend.billify.design_patterns.observer.Observer;
import com.frontend.billify.models.Label;
import com.github.mikephil.charting.animation.Easing;
import com.github.mikephil.charting.charts.PieChart;
import com.github.mikephil.charting.data.PieData;
import com.github.mikephil.charting.data.PieDataSet;
import com.github.mikephil.charting.data.PieEntry;
import com.github.mikephil.charting.formatter.PercentFormatter;

import java.util.ArrayList;
import java.util.Map;

public class TransactionChart extends Observer {
    private final PieChart chart;
    private final Map<Integer, Label> transactionLabelMap;
    private final Map<Integer, Pair<Label, Integer>> labelCountMap;

    public TransactionChart(PieChart chart, Map<Integer, Label> transactionLabelMap,
                            Map<Integer, Pair<Label, Integer>> labelCountMap) {
        this.chart = chart;
        this.transactionLabelMap = transactionLabelMap;
        this.labelCountMap = labelCountMap;
        removeLabelsWithZeroCount();
        initPieChart();
        showPieChart();
    }

    private void removeLabelsWithZeroCount() {
        for (Integer lid: labelCountMap.keySet()) {
            if (labelCountMap.get(lid).second == 0) {
                labelCountMap.remove(lid);
            }
        }
    }

    private void initPieChart(){
        // Convert values to percentage
        chart.setUsePercentValues(true);

        //remove the description label on the lower left corner, default true if not set
        chart.getDescription().setEnabled(false);

        //adding animation so the entries pop up from 0 degree
        chart.animateY(1400, Easing.EaseInOutQuad);

        // set text size of legend
        chart.getLegend().setTextSize(12f);
        chart.getLegend().setWordWrapEnabled(true);
    }

    private void showPieChart(){

        // entry data
        ArrayList<PieEntry> pieEntries = new ArrayList<>();
        // colors for the entries
        ArrayList<Integer> colors = new ArrayList<>();

        for (Pair<Label, Integer> entry: labelCountMap.values()) {
            colors.add(Color.parseColor(entry.first.getLabel_color()));
            pieEntries.add(new PieEntry(entry.second, entry.first.getLabel_name()));
        }

        //collecting the entries with label name
        PieDataSet pieDataSet = new PieDataSet(pieEntries, "");
        //setting text size of the value
        pieDataSet.setValueTextSize(12f);
        //providing color list for coloring different entries
        pieDataSet.setColors(colors);
        //grouping the data set from entry to chart
        PieData pieData = new PieData(pieDataSet);
        // Set values to be displayed with percent sign
        pieData.setValueFormatter(new PercentFormatter(chart));

        chart.setData(pieData);

        // Refresh chart
        chart.invalidate();
    }

    @Override
    public void Update() {}

    @Override
    public void Update(Object o) {
        Label newLabel = (Label) o;
        Label oldLabel = transactionLabelMap.get(newLabel.getTId());

        transactionLabelMap.put(newLabel.getTId(), newLabel);

        int labelLid = oldLabel.getLId();
        Integer labelCount = labelCountMap.get(labelLid).second;

        // Lower count of old label and remove it if its count is 0
        if (labelCount > 1) {
            labelCountMap.put(labelLid, new Pair<>(oldLabel, labelCount - 1));
        } else {
            labelCountMap.remove(labelLid);
        }

        labelLid = newLabel.getLId();

        // Add count of new label or add it if it's not currently displayed
        if (labelCountMap.get(labelLid) != null) {
            labelCount = labelCountMap.get(labelLid).second + 1;

        } else {
            labelCount = 1;
        }

        labelCountMap.put(labelLid, new Pair<>(newLabel, labelCount));
        showPieChart();
    }
}
