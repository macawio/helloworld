package io.zolontech.helloworld.test;

import org.junit.Test;
import org.junit.Assert;

import io.zolontech.helloworld.impl.Helloworld;

public class HelloworldTest {

	@Test
	public void testSayHello() {
		final Helloworld service = new Helloworld();
		final String message = service.sayHello();
		Assert.assertNotNull("Helloworld service returned a null message", message);
		Assert.assertEquals("Unexpected hello-world message", "Hello, World!", message);
	}
}