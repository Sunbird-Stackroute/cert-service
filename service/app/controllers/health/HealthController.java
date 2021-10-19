package controllers.health;

import akka.actor.ActorRef;
import controllers.BaseController;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionStage;

import controllers.RequestHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.sunbird.BaseException;
import org.sunbird.message.IResponseMessage;
import org.sunbird.message.ResponseCode;
import org.sunbird.response.Response;

import com.fasterxml.jackson.databind.JsonNode;

import play.libs.Json;
import play.mvc.Http;
import play.mvc.Result;
import play.mvc.Results;
import utils.module.SignalHandler;

import javax.inject.Inject;
import javax.inject.Named;

/**
 * This controller class will responsible to check health of the services.
 *
 * @author Anmol
 */
public class HealthController extends BaseController {
  // Service name must be "service" for the DevOps monitoring.
  Logger logger = LoggerFactory.getLogger(HealthController.class);
  private static final String service = "service";
  private static final String HEALTH_ACTOR_OPERATION_NAME = "health";

  @Inject
  @Named("health-actor")
  private ActorRef healthActorRef;

  @Inject
  SignalHandler signalHandler;

  /**
   * This action method is responsible for checking complete service and dependency Health.
   *
   * @return a CompletableFuture of success response
   */
  public CompletionStage<Result> getHealth() throws BaseException {
    try {
      handleSigTerm();
      logger.info("complete health method called.");
      CompletionStage<Result> response = handleRequest(healthActorRef, request(), null, HEALTH_ACTOR_OPERATION_NAME);
      return response;
    }  catch (Exception e) {
      return CompletableFuture.completedFuture(RequestHandler.handleFailureResponse(e,request()));
    }
  }

  /**
   * This action method is responsible to check certs-service health
   *
   * @return a CompletableFuture of success response
   */
  public CompletionStage<Result> getServiceHealth(String health, Http.Request httpRequest) throws BaseException {
    CompletableFuture<JsonNode> cf = new CompletableFuture<>();
    try {
      handleSigTerm();
      Response response = new Response();
      response.put(RESPONSE, SUCCESS);
      cf.complete(Json.toJson(response));
      return service.equalsIgnoreCase(health)
              ? cf.thenApplyAsync(Results::ok)
              : cf.thenApplyAsync(Results::badRequest);
    } catch (Exception e) {
      return CompletableFuture.completedFuture(RequestHandler.handleFailureResponse(e,request()));
    }
  }

  private void handleSigTerm() throws BaseException {
    if (signalHandler.isShuttingDown()) {
      logger.info(
              "SIGTERM is "
                      + signalHandler.isShuttingDown()
                      + ", So play server will not allow any new request.");
      throw new BaseException(IResponseMessage.SERVICE_UNAVAILABLE, IResponseMessage.SERVICE_UNAVAILABLE, ResponseCode.SERVICE_UNAVAILABLE.getCode());
    }
  }
}