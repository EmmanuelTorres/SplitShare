package com.splitshare.splitshare;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.DatePicker;
import android.widget.ListView;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by armando on 11/21/17.
 */

public class AddUserToListActivity extends AppCompatActivity{
    public static List<User> usersAvail = new ArrayList<User>();
    public static List<User> usersSelected = new ArrayList<User>();
    private Activity autlAct = this;
    private AddUserToAdapter userAdapter;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.add_user_to_activity);

        //TaskCreationActivity.startDate  = ;
        userAdapter = new AddUserToAdapter(this, usersAvail);
        final ListView userListView = (ListView) findViewById(R.id.userListScrollView);
        userListView.setAdapter(userAdapter);

        Button cancelButton = (Button) findViewById(R.id.button_cancel);
        cancelButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) { closeUserSelector(); }
        });

        Button doneSelectingUser = (Button) findViewById(R.id.button_finish);
        doneSelectingUser.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) { returnUsersSelected(); }
        });

        userListView.setOnItemClickListener(new AdapterView.OnItemClickListener(){
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id){
                CheckBox cb = (CheckBox) view.findViewById(R.id.memberSelectedCheckbox);
                cb.toggle();
                userAdapter.isUserChecked.set(position, cb.isChecked());
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

    private void returnUsersSelected() {
        usersSelected.clear();
        for (int i=0; i<usersAvail.size(); i++) {
            if (userAdapter.isUserChecked.get(i))
                usersSelected.add(usersAvail.get(i));
        }
        Intent data = new Intent();
        data.putExtra("USERS_SELECTED", usersSelected.size());
        setResult(RESULT_OK, data);
        finish();
    }
}
