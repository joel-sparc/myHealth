package com.sparc.myHealth.his;

import static org.junit.Assert.assertNotNull;

import java.io.IOException;

import javax.jms.Message;
import javax.jms.MessageConsumer;
import javax.jms.MessageProducer;
import javax.jms.Session;
import javax.jms.TemporaryQueue;

import org.hornetq.jms.client.HornetQTextMessage;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.switchyard.component.test.mixins.cdi.CDIMixIn;
import org.switchyard.component.test.mixins.hornetq.HornetQMixIn;
import org.switchyard.test.SwitchYardRunner;
import org.switchyard.test.SwitchYardTestCaseConfig;
import org.switchyard.test.SwitchYardTestKit;
import ca.uhn.hl7v2.HL7Exception;
import ca.uhn.hl7v2.model.v26.datatype.TX;
import ca.uhn.hl7v2.model.v26.message.ADT_A01;
import ca.uhn.hl7v2.model.v26.message.ORM_O01;
import ca.uhn.hl7v2.model.v26.message.ORU_R01;
import ca.uhn.hl7v2.model.v26.message.QRY_A19;
import ca.uhn.hl7v2.model.v26.segment.OBX;
import ca.uhn.hl7v2.model.v26.segment.PID;

/*
* Copyright 2013 Red Hat Inc. and/or its affiliates and other contributors.
*
* Licensed under the Apache License, Version 2.0 (the "License");
* you may not use this file except in compliance with the License.
* You may obtain a copy of the License at
* http://www.apache.org/licenses/LICENSE-2.0
* Unless required by applicable law or agreed to in writing, software
* distributed under the License is distributed on an "AS IS" BASIS,
* WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
* See the License for the specific language governing permissions and
* limitations under the License.
*/


/*
 * These tests will currently run as an app. Once we switch over to ActiveMQ they should
 * be able to run as tests with the SwitchYardRunner.
 */





@SwitchYardTestCaseConfig(config = SwitchYardTestCaseConfig.SWITCHYARD_XML, mixins = {
		CDIMixIn.class, HornetQMixIn.class })
// @RunWith(SwitchYardRunner.class)
public class HISTest {

//	private SwitchYardTestKit testKit;
//	private CDIMixIn cdiMixIn;
	
	HornetQMixIn hqMixIn;

	private static final String PATIENT_ID = "1";
	private static final String PATIENT_SURNAME = "Smith";
	private static final String PATIENT_QUERY_QUEUE_NAME = "PatientQuery";
	private static final String PATIENT_ADMIT_IN_TOPIC_NAME = "VirtualTopic.IN.HL7.ADT.A01";
	private static final String PATIENT_ADMIT_OUT_QUEUE_NAME = "Consumer.LIS.VirtualTopic.OUT.HL7.ADT.A01";
	private static final String OBSERVATION_REQUEST_IN_TOPIC_NAME = "VirtualTopic.IN.HL7.ORM.O01";
	private static final String OBSERVATION_REQUEST_OUT_QUEUE_NAME = "Consumer.LIS.VirtualTopic.OUT.HL7.ORM.O01";
	private static final String OBSERVATION_RESULT_IN_TOPIC_NAME = "VirtualTopic.IN.HL7.ORU.R01";
	private static final String OBSERVATION_RESULT_OUT_QUEUE_NAME = "Consumer.PCF.VirtualTopic.OUT.HL7.PATIENT." + PATIENT_ID;

	private static final String USR = "guest";
	private static final String PWD = "guestp.1";
	
