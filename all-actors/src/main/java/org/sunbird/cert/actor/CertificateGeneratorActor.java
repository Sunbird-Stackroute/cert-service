package org.sunbird.cert.actor;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.commons.collections.MapUtils;
import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.BooleanUtils;
import org.apache.commons.lang3.StringUtils;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import org.sunbird.*;
import org.sunbird.cert.actor.operation.CertActorOperation;
import org.sunbird.cloud.storage.BaseStorageService;
import org.sunbird.cloud.storage.factory.StorageConfig;
import org.sunbird.cloud.storage.factory.StorageServiceFactory;
import org.sunbird.incredible.CertificateGenerator;
import org.sunbird.incredible.UrlManager;
import org.sunbird.incredible.pojos.CertificateExtension;
import org.sunbird.incredible.processor.CertModel;
import org.sunbird.incredible.processor.JsonKey;
import org.sunbird.incredible.processor.store.CertStoreFactory;
import org.sunbird.incredible.processor.store.ICertStore;
import org.sunbird.incredible.processor.store.StoreConfig;
import org.sunbird.incredible.processor.views.SvgGenerator;
import org.sunbird.message.IResponseMessage;
import org.sunbird.message.ResponseCode;
import org.sunbird.request.Request;
import org.sunbird.response.CertificateResponse;
import org.sunbird.response.CertificateResponseV1;
import org.sunbird.response.Response;
import scala.Some;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;

/**
 * This actor is responsible for certificate generation.
 *
 * @author manzarul
 */
public class CertificateGeneratorActor extends BaseActor {
    private static CertsConstant certVar = new CertsConstant();
    private static ObjectMapper mapper = new ObjectMapper();
    String directory = "conf/";
    private static final String CERT_REGISTRY_SERVICE = "http://localhost:9010";
    private static final String REGISTRY_PDF_URL = "/certs/v1/registry/add";
    private static final String REGISTRY_SVG_URL = "/certs/v2/registry/add";
    private static ObjectMapper requestMapper = new ObjectMapper();
    static Map<String, String> headerMap = new HashMap<>();
    static {
        headerMap.put("Content-Type", "application/json");
    }
    static {
        mapper.setSerializationInclusion(JsonInclude.Include.NON_NULL);
    }

    @Override
    public void onReceive(Request request) throws Throwable {
        String operation = request.getOperation();
        logger.info(request.getRequestContext(), "onReceive method call start for operation {}",operation);
        if (JsonKey.GENERATE_CERT.equalsIgnoreCase(operation)) {
            generateCertificate(request);
        } else if (CertActorOperation.GET_SIGN_URL.getOperation().equalsIgnoreCase(operation)) {
            generateSignUrl(request);
        } else if (CertActorOperation.GENERATE_CERTIFICATE_V2.getOperation().equalsIgnoreCase(operation)) {
            generateCertificateV2(request);
        }
        logger.info(request.getRequestContext(), "onReceive method call End");
    }

    private void generateSignUrl(Request request) {
      BaseStorageService storageService = null;
        try {
            logger.info(request.getRequestContext(), "generateSignUrl:generate request got : {}", request.getRequest());
            storageService = getStorageService();
            String uri = UrlManager.getContainerRelativePath((String) request.getRequest().get(JsonKey.PDF_URL));
            logger.info(request.getRequestContext(), "generateSignUrl:generate sign url method called for uri: {}", uri);
            String signUrl = storageService.getSignedURL(certVar.getCONTAINER_NAME(), uri, Some.apply(getTimeoutInSeconds()),
                    Some.apply("r"));
            logger.info(request.getRequestContext(), "generateSignUrl:signedUrl got: {}",signUrl);
            Response response = new Response();
            response.put(JsonKey.RESPONSE, JsonKey.SUCCESS);
            response.put(JsonKey.SIGNED_URL, signUrl);
            sender().tell(response, self());
        } catch (Exception e) {
            logger.error(null, "generateSignUrl: error in generating sign url {}", e);
            Response response = new Response();
            response.put(JsonKey.RESPONSE, "failure");
            response.put(JsonKey.SIGNED_URL, "");
            sender().tell(response, self());
        } finally {
          try {
            if (null != storageService) {
              storageService.closeContext();
            }
          } catch (Exception ex) {
            logger.info(request.getRequestContext(), "CertificateGeneratorActor:generateSignUrl : Exception occurred while closing connection");
          }
        }

    }


