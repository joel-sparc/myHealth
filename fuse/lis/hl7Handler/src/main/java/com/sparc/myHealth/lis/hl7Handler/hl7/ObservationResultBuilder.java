package com.sparc.myHealth.lis.hl7Handler.hl7;

import java.io.IOException;

import ca.uhn.hl7v2.HL7Exception;
import ca.uhn.hl7v2.model.DataTypeException;
import ca.uhn.hl7v2.model.v26.datatype.TX;
import ca.uhn.hl7v2.model.v26.group.ORU_R01_OBSERVATION;
import ca.uhn.hl7v2.model.v26.group.ORU_R01_ORDER_OBSERVATION;
import ca.uhn.hl7v2.model.v26.message.ORU_R01;
import ca.uhn.hl7v2.model.v26.segment.OBR;
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
public class ObservationResultBuilder extends HL7MessageBuilder<ORU_R01>{
	

	@Override
	protected ORU_R01 init() {
		ORU_R01 observationResult = new ORU_R01();
		try {
			observationResult.initQuickstart("ORU", "R01", "T");
		} catch (HL7Exception e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return observationResult;
	}
	
	public ObservationResultBuilder setPatient(String patientId, String surname)
			throws DataTypeException{
		
		PID pid = message.getPATIENT_RESULT().getPATIENT().getPID();
		pid.getPatientIdentifierList(0).getIDNumber().setValue(patientId);
		pid.getPatientName(0).getFamilyName().getSurname().setValue(surname);
		
		return this;
	}
	
	public ObservationResultBuilder setObservationResult(String namespace, String orderIdentifier, String results)
			throws DataTypeException{
		ORU_R01_ORDER_OBSERVATION orderObservation = message.getPATIENT_RESULT().getORDER_OBSERVATION();
		
		OBR obr = orderObservation.getOBR();
		obr.getSetIDOBR().setValue("1");
		obr.getFillerOrderNumber().getEntityIdentifier().setValue("1000");
		obr.getFillerOrderNumber().getNamespaceID().setValue(namespace);//"LAB"
		obr.getUniversalServiceIdentifier().getIdentifier().setValue(orderIdentifier);//"87880"
		
		ORU_R01_OBSERVATION observation = orderObservation.getOBSERVATION(0);
		
		OBX obx = observation.getOBX();
		obx.getSetIDOBX().setValue("1");
		obx.getObservationIdentifier().getIdentifier().setValue(orderIdentifier);
		obx.getObservationSubID().setValue("1");
		
		obx.getValueType().setValue("TX");
		TX tx = new TX(message);
		tx.setValue(results);
		obx.getObservationValue(0).setData(tx);
		
		return this;
	}

}
