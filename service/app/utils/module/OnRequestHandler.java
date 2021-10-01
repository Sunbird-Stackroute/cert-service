package utils.module;


import java.lang.reflect.Method;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.CompletionStage;


import org.sunbird.incredible.processor.JsonKey;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.MDC;
import play.http.ActionCreator;
import play.mvc.Action;
import play.mvc.Http;
import play.mvc.Http.Context;
import play.mvc.Result;
/**
 * This class will be called on each request.
 * any request pre-filter can be done here.
 * @author manzarul
 *
 */
public class OnRequestHandler implements ActionCreator {
    Logger logger = LoggerFactory.getLogger(OnRequestHandler.class);
    @Override
    public Action createAction(Http.Request request, Method method) {
        return new Action.Simple() {
            @Override
            public CompletionStage<Result> call(Context context) {
                Optional<String> requestIdHeader = request.getHeaders().get(JsonKey.X_REQUEST_ID);
                String reqId = requestIdHeader.orElseGet(() -> UUID.randomUUID().toString());
                MDC.clear();
                MDC.put(JsonKey.REQUEST_MESSAGE_ID, reqId);
                request.getHeaders().addHeader(JsonKey.REQUEST_MESSAGE_ID, reqId);
                CompletionStage<Result> result = null;
                logger.debug("On request method called");
                result = delegate.call(context);
                return result.thenApply(res -> res.withHeader("Access-Control-Allow-Origin", "*"));
            }
        };
    }



}