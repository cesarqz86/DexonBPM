package us.dexon.dexonbpm.infrastructure.interfaces;

import android.content.Context;

import java.util.ArrayList;

import us.dexon.dexonbpm.model.ReponseDTO.TicketWrapperResponseDto;
import us.dexon.dexonbpm.model.ReponseDTO.TicketsResponseDto;
import us.dexon.dexonbpm.model.RequestDTO.TicketDetailRequestDto;
import us.dexon.dexonbpm.model.RequestDTO.TicketsRequestDto;

/**
 * Created by Cesar Quiroz on 5/9/15.
 */
public interface ITicketService {

    TicketWrapperResponseDto getTicketData(Context context, TicketsRequestDto ticketFilter, int reloginCount);

    void getTicketDataDB(Context context, TicketsRequestDto ticketFilter);

    String[][] getEmptyData();

    void getTicketInfo (Context context, TicketDetailRequestDto ticketDetail, int reloginCount);

}
