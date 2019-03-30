package com.Local_aplication.common;

import com.Local_aplication.managers.EC2Manager;

import java.util.List;


public class common {

    //    SQS message types
    public static final String new_task = "new task";
    public static final String done_task = "done task";
    public static final String new_pdf_task = "new pdf task";
    public static final String done_pdf_task = "done pdf task";
    public static final String terminate_task = "terminate";

    public static String generate_new_task_message(String client_id, String key, int number_of_instances_per_lines){
        return String.format("%s\t%s\t%s\t%s", common.new_task, client_id, key, String.valueOf(number_of_instances_per_lines));
    }
    //    consumer types
    public static String manager_main_thread_consumer = "MANAGER-MAIN_THREAD";
    public static String client_consumer= "CLIENT";
    public static String worker_consumer= "WORKER";

//    queues url
    public static String clients_queue_url = "https://sqs.us-east-2.amazonaws.com/606249488880/clients-queue.fifo";
    public static String manager_queue_url = "https://sqs.us-east-2.amazonaws.com/606249488880/manager-queue.fifo";
    public static String worker_queue_url = "https://sqs.us-east-2.amazonaws.com/606249488880/worker-queue.fifo";
}
