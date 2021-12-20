package controllers.certs;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletionStage;
import java.util.concurrent.ExecutionException;

import akka.actor.ActorRef;
import com.fasterxml.jackson.databind.MappingIterator;
import com.fasterxml.jackson.dataformat.csv.CsvMapper;
import com.fasterxml.jackson.dataformat.csv.CsvSchema;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.sunbird.BaseException;
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

	//New code
	public CompletionStage<Result> bulkUpload(Http.Request httpRequest) throws IOException, BaseException, ExecutionException, InterruptedException {
		File input = new File("/Users/vijaysharma/Desktop/sunbird/result.csv");
		CsvSchema csv = CsvSchema.emptySchema().withHeader();
		CsvMapper csvMapper = new CsvMapper();
		MappingIterator<Map<String, Object>> mappingIterator =  csvMapper.reader().forType(Map.class).with(csv).readValues(input);
		List<Map<String, Object>> list = mappingIterator.readAll();

//			Map<String,Object> map = list.get(i);
		List<Map<String ,Object>> mappedData = assignData(list);

		CompletionStage<Result> response = handleBulkRequest(certGenerateActorRef, httpRequest,mappedData,
				request -> {
					Request req = (Request) request;
					Map<String, Object> context = new HashMap<>();
					context.put(JsonKey.VERSION, JsonKey.VERSION_1);
					Map<String, Object> certReq =  req.getRequest();
					String templateId = (String) certReq.get(JsonKey.HTML_TEMPLATE_ID);
					String url = (String) ((HashMap) ElasticSearchUtil.getTemplates().get()).get(templateId);
					certReq.put(JsonKey.HTML_TEMPLATE, url);
					certReq.put(JsonKey.CERTIFICATE_NUM, JsonKey.CERT_PREFIX + CertificateNumberGenerator.getUniqueIdFromTimestamp(0));
					logger.info("=============" + req);
					context.put(JsonKey.LOCATION,certReq.get(JsonKey.LOCATION));
					req.setContext(context);
					new CertValidator().validateBulkGenerateCertRequest(req);
					return null;
				},
				CertActorOperation.GENERATE_CERTIFICATE.getOperation());
		return response;


	}

	private List<Map<String,Object>> assignData(List<Map<String, Object>> map) {
		List<Map<String,Object>> newValues = new ArrayList<>();
		Map<String,Object> mapValue;
		for(int i=0;i<map.size() ; i++) {
			mapValue = map.get(i);
			//Data
			List<Object> data = new ArrayList<>();
			Map<String, Object> userdata = new HashMap<>();
			userdata.put("recipientName", mapValue.get("recipientName"));
			userdata.put("recipientEmail", mapValue.get("recipientEmail"));
			userdata.put("recipientPhone", mapValue.get("recipientPhone"));
			data.add(userdata);
			mapValue.put("data", data);
			//Issuer
			Map<String, Object> issuer = new HashMap<>();
			issuer.put("name", mapValue.get("issuername"));
			issuer.put("url", mapValue.get("issuerurl"));
			mapValue.put("issuer", issuer);

			//SignatoryList
			List<Object> signatoryList = new ArrayList<>();
			Map<String, Object> signList = new HashMap<>();
			signList.put("name", mapValue.get("signname"));
			signList.put("designation", mapValue.get("signdesignation"));
			signList.put("id", mapValue.get("signid"));
			signList.put("image", mapValue.get("signimage"));
			signatoryList.add(signList);
			mapValue.put("signatoryList", signatoryList);
			Http.RequestBody requ = new Http.RequestBody(map);

			//StoreConfig
			Map<String, Object> storeConfig = new HashMap<>();
			Map<String, Object> azure = new HashMap<>();
			storeConfig.put("type", mapValue.get("type"));
			azure.put("account", mapValue.get("account"));
			azure.put("key", mapValue.get("key"));
			azure.put("containerName", mapValue.get("containerName"));
			storeConfig.put("azure", azure);
			mapValue.put("storeConfig", storeConfig);

			newValues.add(mapValue);
		}
		return newValues;
	}
}