package com.Local_aplication.managers;
import java.util.*;
import com.amazonaws.services.sqs.AmazonSQS;
import com.amazonaws.services.sqs.AmazonSQSClientBuilder;
import com.amazonaws.services.sqs.model.*;


public class SQSManager extends BaseManager {
    AmazonSQS sqs = AmazonSQSClientBuilder.standard()
            .withRegion("us-east-2")
            .build();

    public String create_queue(String queue_name) {
        logger.config("entry");
        try {
            logger.info("Creating a new SQS queue called " + queue_name);
            CreateQueueRequest createQueueRequest = new CreateQueueRequest(queue_name + UUID.randomUUID());
            String answer = sqs.createQueue(createQueueRequest).getQueueUrl();
            logger.config("returned " + answer);
            return answer;
        } catch (Exception exc) {
            handle_exception(exc);
            return null;
        }
    }

    public List<String> list_queues() {
        logger.config("entry");
        try {
            List<String> answer = new ArrayList<String>(sqs.listQueues().getQueueUrls());
            logger.config("returned " + answer);
            return answer;
        } catch (Exception exc) {
            handle_exception(exc);
            return null;
        }
    }

    public SendMessageResult send_message(String queue_url, String message, String request_id, String consumer) {
        logger.config("entry");
        try {
            logger.info(String.format("Sending a message: %s to queue: %s with request_id: %s and consumer: %s",
                    message, queue_url, request_id, consumer));
            final Map<String, MessageAttributeValue> messageAttributes = new HashMap<>();
            messageAttributes.put("request_id", new MessageAttributeValue()
                    .withDataType("String")
                    .withStringValue(request_id));
            messageAttributes.put("consumer", new MessageAttributeValue()
                    .withDataType("String")
                    .withStringValue(consumer));

            SendMessageRequest req = new SendMessageRequest(queue_url, message)
                    .withMessageAttributes(messageAttributes);
            SendMessageResult answer = sqs.sendMessage(req);
            logger.config("returned " + answer);
            return answer;
        } catch (Exception exc) {
            handle_exception(exc);
            return null;
        }

    }

    public List<Message> recieve_messages(String queue_url, String request_id, String consumer) {
        logger.config("entry");
        try {
            logger.info(String.format("Receiving messages from bucket: %s with request_id: %s and consumer: %s"
                    , queue_url, request_id, consumer));
            ReceiveMessageRequest receiveMessageRequest = new ReceiveMessageRequest(queue_url)
                    .withAttributeNames(Arrays.asList(request_id, consumer));
            List<Message> answer = sqs.receiveMessage(receiveMessageRequest).getMessages();
            logger.config("returned " + answer);
            return answer;
        } catch (Exception exc) {
            handle_exception(exc);
            return null;
        }
    }

    public DeleteMessageResult delete_message(String queue_url, Message message) {
        try {
            logger.config("entry");
            logger.info(String.format("Deleting message %s from queue %s",
                    message.getMessageId(), queue_url));
            final String messageReceiptHandle = message.getReceiptHandle();
            DeleteMessageResult answer = sqs.deleteMessage(new DeleteMessageRequest(queue_url, messageReceiptHandle));
            logger.config("returned " + answer);
            return answer;
        } catch (Exception exc) {
            handle_exception(exc);
            return null;
        }
    }

    public DeleteQueueResult delete_queue(String queue_url) {
        try {
            logger.config("entry");
            logger.info(String.format("Deleting queue %s", queue_url));
            DeleteQueueResult answer = sqs.deleteQueue(new DeleteQueueRequest(queue_url));
            logger.config("returned " + answer);
            return answer;

        } catch (Exception exc) {
            handle_exception(exc);
            return null;
        }
    }
}
