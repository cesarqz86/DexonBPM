package us.dexon.dexonbpm.model.ReponseDTO;

import com.google.gson.annotations.SerializedName;

/**
 * Created by Cesar Quiroz on 9/6/15.
 */
public class DescendantResponseDto {

    //region Attributes
    @SerializedName("error")
    private String errorMessage;

    private String ticketID;

    private String ticketName;
    //endregion

    //region Properties
    public String getErrorMessage() {
        return errorMessage;
    }

    public void setErrorMessage(String errorMessage) {
        this.errorMessage = errorMessage;
    }

    public String getTicketID() {
        return ticketID;
    }

    public void setTicketID(String ticketID) {
        this.ticketID = ticketID;
    }

    public String getTicketName() {
        return ticketName;
    }

    public void setTicketName(String ticketName) {
        this.ticketName = ticketName;
    }
    //endregion

    //region Constructor
    //endregion

    //region Public Methods
    //endregion

    //region Private Methods
    //endregion
}
