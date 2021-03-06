package us.dexon.dexonbpm.infrastructure.implementations;

import android.content.Context;
import android.util.Log;

import com.google.gson.Gson;
import com.google.gson.JsonElement;

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

import us.dexon.dexonbpm.infrastructure.interfaces.IDexonDatabaseWrapper;
import us.dexon.dexonbpm.infrastructure.interfaces.ILoginService;
import us.dexon.dexonbpm.model.ReponseDTO.LoginResponseDto;
import us.dexon.dexonbpm.model.RequestDTO.LoginRequestDto;

/**
 * Created by Cesar Quiroz on 5/9/15.
 */
public final class LoginService implements ILoginService {

    //region Attributes
    /**
     * Single instance created upon class loading.
     */
    private static final LoginService fINSTANCE = new LoginService();

    private static String LOGIN_URL = "api/Logon/GetLoggedUser";
    //endregion

    //region Properties
    public static LoginService getInstance() {
        return fINSTANCE;
    }
    //endregion

    //region Constructor

    /**
     * Private constructor prevents construction outside this class.
     */
    private LoginService() {
    }
    //endregion

    //region Public Methods
    @Override
    public LoginResponseDto loginUser(Context context, LoginRequestDto loginData) {
        LoginResponseDto finalResponse = new LoginResponseDto();
        Gson gsonSerializer = new Gson();
        try {
            String finalUrl = ConfigurationService.getConfigurationValue(context, "URLBase");
            finalUrl += LOGIN_URL;

            RestTemplate restTemplate = new RestTemplate();
            restTemplate.getMessageConverters().add(new GsonHttpMessageConverter());

            HttpHeaders headers = new HttpHeaders();
            headers.setAccept(Arrays.asList(MediaType.APPLICATION_JSON));
            headers.setContentType(MediaType.APPLICATION_JSON);
            HttpEntity<?> entity = new HttpEntity<Object>(loginData, headers);
            ResponseEntity<LoginResponseDto> response;
            response = restTemplate.exchange(new URI(finalUrl), HttpMethod.POST, entity, LoginResponseDto.class);
            finalResponse = response.getBody();
            IDexonDatabaseWrapper dexonDatabase = DexonDatabaseWrapper.getInstance();
            dexonDatabase.setContext(context);
            dexonDatabase.saveLoggedUser(finalResponse);

        } catch (HttpClientErrorException ex) {
            finalResponse = gsonSerializer.fromJson(ex.getResponseBodyAsString(), LoginResponseDto.class);
            Log.e("CallingService: " + LOGIN_URL, ex.getResponseBodyAsString() + ex.getStatusText(), ex);
        } catch (Exception ex) {
            finalResponse.setErrorMessage(ex.getMessage());
            Log.e("CallingService: " + LOGIN_URL, ex.getMessage(), ex);
        }

        return finalResponse;
    }
    //endregion

    //region Private Methods
    //endregion
}
