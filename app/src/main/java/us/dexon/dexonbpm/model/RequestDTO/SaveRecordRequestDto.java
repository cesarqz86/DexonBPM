package us.dexon.dexonbpm.model.RequestDTO;

import com.google.gson.JsonObject;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import us.dexon.dexonbpm.model.ReponseDTO.LoginResponseDto;

/**
 * Created by Cesar Quiroz on 9/27/15.
 */
public class SaveRecordRequestDto {

    //region Attributes
    @SerializedName("relatedDataModel")
    private JsonObject fieldInformation;

    @SerializedName("loggedUserModel")
    private LoginResponseDto loggedUser;

    @SerializedName("loadRelatedData")
    private boolean loadRelatedData;
    //endregion

    //region Properties
    public LoginResponseDto getLoggedUser() {
        return loggedUser;
    }

    public void setLoggedUser(LoginResponseDto loggedUser) {
        this.loggedUser = loggedUser;
    }

    public JsonObject getFieldInformation() {
        return fieldInformation;
    }

    public void setFieldInformation(JsonObject fieldInformation) {
        this.fieldInformation = fieldInformation;
    }

    public boolean isLoadRelatedData() {
        return loadRelatedData;
    }

    public void setLoadRelatedData(boolean loadRelatedData) {
        this.loadRelatedData = loadRelatedData;
    }
    //endregion

    //region Constructor
    //endregion

    //region Public Methods
    //endregion

    //region Private Methods
    //endregion
}
