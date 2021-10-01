package controllers.certs;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.CompletionStage;

import akka.actor.ActorRef;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.sunbird.es.ElasticSearchUtil;
import org.sunbird.incredible.processor.JsonKey;
import org.sunbird.cert.actor.operation.CertActorOperation;

import controllers.BaseController;
import org.sunbird.request.Request;
import play.mvc.Http;
import play.mvc.Result;
import utils.module.CertificateNumberGenerator;
import utils.module.OnRequestHandler;

import javax.inject.Inject;
import javax.inject.Named;

/**
 * This controller is responsible for certificate generation.
 * @author manzarul
 *
 */
public class CertsGenerationController  extends BaseController{


	@Inject
	@Named("certificate-generator_actor")
	private ActorRef certGenerateActorRef;

	@Inject
	@Named("certificate_verifier_actor")
	private ActorRef certVerifierActorRef;

	@Inject
	@Named("template_validate_actor")
	private ActorRef templateValidateActorRef;
	Logger logger = LoggerFactory.getLogger(OnRequestHandler.class);

	/**
	   * This method will accept request for certificate generation.
	   * it will do request validation and processing of request.
	   * @return a CompletableFuture of success response
	   */
	  public CompletionStage<Result> generateCerificate(Http.Request httpRequest) {
		CompletionStage<Result> response = handleRequest(certGenerateActorRef, httpRequest,
				request -> {
					Request req = (Request) request;
					Map<String, Object> context = new HashMap<>();
					context.put(JsonKey.VERSION, JsonKey.VERSION_1);
					Map<String, Object> certReq = (Map<String, Object>) req.getRequest().get(JsonKey.CERTIFICATE);
					String templateId = (String) certReq.get(JsonKey.HTML_TEMPLATE_ID);
					String url = (String) ((HashMap) ElasticSearchUtil.getTemplates().get()).get(templateId);
					certReq.put(JsonKey.HTML_TEMPLATE, url);
					certReq.put(JsonKey.CERTIFICATE_NUM, JsonKey.CERT_PREFIX + CertificateNumberGenerator.getUniqueIdFromTimestamp(0));
					logger.info("=============" + req);
					context.put(JsonKey.LOCATION,certReq.get(JsonKey.LOCATION));
					req.setContext(context);
					new CertValidator().validateGenerateCertRequest(req);
					return null;
					},
				CertActorOperation.GENERATE_CERTIFICATE.getOperation());
	    return response;
	  }


	public CompletionStage<Result> generateCertificateV2(Http.Request httpRequest) {
		CompletionStage<Result> response = handleRequest(certGenerateActorRef, httpRequest,
				request -> {
					Request req = (Request) request;
					Map<String, Object> context = new HashMap<>();
					context.put(JsonKey.VERSION, JsonKey.VERSION_2);
					Map<String, Object> certReq = (Map<String, Object>) req.getRequest().get(JsonKey.CERTIFICATE);
					String templateId = (String) certReq.get(JsonKey.SVG_TEMPLATE_ID);
					String url = (String) ((HashMap) ElasticSearchUtil.getTemplates().get()).get(templateId);
					certReq.put(JsonKey.SVG_TEMPLATE, url);
					certReq.put(JsonKey.CERTIFICATE_NUM, JsonKey.CERT_PREFIX + CertificateNumberGenerator.getUniqueIdFromTimestamp(0));
					logger.info("=============" + req);
					context.put(JsonKey.LOCATION,certReq.get(JsonKey.LOCATION));
					req.setContext(context);
					new CertValidator().validateGenerateCertRequest(req);
					return null;
				},
				CertActorOperation.GENERATE_CERTIFICATE_V2.getOperation());
		return response;
	}

	  public CompletionStage<Result> generateSignUrl(Http.Request httpRequest) {
			CompletionStage<Result> response = handleRequest(certGenerateActorRef, httpRequest,
					null,
					CertActorOperation.GET_SIGN_URL.getOperation());
		    return response;
		  }

    public CompletionStage<Result> verifyCerificate(Http.Request httpRequest) {
        CompletionStage<Result> response = handleRequest(certVerifierActorRef, httpRequest,
                request -> {
                    Request req = (Request) request;
                    new VerificationReqValidator().validateVerificationRequest(req);
                    return null;
                },
                CertActorOperation.VERIFY_CERT.getOperation());
        return response;
    }

    public CompletionStage<Result> validateTemplate(Http.Request httpRequest) {
		CompletionStage<Result> response =  handleRequest(templateValidateActorRef, httpRequest,
				request -> {
					Request req = (Request) request;
					new TemplateReqValidator().validateTemplateRequest(req);
					return null;
				},
                CertActorOperation.VALIDATE_TEMPLATE.getOperation());
		return response;
    }
}