package org.eclipselabs.recommenders.ep.template.tests.example;

import junit.framework.Assert;

import org.eclipselabs.recommenders.ep.template.example.HelloService;
import org.junit.Test;

public class TestHelloService {

    @Test
    public void testHello() {
        final HelloService service = new HelloService();
        final String result = service.hello();

        Assert.assertEquals("Hello World", result);
    }
}
