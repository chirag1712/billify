package com.frontend.billify.design_patterns.chart_render_strategies;

import com.github.mikephil.charting.charts.PieChart;
import com.github.mikephil.charting.data.PieData;

public interface ChartRenderStrategy {
    void renderChart(PieChart chart, PieData data);
}
