package us.dexon.dexonbpm.infrastructure.implementations;

import android.content.Context;
import android.util.Log;

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

import us.dexon.dexonbpm.infrastructure.interfaces.IDexonDatabaseWrapper;
import us.dexon.dexonbpm.infrastructure.interfaces.ILoginService;
import us.dexon.dexonbpm.infrastructure.interfaces.ITicketService;
import us.dexon.dexonbpm.model.ReponseDTO.LoginResponseDto;
import us.dexon.dexonbpm.model.ReponseDTO.TicketWrapperResponseDto;
import us.dexon.dexonbpm.model.ReponseDTO.TicketsResponseDto;
import us.dexon.dexonbpm.model.RequestDTO.LoginRequestDto;
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
            Log.e("CallingService: " + TICKETS_URL, ex.getResponseBodyAsString() + ex.getStatusText(), ex);
        } catch (Exception ex) {
            finalResponse.setErrorMessage(ex.getMessage());
            Log.e("CallingService: " + TICKETS_URL, ex.getMessage(), ex);
        }

        return finalResponse;
    }
    //endregion

    //region Private Methods
    private ArrayList<TicketsResponseDto> convertJsonToTicketArray(JsonElement jsonElement) throws IOException {
        ArrayList<TicketsResponseDto> finalResponse = null;
        Gson gsonSerializer = new Gson();

        JsonFactory factory = new JsonFactory();
        ObjectMapper jacksonSerializer = new ObjectMapper(factory);
        TypeReference<HashMap<String, Object>> typeReference = new TypeReference<HashMap<String, Object>>() {
        };
        if (jsonElement != null && jsonElement.isJsonArray()) {
            JsonArray arrayData = jsonElement.getAsJsonArray();
            String ticketString;
            TicketsResponseDto ticketResponseData;
            HashMap<String, Object> ticketDataList;
            finalResponse = new ArrayList<>(arrayData.size());
            for (JsonElement ticketData : arrayData) {
                ticketString =gsonSerializer.toJson(ticketData);
                ticketResponseData =new TicketsResponseDto();
                ticketDataList = jacksonSerializer.readValue(ticketString, typeReference);
                ticketResponseData.setTicketID(String.valueOf(ticketDataList.get("TICKET")));
                ticketResponseData.setIncidentID(String.valueOf(ticketDataList.get("HD_INCIDENT_ID")));
                ticketDataList.remove("TICKET");
                ticketResponseData.setTicketDataList(ticketDataList);
                finalResponse.add(ticketResponseData);
            }
        }
        return finalResponse;
    }
    //endregion
}
