package org.sunbird.incredible.processor.views;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang.StringUtils;
import org.sunbird.incredible.processor.store.ICertStore;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.sunbird.cloud.storage.exception.StorageServiceException;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.nio.charset.StandardCharsets;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

/**
 * Downloads zip file and unzips
 */
public class HTMLTemplateZip {


    private static Logger logger = LoggerFactory.getLogger(HTMLTemplateZip.class);

    /**
     * template zip file url
     */
    private String zipUrl;

    private ICertStore htmlTemplateStore;

    private String zipFilePath = "conf/";

    private String zipFileName;

    private String targetDir;

    public HTMLTemplateZip(ICertStore htmlTemplateStore, String zipUrl) {
        this.htmlTemplateStore = htmlTemplateStore;
        this.zipUrl = zipUrl;
        this.init();
    }


    public void init() {
        this.zipFileName = this.getZipFileName();
        //target directory to unzip the zip file
        this.targetDir = zipFilePath + StringUtils.substringBefore(zipFileName, ".zip");
    }

    public String getTemplateUrl() {
        return zipUrl;
    }

    /**
     * unzips zip file
     */
    public void unzip() throws IOException {
        File dir = new File(targetDir);
        // create output directory if it doesn't exist
        if (!dir.exists()) {
            dir.mkdirs();
        }
        FileInputStream fis = new FileInputStream(zipFilePath + zipFileName);
        ZipInputStream zipIn = new ZipInputStream(fis);
        try {
            ZipEntry entry = zipIn.getNextEntry();
            // iterates over entries in the zip file
            while (entry != null) {
                String filePath = targetDir + File.separator + entry.getName();
                if (!entry.isDirectory()) {
                    // if the entry is a file, extracts it
                    extractFile(zipIn, filePath);
                } else {
                    // if the entry is a directory, make the directory
                    File subDir = new File(filePath);
                    subDir.mkdir();
                }
                zipIn.closeEntry();
                entry = zipIn.getNextEntry();
            }
            logger.info("Unzipping zip file is finished");
        } finally {
            zipIn.close();
            fis.close();
        }

    }

    /**
     * extracts each files in zip file (zip entry)
     *
     * @param zipIn
     * @param filePath
     * @throws IOException
     */
    private void extractFile(ZipInputStream zipIn, String filePath) throws IOException {
        BufferedOutputStream bufferedOutputStream = new BufferedOutputStream(new FileOutputStream(filePath));
        try {
            byte[] bytesIn = new byte[4096];
            int read = 0;
            while ((read = zipIn.read(bytesIn)) != -1) {
                bufferedOutputStream.write(bytesIn, 0, read);
            }
        } finally {
            bufferedOutputStream.close();
        }
    }


    /**
     * used to get file name from the url
     *
     * @return zip file name
     */
    private String getZipFileName() {
        String fileName = null;
        try {
            URI uri = new URI(zipUrl);
            String path = uri.getPath();
            fileName = path.substring(path.lastIndexOf('/') + 1);
            if (!fileName.endsWith(".zip"))
                return fileName.concat(".zip");
        } catch (URISyntaxException e) {
            logger.debug("Exception while getting file name from template url : {}", e.getMessage());
        }
        return fileName;
    }


    public Boolean isIndexHTMlFileExits() {
        boolean isExits;
        File file = new File(this.targetDir + "/index.html");
        if (file.exists()) {
            isExits = true;
        } else {
            isExits = false;
        }
        return isExits;
    }

    public String getTemplateContent() throws IOException {
        String htmlFileName = "index.html";
        File targetDirectory = new File(targetDir);
        FileInputStream fis = new FileInputStream(targetDirectory.getAbsolutePath() + "/" +  htmlFileName);
        String content;
        try {
            content = IOUtils.toString(fis, StandardCharsets.UTF_8);
        } finally {
            fis.close();
        }
        return content;
    }

    public void download() throws IOException, StorageServiceException {
        checkDirectoryExists();
        htmlTemplateStore.init();
        htmlTemplateStore.get(zipUrl, zipFileName, zipFilePath);
    }


    private void checkDirectoryExists() {
        File file = new File(zipFilePath);
        if (!file.exists()) {
            file.mkdirs();
        }
    }


    /**
     * to check zip file is exists or not
     *
     * @return
     */
    public Boolean isZipFileExists() {
        boolean isExits;
        File file = new File(zipFilePath + zipFileName);
        if (file.exists()) {
            isExits = true;
        } else {
            isExits = false;
        }
        return isExits;
    }

    /**
     * deletes downloaded zip file and unzipped directory
     */
    public void cleanUp() {
        String dirName = StringUtils.substringBefore(zipFileName, ".zip");
        File directory = new File(zipFilePath + dirName);
        try {
            FileUtils.deleteDirectory(directory);
        } catch (IOException e) {
            logger.info("Exception while deleting directory  " + directory.getName() + " " + e.getMessage());
        }
        File zipFile = new File(zipFilePath + zipFileName);
        zipFile.delete();
        logger.info("HTMLTemplateZip: cleanUp completed");
    }

}