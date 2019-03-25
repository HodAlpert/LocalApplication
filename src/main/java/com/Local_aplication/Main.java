package com.Local_aplication;

import com.Local_aplication.managers.EC2Manager;
import com.Local_aplication.managers.S3Manager;
import com.amazonaws.services.s3.model.PutObjectResult;
import com.amazonaws.services.s3.model.S3AccelerateUnsupported;

import java.util.List;

public class Main {
    public static void main(String args[])
    {
        EC2Manager ec2 = new EC2Manager();
        S3Manager s3 = new S3Manager();
        ec2.list();
        s3.create_bucket("mybucket");
        s3.delete_bucket();
    }
}
