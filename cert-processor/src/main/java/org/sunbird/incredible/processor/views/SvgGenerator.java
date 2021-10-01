package org.sunbird.incredible.processor.views;

import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.text.StringSubstitutor;
import org.sunbird.incredible.processor.store.LocalStore;
import org.sunbird.incredible.pojos.CertificateExtension;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class SvgGenerator {

    private Logger logger = LoggerFactory.getLogger(SvgGenerator.class);
    private String svgTemplate;
    private String directory;
    private static Map<String, String> encoderMap = new HashMap<>();
    private static Map<String, String> decoderMap = new HashMap<>();
    private static Map<String, String> cachedSvgTemplates = new HashMap<>();


    static {
        encoderMap.put("<", "%3C");
        encoderMap.put(">", "%3E");
        encoderMap.put("#", "%23");
        encoderMap.put("%", "%25");
        encoderMap.put("\"", "\'");
    }

    static{
        decoderMap.put("%3C","<");
        decoderMap.put("%3E",">");
        decoderMap.put("%23","#");
        decoderMap.put("%25","%");
        decoderMap.put("%20"," ");
        decoderMap.put("%2C",",");
        decoderMap.put("%3A",":");
    }

    public SvgGenerator(String svgTemplate, String directory) {
        this.svgTemplate = svgTemplate;
        this.directory = directory;
    }

    public String generate(CertificateExtension certificateExtension, String encodedQrCode) throws IOException {
        String svgFileName = getSvgFileName();
        String svgContent;
        File file = new File(directory + svgFileName);
        if (!file.exists()) {
            logger.info("{} file does not exits , downloading", svgFileName);
            download(svgFileName);
        }
        if (!cachedSvgTemplates.containsKey(this.svgTemplate)) {
            logger.info("svg data is not cached , read svf file");
            svgContent = readSvgContent(file.getAbsolutePath());
            String encodedSvg = "data:image/svg+xml," + encodeData(svgContent);
            encodedSvg = encodedSvg.replaceAll("\n", "").replaceAll("\t", "");
            cachedSvgTemplates.put(this.svgTemplate, encodedSvg);
        }
        logger.info("svg template is cached {}", cachedSvgTemplates.containsKey(this.svgTemplate));
        String svgData = replaceTemplateVars(cachedSvgTemplates.get(this.svgTemplate), certificateExtension, encodedQrCode);
        logger.info("svg template string creation completed {}", StringUtils.isNotBlank(svgData));
        return svgData;
    }


    private String replaceTemplateVars(String svgContent, CertificateExtension certificateExtension, String encodeQrCode) {
        HTMLVarResolver htmlVarResolver = new HTMLVarResolver(certificateExtension);
        Map<String, String> certData = htmlVarResolver.getCertMetaData();
        certData.put("qrCodeImage", "data:image/png;base64," + encodeQrCode);
        StringSubstitutor sub = new StringSubstitutor(certData);
        String resolvedString = sub.replace(svgContent);
        logger.info("replacing temp vars completed");
        return resolvedString;
    }

    private String encodeData(String data) {
        StringBuffer stringBuffer = new StringBuffer();
        Pattern pattern = Pattern.compile("[<>#%\"]");
        Matcher matcher = pattern.matcher(data);
        while (matcher.find()) {
            matcher.appendReplacement(stringBuffer, encoderMap.get(matcher.group()));
        }
        matcher.appendTail(stringBuffer);
        return stringBuffer.toString();
    }

    public String decodeData(String data) {
        StringBuffer stringBuffer = new StringBuffer();
        Pattern pattern = Pattern.compile("%3C|%3E|%23|%25|%20|%2C|%3A");
        Matcher matcher = pattern.matcher(data);
        while (matcher.find()) {
            matcher.appendReplacement(stringBuffer, decoderMap.get(matcher.group()));
        }
        matcher.appendTail(stringBuffer);
        return stringBuffer.toString();
    }

    private String readSvgContent(String path) throws IOException {
        FileInputStream fis;
        String svgContent = null;
        fis = new FileInputStream(path);
        svgContent = IOUtils.toString(fis, StandardCharsets.UTF_8);
        fis.close();
        return svgContent;
    }

    private void download(String fileName) throws IOException {
        LocalStore localStore = new LocalStore("");
        localStore.get(svgTemplate, fileName, directory);
    }

    private String getSvgFileName() {
        String fileName = null;
        try {
            URI uri = new URI(svgTemplate);
            String path = uri.getPath();
            fileName = path.substring(path.lastIndexOf('/') + 1);
            if (!fileName.endsWith(".svg"))
                return fileName.concat(".svg");
        } catch (URISyntaxException e) {
            logger.debug("Exception while getting file name from template url : {}", e.getMessage());
        }
        return fileName;
    }


}
