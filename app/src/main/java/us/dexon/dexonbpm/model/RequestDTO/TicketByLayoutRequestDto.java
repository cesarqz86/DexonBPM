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
    private int layoutID;

    @SerializedName("layoutID")
    private String ticketID;
    //endregion

    //region Properties
    public LoginResponseDto getLoggedUser() {
        return loggedUser;
    }

    public void setLoggedUser(LoginResponseDto loggedUser) {
        this.loggedUser = loggedUser;
    }

    public int getLayoutID() {
        return layoutID;
    }

    public void setLayoutID(int layoutID) {
        this.layoutID = layoutID;
    }
    //endregion

    //region Constructor
    //endregion

    //region Public Methods
    //endregion

    //region Private Methods
    //endregion
}
