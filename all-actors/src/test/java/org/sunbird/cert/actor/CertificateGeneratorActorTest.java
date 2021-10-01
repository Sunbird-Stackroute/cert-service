package org.sunbird.cert.actor;

import akka.actor.ActorRef;
import akka.actor.ActorSystem;
import akka.actor.Props;
import akka.testkit.javadsl.TestKit;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;
import org.apache.commons.io.filefilter.WildcardFileFilter;
import org.sunbird.incredible.processor.JsonKey;
import org.sunbird.incredible.processor.qrcode.AccessCodeGenerator;
import org.sunbird.incredible.processor.qrcode.QRCodeGenerationModel;
import org.sunbird.incredible.processor.qrcode.utils.QRCodeImageGenerator;
import org.sunbird.incredible.processor.store.*;
import org.sunbird.incredible.processor.views.HTMLTemplateZip;
import org.sunbird.incredible.processor.views.HeadlessChromeHtmlToPdfConverter;
import org.junit.After;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PowerMockIgnore;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;
import org.sunbird.CertMapper;
import org.sunbird.incredible.processor.views.SvgGenerator;
import org.sunbird.cloud.storage.BaseStorageService;
import org.sunbird.cloud.storage.factory.StorageConfig;
import org.sunbird.cloud.storage.factory.StorageServiceFactory;
import org.sunbird.message.Localizer;
import org.sunbird.message.ResponseCode;
import org.sunbird.request.Request;
import org.sunbird.response.Response;
import scala.concurrent.duration.Duration;

import java.io.File;
import java.io.FileInputStream;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.when;

@RunWith(PowerMockRunner.class)
@PrepareForTest({HeadlessChromeHtmlToPdfConverter.class,
        CloudStorage.class,
        ICertStore.class,
        Localizer.class,
        AzureStore.class,
        FileUtils.class,
        File.class,
        StorageServiceFactory.class,
        LocalStore.class,
        IOUtils.class,
        SvgGenerator.class})
@PowerMockIgnore({"jdk.internal.reflect.*", "javax.management.*", "sun.security.ssl.*", "javax.net.ssl.*", "javax.crypto.*"})
public class CertificateGeneratorActorTest {
    private static ActorSystem system = ActorSystem.create("system");
    private static final Props props = Props.create(CertificateGeneratorActor.class);

    @BeforeClass
    public static void setUp() throws Exception {
    }

    @After
    public void tearDown() throws Exception {
    }

    @Test
    public void generateCertificate() throws Exception {
        Request request = createCertRequest();
        mock(request);
        TestKit probe = new TestKit(system);
        ActorRef subject = system.actorOf(props);
        subject.tell(request, probe.getRef());
        Response res = probe.expectMsgClass(Duration.create(40, TimeUnit.SECONDS),Response.class);
        Assert.assertTrue(null != res && res.getResponseCode() == ResponseCode.OK);
    }

