package us.dexon.dexonbpm.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.EditText;

import com.google.gson.JsonObject;

import us.dexon.dexonbpm.R;
import us.dexon.dexonbpm.infrastructure.implementations.CommonService;
import us.dexon.dexonbpm.infrastructure.implementations.CommonSharedData;
import us.dexon.dexonbpm.infrastructure.implementations.CommonValidations;
import us.dexon.dexonbpm.infrastructure.implementations.DexonDatabaseWrapper;
import us.dexon.dexonbpm.infrastructure.implementations.ServiceExecuter;
import us.dexon.dexonbpm.infrastructure.implementations.TicketService;
import us.dexon.dexonbpm.infrastructure.interfaces.IDexonDatabaseWrapper;
import us.dexon.dexonbpm.infrastructure.interfaces.ITicketService;
import us.dexon.dexonbpm.model.ReponseDTO.LoginResponseDto;
import us.dexon.dexonbpm.model.RequestDTO.ReopenRequestDto;

public class MultiLineActivity extends FragmentActivity {

    private EditText txt_fieldvalue;
    private String elementKey;
    private Menu menu;
    private boolean isControlEditable;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_multi_line);

        this.txt_fieldvalue = (EditText) this.findViewById(R.id.txt_fieldvalue);

        Intent currentIntent = this.getIntent();
        this.isControlEditable = currentIntent.getBooleanExtra("IsEditable", true);
        this.elementKey = currentIntent.getStringExtra("ElementKey");

        if (CommonSharedData.MultilineData != null) {
            JsonObject jsonField = CommonSharedData.MultilineData;
            String fieldName = jsonField.get("display_name").getAsString();

            String fieldValue = "";
            if (jsonField.has("son") && !jsonField.get("son").isJsonNull()) {
                JsonObject sonObject = jsonField.get("son").getAsJsonObject();
                if (sonObject.has("short_description") && !sonObject.get("short_description").isJsonNull()) {
                    fieldValue = sonObject.get("short_description").isJsonNull() ? "" : sonObject.get("short_description").getAsString();
                }
            } else if (jsonField.has("Value")) {
                fieldValue = jsonField.get("Value").isJsonNull() ? "" : jsonField.get("Value").getAsString();
            }

            this.txt_fieldvalue.setText(fieldValue);
            this.setTitle(fieldName);
        }

        if (CommonSharedData.MultilineDataValue != null) {
            this.txt_fieldvalue.setText(CommonSharedData.MultilineDataValue);
        }

        this.txt_fieldvalue.setEnabled(this.isControlEditable);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        CommonSharedData.MultilineData = null;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu items for use in the action bar
        this.menu = menu;
        if (this.isControlEditable) {
            MenuInflater inflater = getMenuInflater();
            inflater.inflate(R.menu.menu_done, menu);
        }
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_done: {
                this.saveMultilineData();
                break;
            }
        }
        return super.onOptionsItemSelected(item);
    }

    private void saveMultilineData() {

        if (CommonSharedData.MultilineData != null) {
            JsonObject jsonField = CommonSharedData.MultilineData;

            String fieldValue = this.txt_fieldvalue.getText().toString();
            if (jsonField.has("son") && !jsonField.get("son").isJsonNull()) {
                JsonObject sonObject = jsonField.get("son").getAsJsonObject();
                sonObject.addProperty("short_description", fieldValue);
            } else if (jsonField.has("Value")) {
                jsonField.addProperty("Value", fieldValue);
            }

            if (CommonValidations.validateEmpty(this.elementKey)) {
                if (CommonSharedData.RelatedDataActivity != null) {
                } else {
                    JsonObject ticketJsonInfo = CommonSharedData.TicketInfo.getTicketInfo();
                    JsonObject headerInfo = ticketJsonInfo.get("headerInfo").getAsJsonObject();
                    headerInfo.add(this.elementKey, jsonField);

                    ticketJsonInfo.add("headerInfo", headerInfo);
                    ITicketService ticketService = TicketService.getInstance();
                    CommonSharedData.TicketInfo = ticketService.convertToTicketData(ticketJsonInfo, R.id.btn_setmanual_technician, null);

                    TicketDetail ticketDetail = null;
                    NewTicketActivity newTicket = null;
                    Intent ticketDetailActivity = null;

                    if (CommonSharedData.TicketActivity instanceof TicketDetail) {
                        ticketDetail = (TicketDetail) CommonSharedData.TicketActivity;
                        ticketDetailActivity = new Intent(this, TicketDetail.class);
                    } else if (CommonSharedData.TicketActivity instanceof NewTicketActivity) {
                        newTicket = (NewTicketActivity) CommonSharedData.TicketActivity;
                        ticketDetailActivity = new Intent(this, NewTicketActivity.class);
                    }

                    if (ticketDetail != null)
                        ticketDetail.inidentsCallBack(CommonSharedData.TicketInfo);

                    if (newTicket != null)
                        newTicket.inidentsCallBack(CommonSharedData.TicketInfo);

                    ticketDetailActivity.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
                    this.startActivity(ticketDetailActivity);
                    this.overridePendingTransition(R.anim.right_slide_in,
                            R.anim.right_slide_out);
                }
            }
        }
    }

}
