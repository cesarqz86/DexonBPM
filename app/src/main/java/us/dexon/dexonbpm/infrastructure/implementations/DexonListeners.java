package us.dexon.dexonbpm.infrastructure.implementations;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.text.format.DateFormat;
import android.util.Log;
import android.view.View;
import android.widget.RadioButton;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;

import org.w3c.dom.Text;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Map;

import us.dexon.dexonbpm.R;
import us.dexon.dexonbpm.activity.ActivityDetailActivity;
import us.dexon.dexonbpm.activity.IncidentsActivity;
import us.dexon.dexonbpm.activity.ListViewActivity;
import us.dexon.dexonbpm.activity.NewTicketActivity;
import us.dexon.dexonbpm.activity.PlantillaListViewActivity;
import us.dexon.dexonbpm.activity.RelatedDataActivity;
import us.dexon.dexonbpm.activity.TableActivity;
import us.dexon.dexonbpm.activity.TicketDetail;
import us.dexon.dexonbpm.infrastructure.interfaces.IDexonDatabaseWrapper;
import us.dexon.dexonbpm.infrastructure.interfaces.ITicketService;
import us.dexon.dexonbpm.model.ReponseDTO.ActivityTreeDto;
import us.dexon.dexonbpm.model.ReponseDTO.LoginResponseDto;
import us.dexon.dexonbpm.model.ReponseDTO.TicketRelatedDataDto;
import us.dexon.dexonbpm.model.ReponseDTO.TicketResponseDto;
import us.dexon.dexonbpm.model.ReponseDTO.TreeDataDto;
import us.dexon.dexonbpm.model.RequestDTO.TechnicianRequestDto;
import us.dexon.dexonbpm.model.RequestDTO.TicketDetailRequestDto;

/**
 * Created by Cesar Quiroz on 8/22/15.
 */
public final class DexonListeners {

    public static class ListViewClickListener implements View.OnClickListener {

        private final Context currentContext;
        private final String sonData;
        private final String fieldKey;

        public ListViewClickListener(Context context, String sonElement, String fieldKey) {
            this.currentContext = context;
            this.sonData = sonElement;
            this.fieldKey = fieldKey;
        }

        public void onClick(View v) {
            //Activity previousActivity = (Activity) this.currentContext;
            //Toast.makeText(v.getContext(), "I'm clicked!", Toast.LENGTH_SHORT).show();
            Intent listViewDetailIntent = new Intent(CommonSharedData.TicketActivity, ListViewActivity.class);
            listViewDetailIntent.putExtra("sonData", this.sonData);
            listViewDetailIntent.putExtra("fieldKey", this.fieldKey);
            CommonSharedData.TicketActivity.startActivityForResult(listViewDetailIntent, 0);
            CommonSharedData.TicketActivity.overridePendingTransition(R.anim.right_slide_in,
                    R.anim.right_slide_out);
        }
    }

    public static class ListView2ClickListener implements View.OnClickListener {

        private final Context currentContext;
        private final String keyToSearch;
        private final String sonData;
        private final String fieldKey;

        public ListView2ClickListener(Context context, String key, String sonElement, String fieldKey) {
            this.currentContext = context;
            this.keyToSearch = key;
            this.sonData = sonElement;
            this.fieldKey = fieldKey;
        }

        public void onClick(View v) {
            Intent listViewDetailIntent = new Intent(CommonSharedData.TicketActivity, ListViewActivity.class);
            listViewDetailIntent.putExtra("sonData", this.sonData);
            listViewDetailIntent.putExtra("keyToSearch", this.keyToSearch);
            listViewDetailIntent.putExtra("fieldKey", this.fieldKey);
            CommonSharedData.TicketActivity.startActivityForResult(listViewDetailIntent, 0);
            CommonSharedData.TicketActivity.overridePendingTransition(R.anim.right_slide_in,
                    R.anim.right_slide_out);
        }
    }

    public static class ListViewFinalClickListener implements View.OnClickListener {

        private final Context currentContext;
        private final TreeDataDto nodeInfo;
        private TicketResponseDto ticketInfo;
        private final String fieldKey;

        public ListViewFinalClickListener(Context context,
                                          TreeDataDto treeData,
                                          TicketResponseDto ticketInfo,
                                          String fieldKey) {
            this.currentContext = context;
            this.nodeInfo = treeData;
            this.ticketInfo = ticketInfo;
            this.fieldKey = fieldKey;
        }

