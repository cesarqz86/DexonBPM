package us.dexon.dexonbpm.infrastructure.interfaces;

import android.content.Context;

import java.util.ArrayList;

import us.dexon.dexonbpm.model.ReponseDTO.TicketsResponseDto;
import us.dexon.dexonbpm.model.RequestDTO.TicketsRequestDto;

/**
 * Created by Cesar Quiroz on 5/9/15.
 */
public interface ITicketService {

    ArrayList<TicketsResponseDto> getTicketData(Context context, TicketsRequestDto ticketFilter);

}
