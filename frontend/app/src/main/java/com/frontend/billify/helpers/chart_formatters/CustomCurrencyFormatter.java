package com.frontend.billify.helpers.chart_formatters;

import com.github.mikephil.charting.formatter.ValueFormatter;

import java.text.DecimalFormat;

public class CustomCurrencyFormatter extends ValueFormatter {
    protected DecimalFormat format;

    public CustomCurrencyFormatter() {
        format = new DecimalFormat("###,###,##0.00");
    }

    @Override
    public String getFormattedValue(float value) {

        // Don't show values less than 70
        if (value <= 70) {
            return "";
        }

        return "$" + format.format(value);
    }
}
