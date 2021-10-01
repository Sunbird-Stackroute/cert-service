/**
 *
 */
package utils;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import org.sunbird.ActorServiceException;
import org.sunbird.BaseException;
import org.sunbird.message.IResponseMessage;
import org.sunbird.message.Localizer;
import org.sunbird.message.ResponseCode;
import play.libs.Json;

/**
 * This class will map the requested json data into custom class.
 *
 * @author Manzarul
 */
public class RequestMapper {

    /**
     * Method to map request
     *
     * @param req play mvc request
     * @param obj Class<T>
     * @exception RuntimeException
     * @return <T>
     */
    public static <T> Object mapRequest(play.mvc.Http.Request req, Class<T> obj) throws BaseException {
        //ProjectLogger.log("RequestMapper:mapRequest:Requested data:" + req, LoggerEnum.INFO.name());
        if (req == null) throw new ActorServiceException.InvalidRequestData(
                IResponseMessage.INVALID_REQUESTED_DATA,
                Localizer.getInstance().getMessage(IResponseMessage.INVALID_REQUESTED_DATA, null),
                ResponseCode.CLIENT_ERROR.getCode());
        JsonNode requestData = null;
        try {
            requestData = req.body().asJson();
            ObjectNode headerData = Json.mapper().valueToTree(req.getHeaders().toMap());
            ((ObjectNode) requestData).set("headers", headerData);
            return Json.fromJson(requestData, obj);
        } catch (Exception e) {
//            ProjectLogger.log("RequestMapper:mapRequest: " + e.getMessage(), e);
//            ProjectLogger.log("RequestMapper:mapRequest:Requested data " + requestData, LoggerEnum.INFO.name());

            throw new ActorServiceException.InvalidRequestData(
                    IResponseMessage.INVALID_REQUESTED_DATA,
                    Localizer.getInstance().getMessage(IResponseMessage.INVALID_REQUESTED_DATA, null),
                    ResponseCode.CLIENT_ERROR.getCode());
        }
    }
}

