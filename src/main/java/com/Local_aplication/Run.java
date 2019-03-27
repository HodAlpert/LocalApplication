package com.Local_aplication;

import com.Local_aplication.common.init;
import com.Local_aplication.managers.EC2Manager;
import com.Local_aplication.managers.S3Manager;
import com.Local_aplication.managers.SQSManager;

import java.util.Arrays;

public class Run {

    private EC2Manager ec2 = new EC2Manager();
    private S3Manager s3 = new S3Manager();
    private SQSManager sqs = new SQSManager();
    Run(){
        init.main();
    }
    public void main(String[] args){
        power_up_manager_if_needed();
        System.out.println(Arrays.toString(args));

    }
    private void power_up_manager_if_needed(){
        if (!ec2.is_manager_up()){
            ec2.power_up_manager();
        }
    }
}
