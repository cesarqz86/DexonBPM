package us.dexon.dexonbpm.model.RequestDTO;

import com.google.gson.JsonObject;
import com.google.gson.annotations.SerializedName;

import us.dexon.dexonbpm.model.ReponseDTO.LoginResponseDto;

/**
 * Created by Cesar Quiroz on 8/22/15.
 */
public class RecordHeaderResquestDto {

    //region Attributes
    @SerializedName("loggedUserModel")
    private LoginResponseDto loggedUser;

    @SerializedName("uniqueCodeIncidentModel")
    private String incidentCode;

    @SerializedName("fieldConsulted")
    private JsonObject fieldInformation;

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
