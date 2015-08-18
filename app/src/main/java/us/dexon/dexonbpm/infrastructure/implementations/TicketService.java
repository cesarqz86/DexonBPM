package us.dexon.dexonbpm.infrastructure.implementations;

import android.content.Context;
import android.os.DropBoxManager;
import android.util.Log;

import com.fasterxml.jackson.core.JsonFactory;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.json.GsonHttpMessageConverter;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.HttpServerErrorException;
import org.springframework.web.client.RestTemplate;

import java.io.IOException;
import java.net.URI;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import us.dexon.dexonbpm.infrastructure.enums.RenderControlType;
import us.dexon.dexonbpm.infrastructure.interfaces.IDexonDatabaseWrapper;
import us.dexon.dexonbpm.infrastructure.interfaces.ILoginService;
import us.dexon.dexonbpm.infrastructure.interfaces.ITicketService;
import us.dexon.dexonbpm.model.ReponseDTO.LoginResponseDto;
import us.dexon.dexonbpm.model.ReponseDTO.TicketDetailDataDto;
import us.dexon.dexonbpm.model.ReponseDTO.TicketResponseDto;
import us.dexon.dexonbpm.model.ReponseDTO.TicketWrapperResponseDto;
import us.dexon.dexonbpm.model.RequestDTO.LoginRequestDto;
import us.dexon.dexonbpm.model.RequestDTO.TicketDetailRequestDto;
import us.dexon.dexonbpm.model.RequestDTO.TicketsRequestDto;

/**
 * Created by Cesar Quiroz on 5/9/15.
 */
public class TicketService implements ITicketService {

    //region Attributes
    /**
     * Single instance created upon class loading.
     */
    private static final TicketService fINSTANCE = new TicketService();

    private static String TICKETS_URL = "api/Incident/GetTickets";
    private static String TICKET_URL = "api/Incident/GetTicket";
    private int columnCount = 6;
    //endregion

    //region Properties
    public static TicketService getInstance() {
        return fINSTANCE;
    }
    //endregion

    //region Constructor

    /**
     * Private constructor prevents construction outside this class.
     */
    private TicketService() {
    }
    //endregion

    //region Public Methods
    @Override
    public TicketWrapperResponseDto getTicketData(Context context, TicketsRequestDto ticketFilter, int reloginCount) {
        TicketWrapperResponseDto finalResponse = new TicketWrapperResponseDto();
        Gson gsonSerializer = new Gson();
        try {
            String finalUrl = ConfigurationService.getConfigurationValue(context, "URLBase");
            finalUrl += TICKETS_URL;

            RestTemplate restTemplate = new RestTemplate();
            restTemplate.getMessageConverters().add(new GsonHttpMessageConverter());

            HttpHeaders headers = new HttpHeaders();
            headers.setAccept(Arrays.asList(MediaType.APPLICATION_JSON));
            headers.setContentType(MediaType.APPLICATION_JSON);
            HttpEntity<?> entity = new HttpEntity<Object>(ticketFilter, headers);
            ResponseEntity<JsonElement> response;
            response = restTemplate.exchange(new URI(finalUrl), HttpMethod.POST, entity, JsonElement.class);
            JsonElement jsonData = response.getBody();
            finalResponse.setTicketArrayData(this.convertJsonToTicketArray(jsonData));
        } catch (HttpServerErrorException ex) {
            finalResponse = gsonSerializer.fromJson(ex.getResponseBodyAsString(), TicketWrapperResponseDto.class);
            finalResponse.setTicketArrayData(this.getEmptyData());
            Log.e("CallingService: " + TICKETS_URL, ex.getResponseBodyAsString() + ex.getStatusText(), ex);
        } catch (HttpClientErrorException ex) {
            if (ex.getStatusCode() != HttpStatus.INTERNAL_SERVER_ERROR && reloginCount < 2) {
                ILoginService loginService = LoginService.getInstance();
                LoginRequestDto loginRequestData = ConfigurationService.getUserInfo(context);
                LoginResponseDto loggedUser = loginService.loginUser(context, loginRequestData);
                ticketFilter.setLoggedUser(loggedUser);
                finalResponse = this.getTicketData(context, ticketFilter, reloginCount++);
            } else {
                finalResponse = gsonSerializer.fromJson(ex.getResponseBodyAsString(), TicketWrapperResponseDto.class);
            }
            finalResponse.setTicketArrayData(this.getEmptyData());
            Log.e("CallingService: " + TICKETS_URL, ex.getResponseBodyAsString() + ex.getStatusText(), ex);
        } catch (Exception ex) {
            finalResponse.setErrorMessage(ex.getMessage());
            finalResponse.setTicketArrayData(this.getEmptyData());
            Log.e("CallingService: " + TICKETS_URL, ex.getMessage(), ex);
        }

        if (finalResponse.getTicketArrayData() == null) {
            finalResponse.setTicketArrayData(this.getEmptyData());
        }

        return finalResponse;
    }

