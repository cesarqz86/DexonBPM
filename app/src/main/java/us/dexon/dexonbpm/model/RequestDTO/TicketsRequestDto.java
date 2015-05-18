package us.dexon.dexonbpm.model.RequestDTO;

import com.google.gson.annotations.SerializedName;

import us.dexon.dexonbpm.model.ReponseDTO.LoginResponseDto;

/**
 * Created by Cesar Quiroz on 5/9/15.
 */
public class TicketsRequestDto {

    //region Attributes
    @SerializedName("loggedUserModel")
    private LoginResponseDto loggedUser;

    @SerializedName("getClosedTickets")
    private boolean includeClosedTickets;

    // Check enum TicketFilterType
    @SerializedName("currentFilter")
    private int ticketFilterType;

    @SerializedName("ticketsPerPage")
    private int ticketsPerPage;

    //endregion

    //region Properties
    public LoginResponseDto getLoggedUser() {
        return loggedUser;
    }

    public void setLoggedUser(LoginResponseDto loggedUser) {
        this.loggedUser = loggedUser;
    }

    public boolean isIncludeClosedTickets() {
        return includeClosedTickets;
    }

    public void setIncludeClosedTickets(boolean includeClosedTickets) {
        this.includeClosedTickets = includeClosedTickets;
    }

    public int getTicketFilterType() {
        return ticketFilterType;
    }

    public void setTicketFilterType(int ticketFilterType) {
        this.ticketFilterType = ticketFilterType;
    }

    public int getTicketsPerPage() {
        return ticketsPerPage;
    }

    public void setTicketsPerPage(int ticketsPerPage) {
        this.ticketsPerPage = ticketsPerPage;
    }
    //endregion

    //region Constructor
    //endregion

    //region Public Methods
    //endregion

    //region Private Methods
    //endregion
}
