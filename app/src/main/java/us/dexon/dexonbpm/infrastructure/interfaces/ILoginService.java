package us.dexon.dexonbpm.infrastructure.interfaces;

import android.content.Context;

import us.dexon.dexonbpm.model.ReponseDTO.LoginResponseDto;
import us.dexon.dexonbpm.model.RequestDTO.LoginRequestDto;

/**
 * Created by Cesar Quiroz on 5/9/15.
 */
public interface ILoginService {

    LoginResponseDto loginUser(Context context, LoginRequestDto loginData);

}
