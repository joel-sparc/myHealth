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
package com.sparc.myHealth.pcf.androidOut;

import static org.junit.Assert.assertEquals;

import org.apache.camel.CamelContext;
import org.apache.camel.EndpointInject;
import org.apache.camel.Exchange;
import org.apache.camel.Produce;
import org.apache.camel.ProducerTemplate;
import org.apache.camel.builder.AdviceWithRouteBuilder;
import org.apache.camel.component.mock.MockEndpoint;
import org.apache.camel.model.ModelCamelContext;
import org.apache.camel.test.junit4.CamelSpringJUnit4ClassRunner;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;

import ca.uhn.hl7v2.model.v26.segment.OBR;
import ca.uhn.hl7v2.model.v26.segment.OBX;
import ca.uhn.hl7v2.model.v26.segment.PID;
import ca.uhn.hl7v2.model.v26.datatype.TX;
import ca.uhn.hl7v2.model.v26.group.ORU_R01_OBSERVATION;
import ca.uhn.hl7v2.model.v26.group.ORU_R01_ORDER_OBSERVATION;
import ca.uhn.hl7v2.model.v26.message.ORU_R01;

@RunWith(CamelSpringJUnit4ClassRunner.class)
@ContextConfiguration({
	"classpath*:/META-INF/spring/camel-context.xml",
	"classpath*:/test-properties.xml"
	})
public class RouteTest {
	
	@Autowired(required=true)
	protected CamelContext camelContext;
	
	@Produce(uri = "HISBroker:queue://Consumer.PCF.VirtualTopic.OUT.HL7.PATIENT.1234")
	protected ProducerTemplate in;
	
	@EndpointInject(uri = "mock:out")
	protected MockEndpoint out;

	@Test
	public void testCamelRoute() throws Exception {
		
		out.expectedMessageCount(1);
		
		((ModelCamelContext)camelContext).getRouteDefinitions().get(0)
		.adviceWith(((ModelCamelContext)camelContext), new AdviceWithRouteBuilder() {
	        @Override
	        public void configure() throws Exception {
	            weaveById("recipients").remove();
	        }
	    });
		
		ORU_R01 msg = new ORU_R01();
		msg.initQuickstart("ORU", "R01", "T");
		PID pid = msg.getPATIENT_RESULT().getPATIENT().getPID();
		pid.getPatientIdentifierList(0).getIDNumber().setValue("1234");
		
		ORU_R01_ORDER_OBSERVATION orderObservation = msg.getPATIENT_RESULT().getORDER_OBSERVATION();
		
		OBR obr = orderObservation.getOBR();
		obr.getSetIDOBR().setValue("1");
		obr.getFillerOrderNumber().getEntityIdentifier().setValue("1000");
		obr.getFillerOrderNumber().getNamespaceID().setValue("LAB");
		obr.getUniversalServiceIdentifier().getIdentifier().setValue("87880");
		
		ORU_R01_OBSERVATION observation = orderObservation.getOBSERVATION(0);
		
		OBX obx = observation.getOBX();
		obx.getSetIDOBX().setValue("1");
		obx.getObservationIdentifier().getIdentifier().setValue("87880");
		obx.getObservationSubID().setValue("1");
		
		obx.getValueType().setValue("TX");
		TX tx = new TX(msg);
		tx.setValue("Negative");
		obx.getObservationValue(0).setData(tx);
		
		in.sendBody(msg.toString());
		
		
		//Ensure that the message is headed out
		MockEndpoint.assertIsSatisfied(out);
		
		//Ensure that the message has been correctly converted
		Exchange ex = out.getExchanges().get(0);
		assertEquals("ORU.R01?code=87880&text=Negative", String.valueOf(ex.getIn().getBody()));
		
		//Ensure that the patient header is correct
		assertEquals("1234", ex.getIn().getHeader("AHEPatient"));
		
	}

}