        public void onClick(View v) {
            Log.i("DEXON_DEVELOPMENT", this.fieldKey);
            if (CommonSharedData.RelatedDataActivity == null) {
                this.headerClick();
            } else {
                this.relatedDataClick();
            }
        }

        private void headerClick() {
            if (CommonValidations.validateEqualsIgnoreCase(this.fieldKey, "family")) {
                Log.i("DEXON_DEVELOPMENT", this.nodeInfo.getElementId());

                Activity currentActivity = (Activity) this.currentContext;
                Activity parentActivity = null;
                Intent ticketDetailActivity = null;

                if (CommonSharedData.TicketActivity instanceof TicketDetail) {
                    parentActivity = (TicketDetail) CommonSharedData.TicketActivity;
                    ticketDetailActivity = new Intent(currentActivity, TicketDetail.class);
                } else if (CommonSharedData.TicketActivity instanceof NewTicketActivity) {
                    parentActivity = (NewTicketActivity) CommonSharedData.TicketActivity;
                    ticketDetailActivity = new Intent(currentActivity, NewTicketActivity.class);
                }

                CommonSharedData.TreeData = null;

                ticketDetailActivity.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
                currentActivity.startActivity(ticketDetailActivity);
                currentActivity.overridePendingTransition(R.anim.right_slide_in,
                        R.anim.right_slide_out);
                parentActivity.finish();
                CommonSharedData.IncidentListActivity.openFamilyCallBack(this.nodeInfo.getElementId());
            } else {

                JsonObject ticketJsonInfo = this.ticketInfo.getTicketInfo();
                JsonObject headerInfo = ticketJsonInfo.get("headerInfo").getAsJsonObject();
                JsonObject fieldInfo = headerInfo.get(this.fieldKey).getAsJsonObject();
                JsonObject sonData = fieldInfo.get("son").getAsJsonObject();

                fieldInfo.addProperty("Value", this.nodeInfo.getElementId());
                sonData.addProperty("short_description", this.nodeInfo.getElementName());

                fieldInfo.add("son", sonData);
                headerInfo.add(this.fieldKey, fieldInfo);
                ticketJsonInfo.add("headerInfo", headerInfo);
                ITicketService ticketService = TicketService.getInstance();
                this.ticketInfo = ticketService.convertToTicketData(ticketJsonInfo, R.id.btn_setmanual_technician, null);
                //CommonSharedData.TicketActivity.inidentsCallBack(this.ticketInfo);

                Activity currentActivity = (Activity) this.currentContext;
                TicketDetail ticketDetail = null;
                NewTicketActivity newTicket = null;
                Intent ticketDetailActivity = null;

                if (CommonSharedData.TicketActivity instanceof TicketDetail) {
                    ticketDetail = (TicketDetail) CommonSharedData.TicketActivity;
                    ticketDetailActivity = new Intent(currentActivity, TicketDetail.class);
                } else if (CommonSharedData.TicketActivity instanceof NewTicketActivity) {
                    newTicket = (NewTicketActivity) CommonSharedData.TicketActivity;
                    ticketDetailActivity = new Intent(currentActivity, NewTicketActivity.class);
                }

                if (ticketDetail != null)
                    ticketDetail.inidentsCallBack(this.ticketInfo);

                if (newTicket != null)
                    newTicket.inidentsCallBack(this.ticketInfo);

                ticketDetailActivity.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
                currentActivity.startActivity(ticketDetailActivity);
                currentActivity.overridePendingTransition(R.anim.right_slide_in,
                        R.anim.right_slide_out);
                Boolean isReloadRequired = sonData.get("can_trigger_BF").getAsBoolean();
                if (isReloadRequired) {
                    Date currentDate = new Date();

                    SimpleDateFormat dateFormater = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSSSS'-05:00'");
                    CharSequence currentDateString = dateFormater.format(currentDate);
                    ticketJsonInfo.addProperty("LastUpdateTime", currentDateString.toString());
                    //CommonSharedData.TicketActivity.reloadCallback(ticketJsonInfo);
                    if (ticketDetail != null)
                        ticketDetail.reloadCallback(ticketJsonInfo);

                    if (newTicket != null)
                        newTicket.reloadCallback(ticketJsonInfo);
                }
            }
        }

