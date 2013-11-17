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
package com.sparc.myHealth.lis.hl7Handler;

import static org.junit.Assert.assertEquals;

import java.io.IOException;

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

import ca.uhn.hl7v2.HL7Exception;
import ca.uhn.hl7v2.model.v26.message.ORM_O01;
import ca.uhn.hl7v2.model.v26.message.ORU_R01;
import ca.uhn.hl7v2.model.v26.segment.OBX;
import ca.uhn.hl7v2.model.v26.segment.PID;
import ca.uhn.hl7v2.parser.PipeParser;

@RunWith(CamelSpringJUnit4ClassRunner.class)
@ContextConfiguration({
	"classpath*:/META-INF/spring/camel-context.xml",
	"classpath*:/test-properties.xml"
	})
public class RouteTest {
	private static final String strep = "87880";
	private static final String patientId = "1";
	private static final String surname = "Smith";
	
	@Autowired(required=true)
	protected CamelContext camelContext;
	
	@Produce(uri = "HISBroker:queue://Consumer.LIS.VirtualTopic.OUT.HL7.ORM.O01")
	protected ProducerTemplate observationRequest;
	
	@EndpointInject(uri="mock:observationResult")
	private MockEndpoint mockObservationResult;

	@Test
	public void testCamelRoute() throws Exception {
		
		mockObservationResult.expectedMessageCount(1);
		
		((ModelCamelContext)camelContext).getRouteDefinitions().get(1)
			.adviceWith((ModelCamelContext)camelContext, new AdviceWithRouteBuilder() {
	        @Override
	        public void configure() throws Exception {
	            weaveById("observationResultOut").remove();
	        }
	    });
		
		observationRequest.sendBody(buildObservationRequest());

		MockEndpoint.assertIsSatisfied(mockObservationResult);
		
		Exchange ex = mockObservationResult.getExchanges().get(0);
		ORU_R01 observationResult = (ORU_R01) new PipeParser().parse(ex.getIn().getBody(String.class));
		
		PID pid = observationResult.getPATIENT_RESULT().getPATIENT().getPID();
		assertEquals(surname,pid.getPatientName(0).getFamilyName().getSurname().getValue());
		assertEquals(patientId,pid.getPatientIdentifierList(0).getIDNumber().getValue());
		
		OBX obx = observationResult.getPATIENT_RESULT().getORDER_OBSERVATION().getOBSERVATION().getOBX();
		obx.getSetIDOBX().setValue("1");
		assertEquals(strep,obx.getObservationIdentifier().getIdentifier().getValue());
		
	}
	
	private static String buildObservationRequest(){
		ORM_O01 observationRequest = new ORM_O01();
		try {
			observationRequest.initQuickstart("ORM", "O01", "T");
			
			PID pid = observationRequest.getPATIENT().getPID(); 
			pid.getPatientName(0).getFamilyName().getSurname().setValue(surname);
			pid.getPatientIdentifierList(0).getIDNumber().setValue(patientId);
			
			observationRequest.getORDER().getORDER_DETAIL().getOBR()
			.getSetIDOBR().setValue("1");

			observationRequest.getORDER().getORDER_DETAIL().getOBR()
			.getFillerOrderNumber().getNamespaceID()
			.setValue("LAB");

			observationRequest.getORDER().getORDER_DETAIL().getOBR()
			.getFillerOrderNumber().getEntityIdentifier()
			.setValue(strep);
			
			return observationRequest.toString();
		
		} catch (HL7Exception e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return null;
		
	}

}
