package com.splitshare.splitshare;

import android.app.Activity;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.ListView;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by lyphc on 12/6/2017.
 */

public class TaskRemovalActivity extends AppCompatActivity{
    public static List<MasterTask> tasksAvail = new ArrayList<MasterTask>();
    public static List<MasterTask> tasksSelected = new ArrayList<MasterTask>();
    private Activity autlAct = this;
    private MasterTaskAdapter taskAdapter;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.add_user_to_activity);

        //A list of task, and it is translated onto UI to scroll
        taskAdapter = new MasterTaskAdapter(this, tasksAvail);
        final ListView taskListView = (ListView) findViewById(R.id.userListScrollView);
        taskListView.setAdapter(taskAdapter);

        Button cancelButton = (Button) findViewById(R.id.button_cancel);
        cancelButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) { closeUserSelector(); }
        });

        Button doneSelectingUser = (Button) findViewById(R.id.button_finish);
        doneSelectingUser.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) { deleteTasksSelected(); }
        });

        doneSelectingUser.setText("Delete");

        //task check or not checked
        taskListView.setOnItemClickListener(new AdapterView.OnItemClickListener(){
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id){
                CheckBox cb = (CheckBox) view.findViewById(R.id.memberSelectedCheckbox);
                cb.toggle();
                taskAdapter.isTaskChecked.set(position, cb.isChecked());
            }
        });
    }

    @Override
    public void onBackPressed() {
        closeUserSelector();
    }

    private void closeUserSelector(){
        finish();
    }

    private void deleteTasksSelected() {
        tasksSelected.clear();
        for (int i=0; i<tasksAvail.size(); i++) {
            if (taskAdapter.isTaskChecked.get(i))
                tasksSelected.add(tasksAvail.get(i));
        }
        // delete the tasks in tasksSelected from DB
        for(MasterTask i : tasksSelected){
            i.removeFromDatabase();

        }
        //done
        finish();
    }
}
