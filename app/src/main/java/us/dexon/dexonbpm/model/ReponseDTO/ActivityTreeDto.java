package us.dexon.dexonbpm.model.ReponseDTO;

import com.google.gson.JsonObject;

/**
 * Created by Cesar Quiroz on 8/31/15.
 */
public class ActivityTreeDto {

    //region Attributes
    private String elementName;
    private JsonObject activityJson;
    private JsonObject layoutID;
    private JsonObject technicianID;
    private JsonObject plannedStart;
    private JsonObject plannedEnd;
    private JsonObject firstComment;
    private JsonObject adjustSLA;
    private JsonObject nextStatusID;
    //endregion

    //region Properties
    public String getElementName() {
        return elementName;
    }

    public void setElementName(String elementName) {
        this.elementName = elementName;
    }

    public JsonObject getLayoutID() {
        return layoutID;
    }

    public void setLayoutID(JsonObject layoutID) {
        this.layoutID = layoutID;
    }

    public JsonObject getTechnicianID() {
        return technicianID;
    }

    public void setTechnicianID(JsonObject technicianID) {
        this.technicianID = technicianID;
    }

    public JsonObject getPlannedStart() {
        return plannedStart;
    }

    public void setPlannedStart(JsonObject plannedStart) {
        this.plannedStart = plannedStart;
    }

    public JsonObject getPlannedEnd() {
        return plannedEnd;
    }

    public void setPlannedEnd(JsonObject plannedEnd) {
        this.plannedEnd = plannedEnd;
    }

    public JsonObject getFirstComment() {
        return firstComment;
    }

    public void setFirstComment(JsonObject firstComment) {
        this.firstComment = firstComment;
    }

    public JsonObject getAdjustSLA() {
        return adjustSLA;
    }

    public void setAdjustSLA(JsonObject adjustSLA) {
        this.adjustSLA = adjustSLA;
    }

    public JsonObject getNextStatusID() {
        return nextStatusID;
    }

    public void setNextStatusID(JsonObject nextStatusID) {
        this.nextStatusID = nextStatusID;
    }

    public JsonObject getActivityJson() {
        return activityJson;
    }

    public void setActivityJson(JsonObject activityJson) {
        this.activityJson = activityJson;
    }
    //endregion

    //region Constructor
    //endregion

    //region Public Methods
    //endregion

    //region Private Methods
    //endregion

}