    private BaseStorageService getStorageService() {
        StorageConfig storageConfig = new StorageConfig(certVar.getCloudStorageType(), certVar.getAzureStorageKey(), certVar.getAzureStorageSecret());
        logger.info(null, "CertificateGeneratorActor:getStorageService:storage object formed: {}" ,storageConfig.toString());
        return StorageServiceFactory.getStorageService(storageConfig);
    }

    private int getTimeoutInSeconds() {
        String timeoutInSecondsStr = CertsConstant.getExpiryLink(CertsConstant.DOWNLOAD_LINK_EXPIRY_TIMEOUT);
        logger.info(null, "CertificateGeneratorActor:getTimeoutInSeconds:timeout got: ".concat(timeoutInSecondsStr));
        return Integer.parseInt(timeoutInSecondsStr);
    }

    private void generateCertificate(Request request) throws BaseException, IOException, ParseException {
        logger.info(request.getRequestContext(), "Request received====== {}", request.getRequest());
        Map<String, String> properties = populatePropertiesMap(request);
        logger.info(request.getRequestContext(), "Properties ====== {}", properties);
        String reqMarks = properties.get(JsonKey.MARKS);
        if (reqMarks != null) {
            properties.put(JsonKey.IMPLICATION, marksToImplication(reqMarks));
        } else {
            StringUtils.isNotBlank(reqMarks);
        }
        CertStoreFactory certStoreFactory = new CertStoreFactory(properties);
        StoreConfig storeParams = new StoreConfig(getStorageParamsFromRequestOrEnv((Map<String, Object>) ((Map) request.get(JsonKey.CERTIFICATE)).get(JsonKey.STORE)));
        ICertStore certStore = certStoreFactory.getCertStore(storeParams, BooleanUtils.toBoolean(properties.get(JsonKey.PREVIEW)));
        CertMapper certMapper = new CertMapper(properties);
        List<CertModel> certModelList = certMapper.toList(request.getRequest());
        CertificateGenerator certificateGenerator = new CertificateGenerator(properties,directory);
        List<Map<String, Object>> certUrlList = new ArrayList<>();
        String htmlTemplateUrl = (String) ((Map) request.get(JsonKey.CERTIFICATE)).get(JsonKey.HTML_TEMPLATE);
        for (CertModel certModel : certModelList) {
            String uuid = null;
            try {
                CertificateExtension certificateExtension = certificateGenerator.getCertificateExtension(certModel);
                uuid = certificateGenerator.getUUID(certificateExtension);
                Map<String, Object> qrMap = certificateGenerator.generateQrCode();
                String accessCode = (String) qrMap.get(JsonKey.ACCESS_CODE);
                CertificateResponse certificateResponse = null;
                String jsonData = certificateGenerator.generateCertificateJson(certificateExtension);
                String qrImageUrl = uploadQrCode((File) qrMap.get(JsonKey.QR_CODE_FILE), properties);
                String pdfLink = PdfGenerator.generate(htmlTemplateUrl, certificateExtension, qrImageUrl, getContainerName(storeParams), certStoreFactory.setCloudPath(storeParams));
                certificateResponse = new CertificateResponseV1(uuid, accessCode, certModel.getIdentifier(), convertStringToMap(jsonData), properties.get(JsonKey.BASE_PATH).concat(pdfLink));
                Map<String, Object> uploadRes = uploadJson(directory + uuid, certStore, certStoreFactory.setCloudPath(storeParams));
                certificateResponse.setJsonUrl(properties.get(JsonKey.BASE_PATH).concat((String) uploadRes.get(JsonKey.JSON_URL)));
                String apiToCall = CERT_REGISTRY_SERVICE + REGISTRY_PDF_URL;
                Map<String, Object> req = new HashMap<>();
                req.put(JsonKey.REQUEST, certificateResponse);
                String requestBody = requestMapper.writeValueAsString(req);
                CertRegistryAsyncService.makeAsyncPostCall(apiToCall, requestBody, headerMap);
                certUrlList.add(mapper.convertValue(certificateResponse, new TypeReference<Map<String, Object>>() {
                }));
            } catch (Exception ex) {
                logger.error(request.getRequestContext(),"generateCertificate:Exception Occurred while generating certificate. : {}" + ex.getMessage(), ex);
                throw new BaseException(IResponseMessage.INTERNAL_ERROR, ex.getMessage(), ResponseCode.SERVER_ERROR.getCode());
            } finally {
              try{
                certStoreFactory.cleanUp(uuid, directory);
                } catch (Exception ex) {
                  logger.error(request.getRequestContext(),"Exception occurred during resource clean", ex);
                }
            }
        }
        certStore.close();
        Response response = new Response();
        response.getResult().put("response", certUrlList);
        sender().tell(response, getSelf());
        logger.info(request.getRequestContext(), "onReceive method call End");
    }

