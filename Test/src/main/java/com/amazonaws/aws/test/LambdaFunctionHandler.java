package com.amazonaws.aws.test;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;

import com.amazonaws.AmazonClientException;
import com.amazonaws.AmazonServiceException;
import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.RequestHandler;
import com.amazonaws.services.lambda.runtime.events.S3Event;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3ClientBuilder;
import com.amazonaws.services.s3.model.GetObjectRequest;
import com.amazonaws.services.s3.model.S3Object;

public class LambdaFunctionHandler implements RequestHandler<S3Event, String> {

	ArrayList<String> process = new ArrayList<String>();

	@Override
	public String handleRequest(S3Event event, Context context) {

		AmazonS3 s3 = AmazonS3ClientBuilder.standard().withRegion("ap-south-1").build();

		String bucketName = event.getRecords().get(0).getS3().getBucket().getName();
		String key = event.getRecords().get(0).getS3().getObject().getKey();
		String displaySQSInputStream = "";
		context.getLogger().log("Received file : " + key + " from Bucket name : " + bucketName);
		try {

			S3Object object = s3.getObject(new GetObjectRequest(bucketName, key));
			BufferedReader reader = new BufferedReader(new InputStreamReader(object.getObjectContent()));

			try {
				displaySQSInputStream = reader.readLine();
				context.getLogger().log(key + " file contents is : \n" + displaySQSInputStream);
				process = QueueProcess.Process(displaySQSInputStream, context);
			} catch (IOException e) {
				e.printStackTrace();
			}

		} catch (AmazonServiceException ase) {
			context.getLogger().log("Caught an AmazonServiceException, which means your request made it "
					+ "to Amazon S3, but was rejected with an error response for some reason.");
			context.getLogger().log("Error Message:    " + ase.getMessage());
			context.getLogger().log("HTTP Status Code: " + ase.getStatusCode());
			context.getLogger().log("AWS Error Code:   " + ase.getErrorCode());
			context.getLogger().log("Error Type:       " + ase.getErrorType());
			context.getLogger().log("Request ID:       " + ase.getRequestId());
		} catch (AmazonClientException ace) {
			context.getLogger()
					.log("Caught an AmazonClientException, which means the client encountered "
							+ "a serious internal problem while trying to communicate with S3, "
							+ "such as not being able to access the network.");
			context.getLogger().log("Error Message: " + ace.getMessage());
		}

		return "Object Name : " + key;
	}

}
