package org.sunbird.tracker;


import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.sunbird.util.EnvConstants;

import java.io.FileWriter;

public class StatusTracker {

    private static Logger logger = LoggerFactory.getLogger(StatusTracker.class);
    private static FileWriter fwUpdateCassandraSuccess;
    private static FileWriter fwUpdateCassandraFailed;
    private static FileWriter fwUpdateEsFailed;
    private static FileWriter fwUpdateEsSuccess;

    public static void startTracingRecord(String id) {
        logger.info("certID: " + id + " started...");
    }

    public static void endTracingRecord(String id) {
        logger.info("CertId: " + id + " ended...\n");
    }


    public static void logFailedRecord(String id) {
        logger.info(String.format("Record updated Failed for the certId:%s", id));
        writeUpdateFailedRecordToFile(id);
    }


    public static void logSuccessRecord(String certId, boolean isUpdated) {
        logger.info(String.format("Record updated successfully in cassandra with certId:%s isUpdated:%s", certId, isUpdated));
        writeDbSuccessRecordToFile(certId, isUpdated);
    }

    public static void logEsSyncFailedRecord(String id) {
        logger.info(String.format("Document updation Failed for certId:%s", id));
        writeEsFailedRecordToFile(id);
    }


    public static void logEsSyncSuccessRecord(String certId, boolean isUpdated) {
        logger.info(String.format("Document updated success for certId:%s isUpdated:%s", certId, isUpdated));
        writeEsSuccessRecordToFile(certId, isUpdated);
    }


    public static void logExceptionOnProcessingRecord(String certId, String msg) {
        logger.error(String.format("Error occurred in  processing certificate %s : %s", certId, msg));
    }

    public static void logTotalRecords(long count, String date1, String date2) {
        logger.info("================================ Total Records found in es between  {} : {} is {} ========================================", date1, date2, count);
    }

    public static void writeDbSuccessRecordToFile(String userId, boolean isUpdated) {
        try {
            if (fwUpdateCassandraSuccess == null) {
                fwUpdateCassandraSuccess = new FileWriter(EnvConstants.UDATE_SUCCESS_RECORDS);
            }
            fwUpdateCassandraSuccess.write(String.format("%s:%b", userId, isUpdated));
            fwUpdateCassandraSuccess.write("\n");
            fwUpdateCassandraSuccess.flush();
        } catch (Exception e) {
            logger.error(String.format("%s:%s:error occurred while writing preProcessed records to file with message %s", StatusTracker.class.getSimpleName(), "writeCassandraSuccessRecordToFile", e.getMessage()));
            System.exit(0);
        }
    }


    public static void closeFwUpdateCassandraSuccessConnection() {
        try {
            if (fwUpdateCassandraSuccess != null) {
                fwUpdateCassandraSuccess.close();
            }
        } catch (Exception e) {
            logger.error(String.format("%s error occurred while closing connection to file %s and error is %s", "writeCassandraSuccessRecordToFile", EnvConstants.UDATE_SUCCESS_RECORDS, e.getMessage()));
        }
    }


    public static void writeUpdateFailedRecordToFile(String userId) {
        try {
            if (fwUpdateCassandraFailed == null) {
                fwUpdateCassandraFailed = new FileWriter(EnvConstants.UDATE_FAILED_RECORDS);
            }
            fwUpdateCassandraFailed.write(String.format("%s", userId));
            fwUpdateCassandraFailed.write("\n");
            fwUpdateCassandraFailed.flush();
        } catch (Exception e) {
            logger.error(String.format("%s:%s:error occurred while writing cassandra success records to file with message %s", StatusTracker.class.getSimpleName(), "writeCassandraFailedRecordToFile", e.getMessage()));
            System.exit(0);
        }
    }


    public static void closeFwUpdateCassandraFailedConnection() {
        try {
            if (fwUpdateCassandraFailed != null) {
                fwUpdateCassandraFailed.close();
            }
        } catch (Exception e) {
            logger.error(String.format("%s error occurred while closing connection to file %s and error is %s", "writeCassandraFailedRecordToFile", EnvConstants.UDATE_FAILED_RECORDS, e.getMessage()));
        }
    }


    public static void writeEsSuccessRecordToFile(String userId, boolean isUpdated) {
        try {
            if (fwUpdateEsSuccess == null) {
                fwUpdateEsSuccess = new FileWriter(EnvConstants.UDATE_ES_SUCCESS_RECORDS);

            }
            fwUpdateEsSuccess.write(String.format("%s:%b", userId, isUpdated));
            fwUpdateEsSuccess.write("\n");
            fwUpdateEsSuccess.flush();
        } catch (Exception e) {
            logger.error(String.format("%s:%s:error occurred while writing es success records to file with message %s", StatusTracker.class.getSimpleName(), "writeESSuccessRecordToFile", e.getMessage()));
            System.exit(0);
        }
    }


    public static void closeFwUpdateEsSuccessConnection() {
        try {
            if (fwUpdateEsSuccess != null) {
                fwUpdateEsSuccess.close();
            }
        } catch (Exception e) {
            logger.error(String.format("%s error occurred while closing connection to file %s and error is %s", "writeEsSuccessRecordToFile", EnvConstants.UDATE_ES_SUCCESS_RECORDS, e.getMessage()));
        }
    }


    public static void writeEsFailedRecordToFile(String userId) {
        try {
            if (fwUpdateEsFailed == null) {
                fwUpdateEsFailed = new FileWriter(EnvConstants.UDATE_ES_FAILED_RECORDS);
            }
            fwUpdateEsFailed.write(String.format("%s", userId));
            fwUpdateEsFailed.write("\n");
            fwUpdateEsFailed.flush();
        } catch (Exception e) {
            logger.error(String.format("%s:%s:error occurred while writing esFailed records to file with message %s", StatusTracker.class.getSimpleName(), "writeESFailedRecordToFile", e.getMessage()));
            System.exit(0);
        }
    }


    public static void closeFwUpdateEsFailedConnection() {
        try {
            if (fwUpdateEsSuccess != null) {
                fwUpdateEsSuccess.close();
            }
        } catch (Exception e) {
            logger.error(String.format("%s error occurred while closing connection to file %s and error is %s", "writeESFailedRecordToFile", EnvConstants.UDATE_ES_FAILED_RECORDS, e.getMessage()));
        }
    }

}


