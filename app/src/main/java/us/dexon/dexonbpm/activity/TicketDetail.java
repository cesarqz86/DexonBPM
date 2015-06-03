package us.dexon.dexonbpm.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.view.View;
import android.widget.Button;

import us.dexon.dexonbpm.R;

/**
 * Created by androide on 27/05/15.
 */
public class TicketDetail extends FragmentActivity implements View.OnClickListener{

    private String ticketId = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ticket_detail);

        Intent i = getIntent();
        ticketId = i.getExtras().getString("TICKET_ID");

        Button detail = (Button) findViewById(R.id.detalle_btn);
        detail.setOnClickListener(this);

        Button history = (Button) findViewById(R.id.detalle_btn);
        detail.setOnClickListener(this);

        history.setEnabled(false);



    }

    @Override
    public void onClick(View v) {
        switch(v.getId()){

            case R.id.detalle_btn:
                break;

            case R.id.historial_btn:
                break;

        }
    }
}
