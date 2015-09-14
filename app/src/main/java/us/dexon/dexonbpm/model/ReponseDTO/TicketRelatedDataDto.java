package us.dexon.dexonbpm.model.ReponseDTO;

import us.dexon.dexonbpm.infrastructure.enums.RenderControlType;

/**
 * Created by Cesar Quiroz on 9/13/15.
 */
public class TicketRelatedDataDto {

    //region Attributes
    private String fieldName;
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
