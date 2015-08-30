package us.dexon.dexonbpm.model.RequestDTO;

import com.google.gson.annotations.SerializedName;

import us.dexon.dexonbpm.model.ReponseDTO.LoginResponseDto;

/**
 * Created by Cesar Quiroz on 8/28/15.
 */
public class TicketByLayoutRequestDto {

    //region Attributes
    @SerializedName("loggedUserModel")
    private LoginResponseDto loggedUser;

    @SerializedName("layoutID")
    private String layoutID;

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

    public String getLayoutID() {
        return layoutID;
    }

    public void setLayoutID(String layoutID) {
        this.layoutID = layoutID;
    }

    public String getIncidentCode() {
        return incidentCode;
    }

    public void setIncidentCode(String incidentCode) {
        this.incidentCode = incidentCode;
    }
    //endregion

    //region Constructor
    //endregion

    //region Public Methods
    //endregion

    //region Private Methods
    //endregion
}
