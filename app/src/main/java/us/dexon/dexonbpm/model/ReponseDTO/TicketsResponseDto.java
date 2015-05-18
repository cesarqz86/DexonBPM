package us.dexon.dexonbpm.model.ReponseDTO;

import java.util.HashMap;

/**
 * Created by Cesar Quiroz on 5/9/15.
 */
public class TicketsResponseDto {

    //region Attributes
    private String ticketID;

    private HashMap<String, String> ticketDataList;
    //endregion

    //region Properties
    public String getTicketID() {
        return ticketID;
    }

    public void setTicketID(String ticketID) {
        this.ticketID = ticketID;
    }

    public HashMap<String, String> getTicketDataList() {
        return ticketDataList;
    }

    public void setTicketDataList(HashMap<String, String> ticketDataList) {
        this.ticketDataList = ticketDataList;
    }

    //endregion

    //region Constructor
    //endregion

    //region Public Methods
    //endregion

    //region Private Methods
    //endregion
}
