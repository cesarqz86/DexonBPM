package us.dexon.dexonbpm.model.RequestDTO;

import com.google.gson.annotations.SerializedName;

/**
 * Created by Cesar Quiroz on 4/11/15.
 */
public class LoginRequestDto {

    /* Example of the request:
        {
            myGivenPass:'jdheGYklRTpeG788r4HJ877904JJHfgKLOFGHjIJMtGHBN-20150120-Android_App',
            userName: 'confcorreo',
            password: '12345'
        }
     */

    //region Attributes
    @SerializedName("myGivenPass")
    private String applicationPassword;

    @SerializedName("userName")
    private String userName;

    @SerializedName("password")
    private String password;
    //endregion

    //region Properties

    public String getMyGivenPass() {
        return this.applicationPassword;
    }

    public void setMyGivenPass(String myGivenPass) {
        this.applicationPassword = myGivenPass;
    }

    public String getUserName() {
        return this.userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getPassword() {
        return this.password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    //endregion

    //region Constructor
    //endregion

    //region Public Methods
    //endregion

    //region Private Methods
    //endregion
}
