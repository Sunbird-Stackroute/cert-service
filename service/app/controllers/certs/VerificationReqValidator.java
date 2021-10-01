package controllers.certs;

import org.apache.commons.collections.MapUtils;
import org.apache.commons.lang3.StringUtils;
import org.sunbird.incredible.processor.JsonKey;
import org.sunbird.BaseException;
import org.sunbird.message.IResponseMessage;
import org.sunbird.message.ResponseCode;
import org.sunbird.request.Request;

import java.text.MessageFormat;
import java.util.Map;

public class VerificationReqValidator {

    /**
     * This method will validate verify certificate request
     *
     * @param request
     * @throws BaseException
     */
    public void validateVerificationRequest(Request request) throws BaseException {
        Map<String, Object> certReq = (Map<String, Object>) request.getRequest().get(JsonKey.CERTIFICATE);
        validateMandatoryParamsPresent(certReq, JsonKey.CERTIFICATE);
        if (certReq.containsKey(JsonKey.DATA)) {
            validateMandatoryParamsPresent((Map) certReq.get(JsonKey.DATA), JsonKey.CERTIFICATE + "." + JsonKey.DATA);
        } else if (certReq.containsKey(JsonKey.ID)) {
            validateParamUuid((String) certReq.get(JsonKey.ID));
        } else {
            throw new BaseException("INVALID_REQUESTED_DATA",
                    MessageFormat.format(IResponseMessage.INVALID_REQUESTED_DATA, JsonKey.CERTIFICATE),
                    ResponseCode.CLIENT_ERROR.getCode());
        }
    }

    private void validateMandatoryParamsPresent(Map<String, Object> data, String key) throws BaseException {
        if (MapUtils.isEmpty(data)) {
            throw new BaseException("MANDATORY_PARAMETER_MISSING",
                    MessageFormat.format(IResponseMessage.MANDATORY_PARAMETER_MISSING, key),
                    ResponseCode.CLIENT_ERROR.getCode());
        }
    }

    private void validateParamUuid(String uuid) throws BaseException {
        if (StringUtils.isBlank(uuid)) {
            throw new BaseException("MANDATORY_PARAMETER_MISSING",
                    MessageFormat.format(IResponseMessage.MANDATORY_PARAMETER_MISSING, JsonKey.CERTIFICATE
                            + "." + JsonKey.UUID),
                    ResponseCode.CLIENT_ERROR.getCode());
        }
    }

}


