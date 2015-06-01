package us.dexon.dexonbpm.infrastructure.implementations;

import android.content.Context;
import android.util.Log;
import android.util.Pair;

import com.fasterxml.jackson.core.JsonFactory;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;

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
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.SortedMap;
import java.util.TreeMap;

import us.dexon.dexonbpm.infrastructure.interfaces.IDexonDatabaseWrapper;
import us.dexon.dexonbpm.infrastructure.interfaces.ILoginService;
import us.dexon.dexonbpm.infrastructure.interfaces.ITicketService;
import us.dexon.dexonbpm.model.ReponseDTO.LoginResponseDto;
import us.dexon.dexonbpm.model.ReponseDTO.TicketWrapperResponseDto;
import us.dexon.dexonbpm.model.ReponseDTO.TicketsResponseDto;
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

    public void getTicketInfo(Context context, TicketDetailRequestDto ticketDetail, int reloginCount) {
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
        } catch (HttpServerErrorException ex) {
            //finalResponse = gsonSerializer.fromJson(ex.getResponseBodyAsString(), TicketWrapperResponseDto.class);
            Log.e("CallingService: " + TICKET_URL, ex.getResponseBodyAsString() + ex.getStatusText(), ex);
        } catch (HttpClientErrorException ex) {
            if (ex.getStatusCode() != HttpStatus.INTERNAL_SERVER_ERROR && reloginCount < 2) {
                ILoginService loginService = LoginService.getInstance();
                LoginRequestDto loginRequestData = ConfigurationService.getUserInfo(context);
                LoginResponseDto loggedUser = loginService.loginUser(context, loginRequestData);
                ticketDetail.setLoggedUser(loggedUser);
                this.getTicketInfo(context, ticketDetail, reloginCount++);
            } else {
                //finalResponse = gsonSerializer.fromJson(ex.getResponseBodyAsString(), TicketWrapperResponseDto.class);
            }
            Log.e("CallingService: " + TICKET_URL, ex.getResponseBodyAsString() + ex.getStatusText(), ex);
        } catch (Exception ex) {
            //finalResponse.setErrorMessage(ex.getMessage());
            Log.e("CallingService: " + TICKET_URL, ex.getMessage(), ex);
        }
        //return finalResponse;
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
                } else {
                    finalResponse[indexPosition] = ticketResponseData;
                }
                indexPosition++;
            }
        }
        return finalResponse;
    }

    private String[][] getEmptyData() {
        String[][] finalResponse = new String[10][];
        String[] headerRow = {"TICKET", "", "", "", ""};
        String[] dataRow = {"", "", "", "", ""};
        for (int index = 0; index < 10; index++) {
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
    //endregion
}
