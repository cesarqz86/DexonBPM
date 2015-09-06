package us.dexon.dexonbpm.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.support.v4.view.ViewPager;
import android.text.method.ScrollingMovementMethod;
import android.util.Log;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
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
import us.dexon.dexonbpm.infrastructure.implementations.CommonSharedData;
import us.dexon.dexonbpm.infrastructure.implementations.CommonValidations;
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
                IDexonDatabaseWrapper dexonDatabase = DexonDatabaseWrapper.getInstance();
                dexonDatabase.setContext(this);

                LoginResponseDto loggedUser = dexonDatabase.getLoggedUser();

                if (CommonSharedData.TicketInfoUpdated == null) {
                    CommonSharedData.TicketInfoUpdated = CommonSharedData.TicketInfo;
                }

                JsonObject saveTicketInfo = CommonSharedData.TicketInfoUpdated.getTicketInfo();
                JsonObject headerInfo = CommonSharedData.TicketInfo.getTicketInfo().get("headerInfo").getAsJsonObject();
                saveTicketInfo.add("headerInfo", headerInfo);

                SaveTicketRequestDto ticketData = new SaveTicketRequestDto();
                ticketData.setLoggedUser(loggedUser);
                ticketData.setTicketInfo(saveTicketInfo);

                ServiceExecuter serviceExecuter = new ServiceExecuter();
                ServiceExecuter.ExecuteSaveTicket saveTicketService = serviceExecuter.new ExecuteSaveTicket(this);
                saveTicketService.execute(ticketData);

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

    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        CommonSharedData.TreeData = null;
    }

    public void inidentsCallBack(TicketResponseDto responseDto) {

        CommonSharedData.TicketInfo = responseDto;

        this.ticketData = responseDto;

        /*ListView lstvw_ticketdetail = (ListView) this.findViewById(R.id.lstvw_ticketdetail);
        lstvw_ticketdetail.setScrollContainer(false);
        TicketDetailAdapter detailAdapter = new TicketDetailAdapter(this, responseDto.getDataList(), responseDto);
        lstvw_ticketdetail.setAdapter(detailAdapter);*/
        this.drawTicket(responseDto);

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

    private void drawTicket(TicketResponseDto responseDto) {
        LinearLayout lstvw_ticketdetail = (LinearLayout) this.findViewById(R.id.lstvw_ticketdetail);
        lstvw_ticketdetail.removeAllViews();

        LayoutInflater inflater = this.getLayoutInflater();
        if (responseDto != null && responseDto.getDataList() != null && responseDto.getDataList().size() > 0) {
            for (TicketDetailDataDto itemDetail : responseDto.getDataList()) {
                View rowView = inflater.inflate(R.layout.item_detailticket, null);
                RenderControlType controlType = itemDetail.getFieldType();
                switch (controlType) {
                    case DXControlsTree: {
                        rowView = inflater.inflate(R.layout.item_detailticket_tree, null);
                        TextView txt_fieldtitle = (TextView) rowView.findViewById(R.id.txt_fieldtitle);
                        TextView txt_fieldvalue = (TextView) rowView.findViewById(R.id.txt_fieldvalue);

                        txt_fieldtitle.setText(itemDetail.getFieldName());
                        txt_fieldvalue.setText(itemDetail.getFieldValue());
                        if (this.ticketData.getIsOpen() && this.ticketData.getIsEditable()) {
                            rowView.setOnClickListener(new DexonListeners.ListViewClickListener(
                                    this,
                                    itemDetail.getFieldSonData(),
                                    itemDetail.getFieldKey()));
                        }
                        break;
                    }
                    case DXControlsGrid: {
                        rowView = inflater.inflate(R.layout.item_detailticket_tree, null);
                        TextView txt_fieldtitle = (TextView) rowView.findViewById(R.id.txt_fieldtitle);
                        TextView txt_fieldvalue = (TextView) rowView.findViewById(R.id.txt_fieldvalue);

                        txt_fieldtitle.setText(itemDetail.getFieldName());
                        txt_fieldvalue.setText(itemDetail.getFieldValue());
                        if (this.ticketData.getIsOpen() && this.ticketData.getIsEditable()) {
                            rowView.setOnClickListener(new DexonListeners.TableClickListener(
                                    this,
                                    itemDetail.getFieldSonData(),
                                    itemDetail.getFieldKey()));
                        }
                        break;
                    }
                    case DXControlsDate: {
                        TextView txt_fieldtitle = (TextView) rowView.findViewById(R.id.txt_fieldtitle);
                        TextView txt_fieldvalue = (TextView) rowView.findViewById(R.id.txt_fieldvalue);

                        String dateString = itemDetail.getFieldValue();
                        if (CommonValidations.validateEmpty(dateString)) {
                            try {
                                Date date = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss").parse(dateString);
                                dateString = new SimpleDateFormat("dd/MMM/yyyy").format(date);
                            } catch (Exception ex) {
                                Log.e("Converting Date: ", ex.getMessage(), ex);
                            }
                        }

                        txt_fieldtitle.setText(itemDetail.getFieldName());
                        txt_fieldvalue.setText(dateString);
                        break;
                    }
                    case DXControlsGridWithOptions: {
                        rowView = inflater.inflate(R.layout.item_detailticket_technician, null);

                        LinearLayout linear_select_technician = (LinearLayout) rowView.findViewById(R.id.linear_select_technician);
                        TextView txt_fieldtitle = (TextView) rowView.findViewById(R.id.txt_fieldtitle);
                        TextView txt_fieldvalue = (TextView) rowView.findViewById(R.id.txt_fieldvalue);

                        txt_fieldtitle.setText(itemDetail.getFieldName());
                        txt_fieldvalue.setText(itemDetail.getFieldValue());

                        RadioButton btn_setmanual_technician = (RadioButton) rowView.findViewById(R.id.btn_setmanual_technician);
                        RadioButton btn_setautomatic_technician = (RadioButton) rowView.findViewById(R.id.btn_setautomatic_technician);
                        RadioButton btn_settome_technician = (RadioButton) rowView.findViewById(R.id.btn_settome_technician);

                        if (this.ticketData.getIsOpen() && this.ticketData.getIsEditable()) {
                            DexonListeners.TechnicianClickListener technicianClickListener = new DexonListeners.TechnicianClickListener(
                                    this,
                                    this.ticketData.getCurrentTechnician(),
                                    this.ticketData);
                            btn_setmanual_technician.setOnClickListener(technicianClickListener);
                            btn_setautomatic_technician.setOnClickListener(technicianClickListener);
                            btn_settome_technician.setOnClickListener(technicianClickListener);
                        }

                        switch (this.ticketData.getTechnicianSelected()) {
                            case R.id.btn_setmanual_technician: {
                                btn_setmanual_technician.setChecked(true);
                                txt_fieldvalue.setCompoundDrawablesWithIntrinsicBounds(
                                        0, //left
                                        0, //top
                                        R.drawable.ic_arrow, //right
                                        0);//bottom
                                if (this.ticketData.getIsOpen() && this.ticketData.getIsEditable()) {
                                    linear_select_technician.setOnClickListener(new DexonListeners.TableClickListener(
                                            this,
                                            itemDetail.getFieldSonData(),
                                            itemDetail.getFieldKey()));
                                }
                                break;
                            }
                            case R.id.btn_setautomatic_technician: {
                                btn_setautomatic_technician.setChecked(true);
                                txt_fieldvalue.setCompoundDrawablesWithIntrinsicBounds(
                                        0, //left
                                        0, //top
                                        0, //right
                                        0);//bottom
                                break;
                            }
                            case R.id.btn_settome_technician: {
                                btn_settome_technician.setChecked(true);
                                txt_fieldvalue.setCompoundDrawablesWithIntrinsicBounds(
                                        0, //left
                                        0, //top
                                        0, //right
                                        0);//bottom
                                break;
                            }
                        }
                        break;
                    }
                    case DXControlsMultiline: {
                        rowView = inflater.inflate(R.layout.item_detailticket_text_multi, null);
                        TextView txt_fieldtitle = (TextView) rowView.findViewById(R.id.txt_fieldtitle);
                        TextView txt_fieldvalue = (TextView) rowView.findViewById(R.id.txt_fieldvalue);

                        txt_fieldtitle.setText(itemDetail.getFieldName());
                        txt_fieldvalue.setText(itemDetail.getFieldValue());
                        //txt_fieldvalue.setMovementMethod(new ScrollingMovementMethod());
                        break;
                    }
                    default: {
                        TextView txt_fieldtitle = (TextView) rowView.findViewById(R.id.txt_fieldtitle);
                        TextView txt_fieldvalue = (TextView) rowView.findViewById(R.id.txt_fieldvalue);

                        txt_fieldtitle.setText(itemDetail.getFieldName());
                        txt_fieldvalue.setText(itemDetail.getFieldValue());
                        break;
                    }
                }
                lstvw_ticketdetail.addView(rowView);
            }
        }
    }

}
