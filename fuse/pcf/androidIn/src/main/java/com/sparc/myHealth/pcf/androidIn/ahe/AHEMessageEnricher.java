package com.sparc.myHealth.pcf.androidIn.ahe;

import org.apache.camel.Exchange;
import org.apache.camel.processor.aggregate.AggregationStrategy;

import ca.uhn.hl7v2.model.Message;
import ca.uhn.hl7v2.model.v26.message.ADR_A19;
import ca.uhn.hl7v2.model.v26.message.ORM_O01;

import com.sparc.myHealth.pcf.androidIn.hl7.ObservationRequestBuilder;
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
public class AHEMessageEnricher implements AggregationStrategy {

	/**
	 * This enricher uses the AHERequest from the incoming AHE message header
	 * and the results of a Patient Lookup Service invocation to build an
	 * HL7 ORM request.
	 * 
	 * @param original
	 *            AHE Message
	 * @param resource
	 *            The HIS Patient Lookup Service response
	 * @return
	 */
	public Exchange aggregate(Exchange original, Exchange resource) {

		String request = String.valueOf(original.getIn()
				.getHeader("AHERequest"));

		String args = String.valueOf(original.getIn()
				.getHeader("AHEArgs"));

		//Cheating a little here - we know there's only one "code" arg
		String orderIdentifier = args.split("=")[1];

		if (request.equals("ORM.O01")) {
			try {
				Patient patient = Patient
						.fromPatientLookupResponse(resource.getIn()
								.getBody(Message.class));

				ORM_O01 message = new ObservationRequestBuilder()

				// Load any info from the patient lookup response
				// We could have gotten the patient id from the AHEPatient
				// header but for demonstration purposes we use the patient
				// lookup service and grab the surname as well 
				.setPatient(patient.identifier, patient.surname)


				// Build the OBR segment and we're finished
				.requestLab(orderIdentifier).finish();

				original.getIn().setBody(message.toString());

			} catch (Exception e) {
				original.setException(e);
			}
		} else {
			original.setException(new Exception(
					"AHEMessageEnricher: Unknown request [" + request + "]"));
		}

		return original;
	}

	private static class Patient {
		private String identifier;
		private String surname;

		Patient(String identifier, String surname) {
			super();
			this.identifier = identifier;
			this.surname = surname;
		}

		/**
		 * Extract patient info from the given message
		 * 
		 * @param message ADR A19 message
		 * @return
		 * @throws Exception
		 */
		static Patient fromPatientLookupResponse(Message message) throws Exception{
			
			if(message instanceof ADR_A19){
				ADR_A19 response = (ADR_A19)message;
				return new Patient(
						response.getQUERY_RESPONSE().getPID().getPatientIdentifierList(0).getIDNumber().getValue(),
						response.getQUERY_RESPONSE().getPID().getPatientName(0).getFamilyName().getSurname().getValue());
			}
			
			throw new Exception("Unable to extract patient info from " + message.getClass().getSimpleName() + " message");
			

		}
	}

}