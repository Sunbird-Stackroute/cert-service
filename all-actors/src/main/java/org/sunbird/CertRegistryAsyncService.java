package org.sunbird;
import com.mashape.unirest.http.HttpResponse;
import com.mashape.unirest.http.JsonNode;
import com.mashape.unirest.http.Unirest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.Map;
import java.util.concurrent.Future;

public class CertRegistryAsyncService {
    private static Logger logger = LoggerFactory.getLogger(CertRegistryAsyncService.class);

    public static void makeAsyncPostCall(String apiToCall, String requestBody, Map<String, String> headerMap) throws IOException {
        logger.info(":makePostCall:get request to make post call for API:" + apiToCall);
        Future<HttpResponse<JsonNode>> jsonResponse
                = Unirest.post(apiToCall)
                .headers(headerMap)
                .body(requestBody)
                .asJsonAsync();
        logger.info("response from cert-registry: " + jsonResponse);
//        Unirest.shutdown() ;
    }
}
