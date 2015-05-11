package us.dexon.dexonbpm.infrastructure.interfaces;

import android.content.Context;

import us.dexon.dexonbpm.model.ReponseDTO.ForgotPassResponseDto;
import us.dexon.dexonbpm.model.RequestDTO.ForgotPassRequestDto;

/**
 * Created by Cesar Quiroz on 5/11/15.
 */
public interface IForgotPasswordService {

    ForgotPassResponseDto restorePassword(Context context, ForgotPassRequestDto requestData);

}
