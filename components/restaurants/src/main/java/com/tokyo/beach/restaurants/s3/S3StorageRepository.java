package com.tokyo.beach.restaurants.s3;

import com.amazonaws.AmazonClientException;
import com.amazonaws.AmazonServiceException;
import com.amazonaws.auth.AWSCredentials;
import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3Client;
import com.amazonaws.services.s3.model.DeleteObjectRequest;
import org.springframework.stereotype.Repository;

@Repository
public class S3StorageRepository {
    public void deleteFile(String urlString) {
        String accessKey = System.getenv("AWS_S3_ACCESS_KEY");
        String secretKey = System.getenv("AWS_S3_SECRET_KEY");
        String bucketName = System.getenv("AWS_S3_BUCKET_NAME");

        AWSCredentials credentials = new BasicAWSCredentials(accessKey, secretKey);
        AmazonS3 s3Client = new AmazonS3Client(credentials);

        try {
            String[] urlSplit = urlString.split("/");
            String keyName = urlSplit[urlSplit.length - 1];

            s3Client.deleteObject(new DeleteObjectRequest(bucketName, keyName));
        } catch (AmazonServiceException ase) {
            System.out.println("Caught an AmazonServiceException.");
            System.out.println("Error Message:    " + ase.getMessage());
            System.out.println("HTTP Status Code: " + ase.getStatusCode());
            System.out.println("AWS Error Code:   " + ase.getErrorCode());
            System.out.println("Error Type:       " + ase.getErrorType());
            System.out.println("Request ID:       " + ase.getRequestId());
        } catch (AmazonClientException ace) {
            System.out.println("Caught an AmazonClientException.");
            System.out.println("Error Message: " + ace.getMessage());
        }
    }
}
