package com.frontend.billify.adapters;

import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.core.graphics.drawable.DrawableCompat;

import com.frontend.billify.R;
import com.frontend.billify.models.Label;

import java.util.List;


public class LabelDropdownAdapter extends ArrayAdapter<Label> {
    public LabelDropdownAdapter(@NonNull Context context, @NonNull List<Label> labels) {
        super(context, 0, labels);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        Label current_label = getItem(position);
        // Check if the existing view is being reused, otherwise inflate the view
        if (convertView == null) {
            convertView = LayoutInflater.from(getContext()).inflate(
                    R.layout.dropdown_item, parent, false);
        }

        // Populate label name in dropdown list
        TextView nameTextView = convertView.findViewById(R.id.label_option);
        nameTextView.setText(current_label.getLabel_name());

        // The colored circle next to label name in dropdown list
        // This is setting the color of the circle according to the label's color
        ImageView labelColor = convertView.findViewById(R.id.label_color);
        Drawable colorCircle = labelColor.getDrawable();
        colorCircle = DrawableCompat.wrap(colorCircle);
        DrawableCompat.setTint(colorCircle, Color.parseColor(current_label.getLabel_color()));
        labelColor.setBackground(colorCircle);

        return convertView;
    }
}
