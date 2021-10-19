package org.sunbird;


import com.datastax.driver.core.ResultSet;

import java.io.File;
import java.io.IOException;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.mashape.unirest.http.HttpResponse;
import com.mashape.unirest.http.JsonNode;
import com.mashape.unirest.http.Unirest;
import com.mashape.unirest.http.exceptions.UnirestException;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.collections.MapUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.http.HttpStatus;
import org.elasticsearch.action.update.UpdateResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.sunbird.cassandra.CassandraHelper;
import org.sunbird.cassandra.CassandraOperation;
import org.sunbird.cassandra.factory.ConnectionFactory;
import org.sunbird.tracker.StatusTracker;
import org.sunbird.util.JsonKeys;


public class RecordProcessor extends StatusTracker {

    private static String docType = "_doc";
    private static String directory = "certificates/";
    private static int es_limit = 10000;
    private static ObjectMapper mapper = new ObjectMapper();
    public static final int initialCapacity = 50000;
    public static final String scrollTime = "2h";

    static {
        mapper.setSerializationInclusion(JsonInclude.Include.NON_NULL);
    }

    private ConnectionFactory connectionFactory;
    public CassandraOperation cassandraOperation;
    private Request request;
    private static Logger logger = LoggerFactory.getLogger(RecordProcessor.class);
    private static String certBasePath;

    /**
     * constructor for the class
     *
     * @param connectionFactory
     * @param requestParams
     */
    private RecordProcessor(ConnectionFactory connectionFactory, Request requestParams) {
        this.connectionFactory = connectionFactory;
        this.request = requestParams;
        certBasePath = requestParams.getCertificateBasePath();
        this.cassandraOperation =
                connectionFactory.getConnection(
                        requestParams.getCassandraHost(),
                        requestParams.getCassandraKeyspaceName(),
                        requestParams.getCassandraPort());
    }

    /**
     * this method should be used to get the instance of the class..
     *
     * @return
     */
    public static RecordProcessor getInstance(
            ConnectionFactory connectionFactory, Request request) {
        return new RecordProcessor(connectionFactory, request);
    }


    public String getEsSearchUri() {
//        String esApi = "http://" + request.getEsConnection() + "/";
        String esApi=String.format("http://localhost:9200/%s/_search",JsonKeys.CERT_ALIAS);
        logger.info("CertVars:getEsSearchUri:es uri formed:" + esApi);
        return esApi;
    }


    public void processCertificates(String date1, String date2) {
        List<Map<String, Object>> certificatesFromDb = getCertificatesFromEs(date1, date2);
        int[] count = new int[2];
        int[] skipCount = {0};
        // count of certificates not found in cassandra
        int[] notFound = {0};
        int[] failed = {0};
        int size = certificatesFromDb.size();
        certificatesFromDb.forEach(cert -> {
            String id = ((Map<String, String>) cert.get("_source")).get("identifier");
            try {
                startTracingRecord(id);
                Certificate certificate = getCertificateFromDb(id);
                if (certificate == null) {
                    notFound[0]++;
                } else if (StringUtils.isBlank(certificate.getJsonUrl()) && MapUtils.isNotEmpty(certificate.getData()) && StringUtils.isNotEmpty((String) certificate.getData().get(JsonKeys.PRINT_URI))) {
                    String jsonUrl = certBasePath + uploadJson(id, UrlManager.getTagId((String) certificate.getData().get(JsonKeys.id)).concat("/"), certificate.getData());
                    Map<String, Object> data = certificate.getData();
                    data.remove(JsonKeys.PRINT_URI);
                    long updatedAt = System.currentTimeMillis();
                    boolean isDataUpdated = cassandraOperation.updateRecord(CassandraHelper.getUpdateQuery(id, jsonUrl, data, new Timestamp(updatedAt)), id);
                    if (isDataUpdated) {
                        logSuccessRecord(id, isDataUpdated);
                        count[0] += 1;
                        Map<String, Object> esCert = new HashMap<String, Object>() {{
                            put(JsonKeys.DATA, data);
                            put(JsonKeys.JSON_URL, jsonUrl);
                            put(JsonKeys.UPDATED_AT, updatedAt);
                        }};
                        UpdateResponse updateResponse = ElasticSearchUtil.updateDocument(JsonKeys.CERT_ALIAS, docType, esCert, (String) cert.get("_id"));
                        if (updateResponse == null) {
                            logEsSyncFailedRecord(id);
                        } else {
                            count[1] += 1;
                            logEsSyncSuccessRecord(id, true);
                        }
                    } else {
                        failed[0]++;
                        logFailedRecord(id);
                    }
                } else {
                    skipCount[0]++;
                    logger.info("certificate is skipped {} ", id);
                }

            } catch (Exception e) {
                failed[0]++;
                logExceptionOnProcessingRecord(id, e.getMessage());

            } finally {
                endTracingRecord(id);
            }
        });
        logger.info(
                "Total records: "
                        + size
                        + " ,Total Records corrected: "
                        + (size - notFound[0] - skipCount[0])
                        + " ,Certificates Successfully updated in cassandra: "
                        + count[0]
                        + " ,Successfully synced in ES :"
                        + (count[1]));
        logger.info("Certificates Skipped : {} , certificates not found in cassandra : {}", skipCount[0], notFound[0]);
        logger.info("Certificates failed count : {} ", failed[0]);
        cassandraOperation.closeConnection();
        CloudStorage.closeConnection();
        ElasticSearchUtil.cleanESClient();
        closeFwUpdateCassandraSuccessConnection();
        closeFwUpdateCassandraFailedConnection();
        closeFwUpdateEsFailedConnection();
        closeFwUpdateEsSuccessConnection();
    }