    @Override
    public void getTicketDataDB(Context context, TicketsRequestDto ticketFilter) {
        try {
            String finalUrl = ConfigurationService.getConfigurationValue(context, "URLBase");
            finalUrl += TICKETS_URL;

            RestTemplate restTemplate = new RestTemplate();
            restTemplate.getMessageConverters().add(new GsonHttpMessageConverter());

            HttpHeaders headers = new HttpHeaders();
            headers.setAccept(Arrays.asList(MediaType.APPLICATION_JSON));
            headers.setContentType(MediaType.APPLICATION_JSON);
            HttpEntity<?> entity = new HttpEntity<Object>(ticketFilter, headers);
            ResponseEntity<JsonElement> response;
            response = restTemplate.exchange(new URI(finalUrl), HttpMethod.POST, entity, JsonElement.class);
            JsonElement jsonData = response.getBody();
            this.saveJsonToDB(jsonData);
        } catch (HttpServerErrorException ex) {
            Log.e("CallingService: " + TICKETS_URL, ex.getResponseBodyAsString() + ex.getStatusText(), ex);
        } catch (HttpClientErrorException ex) {
            if (ex.getStatusCode() != HttpStatus.INTERNAL_SERVER_ERROR) {
                ILoginService loginService = LoginService.getInstance();
                LoginRequestDto loginRequestData = ConfigurationService.getUserInfo(context);
                LoginResponseDto loggedUser = loginService.loginUser(context, loginRequestData);
                ticketFilter.setLoggedUser(loggedUser);
                this.getTicketDataDB(context, ticketFilter);
            }
            Log.e("CallingService: " + TICKETS_URL, ex.getResponseBodyAsString() + ex.getStatusText(), ex);
        } catch (Exception ex) {
            Log.e("CallingService: " + TICKETS_URL, ex.getMessage(), ex);
        }
    }

    public TicketResponseDto getTicketInfo(Context context, TicketDetailRequestDto ticketDetail, int reloginCount) {
        TicketResponseDto finalResponse = new TicketResponseDto();
        Gson gsonSerializer = new Gson();
        try {
            String finalUrl = ConfigurationService.getConfigurationValue(context, "URLBase");
            finalUrl += TICKET_URL;

            RestTemplate restTemplate = new RestTemplate();
            restTemplate.getMessageConverters().add(new GsonHttpMessageConverter());

            HttpHeaders headers = new HttpHeaders();
            headers.setAccept(Arrays.asList(MediaType.APPLICATION_JSON));
            headers.setContentType(MediaType.APPLICATION_JSON);
            HttpEntity<?> entity = new HttpEntity<Object>(ticketDetail, headers);
            ResponseEntity<JsonElement> response;
            response = restTemplate.exchange(new URI(finalUrl), HttpMethod.POST, entity, JsonElement.class);
            JsonElement jsonData = response.getBody();
            finalResponse = this.convertToTicketData(jsonData);

        } catch (HttpServerErrorException ex) {
            finalResponse = gsonSerializer.fromJson(ex.getResponseBodyAsString(), TicketResponseDto.class);
            Log.e("CallingService: " + TICKET_URL, ex.getResponseBodyAsString() + ex.getStatusText(), ex);
        } catch (HttpClientErrorException ex) {
            if (ex.getStatusCode() != HttpStatus.INTERNAL_SERVER_ERROR && reloginCount < 2) {
                ILoginService loginService = LoginService.getInstance();
                LoginRequestDto loginRequestData = ConfigurationService.getUserInfo(context);
                LoginResponseDto loggedUser = loginService.loginUser(context, loginRequestData);
                ticketDetail.setLoggedUser(loggedUser);
                this.getTicketInfo(context, ticketDetail, reloginCount++);
            } else {
                finalResponse = gsonSerializer.fromJson(ex.getResponseBodyAsString(), TicketResponseDto.class);
            }
            Log.e("CallingService: " + TICKET_URL, ex.getResponseBodyAsString() + ex.getStatusText(), ex);
        } catch (Exception ex) {
            finalResponse.setErrorMessage(ex.getMessage());
            Log.e("CallingService: " + TICKET_URL, ex.getMessage(), ex);
        }
        return finalResponse;
    }
    //endregion

