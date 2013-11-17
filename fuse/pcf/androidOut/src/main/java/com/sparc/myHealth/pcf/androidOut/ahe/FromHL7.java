package com.sparc.myHealth.pcf.androidOut.ahe;

import org.apache.camel.Exchange;
import org.apache.camel.Processor;
import org.apache.commons.lang.StringUtils;

import ca.uhn.hl7v2.model.Message;
import ca.uhn.hl7v2.model.v26.message.ORU_R01;
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
/**
 * Convert HL7 messages to AHE
 *
 */
public class FromHL7 implements Processor {
	/**
	 * 
	 * Convert the incoming HL7 message to AHE
	 * 
	 * 
	 * 
	 * @param exchange The camel exchange
	 */
	public void process(Exchange exchange) throws Exception {
		
		Message hl7Msg = exchange.getIn().getBody(Message.class);
		if(hl7Msg instanceof ORU_R01){
			
			ORU_R01 oru = (ORU_R01)hl7Msg;
			
			PID pid = oru.getPATIENT_RESULT().getPATIENT().getPID();
			String patientId = pid.getPatientIdentifierList(0).getIDNumber().getValue();
			String patientSurname = pid.getPatientName(0).getFamilyName().getSurname().getValue();
			exchange.getIn().setHeader("AHEPatient", patientId);
			
			OBX obx = oru.getPATIENT_RESULT().getORDER_OBSERVATION().getOBSERVATION().getOBX();
			String code = obx.getObservationIdentifier().getIdentifier().getValue();
			String text = obx.getObservationValue(0).getData().toString();
			
			String body = "ORU.R01?code=" + code + "&text=" + text;
			if(!StringUtils.isEmpty(patientSurname)){
				body += "&patient.surname=" + patientSurname;
			}
			exchange.getIn().setBody(body);
			
		} else {
			exchange.setException(new Exception("Unable to convert " + hl7Msg.getClass().getSimpleName() + " message to AHE"));
		}
	}
	
}
