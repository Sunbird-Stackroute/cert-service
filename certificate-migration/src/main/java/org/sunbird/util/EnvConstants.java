package org.sunbird.util;

/**
 * this class has constants which is required to set as env.
 *
 */
public class EnvConstants {
    public static final String SUNBIRD_CASSANDRA_HOST = "sunbird_cassandra_host";
    public static final String SUNBIRD_CASSANDRA_KEYSPACENAME = "sunbird_cassandra_keyspace";
    public static final String SUNBIRD_CASSANDRA_PORT = "sunbird_cassandra_port";
    public static final String FAILED_CERTIFICATE_MIGRATION_RECORDS =
            "certificate_failed.txt";
    public static final String UDATE_FAILED_RECORDS = "update_cassandra_failed_record.txt";
    public static final String UDATE_SUCCESS_RECORDS = "update_cassandra_success_record.txt";
    public static final String UDATE_ES_FAILED_RECORDS = "update_es_failed_record.txt";
    public static final String UDATE_ES_SUCCESS_RECORDS = "update_es_success_record.txt";

    public static final String SUCCESS_RECORDS = "success_certificate_record.txt";

}
