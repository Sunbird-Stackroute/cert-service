package controllers.certs;

import org.apache.commons.lang.StringUtils;
import org.sunbird.incredible.processor.JsonKey;
import org.sunbird.BaseException;
import org.sunbird.message.IResponseMessage;
import org.sunbird.message.ResponseCode;
import org.sunbird.request.Request;

import java.text.MessageFormat;

public class TemplateReqValidator {

    /**
     * This method will validate verify certificate request
     *
     * @param request
     * @throws BaseException
     */
    public void validateTemplateRequest(Request request) throws BaseException {
        String templateUrl = (String) request.getRequest().get(JsonKey.TEMPLATE_URL);
        if (StringUtils.isBlank(templateUrl)) {
            throw new BaseException("MANDATORY_PARAMETER_MISSING",
                    MessageFormat.format(IResponseMessage.MANDATORY_PARAMETER_MISSING, JsonKey.REQUEST + "." + JsonKey.TEMPLATE_URL),
                    ResponseCode.CLIENT_ERROR.getCode());
        }
    }
}
