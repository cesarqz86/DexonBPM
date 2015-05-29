package us.dexon.dexonbpm.activity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.ListView;

import us.dexon.dexonbpm.R;
import us.dexon.dexonbpm.infrastructure.enums.TicketFilter;

public class HomeActivity extends FragmentActivity {

    private int currentFilter = TicketFilter.AssignedTickets.getCode();
    private boolean includeClose;
    CheckBox chk_include_close;

    public Context context;
    static final int FILTER_INCIDENT_CODE = 1;  // The request code

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
        this.context = this;
        Intent currentIntent = this.getIntent();
        this.currentFilter = currentIntent.getIntExtra("CurrentFilter", TicketFilter.AssignedTickets.getCode());
        this.includeClose = currentIntent.getBooleanExtra("IncludeClose", false);

        this.chk_include_close = (CheckBox) this.findViewById(R.id.chk_include_close);
        this.chk_include_close.setChecked(this.includeClose);

        this.LoadMenu();

    }

    private void LoadMenu() {
        String[] menuOptionList;
        Boolean isTechUser = this.getIntent().getBooleanExtra("isTech", false);
        if (isTechUser) {
            menuOptionList = this.getResources().getStringArray(R.array.array_menu_techuser);
        } else {
            menuOptionList = this.getResources().getStringArray(R.array.array_menu_finaluser);
        }

        ArrayAdapter<String> menuAdapter = new ArrayAdapter(this, android.R.layout.simple_list_item_1, menuOptionList);
        final ListView lstvw_menu = (ListView) this.findViewById(R.id.lstvw_menu);
        lstvw_menu.setAdapter(menuAdapter);
        lstvw_menu.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            public void onItemClick(AdapterView<?> parent, View itemClicked, int position, long id) {
                ArrayAdapter customAdapter = (ArrayAdapter) parent.getAdapter();
                String selectedText = (String) customAdapter.getItem(position);
                Intent incidentIntent = new Intent();
                incidentIntent.putExtra("CurrentFilter", selectedText);
                CheckBox chk_include_closelocal = (CheckBox) ((HomeActivity) context).findViewById(R.id.chk_include_close);
                incidentIntent.putExtra("IncludeClose", chk_include_closelocal.isChecked());
                setResult(FILTER_INCIDENT_CODE, incidentIntent);
                finish();
            }
        });
        lstvw_menu.requestFocusFromTouch();
        TicketFilter ticketFilter = TicketFilter.GetValue(this.currentFilter);
        int itemPosition = menuAdapter.getPosition(ticketFilter.getTitle());
        lstvw_menu.setSelection(itemPosition);
    }

}
