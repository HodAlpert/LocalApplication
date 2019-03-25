package com.Local_aplication.managers;/*
 * Copyright 2010-2012 Amazon.com, Inc. or its affiliates. All Rights Reserved.
 *
 * Licensed under the Apache License, Version 2.0 (the "License").
 * You may not use this file except in compliance with the License.
 * A copy of the License is located at
 *
 *  http://aws.amazon.com/apache2.0
 *
 * or in the "license" file accompanying this file. This file is distributed
 * on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either
 * express or implied. See the License for the specific language governing
 * permissions and limitations under the License.
 */
import java.io.*;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;


import com.amazonaws.AmazonClientException;
import com.amazonaws.AmazonServiceException;
import com.amazonaws.SdkClientException;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3ClientBuilder;
import com.amazonaws.services.s3.model.*;

/**
 * This sample demonstrates how to make basic requests to Amazon S3 using
 * the AWS SDK for Java.
 * <p>
 * <b>Prerequisites:</b> You must have a valid Amazon Web Services developer
 * account, and be signed up to use Amazon S3. For more information on
 * Amazon S3, see http://aws.amazon.com/s3.
 * <p>
 * <b>Important:</b> Be sure to fill in your AWS access credentials in the
 *                   AwsCredentials.properties file before you try to run this
 *                   sample.
 * http://aws.amazon.com/security-credentials
 */
public class S3Manager extends BaseManager {
    private AmazonS3 s3 = AmazonS3ClientBuilder.standard()
            .withRegion("us-east-2")
            .build();
    private String bucket_name;

    public S3Manager() {
        super();
    }

//    public static void main(String[] args) throws IOException {
//        /*
//         * Important: Be sure to fill in your AWS access credentials in the
//         *            AwsCredentials.properties file before you try to run this
//         *            sample.
//         * http://aws.amazon.com/security-credentials
//         */
//

    public void create_bucket(String directoryName) {
        bucket_name =
                credentials.getAWSAccessKeyId() + "-" + directoryName.
                        replace('\\', '_').
                        replace('/', '_').
                        replace(':', '_');
        bucket_name = bucket_name.toLowerCase();
        String key = null;

        System.out.println("===========================================");
        System.out.println("Creating S3 Bucket");
        System.out.println("===========================================\n");

        try {
            /*
             * Create a new S3 bucket - Amazon S3 bucket names are globally unique,
             * so once a bucket name has been taken by any user, you can't create
             * another bucket with that same name.
             *
             * You can optionally specify a location for your bucket if you want to
             * keep your data closer to your applications or users.
             */
            System.out.println("Creating bucket " + bucket_name + "\n");
            s3.createBucket(bucket_name);

            /*
             * List the buckets in your account
             */
            System.out.println("Listing buckets");
            for (Bucket bucket : s3.listBuckets()) {
                System.out.println(" - " + bucket.getName());
            }
            System.out.println();

        } catch (AmazonServiceException ase) {
            if (ase.getStatusCode() == 409 && ase.getErrorCode().equals("BucketAlreadyOwnedByYou")){
                System.out.println("bucket " + bucket_name + "already exist");
                return;
            }
            System.out.println("Caught an AmazonServiceException, which means your request made it "
                    + "to Amazon S3, but was rejected with an error response for some reason.");
            System.out.println("Error Message:    " + ase.getMessage());
            System.out.println("HTTP Status Code: " + ase.getStatusCode());
            System.out.println("AWS Error Code:   " + ase.getErrorCode());
            System.out.println("Error Type:       " + ase.getErrorType());
            System.out.println("Request ID:       " + ase.getRequestId());
        } catch (AmazonClientException ace) {
            System.out.println("Caught an AmazonClientException, which means the client encountered "
                    + "a serious internal problem while trying to communicate with S3, "
                    + "such as not being able to access the network.");
            System.out.println("Error Message: " + ace.getMessage());
        }
    }

    public List<PutObjectResult> upload_object(String directoryName) throws AmazonClientException {
        /*
         * Upload an object to your bucket - You can easily upload a file to
         * S3, or upload directly an InputStream if you know the length of
         * the data in the stream. You can also specify your own metadata
         * when uploading to S3, which allows you set a variety of options
         * like content-type and content-encoding, plus additional metadata
         * specific to your applications.
         */
        List<PutObjectResult> list = new ArrayList<PutObjectResult>();
        System.out.println("Uploading a new object to S3 from a file\n");
        File dir = new File(directoryName);
        if (dir.listFiles() == null){
            list.add(put_file(dir));
        }
        else {
            for (File file : dir.listFiles()) {
                list.add(put_file(file));
            }
        }
        return list;
    }

    private PutObjectResult put_file(File file) {
        String key = file.getName().replace('\\', '_').replace('/', '_').replace(':', '_');
        PutObjectRequest req = new PutObjectRequest(bucket_name, key, file);
        return s3.putObject(req);
    }

