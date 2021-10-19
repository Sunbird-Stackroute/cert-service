package org.sunbird;
import com.mashape.unirest.http.HttpResponse;
import com.mashape.unirest.http.JsonNode;
import com.mashape.unirest.http.Unirest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.Map;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;
import play.libs.Json;

public class CertRegistryAsyncService {
    private static Logger logger = LoggerFactory.getLogger(CertRegistryAsyncService.class);

    public static String makeAsyncPostCall(String apiToCall, String requestBody, Map<String, String> headerMap) throws IOException {
        logger.info(":makePostCall:get request to make post call for API:" + apiToCall);
        String result = "";
        Future<HttpResponse<JsonNode>> jsonResponse
                = Unirest.post(apiToCall)
                .headers(headerMap)
                .body(requestBody)
                .asJsonAsync();
        try {
            //logger.info("response from cert-registry: " + jsonResponse.get().getBody());
            //String result =
            //com.fasterxml.jackson.databind.JsonNode jsonnd = jsonResponse.get().getBody();

            com.mashape.unirest.http.JsonNode jsonnd = jsonResponse.get().getBody();
            result  = (String) jsonnd.getObject().get("interpretation");
            logger.info("result"+ result);

        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (ExecutionException e) {
            e.printStackTrace();
        }
        return result;
//        Unirest.shutdown() ;
    }
}
