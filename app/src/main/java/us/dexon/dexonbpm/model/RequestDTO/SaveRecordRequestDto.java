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

    @SerializedName("uniqueCodeIncident")
    private String incidentCode;

    //endregion

    //region Properties
    public LoginResponseDto getLoggedUser() {
        return loggedUser;
    }

    public void setLoggedUser(LoginResponseDto loggedUser) {
        this.loggedUser = loggedUser;
    }

    public String getIncidentCode() {
        return incidentCode;
    }

    public void setIncidentCode(String incidentCode) {
        this.incidentCode = incidentCode;
    }

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
