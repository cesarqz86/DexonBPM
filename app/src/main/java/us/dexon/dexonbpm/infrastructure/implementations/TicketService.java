package us.dexon.dexonbpm.infrastructure.implementations;

import android.content.Context;
import android.util.Log;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.json.GsonHttpMessageConverter;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;

import java.net.URI;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import us.dexon.dexonbpm.infrastructure.interfaces.ITicketService;
import us.dexon.dexonbpm.model.ReponseDTO.TicketsResponseDto;
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

    private static String TICKET_URL = "api/Incident/GetTickets";
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
    public ArrayList<TicketsResponseDto> getTicketData(Context context, TicketsRequestDto ticketFilter) {
        ArrayList<TicketsResponseDto> finalResponse = new ArrayList<TicketsResponseDto>();
        Gson gsonSerializer = new Gson();
        try {
            String finalUrl = ConfigurationService.getConfigurationValue(context, "URLBase");
            finalUrl += TICKET_URL;

            RestTemplate restTemplate = new RestTemplate();
            restTemplate.getMessageConverters().add(new GsonHttpMessageConverter());

            HttpHeaders headers = new HttpHeaders();
            headers.setAccept(Arrays.asList(MediaType.APPLICATION_JSON));
            headers.setContentType(MediaType.APPLICATION_JSON);
            HttpEntity<?> entity = new HttpEntity<Object>(ticketFilter, headers);
            ResponseEntity<JsonElement> response;
            response = restTemplate.exchange(new URI(finalUrl), HttpMethod.POST, entity, JsonElement.class);
            JsonElement jsonData = response.getBody();
            finalResponse = this.convertJsonToTicketArray(jsonData);

        } catch (HttpClientErrorException ex) {
            //finalResponse = gsonSerializer.fromJson(ex.getResponseBodyAsString(), LoginResponseDto.class);
            Log.e("CallingService: " + TICKET_URL, ex.getResponseBodyAsString() + ex.getStatusText(), ex);
        } catch (Exception ex) {
            //finalResponse.setErrorMessage(ex.getMessage());
            Log.e("CallingService: " + TICKET_URL, ex.getMessage(), ex);
        }

        return finalResponse;
    }
    //endregion

    //region Private Methods
    private ArrayList<TicketsResponseDto> convertJsonToTicketArray(JsonElement jsonElement) {
        ArrayList<TicketsResponseDto> finalResponse = new ArrayList<TicketsResponseDto>();
        Gson gsonSerializer = new Gson();
        if (jsonElement != null && jsonElement.isJsonArray()) {
            JsonArray arrayData = jsonElement.getAsJsonArray();
            for (JsonElement ticketData : arrayData) {
                TicketsResponseDto ticketResponseData = new TicketsResponseDto();
                HashMap<String, String> ticketDataList = new HashMap<>();
                ticketDataList = gsonSerializer.fromJson(ticketData, ticketDataList.getClass());
                ticketResponseData.setTicketID(String.valueOf(ticketDataList.get("HD_INCIDENT_ID")));
                ticketDataList.remove("HD_INCIDENT_ID");
                ticketResponseData.setTicketDataList(ticketDataList);
                finalResponse.add(ticketResponseData);
            }
        }
        return finalResponse;
    }
    //endregion
}
