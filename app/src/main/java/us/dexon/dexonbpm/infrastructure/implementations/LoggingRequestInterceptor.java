package us.dexon.dexonbpm.infrastructure.implementations;

import android.util.Log;

import org.springframework.http.HttpRequest;
import org.springframework.http.client.ClientHttpRequestExecution;
import org.springframework.http.client.ClientHttpRequestInterceptor;
import org.springframework.http.client.ClientHttpResponse;

import java.io.IOException;

/**
 * Created by Cesar Quiroz on 12/3/15.
 */
public class LoggingRequestInterceptor implements ClientHttpRequestInterceptor {

    @Override
    public ClientHttpResponse intercept(HttpRequest request, byte[] body, ClientHttpRequestExecution execution) throws IOException {

        traceRequest(request, body);
        ClientHttpResponse response = execution.execute(request, body);
        traceResponse(response);
        return response;
    }

    private void traceRequest(HttpRequest request, byte[] body) throws IOException {
        Log.i("Logging Services", "===========================request begin================================================");

        Log.i("Logging Services", "URI : " + request.getURI());
        Log.i("Logging Services", "Method : " + request.getMethod());
        Log.i("Logging Services", "Request Body : " + new String(body, "UTF-8"));
        Log.i("Logging Services", "==========================request end================================================");
    }

    private void traceResponse(ClientHttpResponse response) throws IOException {
        /*StringBuilder inputStringBuilder = new StringBuilder();
        BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(response.getBody(), "UTF-8"));
        String line = bufferedReader.readLine();
        while (line != null) {
            inputStringBuilder.append(line);
            inputStringBuilder.append('\n');
            line = bufferedReader.readLine();
        }
        logger.debug("============================response begin==========================================");
        logger.debug("status code: " + response.getStatusCode());
        logger.debug("status text: " + response.getStatusText());
        logger.debug("Response Body : " + inputStringBuilder.toString());
        logger.debug("=======================response end=================================================");*/
    }
}