	public static void main(String[] args){
		HISTest test = new HISTest();
		try {
			test.hqMixIn = new HornetQMixIn(false);
			test.hqMixIn.initialize();
			test.testPatientAdmit();
			test.hqMixIn.uninitialize();
			
			test.hqMixIn = new HornetQMixIn(false);
			test.hqMixIn.initialize();
			test.testPatientQuery();
			test.hqMixIn.uninitialize();
			
			test.hqMixIn = new HornetQMixIn(false);
			test.hqMixIn.initialize();
			test.testObservationRequest();
			test.hqMixIn.uninitialize();
			
			test.hqMixIn = new HornetQMixIn(false);
			test.hqMixIn.initialize();
			test.testObservationResult();
			test.hqMixIn.uninitialize();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
//	@Test
    public void testObservationRequest() throws Exception {
		
		hqMixIn.setUser(USR)
				.setPassword(PWD);
		
		
		Session session = null;
		try {
			session = hqMixIn.createJMSSession();
			
			final MessageProducer producer = session.createProducer(
					HornetQMixIn.getJMSTopic(OBSERVATION_REQUEST_IN_TOPIC_NAME));
			
			MessageConsumer ancillaryService = session.createConsumer(
					HornetQMixIn.getJMSQueue(OBSERVATION_REQUEST_OUT_QUEUE_NAME));
			
			 
			Message message = hqMixIn.createJMSMessage(buildObservationRequest());
			
			producer.send(message);
			
			HornetQTextMessage response = (HornetQTextMessage)ancillaryService.receive(3000);
			
			if(response == null){
				System.out.println("The observation request message was not received by ancillary services");
			} else {
				System.out.println("The observation request message was received by ancillary services: "
						+ response.getText());
			}
			
			assertNotNull(response);
			
		} finally {
			session.close();
		}
	}
	
//	@Test
    public void testObservationResult() throws Exception {
		
		hqMixIn.setUser(USR)
				.setPassword(PWD);

		Session session = null;
		try {
			session = hqMixIn.createJMSSession();
			
			final MessageProducer producer = session.createProducer(
					HornetQMixIn.getJMSTopic(OBSERVATION_RESULT_IN_TOPIC_NAME));
			
			MessageConsumer ancillaryService = session.createConsumer(
					HornetQMixIn.getJMSQueue(OBSERVATION_RESULT_OUT_QUEUE_NAME));
			
			 
			Message message = hqMixIn.createJMSMessage(buildObservationResult());
			
			producer.send(message);
			
			HornetQTextMessage response = (HornetQTextMessage)ancillaryService.receive(3000);
			
			if(response == null){
				System.out.println("The observation result message was not received by patient care facilities");
			} else {
				System.out.println("The observation result message was received by patient care facilities: "
						+ response.getText());
			}
			
			assertNotNull(response);
			
		} finally {
			session.close();
		}
	}

//	@Test
    public void testPatientQuery() throws Exception {
		
		hqMixIn.setUser(USR)
				.setPassword(PWD);

		Session session = null;
		try {
			session = hqMixIn.createJMSSession();
			
			final MessageProducer producer = session.createProducer(
					HornetQMixIn.getJMSQueue(PATIENT_QUERY_QUEUE_NAME));
			
			TemporaryQueue replyQueue = session.createTemporaryQueue();
			
	        MessageConsumer replyConsumer = session.createConsumer(replyQueue);
			
			Message message = hqMixIn.createJMSMessage(buildPatientQuery());
			
			message.setJMSReplyTo(replyQueue);
			
			producer.send(message);
			
			HornetQTextMessage response = (HornetQTextMessage)replyConsumer.receive(3000);
			
			if(response == null){
				System.out.println("No patient query response was received by the patient care facility");
			} else {
				System.out.println("Patient query response received by the patient care facility: " + response.getText());
			}
			
			assertNotNull(response);
			
		} finally {
			session.close();
		}
	}
	
//	@Test
    public void testPatientAdmit() throws Exception {
		
		hqMixIn.setUser(USR)
				.setPassword(PWD);

		Session session = null;
		try {
			session = hqMixIn.createJMSSession();
			
			final MessageProducer producer = session.createProducer(
					HornetQMixIn.getJMSTopic(PATIENT_ADMIT_IN_TOPIC_NAME));
			
			MessageConsumer ancillaryService = session.createConsumer(
					HornetQMixIn.getJMSQueue(PATIENT_ADMIT_OUT_QUEUE_NAME));
			
			 
			Message message = hqMixIn.createJMSMessage(buildPatientAdmit());
			
			producer.send(message);
			
			HornetQTextMessage response = (HornetQTextMessage)ancillaryService.receive(1000);
			
			if(response == null){
				System.out.println("The admit message was not received by ancillary services");
			} else {
				System.out.println("The admit message was received by ancillary services: "
						+ response.getText());
			}
			
			assertNotNull(response);
			
			//Sleep a bit to ensure that the db has been updated
			Thread.sleep(1000);
			
		} finally {
			session.close();
		}
	}
	
	private static String buildPatientAdmit(){
		ADT_A01 patientAdmit = new ADT_A01();
		try {
			patientAdmit.initQuickstart("ADT", "A01", "T");
			
			patientAdmit.getPID().getPatientIdentifierList(0).getIDNumber().setValue(PATIENT_ID);
			patientAdmit.getPID().getPatientName(0).getFamilyName().getSurname().setValue(PATIENT_SURNAME);
			
			return patientAdmit.toString();
		
		} catch (HL7Exception e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return null;
		
	}
	
	private static String buildPatientQuery(){
		QRY_A19 patientLookup = new QRY_A19();
		try {
			patientLookup.initQuickstart("QRY", "A19", "T");
			
			patientLookup.getQRD().getWhoSubjectFilter(0).getIDNumber().setValue(PATIENT_ID);
			
			return patientLookup.toString();
		
		} catch (HL7Exception e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return null;
		
	}
	
	private static String buildObservationRequest(){
		ORM_O01 observationRequest = new ORM_O01();
		try {
			observationRequest.initQuickstart("ORM", "O01", "T");
			
			PID pid = observationRequest.getPATIENT().getPID(); 
			pid.getPatientName(0).getFamilyName().getSurname().setValue(PATIENT_SURNAME);
			pid.getPatientIdentifierList(0).getIDNumber().setValue(PATIENT_ID);
			
			observationRequest.getORDER().getORDER_DETAIL().getOBR()
			.getSetIDOBR().setValue("1");

			observationRequest.getORDER().getORDER_DETAIL().getOBR()
			.getFillerOrderNumber().getNamespaceID()
			.setValue("LAB");

			observationRequest.getORDER().getORDER_DETAIL().getOBR()
			.getFillerOrderNumber().getEntityIdentifier()
			.setValue("87880");
			
			return observationRequest.toString();
		
		} catch (HL7Exception e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return null;
		
	}
	
	private static String buildObservationResult(){
		ORU_R01 observationResult = new ORU_R01();
		try {
			observationResult.initQuickstart("ORU", "R01", "T");
			
			PID pid = observationResult.getPATIENT_RESULT().getPATIENT().getPID();
			pid.getPatientName(0).getFamilyName().getSurname().setValue(PATIENT_SURNAME);
			pid.getPatientIdentifierList(0).getIDNumber().setValue(PATIENT_ID);
			
			OBX obx = observationResult.getPATIENT_RESULT().getORDER_OBSERVATION().getOBSERVATION().getOBX();
			obx.getSetIDOBX().setValue("1");
			obx.getObservationIdentifier().getIdentifier().setValue("87880");
			obx.getObservationSubID().setValue("1");
			
			obx.getValueType().setValue("TX");
			TX tx = new TX(observationResult);
			tx.setValue("Negative");
			obx.getObservationValue(0).setData(tx);
			
			return observationResult.toString();
		
		} catch (HL7Exception e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return null;
		
	}

}
