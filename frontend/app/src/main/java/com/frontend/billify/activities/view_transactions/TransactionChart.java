package com.frontend.billify.activities.view_transactions;

import android.graphics.Color;
import android.util.Pair;

import com.frontend.billify.design_patterns.chart_render_strategies.ChartRenderStrategy;
import com.frontend.billify.design_patterns.observer.Observer;
import com.frontend.billify.models.Label;
import com.github.mikephil.charting.animation.Easing;
import com.github.mikephil.charting.charts.PieChart;
import com.github.mikephil.charting.data.PieData;
import com.github.mikephil.charting.data.PieDataSet;
import com.github.mikephil.charting.data.PieEntry;

import java.util.ArrayList;
import java.util.Map;

public class TransactionChart extends Observer {
    private final PieChart chart;
    private final Map<Integer, Label> transactionLabelMap;
    private final Map<Integer, Pair<Label, Float>> labelTotalMap;
    private ChartRenderStrategy renderStrategy;

    public TransactionChart(PieChart chart, Map<Integer, Label> transactionLabelMap,
                            Map<Integer, Pair<Label, Float>> labelTotalMap,
                            ChartRenderStrategy renderStrategy) {
        this.chart = chart;
        this.transactionLabelMap = transactionLabelMap;
        this.labelTotalMap = labelTotalMap;
        this.renderStrategy = renderStrategy;

        removeLabelsWithZeroTotal();
        initPieChart();
        showPieChart();
    }

    private void removeLabelsWithZeroTotal() {
        for (Integer lid: labelTotalMap.keySet()) {
            if (labelTotalMap.get(lid).second == 0) {
                labelTotalMap.remove(lid);
            }
        }
    }

    private void initPieChart(){
        //remove the description label on the lower left corner, default true if not set
        chart.getDescription().setEnabled(false);

        //adding animation so the entries pop up from 0 degree
        chart.animateY(1400, Easing.EaseInOutQuad);

        // set legend
        chart.getLegend().setTextSize(12f);
        chart.getLegend().setWordWrapEnabled(true);

        // set display for label names
        chart.setEntryLabelColor(Color.BLACK);
        chart.setEntryLabelTextSize(10f);
    }

    private void showPieChart(){
        // entry data
        ArrayList<PieEntry> pieEntries = new ArrayList<>();
        // colors for the entries
        ArrayList<Integer> colors = new ArrayList<>();

        for (Pair<Label, Float> entry: labelTotalMap.values()) {
            colors.add(Color.parseColor(entry.first.getLabel_color()));
            pieEntries.add(new PieEntry(entry.second, entry.first.getLabel_name()));
        }

        //collecting the entries with label name
        PieDataSet pieDataSet = new PieDataSet(pieEntries, "");
        //setting text size of the value
        pieDataSet.setValueTextSize(12f);
        //providing color list for coloring different entries
        pieDataSet.setColors(colors);
        //set label names outside of chart
        pieDataSet.setXValuePosition(PieDataSet.ValuePosition.OUTSIDE_SLICE);
        //grouping the data set from entry to chart
        PieData pieData = new PieData(pieDataSet);

        runRenderStrategy(pieData);
        chart.setData(pieData);

        // Refresh chart
        chart.invalidate();
    }

    private void runRenderStrategy(PieData pieData) {
        if (renderStrategy != null) {
            renderStrategy.renderChart(chart, pieData);
        }
    }

    public void setRenderStrategy(ChartRenderStrategy renderStrategy) {
        this.renderStrategy = renderStrategy;
        showPieChart();
    }

    @Override
    public void Update() {}

    @Override
    public void Update(Object o) {
        Label newLabel = (Label) o;
        Label oldLabel = transactionLabelMap.get(newLabel.getTId());

        transactionLabelMap.put(newLabel.getTId(), newLabel);

        int labelLid = oldLabel.getLId();
        float transactionTotal = oldLabel.getTransaction_total();
        float labelTotal = labelTotalMap.get(labelLid).second;

        // Lower count of old label and remove it if its total <= 0
        if (labelTotal > transactionTotal) {
            labelTotalMap.put(labelLid, new Pair<>(oldLabel, labelTotal - transactionTotal));
        } else {
            labelTotalMap.remove(labelLid);
        }

        labelLid = newLabel.getLId();
        transactionTotal = newLabel.getTransaction_total();

        // Add total to new label or set it if it's not currently displayed
        if (labelTotalMap.get(labelLid) != null) {
            labelTotal = labelTotalMap.get(labelLid).second + transactionTotal;
        } else {
            labelTotal = transactionTotal;
        }

        labelTotalMap.put(labelLid, new Pair<>(newLabel, labelTotal));
        showPieChart();
    }
}
