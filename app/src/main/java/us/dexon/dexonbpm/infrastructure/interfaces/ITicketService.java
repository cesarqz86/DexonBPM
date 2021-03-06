package us.dexon.dexonbpm.infrastructure.interfaces;

import android.content.Context;

import com.google.gson.JsonObject;

import java.util.ArrayList;

import us.dexon.dexonbpm.model.ReponseDTO.CleanEntityResponseDto;
import us.dexon.dexonbpm.model.ReponseDTO.DescendantResponseDto;
import us.dexon.dexonbpm.model.ReponseDTO.DocumentInfoResponseDto;
import us.dexon.dexonbpm.model.ReponseDTO.PrintTicketResponseDto;
import us.dexon.dexonbpm.model.ReponseDTO.RecordHeaderResponseDto;
import us.dexon.dexonbpm.model.ReponseDTO.ReopenResponseDto;
import us.dexon.dexonbpm.model.ReponseDTO.SaveRecordResponseDto;
import us.dexon.dexonbpm.model.ReponseDTO.TechnicianResponseDto;
import us.dexon.dexonbpm.model.ReponseDTO.TicketResponseDto;
import us.dexon.dexonbpm.model.ReponseDTO.TicketWrapperResponseDto;
import us.dexon.dexonbpm.model.ReponseDTO.TicketsResponseDto;
import us.dexon.dexonbpm.model.RequestDTO.AllLayoutRequestDto;
import us.dexon.dexonbpm.model.RequestDTO.CleanEntityRequestDto;
import us.dexon.dexonbpm.model.RequestDTO.DescendantRequestDto;
import us.dexon.dexonbpm.model.RequestDTO.DocumentInfoRequestDto;
import us.dexon.dexonbpm.model.RequestDTO.PrintTicketRequestDto;
import us.dexon.dexonbpm.model.RequestDTO.RecalculateSLARequestDto;
import us.dexon.dexonbpm.model.RequestDTO.RecordHeaderResquestDto;
import us.dexon.dexonbpm.model.RequestDTO.RelatedActivitiesRequestDto;
import us.dexon.dexonbpm.model.RequestDTO.ReloadRequestDto;
import us.dexon.dexonbpm.model.RequestDTO.ReopenRequestDto;
import us.dexon.dexonbpm.model.RequestDTO.SaveRecordRequestDto;
import us.dexon.dexonbpm.model.RequestDTO.SaveTicketRequestDto;
import us.dexon.dexonbpm.model.RequestDTO.TechnicianRequestDto;
import us.dexon.dexonbpm.model.RequestDTO.TicketByLayoutRequestDto;
import us.dexon.dexonbpm.model.RequestDTO.TicketDetailRequestDto;
import us.dexon.dexonbpm.model.RequestDTO.TicketsRequestDto;
import us.dexon.dexonbpm.model.RequestDTO.WorkflowRequestDto;

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

    RecordHeaderResponseDto getAllRecordsHeaderTable (Context context, RecordHeaderResquestDto recordDetail, int reloginCount, int position);

    TechnicianResponseDto getTechnician(Context context, TechnicianRequestDto headerInfo);

    TicketResponseDto convertToTicketData(JsonObject ticketObject, int selectedTechnician, JsonObject currentTechnician);

    ReopenResponseDto reopenTicket(Context context, ReopenRequestDto reopenInfo, int reloginCount);

    TicketResponseDto reloadTicket(Context context, ReloadRequestDto reloadInfo, int reloginCount);

    TicketResponseDto saveTicket(Context context, SaveTicketRequestDto saveInfo, int reloginCount);

    RecordHeaderResponseDto getAllLayouts(Context context, AllLayoutRequestDto layoutRequestDto, int reloginCount);

    TicketResponseDto getTicketByLayout(Context context, TicketByLayoutRequestDto ticketRequestDto, int reloginCount);

    RecordHeaderResponseDto loadWorkflow(Context context, WorkflowRequestDto workflowRequestDto);

    TicketResponseDto getRelatedActivities(Context context, RelatedActivitiesRequestDto relatedActivitiesRequestDto, int reloginCount);

    DescendantResponseDto createDescendant(Context context, DescendantRequestDto descendantRequestDto, int reloginCount);

    PrintTicketResponseDto printTicket(Context context, PrintTicketRequestDto printTicketRequestDto, int reloginCount);

    TicketResponseDto recalculateSLA(Context context, RecalculateSLARequestDto recalculateSLARequestDto);

    SaveRecordResponseDto saveRercord(Context context, SaveRecordRequestDto saveRecordRequestDto, int reloginCount);

    CleanEntityResponseDto cleanEntity(Context context, CleanEntityRequestDto cleanEntityRequestDto);

    DocumentInfoResponseDto getDocumentData(Context context, DocumentInfoRequestDto documentInfoRequestDto, int reloginCount);
}
