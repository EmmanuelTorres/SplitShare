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
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

class MasterTaskAdapter extends ArrayAdapter<MasterTask> {
    public List<Boolean> isTaskChecked;
    MasterTaskAdapter(Context context, List<MasterTask> tasks) {
        super(context, R.layout.member_entry_element, tasks);
        isTaskChecked = new ArrayList<Boolean>(tasks.size());
        for (int i=0; i<tasks.size(); i++) {
            isTaskChecked.add(i, false);
        }
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        LayoutInflater taskInflater = LayoutInflater.from(getContext());
        View row = taskInflater.inflate(R.layout.member_entry_element, parent, false);

        MasterTask singleTask = getItem(position);
        TextView userNameField = (TextView) row.findViewById(R.id.memberNameText);
        CheckBox userCheckbox = (CheckBox) row.findViewById(R.id.memberSelectedCheckbox);

        userCheckbox.setChecked(isTaskChecked.get(position));
        userNameField.setText(singleTask.getTitle());
        return row;
    }

    public boolean isChecked(int i) {
        return isTaskChecked.get(i);
    }
}
