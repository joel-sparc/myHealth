/*
* Copyright 2013 FuseSource
*
* Licensed under the Apache License, Version 2.0 (the "License");
* you may not use this file except in compliance with the License.
* You may obtain a copy of the License at
*
* http://www.apache.org/licenses/LICENSE-2.0
*
* Unless required by applicable law or agreed to in writing, software
* distributed under the License is distributed on an "AS IS" BASIS,
* WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
* See the License for the specific language governing permissions and
* limitations under the License.
*/
package com.sparc.myHealth.pcf.hl7In;

import static org.junit.Assert.assertEquals;

import org.apache.camel.CamelContext;
import org.apache.camel.EndpointInject;
import org.apache.camel.Exchange;
import org.apache.camel.Produce;
import org.apache.camel.ProducerTemplate;
import org.apache.camel.component.mock.MockEndpoint;
import org.apache.camel.test.junit4.CamelSpringJUnit4ClassRunner;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;

@RunWith(CamelSpringJUnit4ClassRunner.class)
@ContextConfiguration({
	"classpath*:/META-INF/spring/camel-context.xml",
	"classpath*:/test-properties.xml"
	})
public class RouteTest {
	
	@Autowired(required=true)
	protected CamelContext camelContext;

	protected Object[] expectedBodies = {
			"MSH|^~\\&|||||20131024132816.565-0400||ADT^A01^ADT_A01|301|T|2.6" };
	
	@Produce(uri = "mina:tcp://localhost:8888?sync=true&codec=#hl7codec")
	protected ProducerTemplate mina;
	
	@EndpointInject(uri="mock:fromVT")
	private MockEndpoint fromVT;

	@Test
	public void testCamelRoute() throws Exception {
		
		camelContext.addComponent("HISBroker", camelContext.getComponent("seda"));

		fromVT.expectedMessageCount(1);
		
		for (Object expectedBody : expectedBodies) {
			mina.sendBody(expectedBody);
		}

		MockEndpoint.assertIsSatisfied(fromVT);
		
		Exchange ex = fromVT.getExchanges().get(0);
		assertEquals(ex.getIn().getHeader("JMSDestination").toString(),"topic://VirtualTopic.IN.HL7.ADT.A01");
	}

}
