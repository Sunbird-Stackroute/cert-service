package org.sunbird;

import akka.actor.UntypedAbstractActor;
import org.sunbird.incredible.processor.JsonKey;
import org.sunbird.message.IResponseMessage;
import org.sunbird.message.Localizer;
import org.sunbird.message.ResponseCode;
import org.sunbird.request.Request;
import org.sunbird.request.RequestContext;

import java.util.*;

/**
 * @author Amit Kumar
 */
public abstract class BaseActor extends UntypedAbstractActor {
    public LoggerUtil logger = new LoggerUtil(this.getClass());
    public abstract void onReceive(Request request) throws Throwable;
    protected Localizer localizer = getLocalizer();
   
    @Override
    public void onReceive(Object message) throws Throwable {
        if (message instanceof Request) {
            Request request = (Request) message;
            Map<String, Object> trace = new HashMap<>();
            if (request.getHeaders().containsKey(JsonKey.REQUEST_MESSAGE_ID)) {
                ArrayList<String> requestIds =
                        (ArrayList<String>) request.getHeaders().get(JsonKey.REQUEST_MESSAGE_ID);
                trace.put(JsonKey.REQUEST_MESSAGE_ID, requestIds.get(0));
            }
            String operation = request.getOperation();
            logger.info(request.getRequestContext(), "BaseActor:onReceive called for operation: {}", operation);
            try {
                logger.info(request.getRequestContext(), "method started : operation {}", operation);
                onReceive(request);
                logger.info(request.getRequestContext(), "method ended : operation {}", operation);
            } catch (Exception e) {
                logger.error(request.getRequestContext(), "Exception : operation {} : message : {} {} " + operation + " " + e.getMessage(), e);
                onReceiveException(request.getRequestContext(), operation, e);
            }
        } else {
            logger.info(null, " onReceive called with invalid type of request.");
        }
    }

    /**
     * this method will handle the exception
     * @param callerName
     * @param exception
     * @throws Exception
     */
    protected void onReceiveException(RequestContext requestContext, String callerName, Exception exception) throws Exception {
        logger.error(requestContext, "Exception in message processing for: " + callerName + " :: message: " + exception.getMessage(), exception);
        sender().tell(exception, self());
    }


    /**
     * this message will handle the unsupported actor operation
     * @param callerName
     */
    protected void onReceiveUnsupportedMessage(String callerName) {
        logger.info(null, callerName + ": unsupported operation");
        /**
         * TODO Need to replace null reference from getLocalized method and replace with requested local.
         */
        BaseException exception =
                new ActorServiceException.InvalidOperationName(
                        IResponseMessage.INVALID_OPERATION_NAME,
                        getLocalizedMessage(IResponseMessage.INVALID_OPERATION_NAME,null),
                        ResponseCode.CLIENT_ERROR.getCode());
        sender().tell(exception, self());
    }


    /**
     * this is method is used get message in different different locales
     * @param key
     * @param locale
     * @return
     */

    protected String getLocalizedMessage(String key, Locale locale){
        return localizer.getMessage(key, locale);
    }

    /**
     * This method will return the current timestamp.
     *
     * @return long
     */
    public long getTimeStamp() {
        return System.currentTimeMillis();
    }

    /**
     * This method we used to print the logs of starting time of methods
     *
     * @param tag
     */
    public void startTrace(String tag) {
        logger.info(null, String.format("%s:%s:started at %s", this.getClass().getSimpleName(), tag, getTimeStamp()));
    }

    /**
     * This method we used to print the logs of ending time of methods
     *
     * @param tag
     */
    public void endTrace(String tag) {
        logger.info(null, String.format("%s:%s:ended at %s", this.getClass().getSimpleName(), tag, getTimeStamp()));
    }

    public Localizer getLocalizer(){
        return Localizer.getInstance();
    }
}
