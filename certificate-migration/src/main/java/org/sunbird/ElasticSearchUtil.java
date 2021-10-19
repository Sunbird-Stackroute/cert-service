/**
 *
 */
package org.sunbird;

import com.fasterxml.jackson.databind.ObjectMapper;

import org.apache.http.HttpHost;
import org.elasticsearch.action.ActionListener;

import org.elasticsearch.action.get.GetRequest;
import org.elasticsearch.action.get.GetResponse;
import org.elasticsearch.action.index.IndexRequest;
import org.elasticsearch.action.update.UpdateRequest;
import org.elasticsearch.action.update.UpdateResponse;
import org.elasticsearch.client.Response;
import org.elasticsearch.client.RestClient;
import org.elasticsearch.client.RestHighLevelClient;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;


public class ElasticSearchUtil {

    static {
        System.setProperty("es.set.netty.runtime.available.processors", "false");
        registerShutdownHook();
    }

    private static Map<String, RestHighLevelClient> esClient = new HashMap<String, RestHighLevelClient>();
    private static ObjectMapper mapper = new ObjectMapper();

    public static void initialiseESClient(String indexName, String connectionInfo) {
        createClient(indexName, connectionInfo);
    }

    /**
     *
     */
    private static void createClient(String indexName, String connectionInfo) {
        if (!esClient.containsKey(indexName)) {
            Map<String, Integer> hostPort = new HashMap<String, Integer>();
            for (String info : connectionInfo.split(",")) {
                hostPort.put(info.split(":")[0], Integer.valueOf(info.split(":")[1]));
            }
            List<HttpHost> httpHosts = new ArrayList<>();
            for (String host : hostPort.keySet()) {
                httpHosts.add(new HttpHost(host, hostPort.get(host)));
            }
            RestHighLevelClient client = new RestHighLevelClient(
                    RestClient.builder(httpHosts.toArray(new HttpHost[httpHosts.size()])));
            if (null != client)
                esClient.put(indexName, client);
        }
    }

    private static RestHighLevelClient getClient(String indexName) {
        return esClient.get(indexName);
    }

    public void finalize() {
        cleanESClient();
    }

    public static boolean isIndexExists(String indexName) {
        Response response;
        try {
            response = getClient(indexName).getLowLevelClient().performRequest("HEAD", "/" + indexName);
            return (200 == response.getStatusLine().getStatusCode());
        } catch (IOException e) {
            return false;
        }

    }

    public static void cleanESClient() {
        if (!esClient.isEmpty())
            for (RestHighLevelClient client : esClient.values()) {
                if (null != client)
                    try {
                        client.close();
                    } catch (IOException e) {
                    }
            }
    }


    private static void registerShutdownHook() {
        Runtime.getRuntime().addShutdownHook(new Thread() {
            @Override
            public void run() {
                try {
                    cleanESClient();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
    }

    public static UpdateResponse updateDocument(String indexName, String documentType, Map<String, Object> doc, String documentId) throws IOException {
        IndexRequest indexRequest = new IndexRequest(indexName, documentType, documentId).source(doc);
        UpdateRequest request = new UpdateRequest().index(indexName).type(documentType).id(documentId).doc(doc)
                .upsert(indexRequest);
        UpdateResponse updateResponse = getClient(indexName).update(request);
        return updateResponse;
    }


    public static CompletableFuture<Map<String, Object>> getDocument(String indexName, String documentType, String documentId) {
        CompletableFuture<Map<String, Object>> future = new CompletableFuture<>();
        getClient(indexName).getAsync(new GetRequest(indexName, documentType, documentId), new ActionListener<GetResponse>() {
            @Override
            public void onResponse(GetResponse documentFields) {
                future.complete(documentFields.getSource());
            }

            @Override
            public void onFailure(Exception e) {
                future.completeExceptionally(e);
            }
        });
        return future;
    }


}