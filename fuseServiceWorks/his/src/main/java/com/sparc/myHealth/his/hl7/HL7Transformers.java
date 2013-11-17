package com.sparc.myHealth.his.hl7;

import javax.inject.Named;

import org.jboss.logging.Logger;
import org.switchyard.annotations.Transformer;

import ca.uhn.hl7v2.HL7Exception;
import ca.uhn.hl7v2.model.v26.message.ADR_A19;
import ca.uhn.hl7v2.model.v26.message.ADT_A01;
import ca.uhn.hl7v2.model.v26.message.ORM_O01;
import ca.uhn.hl7v2.model.v26.message.ORU_R01;
import ca.uhn.hl7v2.model.v26.message.QRY_A19;
import ca.uhn.hl7v2.parser.PipeParser;

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


@Named("HL7Transformers")
public class HL7Transformers {
	Logger logger = Logger.getLogger(HL7Transformers.class);
	
    @Transformer
    public QRY_A19 toQryA19(String in) {
    	logger.debug("Transforming patient query:" + in);
    	PipeParser pipeParser = new PipeParser();
		try {
			return (QRY_A19) pipeParser.parse(in);
		} catch (HL7Exception e) {
			logger.error("Unable to transform message: " + e.getMessage());
		}
		return null;
    }
    
    @Transformer
    public String toString(ADR_A19 in) {
    	logger.debug("Transforming patient query response:" + in.toString());
		return String.valueOf(in);
    }
    
    @Transformer
    public ADT_A01 toADT_A01(String in) {
    	logger.debug("Transforming admit message:" + in);
    	PipeParser pipeParser = new PipeParser();
		try {
			return (ADT_A01) pipeParser.parse(in);
		} catch (HL7Exception e) {
			logger.error("Unable to transform message: " + e.getMessage());
		}
		return null;
    }
    
    @Transformer
    public String toString(ADT_A01 in) {
    	logger.debug("Transforming admit message:" + in.toString());
		return String.valueOf(in);
    }
    
    @Transformer
    public ORM_O01 toORM_O01(String in) {
    	logger.debug("Transforming observation request message:" + in);
    	PipeParser pipeParser = new PipeParser();
		try {
			return (ORM_O01) pipeParser.parse(in);
		} catch (HL7Exception e) {
			logger.error("Unable to transform message: " + e.getMessage());
		}
		return null;
    }
    
    @Transformer
    public String toString(ORM_O01 in) {
    	logger.debug("Transforming observation request message:" + in.toString());
		return String.valueOf(in);
    }
    
    @Transformer
    public ORU_R01 toORU_R01(String in) {
    	logger.debug("Transforming observation result message:" + in);
    	PipeParser pipeParser = new PipeParser();
		try {
			return (ORU_R01) pipeParser.parse(in);
		} catch (HL7Exception e) {
			logger.error("Unable to transform message: " + e.getMessage());
		}
		return null;
    }
    
    @Transformer
    public String toString(ORU_R01 in) {
    	logger.debug("Transforming observation result message:" + in.toString());
		return String.valueOf(in);
    }
}
