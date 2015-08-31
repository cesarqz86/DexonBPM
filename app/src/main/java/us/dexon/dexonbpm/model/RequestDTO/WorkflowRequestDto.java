package us.dexon.dexonbpm.model.RequestDTO;

import com.google.gson.JsonObject;
import com.google.gson.annotations.SerializedName;

import us.dexon.dexonbpm.model.ReponseDTO.LoginResponseDto;

/**
 * Created by Cesar Quiroz on 8/30/15.
 */
public class WorkflowRequestDto {

    //region Attributes
    @SerializedName("incidentModel")
    private JsonObject ticketInfo;
    //endregion

    //region Properties
    public JsonObject getTicketInfo() {
        return ticketInfo;
    }

    public void setTicketInfo(JsonObject ticketInfo) {
        this.ticketInfo = ticketInfo;
    }
    //endregion

    //region Constructor
    //endregion

    //region Public Methods
    //endregion

    //region Private Methods
    //endregion
}