        private void relatedDataClick() {
            JsonObject relatedData = CommonSharedData.RelatedData;
            JsonObject fieldInfo = null;
            if (relatedData.has("multipleValues") && !relatedData.get("multipleValues").isJsonNull()) {
                JsonArray multipleFields = relatedData.get("multipleValues").getAsJsonArray();
                for (int index = 0; index < multipleFields.size(); index++) {
                    JsonArray fields = multipleFields.get(index).getAsJsonObject().get("fields").getAsJsonArray();
                    for (int indexFields = 0; indexFields < fields.size(); indexFields++) {
                        JsonObject tempObject = fields.get(indexFields).getAsJsonObject();
                        if (CommonValidations.validateEqualsIgnoreCase(tempObject.get("field_name").getAsString(), this.fieldKey)) {
                            fieldInfo = tempObject;
                            break;
                        }
                    }
                }
            } else {
                JsonArray fields = relatedData.get("fields").getAsJsonArray();
                for (int index = 0; index < fields.size(); index++) {
                    JsonObject tempObject = fields.get(index).getAsJsonObject();
                    if (CommonValidations.validateEqualsIgnoreCase(tempObject.get("field_name").getAsString(), this.fieldKey)) {
                        fieldInfo = tempObject;
                        break;
                    }
                }
            }

            JsonObject sonData = fieldInfo.get("son").getAsJsonObject();

            fieldInfo.addProperty("Value", this.nodeInfo.getElementId());
            sonData.addProperty("short_description", this.nodeInfo.getElementName());
            fieldInfo.add("son", sonData);

            if (relatedData.has("multipleValues") && !relatedData.get("multipleValues").isJsonNull()) {
                JsonArray multipleFields = relatedData.get("multipleValues").getAsJsonArray();
                for (int index = 0; index < multipleFields.size(); index++) {
                    JsonObject multipleFieldTemp = multipleFields.get(index).getAsJsonObject();
                    JsonArray fields = multipleFieldTemp.get("fields").getAsJsonArray();
                    for (int indexFields = 0; indexFields < fields.size(); indexFields++) {
                        JsonObject tempObject = fields.get(indexFields).getAsJsonObject();
                        if (CommonValidations.validateEqualsIgnoreCase(tempObject.get("field_name").getAsString(), this.fieldKey)) {
                            fields.set(indexFields, fieldInfo);
                            break;
                        }
                    }
                    multipleFieldTemp.add("fields", fields);
                    multipleFields.set(index, multipleFieldTemp);
                }
                relatedData.add("multipleValues", multipleFields);
            } else {
                JsonArray fields = relatedData.get("fields").getAsJsonArray();
                for (int index = 0; index < fields.size(); index++) {
                    JsonObject tempObject = fields.get(index).getAsJsonObject();
                    if (CommonValidations.validateEqualsIgnoreCase(tempObject.get("field_name").getAsString(), this.fieldKey)) {
                        fields.set(index, fieldInfo);
                        break;
                    }
                }
                relatedData.add("fields", fields);
            }

            Gson gsonSerializer = new Gson();
            Activity currentActivity = (Activity) this.currentContext;
            Intent relatedDataIntent = new Intent(currentActivity, RelatedDataActivity.class);
            relatedDataIntent.putExtra("activityTitle", relatedData.get("tb_name").getAsString());
            relatedDataIntent.putExtra("nodeData", gsonSerializer.toJson(relatedData));

            relatedDataIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
            currentActivity.startActivity(relatedDataIntent);
            currentActivity.overridePendingTransition(R.anim.right_slide_in,
                    R.anim.right_slide_out);
            Boolean isStatus = this.fieldKey.equals("status");
            Boolean isReloadRequired = sonData.get("can_trigger_BF").getAsBoolean();
            if (isReloadRequired || isStatus) {
                Date currentDate = new Date();

                SimpleDateFormat dateFormater = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSSSS'-05:00'");
                CharSequence currentDateString = dateFormater.format(currentDate);
                //TODO: Call reload data.
            }
        }
    }

    public static class TableClickListener implements View.OnClickListener {

        private final Context currentContext;
        private final String sonData;
        private final String fieldKey;

        public TableClickListener(Context context, String sonElement, String fieldKey) {
            this.currentContext = context;
            this.sonData = sonElement;
            this.fieldKey = fieldKey;
        }

