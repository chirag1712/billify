package com.frontend.billify.activities.view_transactions;

import android.graphics.Color;
import android.graphics.Typeface;
import android.util.Pair;

import com.frontend.billify.design_patterns.chart_render_strategies.ChartRenderStrategy;
import com.frontend.billify.design_patterns.observer.Observer;
import com.frontend.billify.models.Label;
import com.frontend.billify.models.UserTransaction;
import com.github.mikephil.charting.animation.Easing;
import com.github.mikephil.charting.charts.PieChart;
import com.github.mikephil.charting.data.PieData;
import com.github.mikephil.charting.data.PieDataSet;
import com.github.mikephil.charting.data.PieEntry;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Map;

public class TransactionChart extends Observer {
    private final PieChart chart;
    private final Map<Integer, UserTransaction> tidToUserTransactionMap;
    private Map<Integer, Pair<Label, Float>> labelTotalMap;
    private ChartRenderStrategy renderStrategy;

    public TransactionChart(PieChart chart, Map<Integer, UserTransaction> tidToUserTransactionMap,
                            Map<Integer, Pair<Label, Float>> labelTotalMap,
                            ChartRenderStrategy renderStrategy) {
        this.chart = chart;
        this.tidToUserTransactionMap = tidToUserTransactionMap;
        this.renderStrategy = renderStrategy;
        this.labelTotalMap = labelTotalMap;
        removeLabelsWithZeroTotal();
        initPieChart();
        showPieChart();
    }

    private void removeLabelsWithZeroTotal() {
        ArrayList<Integer> lidsToRemove = new ArrayList<>();
        for (Integer lid: labelTotalMap.keySet()) {
            if (labelTotalMap.get(lid).second == 0) {
                lidsToRemove.add(lid);
            }
        }

        for (Integer lid: lidsToRemove) {
            labelTotalMap.remove(lid);
        }
    }

    private void initPieChart(){
        // remove the description label on the lower left corner, default true if not set
        chart.getDescription().setEnabled(false);

        // adding animation so the entries pop up from 0 degree
        chart.animateY(1400, Easing.EaseInOutQuad);

        // set legend
        chart.getLegend().setTextSize(12f);
        chart.getLegend().setWordWrapEnabled(true);

        // set display for label names
        chart.setEntryLabelColor(Color.BLACK);
        chart.setEntryLabelTextSize(10f);
        chart.setCenterText("Spending by Label");
        chart.setCenterTextTypeface(Typeface.DEFAULT_BOLD);
        chart.setCenterTextSize(18f);
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
        /*
        newTempTransactionWithNewLabel is just a copy of the Transaction whose label was updated via
        the dropdown. It has the updated new label, whereas oldTransaction is the same
        transaction as newTempTransactionWithNewLabel, but with the the old label that has to be
        updated.
        */
        UserTransaction newTempTransactionWithNewLabel = (UserTransaction) o;
        UserTransaction oldTransaction = tidToUserTransactionMap.get(newTempTransactionWithNewLabel.getTid());

        // update old label's total transaction amount by decrementing it by deselected transaction
        Label oldLabel = oldTransaction.getLabel();
        int oldLabelLid = oldLabel.getLId();
        float oldTransactionPrice = oldTransaction.getPrice_share();
        float labelTotal = labelTotalMap.get(oldLabelLid).second;
        // Lower count of old label and remove it if its total <= 0
        if (labelTotal > oldTransactionPrice) {
            labelTotalMap.put(oldLabelLid, new Pair<>(oldLabel, labelTotal - oldTransactionPrice));
        } else {
            labelTotalMap.remove(oldLabelLid);
        }

        // update new label's total transaction amount by adding old
        Label newLabel = newTempTransactionWithNewLabel.getLabel();
        int newLabelLid = newLabel.getLId();
        float newTransactionPrice = oldTransaction.getPrice_share();
        // Add total to new label or set it if it's not currently displayed
        if (labelTotalMap.get(newLabelLid) != null) {
            labelTotal = labelTotalMap.get(newLabelLid).second + newTransactionPrice;
        } else {
            labelTotal = newTransactionPrice;
        }
        labelTotalMap.put(newLabelLid, new Pair<>(newLabel, labelTotal));
        oldTransaction.setLabel(newLabel);
        showPieChart();
    }
}
