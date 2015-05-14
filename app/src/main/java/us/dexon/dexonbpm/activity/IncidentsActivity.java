package us.dexon.dexonbpm.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.view.ContextMenu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageButton;
import android.widget.TableLayout;

import us.dexon.dexonbpm.R;
import us.dexon.dexonbpm.infrastructure.implementations.CommonService;

public class IncidentsActivity extends FragmentActivity {

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
}
