package com.sparc.myHealth.his;

import javax.inject.Inject;

import org.jboss.logging.Logger;
import org.switchyard.component.bean.Reference;
import org.switchyard.component.bean.Service;

import ca.uhn.hl7v2.model.v26.message.ADR_A19;
import ca.uhn.hl7v2.model.v26.message.ADT_A01;
import ca.uhn.hl7v2.model.v26.message.ORM_O01;
import ca.uhn.hl7v2.model.v26.message.ORU_R01;
import ca.uhn.hl7v2.model.v26.message.QRY_A19;
import ca.uhn.hl7v2.model.v26.segment.PID;

import com.sparc.myHealth.his.domain.Patient;
import com.sparc.myHealth.his.hl7.PatientQueryResponseBuilder;
import com.sparc.myHealth.his.persistence.PatientDataService;

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


@Service(HISService.class)
public class HISBean implements HISService {

	Logger logger = Logger.getLogger(HISBean.class);
	
	@Inject @Reference
	private AncillaryService ancillaryServices;
	
	@Inject @Reference
	private PCFService patientCareFacilities;
	
	@Inject @Reference
	private PatientDataService patientDataStore;
	
	@Override
	public ADR_A19 handlePatientQueryMessage(QRY_A19 query) {

		if(query == null){
			logger.error(" ----- HIS received NULL patient query------");
			return null;
		}

		String requestedPatientId = query.getQRD().getWhoSubjectFilter(0).getIDNumber().getValue();

		try{
			

			Patient patient = patientDataStore.findByIdentifier(requestedPatientId);

			if(patient == null){
				logger.error(" ----- Unable to find patient with id " + requestedPatientId + " ------");
				return null;
			}
			
			ADR_A19 patientLookupResponse = new PatientQueryResponseBuilder()
				.setPatient(patient)
				.finish();
			

			return patientLookupResponse;
		}catch (Exception e){
			logger.error(e.getMessage());
			return null;
		}
	}

	@Override
	public void handlePatientAdmitMessage(ADT_A01 admitMessage) {
		if(admitMessage == null){
			logger.error(" ----- HIS received NULL patient admit message------");
			return;
		}
		
		//store the id and surname in the db
		PID pid = admitMessage.getPID();
		String identifier = pid.getPatientIdentifierList(0).getIDNumber().getValue();
		String surname = pid.getPatientName(0).getFamilyName().getSurname().getValue();
		patientDataStore.createOrUpdate(new Patient(identifier, surname));
		
		ancillaryServices.sendPatientAdmitMessage(admitMessage);

	}
	
	@Override
	public void handleObservationRequestMessage(
			ORM_O01 observationRequestMessage) {
		
		ancillaryServices.sendObservationRequestMessage(observationRequestMessage);
	}
	
	@Override
	public void handleObservationResultMessage(
			ORU_R01 observationResultMessage) {
		
		patientCareFacilities.sendObservationResultMessage(observationResultMessage);
	}

}
