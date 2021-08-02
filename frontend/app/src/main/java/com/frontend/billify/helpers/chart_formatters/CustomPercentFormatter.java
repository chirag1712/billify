package com.frontend.billify.helpers.chart_formatters;

import com.github.mikephil.charting.formatter.ValueFormatter;

import java.text.DecimalFormat;

public class CustomPercentFormatter extends ValueFormatter {

    protected DecimalFormat format;

    public CustomPercentFormatter() {
        format = new DecimalFormat("###,###,##0.0");
    }

    @Override
    public String getFormattedValue(float value) {

        // Don't show values less than 5%
        if (value <= 5) {
            return "";
        }

        return format.format(value) + "%";
    }
}
