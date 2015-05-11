package us.dexon.dexonbpm.infrastructure.interfaces;

import android.content.Context;

import us.dexon.dexonbpm.model.ReponseDTO.ChangePassResponseDto;
import us.dexon.dexonbpm.model.RequestDTO.ChangePassRequestDto;

/**
 * Created by Cesar Quiroz on 5/11/15.
 */
public interface IChangePasswordService {

    ChangePassResponseDto changePassword(Context context, ChangePassRequestDto changeRequest);
}
