package us.dexon.dexonbpm.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.util.Log;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;

import java.text.SimpleDateFormat;
import java.util.Date;

import us.dexon.dexonbpm.R;
import us.dexon.dexonbpm.infrastructure.implementations.CommonSharedData;
import us.dexon.dexonbpm.infrastructure.implementations.CommonValidations;
import us.dexon.dexonbpm.infrastructure.implementations.DexonListeners;

public class HistoryActivity extends FragmentActivity {

    private LinearLayout lstvw_historydata;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_history);

        Intent currentIntent = this.getIntent();
        String currentTicketID = currentIntent.getStringExtra("ViewTitle");
        if(CommonValidations.validateEmpty(currentTicketID)){
            this.setTitle(currentTicketID);
        }

        this.lstvw_historydata = (LinearLayout) this.findViewById(R.id.lstvw_historydata);
        LayoutInflater inflater = this.getLayoutInflater();

        if (CommonSharedData.TicketInfo != null) {
            JsonObject jsonTicket = CommonSharedData.TicketInfo.getTicketInfo();
            this.drawHistoryData(jsonTicket, inflater);
        }
    }

    public void drawHistoryData(JsonObject ticketInfo, LayoutInflater inflater) {

        this.lstvw_historydata.removeAllViews();

        if (ticketInfo != null && ticketInfo.has("textHistories") && !ticketInfo.get("textHistories").isJsonNull()) {

            JsonArray ticketHistories = ticketInfo.get("textHistories").getAsJsonArray();
            JsonObject ticketHistoriesObject = null;
            if (ticketHistories != null && ticketHistories.size() > 0){

                for (int index = 0; index < ticketHistories.size(); index++) {
                    ticketHistoriesObject = ticketHistories.get(index).getAsJsonObject();

                    if (ticketHistoriesObject != null) {

                        if (ticketHistoriesObject.has("ticketEvent")) {
                            View rowView = inflater.inflate(R.layout.item_detailticket, null);
                            TextView txt_fieldtitle = (TextView) rowView.findViewById(R.id.txt_fieldtitle);
                            TextView txt_fieldvalue = (TextView) rowView.findViewById(R.id.txt_fieldvalue);

                            String fieldValue = ticketHistoriesObject.get("ticketEvent").isJsonNull()? "" : ticketHistoriesObject.get("ticketEvent").getAsString();

                            txt_fieldtitle.setText("ticketEvent");
                            txt_fieldvalue.setText(fieldValue);
                            this.lstvw_historydata.addView(rowView);
                        }
                        if (ticketHistoriesObject.has("ticketComment") && !ticketHistoriesObject.get("ticketComment").isJsonNull()) {
                            View rowView = inflater.inflate(R.layout.item_detailticket_tree, null);
                            TextView txt_fieldtitle = (TextView) rowView.findViewById(R.id.txt_fieldtitle);
                            TextView txt_fieldvalue = (TextView) rowView.findViewById(R.id.txt_fieldvalue);

                            String fieldValue = ticketHistoriesObject.get("ticketComment").isJsonNull()? "" : ticketHistoriesObject.get("ticketComment").getAsString();

                            txt_fieldtitle.setText("ticketComment");
                            txt_fieldvalue.setText(fieldValue);

                            rowView.setOnClickListener(new DexonListeners.MultilineClickListener(
                                    this,
                                    null,
                                    fieldValue,
                                    false));

                            this.lstvw_historydata.addView(rowView);
                        }
                        if (ticketHistoriesObject.has("ticketEventDate") && !ticketHistoriesObject.get("ticketEventDate").isJsonNull()) {
                            View rowView = inflater.inflate(R.layout.item_detailticket, null);
                            TextView txt_fieldtitle = (TextView) rowView.findViewById(R.id.txt_fieldtitle);
                            TextView txt_fieldvalue = (TextView) rowView.findViewById(R.id.txt_fieldvalue);

                            String fieldValue = ticketHistoriesObject.get("ticketEventDate").isJsonNull()? "" : ticketHistoriesObject.get("ticketEventDate").getAsString();

                            String dateString = fieldValue;
                            Date date = new Date();
                            if (CommonValidations.validateEmpty(dateString)) {
                                try {
                                    date = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss").parse(dateString);
                                    dateString = new SimpleDateFormat("dd/MMM/yyyy").format(date);
                                } catch (Exception ex) {
                                    Log.e("Converting Date: ", ex.getMessage(), ex);
                                }
                            }

                            txt_fieldtitle.setText("ticketEventDate");
                            txt_fieldvalue.setText(dateString);
                            this.lstvw_historydata.addView(rowView);
                        }
                        if (ticketHistoriesObject.has("ticketEventDoneBy") && !ticketHistoriesObject.get("ticketEventDoneBy").isJsonNull()) {
                            View rowView = inflater.inflate(R.layout.item_detailticket, null);
                            TextView txt_fieldtitle = (TextView) rowView.findViewById(R.id.txt_fieldtitle);
                            TextView txt_fieldvalue = (TextView) rowView.findViewById(R.id.txt_fieldvalue);

                            String fieldValue = ticketHistoriesObject.get("ticketEventDoneBy").isJsonNull()? "" : ticketHistoriesObject.get("ticketEventDoneBy").getAsString();

                            txt_fieldtitle.setText("ticketEventDoneBy");
                            txt_fieldvalue.setText(fieldValue);
                            this.lstvw_historydata.addView(rowView);
                        }

                        View lineSeparator = inflater.inflate(R.layout.line_separator, null);
                        this.lstvw_historydata.addView(lineSeparator);
                    }
                }
            }
        }
    }
}
