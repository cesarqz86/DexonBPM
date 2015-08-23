package us.dexon.dexonbpm.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.view.View;
import android.widget.Button;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;

import us.dexon.dexonbpm.R;
import us.dexon.dexonbpm.adapters.TicketDetailAdapter;
import us.dexon.dexonbpm.infrastructure.implementations.CommonValidations;
import us.dexon.dexonbpm.infrastructure.implementations.DexonDatabaseWrapper;
import us.dexon.dexonbpm.infrastructure.implementations.ServiceExecuter;
import us.dexon.dexonbpm.infrastructure.interfaces.IDexonDatabaseWrapper;
import us.dexon.dexonbpm.model.ReponseDTO.LoginResponseDto;
import us.dexon.dexonbpm.model.ReponseDTO.TicketResponseDto;
import us.dexon.dexonbpm.model.RequestDTO.TicketDetailRequestDto;

/**
 * Created by androide on 27/05/15.
 */
public class TicketDetail extends FragmentActivity implements View.OnClickListener {

    private String ticketId = "";

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
    public void onClick(View v) {
        /*switch(v.getId()){

            case R.id.detalle_btn:
                break;

            case R.id.historial_btn:
                break;

        }*/
    }

    public void inidentsCallBack(TicketResponseDto responseDto) {
        ListView lstvw_ticketdetail = (ListView) this.findViewById(R.id.lstvw_ticketdetail);

        TicketDetailAdapter detailAdapter = new TicketDetailAdapter(this, responseDto.getDataList(), responseDto);
        lstvw_ticketdetail.setAdapter(detailAdapter);

        StringBuilder progressText = new StringBuilder();
        progressText.append(responseDto.getCircularPercentDone().intValue());
        progressText.append("%");
        TextView txt_progresstext = (TextView) this.findViewById(R.id.txt_progresstext);
        txt_progresstext.setText(progressText.toString());

        ProgressBar circularProgressBar = (ProgressBar) this.findViewById(R.id.circularProgressBar);
        circularProgressBar.setProgress(responseDto.getCircularPercentDone().intValue());
    }
}
