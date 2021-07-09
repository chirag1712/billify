package com.frontend.billify;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.frontend.billify.controllers.GroupService;
import com.frontend.billify.controllers.UserService;
import com.frontend.billify.models.Group;
import com.frontend.billify.models.User;
import com.frontend.billify.persistence.Persistence;
import com.frontend.billify.services.RetrofitService;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;

import org.json.JSONObject;

import java.util.ArrayList;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class CreateGroupActivity extends AppCompatActivity {
    private LinearLayout parentLinearLayout;
    private ArrayList<TextInputLayout> emailLayouts;
    private ArrayList<TextInputEditText> emailTexts;
    private ArrayList<View> emailRows;
    private TextInputLayout groupNameTextInput;
    private TextInputEditText groupNameEditText;

    private final RetrofitService retrofitService = new RetrofitService();
    private final GroupService groupService = new GroupService(retrofitService);

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_group);

        emailLayouts = new ArrayList<>();
        emailTexts = new ArrayList<>();
        emailLayouts.add(findViewById(R.id.member_text_input));
        emailTexts.add(findViewById(R.id.member_edit_text));

        emailRows = new ArrayList<>();
        emailRows.add(findViewById(R.id.email_row));
        groupNameTextInput = findViewById(R.id.group_name_text_input);
        groupNameEditText = findViewById(R.id.group_name_edit_text);

        parentLinearLayout = (LinearLayout) findViewById(R.id.parent_linear_layout);
    }

    public void onAddField(View v) {
        LayoutInflater inflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        final View rowView = inflater.inflate(R.layout.email_button_group, null);

        // Add to lists to retrieve data later
        emailLayouts.add(rowView.findViewById(R.id.member_text_input));
        emailTexts.add(rowView.findViewById(R.id.member_edit_text));

        // Convert from 50 dp
        int dp = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 60,
                getResources().getDisplayMetrics());

        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, dp);

        // Add the new row before buttons.
        parentLinearLayout.addView(rowView, parentLinearLayout.getChildCount() - 1, params);
        emailRows.add(rowView);
    }

    public void onCreateGroup(View v) {
        boolean valid = true;
        ArrayList<String> memberEmails = new ArrayList<>();
        String groupName;

        if (!isTextEmpty(groupNameEditText.getText())) {
            groupNameTextInput.setError(getString(R.string.error_group_name));
            valid = false;
        }

        for (View emailRow : emailRows) {
            TextInputLayout memberTextInput = emailRow.findViewById(R.id.member_text_input);
            TextInputEditText memberEditText = emailRow.findViewById(R.id.member_edit_text);

            if (!isTextEmpty(memberEditText.getText())) {
                memberTextInput.setError(getString(R.string.error_email));
                valid = false;
            } else {
                memberEmails.add(memberEditText.getText().toString());
            }
        }

        if (valid) {
            groupName = groupNameEditText.getText().toString();
            int uid = Persistence.getUserId(this);

            Toast createToast = Toast.makeText(getApplicationContext(),
                    "Creating group ...", Toast.LENGTH_SHORT);
            createToast.show();

            groupService.createGroup(uid, groupName, memberEmails).enqueue(
                    new Callback<Group>() {
                        @Override
                        public void onResponse(Call<Group> call, Response<Group> response) {
                            createToast.cancel();
                            if (!response.isSuccessful()) {
                                try {
                                    JSONObject error = new JSONObject(response.errorBody().string());
                                    Toast.makeText(getApplicationContext(),
                                            error.getString("error"),
                                            Toast.LENGTH_LONG).show();
                                } catch (Exception e) {
                                    e.printStackTrace();
                                    Toast.makeText(getApplicationContext(),
                                            "Sorry :( Something went wrong.",
                                            Toast.LENGTH_SHORT).show();
                                }
                                return;
                            }
                            Group group = response.body(); // only groupId is returned
                            openViewGroupPage();
                        }

                        @Override
                        public void onFailure(Call<Group> call, Throwable t) {
                            createToast.cancel();
                            Toast.makeText(getApplicationContext(),
                                    "Cannot create group", Toast.LENGTH_LONG).show();
                        }
                    });
        }
    }

    public void onDelete(View v) {
        View rowView = (View) v.getParent();

        // Remove from lists
        emailLayouts.remove(rowView.findViewById(R.id.member_text_input));
        emailTexts.remove(rowView.findViewById(R.id.member_edit_text));

        parentLinearLayout.removeView(rowView);
        emailRows.remove(rowView);
    }

    public void onCancel(View v) {
        finish();
    }

    private boolean isTextEmpty(@Nullable Editable text) {
        return text != null && text.length() > 0;
    }

    private void openViewGroupPage(){
        Intent intent = new Intent(this, groupPop.class);
        startActivity(intent);
    }
}