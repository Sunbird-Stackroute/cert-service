package org.sunbird.cassandra;

import org.apache.http.util.TextUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.sunbird.util.JsonKeys;
import org.sunbird.Request;

public class RequestParamValidator {

    private static Logger logger =
            LoggerFactory.getLogger(RequestParamValidator.class);
    private Request requestParams;

    public RequestParamValidator(Request requestParams) {
        this.requestParams = requestParams;
    }

    /**
     * this method is used to validate the request Params (env) values...
     */
    public void validate() {
        isEnvValidated(requestParams);
        logger.info("validate : env variables verified");
    }

    /**
     * this method will only check weather no env should be null or empty
     *
     * @param params
     * @return boolean
     */
    private static boolean isInvalidEnv(String params) {
        return TextUtils.isEmpty(params);
    }

    /**
     * this method will validate the env variable. all the variables are mandatory...
     *
     * @param requestParams
     * @return
     */
    private static boolean isEnvValidated(Request requestParams) {

        if (isInvalidEnv(requestParams.getCassandraHost())) {
            throw new IllegalArgumentException(
                    String.format("Valid %s is required", JsonKeys.SUNBIRD_CASSANDRA_HOST));
        }
        if (isInvalidEnv(requestParams.getCassandraKeyspaceName())) {
            throw new IllegalArgumentException(
                    String.format("Valid %s is required", JsonKeys.SUNBIRD_CASSANDRA_KEYSPACENAME));
        }
        if (isInvalidEnv(requestParams.getCassandraPort())) {
            throw new IllegalArgumentException(
                    String.format("Valid %s is required", JsonKeys.SUNBIRD_CASSANDRA_PORT));
        }

        return true;
    }
}
