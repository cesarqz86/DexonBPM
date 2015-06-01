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
    private String[][] ticketArrayData;
    //endregion

    //region Properties
    public String getErrorMessage() {
        return errorMessage;
    }

    public void setErrorMessage(String errorMessage) {
        this.errorMessage = errorMessage;
    }

    public String[][] getTicketArrayData() {
        return ticketArrayData;
    }

    public void setTicketArrayData(String[][] ticketArrayData) {
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
