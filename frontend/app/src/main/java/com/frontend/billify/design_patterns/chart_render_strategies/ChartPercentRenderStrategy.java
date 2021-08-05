package com.frontend.billify.design_patterns.chart_render_strategies;

import com.frontend.billify.helpers.chart_formatters.CustomPercentFormatter;
import com.github.mikephil.charting.charts.PieChart;
import com.github.mikephil.charting.data.PieData;

public class ChartPercentRenderStrategy implements ChartRenderStrategy {
    @Override
    public void renderChart(PieChart chart, PieData data) {
        chart.setUsePercentValues(true);
        data.setValueFormatter(new CustomPercentFormatter());
    }
}
