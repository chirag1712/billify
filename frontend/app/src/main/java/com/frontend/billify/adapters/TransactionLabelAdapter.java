package com.frontend.billify.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AutoCompleteTextView;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.frontend.billify.R;
import com.frontend.billify.helpers.callback.ICallback;
import com.frontend.billify.models.Label;
import com.frontend.billify.models.UserTransaction;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

public class TransactionLabelAdapter extends
        RecyclerView.Adapter<TransactionLabelAdapter.ViewHolder>{

    // Provide a direct reference to each of the views within a data item
    // Used to cache the views within the item layout for fast access
    public class ViewHolder extends RecyclerView.ViewHolder {
        // Holder should contain a member variable
        // for any view that will be set as you render a row
        public AutoCompleteTextView transactionLabel;
        public TextView transactionName;

        // Constructor accepts the entire item row and does the view lookups to find each subview
        public ViewHolder(View itemView) {
            // Stores the itemView in a public final member variable that can be used
            // to access the context from any ViewHolder instance.
            super(itemView);

            transactionName = itemView.findViewById(R.id.transaction_name);
            transactionLabel = itemView.findViewById(R.id.label_menu);
        }
    }

//    // Names of all transactions
//    private final List<String> transactionNames;
//
//    // Transaction - label data
//    // For example, a member can mean transaction tid 1 is mapped to label tid 2
//    // Each member has lid, label_name, label_color, tid, transaction_name populated
//    private final List<UserTransaction> transactionLabels;
//
//    // Pure label data. Contains all labels for dropdown list
//    // Each member has lid, label_name and label_color populated
    private final List<Label> labelsToDisplay;

    private final ArrayList<UserTransaction> userTransactions;

    // Callback function to notify that a label is selected on dropdown list
    private final ICallback onLabelChanged;

    public TransactionLabelAdapter(ArrayList<UserTransaction> userTransactions, List<Label> labelsToDisplay,
                                   ICallback onLabelChanged)  {
        this.userTransactions = userTransactions;
        this.onLabelChanged = onLabelChanged;
        this.labelsToDisplay = labelsToDisplay;
    }

    // Usually involves inflating a layout from XML and returning the holder
    @NotNull
    @Override
    public TransactionLabelAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        Context context = parent.getContext();
        LayoutInflater inflater = LayoutInflater.from(context);

        // Inflate the custom layout
        View contactView = inflater.inflate(R.layout.transaction_label_row, parent, false);

        // Return a new holder instance
        return new ViewHolder(contactView);
    }

    // Involves populating data into the item through holder
    @Override
    public void onBindViewHolder(TransactionLabelAdapter.ViewHolder holder, int position) {

        // Set item views based on views and data model
        holder.transactionName.setText(userTransactions.get(position).getTransaction_name());
        holder.transactionLabel.setText(userTransactions.get(position).getLabel().getLabel_name());

        // Set label adapter for label dropdown
        LabelDropdownAdapter adapter = new LabelDropdownAdapter(holder.itemView.getContext(), labelsToDisplay);
        holder.transactionLabel.setAdapter(adapter);

        holder.transactionLabel.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {

                /* Making a copy of selected UserTransaction whose label we clicked on,
                and updating it with new label so we can pass it
                 */
                UserTransaction updatedUserTransaction = new UserTransaction(userTransactions.get(position));

                // New label selected
                Label selectedNewLabel = labelsToDisplay.get(i);
                updatedUserTransaction.setLabel(selectedNewLabel);

                // set transaction info to make API call to change label of given tid
                // and update pie chart

                // Notify a label is changed
                // We pass the updated user transaction with new label
                onLabelChanged.callback(updatedUserTransaction);
            }
        });
    }

    // Returns the total count of items in the list
    // Needed for recycler view
    @Override
    public int getItemCount() {
        return userTransactions.size();
    }
}
