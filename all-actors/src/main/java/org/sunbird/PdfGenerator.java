package org.sunbird;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.http.HeaderElement;
import org.apache.http.HeaderElementIterator;
import org.apache.http.HttpEntity;
import org.apache.http.StatusLine;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.conn.ConnectionKeepAliveStrategy;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.impl.conn.PoolingHttpClientConnectionManager;
import org.apache.http.message.BasicHeaderElementIterator;
import org.apache.http.protocol.HTTP;
import org.apache.http.util.EntityUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.sunbird.incredible.pojos.CertificateExtension;
import org.sunbird.incredible.processor.JsonKey;
import org.sunbird.incredible.processor.views.HTMLVarResolver;
import org.sunbird.incredible.processor.views.HTMLVars;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import static org.apache.commons.lang.StringUtils.capitalize;

public class PdfGenerator {
  private static PoolingHttpClientConnectionManager connectionManager =null;
  static {
    connectionManager = new PoolingHttpClientConnectionManager();
    connectionManager.setMaxTotal(200);
    connectionManager.setDefaultMaxPerRoute(150);
    connectionManager.closeIdleConnections(180, TimeUnit.SECONDS);
  }
  private static Logger logger = LoggerFactory.getLogger(PdfGenerator.class);
  private static ObjectMapper mapper = new ObjectMapper();
  private static ConnectionKeepAliveStrategy keepAliveStrategy =
    (response, context) -> {
      HeaderElementIterator it =
        new BasicHeaderElementIterator(response.headerIterator(HTTP.CONN_KEEP_ALIVE));
      while (it.hasNext()) {
        HeaderElement he = it.nextElement();
        String param = he.getName();
        String value = he.getValue();
        if (value != null && param.equalsIgnoreCase("timeout")) {
          return Long.parseLong(value) * 1000;
        }
      }
      return 180 * 1000;
    };

    private static CloseableHttpClient client = HttpClients.custom()
      .setConnectionManager(connectionManager)
      .useSystemProperties()
      .setKeepAliveStrategy(keepAliveStrategy)
      .build();

//    private static final String PRINT_SERVICE_URL = "http://print-service:5000/v1/print/pdf";

    private static final String PRINT_SERVICE_URL = "http://localhost:5000/v1/print/pdf";

    public static String generate(String htmlTemplateUrl, CertificateExtension certificateExtension , String qrImageUrl,
                                  String container, String path) throws IOException {
        long startTime = System.currentTimeMillis();
        Map<String, Object> printServiceReq = new HashMap<>();
        Map<String, Object> request = new HashMap<>();
        printServiceReq.put(JsonKey.REQUEST, request);
        request.put("context", getContext(certificateExtension, qrImageUrl));
        request.put(JsonKey.HTML_TEMPLATE, htmlTemplateUrl);
        Map<String, String> storageParams = new HashMap<>();
        storageParams.put(JsonKey.containerName,container);
        storageParams.put(JsonKey.PATH,path);
        request.put("storageParams",storageParams);
        logger.info("Print service request : ======================= :"+ request);
        String pdfUrl = callPrintService(printServiceReq);
        String [] arr = pdfUrl.split("/");
        long endTime = System.currentTimeMillis();
        logger.info("Total time taken by print service to generate PDF = "+(endTime-startTime));
        return "/"+path+arr[arr.length-1];
    }

    private static  Map<String,Object> getContext(CertificateExtension certificateExtension, String qrImageUrl) {
        Map<String,Object> context = new HashMap<>();
        HTMLVarResolver htmlVarResolver = new HTMLVarResolver(certificateExtension);
        List<String> supportedVarList = HTMLVars.get();
        Iterator<String> iterator = supportedVarList.iterator();
        while (iterator.hasNext()) {
            String macro = iterator.next().substring(1);
            try {
                Method method = htmlVarResolver.getClass().getMethod("get" + capitalize(macro));
                method.setAccessible(true);
                context.put(macro, method.invoke(htmlVarResolver));
            } catch (NoSuchMethodException | IllegalAccessException | InvocationTargetException e) {
                logger.error("exception "+ e.getMessage(),e);
            }
        }
        context.put("qrCodeImage",qrImageUrl);
        return context;
    }

    private static String callPrintService(Map<String, Object> request) throws IOException {
        HttpPost httpPost = new HttpPost(PRINT_SERVICE_URL);
        String json = mapper.writeValueAsString(request);
        json = new String(json.getBytes(), StandardCharsets.UTF_8);
        StringEntity entity = new StringEntity(json, StandardCharsets.UTF_8);
        httpPost.setEntity(entity);
        httpPost.setHeader("Accept", "application/json");
        httpPost.setHeader("Content-type", "application/json");

        CloseableHttpResponse response = client.execute(httpPost);
        String pdfUrl = generateResponse(response);
        try {
          response.close();
        } catch (Exception ex) {
          logger.error("Exception occurred while closing http response.");
        }
        return pdfUrl;
    }

    private static String generateResponse(CloseableHttpResponse httpResponse) throws IOException {
      HttpEntity httpEntity = httpResponse.getEntity();
      byte[] bytes = EntityUtils.toByteArray(httpEntity);
      StatusLine sl = httpResponse.getStatusLine();
      Map<String,Object> resMap = mapper.readValue(new String(bytes),Map.class);
      Map<String,Object> printResponse = (Map<String,Object>)resMap.get(JsonKey.RESULT);
      String pdfUrl = (String)(printResponse.get(JsonKey.PDF_URL));
      return pdfUrl;
    }
}
