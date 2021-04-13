package com.amazonaws.project2.instancedata;

import java.net.URI;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.amazonaws.AmazonClientException;
import com.amazonaws.SdkClientException;
import com.amazonaws.internal.EC2CredentialsUtils;
import com.google.gson.GsonBuilder;

/**
 * This application is designed to run on an EC2 instance with metadata service
 * enabled. Given a path to query, it would retrieve children elements of that
 * path for further querying as needed. This program is not designed to invoke
 * the path recursively as it could potentially lead to throttling. On the EC2
 * instance, you need to enable JAVA to run this program and also AWS SDK for
 * JAVA
 *
 */
public class RetrieveInstanceData {
	private static final String HOST_ADDRESS = "http://169.254.169.254/";
	private static final Log log = LogFactory.getLog(RetrieveInstanceData.class);
	private static final int DEFAULT_QUERY_RETRIES = 3;
	private static final int _RETRIES = 2;
	private static final int MINIMUM_RETRY_WAIT_TIME_MILLISECONDS = 500;

	/**
	 * Main method to bootstrap the project
	 * 
	 * @param args expects a single argument that is the path to retrieve the values
	 *             for example 'latest/meta-data'
	 */
	public static void main(String[] args) {

		if (args.length == 0) {
			System.out.println("Usage: provide a single parameter for the path. Should start with 'latest/meta-data'");
			System.exit(0);
		}

		String JSON = getMetaData(args[0], _RETRIES);

		System.out.println(JSON);
	}

	/**
	 * This method retrieves the keys for the given path from the metadata URL, with
	 * configurable retries
	 * 
	 * @param path  is path to be queries upon
	 * @param tries is the number retries you want to configure
	 * @return a formated JSON string with all the value(s) found for the path being
	 *         queried
	 */
	public static String getMetaData(String path, int tries) {
		if (tries <= 0 || tries >= 5)
			throw new SdkClientException("Retries must be greater than 0 and less than 5");

		List<String> items;
		try {
			String response = EC2CredentialsUtils.getInstance().readResource(new URI(HOST_ADDRESS + path));
			items = Arrays.asList(response.split("\n"));
			Map<String, List<String>> itemsMap = new HashMap<String, List<String>>();
			itemsMap.put(path, items);
			// Generated a pretty string and returns it
			return new GsonBuilder().setPrettyPrinting().create().toJson(itemsMap);
		} catch (AmazonClientException e) {
			log.error("Something went wrong; Check the server connection and path");
			throw new CustomException("Check the server connection", e);
		} catch (Exception e) {
			// Retry on any other exceptions
			int pause = (int) (Math.pow(2, DEFAULT_QUERY_RETRIES - tries) * MINIMUM_RETRY_WAIT_TIME_MILLISECONDS);
			try {
				Thread.sleep(
						pause < MINIMUM_RETRY_WAIT_TIME_MILLISECONDS ? MINIMUM_RETRY_WAIT_TIME_MILLISECONDS : pause);
			} catch (InterruptedException e1) {
				Thread.currentThread().interrupt();
			}
			return getMetaData(path, tries - 1);
		}
	}
}