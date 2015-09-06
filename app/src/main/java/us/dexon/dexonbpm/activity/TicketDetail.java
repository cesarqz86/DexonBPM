package us.dexon.dexonbpm.activity;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.support.v4.view.ViewPager;
import android.text.method.ScrollingMovementMethod;
import android.util.Log;
import android.util.TypedValue;
import android.view.ContextMenu;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RadioButton;
import android.widget.ScrollView;
import android.widget.TextView;

import com.akexorcist.roundcornerprogressbar.RoundCornerProgressBar;
import com.github.lzyzsd.circleprogress.DonutProgress;
import com.google.gson.JsonObject;
import com.viewpagerindicator.CirclePageIndicator;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.zip.Inflater;

import us.dexon.dexonbpm.R;
import us.dexon.dexonbpm.adapters.ProgressPagerAdapter;
import us.dexon.dexonbpm.adapters.TicketDetailAdapter;
import us.dexon.dexonbpm.infrastructure.enums.RenderControlType;
import us.dexon.dexonbpm.infrastructure.implementations.CommonService;
import us.dexon.dexonbpm.infrastructure.implementations.CommonSharedData;
import us.dexon.dexonbpm.infrastructure.implementations.CommonValidations;
import us.dexon.dexonbpm.infrastructure.implementations.ConfigurationService;
import us.dexon.dexonbpm.infrastructure.implementations.DexonDatabaseWrapper;
import us.dexon.dexonbpm.infrastructure.implementations.DexonListeners;
import us.dexon.dexonbpm.infrastructure.implementations.ServiceExecuter;
import us.dexon.dexonbpm.infrastructure.interfaces.IDexonDatabaseWrapper;
import us.dexon.dexonbpm.model.ReponseDTO.LoginResponseDto;
import us.dexon.dexonbpm.model.ReponseDTO.TicketDetailDataDto;
import us.dexon.dexonbpm.model.ReponseDTO.TicketResponseDto;
import us.dexon.dexonbpm.model.RequestDTO.ReloadRequestDto;
import us.dexon.dexonbpm.model.RequestDTO.ReopenRequestDto;
import us.dexon.dexonbpm.model.RequestDTO.SaveTicketRequestDto;
import us.dexon.dexonbpm.model.RequestDTO.TicketDetailRequestDto;

/**
 * Created by androide on 27/05/15.
 */
public class TicketDetail extends FragmentActivity {

    private String ticketId = "";
    private Menu menu;
    private TicketResponseDto ticketData;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ticket_detail);

        ImageButton menuButton = (ImageButton) findViewById(R.id.menu_button);
        this.registerForContextMenu(menuButton);

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

        CommonSharedData.TicketActivity = this;
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
                CommonService.saveTicket(this);
                break;
            }
            case R.id.action_reopen: {
                IDexonDatabaseWrapper dexonDatabase = DexonDatabaseWrapper.getInstance();
                dexonDatabase.setContext(this);

                LoginResponseDto loggedUser = dexonDatabase.getLoggedUser();

                ReopenRequestDto ticketData = new ReopenRequestDto();
                ticketData.setLoggedUser(loggedUser);
                ticketData.setTicketInfo(this.ticketData.getTicketInfo());

                ServiceExecuter serviceExecuter = new ServiceExecuter();
                ServiceExecuter.ExecuteReopenTicket reopenService = serviceExecuter.new ExecuteReopenTicket(this);
                reopenService.execute(ticketData);
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
                inflater.inflate(R.menu.menu_ticket_detail_submenu, menu);
                break;
        }
    }

    @Override
    public boolean onContextItemSelected(MenuItem item) {
        // Previamente creamos el objeto TextView y lo inicializamos para poder
        // asignarle aquí el texto en función de la opción seleccionada.
        switch (item.getItemId()) {
            case R.id.button2_menu_opt1:
                // Create son
                return true;
            case R.id.button2_menu_opt2:
                // Create brother
                return true;
            case R.id.button2_menu_opt3:
                // Print ticket
                return true;
            case R.id.button2_menu_opt4:
                // Recalculate SLA
                return true;
            case R.id.button2_menu_opt5:
                // Cancel does nothing
                return true;
            default:
                return super.onContextItemSelected(item);
        }
    }

    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        CommonSharedData.TreeData = null;
    }

    public void logoClick(View view) {
        Intent webIntent = new Intent();
        webIntent.setAction(Intent.ACTION_VIEW);
        webIntent.addCategory(Intent.CATEGORY_BROWSABLE);
        webIntent.setData(Uri.parse(this.getString(R.string.dexon_website)));
        this.startActivity(webIntent);
    }

    public void menuClick(View view) {
        this.openContextMenu(view);
    }

    public void inidentsCallBack(TicketResponseDto responseDto) {

        CommonSharedData.TicketInfo = responseDto;

        this.ticketData = responseDto;

        /*ListView lstvw_ticketdetail = (ListView) this.findViewById(R.id.lstvw_ticketdetail);
        lstvw_ticketdetail.setScrollContainer(false);
        TicketDetailAdapter detailAdapter = new TicketDetailAdapter(this, responseDto.getDataList(), responseDto);
        lstvw_ticketdetail.setAdapter(detailAdapter);*/
        //this.drawTicket(responseDto);
        CommonService.drawTicket(responseDto, this);

        String activityTitle = this.getString(R.string.app_name);
        for (TicketDetailDataDto ticketData : responseDto.getDataList()) {
            if (ticketData.getFieldKey().equals("ticket")) {
                activityTitle = ticketData.getFieldValue();
                break;
            }
        }
        this.setTitle(activityTitle);

        // Menu options
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

        // Progress Pager
        ProgressPagerAdapter progressPagerAdapter = new ProgressPagerAdapter(
                this.getSupportFragmentManager(), responseDto);
        ViewPager progress_pager = (ViewPager) this.findViewById(R.id.progress_pager);
        progress_pager.setAdapter(progressPagerAdapter);

        CirclePageIndicator progress_pager_indicator = (CirclePageIndicator) this.findViewById(R.id.progress_pager_indicator);
        progress_pager_indicator.setViewPager(progress_pager);

        if (responseDto.getBarPercentDone() == null) {
            progress_pager_indicator.setVisibility(View.INVISIBLE);
        }
    }

    public void reloadCallback(JsonObject ticketInfo) {

        IDexonDatabaseWrapper dexonDatabase = DexonDatabaseWrapper.getInstance();
        dexonDatabase.setContext(this);

        LoginResponseDto loggedUser = dexonDatabase.getLoggedUser();

        ReloadRequestDto reloadData = new ReloadRequestDto();
        reloadData.setTicketInfo(ticketInfo);
        reloadData.setLoggedUser(loggedUser);

        ServiceExecuter serviceExecuter = new ServiceExecuter();
        ServiceExecuter.ExecuteReloadTicket ticketService = serviceExecuter.new ExecuteReloadTicket(this);
        ticketService.execute(reloadData);
    }

}
