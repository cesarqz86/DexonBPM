package us.dexon.dexonbpm.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.widget.EditText;

import com.google.gson.JsonObject;

import us.dexon.dexonbpm.R;
import us.dexon.dexonbpm.infrastructure.implementations.CommonSharedData;

public class MultiLineActivity extends FragmentActivity {

    private EditText txt_fieldvalue;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_multi_line);

        this.txt_fieldvalue = (EditText) this.findViewById(R.id.txt_fieldvalue);

        Intent currentIntent = this.getIntent();
        boolean isControlEditable = currentIntent.getBooleanExtra("IsEditable", true);

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

        if(CommonSharedData.MultilineDataValue != null){
            this.txt_fieldvalue.setText(CommonSharedData.MultilineDataValue);
        }

        this.txt_fieldvalue.setEnabled(isControlEditable);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        CommonSharedData.MultilineData = null;
    }

}