        public void onClick(View v) {
            Activity mainActivity = CommonSharedData.TicketActivity;

            if (CommonSharedData.RelatedDataActivity != null) {
                mainActivity = CommonSharedData.RelatedDataActivity;
            }

            Intent tableDetailIntent = new Intent(mainActivity, TableActivity.class);
            tableDetailIntent.putExtra("sonData", this.sonData);
            tableDetailIntent.putExtra("fieldKey", this.fieldKey);
            CommonSharedData.TicketActivity.startActivityForResult(tableDetailIntent, 0);
            CommonSharedData.TicketActivity.overridePendingTransition(R.anim.right_slide_in,
                    R.anim.right_slide_out);
        }
    }

    public static class TechnicianClickListener implements View.OnClickListener {

        private final Context currentContext;
        private final JsonObject currentTechnician;
        private TicketResponseDto ticketInfo;

        public TechnicianClickListener(Context context, JsonObject technician, TicketResponseDto ticketInfo) {
            this.currentContext = context;
            this.currentTechnician = technician;
            this.ticketInfo = ticketInfo;
        }

        public void onClick(View v) {

            switch (v.getId()) {
                case R.id.btn_setmanual_technician: {
                    ITicketService ticketService = TicketService.getInstance();

                    TicketDetail ticketDetail = null;
                    NewTicketActivity newTicket = null;

                    if (this.currentContext instanceof TicketDetail) {
                        ticketDetail = (TicketDetail) this.currentContext;
                    } else if (this.currentContext instanceof NewTicketActivity) {
                        newTicket = (NewTicketActivity) this.currentContext;
                    }

                    JsonObject ticketInfoTemp = this.ticketInfo.getTicketInfo();
                    ticketInfoTemp.get("headerInfo").getAsJsonObject().add("current_technician", CommonSharedData.OriginalTechnician);
                    this.ticketInfo = ticketService.convertToTicketData(ticketInfoTemp, R.id.btn_setmanual_technician, CommonSharedData.OriginalTechnician);

                    if (ticketDetail != null) {
                        ticketDetail.inidentsCallBack(this.ticketInfo);
                    }
                    if (newTicket != null) {
                        newTicket.inidentsCallBack(this.ticketInfo);
                    }
                    break;
                }
                case R.id.btn_setautomatic_technician: {
                    TechnicianRequestDto headerData = new TechnicianRequestDto();
                    headerData.setHeaderInfo(this.ticketInfo.getTicketInfo().get("headerInfo").getAsJsonObject());

                    ServiceExecuter serviceExecuter = new ServiceExecuter();
                    ServiceExecuter.ExecuteAutomaticTechnician technicianService = serviceExecuter.new ExecuteAutomaticTechnician(this.currentContext,
                            this.ticketInfo,
                            this.currentTechnician);
                    technicianService.execute(headerData);
                    break;
                }
                case R.id.btn_settome_technician: {
                    IDexonDatabaseWrapper dexonDatabase = DexonDatabaseWrapper.getInstance();
                    dexonDatabase.setContext(this.currentContext);
                    LoginResponseDto loggedUser = dexonDatabase.getLoggedUser();
                    JsonObject tempTechninician = this.currentTechnician;

                    if (tempTechninician.has("son")) {
                        JsonObject sonData = tempTechninician.get("son").getAsJsonObject();
                        sonData.addProperty("short_description", loggedUser.getPersonName());
                    }
                    tempTechninician.addProperty("Value", loggedUser.getTechID());

                    ITicketService ticketService = TicketService.getInstance();

                    TicketDetail ticketDetail = null;
                    NewTicketActivity newTicket = null;

                    if (this.currentContext instanceof TicketDetail) {
                        ticketDetail = (TicketDetail) this.currentContext;
                    } else if (this.currentContext instanceof NewTicketActivity) {
                        newTicket = (NewTicketActivity) this.currentContext;
                    }


                    JsonObject ticketInfoTemp = this.ticketInfo.getTicketInfo();
                    ticketInfoTemp.get("headerInfo").getAsJsonObject().add("current_technician", tempTechninician);
                    this.ticketInfo = ticketService.convertToTicketData(ticketInfoTemp, R.id.btn_settome_technician, this.currentTechnician);

                    if (ticketDetail != null) {
                        ticketDetail.inidentsCallBack(this.ticketInfo);
                    }
                    if (newTicket != null) {
                        newTicket.inidentsCallBack(this.ticketInfo);
                    }

                    break;
                }
            }
        }
    }

    public static class Plantilla2ClickListener implements View.OnClickListener {

