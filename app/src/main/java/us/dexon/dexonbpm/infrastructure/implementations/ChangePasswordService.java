package us.dexon.dexonbpm.infrastructure.implementations;

import android.content.Context;
import android.util.Log;

import com.google.gson.Gson;

import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.json.GsonHttpMessageConverter;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;

import java.net.URI;
import java.util.Arrays;

import us.dexon.dexonbpm.infrastructure.interfaces.IChangePasswordService;
import us.dexon.dexonbpm.model.ReponseDTO.ChangePassResponseDto;
import us.dexon.dexonbpm.model.RequestDTO.ChangePassRequestDto;

/**
 * Created by Cesar Quiroz on 5/11/15.
 */
public class ChangePasswordService implements IChangePasswordService {

    //region Attributes
    /**
     * Single instance created upon class loading.
     */
    private static final ChangePasswordService fINSTANCE = new ChangePasswordService();

    private static String CHANGE_PASS_URL = "api/Logon/ChangePassword";
    //endregion

    //region Properties
    public static ChangePasswordService getInstance() {
        return fINSTANCE;
    }
    //endregion

    //region Constructor

    /**
     * Private constructor prevents construction outside this class.
     */
    private ChangePasswordService() {
    }
    //endregion

    //region Public Methods
    //endregion

    //region Private Methods
    //endregion
    @Override
    public ChangePassResponseDto changePassword(Context context, ChangePassRequestDto changeRequest) {
        ChangePassResponseDto finalResponse = new ChangePassResponseDto();
        Gson gsonSerializer = new Gson();
        try {
            String finalUrl = ConfigurationService.getConfigurationValue(context, "URLBase");
            finalUrl += CHANGE_PASS_URL;

            RestTemplate restTemplate = new RestTemplate();
            restTemplate.getMessageConverters().add(new GsonHttpMessageConverter());

            HttpHeaders headers = new HttpHeaders();
            headers.setAccept(Arrays.asList(MediaType.APPLICATION_JSON));
            headers.setContentType(MediaType.APPLICATION_JSON);
            HttpEntity<?> entity = new HttpEntity<Object>(changeRequest, headers);
            ResponseEntity<ChangePassResponseDto> response;
            response = restTemplate.exchange(new URI(finalUrl), HttpMethod.POST, entity, ChangePassResponseDto.class);
            finalResponse = response.getBody();

        } catch (HttpClientErrorException ex) {
            finalResponse = gsonSerializer.fromJson(ex.getResponseBodyAsString(), ChangePassResponseDto.class);
            Log.e("CallingService: " + CHANGE_PASS_URL, ex.getResponseBodyAsString() + ex.getStatusText(), ex);
        } catch (Exception ex) {
            finalResponse.setErrorMessage(ex.getMessage());
            Log.e("CallingService: " + CHANGE_PASS_URL, ex.getMessage(), ex);
        }
        return finalResponse;
    }
}
