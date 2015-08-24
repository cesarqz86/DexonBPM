package us.dexon.dexonbpm.infrastructure.interfaces;

import android.content.Context;

import com.google.gson.JsonObject;

import java.util.ArrayList;

import us.dexon.dexonbpm.model.ReponseDTO.RecordHeaderResponseDto;
import us.dexon.dexonbpm.model.ReponseDTO.ReopenResponseDto;
import us.dexon.dexonbpm.model.ReponseDTO.TechnicianResponseDto;
import us.dexon.dexonbpm.model.ReponseDTO.TicketResponseDto;
import us.dexon.dexonbpm.model.ReponseDTO.TicketWrapperResponseDto;
import us.dexon.dexonbpm.model.ReponseDTO.TicketsResponseDto;
import us.dexon.dexonbpm.model.RequestDTO.RecordHeaderResquestDto;
import us.dexon.dexonbpm.model.RequestDTO.ReloadRequestDto;
import us.dexon.dexonbpm.model.RequestDTO.ReopenRequestDto;
import us.dexon.dexonbpm.model.RequestDTO.SaveTicketRequestDto;
import us.dexon.dexonbpm.model.RequestDTO.TechnicianRequestDto;
import us.dexon.dexonbpm.model.RequestDTO.TicketDetailRequestDto;
import us.dexon.dexonbpm.model.RequestDTO.TicketsRequestDto;

/**
 * Created by Cesar Quiroz on 5/9/15.
 */
public interface ITicketService {

    TicketWrapperResponseDto getTicketData(Context context, TicketsRequestDto ticketFilter, int reloginCount);

    void getTicketDataDB(Context context, TicketsRequestDto ticketFilter);

    String[][] getEmptyData(String firstColumnTitle);

    TicketResponseDto getTicketInfo (Context context, TicketDetailRequestDto ticketDetail, int reloginCount);

    RecordHeaderResponseDto getAllRecordsHeaderTree (Context context, RecordHeaderResquestDto recordDetail, int reloginCount);

    RecordHeaderResponseDto getAllRecordsHeaderTable (Context context, RecordHeaderResquestDto recordDetail, int reloginCount);

    TechnicianResponseDto getTechnician(Context context, TechnicianRequestDto headerInfo);

    TicketResponseDto convertToTicketData(JsonObject ticketObject, int selectedTechnician, JsonObject currentTechnician);

    ReopenResponseDto reopenTicket(Context context, ReopenRequestDto reopenInfo, int reloginCount);

    TicketResponseDto reloadTicket(Context context, ReloadRequestDto reloadInfo, int reloginCount);

    TicketResponseDto saveTicket(Context context, SaveTicketRequestDto saveInfo, int reloginCount);
}
