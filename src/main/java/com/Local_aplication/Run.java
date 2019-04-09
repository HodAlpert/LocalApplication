package com.Local_aplication;

import com.Local_aplication.common.common;
import com.Local_aplication.common.init;
import com.Local_aplication.managers.EC2Manager;
import com.Local_aplication.managers.HTMLManager;
import com.Local_aplication.managers.S3Manager;
import com.Local_aplication.managers.SQSManager;
import com.amazonaws.services.sqs.model.Message;

import java.util.UUID;
import java.util.logging.Logger;

public class Run {

    private EC2Manager ec2 = new EC2Manager();
    private S3Manager s3 = new S3Manager();
    private SQSManager sqs = new SQSManager();
    private boolean should_terminate = false;
    private String input_file;
    private String output_file;
    private int number_of_instances_per_lines = 10;
    private String client_id = String.valueOf(UUID.randomUUID());
    private Logger logger = init.logger;

    Run() {
        init.main();
    }

    public void main(String[] args) {
        initialize_arguments(args);
        logger.info(String.format("uploading file: %s", input_file));
        String key = s3.upload_object(input_file);
        logger.info("sending message to manager");
        sqs.send_message(common.manager_queue_url, common.generate_new_task_message(client_id, key, number_of_instances_per_lines),
                client_id,
                common.manager_main_thread_consumer);
        logger.info("waiting for manager to finish");
        Message message = wait_for_message_from_manager();
        logger.info("got response from manager");
        handle_message_from_manager(message);
        send_termination_message_if_needed();
    }

    /**
     * checks if 'terminate' command was given, if it was- send terminate sqs message to manager
     */
    private void send_termination_message_if_needed() {
        if (should_terminate) {
            logger.info("sending termination message");
            sqs.send_message(common.manager_queue_url, String.format("%s\t%s", common.terminate_task, client_id)
                    , client_id,
                    common.manager_main_thread_consumer);
        }
    }

    /**
     * download the file in message body from s3 and deletes the message
     *
     * @param message to handle
     */
    private void handle_message_from_manager(Message message) {
        String S3_key = message.getBody().split("\t")[2];
        logger.info("downloading file from s3");
        s3.download_file_as_text(S3_key, S3_key);
        logger.info("downloaded output.txt from manager");
        sqs.delete_message(common.clients_queue_url, message);
        new HTMLManager(S3_key, output_file).build_html_file();
    }

    /**
     * @return Message returned from manager if type and client id are matching
     */
    private Message wait_for_message_from_manager() {
        Message message = null;
        while (message == null) {
            message = sqs.recieve_message(common.clients_queue_url, client_id, common.client_consumer);
            if (message != null) {
                String[] parsed_body = message.getBody().split("\t");
                String type = parsed_body[0];
                String tmp_client_id = parsed_body[1];
                if (!type.equals(common.done_task) || !client_id.equals(tmp_client_id))
                    message = null;
            }
            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        return message;
    }

    /**
     * parses and store input_file, number_of_instances_per_lines and terminate
     *
     * @param args list of command line arguments
     */
    private void initialize_arguments(String[] args) {
        power_up_manager_if_needed();
        input_file = args[0];
        output_file = args[1];
        number_of_instances_per_lines = Integer.parseInt(args[1]);
        if (args.length == 4 && args[3].equals("terminate"))
            should_terminate = true;
    }

    /**
     * if manager is not in state running- start the manager.
     */
    private void power_up_manager_if_needed() {
        if (!ec2.is_manager_up()) {
            ec2.power_up_manager();
        }
    }
}
