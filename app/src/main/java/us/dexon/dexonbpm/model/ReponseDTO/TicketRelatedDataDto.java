package us.dexon.dexonbpm.model.ReponseDTO;

import com.google.gson.JsonObject;

import us.dexon.dexonbpm.infrastructure.enums.RenderControlType;

/**
 * Created by Cesar Quiroz on 9/13/15.
 */
public class TicketRelatedDataDto {

    //region Attributes
    private String fieldName;
    private String fieldKey;
    private JsonObject fieldSonData;
    private boolean IsEditable;
    //endregion

    //region Properties
    public String getFieldName() {
        return fieldName;
    }

    public void setFieldName(String fieldName) {
        this.fieldName = fieldName;
    }

    public String getFieldKey() {
        return fieldKey;
    }

    public void setFieldKey(String fieldKey) {
        this.fieldKey = fieldKey;
    }

    public JsonObject getFieldSonData() {
        return fieldSonData;
    }

    public void setFieldSonData(JsonObject fieldSonData) {
        this.fieldSonData = fieldSonData;
    }

    public boolean isEditable() {
        return IsEditable;
    }

    public void setIsEditable(boolean isEditable) {
        IsEditable = isEditable;
    }
    //endregion

    //region Constructor
    //endregion

    //region Public Methods
    //endregion

    //region Private Methods
    //endregion
}
