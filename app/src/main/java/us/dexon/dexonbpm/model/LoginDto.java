package us.dexon.dexonbpm.model;

/**
 * Created by Cesar Quiroz on 4/11/15.
 */
public class LoginDto {

    private String myGivenPass;
    private String userName;
    private String password;

    public String getMyGivenPass() {
        return this.myGivenPass;
    }

    public void setMyGivenPass(String myGivenPass) {
        this.myGivenPass = myGivenPass;
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
}
