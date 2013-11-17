package com.sparc.myHealth.pcf.androidIn;


import static org.junit.Assert.assertEquals;

import org.apache.camel.CamelContext;
import org.apache.camel.EndpointInject;
import org.apache.camel.Exchange;
import org.apache.camel.Processor;
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

import ca.uhn.hl7v2.model.v26.message.ADR_A19;
import ca.uhn.hl7v2.model.v26.message.ORM_O01;
import ca.uhn.hl7v2.model.v26.message.QRY_A19;
import ca.uhn.hl7v2.model.v26.segment.PID;
import ca.uhn.hl7v2.parser.PipeParser;


@RunWith(CamelSpringJUnit4ClassRunner.class)
@ContextConfiguration({
	"classpath*:/META-INF/spring/camel-context.xml",
	"classpath*:/test-properties.xml"
	})
public class RouteTest {
	
	private final String PATIENT_ID = "1234";
	private final String PATIENT_SURNAME = "Smith";

	@Autowired(required=true)
	protected CamelContext camelContext;

	protected Object[] expectedBodies = {
			//87880 is a LOINC object identifier that represents a type of rapid strep test
			"ORM.O01?code=87880" };
	
	
	@Produce(uri = "embeddedBroker:queue://Consumer.PCF.VirtualTopic.IN.AHE.PATIENT.1234")
	protected ProducerTemplate in;
	
	@EndpointInject(uri = "mock:patientLookupService")
	protected MockEndpoint patientLookup;
	
	@EndpointInject(uri = "mock:out")
	protected MockEndpoint out;

	@Test
	public void testCamelRoute() throws Exception {
		
		
		out.expectedMessageCount(1);
		
		//simulated patient lookup service
		patientLookup.whenAnyExchangeReceived(new Processor(){

			@Override
			public void process(Exchange exchange) throws Exception {
				String lookupRequest = String.valueOf(exchange.getIn().getBody());
				
				PipeParser pipeParser = new PipeParser();
				QRY_A19 patientLookupRequest = (QRY_A19) pipeParser.parse(lookupRequest);
				String requestedPatientId = patientLookupRequest.getQRD().getWhoSubjectFilter(0).getIDNumber().getValue();
				
				ADR_A19 patientLookupResponse = new ADR_A19();
				patientLookupResponse.initQuickstart("ADR", "A19", "T");
				
				patientLookupResponse.getQUERY_RESPONSE().getPID()
					.getPatientIdentifierList(0).getIDNumber().setValue(requestedPatientId);
				
				patientLookupResponse.getQUERY_RESPONSE().getPID()
					.getPatientName(0).getFamilyName().getSurname().setValue(PATIENT_SURNAME);
				
				exchange.getIn().setBody(patientLookupResponse.toString());
			}
			
		});
		
		((ModelCamelContext)camelContext).getRouteDefinitions().get(0)
		.adviceWith(((ModelCamelContext)camelContext), new AdviceWithRouteBuilder() {
	        @Override
	        public void configure() throws Exception {
	            weaveById("recipients").remove();
	        }
	    });
		
		((ModelCamelContext)camelContext).getRouteDefinitions().get(2)
		.adviceWith(((ModelCamelContext)camelContext), new AdviceWithRouteBuilder() {
	        @Override
	        public void configure() throws Exception {
	            weaveById("patientQuery").remove();
	        }
	    });
		
		
		
		for (Object expectedBody : expectedBodies) {
			in.sendBody(expectedBody);
		}
		
		//First ensure that the message is headed out
		MockEndpoint.assertIsSatisfied(out);
		
		//Now ensure that the enriched ORM message contains the surname and id that we
		//pulled from the lookup service
		Exchange ex = out.getExchanges().get(0);
		PipeParser pipeParser = new PipeParser();
		ORM_O01 orderRequest = (ORM_O01) pipeParser.parse(String.valueOf(ex.getIn().getBody()));
		
		PID pid = orderRequest.getPATIENT().getPID();
		String orderRequestID = pid.getPatientIdentifierList(0).getIDNumber().getValue();
		
		String orderRequestSurname = pid
					.getPatientName(0).getFamilyName().getSurname().getValue();
		
		assertEquals(orderRequestID, PATIENT_ID);
		assertEquals(orderRequestSurname, PATIENT_SURNAME);
	}

}