    public void download_file_as_text(String key, String path) {


        /*
         * Download an object - When you download an object, you get all of
         * the object's metadata and a stream from which to read the contents.
         * It's important to read the contents of the stream as quickly as
         * possibly since the data is streamed directly from Amazon S3 and your
         * network connection will remain open until you read all the data or
         * close the input stream.
         *
         * GetObjectRequest also supports several other options, including
         * conditional downloading of objects based on modification times,
         * ETags, and selectively downloading a range of an object.
         */
        System.out.println("Downloading an object");
        S3Object object = s3.getObject(new GetObjectRequest(bucket_name, key));
        System.out.println("Content-Type: " + object.getObjectMetadata().getContentType());
        try {
            displayTextInputStream(object.getObjectContent(), path);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public List<S3ObjectSummary> list() {
        /*
         * List objects in your bucket by prefix - There are many options for
         * listing the objects in your bucket.  Keep in mind that buckets with
         * many objects might truncate their results when listing their objects,
         * so be sure to check if the returned object listing is truncated, and
         * use the AmazonS3.listNextBatchOfObjects(...) operation to retrieve
         * additional results.
         */
        System.out.println("Listing objects");
        List<S3ObjectSummary> list = new ArrayList<S3ObjectSummary>();
        ObjectListing objectListing = s3.listObjects(new ListObjectsRequest()
                .withBucketName(bucket_name)
                .withPrefix("My"));
        for (S3ObjectSummary objectSummary : objectListing.getObjectSummaries()) {
            System.out.println(" - " + objectSummary.getKey() + "  " +
                    "(size = " + objectSummary.getSize() + ")");
            list.add(objectSummary);
        }
        return list;
    }

    public void delete_object(String key) {
        /*
         * Delete an object - Unless versioning has been turned on for your bucket,
         * there is no way to undelete an object, so use caution when deleting objects.
         */
        System.out.println("Deleting an object\n");
        s3.deleteObject(bucket_name, key);
    }

    public void delete_bucket() {
        // Delete all objects from the bucket. This is sufficient
        // for unversioned buckets. For versioned buckets, when you attempt to delete objects, Amazon S3 inserts
        // delete markers for all objects, but doesn't delete the object versions.
        // To delete objects from versioned buckets, delete all of the object versions before deleting
        // the bucket (see below for an example).
        try{

        ObjectListing objectListing = s3.listObjects(bucket_name);
        while (true) {
            Iterator<S3ObjectSummary> objIter = objectListing.getObjectSummaries().iterator();
            while (objIter.hasNext()) {
                s3.deleteObject(bucket_name, objIter.next().getKey());
            }

            // If the bucket contains many objects, the listObjects() call
            // might not return all of the objects in the first listing. Check to
            // see whether the listing was truncated. If so, retrieve the next page of objects 
            // and delete them.
            if (objectListing.isTruncated()) {
                objectListing = s3.listNextBatchOfObjects(objectListing);
            } else {
                break;
            }
        }

        // Delete all object versions (required for versioned buckets).
        VersionListing versionList = s3.listVersions(new ListVersionsRequest().withBucketName(bucket_name));
        while (true) {
            Iterator<S3VersionSummary> versionIter = versionList.getVersionSummaries().iterator();
            while (versionIter.hasNext()) {
                S3VersionSummary vs = versionIter.next();
                s3.deleteVersion(bucket_name, vs.getKey(), vs.getVersionId());
            }

            if (versionList.isTruncated()) {
                versionList = s3.listNextBatchOfVersions(versionList);
            } else {
                break;
            }
        }

        // After all objects and object versions are deleted, delete the bucket.
        s3.deleteBucket(bucket_name);
    }
        catch(AmazonServiceException e) {
        // The call was transmitted successfully, but Amazon S3 couldn't process 
        // it, so it returned an error response.
        e.printStackTrace();
    }
        catch( SdkClientException e) {
        // Amazon S3 couldn't be contacted for a response, or the client couldn't
        // parse the response from Amazon S3.
        e.printStackTrace();
    }
}

    
    public String get_key(String format){
        return format.replace('\\', '_').replace('/', '_').replace(':', '_');
    }

    /**
     * Displays the contents of the specified input stream as text.
     *
     * @param input The input stream to display as text.
     * @throws IOException
     */
    private static void displayTextInputStream(InputStream input, String path) throws IOException {
        BufferedReader reader = new BufferedReader(new InputStreamReader(input));
        BufferedWriter out = null;
        try {
            String line = reader.readLine();
            FileWriter fstream = new FileWriter(path, true); //true tells to append data.
            out = new BufferedWriter(fstream);
            while (line != null) {
                out.write(line+ '\n');
                line = reader.readLine();
            }
        } finally {
            if (out != null) {
                out.close();
            }
        }
    }

}
