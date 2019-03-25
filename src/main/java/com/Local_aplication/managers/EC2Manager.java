package com.Local_aplication.managers;

import com.amazonaws.AmazonServiceException;
import com.amazonaws.services.ec2.AmazonEC2;
import com.amazonaws.services.ec2.AmazonEC2ClientBuilder;
import com.amazonaws.services.ec2.model.*;

import java.util.List;

public class EC2Manager extends BaseManager{
    private AmazonEC2 ec2 = AmazonEC2ClientBuilder.standard()
            .withRegion("us-east-2")
            .build();

    public List<Instance> list(){
        DescribeInstancesRequest req = new DescribeInstancesRequest();
        List<Reservation> reservations = ec2.describeInstances(req).getReservations();
        return reservations.get(0).getInstances();
    }

    public Reservation get(String id){
        DescribeInstancesRequest req = new DescribeInstancesRequest().withInstanceIds(id);
        return ec2.describeInstances(req).getReservations().get(0);
    }

    public Instance create(){
    try {
        RunInstancesRequest request = new RunInstancesRequest("ami-00d9aa20ba18dd0b0", 1, 1)
                .withKeyName("my_first_keypair")
                .withSecurityGroups("launch-wizard-1");
        request.setInstanceType(InstanceType.T2Micro.toString());
        return ec2.runInstances(request).getReservation().getInstances().get(0);
    }
    catch (AmazonServiceException ase) {
        handle_amazon_service_exception(ase);
        throw ase;
        }
    }

    private void handle_amazon_service_exception(AmazonServiceException ase){
        System.out.println("Caught Exception: " + ase.getMessage());
        System.out.println("Reponse Status Code: " + ase.getStatusCode());
        System.out.println("Error Code: " + ase.getErrorCode());
        System.out.println("Request ID: " + ase.getRequestId());
    }
}