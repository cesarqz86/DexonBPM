package us.dexon.dexonbpm.activity;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.view.ContextMenu;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;

import inqbarna.tablefixheaders.TableFixHeaders;
import us.dexon.dexonbpm.R;
import us.dexon.dexonbpm.adapters.MatrixTableAdapter;
import us.dexon.dexonbpm.infrastructure.enums.TicketFilter;
import us.dexon.dexonbpm.infrastructure.implementations.CommonService;
import us.dexon.dexonbpm.infrastructure.implementations.CommonSharedData;
import us.dexon.dexonbpm.infrastructure.implementations.CommonValidations;
import us.dexon.dexonbpm.infrastructure.implementations.ConfigurationService;
import us.dexon.dexonbpm.infrastructure.implementations.DexonDatabaseWrapper;
import us.dexon.dexonbpm.infrastructure.implementations.ServiceExecuter;
import us.dexon.dexonbpm.infrastructure.implementations.TicketService;
import us.dexon.dexonbpm.infrastructure.interfaces.IDexonDatabaseWrapper;
import us.dexon.dexonbpm.infrastructure.interfaces.ITicketService;
import us.dexon.dexonbpm.model.ReponseDTO.LoginResponseDto;
import us.dexon.dexonbpm.model.RequestDTO.TicketsRequestDto;

public class IncidentsActivity extends FragmentActivity implements View.OnClickListener {

    private TicketFilter currentTicketFilter = TicketFilter.AssignedTickets;
    private boolean includeClose;
    static final int FILTER_INCIDENT_CODE = 1;  // The request code

    public String[][] ticketListData;
    public String[][] originalTicketListData;