    private void generateCertificateV2(Request request) throws BaseException, IOException, ParseException {
        logger.info(request.getRequestContext(), "generateCertificateV2 request received== {}" + request.getRequest());
        Map<String, String> properties = populatePropertiesMap(request);
        logger.info(request.getRequestContext(), "Properties ====== {}", properties);
        String reqMarks = properties.get(JsonKey.MARKS);
        if (reqMarks != null) {
            properties.put(JsonKey.IMPLICATION, marksToImplication(reqMarks));
        } else {
            StringUtils.isNotBlank(reqMarks);
        }
        CertMapper certMapper = new CertMapper(properties);
        List<CertModel> certModelList = certMapper.toList(request.getRequest());
        CertificateGenerator certificateGenerator = new CertificateGenerator(properties, directory);
        CertStoreFactory certStoreFactory = new CertStoreFactory(properties);
        StoreConfig storeParams = new StoreConfig(getStorageParamsFromRequestOrEnv((Map<String, Object>) ((Map) request.get(JsonKey.CERTIFICATE)).get(JsonKey.STORE)));
        ICertStore certStore = certStoreFactory.getCertStore(storeParams, BooleanUtils.toBoolean(properties.get(JsonKey.PREVIEW)));
        List<Map<String, Object>> certUrlList = new ArrayList<>();
        String uuid = null;
        for (CertModel certModel : certModelList) {
            try {
                CertificateExtension certificateExtension = certificateGenerator.getCertificateExtension(certModel);
                uuid = certificateGenerator.getUUID(certificateExtension);
                Map<String, Object> qrMap = certificateGenerator.generateQrCode();
                String encodedQrCode = encodeQrCode((File) qrMap.get(JsonKey.QR_CODE_FILE));
                SvgGenerator svgGenerator = new SvgGenerator((String) ((Map) request.get(JsonKey.CERTIFICATE)).get(JsonKey.SVG_TEMPLATE), directory);
                String encodedSvg = svgGenerator.generate(certificateExtension, encodedQrCode);
                certificateExtension.setPrintUri(encodedSvg);
                String jsonUrl = uploadJson(certificateExtension, uuid , certStore, certStoreFactory.setCloudPath(storeParams));
                String decodedSvg = svgGenerator.decodeData(encodedSvg);
                String svgUrl = uploadSvg(decodedSvg, uuid, certStore, certStoreFactory.setCloudPath(storeParams));
                CertificateResponse certificateResponse = new CertificateResponse(certificateGenerator.getUUID(certificateExtension), (String) qrMap.get(JsonKey.ACCESS_CODE), certModel.getIdentifier(), mapper.convertValue(certificateExtension, Map.class));
                certificateResponse.setJsonUrl(properties.get(JsonKey.BASE_PATH).concat(jsonUrl));
//                certificateResponse.setSvgUrl(properties.get(JsonKey.BASE_PATH).concat(svgUrl));
                certificateResponse.setSvgUrl(JsonKey.SVG_DOMAIN_URL.concat(svgUrl));
                String apiToCall = CERT_REGISTRY_SERVICE + REGISTRY_SVG_URL;
                Map<String, Object> req = new HashMap<>();
                req.put(JsonKey.REQUEST, certificateResponse);
                String requestBody = requestMapper.writeValueAsString(req);
                CertRegistryAsyncService.makeAsyncPostCall(apiToCall, requestBody, headerMap);
                certUrlList.add(mapper.convertValue(certificateResponse, new TypeReference<Map<String, Object>>() {
                }));
            } catch (Exception ex) {
                logger.error(request.getRequestContext(), "generateCertificateV2:Exception Occurred while generating certificate. : {}" + ex.getMessage(), ex);
                throw new BaseException(IResponseMessage.INTERNAL_ERROR, ex.getMessage(), ResponseCode.SERVER_ERROR.getCode());
            } finally {
                certStore.close();
                try{
                    certStoreFactory.cleanUp(uuid, directory);
                } catch (Exception ex) {
                    logger.error(request.getRequestContext(), "Exception occurred during resource clean", ex);
                }
            }
        }
        Response response = new Response();
        response.getResult().put("response", certUrlList);
        sender().tell(response, getSelf());
        logger.info(request.getRequestContext(), "onReceive method call End", null);
    }

