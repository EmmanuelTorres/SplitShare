package com.splitshare.splitshare;

/**
 * Created by armando on 10/23/17.
 */

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;
import java.util.List;

class TaskAdapter extends ArrayAdapter<Task> {
    TaskAdapter(Context context, List<Task> tasks) {
        super(context, R.layout.mainactivity_task_entry, tasks);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        LayoutInflater taskInflater = LayoutInflater.from(getContext());
        View row = taskInflater.inflate(R.layout.mainactivity_task_entry, parent, false);

        Task singleTask = getItem(position);
        TextView eventTitleText = (TextView) row.findViewById(R.id.entryTitle);
        TextView eventDateText = (TextView) row.findViewById(R.id.entryDate);
        TextView eventGroupText = (TextView) row.findViewById(R.id.entryGroup);
        TextView eventCostText = (TextView) row.findViewById(R.id.entryCost);

        eventTitleText.setText(singleTask.title);
        eventDateText.setText(singleTask.getDate());
        eventGroupText.setText(singleTask.group.getGroupName());
        String c = String.format("$%.2f", singleTask.fee);
        eventCostText.setText(c);
        if (!singleTask.costDue) {
            eventCostText.setVisibility(View.GONE);
        }
        return row;
    }
}
