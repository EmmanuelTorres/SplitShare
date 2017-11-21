package com.splitshare.splitshare;

/**
 * Created by armando on 11/21/17.
 */

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

class AddUserToAdapter extends ArrayAdapter<User> {
    public List<Boolean> isUserChecked;
    AddUserToAdapter(Context context, List<User> users) {
        super(context, R.layout.mainactivity_task_entry, users);
        isUserChecked = new ArrayList<Boolean>(users.size());
        for (int i=0; i<users.size(); i++) {
            isUserChecked.add(i, false);
        }
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        LayoutInflater taskInflater = LayoutInflater.from(getContext());
        View row = taskInflater.inflate(R.layout.member_entry_element, parent, false);

        User singleUser = getItem(position);
        TextView userNameField = (TextView) row.findViewById(R.id.memberNameText);
        CheckBox userCheckbox = (CheckBox) row.findViewById(R.id.memberSelectedCheckbox);

        userCheckbox.setChecked(isUserChecked.get(position));
        userNameField.setText(singleUser.getUserName());
        return row;
    }

    public boolean isChecked(int i) {
        return isUserChecked.get(i);
    }
}
