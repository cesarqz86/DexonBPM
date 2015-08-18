package us.dexon.dexonbpm.model.ReponseDTO;

import com.google.gson.JsonObject;
import com.google.gson.annotations.SerializedName;

import java.util.List;

/**
 * Created by Cesar Quiroz on 8/17/15.
 */
public class TicketResponseDto {

    //region Attributes
    @SerializedName("result")
    private String resultMessage;
    @SerializedName("error")
    private String errorMessage;
    private Double circularPercentDone;
    private Double barPercentDone;
    private JsonObject currentTechnician;
    private JsonObject ticketInfo;
    private Boolean isOpen;
    private Boolean isEditable;
    private List<TicketDetailDataDto> dataList;
    //endregion

    //region Properties
    public Double getCircularPercentDone() {
        return circularPercentDone;
    }

    public void setCircularPercentDone(Double circularPercentDone) {
        this.circularPercentDone = circularPercentDone;
    }

    public JsonObject getCurrentTechnician() {
        return currentTechnician;
    }

    public void setCurrentTechnician(JsonObject currentTechnician) {
        this.currentTechnician = currentTechnician;
    }

    public Double getBarPercentDone() {
        return barPercentDone;
    }

    public void setBarPercentDone(Double barPercentDone) {
        this.barPercentDone = barPercentDone;
    }

    public JsonObject getTicketInfo() {
        return ticketInfo;
    }

    public void setTicketInfo(JsonObject ticketInfo) {
        this.ticketInfo = ticketInfo;
    }

    public Boolean getIsOpen() {
        return isOpen;
    }

    public void setIsOpen(Boolean isOpen) {
        this.isOpen = isOpen;
    }

    public Boolean getIsEditable() {
        return isEditable;
    }

    public void setIsEditable(Boolean isEditable) {
        this.isEditable = isEditable;
    }

    public String getResultMessage() {
        return resultMessage;
    }

    public void setResultMessage(String resultMessage) {
        this.resultMessage = resultMessage;
    }

    public String getErrorMessage() {
        return errorMessage;
    }

    public void setErrorMessage(String errorMessage) {
        this.errorMessage = errorMessage;
    }

    public List<TicketDetailDataDto> getDataList() {
        return dataList;
    }

    public void setDataList(List<TicketDetailDataDto> dataList) {
        this.dataList = dataList;
    }
    //endregion

    //region Constructor
    //endregion

    //region Public Methods
    //endregion

    //region Private Methods
    //endregion
}
