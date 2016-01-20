package us.dexon.dexonbpm.activity;

import android.content.Intent;
import android.support.v4.app.FragmentActivity;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.util.TypedValue;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.google.gson.JsonObject;

import us.dexon.dexonbpm.R;
import us.dexon.dexonbpm.adapters.TicketDetailAdapter;
import us.dexon.dexonbpm.infrastructure.implementations.CommonService;
import us.dexon.dexonbpm.infrastructure.implementations.CommonSharedData;
import us.dexon.dexonbpm.infrastructure.implementations.CommonValidations;
import us.dexon.dexonbpm.infrastructure.implementations.DexonDatabaseWrapper;
import us.dexon.dexonbpm.infrastructure.implementations.ServiceExecuter;
import us.dexon.dexonbpm.infrastructure.interfaces.IDexonDatabaseWrapper;
import us.dexon.dexonbpm.model.ReponseDTO.LoginResponseDto;
import us.dexon.dexonbpm.model.ReponseDTO.TicketDetailDataDto;
import us.dexon.dexonbpm.model.ReponseDTO.TicketResponseDto;
import us.dexon.dexonbpm.model.RequestDTO.ReloadRequestDto;
import us.dexon.dexonbpm.model.RequestDTO.ReopenRequestDto;
import us.dexon.dexonbpm.model.RequestDTO.SaveTicketRequestDto;
import us.dexon.dexonbpm.model.RequestDTO.TicketByLayoutRequestDto;
import us.dexon.dexonbpm.model.RequestDTO.TicketDetailRequestDto;

public class NewTicketActivity extends FragmentActivity {

    private String ticketId = "";
    private Menu menu;
    private TicketResponseDto ticketData;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_ticket);

        Intent i = getIntent();
        ticketId = i.getExtras().getString("TICKET_ID");

        if (CommonValidations.validateEmpty(ticketId)) {
            IDexonDatabaseWrapper dexonDatabase = DexonDatabaseWrapper.getInstance();
            dexonDatabase.setContext(this);

            LoginResponseDto loggedUser = dexonDatabase.getLoggedUser();

            TicketDetailRequestDto ticketData = new TicketDetailRequestDto();
            ticketData.setIncidentID(Integer.parseInt(ticketId));
            ticketData.setLoggedUser(loggedUser);

            ServiceExecuter serviceExecuter = new ServiceExecuter();
            ServiceExecuter.ExecuteTicketDetailService ticketService = serviceExecuter.new ExecuteTicketDetailService(this);
            ticketService.execute(ticketData);
        }

        CommonSharedData.TicketActivity = this;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu items for use in the action bar
        this.menu = menu;
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_new_ticket, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_save: {
                CommonService.saveTicket(this);
                break;
            }
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        CommonSharedData.OriginalTechnician = null;
        CommonSharedData.AttachmentList = null;
        CommonSharedData.ManualTechnician = null;
    }

    public void selectPlantilla_Click(View view) {
        Intent plantillaTreeView = new Intent(this, PlantillaListViewActivity.class);
        plantillaTreeView.putExtra("fieldKey", "HD_INCIDENT_LAYOUT_ID");
        this.startActivityForResult(plantillaTreeView, 0);
        this.overridePendingTransition(R.anim.right_slide_in,
                R.anim.right_slide_out);
    }

    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        CommonSharedData.TreeData = null;
    }

    public void inidentsCallBack(TicketResponseDto responseDto) {

        CommonSharedData.TicketInfo = responseDto;

        this.ticketData = responseDto;

        /*ListView lstvw_ticketdetail = (ListView) this.findViewById(R.id.lstvw_ticketdetail);
        TicketDetailAdapter detailAdapter = new TicketDetailAdapter(this, responseDto.getDataList(), responseDto);
        lstvw_ticketdetail.setAdapter(detailAdapter);*/

        CommonService.drawTicket(responseDto, this);
    }

    public void reloadCallback(JsonObject ticketInfo) {

        IDexonDatabaseWrapper dexonDatabase = DexonDatabaseWrapper.getInstance();
        dexonDatabase.setContext(this);

        LoginResponseDto loggedUser = dexonDatabase.getLoggedUser();

        ReloadRequestDto reloadData = new ReloadRequestDto();
        reloadData.setTicketInfo(ticketInfo);
        reloadData.setLoggedUser(loggedUser);

        ServiceExecuter serviceExecuter = new ServiceExecuter();
        ServiceExecuter.ExecuteReloadTicket ticketService = serviceExecuter.new ExecuteReloadTicket(this);
        ticketService.execute(reloadData);
    }

    public void reloadPlantillaCallback(String plantillaId, String incidentUniqueId) {

        IDexonDatabaseWrapper dexonDatabase = DexonDatabaseWrapper.getInstance();
        dexonDatabase.setContext(this);

        LoginResponseDto loggedUser = dexonDatabase.getLoggedUser();

        TicketByLayoutRequestDto layoutData = new TicketByLayoutRequestDto();
        layoutData.setLoggedUser(loggedUser);
        layoutData.setLayoutID(plantillaId);
        layoutData.setIncidentCode(incidentUniqueId);

        ServiceExecuter serviceExecuter = new ServiceExecuter();
        ServiceExecuter.ExecuteTicketByLayout ticketService = serviceExecuter.new ExecuteTicketByLayout(this);
        ticketService.execute(layoutData);
    }

}
