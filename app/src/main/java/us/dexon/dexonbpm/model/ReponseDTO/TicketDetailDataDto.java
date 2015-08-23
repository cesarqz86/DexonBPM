package us.dexon.dexonbpm.model.ReponseDTO;

import com.google.gson.JsonObject;

import us.dexon.dexonbpm.infrastructure.enums.RenderControlType;

/**
 * Created by Cesar Quiroz on 8/17/15.
 */
public class TicketDetailDataDto {
    //region Attributes
    private String fieldName;
    private String fieldValue;
    private int Order;
    private RenderControlType fieldType;
    private String fieldKey;
    private String fieldSonData;
    //endregion

    //region Properties
    public String getFieldName() {
        return fieldName;
    }

    public void setFieldName(String fieldName) {
        this.fieldName = fieldName;
    }

    public String getFieldValue() {
        return fieldValue;
    }

    public void setFieldValue(String fieldValue) {
        this.fieldValue = fieldValue;
    }

    public int getOrder() {
        return Order;
    }

    public void setOrder(int order) {
        Order = order;
    }

    public RenderControlType getFieldType() {
        return fieldType;
    }

    public void setFieldType(RenderControlType fieldType) {
        this.fieldType = fieldType;
    }

    public String getFieldKey() {
        return fieldKey;
    }

    public void setFieldKey(String fieldKey) {
        this.fieldKey = fieldKey;
    }

    public String getFieldSonData() {
        return fieldSonData;
    }

    public void setFieldSonData(String fieldSonData) {
        this.fieldSonData = fieldSonData;
    }
    //endregion

    //region Constructor
    //endregion

    //region Public Methods
    //endregion

    //region Private Methods
    //endregion
}
