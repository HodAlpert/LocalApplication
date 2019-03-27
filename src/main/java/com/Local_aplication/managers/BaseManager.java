package com.Local_aplication.managers;
import com.Local_aplication.common.init;
import com.amazonaws.AmazonClientException;
import com.amazonaws.AmazonServiceException;
import com.amazonaws.auth.AWSCredentials;
import com.amazonaws.auth.DefaultAWSCredentialsProviderChain;
import java.util.logging.*;

public class BaseManager {
    protected AWSCredentials credentials = new DefaultAWSCredentialsProviderChain().getCredentials();

    protected void handle_amazon_service_exception(AmazonServiceException ase){
        System.out.println("Caught Exception: " + ase.getMessage());
        System.out.println("Reponse Status Code: " + ase.getStatusCode());
        System.out.println("Error Code: " + ase.getErrorCode());
        System.out.println("Request ID: " + ase.getRequestId());
        ase.printStackTrace();
    }
    protected void handle_client_exception(AmazonClientException ace){
        System.out.println("Caught an AmazonClientException, which means the client encountered " +
                "a serious internal problem while trying to communicate with service, such as not " +
                "being able to access the network.");
        System.out.println("Error Message: " + ace.getMessage());
        ace.printStackTrace();
    }
    protected void handle_exception(Exception exc){

        if (exc instanceof AmazonServiceException){
            handle_amazon_service_exception((AmazonServiceException) exc);
        }
        else if (exc instanceof AmazonClientException){
            handle_client_exception((AmazonClientException) exc);
        }
        else{
            exc.printStackTrace();

        }
    }

    protected Logger logger = Logger.getLogger(init.log_name);
}
