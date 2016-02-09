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
import org.springframework.http.client.ClientHttpRequestInterceptor;
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
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import us.dexon.dexonbpm.R;
import us.dexon.dexonbpm.infrastructure.enums.RenderControlType;
import us.dexon.dexonbpm.infrastructure.interfaces.IDexonDatabaseWrapper;
import us.dexon.dexonbpm.infrastructure.interfaces.ILoginService;
import us.dexon.dexonbpm.infrastructure.interfaces.ITicketService;
import us.dexon.dexonbpm.model.ReponseDTO.CleanEntityResponseDto;
import us.dexon.dexonbpm.model.ReponseDTO.DescendantResponseDto;
import us.dexon.dexonbpm.model.ReponseDTO.DocumentInfoResponseDto;
import us.dexon.dexonbpm.model.ReponseDTO.LoginResponseDto;
import us.dexon.dexonbpm.model.ReponseDTO.PrintTicketResponseDto;
import us.dexon.dexonbpm.model.ReponseDTO.RecordHeaderResponseDto;
import us.dexon.dexonbpm.model.ReponseDTO.ReopenResponseDto;
import us.dexon.dexonbpm.model.ReponseDTO.SaveRecordResponseDto;
import us.dexon.dexonbpm.model.ReponseDTO.TechnicianResponseDto;
import us.dexon.dexonbpm.model.ReponseDTO.TicketDetailDataDto;
import us.dexon.dexonbpm.model.ReponseDTO.TicketRelatedDataDto;
import us.dexon.dexonbpm.model.ReponseDTO.TicketResponseDto;
import us.dexon.dexonbpm.model.ReponseDTO.TicketWrapperResponseDto;
import us.dexon.dexonbpm.model.ReponseDTO.TreeDataDto;
import us.dexon.dexonbpm.model.RequestDTO.AllLayoutRequestDto;
import us.dexon.dexonbpm.model.RequestDTO.CleanEntityRequestDto;
import us.dexon.dexonbpm.model.RequestDTO.DescendantRequestDto;
import us.dexon.dexonbpm.model.RequestDTO.DocumentInfoRequestDto;
import us.dexon.dexonbpm.model.RequestDTO.LoginRequestDto;
import us.dexon.dexonbpm.model.RequestDTO.PrintTicketRequestDto;
import us.dexon.dexonbpm.model.RequestDTO.RecalculateSLARequestDto;
import us.dexon.dexonbpm.model.RequestDTO.RecordHeaderResquestDto;
import us.dexon.dexonbpm.model.RequestDTO.RelatedActivitiesRequestDto;
import us.dexon.dexonbpm.model.RequestDTO.ReloadRequestDto;
import us.dexon.dexonbpm.model.RequestDTO.ReopenRequestDto;
import us.dexon.dexonbpm.model.RequestDTO.SaveRecordRequestDto;
import us.dexon.dexonbpm.model.RequestDTO.SaveTicketRequestDto;
import us.dexon.dexonbpm.model.RequestDTO.TechnicianRequestDto;
import us.dexon.dexonbpm.model.RequestDTO.TicketByLayoutRequestDto;
import us.dexon.dexonbpm.model.RequestDTO.TicketDetailRequestDto;
import us.dexon.dexonbpm.model.RequestDTO.TicketsRequestDto;
import us.dexon.dexonbpm.model.RequestDTO.WorkflowRequestDto;

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
    private static String RECORD_HEADER_URL = "api/Header/GetAllRecordsControlHeader";
    private static String TECHNICIAN_URL = "api/Incident/GetLessUsedTechnician";
    private static String REOPEN_TICKET_URL = "api/Incident/ReopenTicket";
    private static String RELOAD_TICKET_URL = "api/Incident/LoadTicket";
    private static String SAVE_TICKET_URL = "api/Incident/SaveTicket";
    private static String ALL_LAYOUT_URL = "api/Header/GetAllLayouts";
    private static String TICKET_LAYOUT_URL = "api/Incident/GetTicketByLayout";
    private static String LOAD_WORKFLOW_URL = "api/Incident/GetWFResult";
    private static String RELATED_ACT_URL = "api/Incident/GetActivitiesRelated";
    private static String CREATE_DESCENDANT_URL = "api/Incident/CreateDescendant";
    private static String PRINT_TICKET_URL = "api/Incident/PrintTicket";
    private static String RECALCULATE_SLA_URL = "api/Incident/RecalculateSLA";
    private static String SAVE_RECORD_URL = "api/Incident/SaveRecord";
    private static String CLEAN_ENTITY_URL = "api/Helper/CleanEntity";
    private static String GET_ATTACHMENT_URL = "api/Incident/GetDocumentByRecordID";

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
            finalResponse.setTicketArrayData(this.getEmptyData("TICKET"));
            Log.e("CallingService: " + TICKETS_URL, ex.getResponseBodyAsString() + ex.getStatusText(), ex);
        } catch (HttpClientErrorException ex) {
            if (ex.getStatusCode() == HttpStatus.UNAUTHORIZED && reloginCount < 2) {
                ILoginService loginService = LoginService.getInstance();
                LoginRequestDto loginRequestData = ConfigurationService.getUserInfo(context);
                LoginResponseDto loggedUser = loginService.loginUser(context, loginRequestData);
                ticketFilter.setLoggedUser(loggedUser);
                finalResponse = this.getTicketData(context, ticketFilter, reloginCount + 1);
            } else {
                finalResponse = gsonSerializer.fromJson(ex.getResponseBodyAsString(), TicketWrapperResponseDto.class);
            }
            finalResponse.setTicketArrayData(this.getEmptyData("TICKET"));
            Log.e("CallingService: " + TICKETS_URL, ex.getResponseBodyAsString() + ex.getStatusText(), ex);
        } catch (Exception ex) {
            finalResponse.setErrorMessage(ex.getMessage());
            finalResponse.setTicketArrayData(this.getEmptyData("TICKET"));
            Log.e("CallingService: " + TICKETS_URL, ex.getMessage(), ex);
        }

        if (finalResponse.getTicketArrayData() == null) {
            finalResponse.setTicketArrayData(this.getEmptyData("TICKET"));
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
            if (ex.getStatusCode() == HttpStatus.UNAUTHORIZED) {
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

    public String[][] getEmptyData(String firstColumnTitle) {
        String[][] finalResponse = new String[10][];
        String[] headerRow = {firstColumnTitle, "", "", "", ""};
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
            if (ex.getStatusCode() == HttpStatus.UNAUTHORIZED && reloginCount < 2) {
                ILoginService loginService = LoginService.getInstance();
                LoginRequestDto loginRequestData = ConfigurationService.getUserInfo(context);
                LoginResponseDto loggedUser = loginService.loginUser(context, loginRequestData);
                ticketDetail.setLoggedUser(loggedUser);
                this.getTicketInfo(context, ticketDetail, reloginCount + 1);
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

    public RecordHeaderResponseDto getAllRecordsHeaderTree(Context context, RecordHeaderResquestDto recordDetail, int reloginCount) {
        RecordHeaderResponseDto finalResponse = new RecordHeaderResponseDto();
        Gson gsonSerializer = new Gson();
        try {
            String finalUrl = ConfigurationService.getConfigurationValue(context, "URLBase");
            finalUrl += RECORD_HEADER_URL;

            RestTemplate restTemplate = new RestTemplate();
            restTemplate.getMessageConverters().add(new GsonHttpMessageConverter());

            HttpHeaders headers = new HttpHeaders();
            headers.setAccept(Arrays.asList(MediaType.APPLICATION_JSON));
            headers.setContentType(MediaType.APPLICATION_JSON);
            HttpEntity<?> entity = new HttpEntity<Object>(recordDetail, headers);
            ResponseEntity<JsonElement> response;
            response = restTemplate.exchange(new URI(finalUrl), HttpMethod.POST, entity, JsonElement.class);
            JsonElement jsonData = response.getBody();
            finalResponse = this.convertToRecordHeaderTree(jsonData, recordDetail.getFieldInformation());

        } catch (HttpServerErrorException ex) {
            finalResponse = gsonSerializer.fromJson(ex.getResponseBodyAsString(), RecordHeaderResponseDto.class);
            Log.e("CallingService: " + RECORD_HEADER_URL, ex.getResponseBodyAsString() + ex.getStatusText(), ex);
        } catch (HttpClientErrorException ex) {
            if (ex.getStatusCode() == HttpStatus.UNAUTHORIZED && reloginCount < 2) {
                ILoginService loginService = LoginService.getInstance();
                LoginRequestDto loginRequestData = ConfigurationService.getUserInfo(context);
                LoginResponseDto loggedUser = loginService.loginUser(context, loginRequestData);
                recordDetail.setLoggedUser(loggedUser);
                this.getAllRecordsHeaderTree(context, recordDetail, reloginCount + 1);
            } else {
                finalResponse = gsonSerializer.fromJson(ex.getResponseBodyAsString(), RecordHeaderResponseDto.class);
            }
            Log.e("CallingService: " + RECORD_HEADER_URL, ex.getResponseBodyAsString() + ex.getStatusText(), ex);
        } catch (Exception ex) {
            finalResponse.setErrorMessage(ex.getMessage());
            Log.e("CallingService: " + RECORD_HEADER_URL, ex.getMessage(), ex);
        }
        return finalResponse;
    }

    public RecordHeaderResponseDto getAllRecordsHeaderTable(Context context, RecordHeaderResquestDto recordDetail, int reloginCount) {
        RecordHeaderResponseDto finalResponse = new RecordHeaderResponseDto();
        Gson gsonSerializer = new Gson();
        try {
            String finalUrl = ConfigurationService.getConfigurationValue(context, "URLBase");
            finalUrl += RECORD_HEADER_URL;

            RestTemplate restTemplate = new RestTemplate();
            restTemplate.getMessageConverters().add(new GsonHttpMessageConverter());

            HttpHeaders headers = new HttpHeaders();
            headers.setAccept(Arrays.asList(MediaType.APPLICATION_JSON));
            headers.setContentType(MediaType.APPLICATION_JSON);
            HttpEntity<?> entity = new HttpEntity<Object>(recordDetail, headers);
            ResponseEntity<JsonElement> response;
            response = restTemplate.exchange(new URI(finalUrl), HttpMethod.POST, entity, JsonElement.class);
            JsonElement jsonData = response.getBody();
            finalResponse = this.convertToRecordHeaderTable(jsonData, recordDetail.getFieldInformation());
        } catch (HttpServerErrorException ex) {
            finalResponse = gsonSerializer.fromJson(ex.getResponseBodyAsString(), RecordHeaderResponseDto.class);
            Log.e("CallingService: " + RECORD_HEADER_URL, ex.getResponseBodyAsString() + ex.getStatusText(), ex);
        } catch (HttpClientErrorException ex) {
            if (ex.getStatusCode() == HttpStatus.UNAUTHORIZED && reloginCount < 2) {
                ILoginService loginService = LoginService.getInstance();
                LoginRequestDto loginRequestData = ConfigurationService.getUserInfo(context);
                LoginResponseDto loggedUser = loginService.loginUser(context, loginRequestData);
                recordDetail.setLoggedUser(loggedUser);
                this.getAllRecordsHeaderTree(context, recordDetail, reloginCount + 1);
            } else {
                finalResponse = gsonSerializer.fromJson(ex.getResponseBodyAsString(), RecordHeaderResponseDto.class);
            }
            Log.e("CallingService: " + RECORD_HEADER_URL, ex.getResponseBodyAsString() + ex.getStatusText(), ex);
        } catch (Exception ex) {
            finalResponse.setErrorMessage(ex.getMessage());
            Log.e("CallingService: " + RECORD_HEADER_URL, ex.getMessage(), ex);
        }
        return finalResponse;
    }

    public RecordHeaderResponseDto getAllRecordsHeaderTable(Context context, RecordHeaderResquestDto recordDetail, int reloginCount, int position) {
        RecordHeaderResponseDto finalResponse = new RecordHeaderResponseDto();
        Gson gsonSerializer = new Gson();
        try {
            String finalUrl = ConfigurationService.getConfigurationValue(context, "URLBase");
            finalUrl += RECORD_HEADER_URL;

            RestTemplate restTemplate = new RestTemplate();
            restTemplate.getMessageConverters().add(new GsonHttpMessageConverter());

            HttpHeaders headers = new HttpHeaders();
            headers.setAccept(Arrays.asList(MediaType.APPLICATION_JSON));
            headers.setContentType(MediaType.APPLICATION_JSON);
            HttpEntity<?> entity = new HttpEntity<Object>(recordDetail, headers);
            ResponseEntity<JsonElement> response;
            response = restTemplate.exchange(new URI(finalUrl), HttpMethod.POST, entity, JsonElement.class);
            JsonElement jsonData = response.getBody();
            finalResponse = this.convertToRecordHeaderTable(jsonData, recordDetail.getSecondFieldInformation(), position);
        } catch (HttpServerErrorException ex) {
            finalResponse = gsonSerializer.fromJson(ex.getResponseBodyAsString(), RecordHeaderResponseDto.class);
            Log.e("CallingService: " + RECORD_HEADER_URL, ex.getResponseBodyAsString() + ex.getStatusText(), ex);
        } catch (HttpClientErrorException ex) {
            if (ex.getStatusCode() == HttpStatus.UNAUTHORIZED && reloginCount < 2) {
                ILoginService loginService = LoginService.getInstance();
                LoginRequestDto loginRequestData = ConfigurationService.getUserInfo(context);
                LoginResponseDto loggedUser = loginService.loginUser(context, loginRequestData);
                recordDetail.setLoggedUser(loggedUser);
                this.getAllRecordsHeaderTree(context, recordDetail, reloginCount + 1);
            } else {
                finalResponse = gsonSerializer.fromJson(ex.getResponseBodyAsString(), RecordHeaderResponseDto.class);
            }
            Log.e("CallingService: " + RECORD_HEADER_URL, ex.getResponseBodyAsString() + ex.getStatusText(), ex);
        } catch (Exception ex) {
            finalResponse.setErrorMessage(ex.getMessage());
            Log.e("CallingService: " + RECORD_HEADER_URL, ex.getMessage(), ex);
        }
        return finalResponse;
    }

    public TechnicianResponseDto getTechnician(Context context, TechnicianRequestDto headerInfo) {
        TechnicianResponseDto finalResponse = new TechnicianResponseDto();
        Gson gsonSerializer = new Gson();
        try {
            String finalUrl = ConfigurationService.getConfigurationValue(context, "URLBase");
            finalUrl += TECHNICIAN_URL;

            RestTemplate restTemplate = new RestTemplate();
            restTemplate.getMessageConverters().add(new GsonHttpMessageConverter());

            HttpHeaders headers = new HttpHeaders();
            headers.setAccept(Arrays.asList(MediaType.APPLICATION_JSON));
            headers.setContentType(MediaType.APPLICATION_JSON);
            HttpEntity<?> entity = new HttpEntity<Object>(headerInfo, headers);
            ResponseEntity<JsonElement> response;
            response = restTemplate.exchange(new URI(finalUrl), HttpMethod.POST, entity, JsonElement.class);
            JsonElement jsonData = response.getBody();
            finalResponse = this.convertToTechnicianData(jsonData);

        } catch (HttpServerErrorException ex) {
            finalResponse = gsonSerializer.fromJson(ex.getResponseBodyAsString(), TechnicianResponseDto.class);
            Log.e("CallingService: " + TECHNICIAN_URL, ex.getResponseBodyAsString() + ex.getStatusText(), ex);
        } catch (HttpClientErrorException ex) {
            finalResponse = gsonSerializer.fromJson(ex.getResponseBodyAsString(), TechnicianResponseDto.class);
            Log.e("CallingService: " + TECHNICIAN_URL, ex.getResponseBodyAsString() + ex.getStatusText(), ex);
        } catch (Exception ex) {
            finalResponse.setErrorMessage(ex.getMessage());
            Log.e("CallingService: " + TECHNICIAN_URL, ex.getMessage(), ex);
        }
        return finalResponse;
    }

    public TicketResponseDto convertToTicketData(JsonObject ticketObject, int selectedTechnician, JsonObject currentTechnician) {
        TicketResponseDto finalResult = new TicketResponseDto();
        Gson gsonSerializer = new Gson();

        if (ticketObject != null) {
            finalResult.setTicketInfo(ticketObject);
            JsonObject headerInfo = ticketObject.getAsJsonObject("headerInfo");
            JsonObject taskProgress = headerInfo.getAsJsonObject("taskProgress");
            finalResult.setCircularPercentDone(headerInfo.get("percent").getAsDouble());
            finalResult.setTicketCode(ticketObject.get("uniqueCode").getAsString());
            if (currentTechnician == null) {
                currentTechnician = headerInfo.getAsJsonObject("current_technician");
            }
            finalResult.setCurrentTechnician(currentTechnician);
            finalResult.setTechnicianSelected(selectedTechnician);

            JsonArray taskProgressFields = taskProgress.getAsJsonArray("fields");
            if (taskProgressFields != null && taskProgressFields.size() > 0) {
                for (JsonElement fields : taskProgressFields) {
                    JsonObject tempField = fields.getAsJsonObject();
                    String fieldName = tempField.get("field_name").getAsString();
                    if (CommonValidations.validateEmpty(fieldName) && fieldName.equals("PERCENT_DONE")) {
                        if (tempField.get("Value") != null && !tempField.get("Value").isJsonNull()) {
                            finalResult.setBarPercentDone(tempField.get("Value").getAsDouble());
                        } else {
                            finalResult.setBarPercentDone(null);
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
                        tempDataList.setIsMandatory(tempData.get("is_mandatory").getAsBoolean());
                        tempDataList.setFieldValue(this.getValueFromTicketField(tempData, controlType));
                        tempDataList.setFieldJsonObject(tempData);

                        if (tempData.has("son")) {
                            JsonElement sonElement = tempData.get("son");
                            tempDataList.setFieldSonData(gsonSerializer.toJson(sonElement));
                        }
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

            // Related data
            List<TicketRelatedDataDto> relatedDataList = new ArrayList<>();
            JsonArray relatedData = ticketObject.getAsJsonArray("relatedData");
            TicketRelatedDataDto tempRelatedData;
            for (int index = 0; index < relatedData.size(); index++) {
                JsonObject relatedDataItem = relatedData.get(index).getAsJsonObject();
                tempRelatedData = new TicketRelatedDataDto();
                tempRelatedData.setFieldName(relatedDataItem.get("tb_name").getAsString());
                tempRelatedData.setFieldKey(String.valueOf(index));
                tempRelatedData.setFieldSonData(relatedDataItem);
                relatedDataList.add(tempRelatedData);
            }
            finalResult.setRelatedList(relatedDataList);
        }
        return finalResult;
    }

    public ReopenResponseDto reopenTicket(Context context, ReopenRequestDto reopenInfo, int reloginCount) {
        ReopenResponseDto finalResponse = new ReopenResponseDto();
        Gson gsonSerializer = new Gson();
        try {
            String finalUrl = ConfigurationService.getConfigurationValue(context, "URLBase");
            finalUrl += REOPEN_TICKET_URL;

            RestTemplate restTemplate = new RestTemplate();
            restTemplate.getMessageConverters().add(new GsonHttpMessageConverter());

            HttpHeaders headers = new HttpHeaders();
            headers.setAccept(Arrays.asList(MediaType.APPLICATION_JSON));
            headers.setContentType(MediaType.APPLICATION_JSON);
            HttpEntity<?> entity = new HttpEntity<Object>(reopenInfo, headers);
            ResponseEntity<JsonElement> response;
            response = restTemplate.exchange(new URI(finalUrl), HttpMethod.POST, entity, JsonElement.class);
            JsonElement jsonData = response.getBody();
            finalResponse = this.convertToReopenTicket(jsonData);
        } catch (HttpServerErrorException ex) {
            finalResponse = gsonSerializer.fromJson(ex.getResponseBodyAsString(), ReopenResponseDto.class);
            Log.e("CallingService: " + REOPEN_TICKET_URL, ex.getResponseBodyAsString() + ex.getStatusText(), ex);
        } catch (HttpClientErrorException ex) {
            if (ex.getStatusCode() == HttpStatus.UNAUTHORIZED && reloginCount < 2) {
                ILoginService loginService = LoginService.getInstance();
                LoginRequestDto loginRequestData = ConfigurationService.getUserInfo(context);
                LoginResponseDto loggedUser = loginService.loginUser(context, loginRequestData);
                reopenInfo.setLoggedUser(loggedUser);
                this.reopenTicket(context, reopenInfo, reloginCount + 1);
            } else {
                finalResponse = gsonSerializer.fromJson(ex.getResponseBodyAsString(), ReopenResponseDto.class);
            }
            Log.e("CallingService: " + REOPEN_TICKET_URL, ex.getResponseBodyAsString() + ex.getStatusText(), ex);
        } catch (Exception ex) {
            finalResponse.setErrorMessage(ex.getMessage());
            Log.e("CallingService: " + REOPEN_TICKET_URL, ex.getMessage(), ex);
        }
        return finalResponse;
    }

    public TicketResponseDto reloadTicket(Context context, ReloadRequestDto reloadInfo, int reloginCount) {
        TicketResponseDto finalResponse = new TicketResponseDto();
        Gson gsonSerializer = new Gson();
        try {
            String finalUrl = ConfigurationService.getConfigurationValue(context, "URLBase");
            finalUrl += RELOAD_TICKET_URL;

            RestTemplate restTemplate = new RestTemplate();
            restTemplate.getMessageConverters().add(new GsonHttpMessageConverter());

            HttpHeaders headers = new HttpHeaders();
            headers.setAccept(Arrays.asList(MediaType.APPLICATION_JSON));
            headers.setContentType(MediaType.APPLICATION_JSON);
            HttpEntity<?> entity = new HttpEntity<Object>(reloadInfo, headers);
            ResponseEntity<JsonElement> response;
            response = restTemplate.exchange(new URI(finalUrl), HttpMethod.POST, entity, JsonElement.class);
            JsonElement jsonData = response.getBody();
            finalResponse = this.convertToTicketData(jsonData);

        } catch (HttpServerErrorException ex) {
            finalResponse = gsonSerializer.fromJson(ex.getResponseBodyAsString(), TicketResponseDto.class);
            Log.e("CallingService: " + RELOAD_TICKET_URL, ex.getResponseBodyAsString() + ex.getStatusText(), ex);
        } catch (HttpClientErrorException ex) {
            if (ex.getStatusCode() == HttpStatus.UNAUTHORIZED && reloginCount < 2) {
                ILoginService loginService = LoginService.getInstance();
                LoginRequestDto loginRequestData = ConfigurationService.getUserInfo(context);
                LoginResponseDto loggedUser = loginService.loginUser(context, loginRequestData);
                reloadInfo.setLoggedUser(loggedUser);
                this.reloadTicket(context, reloadInfo, reloginCount + 1);
            } else {
                finalResponse = gsonSerializer.fromJson(ex.getResponseBodyAsString(), TicketResponseDto.class);
            }
            Log.e("CallingService: " + RELOAD_TICKET_URL, ex.getResponseBodyAsString() + ex.getStatusText(), ex);
        } catch (Exception ex) {
            finalResponse.setErrorMessage(ex.getMessage());
            Log.e("CallingService: " + RELOAD_TICKET_URL, ex.getMessage(), ex);
        }
        return finalResponse;
    }

    public TicketResponseDto saveTicket(Context context, SaveTicketRequestDto saveInfo, int reloginCount) {
        TicketResponseDto finalResponse = new TicketResponseDto();
        Gson gsonSerializer = new Gson();
        try {
            String finalUrl = ConfigurationService.getConfigurationValue(context, "URLBase");
            finalUrl += SAVE_TICKET_URL;

            RestTemplate restTemplate = new RestTemplate();
            restTemplate.getMessageConverters().add(new GsonHttpMessageConverter());

            HttpHeaders headers = new HttpHeaders();
            headers.setAccept(Arrays.asList(MediaType.APPLICATION_JSON));
            headers.setContentType(MediaType.APPLICATION_JSON);
            HttpEntity<?> entity = new HttpEntity<Object>(saveInfo, headers);
            ResponseEntity<JsonElement> response;
            response = restTemplate.exchange(new URI(finalUrl), HttpMethod.POST, entity, JsonElement.class);
            JsonElement jsonData = response.getBody();
            finalResponse = this.convertToTicketData(jsonData);
        } catch (HttpServerErrorException ex) {
            finalResponse = gsonSerializer.fromJson(ex.getResponseBodyAsString(), TicketResponseDto.class);
            Log.e("CallingService: " + SAVE_TICKET_URL, ex.getResponseBodyAsString() + ex.getStatusText(), ex);
        } catch (HttpClientErrorException ex) {
            if (ex.getStatusCode() == HttpStatus.UNAUTHORIZED && reloginCount < 2) {
                ILoginService loginService = LoginService.getInstance();
                LoginRequestDto loginRequestData = ConfigurationService.getUserInfo(context);
                LoginResponseDto loggedUser = loginService.loginUser(context, loginRequestData);
                saveInfo.setLoggedUser(loggedUser);
                this.saveTicket(context, saveInfo, reloginCount + 1);
            } else {
                finalResponse = gsonSerializer.fromJson(ex.getResponseBodyAsString(), TicketResponseDto.class);
            }
            Log.e("CallingService: " + SAVE_TICKET_URL, ex.getResponseBodyAsString() + ex.getStatusText(), ex);
        } catch (Exception ex) {
            finalResponse.setErrorMessage(ex.getMessage());
            Log.e("CallingService: " + SAVE_TICKET_URL, ex.getMessage(), ex);
        }
        return finalResponse;
    }

    public RecordHeaderResponseDto getAllLayouts(Context context, AllLayoutRequestDto layoutRequestDto, int reloginCount) {
        RecordHeaderResponseDto finalResponse = new RecordHeaderResponseDto();
        Gson gsonSerializer = new Gson();
        try {
            String finalUrl = ConfigurationService.getConfigurationValue(context, "URLBase");
            finalUrl += ALL_LAYOUT_URL;

            RestTemplate restTemplate = new RestTemplate();
            restTemplate.getMessageConverters().add(new GsonHttpMessageConverter());

            HttpHeaders headers = new HttpHeaders();
            headers.setAccept(Arrays.asList(MediaType.APPLICATION_JSON));
            headers.setContentType(MediaType.APPLICATION_JSON);
            HttpEntity<?> entity = new HttpEntity<Object>(layoutRequestDto, headers);
            ResponseEntity<JsonElement> response;
            response = restTemplate.exchange(new URI(finalUrl), HttpMethod.POST, entity, JsonElement.class);
            JsonElement jsonData = response.getBody();
            finalResponse = this.convertToRecordHeaderTree(jsonData, "LAYOUT_NAME", "HD_INCIDENT_LAYOUT_ID");

        } catch (HttpServerErrorException ex) {
            finalResponse = gsonSerializer.fromJson(ex.getResponseBodyAsString(), RecordHeaderResponseDto.class);
            Log.e("CallingService: " + ALL_LAYOUT_URL, ex.getResponseBodyAsString() + ex.getStatusText(), ex);
        } catch (HttpClientErrorException ex) {
            if (ex.getStatusCode() == HttpStatus.UNAUTHORIZED && reloginCount < 2) {
                ILoginService loginService = LoginService.getInstance();
                LoginRequestDto loginRequestData = ConfigurationService.getUserInfo(context);
                LoginResponseDto loggedUser = loginService.loginUser(context, loginRequestData);
                layoutRequestDto.setLoggedUser(loggedUser);
                this.getAllLayouts(context, layoutRequestDto, reloginCount + 1);
            } else {
                finalResponse = gsonSerializer.fromJson(ex.getResponseBodyAsString(), RecordHeaderResponseDto.class);
            }
            Log.e("CallingService: " + ALL_LAYOUT_URL, ex.getResponseBodyAsString() + ex.getStatusText(), ex);
        } catch (Exception ex) {
            finalResponse.setErrorMessage(ex.getMessage());
            Log.e("CallingService: " + ALL_LAYOUT_URL, ex.getMessage(), ex);
        }
        return finalResponse;
    }

    public TicketResponseDto getTicketByLayout(Context context, TicketByLayoutRequestDto ticketRequestDto, int reloginCount) {
        TicketResponseDto finalResponse = new TicketResponseDto();
        Gson gsonSerializer = new Gson();
        try {
            String finalUrl = ConfigurationService.getConfigurationValue(context, "URLBase");
            finalUrl += TICKET_LAYOUT_URL;

            RestTemplate restTemplate = new RestTemplate();
            restTemplate.getMessageConverters().add(new GsonHttpMessageConverter());

            HttpHeaders headers = new HttpHeaders();
            headers.setAccept(Arrays.asList(MediaType.APPLICATION_JSON));
            headers.setContentType(MediaType.APPLICATION_JSON);
            HttpEntity<?> entity = new HttpEntity<Object>(ticketRequestDto, headers);
            ResponseEntity<JsonElement> response;
            response = restTemplate.exchange(new URI(finalUrl), HttpMethod.POST, entity, JsonElement.class);
            JsonElement jsonData = response.getBody();
            finalResponse = this.convertToTicketData(jsonData);

        } catch (HttpServerErrorException ex) {
            finalResponse = gsonSerializer.fromJson(ex.getResponseBodyAsString(), TicketResponseDto.class);
            Log.e("CallingService: " + TICKET_LAYOUT_URL, ex.getResponseBodyAsString() + ex.getStatusText(), ex);
        } catch (HttpClientErrorException ex) {
            if (ex.getStatusCode() == HttpStatus.UNAUTHORIZED && reloginCount < 2) {
                ILoginService loginService = LoginService.getInstance();
                LoginRequestDto loginRequestData = ConfigurationService.getUserInfo(context);
                LoginResponseDto loggedUser = loginService.loginUser(context, loginRequestData);
                ticketRequestDto.setLoggedUser(loggedUser);
                this.getTicketByLayout(context, ticketRequestDto, reloginCount + 1);
            } else {
                finalResponse = gsonSerializer.fromJson(ex.getResponseBodyAsString(), TicketResponseDto.class);
            }
            Log.e("CallingService: " + TICKET_LAYOUT_URL, ex.getResponseBodyAsString() + ex.getStatusText(), ex);
        } catch (Exception ex) {
            finalResponse.setErrorMessage(ex.getMessage());
            Log.e("CallingService: " + TICKET_LAYOUT_URL, ex.getMessage(), ex);
        }
        return finalResponse;
    }

    public RecordHeaderResponseDto loadWorkflow(Context context, WorkflowRequestDto workflowRequestDto) {
        RecordHeaderResponseDto finalResponse = new RecordHeaderResponseDto();
        Gson gsonSerializer = new Gson();
        try {
            String finalUrl = ConfigurationService.getConfigurationValue(context, "URLBase");
            finalUrl += LOAD_WORKFLOW_URL;

            RestTemplate restTemplate = new RestTemplate();
            restTemplate.getMessageConverters().add(new GsonHttpMessageConverter());

            HttpHeaders headers = new HttpHeaders();
            headers.setAccept(Arrays.asList(MediaType.APPLICATION_JSON));
            headers.setContentType(MediaType.APPLICATION_JSON);
            HttpEntity<?> entity = new HttpEntity<Object>(workflowRequestDto, headers);
            ResponseEntity<JsonElement> response;
            response = restTemplate.exchange(new URI(finalUrl), HttpMethod.POST, entity, JsonElement.class);
            JsonElement jsonData = response.getBody();
            finalResponse = this.convertToRecordWorkflowTable(jsonData);
        } catch (HttpServerErrorException ex) {
            finalResponse = gsonSerializer.fromJson(ex.getResponseBodyAsString(), RecordHeaderResponseDto.class);
            Log.e("CallingService: " + LOAD_WORKFLOW_URL, ex.getResponseBodyAsString() + ex.getStatusText(), ex);
        } catch (HttpClientErrorException ex) {
            finalResponse = gsonSerializer.fromJson(ex.getResponseBodyAsString(), RecordHeaderResponseDto.class);
            Log.e("CallingService: " + LOAD_WORKFLOW_URL, ex.getResponseBodyAsString() + ex.getStatusText(), ex);
        } catch (Exception ex) {
            finalResponse.setErrorMessage(ex.getMessage());
            Log.e("CallingService: " + LOAD_WORKFLOW_URL, ex.getMessage(), ex);
        }
        return finalResponse;
    }

    public TicketResponseDto getRelatedActivities(Context context, RelatedActivitiesRequestDto relatedActivitiesRequestDto, int reloginCount) {
        TicketResponseDto finalResponse = new TicketResponseDto();
        Gson gsonSerializer = new Gson();
        try {
            String finalUrl = ConfigurationService.getConfigurationValue(context, "URLBase");
            finalUrl += RELATED_ACT_URL;

            RestTemplate restTemplate = new RestTemplate();
            restTemplate.getMessageConverters().add(new GsonHttpMessageConverter());

            HttpHeaders headers = new HttpHeaders();
            headers.setAccept(Arrays.asList(MediaType.APPLICATION_JSON));
            headers.setContentType(MediaType.APPLICATION_JSON);
            HttpEntity<?> entity = new HttpEntity<Object>(relatedActivitiesRequestDto, headers);
            ResponseEntity<JsonElement> response;
            response = restTemplate.exchange(new URI(finalUrl), HttpMethod.POST, entity, JsonElement.class);
            JsonElement jsonData = response.getBody();
            finalResponse = this.convertToTicketData(jsonData);

        } catch (HttpServerErrorException ex) {
            finalResponse = gsonSerializer.fromJson(ex.getResponseBodyAsString(), TicketResponseDto.class);
            Log.e("CallingService: " + RELATED_ACT_URL, ex.getResponseBodyAsString() + ex.getStatusText(), ex);
        } catch (HttpClientErrorException ex) {
            if (ex.getStatusCode() == HttpStatus.UNAUTHORIZED && reloginCount < 2) {
                ILoginService loginService = LoginService.getInstance();
                LoginRequestDto loginRequestData = ConfigurationService.getUserInfo(context);
                LoginResponseDto loggedUser = loginService.loginUser(context, loginRequestData);
                relatedActivitiesRequestDto.setLoggedUser(loggedUser);
                finalResponse = this.getRelatedActivities(context, relatedActivitiesRequestDto, reloginCount + 1);
            } else {
                finalResponse = gsonSerializer.fromJson(ex.getResponseBodyAsString(), TicketResponseDto.class);
            }
            Log.e("CallingService: " + RELATED_ACT_URL, ex.getResponseBodyAsString() + ex.getStatusText(), ex);
        } catch (Exception ex) {
            finalResponse.setErrorMessage(ex.getMessage());
            Log.e("CallingService: " + RELATED_ACT_URL, ex.getMessage(), ex);
        }
        return finalResponse;
    }

    public DescendantResponseDto createDescendant(Context context, DescendantRequestDto descendantRequestDto, int reloginCount) {
        DescendantResponseDto finalResponse = new DescendantResponseDto();
        Gson gsonSerializer = new Gson();
        try {
            String finalUrl = ConfigurationService.getConfigurationValue(context, "URLBase");
            finalUrl += CREATE_DESCENDANT_URL;

            RestTemplate restTemplate = new RestTemplate();
            restTemplate.getMessageConverters().add(new GsonHttpMessageConverter());

            HttpHeaders headers = new HttpHeaders();
            headers.setAccept(Arrays.asList(MediaType.APPLICATION_JSON));
            headers.setContentType(MediaType.APPLICATION_JSON);
            HttpEntity<?> entity = new HttpEntity<Object>(descendantRequestDto, headers);
            ResponseEntity<JsonElement> response;
            response = restTemplate.exchange(new URI(finalUrl), HttpMethod.POST, entity, JsonElement.class);
            JsonElement jsonData = response.getBody();
            finalResponse = this.convertToDescendant(jsonData);
        } catch (HttpServerErrorException ex) {
            finalResponse = gsonSerializer.fromJson(ex.getResponseBodyAsString(), DescendantResponseDto.class);
            Log.e("CallingService: " + CREATE_DESCENDANT_URL, ex.getResponseBodyAsString() + ex.getStatusText(), ex);
        } catch (HttpClientErrorException ex) {
            if (ex.getStatusCode() == HttpStatus.UNAUTHORIZED && reloginCount < 2) {
                ILoginService loginService = LoginService.getInstance();
                LoginRequestDto loginRequestData = ConfigurationService.getUserInfo(context);
                LoginResponseDto loggedUser = loginService.loginUser(context, loginRequestData);
                descendantRequestDto.setLoggedUser(loggedUser);
                finalResponse = this.createDescendant(context, descendantRequestDto, reloginCount + 1);
            } else {
                finalResponse = gsonSerializer.fromJson(ex.getResponseBodyAsString(), DescendantResponseDto.class);
            }
            Log.e("CallingService: " + CREATE_DESCENDANT_URL, ex.getResponseBodyAsString() + ex.getStatusText(), ex);
        } catch (Exception ex) {
            finalResponse.setErrorMessage(ex.getMessage());
            Log.e("CallingService: " + CREATE_DESCENDANT_URL, ex.getMessage(), ex);
        }
        return finalResponse;
    }

    public PrintTicketResponseDto printTicket(Context context, PrintTicketRequestDto printTicketRequestDto, int reloginCount) {
        PrintTicketResponseDto finalResponse = new PrintTicketResponseDto();
        Gson gsonSerializer = new Gson();
        try {
            String finalUrl = ConfigurationService.getConfigurationValue(context, "URLBase");
            finalUrl += PRINT_TICKET_URL;

            RestTemplate restTemplate = new RestTemplate();
            restTemplate.getMessageConverters().add(new GsonHttpMessageConverter());

            HttpHeaders headers = new HttpHeaders();
            headers.setAccept(Arrays.asList(MediaType.APPLICATION_JSON));
            headers.setContentType(MediaType.APPLICATION_JSON);
            HttpEntity<?> entity = new HttpEntity<Object>(printTicketRequestDto, headers);
            ResponseEntity<JsonElement> response;
            response = restTemplate.exchange(new URI(finalUrl), HttpMethod.POST, entity, JsonElement.class);
            JsonElement jsonData = response.getBody();
            finalResponse = this.convertToPrintResult(jsonData);
        } catch (HttpServerErrorException ex) {
            finalResponse = gsonSerializer.fromJson(ex.getResponseBodyAsString(), PrintTicketResponseDto.class);
            Log.e("CallingService: " + PRINT_TICKET_URL, ex.getResponseBodyAsString() + ex.getStatusText(), ex);
        } catch (HttpClientErrorException ex) {
            if (ex.getStatusCode() == HttpStatus.UNAUTHORIZED && reloginCount < 2) {
                ILoginService loginService = LoginService.getInstance();
                LoginRequestDto loginRequestData = ConfigurationService.getUserInfo(context);
                LoginResponseDto loggedUser = loginService.loginUser(context, loginRequestData);
                printTicketRequestDto.setLoggedUser(loggedUser);
                finalResponse = this.printTicket(context, printTicketRequestDto, reloginCount + 1);
            } else {
                finalResponse = gsonSerializer.fromJson(ex.getResponseBodyAsString(), PrintTicketResponseDto.class);
            }
            Log.e("CallingService: " + PRINT_TICKET_URL, ex.getResponseBodyAsString() + ex.getStatusText(), ex);
        } catch (Exception ex) {
            finalResponse.setErrorMessage(ex.getMessage());
            Log.e("CallingService: " + PRINT_TICKET_URL, ex.getMessage(), ex);
        }
        return finalResponse;
    }

    public TicketResponseDto recalculateSLA(Context context, RecalculateSLARequestDto recalculateSLARequestDto) {
        TicketResponseDto finalResponse = new TicketResponseDto();
        Gson gsonSerializer = new Gson();
        try {
            String finalUrl = ConfigurationService.getConfigurationValue(context, "URLBase");
            finalUrl += RECALCULATE_SLA_URL;

            RestTemplate restTemplate = new RestTemplate();
            restTemplate.getMessageConverters().add(new GsonHttpMessageConverter());

            HttpHeaders headers = new HttpHeaders();
            headers.setAccept(Arrays.asList(MediaType.APPLICATION_JSON));
            headers.setContentType(MediaType.APPLICATION_JSON);
            HttpEntity<?> entity = new HttpEntity<Object>(recalculateSLARequestDto, headers);
            ResponseEntity<JsonElement> response;
            response = restTemplate.exchange(new URI(finalUrl), HttpMethod.POST, entity, JsonElement.class);
            JsonElement jsonData = response.getBody();
            finalResponse = this.convertToTicketData(jsonData);
        } catch (HttpServerErrorException ex) {
            finalResponse = gsonSerializer.fromJson(ex.getResponseBodyAsString(), TicketResponseDto.class);
            Log.e("CallingService: " + RECALCULATE_SLA_URL, ex.getResponseBodyAsString() + ex.getStatusText(), ex);
        } catch (HttpClientErrorException ex) {
            finalResponse = gsonSerializer.fromJson(ex.getResponseBodyAsString(), TicketResponseDto.class);
            Log.e("CallingService: " + RECALCULATE_SLA_URL, ex.getResponseBodyAsString() + ex.getStatusText(), ex);
        } catch (Exception ex) {
            finalResponse.setErrorMessage(ex.getMessage());
            Log.e("CallingService: " + RECALCULATE_SLA_URL, ex.getMessage(), ex);
        }
        return finalResponse;
    }

    public SaveRecordResponseDto saveRercord(Context context, SaveRecordRequestDto saveRecordRequestDto, int reloginCount) {
        SaveRecordResponseDto finalResponse = new SaveRecordResponseDto();
        Gson gsonSerializer = new Gson();
        try {
            String finalUrl = ConfigurationService.getConfigurationValue(context, "URLBase");
            finalUrl += SAVE_RECORD_URL;

            RestTemplate restTemplate = new RestTemplate();
            restTemplate.getMessageConverters().add(new GsonHttpMessageConverter(true));

            List<ClientHttpRequestInterceptor> interceptors = new ArrayList<ClientHttpRequestInterceptor>();
            interceptors.add(new LoggingRequestInterceptor());
            restTemplate.setInterceptors(interceptors);

            HttpHeaders headers = new HttpHeaders();
            headers.setAccept(Arrays.asList(MediaType.APPLICATION_JSON));
            headers.setContentType(MediaType.APPLICATION_JSON);
            HttpEntity<?> entity = new HttpEntity<Object>(saveRecordRequestDto, headers);
            ResponseEntity<JsonElement> response;
            response = restTemplate.exchange(new URI(finalUrl), HttpMethod.POST, entity, JsonElement.class);
            JsonElement jsonData = response.getBody();
            finalResponse.setRecordObject(jsonData.getAsJsonObject());
        } catch (HttpServerErrorException ex) {
            finalResponse = gsonSerializer.fromJson(ex.getResponseBodyAsString(), SaveRecordResponseDto.class);
            Log.e("CallingService: " + SAVE_RECORD_URL, ex.getResponseBodyAsString() + ex.getStatusText(), ex);
        } catch (HttpClientErrorException ex) {
            if (ex.getStatusCode() == HttpStatus.UNAUTHORIZED && reloginCount < 2) {
                ILoginService loginService = LoginService.getInstance();
                LoginRequestDto loginRequestData = ConfigurationService.getUserInfo(context);
                LoginResponseDto loggedUser = loginService.loginUser(context, loginRequestData);
                saveRecordRequestDto.setLoggedUser(loggedUser);
                finalResponse = this.saveRercord(context, saveRecordRequestDto, reloginCount + 1);
            } else {
                finalResponse = gsonSerializer.fromJson(ex.getResponseBodyAsString(), SaveRecordResponseDto.class);
            }
            Log.e("CallingService: " + SAVE_RECORD_URL, ex.getResponseBodyAsString() + ex.getStatusText(), ex);
        } catch (Exception ex) {
            finalResponse.setErrorMessage(ex.getMessage());
            Log.e("CallingService: " + SAVE_RECORD_URL, ex.getMessage(), ex);
        }
        return finalResponse;
    }

    public CleanEntityResponseDto cleanEntity(Context context, CleanEntityRequestDto cleanEntityRequestDto) {
        CleanEntityResponseDto finalResponse = new CleanEntityResponseDto();
        Gson gsonSerializer = new Gson();
        try {
            String finalUrl = ConfigurationService.getConfigurationValue(context, "URLBase");
            finalUrl += CLEAN_ENTITY_URL;

            RestTemplate restTemplate = new RestTemplate();
            restTemplate.getMessageConverters().add(new GsonHttpMessageConverter());

            HttpHeaders headers = new HttpHeaders();
            headers.setAccept(Arrays.asList(MediaType.APPLICATION_JSON));
            headers.setContentType(MediaType.APPLICATION_JSON);
            HttpEntity<?> entity = new HttpEntity<Object>(cleanEntityRequestDto, headers);
            ResponseEntity<JsonElement> response;
            response = restTemplate.exchange(new URI(finalUrl), HttpMethod.POST, entity, JsonElement.class);
            JsonElement jsonData = response.getBody();
            finalResponse.setRecordObject(jsonData.getAsJsonObject());
        } catch (HttpServerErrorException ex) {
            finalResponse = gsonSerializer.fromJson(ex.getResponseBodyAsString(), CleanEntityResponseDto.class);
            Log.e("CallingService: " + CLEAN_ENTITY_URL, ex.getResponseBodyAsString() + ex.getStatusText(), ex);
        } catch (HttpClientErrorException ex) {
            finalResponse = gsonSerializer.fromJson(ex.getResponseBodyAsString(), CleanEntityResponseDto.class);
            Log.e("CallingService: " + CLEAN_ENTITY_URL, ex.getResponseBodyAsString() + ex.getStatusText(), ex);
        } catch (Exception ex) {
            finalResponse.setErrorMessage(ex.getMessage());
            Log.e("CallingService: " + CLEAN_ENTITY_URL, ex.getMessage(), ex);
        }
        return finalResponse;
    }

    public DocumentInfoResponseDto getDocumentData(Context context, DocumentInfoRequestDto documentInfoRequestDto, int reloginCount) {
        DocumentInfoResponseDto finalResponse = new DocumentInfoResponseDto();
        Gson gsonSerializer = new Gson();
        try {
            String finalUrl = ConfigurationService.getConfigurationValue(context, "URLBase");
            finalUrl += GET_ATTACHMENT_URL;

            RestTemplate restTemplate = new RestTemplate();
            restTemplate.getMessageConverters().add(new GsonHttpMessageConverter());

            HttpHeaders headers = new HttpHeaders();
            headers.setAccept(Arrays.asList(MediaType.APPLICATION_JSON));
            headers.setContentType(MediaType.APPLICATION_JSON);
            HttpEntity<?> entity = new HttpEntity<Object>(documentInfoRequestDto, headers);
            ResponseEntity<JsonElement> response;
            response = restTemplate.exchange(new URI(finalUrl), HttpMethod.POST, entity, JsonElement.class);
            JsonElement jsonData = response.getBody();
            finalResponse.setRecordObject(jsonData.getAsJsonObject());
        } catch (HttpServerErrorException ex) {
            finalResponse = gsonSerializer.fromJson(ex.getResponseBodyAsString(), DocumentInfoResponseDto.class);
            Log.e("CallingService: " + GET_ATTACHMENT_URL, ex.getResponseBodyAsString() + ex.getStatusText(), ex);
        } catch (HttpClientErrorException ex) {
            if (ex.getStatusCode() == HttpStatus.UNAUTHORIZED && reloginCount < 2) {
                ILoginService loginService = LoginService.getInstance();
                LoginRequestDto loginRequestData = ConfigurationService.getUserInfo(context);
                LoginResponseDto loggedUser = loginService.loginUser(context, loginRequestData);
                documentInfoRequestDto.setLoggedUser(loggedUser);
                finalResponse = this.getDocumentData(context, documentInfoRequestDto, reloginCount + 1);
            } else {
                finalResponse = gsonSerializer.fromJson(ex.getResponseBodyAsString(), DocumentInfoResponseDto.class);
            }
            Log.e("CallingService: " + GET_ATTACHMENT_URL, ex.getResponseBodyAsString() + ex.getStatusText(), ex);
        } catch (Exception ex) {
            finalResponse.setErrorMessage(ex.getMessage());
            Log.e("CallingService: " + GET_ATTACHMENT_URL, ex.getMessage(), ex);
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
            String[] headerData = null;
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

                if (indexPosition == 0) {
                    String[] headerTempArray = ticketDataList.keySet().toArray(new String[0]);
                    headerTempArray = this.arrangeHeaderArray(headerTempArray);
                    finalResponse[indexPosition] = headerTempArray;
                    headerData = finalResponse[indexPosition];
                    columnCount = finalResponse[indexPosition].length;
                    indexPosition++;
                }
                this.setMissingFields(ticketDataList, headerData);

                ticketResponseData = ticketDataList.values().toArray(new String[0]);
                finalResponse[indexPosition] = ticketResponseData;
                indexPosition++;
            }
        } else {
            finalResponse = this.getEmptyData("TICKET");
        }
        return finalResponse;
    }

    private String[] arrangeHeaderArray(String[] headerTempArray) {
        String[] result = new String[headerTempArray.length];
        LinkedHashMap<String, String> hm = new LinkedHashMap<>();
        for (String headerTitle : headerTempArray) {
            hm.put(headerTitle, headerTitle);
        }

        Set set = hm.entrySet();
        Iterator i = set.iterator();
        int index = 0;
        while (i.hasNext()) {
            Map.Entry me = (Map.Entry) i.next();
            result[index] = String.valueOf(me.getKey());
            index++;
        }
        return result;
    }

    private void setMissingFields(LinkedHashMap<String, String> ticketDataListTemp, String[] headerData) {
        LinkedHashMap<String, String> hm = new LinkedHashMap<>();
        if (headerData != null && headerData.length > 0) {
            for (String headerTitle : headerData) {
                if (ticketDataListTemp.get(headerTitle) == null) {
                    hm.put(headerTitle, "");
                } else {
                    hm.put(headerTitle, ticketDataListTemp.get(headerTitle));
                }
            }

            ticketDataListTemp.clear();
            ticketDataListTemp.putAll(hm);
        }
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
        Gson gsonSerializer = new Gson();

        if (jsonElement != null) {
            JsonObject ticketObject = jsonElement.getAsJsonObject();
            finalResult.setTicketInfo(ticketObject);
            JsonObject headerInfo = ticketObject.getAsJsonObject("headerInfo");
            JsonObject currentTechnician = headerInfo.getAsJsonObject("current_technician");
            JsonObject taskProgress = headerInfo.getAsJsonObject("taskProgress");
            finalResult.setCircularPercentDone(headerInfo.get("percent").getAsDouble());
            finalResult.setTicketCode(ticketObject.get("uniqueCode").getAsString());
            finalResult.setCurrentTechnician(currentTechnician);
            finalResult.setTechnicianSelected(R.id.btn_setmanual_technician);

            JsonArray taskProgressFields = taskProgress.getAsJsonArray("fields");
            if (taskProgressFields != null && taskProgressFields.size() > 0) {
                for (JsonElement fields : taskProgressFields) {
                    JsonObject tempField = fields.getAsJsonObject();
                    String fieldName = tempField.get("field_name").getAsString();
                    if (CommonValidations.validateEmpty(fieldName) && fieldName.equals("PERCENT_DONE")) {
                        if (tempField.get("Value") != null && !tempField.get("Value").isJsonNull()) {
                            finalResult.setBarPercentDone(tempField.get("Value").getAsDouble());
                        } else {
                            finalResult.setBarPercentDone(null);
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
                        tempDataList.setIsMandatory(tempData.get("is_mandatory").getAsBoolean());
                        tempDataList.setFieldValue(this.getValueFromTicketField(tempData, controlType));
                        tempDataList.setFieldJsonObject(tempData);

                        if (tempData.has("son")) {
                            JsonElement sonElement = tempData.get("son");
                            tempDataList.setFieldSonData(gsonSerializer.toJson(sonElement));
                        }
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

            // Related data
            List<TicketRelatedDataDto> relatedDataList = new ArrayList<>();
            JsonArray relatedData = ticketObject.getAsJsonArray("relatedData");
            TicketRelatedDataDto tempRelatedData;
            for (int index = 0; index < relatedData.size(); index++) {
                JsonObject relatedDataItem = relatedData.get(index).getAsJsonObject();
                tempRelatedData = new TicketRelatedDataDto();
                tempRelatedData.setFieldName(relatedDataItem.get("tb_name").getAsString());
                tempRelatedData.setFieldKey(String.valueOf(index));
                tempRelatedData.setFieldSonData(relatedDataItem);
                relatedDataList.add(tempRelatedData);
            }
            finalResult.setRelatedList(relatedDataList);
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
            case DXControlsMultiline:{
                if(nodeData.get("Value").isJsonPrimitive() && nodeData.get("Value").getAsJsonPrimitive().isString()) {
                    finalNodeData = nodeData.get("Value");
                }
                else{
                    nodeData.addProperty("Value", "");
                }
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

    private RecordHeaderResponseDto convertToRecordHeaderTree(JsonElement jsonElement, JsonObject sonData) {
        RecordHeaderResponseDto finalResult = new RecordHeaderResponseDto();
        if (jsonElement != null && sonData != null) {
            String keyName = sonData.get("main_field").getAsString();
            String keyId = sonData.get("tb_name").getAsString() + "_ID";
            JsonArray arrayData = jsonElement.getAsJsonArray();

            finalResult.setFieldKeyId(keyId);
            finalResult.setFieldKeyName(keyName);

            Map<String, List<TreeDataDto>> finalDataList = null;
            String[][] finalDataTable = null;

            if (arrayData.size() > 0) {

                if (arrayData.get(0).getAsJsonObject().has("PART_OF")) {

                    finalDataList = new HashMap<>();
                    List<TreeDataDto> tempDataList = new ArrayList<>();
                    for (JsonElement elementData : arrayData) {
                        JsonObject tempElementData = elementData.getAsJsonObject();
                        TreeDataDto dataTemp = new TreeDataDto();
                        dataTemp.setElementName(tempElementData.get(keyName).getAsString());
                        dataTemp.setElementId(tempElementData.get(keyId).getAsString());
                        dataTemp.setParentId(tempElementData.get("PART_OF").getAsString());
                        tempDataList.add(dataTemp);
                    }

                /*Collections.sort(tempDataList, new Comparator<TreeDataDto>() {
                    @Override
                    public int compare(TreeDataDto c1, TreeDataDto c2) {
                        if (c1.getElementId() == c1.getElementId()) {
                            return 0;
                        }
                        if (c1.getElementId() == null) {
                            return -1;
                        }
                        if (c2.getElementId() == null) {
                            return 1;
                        }
                        return c1.getElementId().compareTo(c2.getElementId());
                    }
                });*/

                    for (TreeDataDto elementData : tempDataList) {
                        List<TreeDataDto> tempList = null;
                        if (finalDataList.containsKey(elementData.getParentId())) {
                            tempList = finalDataList.get(elementData.getParentId());
                            tempList.add(elementData);
                        } else {
                            tempList = new ArrayList<>();
                            tempList.add(elementData);
                            finalDataList.put(elementData.getParentId(), tempList);
                        }
                    }
                } else {
                    try {
                        int finalSize = arrayData.size() + 1;
                        finalDataTable = new String[finalSize][];
                        Gson gsonSerializer = new Gson();
                        JsonFactory factory = new JsonFactory();
                        ObjectMapper jacksonSerializer = new ObjectMapper(factory);
                        TypeReference<LinkedHashMap<String, String>> typeReference = new TypeReference<LinkedHashMap<String, String>>() {
                        };
                        String[] dataResponse;
                        String dataString;
                        LinkedHashMap<String, String> dataList;
                        LinkedHashMap<String, String> dataListTemp;
                        int indexPosition = 0;

                        for (JsonElement elementData : arrayData) {
                            dataString = gsonSerializer.toJson(elementData);
                            dataListTemp = jacksonSerializer.readValue(dataString, typeReference);
                            dataList = new LinkedHashMap<>();

                            if (dataListTemp.containsKey(keyName)) {
                                String keyNameValue = dataListTemp.get(keyName);
                                dataListTemp.remove(keyName);
                                dataList.put(keyName, keyNameValue);
                                dataList.putAll(dataListTemp);
                            }
                            dataResponse = dataList.values().toArray(new String[0]);
                            if (indexPosition == 0) {
                                finalDataTable[indexPosition] = dataList.keySet().toArray(new String[0]);
                                indexPosition++;
                            }
                            finalDataTable[indexPosition] = dataResponse;
                            indexPosition++;
                        }
                    } catch (Exception ex) {

                    }
                }
            }
            finalResult.setDataList(finalDataList);
            finalResult.setTableDataList(finalDataTable);
        }
        return finalResult;
    }

    private RecordHeaderResponseDto convertToRecordHeaderTree(JsonElement jsonElement, String mainField, String idField) {
        RecordHeaderResponseDto finalResult = new RecordHeaderResponseDto();
        if (jsonElement != null && CommonValidations.validateEmpty(mainField) && CommonValidations.validateEmpty(idField)) {
            String keyName = mainField;
            String keyId = idField;
            JsonArray arrayData = jsonElement.getAsJsonArray();

            Map<String, List<TreeDataDto>> finalDataList = new HashMap<>();

            if (arrayData.size() > 0) {

                List<TreeDataDto> tempDataList = new ArrayList<>();
                for (JsonElement elementData : arrayData) {
                    JsonObject tempElementData = elementData.getAsJsonObject();
                    TreeDataDto dataTemp = new TreeDataDto();
                    dataTemp.setElementName(tempElementData.get(keyName).getAsString());
                    dataTemp.setElementId(tempElementData.get(keyId).getAsString());
                    dataTemp.setParentId(tempElementData.get("PART_OF").getAsString());
                    tempDataList.add(dataTemp);
                }

                /*Collections.sort(tempDataList, new Comparator<TreeDataDto>() {
                    @Override
                    public int compare(TreeDataDto c1, TreeDataDto c2) {
                        if (c1.getElementId() == c1.getElementId()) {
                            return 0;
                        }
                        if (c1.getElementId() == null) {
                            return -1;
                        }
                        if (c2.getElementId() == null) {
                            return 1;
                        }
                        return c1.getElementId().compareTo(c2.getElementId());
                    }
                });*/

                for (TreeDataDto elementData : tempDataList) {
                    List<TreeDataDto> tempList = null;
                    if (finalDataList.containsKey(elementData.getParentId())) {
                        tempList = finalDataList.get(elementData.getParentId());
                        tempList.add(elementData);
                    } else {
                        tempList = new ArrayList<>();
                        tempList.add(elementData);
                        finalDataList.put(elementData.getParentId(), tempList);
                    }
                }
            }
            finalResult.setDataList(finalDataList);
        }
        return finalResult;
    }

    private RecordHeaderResponseDto convertToRecordHeaderTable(JsonElement jsonElement, JsonObject sonData) throws IOException {
        RecordHeaderResponseDto finalResult = new RecordHeaderResponseDto();
        if (jsonElement != null && jsonElement.isJsonArray() && sonData != null) {
            String keyName = sonData.get("main_field").getAsString();
            String[][] finalData = null;
            Gson gsonSerializer = new Gson();
            JsonFactory factory = new JsonFactory();
            ObjectMapper jacksonSerializer = new ObjectMapper(factory);
            TypeReference<LinkedHashMap<String, String>> typeReference = new TypeReference<LinkedHashMap<String, String>>() {
            };
            JsonArray arrayData = jsonElement.getAsJsonArray();
            String dataString;
            String[] dataResponse;
            LinkedHashMap<String, String> dataList;
            LinkedHashMap<String, String> dataListTemp;
            int finalSize = arrayData.size() + 1;
            finalData = new String[finalSize][];
            int indexPosition = 0;
            for (JsonElement itemData : arrayData) {
                dataString = gsonSerializer.toJson(itemData);
                dataListTemp = jacksonSerializer.readValue(dataString, typeReference);

                if (dataListTemp.containsKey("USER_N")) {
                    String keyNameValue = dataListTemp.get("USER_N");
                    dataListTemp.remove("USER_N");
                    dataList = new LinkedHashMap<>();
                    dataList.put("USER_N", keyNameValue);
                    dataList.putAll(dataListTemp);
                } else {
                    String keyNameValue = dataListTemp.get(keyName);
                    dataListTemp.remove(keyName);
                    dataList = new LinkedHashMap<>();
                    dataList.put(keyName, keyNameValue);
                    dataList.putAll(dataListTemp);
                }

                dataResponse = dataList.values().toArray(new String[0]);
                if (indexPosition == 0) {
                    finalData[indexPosition] = dataList.keySet().toArray(new String[0]);
                    columnCount = finalData[indexPosition].length;
                    indexPosition++;
                }
                finalData[indexPosition] = dataResponse;
                indexPosition++;
            }
            finalResult.setTableDataList(finalData);
        } else {
            finalResult.setTableDataList(this.getEmptyData(""));
        }
        return finalResult;
    }

    private RecordHeaderResponseDto convertToRecordHeaderTable(JsonElement jsonElement, JsonObject sonData, int position) throws IOException {
        RecordHeaderResponseDto finalResult = new RecordHeaderResponseDto();
        if (jsonElement != null &&
                jsonElement.isJsonArray() &&
                sonData != null &&
                jsonElement.getAsJsonArray().get(position).isJsonArray()) {
            String keyName = sonData.get("main_field").getAsString();
            String[][] finalData = null;
            Gson gsonSerializer = new Gson();
            JsonFactory factory = new JsonFactory();
            ObjectMapper jacksonSerializer = new ObjectMapper(factory);
            TypeReference<LinkedHashMap<String, String>> typeReference = new TypeReference<LinkedHashMap<String, String>>() {
            };
            JsonArray arrayData = jsonElement.getAsJsonArray().get(position).getAsJsonArray();
            String dataString;
            String[] dataResponse;
            LinkedHashMap<String, String> dataList;
            LinkedHashMap<String, String> dataListTemp;
            int finalSize = arrayData.size() + 1;
            finalData = new String[finalSize][];
            int indexPosition = 0;
            for (JsonElement itemData : arrayData) {
                dataString = gsonSerializer.toJson(itemData);
                dataListTemp = jacksonSerializer.readValue(dataString, typeReference);

                if (dataListTemp.containsKey("PS_NAME")) {
                    String keyNameValue = dataListTemp.get("PS_NAME");
                    dataListTemp.remove("PS_NAME");
                    dataList = new LinkedHashMap<>();
                    dataList.put("PS_NAME", keyNameValue);
                    dataList.putAll(dataListTemp);
                } else {
                    String keyNameValue = dataListTemp.get(keyName);
                    dataListTemp.remove(keyName);
                    dataList = new LinkedHashMap<>();
                    dataList.put(keyName, keyNameValue);
                    dataList.putAll(dataListTemp);
                }

                dataResponse = dataList.values().toArray(new String[0]);
                if (indexPosition == 0) {
                    finalData[indexPosition] = dataList.keySet().toArray(new String[0]);
                    columnCount = finalData[indexPosition].length;
                    indexPosition++;
                }
                finalData[indexPosition] = dataResponse;
                indexPosition++;
            }
            finalResult.setTableDataList(finalData);
        } else {
            finalResult.setTableDataList(this.getEmptyData(""));
        }
        return finalResult;
    }

    private RecordHeaderResponseDto convertToRecordWorkflowTable(JsonElement jsonElement) throws IOException {
        RecordHeaderResponseDto finalResult = new RecordHeaderResponseDto();
        if (jsonElement != null) {
            String keyName = "WF_RESULT_LST_ID";
            String[][] finalData = null;
            Gson gsonSerializer = new Gson();
            JsonFactory factory = new JsonFactory();
            ObjectMapper jacksonSerializer = new ObjectMapper(factory);
            TypeReference<LinkedHashMap<String, String>> typeReference = new TypeReference<LinkedHashMap<String, String>>() {
            };
            JsonArray arrayData = jsonElement.getAsJsonArray();
            String dataString;
            String[] dataResponse;
            LinkedHashMap<String, String> dataList;
            LinkedHashMap<String, String> dataListTemp;
            int finalSize = arrayData.size() + 1;
            finalData = new String[finalSize][];
            int indexPosition = 0;
            for (JsonElement itemData : arrayData) {
                dataString = gsonSerializer.toJson(itemData);
                dataListTemp = jacksonSerializer.readValue(dataString, typeReference);

                if (dataListTemp.containsKey("WF_RESULT")) {
                    String keyNameValue = dataListTemp.get("WF_RESULT");
                    dataListTemp.remove("WF_RESULT");
                    dataList = new LinkedHashMap<>();
                    dataList.put("WF_RESULT", keyNameValue);
                    dataList.putAll(dataListTemp);
                } else {
                    String keyNameValue = dataListTemp.get(keyName);
                    dataListTemp.remove(keyName);
                    dataList = new LinkedHashMap<>();
                    dataList.put(keyName, keyNameValue);
                    dataList.putAll(dataListTemp);
                }

                dataResponse = dataList.values().toArray(new String[0]);
                if (indexPosition == 0) {
                    finalData[indexPosition] = dataList.keySet().toArray(new String[0]);
                    columnCount = finalData[indexPosition].length;
                    indexPosition++;
                }
                finalData[indexPosition] = dataResponse;
                indexPosition++;
            }
            finalResult.setTableDataList(finalData);
        } else {
            finalResult.setTableDataList(this.getEmptyData(""));
        }
        return finalResult;
    }

    private TechnicianResponseDto convertToTechnicianData(JsonElement jsonElement) {
        TechnicianResponseDto finalResult = new TechnicianResponseDto();

        if (jsonElement != null) {
            finalResult.setTechnicianInfo(jsonElement.getAsJsonObject());
        }
        return finalResult;
    }

    private ReopenResponseDto convertToReopenTicket(JsonElement jsonElement) {
        ReopenResponseDto finalResult = new ReopenResponseDto();

        if (jsonElement != null) {
            finalResult.setTicketData(this.convertToTicketData(jsonElement));
        }
        return finalResult;
    }

    private DescendantResponseDto convertToDescendant(JsonElement jsonData) {
        DescendantResponseDto finalResult = new DescendantResponseDto();
        if (jsonData != null && jsonData.isJsonObject()) {
            JsonObject jsonObject = jsonData.getAsJsonObject();
            finalResult.setTicketName(jsonObject.get("ShortDescription").getAsString());
            finalResult.setTicketID(jsonObject.get("IncidentID").getAsString());
        }
        return finalResult;
    }

    private PrintTicketResponseDto convertToPrintResult(JsonElement jsonData) {
        PrintTicketResponseDto finalResult = new PrintTicketResponseDto();
        if (jsonData != null && jsonData.isJsonObject()) {
            JsonObject jsonElement = jsonData.getAsJsonObject();
            if (jsonElement.has("_buffer")) {
                finalResult.setBufferData(jsonElement.get("_buffer").getAsString());
            } else if (jsonElement.has("error")) {
                finalResult.setBufferData(jsonElement.get("error").getAsString());
            }
        }
        return finalResult;
    }
    //endregion
}
