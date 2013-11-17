package com.sparc.myHealth.pcf.androidIn.hl7;

import java.io.IOException;

import org.apache.camel.Exchange;
import org.apache.camel.Processor;

import ca.uhn.hl7v2.HL7Exception;
import ca.uhn.hl7v2.model.DataTypeException;
import ca.uhn.hl7v2.model.v26.message.QRY_A19;
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
public class PatientQueryBuilder extends HL7MessageBuilder<QRY_A19> implements Processor {
	/**
	 * 
	 * Builds an HL7 patient query. This processor assumes that
	 * the AHEPatient header has been set.
	 * 
	 * @param exchange The camel exchange
	 */
	public void process(Exchange exchange) throws Exception {
		
		String patientIdentifier = String.valueOf(exchange.getIn().getHeader("AHEPatient"));
		
		exchange.getIn().setBody(setPatientId(patientIdentifier).finish().toString());
	}

	@Override
	protected QRY_A19 init() {
		QRY_A19 message = new QRY_A19();
		try {
			message.initQuickstart("QRY", "A19", "T");
		} catch (HL7Exception e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		return message;
	}
	
	public PatientQueryBuilder setPatientId(String patientId)
			throws DataTypeException{
		message.getQRD().getWhoSubjectFilter(0).getIDNumber().setValue(patientId);
		return this;
	}
}