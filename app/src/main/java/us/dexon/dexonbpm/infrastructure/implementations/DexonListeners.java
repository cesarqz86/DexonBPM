package us.dexon.dexonbpm.infrastructure.implementations;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.view.View;
import android.widget.RadioButton;
import android.widget.Toast;

import com.google.gson.JsonObject;

import java.util.List;
import java.util.Map;

import us.dexon.dexonbpm.R;
import us.dexon.dexonbpm.activity.IncidentsActivity;
import us.dexon.dexonbpm.activity.ListViewActivity;
import us.dexon.dexonbpm.activity.TableActivity;
import us.dexon.dexonbpm.activity.TicketDetail;
import us.dexon.dexonbpm.infrastructure.interfaces.IDexonDatabaseWrapper;
import us.dexon.dexonbpm.infrastructure.interfaces.ITicketService;
import us.dexon.dexonbpm.model.ReponseDTO.LoginResponseDto;
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

        public ListViewClickListener(Context context, String sonElement) {
            this.currentContext = context;
            this.sonData = sonElement;
        }

        public void onClick(View v) {
            Activity previousActivity = (Activity) this.currentContext;
            //Toast.makeText(v.getContext(), "I'm clicked!", Toast.LENGTH_SHORT).show();
            Intent listViewDetailIntent = new Intent(previousActivity, ListViewActivity.class);
            listViewDetailIntent.putExtra("sonData", this.sonData);
            previousActivity.startActivity(listViewDetailIntent);
            previousActivity.overridePendingTransition(R.anim.right_slide_in,
                    R.anim.right_slide_out);
        }
    }

    public static class ListView2ClickListener implements View.OnClickListener {

        private final Context currentContext;
        private final String keyToSearch;
        private final String sonData;

        public ListView2ClickListener(Context context, String key, String sonElement) {
            this.currentContext = context;
            this.keyToSearch = key;
            this.sonData = sonElement;
        }

        public void onClick(View v) {
            Activity previousActivity = (Activity) this.currentContext;
            //Toast.makeText(v.getContext(), "I'm clicked!", Toast.LENGTH_SHORT).show();
            Intent listViewDetailIntent = new Intent(previousActivity, ListViewActivity.class);
            listViewDetailIntent.putExtra("sonData", this.sonData);
            listViewDetailIntent.putExtra("keyToSearch", this.keyToSearch);
            previousActivity.startActivity(listViewDetailIntent);
            previousActivity.overridePendingTransition(R.anim.right_slide_in,
                    R.anim.right_slide_out);
        }
    }

    public static class TableClickListener implements View.OnClickListener {

        private final Context currentContext;
        private final String sonData;

        public TableClickListener(Context context, String sonElement) {
            this.currentContext = context;
            this.sonData = sonElement;
        }

        public void onClick(View v) {
            Activity previousActivity = (Activity) this.currentContext;
            //Toast.makeText(v.getContext(), "I'm clicked!", Toast.LENGTH_SHORT).show();
            Intent tableDetailIntent = new Intent(previousActivity, TableActivity.class);
            tableDetailIntent.putExtra("sonData", this.sonData);
            previousActivity.startActivity(tableDetailIntent);
            previousActivity.overridePendingTransition(R.anim.right_slide_in,
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
                    TicketDetail ticketDetailView = (TicketDetail) this.currentContext;
                    if (ticketDetailView != null) {
                        JsonObject ticketInfoTemp = this.ticketInfo.getTicketInfo();
                        this.ticketInfo = ticketService.convertToTicketData(ticketInfoTemp, R.id.btn_setmanual_technician, this.currentTechnician);
                        ticketDetailView.inidentsCallBack(this.ticketInfo);
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
                    TicketDetail ticketDetailView = (TicketDetail) this.currentContext;
                    if (ticketDetailView != null) {
                        JsonObject ticketInfoTemp = this.ticketInfo.getTicketInfo();
                        ticketInfoTemp.get("headerInfo").getAsJsonObject().add("current_technician", tempTechninician);
                        this.ticketInfo = ticketService.convertToTicketData(ticketInfoTemp, R.id.btn_settome_technician, this.currentTechnician);
                        ticketDetailView.inidentsCallBack(this.ticketInfo);
                    }

                    break;
                }
            }
        }
    }

}
