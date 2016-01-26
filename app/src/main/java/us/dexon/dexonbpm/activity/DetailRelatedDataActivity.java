package us.dexon.dexonbpm.activity;

import android.content.Intent;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.support.v4.app.FragmentActivity;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
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
import us.dexon.dexonbpm.infrastructure.implementations.CommonSharedData;
import us.dexon.dexonbpm.infrastructure.implementations.CommonValidations;
import us.dexon.dexonbpm.infrastructure.implementations.ConfigurationService;
import us.dexon.dexonbpm.infrastructure.implementations.DexonDatabaseWrapper;
import us.dexon.dexonbpm.infrastructure.implementations.DexonListeners;
import us.dexon.dexonbpm.infrastructure.implementations.ServiceExecuter;
import us.dexon.dexonbpm.infrastructure.interfaces.IDexonDatabaseWrapper;
import us.dexon.dexonbpm.model.ReponseDTO.CleanEntityResponseDto;
import us.dexon.dexonbpm.model.ReponseDTO.LoginResponseDto;
import us.dexon.dexonbpm.model.RequestDTO.CleanEntityRequestDto;
import us.dexon.dexonbpm.model.RequestDTO.SaveRecordRequestDto;

public class DetailRelatedDataActivity extends FragmentActivity {

    private JsonObject jsonNodeData;
    private LinearLayout lstvw_ticketdetail;
    private JsonArray detailArray;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail_related_data);
        this.lstvw_ticketdetail = (LinearLayout) this.findViewById(R.id.lstvw_ticketdetail);

        if (CommonSharedData.RelatedDataDetail != null) {
            this.jsonNodeData = CommonSharedData.RelatedDataDetail;
            this.drawDetailRelatedData(this.jsonNodeData);
        }

        this.SetConfiguredColors();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        CommonSharedData.RelatedDataDetail = null;
    }

    public void logoClick(View view) {
        Intent webIntent = new Intent();
        webIntent.setAction(Intent.ACTION_VIEW);
        webIntent.addCategory(Intent.CATEGORY_BROWSABLE);
        webIntent.setData(Uri.parse(this.getString(R.string.dexon_website)));
        this.startActivity(webIntent);
    }

    public void newDetailData(View view) {

        if (this.detailArray != null && this.detailArray.size() > 0) {
            CleanEntityRequestDto ticketData = new CleanEntityRequestDto();
            ticketData.setFieldInformation(this.detailArray.get(0).getAsJsonObject());

            ServiceExecuter serviceExecuter = new ServiceExecuter();
            ServiceExecuter.ExecuteCleanEntityService saveTicketService = serviceExecuter.new ExecuteCleanEntityService(this);
            saveTicketService.execute(ticketData);
        }

    }

    public void drawDetailRelatedData(JsonObject jsonData) {
        this.lstvw_ticketdetail.removeAllViews();
        LayoutInflater inflater = this.getLayoutInflater();

        this.detailArray = jsonData.get("details").getAsJsonArray();
        for (int index = 0; index < this.detailArray.size(); index++) {
            JsonObject tempObject = this.detailArray.get(index).getAsJsonObject();
            View rowView = inflater.inflate(R.layout.item_title_delete, null);
            TextView txt_fieldtitle = (TextView) rowView.findViewById(R.id.txt_fieldtitle);
            txt_fieldtitle.setText(tempObject.get("tb_name").getAsString());
            this.lstvw_ticketdetail.addView(rowView);

            this.drawSingleRelatedData(tempObject, inflater);

            if ((index + 1) < this.detailArray.size()) {
                rowView = inflater.inflate(R.layout.item_empty_row, null);
                this.lstvw_ticketdetail.addView(rowView);
            }
        }
    }

    public void callBackAddDetail(CleanEntityResponseDto responseDto) {
        if (this.detailArray != null && this.detailArray.size() > 0
                && responseDto != null && responseDto.getRecordObject() != null) {
            this.detailArray.add(responseDto.getRecordObject());
            this.jsonNodeData.add("details", this.detailArray);
            this.drawDetailRelatedData(this.jsonNodeData);
        }
    }

    private void drawSingleRelatedData(JsonObject jsonData, LayoutInflater inflater) {
        JsonArray fields = jsonData.get("fields").getAsJsonArray();
        for (int index = 0; index < fields.size(); index++) {
            if (!fields.get(index).isJsonNull()) {
                this.drawRelatedDataControls(fields.get(index).getAsJsonObject(), inflater);
            }
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
                fieldValue = (jsonField.get("Value").isJsonNull()) ? "" : jsonField.get("Value").getAsString();
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
            this.lstvw_ticketdetail.addView(rowView);
        }
    }

    private void SetConfiguredColors() {

        String primaryColorString = ConfigurationService.getConfigurationValue(this, "ColorPrimario");
        int primaryColor = Color.parseColor(primaryColorString);
        String secondaryColorString = ConfigurationService.getConfigurationValue(this, "ColorSecundario");
        int secondaryColor = Color.parseColor(secondaryColorString);

        Drawable logo_mini = this.getResources().getDrawable(R.drawable.logo_mini);
        Drawable ic_plus = this.getResources().getDrawable(R.drawable.ic_plus);

        ImageButton plus_button = (ImageButton) this.findViewById(R.id.plus_button);
        ImageButton dexon_logo = (ImageButton) this.findViewById(R.id.dexon_logo);

        logo_mini.setColorFilter(primaryColor, PorterDuff.Mode.SRC_ATOP);
        ic_plus.setColorFilter(primaryColor, PorterDuff.Mode.SRC_ATOP);

        dexon_logo.setBackground(logo_mini);
        plus_button.setBackground(ic_plus);
    }
}
