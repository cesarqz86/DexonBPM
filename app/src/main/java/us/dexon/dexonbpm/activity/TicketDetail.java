package us.dexon.dexonbpm.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.ListView;

import com.github.lzyzsd.circleprogress.DonutProgress;
import com.google.gson.JsonObject;

import us.dexon.dexonbpm.R;
import us.dexon.dexonbpm.adapters.TicketDetailAdapter;
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
import us.dexon.dexonbpm.model.RequestDTO.TicketDetailRequestDto;

/**
 * Created by androide on 27/05/15.
 */
public class TicketDetail extends FragmentActivity {

    private String ticketId = "";
    private Menu menu;
    private TicketResponseDto ticketData;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ticket_detail);

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
        inflater.inflate(R.menu.menu_ticket_detail, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_save: {
                IDexonDatabaseWrapper dexonDatabase = DexonDatabaseWrapper.getInstance();
                dexonDatabase.setContext(this);

                LoginResponseDto loggedUser = dexonDatabase.getLoggedUser();

                if (CommonSharedData.TicketInfoUpdated == null) {
                    CommonSharedData.TicketInfoUpdated = CommonSharedData.TicketInfo;
                }

                JsonObject saveTicketInfo = CommonSharedData.TicketInfoUpdated.getTicketInfo();
                JsonObject tempHeaderInfo = saveTicketInfo.get("headerInfo").getAsJsonObject();
                JsonObject headerInfo = CommonSharedData.TicketInfo.getTicketInfo().get("headerInfo").getAsJsonObject();
                saveTicketInfo.add("headerInfo", headerInfo);

                Boolean isClosed = tempHeaderInfo.get("closureStatus").getAsBoolean();

                if (!isClosed) {
                    SaveTicketRequestDto ticketData = new SaveTicketRequestDto();
                    ticketData.setLoggedUser(loggedUser);
                    ticketData.setTicketInfo(saveTicketInfo);

                    ServiceExecuter serviceExecuter = new ServiceExecuter();
                    ServiceExecuter.ExecuteSaveTicket saveTicketService = serviceExecuter.new ExecuteSaveTicket(this);
                    saveTicketService.execute(ticketData);
                } else {
                    //TODO Workflow process.
                }
                break;
            }
            case R.id.action_reopen: {
                IDexonDatabaseWrapper dexonDatabase = DexonDatabaseWrapper.getInstance();
                dexonDatabase.setContext(this);

                LoginResponseDto loggedUser = dexonDatabase.getLoggedUser();

                ReopenRequestDto ticketData = new ReopenRequestDto();
                ticketData.setLoggedUser(loggedUser);
                ticketData.setTicketInfo(this.ticketData.getTicketInfo());

                ServiceExecuter serviceExecuter = new ServiceExecuter();
                ServiceExecuter.ExecuteReopenTicket reopenService = serviceExecuter.new ExecuteReopenTicket(this);
                reopenService.execute(ticketData);
                break;
            }
        }
        return super.onOptionsItemSelected(item);
    }

    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        CommonSharedData.TreeData = null;
    }

    public void inidentsCallBack(TicketResponseDto responseDto) {

        CommonSharedData.TicketInfo = responseDto;

        this.ticketData = responseDto;

        ListView lstvw_ticketdetail = (ListView) this.findViewById(R.id.lstvw_ticketdetail);
        TicketDetailAdapter detailAdapter = new TicketDetailAdapter(this, responseDto.getDataList(), responseDto);
        lstvw_ticketdetail.setAdapter(detailAdapter);

        String activityTitle = this.getString(R.string.app_name);
        for (TicketDetailDataDto ticketData : responseDto.getDataList()) {
            if (ticketData.getFieldKey().equals("ticket")) {
                activityTitle = ticketData.getFieldValue();
                break;
            }
        }
        this.setTitle(activityTitle);

        int progressInt = responseDto.getCircularPercentDone().intValue();

        StringBuilder progressText = new StringBuilder();
        progressText.append(progressInt);
        /*progressText.append("%");*/

        DonutProgress circularProgressBar = (DonutProgress) findViewById(R.id.circularProgressBar);
        circularProgressBar.setMax(progressInt > 100 ? progressInt : 100);
        circularProgressBar.setProgress(progressInt);
        circularProgressBar.setTextSize(80);

        //Log.i("PROGRESS " , progressInt + "");

        if (progressInt <= 50) {
            circularProgressBar.setTextColor(getResources().getColor(R.color.progress_green_transparent));
            circularProgressBar.setFinishedStrokeColor(getResources().getColor(R.color.progress_green));
            circularProgressBar.setUnfinishedStrokeColor(getResources().getColor(R.color.progress_background));
        } else if (progressInt <= 80) {
            circularProgressBar.setTextColor(getResources().getColor(R.color.progress_yellow_transparent));
            circularProgressBar.setFinishedStrokeColor(getResources().getColor(R.color.progress_yellow));
            circularProgressBar.setUnfinishedStrokeColor(getResources().getColor(R.color.progress_background));
        } else {
            circularProgressBar.setTextColor(getResources().getColor(R.color.progress_red_transparent));
            circularProgressBar.setFinishedStrokeColor(getResources().getColor(R.color.progress_red));
            circularProgressBar.setUnfinishedStrokeColor(getResources().getColor(R.color.progress_background));
        }

        if (progressText.length() >= 4) {
            circularProgressBar.setTextSize(50);
        }

        MenuItem action_reopen = this.menu.findItem(R.id.action_reopen);
        MenuItem action_save = this.menu.findItem(R.id.action_save);

        action_save.setVisible(false);
        action_reopen.setVisible(false);

        if (responseDto.getIsOpen()) {
            if (responseDto.getIsEditable()) {
                action_save.setVisible(true);
            }
        } else {
            action_reopen.setVisible(true);
        }
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
}
