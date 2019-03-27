package com.Local_aplication.managers;
import com.amazonaws.AmazonServiceException;
import com.amazonaws.services.ec2.AmazonEC2;
import com.amazonaws.services.ec2.AmazonEC2ClientBuilder;
import com.amazonaws.services.ec2.model.*;
import com.amazonaws.services.translate.model.Term;
import org.apache.commons.codec.binary.Base64;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;

public class EC2Manager extends BaseManager{
    private String manager_instance_id = "i-0e07f0e6914ef47fe";
    private AmazonEC2 ec2 = AmazonEC2ClientBuilder.standard()
            .withRegion("us-east-2")
            .build();

    public List<Instance> list(){
        logger.config("entry");
        DescribeInstancesRequest req = new DescribeInstancesRequest();
        List<Reservation> reservations = ec2.describeInstances(req).getReservations();
        logger.exiting("EC2Manager", "list");
        List<Instance> answer = reservations.get(0).getInstances();
        logger.config("returned " + answer);
        return answer;
    }

    public Instance get(String id){
        logger.info("entry");
        DescribeInstancesRequest req = new DescribeInstancesRequest().withInstanceIds(id);
        Instance answer = ec2.describeInstances(req).getReservations().get(0).getInstances().get(0);
        logger.config("returned " + answer);
        return answer;
    }

    public Instance create(List<String> user_data){
        logger.config("entry");
        IamInstanceProfileSpecification instance_profile = new IamInstanceProfileSpecification()
                .withArn("arn:aws:iam::606249488880:instance-profile/worker_role");
    try {
        RunInstancesRequest request = new RunInstancesRequest("ami-0cd3dfa4e37921605", 1, 1)
                .withKeyName("my_first_keypair")
                .withSecurityGroups("launch-wizard-1")
                .withIamInstanceProfile(instance_profile)
                .withUserData(getUserDataScript(user_data))
                .withTagSpecifications();
        request.setInstanceType(InstanceType.T2Micro.toString());
        logger.info(String.format("Creating instance with user-data %s", user_data.toString()));
        Instance answer = ec2.runInstances(request).getReservation().getInstances().get(0);
        logger.config("returned " + answer);
        return answer;
    }
    catch (AmazonServiceException ase) {
        handle_amazon_service_exception(ase);
        throw ase;
        }
    }

    public boolean is_manager_up(){
        logger.info("entry");
        boolean answer = get(manager_instance_id).getState().getName().equals("running");
        logger.config("returned " + answer);
        return answer;
    }

    private String getUserDataScript(List <String> script){
        return new String(Base64.encodeBase64(join(script).getBytes()));
    }

    private static String join(Collection<String> s) {
        StringBuilder builder = new StringBuilder();
        Iterator<String> iter = s.iterator();
        while (iter.hasNext()) {
            builder.append(iter.next());
            if (!iter.hasNext()) {
                break;
            }
            builder.append("\n");
        }
        return builder.toString();
    }

    public static List<String> init_script(){
        List<String> list = new ArrayList<>();
        list.add("#! /bin/bash");
        return list;
    }
    public StartInstancesResult power_up_manager(){
        logger.config("entry");
        try {
            StartInstancesRequest startInstancesRequest = new StartInstancesRequest().withInstanceIds(manager_instance_id);
            StartInstancesResult answer = ec2.startInstances(startInstancesRequest);
            logger.config("returned " + answer);
            return answer;
        }
        catch (Exception exc) {
            handle_exception(exc);
            return null;
        }
    }

    public void stop_manager(){
        logger.config("entry");
        logger.info("stopping manager");
        try{
            StopInstancesRequest stopInstancesRequest = new StopInstancesRequest()
                    .withInstanceIds(manager_instance_id);

            ec2.stopInstances(stopInstancesRequest)
                    .getStoppingInstances()
                    .get(0)
                    .getPreviousState()
                    .getName();
        }
        catch (Exception exc) {
            handle_exception(exc);
        }
    }
    public TerminateInstancesResult terminate_instance(String id){
        logger.config("entry");
        try{
            TerminateInstancesRequest request = new TerminateInstancesRequest().withInstanceIds(id);
            TerminateInstancesResult answer = ec2.terminateInstances(request);
            logger.config("returned " + answer);
            return answer;
        }
        catch (Exception exc) {
            handle_exception(exc);
            return null;
        }
    }
}