    private Map<String, Object> convertStringToMap(String jsonData) {
        Map<String, Object> data = new HashMap<>();
        try {
            data = mapper.readValue(jsonData, new TypeReference<Map<String, Object>>() {});
        } catch (IOException e) {
            logger.error(null, "Exception occurred convert json String To Map {}" + e.getMessage(), e);
        }
        return data;
    }

    private String uploadQrCode(File qrCodeFile,Map<String, String> properties) throws IOException {
        CertStoreFactory certStoreFactory = new CertStoreFactory(properties);
        QRStorageParams qrStorageParams = new QRStorageParams(certVar.getCloudStorageType());
        StoreConfig storeConfig = new StoreConfig(qrStorageParams.storeParams);
        ICertStore certStore = certStoreFactory.getCertStore(storeConfig, BooleanUtils.toBoolean(properties.get(JsonKey.PREVIEW)));
        String qrImageUrl = certStore.getPublicLink(qrCodeFile, certStoreFactory.setCloudPath(storeConfig));
        certStore.close();
        logger.info(null, "QR code is created for the certificate : {} URL : {}", qrCodeFile.getName() + " " + qrImageUrl);
        return qrImageUrl;
    }

    private String encodeQrCode(File file) throws IOException {
        byte[] fileContent = FileUtils.readFileToByteArray(file);
        file.delete();
        return Base64.getEncoder().encodeToString(fileContent);
    }

    private String getContainerName (StoreConfig storeParams) {
        String type = storeParams.getType();
        if (JsonKey.AZURE.equalsIgnoreCase(type)) {
            return storeParams.getAzureStoreConfig().getContainerName();
        } else {
            return storeParams.getAwsStoreConfig().getContainerName();
        }
    }

    private Map<String, Object> uploadJson(String fileName, ICertStore certStore, String cloudPath) throws IOException {
        Map<String, Object> resMap = new HashMap<>();
        File file = FileUtils.getFile(fileName.concat(".json"));
        resMap.put(JsonKey.JSON_URL, certStore.save(file, cloudPath));
        return resMap;
    }

    private String uploadSvg(String decodedSvg, String uuid, ICertStore certStore, String cloudPath) throws IOException {
        logger.info(null, "uploadSvg: uploading svg file started {}", uuid);
        File file = new File(directory + uuid + ".svg");
        boolean ifSuccess;
        try
        {
            ifSuccess = file.createNewFile();
            if(ifSuccess)
            {
                System.out.println("file created.");
                Path path = Paths.get(file.getCanonicalPath());
                Files.write(path, Collections.singleton(decodedSvg.substring(19)));
            }
            else
            {
                System.out.println("File already exist at location: "+file.getCanonicalPath());
                Path path = Paths.get(file.getCanonicalPath());
                Files.write(path, Collections.singleton(decodedSvg.substring(19)));
            }

        }catch (IOException e)
        {
            e.printStackTrace();
        }
        return certStore.save(file, cloudPath);
    }

    private String uploadJson(CertificateExtension certificateExtension, String uuid, ICertStore certStore, String cloudPath) throws IOException {
        logger.info(null, "uploadJson: uploading json file started {}", uuid);
        File file = new File(directory + uuid + ".json");
        mapper.writeValue(file, certificateExtension);
        return certStore.save(file, cloudPath);
    }

