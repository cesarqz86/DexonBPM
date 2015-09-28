package us.dexon.dexonbpm.activity;

import android.content.Intent;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;
import android.util.Log;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.ToggleButton;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import java.text.SimpleDateFormat;
import java.util.Date;

import us.dexon.dexonbpm.R;
import us.dexon.dexonbpm.infrastructure.enums.RenderControlType;
import us.dexon.dexonbpm.infrastructure.implementations.CommonService;
import us.dexon.dexonbpm.infrastructure.implementations.CommonSharedData;
import us.dexon.dexonbpm.infrastructure.implementations.CommonValidations;
import us.dexon.dexonbpm.infrastructure.implementations.DexonListeners;

public class RelatedDataActivity extends FragmentActivity {

    private String nodeData;
    private JsonObject jsonNodeData;
    private Menu menu;
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
        this.jsonNodeData = gsonSerializer.parse(this.nodeData).getAsJsonObject();
        CommonSharedData.RelatedData = jsonNodeData;
        this.drawRelatedData(jsonNodeData);

        CommonSharedData.RelatedDataActivity = this;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu items for use in the action bar
        this.menu = menu;
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_related_data, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_save: {
                this.jsonNodeData = this.getDataFromView();
                CommonService.saveRelatedData(this, this.jsonNodeData);
                break;
            }
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        CommonSharedData.RelatedDataActivity = null;
    }

    public void drawRelatedData(JsonObject jsonData) {
        this.lstvw_tree_detail.removeAllViews();
        LayoutInflater inflater = this.getLayoutInflater();

        if (jsonData.has("multipleValues") && !jsonData.get("multipleValues").isJsonNull()) {
            this.drawMultipleRelatedData(jsonData, inflater);
        } else {
            this.drawSingleRelatedData(jsonData, inflater);
        }

        if (jsonData.has("details")) {
            View rowView = inflater.inflate(R.layout.item_disclosure, null);
            TextView txt_fieldtitle = (TextView) rowView.findViewById(R.id.txt_fieldtitle);
            txt_fieldtitle.setText(this.getResources().getString(R.string.txt_relateddata_detail_title));
            txt_fieldtitle.setTextSize(TypedValue.COMPLEX_UNIT_SP, 15f);
            rowView.setOnClickListener(new DexonListeners.DetailRelatedDataClickListener(this, this.nodeData));
            this.lstvw_tree_detail.addView(rowView);
        }
    }

    private void drawMultipleRelatedData(JsonObject jsonData, LayoutInflater inflater) {
        JsonArray fields = jsonData.get("multipleValues").getAsJsonArray();
        for (int index = 0; index < fields.size(); index++) {
            this.drawSingleRelatedData(fields.get(index).getAsJsonObject(), inflater);
        }
    }

    private void drawSingleRelatedData(JsonObject jsonData, LayoutInflater inflater) {
        JsonArray fields = jsonData.get("fields").getAsJsonArray();
        for (int index = 0; index < fields.size(); index++) {
            this.drawRelatedDataControls(fields.get(index).getAsJsonObject(), inflater);
        }
    }

    private void drawRelatedDataControls(JsonObject jsonField, LayoutInflater inflater) {

        Gson gsonSerializer = new Gson();

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
                case DXControlsFloat:
                case DXControlsCurrency: {
                    rowView = inflater.inflate(R.layout.item_detailticket_float, null);
                    TextView txt_fieldtitle = (TextView) rowView.findViewById(R.id.txt_fieldtitle);
                    TextView txt_fieldvalue = (TextView) rowView.findViewById(R.id.txt_fieldvalue);

                    txt_fieldtitle.setText(fieldName);
                    txt_fieldvalue.setText(fieldValue);
                    break;
                }
                case DXControlsCharacter:
                case DXControlsLine: {
                    rowView = inflater.inflate(R.layout.item_detailticket_text, null);
                    TextView txt_fieldtitle = (TextView) rowView.findViewById(R.id.txt_fieldtitle);
                    TextView txt_fieldvalue = (TextView) rowView.findViewById(R.id.txt_fieldvalue);

                    txt_fieldtitle.setText(fieldName);
                    txt_fieldvalue.setText(fieldValue);
                    break;
                }
                case DXControlsPassword: {
                    rowView = inflater.inflate(R.layout.item_detailticket_password, null);
                    TextView txt_fieldtitle = (TextView) rowView.findViewById(R.id.txt_fieldtitle);
                    TextView txt_fieldvalue = (TextView) rowView.findViewById(R.id.txt_fieldvalue);

                    txt_fieldtitle.setText(fieldName);
                    txt_fieldvalue.setText(fieldValue);
                    break;
                }
                case DXControlsPhone: {
                    rowView = inflater.inflate(R.layout.item_detailticket_text, null);
                    TextView txt_fieldtitle = (TextView) rowView.findViewById(R.id.txt_fieldtitle);
                    TextView txt_fieldvalue = (TextView) rowView.findViewById(R.id.txt_fieldvalue);

                    txt_fieldtitle.setText(fieldName);
                    txt_fieldvalue.setText(fieldValue);
                    break;
                }
                case DXControlsCheckbox: {
                    rowView = inflater.inflate(R.layout.item_detailticket_bool, null);
                    TextView txt_fieldtitle = (TextView) rowView.findViewById(R.id.txt_fieldtitle);
                    ToggleButton txt_fieldvalue = (ToggleButton) rowView.findViewById(R.id.txt_fieldvalue);

                    txt_fieldtitle.setText(fieldName);
                    boolean toggleState = Boolean.parseBoolean(fieldValue);
                    txt_fieldvalue.setChecked(toggleState);
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
                    Date date = new Date();
                    if (CommonValidations.validateEmpty(dateString)) {
                        try {
                            date = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss").parse(dateString);
                            dateString = new SimpleDateFormat("dd/MMM/yyyy").format(date);
                        } catch (Exception ex) {
                            Log.e("Converting Date: ", ex.getMessage(), ex);
                        }
                    }

                    txt_fieldtitle.setText(fieldName);
                    txt_fieldvalue.setText(dateString);
                    txt_fieldvalue.setOnClickListener(new DexonListeners.DateClickListener(this, fieldKey, date, txt_fieldvalue));
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
            rowView.setTag(fieldKey);
            this.lstvw_tree_detail.addView(rowView);
        }
    }

    private JsonObject getDataFromView() {
        JsonObject finalResult = this.jsonNodeData;
        int childcount = this.lstvw_tree_detail.getChildCount();
        for (int i = 0; i < childcount; i++) {
            View rowView = this.lstvw_tree_detail.getChildAt(i);
            String fieldKey = rowView.getTag().toString();

            if (finalResult.has("multipleValues") && !finalResult.get("multipleValues").isJsonNull()) {
                JsonArray fields = finalResult.get("multipleValues").getAsJsonArray();
                for (int index = 0; index < fields.size(); index++) {
                    JsonArray innerFields = finalResult.get("fields").getAsJsonArray();
                    for (int innerIndex = 0; index < innerFields.size(); index++) {

                    }
                }
            } else {
                JsonArray fields = finalResult.get("fields").getAsJsonArray();
                for (int index = 0; index < fields.size(); index++) {

                }
            }
        }
        return finalResult;
    }
}