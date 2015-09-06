package us.dexon.dexonbpm.model.RequestDTO;

import com.google.gson.JsonObject;
import com.google.gson.annotations.SerializedName;

import us.dexon.dexonbpm.model.ReponseDTO.LoginResponseDto;

/**
 * Created by Cesar Quiroz on 9/6/15.
 */
public class DescendantRequestDto {

    //region Attributes
    @SerializedName("incidentModel")
    private JsonObject ticketInfo;

    @SerializedName("loggedUserModel")
    private LoginResponseDto loggedUser;

    @SerializedName("descendant")
    private boolean isTicketSon;
    //endregion

    //region Properties
    public JsonObject getTicketInfo() {
        return ticketInfo;
    }

    public void setTicketInfo(JsonObject ticketInfo) {
        this.ticketInfo = ticketInfo;
    }

    public LoginResponseDto getLoggedUser() {
        return loggedUser;
    }

    public void setLoggedUser(LoginResponseDto loggedUser) {
        this.loggedUser = loggedUser;
    }

    public boolean isTicketSon() {
        return isTicketSon;
    }

    public void setIsTicketSon(boolean isTicketSon) {
        this.isTicketSon = isTicketSon;
    }
    //endregion

    //region Constructor
    //endregion

    //region Public Methods
    //endregion

    //region Private Methods
    //endregion
}
