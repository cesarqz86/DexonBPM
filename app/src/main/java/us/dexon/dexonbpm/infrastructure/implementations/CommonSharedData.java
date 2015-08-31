package us.dexon.dexonbpm.infrastructure.implementations;

import android.app.Activity;

import us.dexon.dexonbpm.activity.NewTicketActivity;
import us.dexon.dexonbpm.activity.TicketDetail;
import us.dexon.dexonbpm.model.ReponseDTO.ActivityTreeDto;
import us.dexon.dexonbpm.model.ReponseDTO.RecordHeaderResponseDto;
import us.dexon.dexonbpm.model.ReponseDTO.TicketResponseDto;

/**
 * Created by Cesar Quiroz on 8/23/15.
 */
public class CommonSharedData {

    public static TicketResponseDto TicketInfo;

    public static TicketResponseDto TicketInfoUpdated;

    public static Activity TicketActivity;

    public static Activity ActivityActivity;

    public static RecordHeaderResponseDto TreeData;

    public static ActivityTreeDto NewActivityData;
}
