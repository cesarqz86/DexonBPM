package us.dexon.dexonbpm.infrastructure.enums;

/**
 * Created by Cesar Quiroz on 4/15/15.
 */
public enum MessageTypeIcon {

    Success(0),
    Warning(1),
    Error(2),
    Information(3),
    Question(4);

    private int code;

    private MessageTypeIcon(int code) {
        this.code = code;
    }

    public int getCode() {
        return this.code;
    }
}