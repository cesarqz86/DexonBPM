package us.dexon.dexonbpm.activity;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;
import android.util.Log;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
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
import us.dexon.dexonbpm.infrastructure.implementations.ConfigurationService;
import us.dexon.dexonbpm.infrastructure.implementations.DexonDatabaseWrapper;
import us.dexon.dexonbpm.infrastructure.implementations.DexonListeners;
import us.dexon.dexonbpm.infrastructure.implementations.ServiceExecuter;
import us.dexon.dexonbpm.infrastructure.implementations.TicketService;
import us.dexon.dexonbpm.infrastructure.interfaces.IDexonDatabaseWrapper;
import us.dexon.dexonbpm.infrastructure.interfaces.ITicketService;
import us.dexon.dexonbpm.model.ReponseDTO.CleanEntityResponseDto;
import us.dexon.dexonbpm.model.ReponseDTO.LoginResponseDto;
import us.dexon.dexonbpm.model.ReponseDTO.RecordHeaderResponseDto;
import us.dexon.dexonbpm.model.ReponseDTO.TicketRelatedDataDto;
import us.dexon.dexonbpm.model.RequestDTO.CleanEntityRequestDto;
import us.dexon.dexonbpm.model.RequestDTO.RecordHeaderResquestDto;

public class RelatedDataActivity extends FragmentActivity {

