package us.dexon.dexonbpm.activity;

import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TableLayout;

import us.dexon.dexonbpm.R;
import us.dexon.dexonbpm.infrastructure.implementations.CommonService;

public class IncidentsActivity extends ActionBarActivity {

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
        for (int rows = 0; rows <= 20; rows++) {
            tbl_incident_data = CommonService.AddRowToTable(this, tbl_incident_data, false, headerText);
        }

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_incidents, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
