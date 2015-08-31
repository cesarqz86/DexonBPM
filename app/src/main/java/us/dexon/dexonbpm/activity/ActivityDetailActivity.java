package us.dexon.dexonbpm.activity;

import android.content.Intent;
import android.support.v4.app.FragmentActivity;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

import us.dexon.dexonbpm.R;
import us.dexon.dexonbpm.infrastructure.implementations.CommonService;
import us.dexon.dexonbpm.infrastructure.implementations.CommonSharedData;
import us.dexon.dexonbpm.model.ReponseDTO.ActivityTreeDto;

public class ActivityDetailActivity extends FragmentActivity {

    private ActivityTreeDto currentActivity;
    private int position;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_activity_detail);
        Intent currentIntent = this.getIntent();

        String title = currentIntent.getStringExtra("activityTitle");
        this.setTitle(title);

        position = currentIntent.getIntExtra("position", 0);

        CommonSharedData.ActivityActivity = this;

        this.drawInfo();
    }

    public void technicianClick(View view) {
        Gson gsonSerializer = new Gson();

        JsonElement sonElement = this.currentActivity.getLayoutID();
        JsonElement scondSonElement = this.currentActivity.getTechnicianID();

        Intent tableDetailIntent = new Intent(this, ActivityOptionsTableActivity.class);
        tableDetailIntent.putExtra("sonData", gsonSerializer.toJson(sonElement));
        tableDetailIntent.putExtra("secondSonData", gsonSerializer.toJson(scondSonElement));
        tableDetailIntent.putExtra("fieldKey", "technician_ID");
        tableDetailIntent.putExtra("position", 0);
        this.startActivityForResult(tableDetailIntent, 0);
        this.overridePendingTransition(R.anim.right_slide_in,
                R.anim.right_slide_out);
    }

    public void statusClick(View view) {
        Gson gsonSerializer = new Gson();

        JsonElement sonElement = this.currentActivity.getLayoutID();
        JsonElement scondSonElement = this.currentActivity.getNextStatusID();

        Intent tableDetailIntent = new Intent(this, ActivityOptionsTableActivity.class);
        tableDetailIntent.putExtra("sonData", gsonSerializer.toJson(sonElement));
        tableDetailIntent.putExtra("secondSonData", gsonSerializer.toJson(scondSonElement));
        tableDetailIntent.putExtra("fieldKey", "next_status_ID");
        tableDetailIntent.putExtra("position", 1);
        this.startActivityForResult(tableDetailIntent, 0);
        this.overridePendingTransition(R.anim.right_slide_in,
                R.anim.right_slide_out);
    }

    public void drawInfo() {

        currentActivity = CommonSharedData.NewActivityData;
        CommonSharedData.ActivityList.set(this.position, CommonSharedData.NewActivityData);

        TextView txt_activity_technician = (TextView) this.findViewById(R.id.txt_activity_technician);
        TextView txt_activity_status = (TextView) this.findViewById(R.id.txt_activity_status);
        TextView txt_activity_startdate = (TextView) this.findViewById(R.id.txt_activity_startdate);
        TextView txt_activity_enddate = (TextView) this.findViewById(R.id.txt_activity_enddate);
        TextView txt_activity_comments = (TextView) this.findViewById(R.id.txt_activity_comments);

        String technicianText = currentActivity.getTechnicianID().get("short_description").isJsonNull() ? "" : currentActivity.getTechnicianID().get("short_description").getAsString();
        String statusText = currentActivity.getNextStatusID().get("short_description").isJsonNull() ? "" : currentActivity.getNextStatusID().get("short_description").getAsString();
        String startDateText = currentActivity.getPlannedStart().get("Value").isJsonNull() ? "" : currentActivity.getPlannedStart().get("Value").getAsString();
        String endDateText = currentActivity.getPlannedEnd().get("Value").isJsonNull() ? "" : currentActivity.getPlannedEnd().get("Value").getAsString();
        String commentText = currentActivity.getFirstComment().get("Value").isJsonNull() ? "" : currentActivity.getFirstComment().get("Value").getAsString();

        startDateText = CommonService.convertToDexonDate(startDateText);
        endDateText = CommonService.convertToDexonDate(endDateText);

        txt_activity_technician.setText(technicianText);
        txt_activity_status.setText(statusText);
        txt_activity_startdate.setText(startDateText);
        txt_activity_enddate.setText(endDateText);
        txt_activity_comments.setText(commentText);
    }
}