    private List<Map<String, Object>> getCertificatesFromEs(String date1, String date2) {
        String req = String.format("{\"_source\":[\"identifier\"],\"size\":%d,\"query\":{\"bool\":{\"must\":" +
                "[{\"bool\":{\"must_not\":{\"exists\":{\"field\":\"data\"}}}},{\"bool\":{\"must_not\":{\"exists\":{\"field\":" +
                "\"jsonUrl\"}}}},{\"range\":{\"createdAt\":{\"gte\":\"%s\",\"lte\":\"%s\"}}}]}}}", es_limit, date1, date2);
        String searchUrl = getEsSearchUri() + JsonKeys.CERT_ALIAS + "/_search/?scroll=" + scrollTime;
        logger.info("search url formed {}", searchUrl);
        List<Map<String, Object>> certificates = new ArrayList<>(initialCapacity);
        HttpResponse<JsonNode> response = post(searchUrl, req);
        try {
            if (response != null && response.getStatus() == HttpStatus.SC_OK) {
                String esRes = response.getBody().getObject().toString();
                Map<String, Object> apiResp = mapper.readValue(esRes, Map.class);
                ESResponseMapper mappedResponse = new ObjectMapper().convertValue(apiResp, ESResponseMapper.class);
                String scrollId = mappedResponse.getScrollId();
                certificates.addAll(mappedResponse.content);
                int scrollCount = mappedResponse.scrollCount;
                int totalCount = scrollCount;
                logger.info("total number records found is {}", mappedResponse.count);
                logger.info("initial scroll count {}", scrollCount);
                int iteration = 1;
                while (scrollCount > 0) {
                    ESResponseMapper data = callScrollApi(scrollId);
                    certificates.addAll(data.content);
                    scrollCount = data.scrollCount;
                    totalCount = totalCount + scrollCount;
                    iteration++;
                    logger.info("after {} iteration scroll count {}", iteration, scrollCount);
                }
                logger.info("total no of times scroll api called {}", iteration);

                if (totalCount == mappedResponse.count && certificates.size() == mappedResponse.count)
                    logTotalRecords(certificates.size(), date1, date2);
            }
        } catch (IOException e) {
            logger.info("Exception While getting data from es {}", e.getMessage() + e);
        }
        return certificates;
    }

    private ESResponseMapper callScrollApi(String scrollId) {
        String req = String.format("{\"scroll\":\"1m\",\"scroll_id\":\"%s\"}", scrollId);
        String searchUrl = getEsSearchUri() + "_search/scroll";
        logger.info("scroll url formed {}", searchUrl);
        HttpResponse<JsonNode> response = post(searchUrl, req);
        ESResponseMapper mappedResponse = null;
        if (response != null && response.getStatus() == HttpStatus.SC_OK) {
            String esRes = response.getBody().getObject().toString();
            Map<String, Object> apiResp;
            try {
                apiResp = mapper.readValue(esRes, Map.class);
                mappedResponse = new ObjectMapper().convertValue(apiResp, ESResponseMapper.class);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return mappedResponse;
    }


    private String uploadJson(String fileName, String cloudPath, Map<String, Object> data) throws IOException {
        checkDirectoryExists();
        logger.info("uploading json file started");
        File file = new File(directory + fileName + ".json");
        mapper.writeValue(file, data);
        String jsonUrl = CloudStorage.uploadFile(cloudPath, file);
        boolean isDeleted = file.delete();
        logger.info("uploading json file ended {} : {}", jsonUrl, isDeleted);
        return jsonUrl;
    }

    private void checkDirectoryExists() {
        File file = new File(directory);
        if (!file.exists()) {
            logger.info("File directory does not exist." + file.getName());
            file.mkdirs();
        }
    }

    /**
     * this method is responsible to fetch the certificates data from cassandra
     *
     * @return List<Certificate>
     */
    private Certificate getCertificateFromDb(String id) {
        ResultSet resultSet = cassandraOperation.getRecord(CassandraHelper.getRecordQuery(id));
        List<Certificate> certificates = CassandraHelper.getCertificatesFromResultSet(resultSet);
        if (CollectionUtils.isNotEmpty(certificates)) {
            return certificates.get(0);
        } else {
            logger.info("Certificate Not Found in cassandra for the id : {}", id);
            return null;
        }
    }


    public HttpResponse<JsonNode> post(String url, String body) {
        HttpResponse<JsonNode> response = null;
        try {
            response = Unirest.post(url).header("Content-Type", "application/json").body(body).asJson();
        } catch (UnirestException e) {
            logger.info("Exception while hitting api {}", e.getMessage() + e);
        }
        return response;
    }


}
