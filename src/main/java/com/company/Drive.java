package com.company;

// Imported statements from google api drive

import com.google.api.client.auth.oauth2.Credential;
import com.google.api.client.extensions.java6.auth.oauth2.AuthorizationCodeInstalledApp;
import com.google.api.client.extensions.jetty.auth.oauth2.LocalServerReceiver;
import com.google.api.client.googleapis.auth.oauth2.GoogleAuthorizationCodeFlow;
import com.google.api.client.googleapis.auth.oauth2.GoogleClientSecrets;
import com.google.api.client.googleapis.javanet.GoogleNetHttpTransport;
import com.google.api.client.googleapis.json.GoogleJsonError;
import com.google.api.client.googleapis.json.GoogleJsonResponseException;
import com.google.api.client.googleapis.media.MediaHttpDownloader;
import com.google.api.client.googleapis.media.MediaHttpUploader;
import com.google.api.client.http.FileContent;
import com.google.api.client.http.GenericUrl;
import com.google.api.client.http.HttpTransport;
import com.google.api.client.json.JsonFactory;
import com.google.api.client.json.jackson2.JacksonFactory;
import com.google.api.client.util.Preconditions;
import com.google.api.client.util.store.DataStoreFactory;
import com.google.api.client.util.store.FileDataStoreFactory;
import com.google.api.services.drive.DriveScopes;
import com.google.api.services.drive.model.File;
import com.google.api.services.drive.model.FileList;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

import static com.google.api.client.googleapis.media.MediaHttpUploader.UploadState.MEDIA_COMPLETE;

   // github public repository https://github.com/qaalib101/DriveProject


    public class Drive{
        public static final String APPLICATION_NAME = "DriveDesktop";

        // by default downloads go to a folder in your root directory

        private static java.io.File DIR_FOR_DOWNLOADS = new java.io.File("/Downloads");

        /**
         * Directory to store user credentials.
         */
        private static final java.io.File DATA_STORE_DIR =
                new java.io.File("DriveProject");

        /**
         * Global instance of the {@link DataStoreFactory}. The best practice is to make it a single
         * globally shared instance across your application.
         */
        private static FileDataStoreFactory dataStoreFactory;

        /**
         * Global instance of the HTTP transport.
         */
        private static HttpTransport httpTransport;

        /**
         * Global instance of the JSON factory.
         */
        private static final JsonFactory JSON_FACTORY = JacksonFactory.getDefaultInstance();

        /**
         * Global Drive API client.
         */
        public static com.google.api.services.drive.Drive drive;

        // Drive list object created
        public static com.google.api.services.drive.Drive.Files.List request;

        private static boolean connected = false;

        Drive() {
            try {
                httpTransport = GoogleNetHttpTransport.newTrustedTransport();
                dataStoreFactory = new FileDataStoreFactory(DATA_STORE_DIR);
            } catch (IOException ioe) {
                ioe.printStackTrace();
            } catch (Throwable t) {
                t.printStackTrace();
            }
        }

        /**
         * Authorizes the installed application to access user's protected data.
         */
        public static void connectToDrive(){
            try{
                Credential credential = authorize();
                drive = new com.google.api.services.drive.Drive.Builder(httpTransport, JSON_FACTORY, credential).setApplicationName(
                        APPLICATION_NAME).build();
                connected = true;
            }catch (Exception e){
                e.printStackTrace();
            }
        }
        private static Credential authorize() throws Exception {
            // load client secrets
            GoogleClientSecrets clientSecrets = GoogleClientSecrets.load(JSON_FACTORY,
                    new InputStreamReader(Drive.class.getResourceAsStream("/client_secrets.json")));
            // set up authorization code flow
            GoogleAuthorizationCodeFlow flow = new GoogleAuthorizationCodeFlow.Builder(
                    httpTransport, JSON_FACTORY, clientSecrets,
                    Collections.singleton(DriveScopes.DRIVE)).setAccessType("offline").setDataStoreFactory(dataStoreFactory)
                    .setApprovalPrompt("force").build();
            // authorize
            return new AuthorizationCodeInstalledApp(flow, new LocalServerReceiver()).authorize("qaalibomer@gmail.com");
        }

        /**
         * Uploads a file using either resumable or direct media upload.
         */
        public static String uploadFile(boolean useDirectUpload, String filePath, String mimeType) {
            String result;
            try {
                File fileMetadata = new File();
                java.io.File UPLOAD_FILE = new java.io.File(filePath);

                fileMetadata.setName(UPLOAD_FILE.getName());

                FileContent mediaContent = new FileContent(mimeType, UPLOAD_FILE);

                File file = drive.files().create(fileMetadata, mediaContent)
                        .setFields("id")
                        .execute();
                result = "UPLOAD IS COMPLETE";
                return result;
            } catch (IOException ioe) {
                System.out.println(ioe);
                return null;
            } catch (Exception e) {
                System.out.println(e);
                return null;
            }
        }


        /**
         * Downloads a file using either resumable or direct media download.
         */
        public static String downloadFile( File uploadedFile) {
            // create parent directory (if necessary)
            try {
                java.io.File parentDir = DIR_FOR_DOWNLOADS;

                OutputStream out = new FileOutputStream(new java.io.File(parentDir, uploadedFile.getName()));

                // creating a media http downloader
                String fileId = uploadedFile.getId();
                drive.files().get(fileId)
                        .executeMediaAndDownloadTo(out);

                return "File printed out to " + parentDir.getAbsolutePath();
            } catch (IOException ioe) {
                ioe.printStackTrace();
                return ioe.getMessage();
            } catch (Exception e) {
                e.printStackTrace();
                return e.getMessage();
            }
        }

        // getting all the files

        public List<File> getAllFiles() {

            // list of drive files

            List<File> result = new ArrayList<File>();
            try {
                request = drive.files().list();
                FileList files = request.execute();
                result.addAll(files.getFiles());
                request.setPageToken(files.getNextPageToken());
            } catch (IOException ioe) {
                ioe.printStackTrace();
                request.setPageToken(null);
            } catch (Exception e){
                e.printStackTrace();
            }
            return result;
        }

        public java.io.File getDataStoreDir(){
            return DATA_STORE_DIR;
        }


        public void setDirForDownloads(java.io.File dir){
            DIR_FOR_DOWNLOADS = dir;
        }

        public boolean getConnectedStatus(){
            return connected;
        }
    }
