package us.dexon.dexonbpm.model.ReponseDTO;

import com.google.gson.annotations.SerializedName;

/**
 * Created by Cesar Quiroz on 5/9/15.
 */
public class LoginResponseDto {

    // Response example:
    /*
    {
        user_id: 44
        person_name: "confcorreo"
        user: "confcorreo"
        logged_on: true
        person_id: 1060
        tech_id: 65
        profile_id: 20
        preferred_lang_ID: 1
        last_error_code: 0
        last_error_str: null
        token: "ZoX0DoOBU15nk3IWQMvSkmmg1U9v1SadEPQeVWsUkwftIT7frO+ni1bjgUBgHAQV.(1060.44.65)."
        sessionId: "F04FE384A48268B0"
        uniqueCode: "ZoX0D44"
     }
     */

    @SerializedName("user_id")
    private int userID;

    @SerializedName("person_name")
    private String personaName;

    @SerializedName("user")
    private String userName;

    @SerializedName("logged_on")
    private boolean isLogged;

    @SerializedName("person_id")
    private int personID;

    @SerializedName("tech_id")
    private int techID;

    @SerializedName("profile_id")
    private int profileID;

    @SerializedName("preferred_lang_ID")
    private int preferLanguageID;

    @SerializedName("last_error_code")
    private int lastErrorCode;

    @SerializedName("last_error_str")
    private String lastErrorMessage;

    @SerializedName("token")
    private String sessionToken;

    @SerializedName("sessionId")
    private String sessionID;

    @SerializedName("uniqueCode")
    private String uniqueCode;

    public int getUserID() {
        return userID;
    }

    public void setUserID(int userID) {
        this.userID = userID;
    }

    public String getPersonaName() {
        return personaName;
    }

    public void setPersonaName(String personaName) {
        this.personaName = personaName;
    }

    public boolean isLogged() {
        return isLogged;
    }

    public void setIsLogged(boolean isLogged) {
        this.isLogged = isLogged;
    }

    public int getPersonID() {
        return personID;
    }

    public void setPersonID(int personID) {
        this.personID = personID;
    }

    public int getTechID() {
        return techID;
    }

    public void setTechID(int techID) {
        this.techID = techID;
    }

    public int getProfileID() {
        return profileID;
    }

    public void setProfileID(int profileID) {
        this.profileID = profileID;
    }

    public int getPreferLanguageID() {
        return preferLanguageID;
    }

    public void setPreferLanguageID(int preferLanguageID) {
        this.preferLanguageID = preferLanguageID;
    }

    public int getLastErrorCode() {
        return lastErrorCode;
    }

    public void setLastErrorCode(int lastErrorCode) {
        this.lastErrorCode = lastErrorCode;
    }

    public String getLastErrorMessage() {
        return lastErrorMessage;
    }

    public void setLastErrorMessage(String lastErrorMessage) {
        this.lastErrorMessage = lastErrorMessage;
    }

    public String getSessionToken() {
        return sessionToken;
    }

    public void setSessionToken(String sessionToken) {
        this.sessionToken = sessionToken;
    }

    public String getSessionID() {
        return sessionID;
    }

    public void setSessionID(String sessionID) {
        this.sessionID = sessionID;
    }

    public String getUniqueCode() {
        return uniqueCode;
    }

    public void setUniqueCode(String uniqueCode) {
        this.uniqueCode = uniqueCode;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }
}
