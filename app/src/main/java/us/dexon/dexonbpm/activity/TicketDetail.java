package us.dexon.dexonbpm.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.JsonElement;

import us.dexon.dexonbpm.R;
import us.dexon.dexonbpm.adapters.TicketDetailAdapter;
import us.dexon.dexonbpm.infrastructure.implementations.CommonValidations;
import us.dexon.dexonbpm.infrastructure.implementations.DexonDatabaseWrapper;
import us.dexon.dexonbpm.infrastructure.implementations.ServiceExecuter;
import us.dexon.dexonbpm.infrastructure.interfaces.IDexonDatabaseWrapper;
import us.dexon.dexonbpm.model.ReponseDTO.LoginResponseDto;
import us.dexon.dexonbpm.model.ReponseDTO.TicketDetailDataDto;
import us.dexon.dexonbpm.model.ReponseDTO.TicketResponseDto;
import us.dexon.dexonbpm.model.RequestDTO.TicketDetailRequestDto;

/**
 * Created by androide on 27/05/15.
 */
public class TicketDetail extends FragmentActivity {

    private String ticketId = "";
    private Menu menu;

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
                Toast.makeText(this, "I'm clicked save!", Toast.LENGTH_SHORT).show();
                break;
            }
            case R.id.action_reopen: {
                Toast.makeText(this, "I'm clicked reopen!", Toast.LENGTH_SHORT).show();
                break;
            }
        }
        return super.onOptionsItemSelected(item);
    }

    public void inidentsCallBack(TicketResponseDto responseDto) {
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

        StringBuilder progressText = new StringBuilder();
        progressText.append(responseDto.getCircularPercentDone().intValue());
        progressText.append("%");
        TextView txt_progresstext = (TextView) this.findViewById(R.id.txt_progresstext);
        txt_progresstext.setText(progressText.toString());

        ProgressBar circularProgressBar = (ProgressBar) this.findViewById(R.id.circularProgressBar);
        circularProgressBar.setProgress(responseDto.getCircularPercentDone().intValue());

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
}
