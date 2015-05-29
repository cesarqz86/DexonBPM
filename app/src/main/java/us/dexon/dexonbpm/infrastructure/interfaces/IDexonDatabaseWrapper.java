package us.dexon.dexonbpm.infrastructure.interfaces;

import android.content.Context;

import java.util.ArrayList;
import java.util.HashMap;

import us.dexon.dexonbpm.infrastructure.enums.TicketFilter;
import us.dexon.dexonbpm.model.ReponseDTO.LoginResponseDto;
import us.dexon.dexonbpm.model.ReponseDTO.TicketsResponseDto;

/**
 * Created by Cesar Quiroz on 5/16/15.
 */
public interface IDexonDatabaseWrapper {

    void setContext(Context contextData);

    void saveLoggedUser(LoginResponseDto loggedUser);

    LoginResponseDto getLoggedUser();

    void deleteUserTable();

    void saveTicketData(ArrayList<TicketsResponseDto> ticketDataList);

    void saveTicketData(String ticketID, String incidentID, HashMap<String, Object> dataToSave);

    TicketsResponseDto getTicketData(String ticketID);

    ArrayList<TicketsResponseDto> getTicketData (String filterWord, TicketFilter ticketFilter);

    void deleteTicketTable();
}
