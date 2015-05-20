package us.dexon.dexonbpm.activity;

import android.content.Context;
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
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TableLayout;
import android.widget.TextView;

import us.dexon.dexonbpm.R;
import us.dexon.dexonbpm.infrastructure.enums.TicketFilter;
import us.dexon.dexonbpm.infrastructure.implementations.CommonService;
import us.dexon.dexonbpm.infrastructure.implementations.ConfigurationService;
import us.dexon.dexonbpm.infrastructure.implementations.DexonDatabaseWrapper;
import us.dexon.dexonbpm.infrastructure.implementations.ServiceExecuter;
import us.dexon.dexonbpm.infrastructure.interfaces.IDexonDatabaseWrapper;
import us.dexon.dexonbpm.model.ReponseDTO.LoginResponseDto;
import us.dexon.dexonbpm.model.RequestDTO.TicketsRequestDto;

public class IncidentsActivity extends FragmentActivity implements View.OnClickListener{

    private TableLayout tbl_incident_header;
    private TableLayout tbl_incident_data;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_incidents);
        String[] headerText = this.getResources().getStringArray(R.array.array_column_incidents);
        tbl_incident_header = (TableLayout) this.findViewById(R.id.tbl_incident_header);
        tbl_incident_data = (TableLayout) this.findViewById(R.id.tbl_incident_data);

        tbl_incident_header = CommonService.AddRowToTable(this, tbl_incident_header, true, headerText);
        /*for (int rows = 0; rows <= 2000; rows++) {
            tbl_incident_data = CommonService.AddRowToTable(this, tbl_incident_data, false, headerText);
        }*/

        ImageButton menuButton = (ImageButton) findViewById(R.id.menu_button);
        registerForContextMenu(menuButton);
        menuButton.setOnClickListener(this);

        IDexonDatabaseWrapper dexonDatabase = DexonDatabaseWrapper.getInstance();
        dexonDatabase.setContext(this);
        LoginResponseDto loggedUser = dexonDatabase.getLoggedUser();

        TicketsRequestDto ticketFirstData = new TicketsRequestDto();
        ticketFirstData.setIncludeClosedTickets(false);
        ticketFirstData.setLoggedUser(loggedUser);
        ticketFirstData.setTicketFilterType(TicketFilter.None.getCode());
        ticketFirstData.setTicketsPerPage(20); // First type we will get only 20 tickets

        ServiceExecuter serviceExecuter = new ServiceExecuter();
        ServiceExecuter.ExecuteTicketService ticketService = serviceExecuter.new ExecuteTicketService(this);
        ticketService.execute(ticketFirstData);

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
        switch(v.getId()){
            case R.id.menu_button:
                this.openContextMenu(v);
                break;
            case R.id.asignados_btn:
                Intent incidentActivity = new Intent(IncidentsActivity.this, HomeActivity.class);
                startActivity(incidentActivity);
                overridePendingTransition(R.anim.right_slide_in,
                        R.anim.right_slide_out);
                break;
        }
    }

    public void logoClick(View view) {
        Intent webIntent = new Intent();
        webIntent.setAction(Intent.ACTION_VIEW);
        webIntent.addCategory(Intent.CATEGORY_BROWSABLE);
        webIntent.setData(Uri.parse(this.getString(R.string.dexon_website)));
        this.startActivity(webIntent);
    }
}
