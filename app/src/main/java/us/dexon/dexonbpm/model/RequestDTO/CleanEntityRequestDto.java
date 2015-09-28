package us.dexon.dexonbpm.model.RequestDTO;

import com.google.gson.JsonObject;
import com.google.gson.annotations.SerializedName;

import us.dexon.dexonbpm.model.ReponseDTO.LoginResponseDto;

/**
 * Created by Cesar Quiroz on 9/27/15.
 */
public class CleanEntityRequestDto {

    //region Attributes
    @SerializedName("entityModel")
    private JsonObject fieldInformation;

    //endregion

    //region Properties
    public JsonObject getFieldInformation() {
        return fieldInformation;
    }

    public void setFieldInformation(JsonObject fieldInformation) {
        this.fieldInformation = fieldInformation;
    }
    //endregion

    //region Constructor
    //endregion

    //region Public Methods
    //endregion

    //region Private Methods
    //endregion

}
