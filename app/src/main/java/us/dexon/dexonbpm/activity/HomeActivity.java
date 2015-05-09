package us.dexon.dexonbpm.activity;

import android.content.Context;
import android.content.Intent;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import us.dexon.dexonbpm.R;

public class HomeActivity extends ActionBarActivity {

    // Updating the test commit 2

    public Context context;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
        this.context = this;
        this.LoadMenu();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_home, menu);
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

    private void LoadMenu() {
        String[] menuOptionList;
        Boolean isTechUser = this.getIntent().getBooleanExtra("isTech", false);
        if (isTechUser) {
            menuOptionList = this.getResources().getStringArray(R.array.array_menu_techuser);
        } else {
            menuOptionList = this.getResources().getStringArray(R.array.array_menu_finaluser);
        }

        ArrayAdapter<String> menuAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, menuOptionList);
        ListView lstvw_menu = (ListView) this.findViewById(R.id.lstvw_menu);
        lstvw_menu.setAdapter(menuAdapter);
        lstvw_menu.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            public void onItemClick(AdapterView<?> parent, View itemClicked, int position, long id) {
                ArrayAdapter customAdapter = (ArrayAdapter) parent.getAdapter();
                String selectedText = (String) customAdapter.getItem(position);
                switch (selectedText) {
                    case "Incidentes":
                    {
                        Intent incidentIntent = new Intent(itemClicked.getContext(), IncidentsActivity.class);
                        startActivity(incidentIntent);
                        break;
                    }
                }

            }
        });
    }

}
