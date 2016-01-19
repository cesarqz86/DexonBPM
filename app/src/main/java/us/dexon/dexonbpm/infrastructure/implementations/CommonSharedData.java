package us.dexon.dexonbpm.infrastructure.implementations;

import android.app.Activity;

import com.google.gson.JsonObject;

import java.util.List;

import us.dexon.dexonbpm.activity.IncidentsActivity;
import us.dexon.dexonbpm.activity.NewTicketActivity;
import us.dexon.dexonbpm.activity.TicketDetail;
import us.dexon.dexonbpm.model.ReponseDTO.ActivityTreeDto;
import us.dexon.dexonbpm.model.ReponseDTO.AttachmentItem;
import us.dexon.dexonbpm.model.ReponseDTO.RecordHeaderResponseDto;
import us.dexon.dexonbpm.model.ReponseDTO.TicketRelatedDataDto;
import us.dexon.dexonbpm.model.ReponseDTO.TicketResponseDto;

/**
 * Created by Cesar Quiroz on 8/23/15.
 */
public class CommonSharedData {

    public static TicketResponseDto TicketInfo;

    public static TicketResponseDto TicketInfoUpdated;

    public static Activity TicketActivity;

    public static Activity ActivityActivity;

    public static Activity RelatedDataActivity;

    public static RecordHeaderResponseDto TreeData;

    public static ActivityTreeDto NewActivityData;

    public static List<ActivityTreeDto> ActivityList;

    public static JsonObject OriginalTechnician;

    public static JsonObject OriginalState;

    public static IncidentsActivity IncidentListActivity;

    public static JsonObject RelatedData;

    public static JsonObject RelatedDataDetail;

    public static JsonObject MultilineData;

    public static String MultilineDataValue;

    public static TicketRelatedDataDto SelectedRelatedData;

    public static Boolean IsOnClick;

    public static Boolean IsReloadRelatedData;

    public static List<AttachmentItem> AttachmentList;
}
