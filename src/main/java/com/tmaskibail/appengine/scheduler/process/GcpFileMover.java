/*
 * Copyright (C) 2018 Google Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License. You may obtain a copy of
 * the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations under
 * the License.
 */
package com.tmaskibail.appengine.scheduler.process;

import com.google.api.gax.paging.Page;
import com.google.api.services.storage.StorageScopes;
import com.google.auth.oauth2.GoogleCredentials;
import com.google.cloud.storage.*;
import com.google.common.collect.Iterables;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.io.FileInputStream;
import java.io.IOException;

@Component
public class GcpFileMover {
    private static final Logger LOG = LoggerFactory.getLogger(GcpFileMover.class);

    @Value("${project.id}")
    private String projectId;
    @Value("${gcs.input.bucket}")
    private String inputBucket;
    @Value("${gcs.processing.bucket}")
    private String processingBucket;
    @Value("${gcs.archive.bucket}")
    private String archiveBucket;
    @Value("${service.account.key.file}")
    private String saKeyFile;

    public boolean moveBlobsToArchive() {
        LOG.info("Initiating files moves from [{}] to [{}]", processingBucket, archiveBucket);
        return moveFiles(processingBucket, archiveBucket);
    }

    public boolean moveBlobsToProcessing() {
        LOG.info("Initiating files moves from [{}] to [{}]", inputBucket, processingBucket);
        return moveFiles(inputBucket, processingBucket);
    }

    private boolean moveFiles(String source, String target) {
        GoogleCredentials credentials = null;
        try {
            credentials = GoogleCredentials.fromStream(new FileInputStream(saKeyFile));
            // CAUTION: This is for demo purposes only! Use granular scopes
            credentials.createScoped(StorageScopes.all());
        } catch (IOException e) {
            LOG.error("error occurred while creating credentials from ", e);
        }

        Storage storage = StorageOptions.getDefaultInstance()
                .newBuilder()
                .setProjectId(projectId)
                .setCredentials(credentials)
                .build()
                .getService();
        Bucket bucket = storage.get(source);
        Page<Blob> blobs = bucket.list();

        // If no files present, skip next steps
        if (Iterables.size(blobs.getValues()) == 0) {
            LOG.info("No files found in [{}]", source);
            return false;
        }

        for (Blob blob : blobs.iterateAll()) {
            // copy to processing bucket
            CopyWriter copyWriter = blob.copyTo(target, blob.getName());

            // Delete the file from inputBucket, ensure it copied before deleting
            Blob copiedBlob = copyWriter.getResult();
            storage.delete(source, blob.getName());
            LOG.info("Moved object [{}] from bucket [{}] to [{}]", blob.getName(), source, target);
        }
        return true;
    }
}
