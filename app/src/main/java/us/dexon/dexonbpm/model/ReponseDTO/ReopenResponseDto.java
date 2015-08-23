package us.dexon.dexonbpm.model.ReponseDTO;

import com.google.gson.annotations.SerializedName;

/**
 * Created by Cesar Quiroz on 8/23/15.
 */
public class ReopenResponseDto {

    //region Attributes
    @SerializedName("result")
    private String resultMessage;
    @SerializedName("error")
    private String errorMessage;

    private TicketResponseDto ticketData;
    //endregion

    //region Properties
    public String getResultMessage() {
        return resultMessage;
    }

    public void setResultMessage(String resultMessage) {
        this.resultMessage = resultMessage;
    }

    public String getErrorMessage() {
        return errorMessage;
    }

    public void setErrorMessage(String errorMessage) {
        this.errorMessage = errorMessage;
    }

    public TicketResponseDto getTicketData() {
        return ticketData;
    }

    public void setTicketData(TicketResponseDto ticketData) {
        this.ticketData = ticketData;
    }
    //endregion

    //region Constructor
    //endregion

    //region Public Methods
    //endregion

    //region Private Methods
    //endregion

}
