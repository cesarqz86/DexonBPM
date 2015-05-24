package us.dexon.dexonbpm.model.ReponseDTO;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;

/**
 * Created by Cesar Quiroz on 5/24/15.
 */
public class TicketWrapperResponseDto {

    //region Attributes
    @SerializedName("Message")
    private String errorMessage;

    @Expose(serialize = false, deserialize = false)
    private ArrayList<TicketsResponseDto> ticketArrayData;
    //endregion

    //region Properties
    public String getErrorMessage() {
        return errorMessage;
    }

    public void setErrorMessage(String errorMessage) {
        this.errorMessage = errorMessage;
    }

    public ArrayList<TicketsResponseDto> getTicketArrayData() {
        return ticketArrayData;
    }

    public void setTicketArrayData(ArrayList<TicketsResponseDto> ticketArrayData) {
        this.ticketArrayData = ticketArrayData;
    }
    //endregion

    //region Constructor
    //endregion

    //region Public Methods
    //endregion

    //region Private Methods
    //endregion
}
