package controllers;

import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionStage;
import javax.inject.Inject;

import akka.actor.ActorRef;

import com.fasterxml.jackson.databind.JsonNode;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.sunbird.BaseException;
import org.sunbird.RequestValidatorFunction;
import org.sunbird.message.Localizer;
import org.sunbird.request.Request;
import org.sunbird.request.RequestContext;
import org.sunbird.response.Response;

import play.libs.Json;
import play.libs.concurrent.HttpExecutionContext;
import play.libs.typedmap.TypedKey;
import play.mvc.Controller;
import play.mvc.Http;
import play.mvc.Result;
import play.mvc.Results;
import utils.RequestMapper;

/**
 * This controller we can use for writing some common method to handel api
 * request. CompletableFuture: A Future that may be explicitly completed
 * (setting its value and status), and may be used as a CompletionStage,
 * supporting dependent functions and actions that trigger upon its completion.
 * CompletionStage: A stage of a possibly asynchronous computation, that
 * performs an action or computes a value when another CompletionStage completes
 *
 * @author Anmol
 */
public class BaseController extends Controller {
	Logger logger = LoggerFactory.getLogger(BaseController.class);
	private static final String debugEnabled = "false";

	/**
	 * We injected HttpExecutionContext to decrease the response time of APIs.
	 */
	@Inject
	protected HttpExecutionContext httpExecutionContext;
	protected final static Localizer locale = Localizer.getInstance();
	public static final String RESPONSE = "Response";
	public static final String SUCCESS = "Success";


	public CompletionStage<Result> handleRequest() {
		startTrace("handelRequest");
		CompletableFuture<JsonNode> future = new CompletableFuture<>();
		Response response = new Response();
		response.put(RESPONSE, SUCCESS);
		future.complete(Json.toJson(response));
		endTrace("handelRequest");
		return future.thenApplyAsync(Results::ok, httpExecutionContext.current());
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
		logger.info("Method call started.");
	}

	/**
	 * This method we used to print the logs of ending time of methods
	 *
	 * @param tag
	 */
	public void endTrace(String tag) {
		logger.info("Method call ended.");
	}


	/**
	 * this method will take play.mv.http request and a validation function and
	 * lastly operation(Actor operation) this method is validating the request and ,
	 * it will map the request to our sunbird Request class and make a call to
	 * requestHandler which is internally calling ask to actor this method is used
	 * to handle all the request type which has requestBody
	 *
	 * @param req
	 * @param validatorFunction
	 * @param operation
	 * @return
	 */
	public CompletionStage<Result> handleRequest(ActorRef actorRef, play.mvc.Http.Request req, RequestValidatorFunction validatorFunction,
			String operation) {
		try {
			Request request = new Request();
			if (req.body() != null && req.body().asJson() != null) {
				request = (Request) RequestMapper.mapRequest(req, Request.class);
				request.setRequestContext(getRequestContext(req, operation));
			}
			if (validatorFunction != null) {
				validatorFunction.apply(request);
			}
			return new RequestHandler().handleRequest(request,actorRef,operation,req);
		} catch (BaseException ex) {
			return CompletableFuture.completedFuture(RequestHandler.handleFailureResponse(ex, req));
		} catch (Exception ex) {
			return CompletableFuture.completedFuture(RequestHandler.handleFailureResponse(ex, req));
		}

	}

	private RequestContext getRequestContext(Http.Request httpRequest, String actorOperation) {
		RequestContext requestContext = new RequestContext(httpRequest.attrs().getOptional(TypedKey.<String>create("user_id")).orElse(null),
				httpRequest.header("x-device-id").orElse(null), httpRequest.header("x-session-id").orElse(null),
				httpRequest.header("x-app-id").orElse(null), httpRequest.header("x-app-ver").orElse(null),
				httpRequest.header("x-trace-id").orElse(UUID.randomUUID().toString()),
				(httpRequest.header("x-trace-enabled").isPresent() ? httpRequest.header("x-trace-enabled").orElse(debugEnabled): debugEnabled),
				actorOperation);
		return requestContext;
	}


	/**
	 * this method is used to handle the only GET requests.
	 *
	 * @param req
	 * @param operation
	 * @return
	 */
	public CompletionStage<Result> handleRequest(ActorRef actorRef, Request req, String operation, play.mvc.Http.Request httpReq) throws Exception {
		try {
			return new RequestHandler().handleRequest(req, actorRef, operation, httpReq);
		} catch (BaseException ex) {
			return CompletableFuture.completedFuture(RequestHandler.handleFailureResponse(ex,httpReq));
		} catch (Exception ex) {
			return CompletableFuture.completedFuture(RequestHandler.handleFailureResponse(ex, httpReq));
		}

	}

	/**
	 * This method is used specifically to handel Log Apis request this will set log
	 * levels and then return the CompletionStage of Result
	 *
	 * @return
	 */
	public CompletionStage<Result> handleLogRequest() {
		startTrace("handleLogRequest");
		Response response = new Response();
		/*
		 * if (LogValidator.isLogParamsPresent(request)) { if
		 * (LogValidator.isValidLogLevelPresent((String)
		 * request.get(JsonKey.LOG_LEVEL))) {
		 * ProjectLogger.setUserOrgServiceProjectLogger( (String)
		 * request.get(JsonKey.LOG_LEVEL)); response.put(JsonKey.ERROR, false);
		 * response.put( JsonKey.MESSAGE, "Log Level successfully set to " +
		 * request.get(JsonKey.LOG_LEVEL)); } else { List<Enum> supportedLogLevelsValues
		 * = new ArrayList<>(EnumSet.allOf(LoggerEnum.class));
		 * response.put(JsonKey.ERROR, true); response.put( JsonKey.MESSAGE,
		 * "Valid Log Levels are " + Arrays.asList(supportedLogLevelsValues.toArray()));
		 * } } else { response.put(JsonKey.ERROR, true); response.put( JsonKey.MESSAGE,
		 * "Missing Mandatory Request Param " + JsonKey.LOG_LEVEL); }
		 */
		return CompletableFuture.completedFuture( RequestHandler.handleSuccessResponse(response,null));
	}
}
