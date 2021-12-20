package controllers.Model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.commons.lang3.StringUtils;
import org.sunbird.ActorServiceException;
import org.sunbird.BaseException;
import org.sunbird.message.IResponseMessage;
import org.sunbird.message.Localizer;
import org.sunbird.message.ResponseCode;
import org.sunbird.request.RequestContext;
import org.sunbird.request.RequestParams;


import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.WeakHashMap;


@JsonIgnoreProperties(ignoreUnknown = true)
public class Certificate implements Serializable {



private ArrayList<Data> data;

private Issuer issuer;

private ArrayList<SignatoryList> signatoryList;

private String htmlTemplateId;

private String svgTemplateId;

private String courseName;

private String Location;

private String marks;

private String studentRegNo;

private String htmlTemplate;

private String svgTemplate;

private String recipientEmail;

private StoreConfig storeConfig;



    private static final long serialVersionUID = -2362783406031347676L;
    private static final Integer MIN_TIMEOUT = 0;
    private static final Integer MAX_TIMEOUT = 120;
    private static final int WAIT_TIME_VALUE = 120;

    protected Map<String, Object> context;
    private String id;
    private String ver;
    private String ts;
    private RequestParams params;

    private Map<String, Object> request = new HashMap<>();
    private Map<String, Object> headers = new WeakHashMap<>();

    private String managerName;
    private String operation;
    private String requestId;
    private int env;

    private Integer timeout; // in seconds

    private RequestContext requestContext;

    private static ObjectMapper mapper = new ObjectMapper();

    public Certificate(){
        this.params = new RequestParams();
        this.params.setMsgid(requestId);
    }

    public Certificate(Certificate request) {
        this.params = request.getParams();
        if (null == this.params) this.params = new RequestParams();
        if (StringUtils.isBlank(this.params.getMsgid()) && !StringUtils.isBlank(requestId))
            this.params.setMsgid(requestId);
        this.context.putAll(request.getContext());
    }

    public Certificate(RequestContext requestContext) {
        this.context = new WeakHashMap<>();
        this.params = new RequestParams();
        this.requestContext = requestContext;
    }

    public String getRequestId() {
        if (null != this.params) return this.params.getMsgid();
        return requestId;
    }

    public Map<String, Object> getContext() {
        return context;
    }

    public void setContext(Map<String, Object> context) {
        this.context = context;
    }

    /** @return the requestValueObjects */
    public Map<String, Object> getRequest() {
        return request;
    }

    public void setRequest(Map<String, Object> request) {
        this.request = request;
    }

    public Object get(String key) {
        return request.get(key);
    }

    public void setRequestId(String requestId) {
        this.requestId = requestId;
    }

    public void put(String key, Object vo) {
        request.put(key, vo);
    }

    public String getManagerName() {
        return managerName;
    }

    public void setManagerName(String managerName) {
        this.managerName = managerName;
    }

    public String getOperation() {
        return operation;
    }

    public void setOperation(String operation) {
        this.operation = operation;
    }



    public void copyRequestValueObjects(Map<String, Object> map) {
        if (null != map && map.size() > 0) {
            this.request.putAll(map);
        }
    }


    public ArrayList<Data> getData() {
        return data;
    }

    public void setData(ArrayList<Data> data) {
        this.data = data;
    }

    public Issuer getIssuer() {
        return issuer;
    }

    public void setIssuer(Issuer issuer) {
        this.issuer = issuer;
    }

    public ArrayList<SignatoryList> getSignatoryList() {
        return signatoryList;
    }

    public void setSignatoryList(ArrayList<SignatoryList> signatoryList) {
        this.signatoryList = signatoryList;
    }

    public String getHtmlTemplateId() {
        return htmlTemplateId;
    }

    public void setHtmlTemplateId(String htmlTemplateId) {
        this.htmlTemplateId = htmlTemplateId;
    }

    public String getSvgTemplateId() {
        return svgTemplateId;
    }

    public void setSvgTemplateId(String svgTemplateId) {
        this.svgTemplateId = svgTemplateId;
    }

    public String getCourseName() {
        return courseName;
    }

    public void setCourseName(String courseName) {
        this.courseName = courseName;
    }

    public String getLocation() {
        return Location;
    }

    public void setLocation(String location) {
        Location = location;
    }

    public String getMarks() {
        return marks;
    }

    public void setMarks(String marks) {
        this.marks = marks;
    }

    public String getStudentRegNo() {
        return studentRegNo;
    }

    public void setStudentRegNo(String studentRegNo) {
        this.studentRegNo = studentRegNo;
    }

    public String getHtmlTemplate() {
        return htmlTemplate;
    }

    public void setHtmlTemplate(String htmlTemplate) {
        this.htmlTemplate = htmlTemplate;
    }

    public String getSvgTemplate() {
        return svgTemplate;
    }

    public void setSvgTemplate(String svgTemplate) {
        this.svgTemplate = svgTemplate;
    }

    public String getRecipientEmail() {
        return recipientEmail;
    }

