package us.dexon.dexonbpm.activity;

import android.content.Intent;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import java.text.SimpleDateFormat;
import java.util.Date;

import us.dexon.dexonbpm.R;
import us.dexon.dexonbpm.infrastructure.enums.RenderControlType;
import us.dexon.dexonbpm.infrastructure.implementations.CommonSharedData;
import us.dexon.dexonbpm.infrastructure.implementations.CommonValidations;
import us.dexon.dexonbpm.infrastructure.implementations.DexonListeners;

public class RelatedDataActivity extends FragmentActivity {

    private String nodeData;
    private LinearLayout lstvw_tree_detail;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_related_data);

        this.lstvw_tree_detail = (LinearLayout) this.findViewById(R.id.lstvw_tree_detail);

        JsonParser gsonSerializer = new JsonParser();

        Intent currentIntent = this.getIntent();
        String activityTitle = currentIntent.getStringExtra("activityTitle");
        this.setTitle(activityTitle);

        this.nodeData = currentIntent.getStringExtra("nodeData");
        JsonObject jsonNodeData = gsonSerializer.parse(this.nodeData).getAsJsonObject();
        CommonSharedData.RelatedData = jsonNodeData;
        this.drawRelatedData(jsonNodeData);

        CommonSharedData.RelatedDataActivity = this;
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        CommonSharedData.RelatedDataActivity = null;
    }

    public void drawRelatedData(JsonObject jsonData) {
        this.lstvw_tree_detail.removeAllViews();

        if (jsonData.has("multipleValues") && !jsonData.get("multipleValues").isJsonNull()) {
            this.drawMultipleRelatedData(jsonData);
        } else {
            this.drawSingleRelatedData(jsonData);
        }
    }

    private void drawMultipleRelatedData(JsonObject jsonData) {
        JsonArray fields = jsonData.get("multipleValues").getAsJsonArray();
        for (int index = 0; index < fields.size(); index++) {
            this.drawSingleRelatedData(fields.get(index).getAsJsonObject());
        }
    }

    private void drawSingleRelatedData(JsonObject jsonData) {
        JsonArray fields = jsonData.get("fields").getAsJsonArray();
        for (int index = 0; index < fields.size(); index++) {
            this.drawRelatedDataControls(fields.get(index).getAsJsonObject());
        }
    }

    private void drawRelatedDataControls(JsonObject jsonField) {

        Gson gsonSerializer = new Gson();
        LayoutInflater inflater = this.getLayoutInflater();

        if (jsonField.has("is_hidden") && !jsonField.get("is_hidden").getAsBoolean()) {
            View rowView = inflater.inflate(R.layout.item_detailticket_int, null);
            RenderControlType controlType = RenderControlType.values()[jsonField.get("render_ctl").getAsInt()];
            String fieldKey = jsonField.get("field_name").getAsString();
            String fieldName = jsonField.get("display_name").getAsString();
            String fieldValue = "";
            if (jsonField.has("son") && !jsonField.get("son").isJsonNull()) {
                JsonObject sonObject = jsonField.get("son").getAsJsonObject();
                if (sonObject.has("short_description") && !sonObject.get("short_description").isJsonNull()) {
                    fieldValue = sonObject.get("short_description").getAsString();
                }
            } else if (jsonField.has("Value")) {
                fieldValue = jsonField.get("Value").getAsString();
            }

            switch (controlType) {
                case DXControlsLabel: {
                    rowView = inflater.inflate(R.layout.item_detailticket_tree, null);
                    TextView txt_fieldtitle = (TextView) rowView.findViewById(R.id.txt_fieldtitle);
                    TextView txt_fieldvalue = (TextView) rowView.findViewById(R.id.txt_fieldvalue);

                    txt_fieldtitle.setText(fieldName);
                    txt_fieldvalue.setText(fieldValue);
                    break;
                }
                case DXControlsTree: {
                    String sonString = gsonSerializer.toJson(jsonField.get("son"));

                    rowView = inflater.inflate(R.layout.item_detailticket_tree, null);
                    TextView txt_fieldtitle = (TextView) rowView.findViewById(R.id.txt_fieldtitle);
                    TextView txt_fieldvalue = (TextView) rowView.findViewById(R.id.txt_fieldvalue);

                    txt_fieldtitle.setText(fieldName);
                    txt_fieldvalue.setText(fieldValue);
                    rowView.setOnClickListener(new DexonListeners.ListViewClickListener(
                            this,
                            sonString,
                            fieldKey));
                    break;
                }
                case DXControlsGrid: {
                    String sonString = gsonSerializer.toJson(jsonField.get("son"));

                    rowView = inflater.inflate(R.layout.item_detailticket_tree, null);
                    TextView txt_fieldtitle = (TextView) rowView.findViewById(R.id.txt_fieldtitle);
                    TextView txt_fieldvalue = (TextView) rowView.findViewById(R.id.txt_fieldvalue);

                    txt_fieldtitle.setText(fieldName);
                    txt_fieldvalue.setText(fieldValue);

                    rowView.setOnClickListener(new DexonListeners.TableClickListener(
                            this,
                            sonString,
                            fieldKey));
                    break;
                }
                case DXControlsDate: {
                    rowView = inflater.inflate(R.layout.item_detailticket, null);

                    TextView txt_fieldtitle = (TextView) rowView.findViewById(R.id.txt_fieldtitle);
                    TextView txt_fieldvalue = (TextView) rowView.findViewById(R.id.txt_fieldvalue);

                    String dateString = fieldValue;
                    if (CommonValidations.validateEmpty(dateString)) {
                        try {
                            Date date = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss").parse(dateString);
                            dateString = new SimpleDateFormat("dd/MMM/yyyy").format(date);
                        } catch (Exception ex) {
                            Log.e("Converting Date: ", ex.getMessage(), ex);
                        }
                    }

                    txt_fieldtitle.setText(fieldName);
                    txt_fieldvalue.setText(dateString);
                    break;
                }
                case DXControlsMultiline: {
                    rowView = inflater.inflate(R.layout.item_detailticket_text_multi, null);
                    TextView txt_fieldtitle = (TextView) rowView.findViewById(R.id.txt_fieldtitle);
                    TextView txt_fieldvalue = (TextView) rowView.findViewById(R.id.txt_fieldvalue);

                    txt_fieldtitle.setText(fieldName);
                    txt_fieldvalue.setText(fieldValue);
                    break;
                }
                default: {
                    TextView txt_fieldtitle = (TextView) rowView.findViewById(R.id.txt_fieldtitle);
                    EditText txt_fieldvalue = (EditText) rowView.findViewById(R.id.txt_fieldvalue);

                    txt_fieldtitle.setText(fieldName);
                    txt_fieldvalue.setText(fieldValue);
                    break;
                }
            }
            this.lstvw_tree_detail.addView(rowView);
        }
    }
}
