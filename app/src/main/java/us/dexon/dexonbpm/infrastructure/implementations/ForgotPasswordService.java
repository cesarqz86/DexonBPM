package us.dexon.dexonbpm.infrastructure.implementations;

import android.content.Context;
import android.util.Log;

import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.json.GsonHttpMessageConverter;
import org.springframework.web.client.RestTemplate;

import java.net.URI;
import java.util.Arrays;

import us.dexon.dexonbpm.infrastructure.interfaces.IForgotPasswordService;
import us.dexon.dexonbpm.model.ReponseDTO.ForgotPassResponseDto;
import us.dexon.dexonbpm.model.RequestDTO.ForgotPassRequestDto;

/**
 * Created by Cesar Quiroz on 5/11/15.
 */
public class ForgotPasswordService implements IForgotPasswordService {

    //region Attributes
    /**
     * Single instance created upon class loading.
     */
    private static final ForgotPasswordService fINSTANCE =  new ForgotPasswordService();

    private static String FORGOT_URL = "api/Logon/ForgetPassword";
    //endregion

    //region Properties
    public static ForgotPasswordService getInstance() {
        return fINSTANCE;
    }
    //endregion

    //region Constructor
    /**
     * Private constructor prevents construction outside this class.
     */
    private ForgotPasswordService() {
    }
    //endregion

    //region Public Methods
    @Override
    public ForgotPassResponseDto restorePassword(Context context, ForgotPassRequestDto requestData) {
        ForgotPassResponseDto finalResponse = new ForgotPassResponseDto();

        try {
            String finalUrl = ConfigurationService.getConfigurationValue(context, "URLBase");
            finalUrl += FORGOT_URL;

            RestTemplate restTemplate = new RestTemplate();
            restTemplate.getMessageConverters().add(new GsonHttpMessageConverter());

            HttpHeaders headers = new HttpHeaders();
            headers.setAccept(Arrays.asList(MediaType.APPLICATION_JSON));
            headers.setContentType(MediaType.APPLICATION_JSON);
            HttpEntity<?> entity = new HttpEntity<Object>(requestData, headers);
            ResponseEntity<ForgotPassResponseDto> response;
            response = restTemplate.exchange(new URI(finalUrl), HttpMethod.POST, entity, ForgotPassResponseDto.class);
            finalResponse = response.getBody();

        } catch (Exception ex) {
            Log.e("CallingService: " + FORGOT_URL, ex.getMessage(), ex);
        }
        return  finalResponse;
    }
    //endregion

    //region Private Methods
    //endregion

}