    private HashMap<String, String> populatePropertiesMap(Request request) {
        HashMap<String, String> properties = new HashMap<>();
        String tag = (String) ((Map) request.get(JsonKey.CERTIFICATE)).get(JsonKey.TAG);
        String preview = (String) ((Map<String, Object>) request.getRequest().get(JsonKey.CERTIFICATE)).get(JsonKey.PREVIEW);
        Map<String, Object> keysObject = (Map<String, Object>) ((Map) request.get(JsonKey.CERTIFICATE)).get(JsonKey.KEYS);
        certVar.setBasePath((String) ((Map<String, Object>) request.getRequest().get(JsonKey.CERTIFICATE))
                .get(JsonKey.BASE_PATH));
        if (MapUtils.isNotEmpty(keysObject)) {
            String keyId = (String) keysObject.get(JsonKey.ID);
            properties.put(JsonKey.KEY_ID, keyId);
            properties.put(JsonKey.SIGN_CREATOR, certVar.getSignCreator(keyId));
            properties.put(JsonKey.PUBLIC_KEY_URL, certVar.getPUBLIC_KEY_URL(keyId));
            logger.info(request.getRequestContext(), "populatePropertiesMap: keys after {}", keyId);
        }
        properties.put(JsonKey.TAG, tag);
        properties.put(JsonKey.LOCATION,(String) ((Map) request.get(JsonKey.CERTIFICATE)).get(JsonKey.LOCATION));
        properties.put(JsonKey.STUDENT_REG_NUM,(String) ((Map) request.get(JsonKey.CERTIFICATE)).get(JsonKey.STUDENT_REG_NUM));
        properties.put(JsonKey.CERTIFICATE_NUM,(String) ((Map) request.get(JsonKey.CERTIFICATE)).get(JsonKey.CERTIFICATE_NUM));
        properties.put(JsonKey.MARKS,(String) ((Map) request.get(JsonKey.CERTIFICATE)).get(JsonKey.MARKS));
        properties.put(JsonKey.CONTAINER_NAME, certVar.getCONTAINER_NAME());
        properties.put(JsonKey.BADGE_URL, certVar.getBADGE_URL(tag));
        properties.put(JsonKey.ISSUER_URL, certVar.getISSUER_URL());
        properties.put(JsonKey.EVIDENCE_URL, certVar.getEVIDENCE_URL());
        properties.put(JsonKey.CONTEXT, certVar.getCONTEXT());
        properties.put(JsonKey.VERIFICATION_TYPE, certVar.getVERIFICATION_TYPE());
        properties.put(JsonKey.ACCESS_CODE_LENGTH, certVar.getACCESS_CODE_LENGTH());
        properties.put(JsonKey.SIGN_URL, certVar.getEncSignUrl());
        properties.put(JsonKey.SIGN_VERIFY_URL, certVar.getEncSignVerifyUrl());
        properties.put(JsonKey.ENC_SERVICE_URL, certVar.getEncryptionServiceUrl());
        properties.put(JsonKey.SIGNATORY_EXTENSION, certVar.getSignatoryExtensionUrl());
        properties.put(JsonKey.SLUG, certVar.getSlug());
        properties.put(JsonKey.PREVIEW, certVar.getPreview(preview));
        properties.put(JsonKey.BASE_PATH, certVar.getBasePath());
        properties.put(JsonKey.RECIPIENT_EMAIl,(String) ((Map) request.get(JsonKey.CERTIFICATE)).get(JsonKey.RECIPIENT_EMAIl));
        logger.info(request.getRequestContext(), "getProperties:properties got from Constant File ".concat(Collections.singleton(properties.toString()) + ""));
        return properties;
    }

    private Map<String, Object> getStorageParamsFromRequestOrEnv(Map<String, Object> storeParams) {
        logger.info(null , "============Store Params======="+ storeParams);
        if (MapUtils.isNotEmpty(storeParams)) {
            return storeParams;
        } else {
            return certVar.getStorageParamsFromEvn();
        }
    }

    private String marksToImplication(String reqMarks) throws IOException, ParseException {
        double marks = Double.parseDouble(reqMarks);
        String implication = null;
        String filePath = new File("").getAbsolutePath();
        Object obj = new JSONParser().parse(new FileReader(filePath + JsonKey.GRADE_MANAGEMENT_JSON));
        JSONObject jo = (JSONObject) obj;
        List<Map<String, Object>> range = ((List<Map<String, Object>>) jo.get(JsonKey.GRADE_MANAGEMENT_KEY));
        for (Map<String, Object> map : range) {
            double lowerLimit = Double.parseDouble((String) map.get(JsonKey.LOWER_LIMIT));
            double upperLimit = Double.parseDouble((String) map.get(JsonKey.UPPER_LIMIT));
            if (marks >= lowerLimit && marks <= upperLimit) {
                implication = (String) map.get(JsonKey.INTERPRETITION);
            }
        }
        return implication;
    }

    private void cleanup(String path, String fileName) {
      try {
        File directory = new File(path);
        File[] files = directory.listFiles();
        for (File file : files) {
          if (file.getName().startsWith(fileName)) file.delete();
        }
        logger.info(null, "CertificateGeneratorActor: cleanUp completed");
      } catch (Exception ex) {
        logger.error(null, ex.getMessage(), ex);
      }
    }

}