    private JsonObject nodeData;
    private JsonObject jsonNodeData;
    private Menu menu;
    private LinearLayout lstvw_tree_detail;
    private RelativeLayout footer_container;
    private JsonArray multipleValuesArray;
    private LinearLayout linear_plantilla;
    private boolean isRelatedDataEditable;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_related_data);

        this.lstvw_tree_detail = (LinearLayout) this.findViewById(R.id.lstvw_tree_detail);
        this.footer_container = (RelativeLayout) this.findViewById(R.id.footer_container);
        this.linear_plantilla = (LinearLayout) this.findViewById(R.id.linear_plantilla);

        //JsonParser gsonSerializer = new JsonParser();

        Intent currentIntent = this.getIntent();
        String activityTitle = currentIntent.getStringExtra("activityTitle");
        this.setTitle(activityTitle);

        if (CommonSharedData.SelectedRelatedData != null) {
            this.nodeData = CommonSharedData.SelectedRelatedData.getFieldSonData();
            this.jsonNodeData = this.nodeData;
            CommonSharedData.RelatedData = jsonNodeData;

            boolean isDisable = false;
            boolean isEditInPlace = true;
            if (this.jsonNodeData.has("isDisable")) {
                isDisable = this.jsonNodeData.get("isDisable").getAsBoolean();
            }
            if (this.jsonNodeData.has("edit_in_place")) {
                isEditInPlace = this.jsonNodeData.get("edit_in_place").getAsBoolean();
            }
            this.isRelatedDataEditable = !isDisable && isEditInPlace;

            if (isDisable) {
                // Menu options
                MenuItem action_save = this.menu.findItem(R.id.action_save);
                action_save.setVisible(false);
                this.linear_plantilla.setVisibility(View.GONE);
            } else {
                this.linear_plantilla.setVisibility(View.VISIBLE);
            }

            this.drawRelatedData(jsonNodeData);
        }

        if (CommonSharedData.IsReloadRelatedData != null && CommonSharedData.IsReloadRelatedData) {
            CommonSharedData.IsReloadRelatedData = null;
            this.reloadRelatedData(CommonSharedData.RelatedData);
        }

        CommonSharedData.RelatedDataActivity = this;

        this.SetConfiguredColors();
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

        if (CommonSharedData.IsOnClick == null || (CommonSharedData.IsOnClick != null && !CommonSharedData.IsOnClick)) {
            this.updateTicketInfo();

            TicketDetail ticketDetail = null;
            NewTicketActivity newTicket = null;

            if (CommonSharedData.TicketActivity instanceof TicketDetail) {
                ticketDetail = (TicketDetail) CommonSharedData.TicketActivity;
            } else if (CommonSharedData.TicketActivity instanceof NewTicketActivity) {
                newTicket = (NewTicketActivity) CommonSharedData.TicketActivity;
            }

            if (ticketDetail != null)
                ticketDetail.inidentsCallBack(CommonSharedData.TicketInfoUpdated);

            if (newTicket != null)
                newTicket.inidentsCallBack(CommonSharedData.TicketInfoUpdated);

            CommonSharedData.SelectedRelatedData = null;
            CommonSharedData.RelatedDataActivity = null;
        }

        CommonSharedData.IsOnClick = false;
    }

    public void logoClick(View view) {
        Intent webIntent = new Intent();
        webIntent.setAction(Intent.ACTION_VIEW);
        webIntent.addCategory(Intent.CATEGORY_BROWSABLE);
        webIntent.setData(Uri.parse(this.getString(R.string.dexon_website)));
        this.startActivity(webIntent);
    }

    public void selectPlantilla_Click(View view) {

        IDexonDatabaseWrapper dexonDatabase = DexonDatabaseWrapper.getInstance();
        dexonDatabase.setContext(this);

        LoginResponseDto loggedUser = dexonDatabase.getLoggedUser();

        RecordHeaderResquestDto recordHeader = new RecordHeaderResquestDto();
        recordHeader.setLoggedUser(loggedUser);
        recordHeader.setFieldInformation(this.nodeData);
        recordHeader.setIncidentCode(CommonSharedData.TicketInfo.getTicketCode());

        ServiceExecuter serviceExecuter = new ServiceExecuter();
        ServiceExecuter.ExecuteAllRecordHeaderTree getAllRecords = serviceExecuter.new ExecuteAllRecordHeaderTree(this);
        getAllRecords.execute(recordHeader);


        /*Intent plantillaTreeView = new Intent(this, PlantillaListViewActivity.class);
        plantillaTreeView.putExtra("fieldKey", "main_field");
        this.startActivityForResult(plantillaTreeView, 0);
        this.overridePendingTransition(R.anim.right_slide_in,
                R.anim.right_slide_out);*/
    }

    public void newDetailData(View view) {

        if (this.multipleValuesArray != null && this.multipleValuesArray.size() > 0) {
            CleanEntityRequestDto ticketData = new CleanEntityRequestDto();
            ticketData.setFieldInformation(this.multipleValuesArray.get(0).getAsJsonObject());

            ServiceExecuter serviceExecuter = new ServiceExecuter();
            ServiceExecuter.ExecuteCleanEntityService saveTicketService = serviceExecuter.new ExecuteCleanEntityService(this);
            saveTicketService.execute(ticketData);
        }
    }

    public void drawRelatedData(JsonObject jsonData) {
        this.lstvw_tree_detail.removeAllViews();
        LayoutInflater inflater = this.getLayoutInflater();

        if (jsonData.has("multipleValues") && !jsonData.get("multipleValues").isJsonNull() ) {
            this.drawMultipleRelatedData(jsonData, inflater);
        } else {
            this.footer_container.setVisibility(View.INVISIBLE);
            this.drawSingleRelatedData(jsonData, inflater);
        }

        if (jsonData.has("details") && !jsonData.get("details").isJsonNull()) {
            View rowView = inflater.inflate(R.layout.item_disclosure, null);
            TextView txt_fieldtitle = (TextView) rowView.findViewById(R.id.txt_fieldtitle);
            txt_fieldtitle.setText(this.getResources().getString(R.string.txt_relateddata_detail_title));
            txt_fieldtitle.setTextSize(TypedValue.COMPLEX_UNIT_SP, 15f);
            rowView.setOnClickListener(new DexonListeners.DetailRelatedDataClickListener(this, this.nodeData));
            this.lstvw_tree_detail.addView(rowView);
        }
    }

    public void plantillaCallBack(RecordHeaderResponseDto responseDto) {

        if (responseDto != null) {
            if (responseDto.getDataList() != null) {
                CommonSharedData.TreeData = responseDto;
                Intent plantillaTreeView = new Intent(this, PlantillaListViewActivity.class);
                plantillaTreeView.putExtra("fieldKey", responseDto.getFieldKeyId());
                this.startActivityForResult(plantillaTreeView, 0);
                this.overridePendingTransition(R.anim.right_slide_in,
                        R.anim.right_slide_out);

            } else if (responseDto.getTableDataList() != null) {
                CommonSharedData.TreeData = responseDto;
                Intent plantillaTableView = new Intent(this, PlantillaTableActivity.class);
                plantillaTableView.putExtra("fieldKey", responseDto.getFieldKeyId());
                this.startActivityForResult(plantillaTableView, 0);
                this.overridePendingTransition(R.anim.right_slide_in,
                        R.anim.right_slide_out);
            }
        }
    }

    public void reloadRelatedData(JsonObject jsonData) {

        /*if (jsonData != null && jsonData.has("multipleValues") && jsonData.get("multipleValues").isJsonNull()) {
            jsonData.addProperty("multipleValues", "");
        }*/

        CommonService.saveRelatedDataBackground(this, jsonData);
    }

    private void drawMultipleRelatedData(JsonObject jsonData, LayoutInflater inflater) {
        JsonArray fields = jsonData.get("multipleValues").getAsJsonArray();
        this.multipleValuesArray = fields;
        for (int index = 0; index < fields.size(); index++) {
            JsonObject tempObject = fields.get(index).getAsJsonObject();
            View rowView = inflater.inflate(R.layout.item_title_delete, null);
            TextView txt_fieldtitle = (TextView) rowView.findViewById(R.id.txt_fieldtitle);
            txt_fieldtitle.setText(tempObject.get("tb_name").getAsString());
            this.lstvw_tree_detail.addView(rowView);

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

        String secondaryColorString = ConfigurationService.getConfigurationValue(this, "ColorSecundario");
        int secondaryColor = Color.parseColor(secondaryColorString);

        if (jsonField.has("is_hidden") && !jsonField.get("is_hidden").getAsBoolean()) {
            View rowView = inflater.inflate(R.layout.item_detailticket_int, null);
            RenderControlType controlType = RenderControlType.values()[jsonField.get("render_ctl").getAsInt()];
            String fieldKey = jsonField.get("field_name").getAsString();
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
                    rowView = inflater.inflate(R.layout.item_detailticket_tree, null);
                    TextView txt_fieldtitle = (TextView) rowView.findViewById(R.id.txt_fieldtitle);
                    TextView txt_fieldvalue = (TextView) rowView.findViewById(R.id.txt_fieldvalue);

                    txt_fieldtitle.setText(fieldName);
                    txt_fieldvalue.setText(fieldValue);
                    rowView.setOnClickListener(new DexonListeners.MultilineClickListener(
                            this,
                            jsonField,
                            null,
                            fieldKey,
                            true));
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
            this.setEnableView(rowView, this.isRelatedDataEditable);
            View separator = rowView.findViewById(R.id.blue_separator);
            if(separator != null){
                separator.setBackgroundColor(secondaryColor);
            }
            rowView.setTag(fieldKey);
            this.lstvw_tree_detail.addView(rowView);
        }
    }

    private void setEnableView(View rowView, boolean isEnable) {
        rowView.setEnabled(isEnable);
        if (rowView instanceof ViewGroup) {
            for (int i = 0; i < ((ViewGroup) rowView).getChildCount(); i++) {
                View child = ((ViewGroup) rowView).getChildAt(i);
                child.setEnabled(isEnable);
                if (child instanceof ViewGroup && ((ViewGroup) child).getChildCount() > 0) {
                    this.setEnableView(child, isEnable);
                }
            }
        }
    }

    private JsonObject getDataFromView() {
        JsonObject finalResult = this.jsonNodeData;
        int childcount = this.lstvw_tree_detail.getChildCount();
        for (int i = 0; i < childcount; i++) {
            View rowView = this.lstvw_tree_detail.getChildAt(i);

            if (rowView.getTag() != null) {
                String fieldKey = rowView.getTag().toString();

                if (CommonValidations.validateEmpty(fieldKey)) {
                    if (this.jsonNodeData.has("multipleValues") && !this.jsonNodeData.get("multipleValues").isJsonNull()) {
                        JsonArray multipleValues = this.jsonNodeData.get("multipleValues").getAsJsonArray();

                        for (int index = 0; index < multipleValues.size(); index++) {

                            JsonObject tempMultipleValues = multipleValues.get(index).getAsJsonObject();
                            JsonArray fields = tempMultipleValues.get("fields").getAsJsonArray();
                            for (int indexFields = 0; indexFields < fields.size(); indexFields++) {
                                JsonObject tempField = fields.get(indexFields).getAsJsonObject();
                                String tempFieldKey = tempField.get("field_name").getAsString();
                                if (CommonValidations.validateEqualsIgnoreCase(fieldKey, tempFieldKey)) {
                                    RenderControlType controlType = RenderControlType.values()[tempField.get("render_ctl").getAsInt()];
                                    String value = this.getValueByType(rowView, controlType);
                                    if (CommonValidations.validateEmpty(value)) {
                                        tempField.addProperty("Value", value);
                                        fields.set(indexFields, tempField);
                                        break;
                                    }
                                }
                            }
                            tempMultipleValues.add("fields", fields);
                            multipleValues.set(index, tempMultipleValues);
                        }
                        this.jsonNodeData.add("multipleValues", multipleValues);
                    } else {
                        JsonArray fields = this.jsonNodeData.get("fields").getAsJsonArray();
                        for (int indexFields = 0; indexFields < fields.size(); indexFields++) {
                            JsonObject tempField = fields.get(indexFields).getAsJsonObject();
                            String tempFieldKey = tempField.get("field_name").getAsString();
                            if (CommonValidations.validateEqualsIgnoreCase(fieldKey, tempFieldKey)) {
                                RenderControlType controlType = RenderControlType.values()[tempField.get("render_ctl").getAsInt()];
                                String value = this.getValueByType(rowView, controlType);
                                if (CommonValidations.validateEmpty(value)) {
                                    tempField.addProperty("Value", value);
                                    fields.set(indexFields, tempField);
                                    break;
                                }
                            }
                        }
                        this.jsonNodeData.add("fields", fields);
                    }
                }
            }
        }
        return finalResult;
    }

    private String getValueByType(View rowView, RenderControlType controlType) {
        String value = "";
        switch (controlType) {
            case DXControlsLabel: {
                TextView txt_fieldvalue = (TextView) rowView.findViewById(R.id.txt_fieldvalue);
                value = txt_fieldvalue.getText().toString();
                break;
            }
            case DXControlsFloat:
            case DXControlsCurrency: {
                TextView txt_fieldvalue = (TextView) rowView.findViewById(R.id.txt_fieldvalue);
                value = txt_fieldvalue.getText().toString();
                break;
            }
            case DXControlsCharacter:
            case DXControlsLine: {
                TextView txt_fieldvalue = (TextView) rowView.findViewById(R.id.txt_fieldvalue);
                value = txt_fieldvalue.getText().toString();
                break;
            }
            case DXControlsPassword: {
                TextView txt_fieldvalue = (TextView) rowView.findViewById(R.id.txt_fieldvalue);
                value = txt_fieldvalue.getText().toString();
                break;
            }
            case DXControlsPhone: {
                TextView txt_fieldvalue = (TextView) rowView.findViewById(R.id.txt_fieldvalue);
                value = txt_fieldvalue.getText().toString();
                break;
            }
            case DXControlsCheckbox: {
                ToggleButton txt_fieldvalue = (ToggleButton) rowView.findViewById(R.id.txt_fieldvalue);
                value = String.valueOf(txt_fieldvalue.isChecked());
                break;
            }
            case DXControlsDate: {
                TextView txt_fieldvalue = (TextView) rowView.findViewById(R.id.txt_fieldvalue);

                /*String dateString = fieldValue;
                Date date = new Date();
                if (CommonValidations.validateEmpty(dateString)) {
                    try {
                        date = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss").parse(dateString);
                        dateString = new SimpleDateFormat("dd/MMM/yyyy").format(date);
                    } catch (Exception ex) {
                        Log.e("Converting Date: ", ex.getMessage(), ex);
                    }
                }*/
                break;
            }
            case DXControlsMultiline: {
                TextView txt_fieldvalue = (TextView) rowView.findViewById(R.id.txt_fieldvalue);
                value = txt_fieldvalue.getText().toString();
                break;
            }
            default: {
                TextView txt_fieldvalue = (TextView) rowView.findViewById(R.id.txt_fieldvalue);
                value = txt_fieldvalue.getText().toString();
                break;
            }
        }
        return value;
    }

    public void updateTicketInfo() {

        this.jsonNodeData = this.getDataFromView();

        if (CommonSharedData.TicketInfoUpdated == null) {
            CommonSharedData.TicketInfoUpdated = CommonSharedData.TicketInfo;
        }
        String relatedDataKey = this.jsonNodeData.get("tb_name").getAsString();
        JsonObject ticketJson = CommonSharedData.TicketInfoUpdated.getTicketInfo();
        JsonArray relatedData = ticketJson.getAsJsonArray("relatedData");
        for (int index = 0; index < relatedData.size(); index++) {
            JsonObject tempRelatedData = relatedData.get(index).getAsJsonObject();
            String tempKey = tempRelatedData.get("tb_name").getAsString();
            if (CommonValidations.validateEqualsIgnoreCase(relatedDataKey, tempKey)) {
                relatedData.set(index, this.jsonNodeData);
                break;
            }
        }
        ticketJson.add("relatedData", relatedData);
        ITicketService ticketService = TicketService.getInstance();
        CommonSharedData.TicketInfoUpdated = ticketService.convertToTicketData(ticketJson, R.id.btn_setmanual_technician, null);
    }

    public void callBackAddDetail(CleanEntityResponseDto responseDto) {
        if (this.multipleValuesArray != null && responseDto != null && responseDto.getRecordObject() != null) {
            this.multipleValuesArray.add(responseDto.getRecordObject());
            this.jsonNodeData.add("multipleValues", this.multipleValuesArray);
            this.drawRelatedData(this.jsonNodeData);
        }
    }

    private void SetConfiguredColors() {

        String primaryColorString = ConfigurationService.getConfigurationValue(this, "ColorPrimario");
        int primaryColor = Color.parseColor(primaryColorString);
        String secondaryColorString = ConfigurationService.getConfigurationValue(this, "ColorSecundario");
        int secondaryColor = Color.parseColor(secondaryColorString);

        Drawable ic_action_newticket_layoutimage3x = this.getResources().getDrawable(R.drawable.ic_action_newticket_layoutimage3x);

        ic_action_newticket_layoutimage3x.setColorFilter(primaryColor, PorterDuff.Mode.SRC_ATOP);
    }
}
