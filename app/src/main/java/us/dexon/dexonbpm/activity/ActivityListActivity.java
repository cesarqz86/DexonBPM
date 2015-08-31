package us.dexon.dexonbpm.activity;

import android.content.Intent;
import android.support.v4.app.FragmentActivity;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.ListView;
import android.widget.TextView;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import org.w3c.dom.Text;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import us.dexon.dexonbpm.R;
import us.dexon.dexonbpm.adapters.ActivityListAdapter;
import us.dexon.dexonbpm.adapters.TreeAdapter;
import us.dexon.dexonbpm.infrastructure.enums.MessageTypeIcon;
import us.dexon.dexonbpm.infrastructure.implementations.CommonService;
import us.dexon.dexonbpm.infrastructure.implementations.CommonSharedData;
import us.dexon.dexonbpm.infrastructure.implementations.CommonValidations;
import us.dexon.dexonbpm.infrastructure.implementations.DexonDatabaseWrapper;
import us.dexon.dexonbpm.infrastructure.implementations.ServiceExecuter;
import us.dexon.dexonbpm.infrastructure.interfaces.IDexonDatabaseWrapper;
import us.dexon.dexonbpm.model.ReponseDTO.ActivityTreeDto;
import us.dexon.dexonbpm.model.ReponseDTO.LoginResponseDto;
import us.dexon.dexonbpm.model.ReponseDTO.RecordHeaderResponseDto;
import us.dexon.dexonbpm.model.ReponseDTO.TicketResponseDto;
import us.dexon.dexonbpm.model.RequestDTO.RecordHeaderResquestDto;
import us.dexon.dexonbpm.model.RequestDTO.RelatedActivitiesRequestDto;
import us.dexon.dexonbpm.model.RequestDTO.SaveTicketRequestDto;
import us.dexon.dexonbpm.model.RequestDTO.TicketDetailRequestDto;

public class ActivityListActivity extends FragmentActivity {

    private Menu menu;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_activity_list);

        IDexonDatabaseWrapper dexonDatabase = DexonDatabaseWrapper.getInstance();
        dexonDatabase.setContext(this);

        LoginResponseDto loggedUser = dexonDatabase.getLoggedUser();

        Date currentDate = new Date();

        SimpleDateFormat dateFormater = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSSSS'-05:00'");
        CharSequence currentDateString = dateFormater.format(currentDate);

        JsonObject ticketJsonInfo = CommonSharedData.TicketInfo.getTicketInfo();
        ticketJsonInfo.addProperty("LastUpdateTime", currentDateString.toString());

        RelatedActivitiesRequestDto ticketData = new RelatedActivitiesRequestDto();
        ticketData.setTicketInfo(ticketJsonInfo);
        ticketData.setLoggedUser(loggedUser);

        ServiceExecuter serviceExecuter = new ServiceExecuter();
        ServiceExecuter.ExecuteRelatedActivities ticketService = serviceExecuter.new ExecuteRelatedActivities(this);
        ticketService.execute(ticketData);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu items for use in the action bar
        this.menu = menu;
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_activity_list, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_save: {
                Boolean canSave = true;

                for (ActivityTreeDto activityData : CommonSharedData.ActivityList) {
                    int technicianID = activityData.getTechnicianID().get("record_ID").getAsInt();
                    if (technicianID == 0) {
                        canSave = false;
                        break;
                    }

                    int statusID = activityData.getNextStatusID().get("record_ID").getAsInt();
                    if (statusID == 0) {
                        canSave = false;
                        break;
                    }
                }

                if (canSave) {
                    IDexonDatabaseWrapper dexonDatabase = DexonDatabaseWrapper.getInstance();
                    dexonDatabase.setContext(this);

                    LoginResponseDto loggedUser = dexonDatabase.getLoggedUser();

                    if (CommonSharedData.TicketInfoUpdated == null) {
                        CommonSharedData.TicketInfoUpdated = CommonSharedData.TicketInfo;
                    }

                    JsonObject saveTicketInfo = CommonSharedData.TicketInfoUpdated.getTicketInfo();
                    JsonObject headerInfo = CommonSharedData.TicketInfo.getTicketInfo().get("headerInfo").getAsJsonObject();
                    saveTicketInfo.add("headerInfo", headerInfo);

                    SaveTicketRequestDto ticketData = new SaveTicketRequestDto();
                    ticketData.setLoggedUser(loggedUser);
                    ticketData.setTicketInfo(saveTicketInfo);

                    ServiceExecuter serviceExecuter = new ServiceExecuter();
                    ServiceExecuter.ExecuteSaveTicket saveTicketService = serviceExecuter.new ExecuteSaveTicket(this);
                    saveTicketService.execute(ticketData);
                }
                else {
                    CommonService.ShowAlertDialog(this, R.string.validation_general_error_title, R.string.validation_workflow_error_save, MessageTypeIcon.Error, false);
                }

                break;
            }
        }
        return super.onOptionsItemSelected(item);
    }

    public void inidentsCallBack(TicketResponseDto responseDto) {
        JsonArray activitiesObject = responseDto.getTicketInfo().get("nextActivities").getAsJsonArray();

        List<ActivityTreeDto> activityList = new ArrayList<>(activitiesObject.size());

        for (JsonElement activityElement : activitiesObject) {
            JsonObject activityJson = activityElement.getAsJsonObject();
            ActivityTreeDto activityTreeDto = new ActivityTreeDto();

            JsonObject tempJson = activityJson.get("layout_ID").getAsJsonObject();
            activityTreeDto.setElementName(tempJson.get("short_description").getAsString());
            activityTreeDto.setLayoutID(tempJson);

            tempJson = activityJson.get("technician_ID").getAsJsonObject();
            activityTreeDto.setTechnicianID(tempJson);

            tempJson = activityJson.get("planned_start").getAsJsonObject();
            activityTreeDto.setPlannedStart(tempJson);

            tempJson = activityJson.get("planned_end").getAsJsonObject();
            activityTreeDto.setPlannedEnd(tempJson);

            tempJson = activityJson.get("first_comment").getAsJsonObject();
            activityTreeDto.setFirstComment(tempJson);

            tempJson = activityJson.get("adjust_SLA").getAsJsonObject();
            activityTreeDto.setAdjustSLA(tempJson);

            tempJson = activityJson.get("next_status_ID").getAsJsonObject();
            activityTreeDto.setNextStatusID(tempJson);

            activityTreeDto.setActivityJson(activityJson);

            activityList.add(activityTreeDto);
        }

        CommonSharedData.ActivityList = activityList;

        ActivityListAdapter treeAdapter = new ActivityListAdapter(this, activityList);

        ListView lstvw_tree_detail = (ListView) this.findViewById(R.id.lstvw_tree_detail);
        lstvw_tree_detail.setAdapter(treeAdapter);
    }
}