    private TextView asignados_btn;
    private MatrixTableAdapter matrixTableAdapter;
    public final Context currentContext = this;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_incidents);
        asignados_btn = (TextView) this.findViewById(R.id.asignados_btn);

        ImageButton menuButton = (ImageButton) findViewById(R.id.menu_button);
        registerForContextMenu(menuButton);
        menuButton.setOnClickListener(this);

        this.currentTicketFilter = TicketFilter.AssignedTickets;
        this.executeSearch();

        TextView asignadosBtn = (TextView) findViewById(R.id.asignados_btn);
        asignadosBtn.setOnClickListener(this);

        final EditText findDaemon = (EditText) findViewById(R.id.search_field);
        findDaemon.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_DONE) {
                    ServiceExecuter serviceExecuter = new ServiceExecuter();
                    ServiceExecuter.ExecuteFilter filterService = serviceExecuter.new ExecuteFilter(currentContext);
                    CommonSharedData.FilterCriteria = v.getText().toString();
                    filterService.execute(CommonSharedData.FilterCriteria);
                }
                return false;
            }
        });

        this.searchFilterCriteria();

        // To avoid issues with the Spring RestClient and HttpHeaders
        System.setProperty("http.keepAlive", "false");

        CommonSharedData.IncidentListActivity = this;
        this.SetConfiguredColors();
    }

    @Override
    protected void onStop() {
        super.onStop();  // Always call the superclass method first
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu items for use in the action bar
        //this.menu = menu;
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_home, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_reloadincidents: {
                this.executeSearch();
                break;
            }
        }

        return super.onOptionsItemSelected(item);
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
                incidentFilterActivity.putExtra("IncludeClose", this.includeClose);
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
        if (requestCode == FILTER_INCIDENT_CODE && data != null) {
            String filterText = data.getStringExtra("CurrentFilter");
            asignados_btn.setText(filterText);

            boolean filterClose = data.getBooleanExtra("IncludeClose", false);
            this.currentTicketFilter = TicketFilter.GetValue(filterText);
            this.includeClose = filterClose;
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

    public void newTicketClick(View view) {
        Intent newTicketIntent = new Intent(this, NewTicketActivity.class);
        newTicketIntent.putExtra("TICKET_ID", "-1");
        this.startActivity(newTicketIntent);
        this.overridePendingTransition(R.anim.right_slide_in,
                R.anim.right_slide_out);
    }

    public final void executeSearch() {
        IDexonDatabaseWrapper dexonDatabase = DexonDatabaseWrapper.getInstance();
        dexonDatabase.setContext(this);

        LoginResponseDto loggedUser = dexonDatabase.getLoggedUser();

        TicketsRequestDto ticketFirstData = new TicketsRequestDto();
        ticketFirstData.setIncludeClosedTickets(this.includeClose);
        ticketFirstData.setLoggedUser(loggedUser);
        ticketFirstData.setTicketFilterType(this.currentTicketFilter.getCode());
        ticketFirstData.setTicketsPerPage(100); // First type we will get only 100 tickets

        ServiceExecuter serviceExecuter = new ServiceExecuter();
        ServiceExecuter.ExecuteTicketService ticketService = serviceExecuter.new ExecuteTicketService(this);
        ticketService.execute(ticketFirstData);

        TicketsRequestDto ticketTotalFirstData = new TicketsRequestDto();
        ticketTotalFirstData.setIncludeClosedTickets(this.includeClose);
        ticketTotalFirstData.setLoggedUser(loggedUser);
        ticketTotalFirstData.setTicketFilterType(this.currentTicketFilter.getCode());
        ticketTotalFirstData.setTicketsPerPage(0); // First type we will get only 100 tickets

        ServiceExecuter.ExecuteTicketTotalService ticketTotalService = serviceExecuter.new ExecuteTicketTotalService(this);
        ticketTotalService.execute(ticketTotalFirstData);

        this.searchFilterCriteria();
    }

    public void inidentsCallBack(String[][] dataList) {
        if (CommonValidations.validateArrayNullOrEmpty(dataList)) {
            ITicketService ticketService = TicketService.getInstance();
            dataList = ticketService.getEmptyData("TICKET");
        }
        int indexColumnID = -1;
        if (dataList.length > 0) {
            int tempIndex = 0;
            for (String columnData : dataList[0]) {
                if (columnData != null && columnData.equals("HD_INCIDENT_ID")) {
                    indexColumnID = tempIndex;
                    break;
                }
                tempIndex++;
            }
        }
        TableFixHeaders tableFixHeaders = (TableFixHeaders) findViewById(R.id.table_container);
        this.matrixTableAdapter = new MatrixTableAdapter(this, dataList, indexColumnID);
        tableFixHeaders.setAdapter(this.matrixTableAdapter);
    }

    public void openFamilyCallBack(final String ticketId) {
        final ProgressDialog progressDialog = CommonService.getCustomProgressDialog(this);
        progressDialog.show();
        // To emulate a 2 seconds time to let the user sees the incident list.
        android.os.Handler handler = new android.os.Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                progressDialog.hide();
                Activity currentActivity = (Activity) currentContext;
                Intent incidentDetail = new Intent(currentContext, TicketDetail.class);
                incidentDetail.putExtra("TICKET_ID", ticketId);
                currentActivity.startActivity(incidentDetail);
                currentActivity.overridePendingTransition(R.anim.right_slide_in,
                        R.anim.right_slide_out);
            }
        }, 2000);
    }

    private void searchFilterCriteria() {

        if (CommonValidations.validateEmpty(CommonSharedData.FilterCriteria)) {
            EditText findDaemon = (EditText) findViewById(R.id.search_field);
            findDaemon.setText(CommonSharedData.FilterCriteria);
            ServiceExecuter serviceExecuter = new ServiceExecuter();
            ServiceExecuter.ExecuteFilter filterService = serviceExecuter.new ExecuteFilter(currentContext);
            filterService.execute(CommonSharedData.FilterCriteria);
        }
    }

    private void SetConfiguredColors() {

        String primaryColorString = ConfigurationService.getConfigurationValue(this, "ColorPrimario");
        int primaryColor = Color.parseColor(primaryColorString);
        String secondaryColorString = ConfigurationService.getConfigurationValue(this, "ColorSecundario");
        int secondaryColor = Color.parseColor(secondaryColorString);

        Drawable ic_incidentes = this.getResources().getDrawable(R.drawable.ic_incidentes);
        Drawable logo_mini = this.getResources().getDrawable(R.drawable.logo_mini);
        Drawable ic_plus = this.getResources().getDrawable(R.drawable.ic_plus);
        Drawable ic_action_ticket_menu = this.getResources().getDrawable(R.drawable.ic_action_ticket_menu);
        Drawable ic_arrow = this.getResources().getDrawable(R.drawable.ic_arrow);
        View blue_separator = this.findViewById(R.id.blue_separator);
        LinearLayout search_container = (LinearLayout) this.findViewById(R.id.search_container);

        ic_incidentes.setColorFilter(primaryColor, PorterDuff.Mode.SRC_ATOP);
        logo_mini.setColorFilter(primaryColor, PorterDuff.Mode.SRC_ATOP);
        ic_plus.setColorFilter(primaryColor, PorterDuff.Mode.SRC_ATOP);
        ic_action_ticket_menu.setColorFilter(primaryColor, PorterDuff.Mode.SRC_ATOP);
        ic_arrow.setColorFilter(primaryColor, PorterDuff.Mode.SRC_ATOP);
        blue_separator.setBackgroundColor(primaryColor);
        search_container.setBackgroundColor(secondaryColor);
    }
}
