package com.mkyong.test;

import com.mkyong.test.core.Test;
import com.mkyong.test.core.TesterInfo;

@TesterInfo(
	priority = TesterInfo.Priority.HIGH,
	createdBy = "mkyong.com",  
	tags = {"sales","test" }
)
public class TestExample {

	@Test(enabled = false)
	void testA() {
	  if (true)
		throw new RuntimeException("This test always failed");
	}

	@Test(enabled = true)
	void testB() {
	  if (false)
		throw new RuntimeException("This test always passed");
	}

	@Test(enabled = true)
	void testC() {
	  if (10 > 1) {
		// do nothing, this test always passed.
	  }
	}

}