    public void setRecipientEmail(String recipientEmail) {
        this.recipientEmail = recipientEmail;
    }

    public StoreConfig getStoreConfig() {
        return storeConfig;
    }

    public void setStoreConfig(StoreConfig storeConfig) {
        this.storeConfig = storeConfig;
    }

    public static ObjectMapper getMapper() {
        return mapper;
    }

    public static void setMapper(ObjectMapper mapper) {
        Certificate.mapper = mapper;
    }




    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getVer() {
        return ver;
    }

    public void setVer(String ver) {
        this.ver = ver;
    }

    public String getTs() {
        return ts;
    }

    public void setTs(String ts) {
        this.ts = ts;
    }

    public RequestParams getParams() {
        return params;
    }

    public void setParams(RequestParams params) {
        this.params = params;
        if (this.params.getMsgid() == null && requestId != null) this.params.setMsgid(requestId);
    }

    public Map<String, Object> getHeaders() {
        return headers;
    }

    public void setHeaders(Map<String, Object> headers) {
        this.headers = headers;
    }

    /** @return the env */
    public int getEnv() {
        return env;
    }

    /** @param env the env to set */
    public void setEnv(int env) {
        this.env = env;
    }

    public Integer getTimeout() {
        return timeout == null ? WAIT_TIME_VALUE : timeout;
    }

    public void setTimeout(Integer timeout) throws BaseException {
        if (timeout < MIN_TIMEOUT && timeout > MAX_TIMEOUT) {
            throw new ActorServiceException.InvalidRequestTimeout(
                    IResponseMessage.INVALID_OPERATION_NAME,
                    Localizer.getInstance().getMessage(IResponseMessage.INVALID_OPERATION_NAME,null),
                    ResponseCode.CLIENT_ERROR.getCode());
        }
        this.timeout = timeout;
    }

    public RequestContext getRequestContext() {
        return requestContext;
    }

    public void setRequestContext(RequestContext requestContext) {
        this.requestContext = requestContext;
    }

//    @Override
//    public String toString() {
////        return  "request{" +
////                "certificate{" +
////                (context != null ? "context=" + context + ", " : "")
////                + (request != null ? "requestValueObjects=" + request : "")
////                + "data=" + data +
////                ", issuer=" + issuer +
////                ", signatoryList=" + signatoryList +
////                ", htmlTemplateId='" + htmlTemplateId + '\'' +
////                ", svgTemplateId='" + svgTemplateId + '\'' +
////                ", courseName='" + courseName + '\'' +
////                ", Location='" + Location + '\'' +
////                ", marks='" + marks + '\'' +
////                ", studentRegNo='" + studentRegNo + '\'' +
////                ", htmlTemplate='" + htmlTemplate + '\'' +
////                ", svgTemplate='" + svgTemplate + '\'' +
////                ", recipientEmail='" + recipientEmail + '\'' +
////                ", storeConfig=" + storeConfig +
////                '}' +
////                '}';
////        return "Request ["
////                + (context != null ? "context=" + context + ", " : "")
////                + (request != null ? "requestValueObjects= {" + data +
////                ", issuer=" + issuer +
////                ", signatoryList=" + signatoryList +
////                ", htmlTemplateId='" + htmlTemplateId + '\'' +
////                ", svgTemplateId='" + svgTemplateId + '\'' +
////                ", courseName='" + courseName + '\'' +
////                ", Location='" + Location + '\'' +
////                ", marks='" + marks + '\'' +
////                ", studentRegNo='" + studentRegNo + '\'' +
////                ", htmlTemplate='" + htmlTemplate + '\'' +
////                ", svgTemplate='" + svgTemplate + '\'' +
////                ", recipientEmail='" + recipientEmail + '\'' +
////                ", storeConfig=" + storeConfig + ", " : "")
////                + "}]";
//    }


    @Override
    public String toString() {
        return "certificate{" +
                "data=" + data +
                ", issuer=" + issuer +
                ", signatoryList=" + signatoryList +
                ", htmlTemplateId='" + htmlTemplateId + '\'' +
                ", svgTemplateId='" + svgTemplateId + '\'' +
                ", courseName='" + courseName + '\'' +
                ", Location='" + Location + '\'' +
                ", marks='" + marks + '\'' +
                ", studentRegNo='" + studentRegNo + '\'' +
                ", htmlTemplate='" + htmlTemplate + '\'' +
                ", svgTemplate='" + svgTemplate + '\'' +
                ", recipientEmail='" + recipientEmail + '\'' +
                ", storeConfig=" + storeConfig +
                ", context=" + context +
                ", id='" + id + '\'' +
                ", ver='" + ver + '\'' +
                ", ts='" + ts + '\'' +
                ", params=" + params +
                ", request=" + request +
                ", headers=" + headers +
                ", managerName='" + managerName + '\'' +
                ", operation='" + operation + '\'' +
                ", requestId='" + requestId + '\'' +
                ", env=" + env +
                ", timeout=" + timeout +
                ", requestContext=" + requestContext +
                '}';
    }
}
