package com.amazonaws.aws.test;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import com.amazonaws.AmazonClientException;
import com.amazonaws.AmazonServiceException;
import com.amazonaws.services.dynamodbv2.AmazonDynamoDB;
import com.amazonaws.services.dynamodbv2.AmazonDynamoDBClientBuilder;
import com.amazonaws.services.dynamodbv2.model.AttributeValue;
import com.amazonaws.services.dynamodbv2.model.DescribeTableRequest;
import com.amazonaws.services.dynamodbv2.model.PutItemRequest;
import com.amazonaws.services.dynamodbv2.model.PutItemResult;
import com.amazonaws.services.dynamodbv2.model.TableDescription;
import com.amazonaws.services.lambda.runtime.Context;

public class Repository {

	static AmazonDynamoDB dynamoDB;

	public static void Process(String MessageId, ArrayList<String> messageArrayList, Context context) {
		try {

			dynamoDB = AmazonDynamoDBClientBuilder.standard().withRegion("ap-south-1").build();
			String tableName = "Combinations";

			// Describe our new table
			DescribeTableRequest describeTableRequest = new DescribeTableRequest().withTableName(tableName);
			TableDescription tableDescription = dynamoDB.describeTable(describeTableRequest).getTable();

			// Add another item
			Map<String, AttributeValue> item = newItem(MessageId, messageArrayList);
			PutItemRequest putItemRequest = new PutItemRequest(tableName, item);
			PutItemResult putItemResult = dynamoDB.putItem(putItemRequest);
			putItemRequest = new PutItemRequest(tableName, item);
			putItemResult = dynamoDB.putItem(putItemRequest);

			context.getLogger().log("The Result is successfully stored in table : " + tableName);

		} catch (AmazonServiceException ase) {
			context.getLogger().log("Caught an AmazonServiceException, which means your request made it "
					+ "to AWS, but was rejected with an error response for some reason.");
			context.getLogger().log("Error Message:    " + ase.getMessage());
			context.getLogger().log("HTTP Status Code: " + ase.getStatusCode());
			context.getLogger().log("AWS Error Code:   " + ase.getErrorCode());
			context.getLogger().log("Error Type:       " + ase.getErrorType());
			context.getLogger().log("Request ID:       " + ase.getRequestId());
		} catch (AmazonClientException ace) {
			context.getLogger()
					.log("Caught an AmazonClientException, which means the client encountered "
							+ "a serious internal problem while trying to communicate with AWS, "
							+ "such as not being able to access the network.");
			context.getLogger().log("Error Message: " + ace.getMessage());
		}
	}

	private static Map<String, AttributeValue> newItem(String MessageId, ArrayList<String> messageArrayList) {
		Map<String, AttributeValue> item = new HashMap<String, AttributeValue>();
		item.put("MessageId", new AttributeValue(MessageId));
		item.put("Response", new AttributeValue(messageArrayList));

		return item;
	}

}
