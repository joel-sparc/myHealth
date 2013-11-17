package com.sparc.myHealth.pcf.androidIn.hl7;

import java.io.IOException;

import ca.uhn.hl7v2.HL7Exception;
import ca.uhn.hl7v2.model.DataTypeException;
import ca.uhn.hl7v2.model.v26.message.ORM_O01;
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
public class ObservationRequestBuilder extends HL7MessageBuilder<ORM_O01>{
	

	@Override
	protected ORM_O01 init() {
		ORM_O01 observationRequest = new ORM_O01();
		try {
			observationRequest.initQuickstart("ORM", "O01", "T");
		} catch (HL7Exception e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return observationRequest;
	}
	
	public ObservationRequestBuilder setPatient(String patientId, String surname)
			throws DataTypeException{
		
		PID pid = message.getPATIENT().getPID(); 
		pid.getPatientName(0).getFamilyName().getSurname().setValue(surname);
		pid.getPatientIdentifierList(0).getIDNumber().setValue(patientId);
		
		return this;
	}
	
	public ObservationRequestBuilder requestLab(String orderIdentifier)
			throws DataTypeException{
		message.getORDER().getORDER_DETAIL().getOBR()
		.getSetIDOBR().setValue("1");

		message.getORDER().getORDER_DETAIL().getOBR()
		.getFillerOrderNumber().getNamespaceID()
		.setValue("LAB");

		message.getORDER().getORDER_DETAIL().getOBR()
		.getFillerOrderNumber().getEntityIdentifier()
		.setValue(orderIdentifier);
		
		return this;
	}

}
