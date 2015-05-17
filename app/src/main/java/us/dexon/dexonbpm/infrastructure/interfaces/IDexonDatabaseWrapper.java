package us.dexon.dexonbpm.infrastructure.interfaces;

import android.content.Context;

import us.dexon.dexonbpm.model.ReponseDTO.LoginResponseDto;

/**
 * Created by Cesar Quiroz on 5/16/15.
 */
public interface IDexonDatabaseWrapper {

    void setContext(Context contextData);

    void saveLoggedUser(LoginResponseDto loggedUser);

    LoginResponseDto getLoggedUser();

    void deleteUserTable();
}