    //region Private Methods
    private String[][] convertJsonToTicketArray(JsonElement jsonElement) throws IOException {
        String[][] finalResponse = null;
        Gson gsonSerializer = new Gson();

        JsonFactory factory = new JsonFactory();
        ObjectMapper jacksonSerializer = new ObjectMapper(factory);
        TypeReference<LinkedHashMap<String, String>> typeReference = new TypeReference<LinkedHashMap<String, String>>() {
        };
        if (jsonElement != null && jsonElement.isJsonArray()) {
            JsonArray arrayData = jsonElement.getAsJsonArray();
            String ticketString;
            String[] ticketResponseData;
            LinkedHashMap<String, String> ticketDataList;
            LinkedHashMap<String, String> ticketDataListTemp;
            int finalSize = arrayData.size() + 1;
            finalResponse = new String[finalSize][];
            int indexPosition = 0;
            for (JsonElement ticketData : arrayData) {
                ticketString = gsonSerializer.toJson(ticketData);
                ticketDataListTemp = jacksonSerializer.readValue(ticketString, typeReference);

                String ticketID = ticketDataListTemp.get("TICKET");
                ticketDataListTemp.remove("TICKET");
                ticketDataList = new LinkedHashMap<>();
                ticketDataList.put("TICKET", ticketID);
                ticketDataList.putAll(ticketDataListTemp);

                ticketResponseData = ticketDataList.values().toArray(new String[0]);
                if (indexPosition == 0) {
                    finalResponse[indexPosition] = ticketDataList.keySet().toArray(new String[0]);
                    columnCount = finalResponse[indexPosition].length;
                    indexPosition++;
                }
                finalResponse[indexPosition] = ticketResponseData;
                indexPosition++;
            }
        } else {
            finalResponse = this.getEmptyData();
        }
        return finalResponse;
    }

    public String[][] getEmptyData() {
        String[][] finalResponse = new String[10][];
        String[] headerRow = {"TICKET", " ", " ", " ", " "};
        String[] dataRow = {" ", " ", " ", " ", " "};

        for (int index = 0; index < finalResponse.length; index++) {
            if (index == 0) {
                finalResponse[index] = headerRow;
            } else {
                finalResponse[index] = dataRow;
            }
        }
        return finalResponse;
    }

    private void saveJsonToDB(JsonElement jsonElement) throws IOException {
        Gson gsonSerializer = new Gson();

        JsonFactory factory = new JsonFactory();
        ObjectMapper jacksonSerializer = new ObjectMapper(factory);
        TypeReference<HashMap<String, Object>> typeReference = new TypeReference<HashMap<String, Object>>() {
        };
        IDexonDatabaseWrapper databaseWrapper = DexonDatabaseWrapper.getInstance();
        if (jsonElement != null && jsonElement.isJsonArray()) {
            JsonArray arrayData = jsonElement.getAsJsonArray();
            String ticketString;
            HashMap<String, Object> ticketDataList;
            databaseWrapper.deleteTicketTable();
            for (JsonElement ticketData : arrayData) {
                ticketString = gsonSerializer.toJson(ticketData);
                ticketDataList = jacksonSerializer.readValue(ticketString, typeReference);
                String ticketID = String.valueOf(ticketDataList.get("TICKET"));
                String incidentID = String.valueOf(ticketDataList.get("HD_INCIDENT_ID"));
                ticketDataList.remove("TICKET");
                databaseWrapper.saveTicketData(ticketID, incidentID, ticketDataList);
            }
        }
    }

