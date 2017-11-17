package com.splitshare.splitshare;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.Toast;

import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import static com.splitshare.splitshare.SplitShareApp.splitShareUser;
import static com.splitshare.splitshare.SplitShareApp.usersGroups;
import static com.splitshare.splitshare.SplitShareApp.usersMasterTasks;

public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {


    public static Activity mainActRef;
    public static List<Task> mainTaskList = new ArrayList<Task>();
    public static ListAdapter taskViewList;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_mainactivity);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        // Establishes a global connection to the
        SplitShareApp.firebaseDatabase = FirebaseDatabase.getInstance();
        SplitShareApp.splitShareUser = new SplitShareUser();
        //usersGroups = new ArrayList<Group>();
        //splitShareUser = new SplitShareUser();
        // used so things can externally update the agenda
        mainActRef = this;
        taskViewList = new TaskAdapter(mainActRef, mainTaskList);

        splitShareUser.createAccount();
        splitShareUser.getGroups();

        new Timer().schedule(new TimerTask() {
            @Override
            public void run() {
                splitShareUser.getTasks();
            }
        }, 6000);

        if (usersGroups.size() > 0)
            Toast.makeText(this, "Member of " +
                usersGroups.size() + " groups!" , Toast.LENGTH_LONG).show();
        else
            Toast.makeText(this, "No groups!", Toast.LENGTH_LONG).show();


        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {


                // Creates a SplitShareUser object to interact with the database to make
                // accounts, etc
                if (usersMasterTasks.size() > 0)
                    Toast.makeText(mainActRef, "We have " + usersMasterTasks.size() + " MasterTasks!" , Toast.LENGTH_LONG).show();
                else
                    Toast.makeText(mainActRef, "No tasks!", Toast.LENGTH_LONG).show();
                //Group group = new Group("Fumbling Narwhals");
                // Creates a group with ourselves as the sole member
                //group.createGroup();
                // Adds ourselves
                //group.addMember(splitShareUser.getAccountId());
                //group.addMember("6969");
                openTaskCreator();
            }
        });

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);



        //mainTaskList.add(new Task());
        //mainTaskList.add(new Task());
        //mainTaskList.add(new Task());
        ListView mainTaskView = (ListView) findViewById(R.id.mainListView);
        mainTaskView.setAdapter(taskViewList);
    }

    private void openTaskCreator() {
        startActivity(new Intent(this, TaskCreationActivity.class));
    }

    private void openGroupCreator() {
        startActivity(new Intent(this, GroupCreationActivity.class));
    }

    public static void addToTaskList(Task inputTask)
    {
        mainTaskList.add(inputTask);
        ((ArrayAdapter)taskViewList).notifyDataSetChanged();
    }

    public static void addAllToTaskList(ArrayList<Task> tasks) {
        mainTaskList.clear();
        for(Task a : tasks) {
            mainTaskList.add(a);
        }
        tasks.clear();
        ((ArrayAdapter)taskViewList).notifyDataSetChanged();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.groups, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();
        if (id == R.id.add_group)
        {
            openGroupCreator();
        }
        else if (id == R.id.nav_camera) {
            // Handle the camera action
        } else if (id == R.id.nav_gallery) {

        } else if (id == R.id.nav_slideshow) {

        } else if (id == R.id.nav_manage) {

        } else if (id == R.id.nav_share) {

        } else if (id == R.id.nav_send) {

        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    @Override
    public void onStart() {
        super.onStart();
        SplitShareApp.mAuth.addAuthStateListener(SplitShareApp.mAuthListener);
    }

    @Override
    public void onStop() {
        super.onStop();
        if (SplitShareApp.mAuthListener != null) {
            SplitShareApp.mAuth.removeAuthStateListener(SplitShareApp.mAuthListener);
        }
    }
}
