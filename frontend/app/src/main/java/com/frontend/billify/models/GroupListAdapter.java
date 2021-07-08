package com.frontend.billify.models;

import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.frontend.billify.R;

import java.util.ArrayList;

public class GroupListAdapter extends ArrayAdapter<Group> {
    private static final String LOG_TAG = GroupListAdapter.class.getSimpleName();

    public GroupListAdapter(Activity context, ArrayList<Group> groups) {
        super(context,0,groups);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        // Check if the existing view is being reused, otherwise inflate the view
        View listItemView = convertView;
        if(listItemView == null) {
            listItemView = LayoutInflater.from(getContext()).inflate(
                    R.layout.list_of_groups, parent, false);
        }
        Group current_group = getItem(position);
        TextView nameTextView = (TextView) listItemView.findViewById(R.id.group_name);
        nameTextView.setText(current_group.getGroup_name());

        return listItemView;
    }

}
