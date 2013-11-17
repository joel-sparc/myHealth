package com.sparc.myHealth.his.hl7;

import java.io.IOException;

import com.sparc.myHealth.his.domain.Patient;

import ca.uhn.hl7v2.HL7Exception;
import ca.uhn.hl7v2.model.DataTypeException;
import ca.uhn.hl7v2.model.v26.message.ADR_A19;
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
public class PatientQueryResponseBuilder extends HL7MessageBuilder<ADR_A19>{

	@Override
	protected ADR_A19 init() {
		ADR_A19 message = new ADR_A19();
		try {
			message.initQuickstart("ADR", "A19", "T");
		} catch (HL7Exception e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		return message;
	}
	
	public PatientQueryResponseBuilder setPatient(Patient patient)
			throws DataTypeException{
		
		message.getQUERY_RESPONSE().getPID()
			.getPatientIdentifierList(0).getIDNumber().setValue(patient.getIdentifier());
		
		message.getQUERY_RESPONSE().getPID()
			.getPatientName(0).getFamilyName().getSurname().setValue(patient.getSurname());
		
		return this;
	}

	@Override
	public ADR_A19 finish() {
		return message;
	}

}
