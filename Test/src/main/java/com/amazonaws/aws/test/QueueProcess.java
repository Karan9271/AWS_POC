package com.amazonaws.aws.test;

import java.util.ArrayList;
import java.util.List;

import com.amazonaws.AmazonClientException;
import com.amazonaws.AmazonServiceException;
import com.amazonaws.regions.Regions;
import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.sqs.AmazonSQS;
import com.amazonaws.services.sqs.AmazonSQSClientBuilder;
import com.amazonaws.services.sqs.model.DeleteMessageRequest;
import com.amazonaws.services.sqs.model.Message;
import com.amazonaws.services.sqs.model.ReceiveMessageRequest;
import com.amazonaws.services.sqs.model.SendMessageRequest;

public class QueueProcess {

	static AmazonSQS sqs = AmazonSQSClientBuilder.standard().withRegion(Regions.AP_SOUTH_1).build();

	static String data[] = new String[1];

	static String MessageId = "";

	static String QueueMessage = "";

	public static ArrayList<String> Process(String S3data, Context context) {
		ArrayList<String> messageCombinationList = new ArrayList<String>();
		try {

			String queueName = "CombineMatchQueue";

			// Send a message
			context.getLogger().log("Sending a message to " + queueName);

			data[0] = S3data;

			sqs.sendMessage(new SendMessageRequest(queueName, data[0]));

			// Receive messages
			context.getLogger().log("Receiving messages from " + queueName);
			ReceiveMessageRequest receiveMessageRequest = new ReceiveMessageRequest(queueName);
			List<Message> messages = sqs.receiveMessage(receiveMessageRequest).getMessages();
			for (Message message : messages) {
				QueueMessage = message.getBody();
				MessageId = message.getMessageId();

			}

			context.getLogger().log("The Message is : " + QueueMessage);
			QueueMessage = QueueMessage.replace("\n", "");
			QueueMessage = QueueMessage.replace("input", "");
			QueueMessage = QueueMessage.replace(":", "");
			QueueMessage = QueueMessage.replace("[", "");
			QueueMessage = QueueMessage.replace("]", "");
			QueueMessage = QueueMessage.replace("{", "");
			QueueMessage = QueueMessage.replace("}", "");
			QueueMessage = QueueMessage.replace(" ", "");
			QueueMessage = QueueMessage.replace(",", "");
			QueueMessage = QueueMessage.replace("\"", "");
			context.getLogger().log("The Processed Message is : " + QueueMessage);

			messageCombinationList = Combination(QueueMessage);

			context.getLogger().log("The Combinations are : \n" + messageCombinationList);

			// Delete a message
			context.getLogger().log("Deleting a message from " + queueName);
			String messageReceiptHandle = messages.get(0).getReceiptHandle();
			sqs.deleteMessage(new DeleteMessageRequest(queueName, messageReceiptHandle));

			Repository.Process(MessageId, messageCombinationList, context);

		} catch (AmazonServiceException ase) {
			context.getLogger().log("Caught an AmazonServiceException, which means your request made it "
					+ "to Amazon SQS, but was rejected with an error response for some reason.");
			context.getLogger().log("Error Message:    " + ase.getMessage());
			context.getLogger().log("HTTP Status Code: " + ase.getStatusCode());
			context.getLogger().log("AWS Error Code:   " + ase.getErrorCode());
			context.getLogger().log("Error Type:       " + ase.getErrorType());
			context.getLogger().log("Request ID:       " + ase.getRequestId());

		} catch (AmazonClientException ace) {
			context.getLogger()
					.log("Caught an AmazonClientException, which means the client encountered "
							+ "a serious internal problem while trying to communicate with SQS, such as not "
							+ "being able to access the network.");
			context.getLogger().log("Error Message: " + ace.getMessage());

		}
		return messageCombinationList;
	}

	private static ArrayList<String> Combination(String QueueMessage) {

		ArrayList<String> messageArrayList = new ArrayList<String>();

		String str = "";
		for (int i = 0; i < QueueMessage.length(); i++) {
			messageArrayList.add(str + QueueMessage.charAt(i));
		}
		for (int i = 0; i < QueueMessage.length(); i++) {
			for (int j = 0; j < QueueMessage.length(); j++) {
				if (i < j) {
					messageArrayList.add(str + QueueMessage.charAt(i) + QueueMessage.charAt(j));
				}
			}
		}

		for (int i = 0; i < QueueMessage.length(); i++) {
			for (int j = 0; j < QueueMessage.length(); j++) {
				for (int k = 0; k < QueueMessage.length(); k++) {
					if ((j == i + 1) && (k == (j + 1))) {
						messageArrayList
								.add(str + QueueMessage.charAt(i) + QueueMessage.charAt(j) + QueueMessage.charAt(k));
						break;
					}
				}
			}
		}

		messageArrayList.add(str + QueueMessage.charAt(QueueMessage.length() - 2)
				+ QueueMessage.charAt(QueueMessage.length() - 1) + QueueMessage.charAt(0));

		messageArrayList.add(
				str + QueueMessage.charAt(QueueMessage.length() - 1) + QueueMessage.charAt(0) + QueueMessage.charAt(1));

		messageArrayList.add(QueueMessage);

		return messageArrayList;
	}
}
