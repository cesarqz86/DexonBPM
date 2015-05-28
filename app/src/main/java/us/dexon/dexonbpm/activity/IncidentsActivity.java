package us.dexon.dexonbpm.activity;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.ContextMenu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.ArrayList;

import us.dexon.dexonbpm.R;
import us.dexon.dexonbpm.adapters.TableMainLayout;
import us.dexon.dexonbpm.infrastructure.enums.TicketFilter;
import us.dexon.dexonbpm.infrastructure.implementations.ConfigurationService;
import us.dexon.dexonbpm.infrastructure.implementations.DexonDatabaseWrapper;
import us.dexon.dexonbpm.infrastructure.implementations.ServiceExecuter;
import us.dexon.dexonbpm.infrastructure.interfaces.IDexonDatabaseWrapper;
import us.dexon.dexonbpm.model.ReponseDTO.LoginResponseDto;
import us.dexon.dexonbpm.model.ReponseDTO.TicketsResponseDto;
import us.dexon.dexonbpm.model.RequestDTO.TicketsRequestDto;

public class IncidentsActivity extends FragmentActivity implements View.OnClickListener {

    //private TableLayout tbl_incidents;
    private TicketFilter currentTicketFilter;
    static final int FILTER_INCIDENT_CODE = 1;  // The request code

    public ArrayList<TicketsResponseDto> ticketListData;

    private TextView asignados_btn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_incidents);
        asignados_btn = (TextView) this.findViewById(R.id.asignados_btn);

        ImageButton menuButton = (ImageButton) findViewById(R.id.menu_button);
        registerForContextMenu(menuButton);
        menuButton.setOnClickListener(this);

        currentTicketFilter = TicketFilter.AssignedTickets;

        this.executeSearch();

        TextView asignadosBtn = (TextView) findViewById(R.id.asignados_btn);
        asignadosBtn.setOnClickListener(this);

        final EditText findDaemon = (EditText) findViewById(R.id.search_field);
        findDaemon.addTextChangedListener(new TextWatcher() {
            public void afterTextChanged(Editable s) {
                String wordTyped = findDaemon.getText().toString().trim();
                //Hacer tratamiento ala palabra digitada
            }

            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            public void onTextChanged(CharSequence s, int start, int before, int count) {
            }

        });

    }

    @Override
    public void onCreateContextMenu(ContextMenu menu, View v,
                                    ContextMenu.ContextMenuInfo menuInfo) {
        super.onCreateContextMenu(menu, v, menuInfo);
        // Creamos el objeto que debe inflar la vista del menú en la pantalla.
        MenuInflater inflater = new MenuInflater(this);

        switch (v.getId()) {
            case R.id.menu_button:
                inflater.inflate(R.menu.menu_incidents, menu);
                break;
        }
    }

    @Override
    public boolean onContextItemSelected(MenuItem item) {
        // Previamente creamos el objeto TextView y lo inicializamos para poder
        // asignarle aquí el texto en función de la opción seleccionada.
        switch (item.getItemId()) {
            case R.id.button2_menu_opt1:
                Intent changePassIntent = new Intent(this, ChangePasswordActivity.class);
                this.startActivity(changePassIntent);
                return true;
            case R.id.button2_menu_opt2:
                // Delete the user info.
                ConfigurationService.deleteUserInfo(this);
                Intent loginIntent = new Intent(this, LoginActivity.class);
                this.startActivity(loginIntent);
                this.finish();
                return true;
            case R.id.button2_menu_opt3:
                //Cancel does not do anything.
                return true;
            default:
                return super.onContextItemSelected(item);
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.menu_button:
                this.openContextMenu(v);
                break;
            case R.id.asignados_btn:
                Intent incidentFilterActivity = new Intent(this, HomeActivity.class);
                incidentFilterActivity.putExtra("CurrentFilter", this.currentTicketFilter.getCode());
                this.startActivityForResult(incidentFilterActivity, FILTER_INCIDENT_CODE);
                this.overridePendingTransition(R.anim.right_slide_in,
                        R.anim.right_slide_out);
                break;
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        // check if the request code is same as what is passed here it is 1
        if (requestCode == FILTER_INCIDENT_CODE) {
            String filterText = data.getStringExtra("CurrentFilter");
            this.currentTicketFilter = TicketFilter.GetValue(filterText);
            asignados_btn.setText(filterText);
            this.executeSearch();
        }
    }

    public void logoClick(View view) {
        Intent webIntent = new Intent();
        webIntent.setAction(Intent.ACTION_VIEW);
        webIntent.addCategory(Intent.CATEGORY_BROWSABLE);
        webIntent.setData(Uri.parse(this.getString(R.string.dexon_website)));
        this.startActivity(webIntent);
    }

    private void executeSearch() {
        IDexonDatabaseWrapper dexonDatabase = DexonDatabaseWrapper.getInstance();
        dexonDatabase.setContext(this);
        LoginResponseDto loggedUser = dexonDatabase.getLoggedUser();

        TicketsRequestDto ticketFirstData = new TicketsRequestDto();
        ticketFirstData.setIncludeClosedTickets(false);
        ticketFirstData.setLoggedUser(loggedUser);
        ticketFirstData.setTicketFilterType(currentTicketFilter.getCode());
        ticketFirstData.setTicketsPerPage(100); // First type we will get only 100 tickets
        //ticketFirstData.setTicketsPerPage(0); // First type we will get only 100 tickets

        ServiceExecuter serviceExecuter = new ServiceExecuter();
        ServiceExecuter.ExecuteTicketService ticketService = serviceExecuter.new ExecuteTicketService(this);
        ticketService.execute(ticketFirstData);

        TicketsRequestDto ticketTotalData = new TicketsRequestDto();
        ticketTotalData.setIncludeClosedTickets(false);
        ticketTotalData.setLoggedUser(loggedUser);
        ticketTotalData.setTicketFilterType(currentTicketFilter.getCode());
        ticketTotalData.setTicketsPerPage(0); // Get all the tickets

        ServiceExecuter.ExecuteTicketTotalService totalTicketService = serviceExecuter.new ExecuteTicketTotalService(this);
        totalTicketService.execute(ticketTotalData);
    }

    public void inidentsCallBack(){
        LinearLayout tableContainer = (LinearLayout) findViewById(R.id.table_container);
        tableContainer.removeAllViews();
        tableContainer.addView(new TableMainLayout(this, ticketListData));
    }
}
