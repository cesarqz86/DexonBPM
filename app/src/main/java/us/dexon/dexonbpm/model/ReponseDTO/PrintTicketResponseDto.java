package us.dexon.dexonbpm.model.ReponseDTO;

import com.google.gson.annotations.SerializedName;

/**
 * Created by Cesar Quiroz on 9/6/15.
 */
public class PrintTicketResponseDto {

    //region Attributes
    @SerializedName("error")
    private String errorMessage;

    @SerializedName("documentName")
    private String documentName;

    @SerializedName("fullName")
    private String documentFullName;

    @SerializedName("_buffer")
    private String bufferData;

    //endregion

    //region Properties
    public String getErrorMessage() {
        return errorMessage;
    }

    public void setErrorMessage(String errorMessage) {
        this.errorMessage = errorMessage;
    }

    public String getDocumentName() {
        return documentName;
    }

    public void setDocumentName(String documentName) {
        this.documentName = documentName;
    }

    public String getDocumentFullName() {
        return documentFullName;
    }

    public void setDocumentFullName(String documentFullName) {
        this.documentFullName = documentFullName;
    }

    public String getBufferData() {
        return bufferData;
    }

    public void setBufferData(String bufferData) {
        this.bufferData = bufferData;
    }
    //endregion
}