    private TicketResponseDto convertToTicketData(JsonElement jsonElement) {
        TicketResponseDto finalResult = new TicketResponseDto();
        if (jsonElement != null) {
            JsonObject ticketObject = jsonElement.getAsJsonObject();
            finalResult.setTicketInfo(ticketObject);
            JsonObject headerInfo = ticketObject.getAsJsonObject("headerInfo");
            JsonObject currentTechnician = headerInfo.getAsJsonObject("current_technician");
            JsonObject taskProgress = headerInfo.getAsJsonObject("taskProgress");
            finalResult.setCircularPercentDone(headerInfo.get("percent").getAsDouble());
            finalResult.setCurrentTechnician(currentTechnician);

            JsonArray taskProgressFields = taskProgress.getAsJsonArray("fields");
            if (taskProgressFields != null && taskProgressFields.size() > 0) {
                for (JsonElement fields : taskProgressFields) {
                    JsonObject tempField = fields.getAsJsonObject();
                    String fieldName = tempField.get("field_name").getAsString();
                    if (CommonValidations.validateEmpty(fieldName) && fieldName.equals("PERCENT_DONE")) {
                        if (tempField.get("Value") != null && !tempField.get("Value").isJsonNull()) {
                            finalResult.setBarPercentDone(tempField.get("Value").getAsDouble());
                        } else {
                            finalResult.setBarPercentDone(-1d);
                        }
                        break;
                    }
                }
            }

            finalResult.setIsOpen(ticketObject.get("isOpen").getAsBoolean());
            finalResult.setIsEditable(ticketObject.get("editable").getAsBoolean());

            List<TicketDetailDataDto> finalDataList = new ArrayList<>();
            //Object[] test = headerInfo.entrySet().toArray();

            for (Map.Entry<String, JsonElement> headerInfoData : headerInfo.entrySet()) {
                if (headerInfoData.getValue() != null && headerInfoData.getValue().isJsonObject() && !headerInfoData.getValue().isJsonPrimitive()) {
                    JsonObject tempData = headerInfoData.getValue().getAsJsonObject();
                    if (tempData.has("is_hidden") && !tempData.get("is_hidden").getAsBoolean()) {
                        TicketDetailDataDto tempDataList = new TicketDetailDataDto();
                        RenderControlType controlType = RenderControlType.values()[tempData.get("render_ctl").getAsInt()];
                        if (headerInfoData.getKey().equals("current_technician")) {
                            controlType = RenderControlType.DXControlsGridWithOptions;
                        }
                        tempDataList.setFieldType(controlType);
                        tempDataList.setFieldName(tempData.get("display_name").getAsString());
                        tempDataList.setFieldKey(headerInfoData.getKey());
                        tempDataList.setOrder(tempData.get("order").getAsInt());
                        tempDataList.setFieldValue(this.getValueFromTicketField(tempData, controlType));
                        finalDataList.add(tempDataList);
                    }
                }
            }

            if (!finalDataList.isEmpty()) {
                Collections.sort(finalDataList, new Comparator<TicketDetailDataDto>() {
                    @Override
                    public int compare(TicketDetailDataDto c1, TicketDetailDataDto c2) {
                        int compareResult = 0;
                        if (c1.getOrder() < c2.getOrder())
                            compareResult = -1;
                        else if (c1.getOrder() > c2.getOrder())
                            compareResult = 1;

                        return compareResult;
                    }
                });
            }
            finalResult.setDataList(finalDataList);
        }
        return finalResult;
    }

    private String getValueFromTicketField(JsonObject nodeData, RenderControlType controlType) {
        String finalResult = "";
        JsonElement finalNodeData = null;

        switch (controlType) {
            case DXControlsInt:
            case DXControlsFloat:
            case DXControlsLine:
            case DXControlsPhone:
            case DXControlsCellphone:
            case DXControlsEmail:
            case DXControlsMultiline:
            case DXControlsPassword:
            case DXControlsCurrency:
            case DXControlsCharacter:
            case DXControlsImage:
            case DXControlsColor:
            case DXControlsDate:
            case DXControlsCheckbox:
            case DXControlsContainer:
            case DXControlsTable: {
                finalNodeData = nodeData.get("Value");
                break;
            }
            case DXControlsGrid:
            case DXControlsTree:
            case DXControlsLabel:
            case DXControlsGridWithOptions: {
                if (!nodeData.getAsJsonObject("son").isJsonNull()) {
                    JsonObject sonData = nodeData.getAsJsonObject("son");
                    finalNodeData = sonData.get("short_description");
                }
                break;
            }
            default: {
                break;
            }
        }
        if (finalNodeData != null && !finalNodeData.isJsonNull()) {
            finalResult = finalNodeData.getAsString();
        }
        return finalResult;
    }
    //endregion
}
