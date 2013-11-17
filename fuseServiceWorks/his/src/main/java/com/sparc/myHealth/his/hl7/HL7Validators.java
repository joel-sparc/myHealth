package com.sparc.myHealth.his.hl7;

import javax.inject.Named;

import org.apache.log4j.Logger;
import org.drools.core.util.StringUtils;
import org.switchyard.annotations.Validator;
import org.switchyard.validate.ValidationResult;

import ca.uhn.hl7v2.model.v26.message.ADR_A19;
import ca.uhn.hl7v2.model.v26.message.ORM_O01;
import ca.uhn.hl7v2.model.v26.message.ORU_R01;
import ca.uhn.hl7v2.model.v26.message.QRY_A19;
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


@Named("HL7Validators")
public class HL7Validators {
	
	Logger logger = Logger.getLogger(HL7Validators.class);

	@Validator(name = "java:ca.uhn.hl7v2.model.v26.message.QRY_A19")
    public ValidationResult validate(QRY_A19 msg) {
		
		if(msg == null){
			return nullMessage();
		}
		
		String patientId = msg.getQRD().getWhoSubjectFilter(0).getIDNumber().getValue();
		if(StringUtils.isEmpty(patientId)){
			return invalid("Received patient query with no patient id");
		}
		
		return ok();
	}
	
	@Validator(name = "java:ca.uhn.hl7v2.model.v26.message.ADR_A19")
    public ValidationResult validate(ADR_A19 msg) {
		
		if(msg == null){
			return nullMessage();
		}
		
		if(!isValid(msg.getQUERY_RESPONSE().getPID())){
			return invalidPID();
		}
		
		return ok();
	}
	
	@Validator(name = "java:ca.uhn.hl7v2.model.v26.message.ORM_O01")
    public ValidationResult validate(ORM_O01 msg) {
		
		if(msg == null){
			return nullMessage();
		}
		
		String patientId = msg.getPATIENT().getPID().getPatientIdentifierList(0).getIDNumber().getValue();
		if(StringUtils.isEmpty(patientId)){
			return invalid("Received patient query with no patient id");
		}
		
		return ok();
	}
	
	@Validator(name = "java:ca.uhn.hl7v2.model.v26.message.ORU_R01")
    public ValidationResult validate(ORU_R01 msg) {
		
		if(msg == null){
			return nullMessage();
		}
		
		String patientId = msg.getPATIENT_RESULT().getPATIENT().getPID()
				.getPatientIdentifierList(0).getIDNumber().getValue();
		if(StringUtils.isEmpty(patientId)){
			return invalid("Received observation response with no patient id");
		}
		
		return ok();
	}
	
	public static boolean isValid(PID pid){
		if(pid == null){
			return false;
		}
		if(StringUtils.isEmpty(pid.getPatientIdentifierList(0).getIDNumber().getValue())){
			return false;
		}
		return true;
	}
	
	private static ValidationResult invalidPID(){
		return invalid("Message contains invalid or insufficient patient information");
	}
	
	private static ValidationResult nullMessage(){
		return invalid("Message is null");
	}
	
	private static ValidationResult invalid(final String reason){
		return new ValidationResult(){

			@Override
			public String getDetail() {
				return reason;
			}

			@Override
			public boolean isValid() {
				return false;
			}
			
		};
	}
	
	private static ValidationResult ok(){
		return new ValidationResult(){

			@Override
			public String getDetail() {
				return null;
			}

			@Override
			public boolean isValid() {
				return true;
			}
			
		};
	}
    
}