    private void mock(Request request) throws Exception {
        PowerMockito.mockStatic(HeadlessChromeHtmlToPdfConverter.class);
        PowerMockito.mockStatic(FileUtils.class);
        PowerMockito.mockStatic(Localizer.class);
        when(Localizer.getInstance()).thenReturn(null);
        CertStoreFactory certStoreFactory = PowerMockito.mock(CertStoreFactory.class);
        PowerMockito.whenNew(CertStoreFactory.class).withArguments(Mockito.anyMap()).thenReturn(certStoreFactory);
        StoreConfig storeParams = PowerMockito.mock(StoreConfig.class);
        PowerMockito.whenNew(StoreConfig.class).withArguments(Mockito.anyMap()).thenReturn(storeParams);
        ICertStore htmlTemplateStore = PowerMockito.mock(ICertStore.class);
        when(certStoreFactory.getHtmlTemplateStore(Mockito.anyString(),Mockito.any(StoreConfig.class))).thenReturn(htmlTemplateStore);
        ICertStore certStore = PowerMockito.mock(ICertStore.class);
        when(certStoreFactory.getCertStore(Mockito.any(StoreConfig.class),Mockito.anyBoolean())).thenReturn(certStore);
        CertMapper certMapper = PowerMockito.mock(CertMapper.class);
        PowerMockito.whenNew(CertMapper.class).withArguments(Mockito.anyMap()).thenReturn(certMapper);
        HTMLTemplateZip hTMLTemplateZip = PowerMockito.mock(HTMLTemplateZip.class);
        PowerMockito.whenNew(HTMLTemplateZip.class).withArguments(Mockito.any(ICertStore.class),Mockito.anyString()).thenReturn(hTMLTemplateZip);
        PowerMockito.when(certStoreFactory.getDirectoryName(Mockito.anyString())).thenReturn("directory");
        AccessCodeGenerator accessCodeGenerator = PowerMockito.mock(AccessCodeGenerator.class);
        PowerMockito.whenNew(AccessCodeGenerator.class).withArguments(Mockito.anyDouble()).thenReturn(accessCodeGenerator);
        PowerMockito.when(accessCodeGenerator.generate()).thenReturn("anyString");
        QRCodeGenerationModel qrCodeGenerationModel = PowerMockito.mock(QRCodeGenerationModel.class);
        PowerMockito.whenNew(QRCodeGenerationModel.class).withNoArguments().thenReturn(qrCodeGenerationModel);
        QRCodeImageGenerator qrCodeImageGenerator = PowerMockito.mock(QRCodeImageGenerator.class);
        PowerMockito.whenNew(QRCodeImageGenerator.class).withNoArguments().thenReturn(qrCodeImageGenerator);
        File newFile = PowerMockito.mock(File.class);
        PowerMockito.when(qrCodeImageGenerator.createQRImages(qrCodeGenerationModel)).thenReturn(newFile);
        byte[] fileContent = new byte[4];
        when(FileUtils.readFileToByteArray(Mockito.any(File.class))).thenReturn(fileContent);
        PowerMockito.when(storeParams.getType()).thenReturn("Mockito.anyString()");
        File file6 = PowerMockito.mock(File.class);
        PowerMockito.when(FileUtils.getFile(Mockito.anyString())).thenReturn(file6);
        when(file6.exists()).thenReturn(true);
        AzureStoreConfig azureStoreConfig = PowerMockito.mock(AzureStoreConfig.class);
        PowerMockito.when(storeParams.getAzureStoreConfig()).thenReturn(azureStoreConfig);
        PowerMockito.when(azureStoreConfig.getAccount()).thenReturn("Mockito.anyString()");
        PowerMockito.when(azureStoreConfig.getKey()).thenReturn("Mockito.anyString()");
        StorageConfig storageConfig = PowerMockito.mock(StorageConfig.class);
        PowerMockito.when(storeParams.getType()).thenReturn("Mockito.anyString()");
        PowerMockito.whenNew(StorageConfig.class).withArguments(Mockito.anyString(),Mockito.anyString(),Mockito.anyString()).thenReturn(storageConfig);
        BaseStorageService storageService = PowerMockito.mock(BaseStorageService.class);
        PowerMockito.mockStatic(StorageServiceFactory.class);
        PowerMockito.when(StorageServiceFactory.getStorageService(Mockito.any(StorageConfig.class))).thenReturn(storageService);
        PowerMockito.when(certStore.save(Mockito.any(File.class), Mockito.anyString())).thenReturn("Mockito.anyString()");
        PowerMockito.when(certStore.getPublicLink(Mockito.any(File.class), Mockito.anyString())).thenReturn("Mockito.anyString()");
        File file2 = PowerMockito.mock(File.class);
        File file3 = PowerMockito.mock(File.class);
        List<File> fileList = new ArrayList<>();
        fileList.add(file2);
        WildcardFileFilter wildcardFileFilter = PowerMockito.mock(WildcardFileFilter.class);
        PowerMockito.when(FileUtils.listFiles(Mockito.any(File.class),Mockito.any(WildcardFileFilter.class),Mockito.any())).thenReturn(fileList);
        PowerMockito.when(file3.delete()).thenReturn(true);
        Iterator iterator = PowerMockito.mock(Iterator.class);
        PowerMockito.when(iterator.hasNext()).thenReturn(false);
        LocalStore localStore = PowerMockito.mock(LocalStore.class);
        PowerMockito.whenNew(LocalStore.class).withArguments(Mockito.anyString()).thenReturn(localStore);
        doNothing().when(localStore).get(Mockito.anyString(), Mockito.anyString(), Mockito.anyString());
        FileInputStream fileInputStreamMock = PowerMockito.mock(FileInputStream.class);
        PowerMockito.whenNew(FileInputStream.class).withArguments(Mockito.anyString())
                .thenReturn(fileInputStreamMock);
        PowerMockito.mockStatic(IOUtils.class);
        when(IOUtils.toString(fileInputStreamMock, StandardCharsets.UTF_8)).thenReturn("This is to acknowledge that $recipientName has successfully completed the training $courseName");

    }

    private Request createCertRequest() {
        Request reqObj = new Request();
        reqObj.setOperation(JsonKey.GENERATE_CERT_V2);
        Map<String, Object> innerMap = new HashMap<>();
        List<Map<String, Object>> listOfData = new ArrayList<>();
        Map<String, Object> data = new HashMap<>();
        data.put(JsonKey.RECIPIENT_NAME, "name");
        listOfData.add(data);
        Map<String, Object> issuer = new HashMap<>();
        issuer.put(JsonKey.NAME, "issuer name");
        issuer.put(JsonKey.URL, "issuer url");
        List<Map<String, Object>> signatoryList = new ArrayList<>();
        Map<String, Object> signatory = new HashMap<>();
        signatory.put(JsonKey.NAME, "signatory name");
        signatoryList.add(signatory);
        Map<String,Object>storeMap=new HashMap<>();
        storeMap.put("cloudStore",false);
        innerMap.put(JsonKey.STORE,storeMap);
        innerMap.put(JsonKey.SIGNATORY_LIST, signatoryList);
        innerMap.put(JsonKey.DATA, listOfData);
        innerMap.put(JsonKey.KEYS, null);
        innerMap.put(JsonKey.ISSUER, issuer);
        innerMap.put(JsonKey.HTML_TEMPLATE, "https://drive.google.com/a/ilimi.in/uc?authuser=1&id=16WgZrm-1Dh44uFryMTo_0uVjZv65mp4u&export=download");
        innerMap.put(JsonKey.SVG_TEMPLATE, "https://sunbirddev.blob.core.windows.net/user/cert/File-01308512781758464046.svg");
        reqObj.getRequest().put(JsonKey.CERTIFICATE, innerMap);
        Map<String, Object> context = new HashMap<>();
        context.put(JsonKey.VERSION, JsonKey.VERSION_2);
        reqObj.setContext(context);
        return reqObj;
    }

}