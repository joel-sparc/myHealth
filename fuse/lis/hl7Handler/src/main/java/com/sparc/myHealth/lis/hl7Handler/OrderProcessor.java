package com.sparc.myHealth.lis.hl7Handler;

import java.util.Hashtable;
import java.util.Map;

import org.apache.camel.Exchange;
import org.apache.camel.Processor;

import com.sparc.myHealth.lis.hl7Handler.hl7.ObservationResultBuilder;

import ca.uhn.hl7v2.model.Message;
import ca.uhn.hl7v2.model.v26.message.ORM_O01;
import ca.uhn.hl7v2.model.v26.message.ORU_R01;
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
public class OrderProcessor  implements Processor{
	
	private static final String NAMESPACE= "LAB";
	
	private static final String STREP_TEST = "87880";
	private static final String STREP_TEST_RESULTS = "Negative (no strep detected)";
	
	private static final String CBC_TEST = "58410-2";
	private static final String CBC_TEST_RESULTS = "WBC 3.3\\.br\\RBC 4.21\\.br\\Hgb 12.8\\.br\\Plt 202.";
	
	private final static Map<String, String> resultsMap;
	
	static{
		resultsMap = new Hashtable<String, String>();
		resultsMap.put(STREP_TEST, STREP_TEST_RESULTS);
		resultsMap.put(CBC_TEST, CBC_TEST_RESULTS);
	}

	@Override
	public void process(Exchange exchange) throws Exception {

		Message hl7Msg = exchange.getIn().getBody(Message.class);
		if(hl7Msg instanceof ORM_O01){
			ORM_O01 observationRequest = (ORM_O01)hl7Msg;
			
			String orderIdentifier = observationRequest.getORDER().getORDER_DETAIL().getOBR()
			.getFillerOrderNumber().getEntityIdentifier()
			.getValue();
			
			String results = resultsMap.get(orderIdentifier);
			if(results == null){
				exchange.setException(new Exception("Unable to recognize order identifier: " + orderIdentifier));
				return;
			}
			
			PID pid = observationRequest.getPATIENT().getPID();
			String patientId = pid.getPatientIdentifierList(0).getIDNumber().getValue();
			String surname = pid.getPatientName(0).getFamilyName().getSurname().getValue();
			
			ORU_R01 result = new ObservationResultBuilder()
			
			.setPatient(patientId, surname)
			
			.setObservationResult(NAMESPACE, orderIdentifier, results)
			
			.finish();
			
			exchange.getIn().setBody(result.toString());
			
			return;
		}
		exchange.setException(new Exception("Unable to process message of type: " + hl7Msg.getClass().getSimpleName()));
	}

}