        private final Context currentContext;
        private final String keyToSearch;
        private final String fieldKey;

        public Plantilla2ClickListener(Context context, String key, String fieldKey) {
            this.currentContext = context;
            this.keyToSearch = key;
            this.fieldKey = fieldKey;
        }

        public void onClick(View v) {
            Intent listViewDetailIntent = new Intent(CommonSharedData.TicketActivity, PlantillaListViewActivity.class);
            listViewDetailIntent.putExtra("keyToSearch", this.keyToSearch);
            listViewDetailIntent.putExtra("fieldKey", this.fieldKey);
            CommonSharedData.TicketActivity.startActivityForResult(listViewDetailIntent, 0);
            CommonSharedData.TicketActivity.overridePendingTransition(R.anim.right_slide_in,
                    R.anim.right_slide_out);
        }
    }

    public static class PlantillaFinalClickListener implements View.OnClickListener {

        private final Context currentContext;
        private final TreeDataDto nodeInfo;
        private TicketResponseDto ticketInfo;
        private final String fieldKey;

        public PlantillaFinalClickListener(Context context,
                                           TreeDataDto treeData,
                                           TicketResponseDto ticketInfo,
                                           String fieldKey) {
            this.currentContext = context;
            this.nodeInfo = treeData;
            this.ticketInfo = ticketInfo;
            this.fieldKey = fieldKey;
        }

        public void onClick(View v) {
            JsonObject ticketJsonInfo = this.ticketInfo.getTicketInfo();
            String incidenteCode = ticketJsonInfo.get("uniqueCode").getAsString();
            String plantillaId = this.nodeInfo.getElementId();

            NewTicketActivity newTicket = (NewTicketActivity) CommonSharedData.TicketActivity;
            newTicket.reloadPlantillaCallback(plantillaId, incidenteCode);
            TextView txt_plantilla_value = (TextView) newTicket.findViewById(R.id.txt_plantilla_value);
            txt_plantilla_value.setText(this.nodeInfo.getElementName());

            Activity currentActivity = (Activity) this.currentContext;
            Intent ticketDetailActivity = new Intent(currentActivity, NewTicketActivity.class);
            ticketDetailActivity.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
            currentActivity.startActivity(ticketDetailActivity);
            currentActivity.overridePendingTransition(R.anim.right_slide_in,
                    R.anim.right_slide_out);
        }
    }

    public static class ActivityClickListener implements View.OnClickListener {

        private final Context currentContext;
        private final ActivityTreeDto nodeInfo;
        private final String selectedField;
        private final int position;

        public ActivityClickListener(Context context,
                                     ActivityTreeDto treeData,
                                     String fieldName,
                                     int positionData) {
            this.currentContext = context;
            this.nodeInfo = treeData;
            this.selectedField = fieldName;
            this.position = positionData;
        }

        public void onClick(View v) {
            CommonSharedData.NewActivityData = this.nodeInfo;
            Intent listViewDetailIntent = new Intent(CommonSharedData.TicketActivity, ActivityDetailActivity.class);
            listViewDetailIntent.putExtra("activityTitle", this.selectedField);
            listViewDetailIntent.putExtra("position", this.position);
            CommonSharedData.TicketActivity.startActivityForResult(listViewDetailIntent, 0);
            CommonSharedData.TicketActivity.overridePendingTransition(R.anim.right_slide_in,
                    R.anim.right_slide_out);
        }
    }

    public static class RelatedDataClickListener implements View.OnClickListener {

        private final Context currentContext;
        private final TicketRelatedDataDto nodeInfo;
        private final String selectedField;

        public RelatedDataClickListener(Context context,
                                        TicketRelatedDataDto treeData,
                                        String fieldName) {
            this.currentContext = context;
            this.nodeInfo = treeData;
            this.selectedField = fieldName;
        }

        public void onClick(View v) {

            Intent relatedDataIntent = new Intent(CommonSharedData.TicketActivity, RelatedDataActivity.class);
            relatedDataIntent.putExtra("activityTitle", this.selectedField);
            relatedDataIntent.putExtra("nodeData", this.nodeInfo.getFieldSonData());
            CommonSharedData.TicketActivity.startActivityForResult(relatedDataIntent, 0);
            CommonSharedData.TicketActivity.overridePendingTransition(R.anim.right_slide_in,
                    R.anim.right_slide_out);
        }
    }

}
