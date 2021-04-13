package com.amazonaws.project2.instancedata.test;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;

import com.amazonaws.project2.instancedata.RetrieveInstanceData;
import com.amazonaws.SdkClientException;
import com.amazonaws.project2.instancedata.CustomException;

public class RetrieveInstanceDataTests {

	@Test
	public void validPathStringAndRetries() {
		String json = RetrieveInstanceData.getMetaData("latest/meta-data", 2);
		assertTrue(json.indexOf("hostname") > 1);
	}

	@Test
	public void invalidPathAndValidRetries() {
		assertThrows(CustomException.class, () -> {
			RetrieveInstanceData.getMetaData("latest/meta-d", 2);
		});
	}

	@Test
	public void nullPathAndValidRetries() {
		assertThrows(CustomException.class, () -> {
			RetrieveInstanceData.getMetaData(null, 2);
		});
	}

	@Test
	public void validPathAndinvalidRetries1() {
		assertThrows(SdkClientException.class, () -> {
			RetrieveInstanceData.getMetaData(null, 0);
		});
	}

	@Test
	public void validPathAndinvalidRetries2() {
		assertThrows(SdkClientException.class, () -> {
			RetrieveInstanceData.getMetaData(null, 5);
		});
	}
}
