package us.dexon.dexonbpm.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.widget.EditText;

import com.google.gson.JsonObject;

import us.dexon.dexonbpm.R;
import us.dexon.dexonbpm.infrastructure.implementations.CommonSharedData;
import us.dexon.dexonbpm.infrastructure.implementations.CommonValidations;
import us.dexon.dexonbpm.infrastructure.implementations.TicketService;
import us.dexon.dexonbpm.infrastructure.interfaces.ITicketService;

public class MultiLineActivity extends FragmentActivity {

    private EditText txt_fieldvalue;
    private String elementKey;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_multi_line);

        this.txt_fieldvalue = (EditText) this.findViewById(R.id.txt_fieldvalue);

        Intent currentIntent = this.getIntent();
        boolean isControlEditable = currentIntent.getBooleanExtra("IsEditable", true);
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

        this.txt_fieldvalue.setEnabled(isControlEditable);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (CommonSharedData.MultilineData != null) {
            JsonObject jsonField = CommonSharedData.MultilineData;
            String fieldName = jsonField.get("display_name").getAsString();

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
        CommonSharedData.MultilineData = null;
    }

}
