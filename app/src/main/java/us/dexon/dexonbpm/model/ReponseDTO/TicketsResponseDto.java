package us.dexon.dexonbpm.model.ReponseDTO;

import java.util.HashMap;

/**
 * Created by Cesar Quiroz on 5/9/15.
 */
public class TicketsResponseDto {

    //region Attributes
    private String ticketID;

    private String incidentID;

    private HashMap<String, Object> ticketDataList;
    //endregion

    //region Properties
    public String getTicketID() {
        return ticketID;
    }

    public void setTicketID(String ticketID) {
        this.ticketID = ticketID;
    }

    public HashMap<String, Object> getTicketDataList() {
        return ticketDataList;
    }

    public void setTicketDataList(HashMap<String, Object> ticketDataList) {
        this.ticketDataList = ticketDataList;
    }

    public String getIncidentID() {
        return incidentID;
    }

    public void setIncidentID(String incidentID) {
        this.incidentID = incidentID;
    }

    //endregion

    //region Constructor
    //endregion

    //region Public Methods
    //endregion

    //region Private Methods
    //endregion
}
