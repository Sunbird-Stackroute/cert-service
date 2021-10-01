package org.sunbird;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.sunbird.cassandra.RequestParamValidator;
import org.sunbird.cassandra.factory.CassandraConnectionFactory;
import org.sunbird.cassandra.factory.ConnectionFactory;
import org.sunbird.util.JsonKeys;

public class CertificateMigration {

    static Logger logger = LoggerFactory.getLogger(CertificateMigration.class);


    /**
     * the Code begins here...
     *
     * @param args
     * @throws Exception
     */
    public static void main(String[] args) {
        try {
            Request requestParams = prepareRequestParams();
            RequestParamValidator requestParamValidator = new RequestParamValidator(requestParams);
            requestParamValidator.validate();
            ConnectionFactory connectionFactory = new CassandraConnectionFactory();
            ElasticSearchUtil.initialiseESClient(JsonKeys.CERT_ALIAS, requestParams.getEsConnection());
            RecordProcessor recordProcessor =
                    RecordProcessor.getInstance(connectionFactory, requestParams);
            String date1 = args.length >= 2 && StringUtils.isNotBlank(args[0]) ? args[0] : "2020-08-01";
            String date2 = args.length >= 2 && StringUtils.isNotBlank(args[1]) ? args[1] : "2020-10-01";
            logger.info("date" + date1 + date2);
            recordProcessor.processCertificates(date1, date2);
        } catch (Exception ex) {
            ElasticSearchUtil.cleanESClient();
            logger.error("Error :" + ex.getMessage());
        }
    }


    /**
     * this method will prepare RequestParams object while reading constants from env
     *
     * @return RequestParams
     */
    private static Request prepareRequestParams() {
        Request requestParams = new Request();
        requestParams.setCassandraHost(System.getenv(JsonKeys.SUNBIRD_CASSANDRA_HOST));
        requestParams.setCassandraKeyspaceName(
                System.getenv(JsonKeys.SUNBIRD_CASSANDRA_KEYSPACENAME));
        requestParams.setCassandraPort(System.getenv(JsonKeys.SUNBIRD_CASSANDRA_PORT));
        requestParams.setEsConnection(System.getenv(JsonKeys.es_conn_info));
        requestParams.setCertificateBasePath(System.getenv(JsonKeys.DOMAIN_URL) + "/certs");
        logger.info("env variable got prepareRequestParams {}", requestParams.toString());
        return requestParams;
    }
}
