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

    /*
 * Copyright (c) 2012 Google Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except
 * in compliance with the License. You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software distributed under the License
 * is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express
 * or implied. See the License for the specific language governing permissions and limitations under
 * the License. */

    /**
     * A sample application that runs multiple requests against the Drive API. The requests this sample
     * makes are:
     * <ul>
     * <li>Does a resumable media upload</li>
     * <li>Updates the uploaded file by renaming it</li>
     * <li>Does a resumable media download</li>
     * <li>Does a direct media upload</li>
     * <li>Does a direct media download</li>
     * </ul>
     *
     * @author rmistry@google.com (Ravi Mistry)
     * //     *
     * /**
     * Be sure to specify the name of your application. If the application name is {@code null} or
     * blank, the application will log a warning. Suggested format is "MyCompany-ProductName/1.0".
     */
    public class Drive{
        public static final String APPLICATION_NAME = "Drive Project";


        private static java.io.File DIR_FOR_DOWNLOADS = null;

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

        private static com.google.api.services.drive.Drive.Files.List request;

        Drive() {
            try {
                httpTransport = GoogleNetHttpTransport.newTrustedTransport();
                dataStoreFactory = new FileDataStoreFactory(DATA_STORE_DIR);
                // authorization
                Credential credential = authorize();
                // set up the global Drive instance
                drive = new com.google.api.services.drive.Drive.Builder(httpTransport, JSON_FACTORY, credential).setApplicationName(
                        APPLICATION_NAME).build();
            } catch (IOException ioe) {
                System.out.println(ioe);
            } catch (Throwable t) {
                System.out.println(t);
            }
        }

        /**
         * Authorizes the installed application to access user's protected data.
         */
        private static Credential authorize() throws Exception {
            // load client secrets
            GoogleClientSecrets clientSecrets = GoogleClientSecrets.load(JSON_FACTORY,
                    new InputStreamReader(Drive.class.getResourceAsStream("/client_secrets.json")));
            if (clientSecrets.getDetails().getClientId().startsWith("Enter")
                    || clientSecrets.getDetails().getClientSecret().startsWith("Enter ")) {
                System.out.println(
                        "Enter Client ID and Secret from https://code.google.com/apis/console/?api=drive "
                                + "into drive-cmdline-sample/src/main/resources/client_secrets1.json");
                System.exit(1);
            }
            // set up authorization code flow
            GoogleAuthorizationCodeFlow flow = new GoogleAuthorizationCodeFlow.Builder(
                    httpTransport, JSON_FACTORY, clientSecrets,
                    Collections.singleton(DriveScopes.DRIVE_FILE)).setDataStoreFactory(dataStoreFactory)
                    .build();
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

                fileMetadata.setTitle(UPLOAD_FILE.getName());

                FileContent mediaContent = new FileContent(mimeType, UPLOAD_FILE);

                com.google.api.services.drive.Drive.Files.Insert insert = drive.files().insert(fileMetadata, mediaContent);
                MediaHttpUploader uploader = insert.getMediaHttpUploader();
                uploader.setDirectUploadEnabled(useDirectUpload);
                insert.execute();
                result = "upload is complete";
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

                OutputStream out = new FileOutputStream(new java.io.File(parentDir, uploadedFile.getTitle()));

                MediaHttpDownloader downloader =
                        new MediaHttpDownloader(httpTransport, drive.getRequestFactory().getInitializer());


                downloader.download(new GenericUrl(uploadedFile.getDownloadUrl()), out);
                return "File printed out to " + parentDir.getAbsolutePath();
            } catch (IOException ioe) {
                System.out.println(ioe.getMessage());
                return ioe.getMessage();
            } catch (Exception e) {
                return e.getMessage();
            }
        }

        public List<File> getAllFiles() {
            List<File> result = new ArrayList<File>();
            try {
                request = drive.files().list();
                FileList files = request.execute();
                result.addAll(files.getItems());
                request.setPageToken(files.getNextPageToken());
            } catch (IOException ioe) {
                System.out.println("An error occurred: " + ioe);
                request.setPageToken(null);
            }
            return result;
        }
        public java.io.File getDataStoreDir(){
            return DATA_STORE_DIR;
        }
        public void setDirForDownloads(java.io.File dir){
            DIR_FOR_DOWNLOADS = dir;
        }
    }
