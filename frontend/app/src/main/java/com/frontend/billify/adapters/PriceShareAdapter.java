package com.frontend.billify.adapters;

import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.frontend.billify.models.UserTransactionShare;

public class PriceShareAdapter extends ArrayAdapter<UserTransactionShare> {
    private final Context context;
    private final ArrayList<Person> data;
    private final int layoutResourceId;

    public PersonAdapter(Context context, int layoutResourceId, ArrayList<Person> data) {
        super(context, layoutResourceId, data);
        this.context = context;
        this.data = data;
        this.layoutResourceId = layoutResourceId;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View row = convertView;
        ViewHolder holder = null;

        if(row == null)
        {
            LayoutInflater inflater = ((Activity)context).getLayoutInflater();
            row = inflater.inflate(layoutResourceId, parent, false);

            holder = new ViewHolder();
            holder.textView1 = (TextView)row.findViewById(R.id.text1);
            holder.textView2 = (TextView)row.findViewById(R.id.text2);
            ...
            ...
            holder.textView3 = (TextView)row.findViewById(R.id.text3);

            row.setTag(holder);
        }
        else
        {
            holder = (ViewHolder)row.getTag();
        }

        Person person = data.get(position);

        holder.textView1.setText(person.getName());
        holder.textView2.setText(person.getAddress());
        ...
        ...
        holder.textView3.setText(person.getEtc());

        return row;
    }

    static class ViewHolder
    {
        TextView textView1;
        TextView textView2;
        ...
                ...
        TextView textView3;
    }
