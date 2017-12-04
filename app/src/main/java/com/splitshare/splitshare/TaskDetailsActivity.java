package com.splitshare.splitshare;

/**
 * Created by Armando on 12/02/17.
 */

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

public class TaskDetailsActivity extends AppCompatActivity {
    private Button doneButton;
    public static Activity taskViewingActivityRef;
    public static Task taskForViewing;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.simple_task_details_activity);
        taskViewingActivityRef = this;

        // populate UI
        TextView displayGroup = findViewById(R.id.task_group_value);
        displayGroup.setText(taskForViewing.group.getGroupName());

        TextView displayTitle = findViewById(R.id.task_title_value);
        displayTitle.setText(taskForViewing.title);

        TextView displayCategory = findViewById(R.id.task_category_value);
        displayCategory.setText(taskForViewing.category);
        LinearLayout catDet = findViewById(R.id.task_category_details);
        if (taskForViewing.category.length() == 0)
            catDet.setVisibility(View.GONE);

        TextView displayDescription = findViewById(R.id.task_description_value);
        displayDescription.setText(taskForViewing.description);
        LinearLayout descDet = findViewById(R.id.task_description_details);
        if (taskForViewing.description.length() == 0)
            descDet.setVisibility(View.GONE);


        TextView displayDate = findViewById(R.id.task_date_value);
        displayDate.setText(taskForViewing.getDate());
        if (taskForViewing.costDue) {
            TextView displayFee = findViewById(R.id.task_cost_value);
            displayFee.setText(taskForViewing.getCost());

            TextView displayPaymentCollector = findViewById(R.id.task_collector_value);
            displayPaymentCollector.setText(taskForViewing.feeCollectionMember);
            LinearLayout collectorDet = findViewById(R.id.task_pay_to_details);
            if (taskForViewing.feeCollectionMember.length() == 0)
                collectorDet.setVisibility(View.GONE);
        } else {
            LinearLayout taskRelatedInfo = findViewById(R.id.task_cost_related);
            taskRelatedInfo.setVisibility(View.GONE);
        }


        doneButton = (Button) findViewById(R.id.button_finish);
        doneButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                closeTaskViewer();
            }
        });
    }

    @Override
    public void onBackPressed() {
        closeTaskViewer();
    }

    private void closeTaskViewer(){
        finish();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
    }
}
