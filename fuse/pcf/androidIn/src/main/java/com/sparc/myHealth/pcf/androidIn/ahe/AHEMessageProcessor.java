package com.sparc.myHealth.pcf.androidIn.ahe;

import org.apache.camel.Exchange;
import org.apache.camel.Processor;
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
 * This is the first stop for all incoming "AHE" messages
 *
 */
public class AHEMessageProcessor implements Processor {
	/**
	 * 
	 * Parse the message body and load the exchange headers.
	 * 
	 * We need a good header prefix, so let's name our message format
	 * the "Abbreviated HL7 Exchange" format and prefix our headers with "AHE".
	 * 
	 * The incoming request format is <request>:<args> so we parse the message
	 * body to load the AHERequest and AHEArgs headers.
	 * 
	 * For this application, all AHE requests are affiliated with a patient and the
	 * patient id is the last part of the JMS destination. We get the patient id from the JMS
	 * destination and put it in a new AHEPatient header.
	 * 
	 * 
	 * 
	 * @param exchange The camel exchange
	 */
	public void process(Exchange exchange) throws Exception {
		
		String msg = exchange.getIn().getBody(String.class);
		int div = msg.indexOf('?');
		exchange.getIn().setHeader("AHERequest", msg.substring(0, div));
		exchange.getIn().setHeader("AHEArgs", msg.substring(div + 1));
		
		String jmsDestination =
				String.valueOf(exchange.getIn().getHeader("JMSDestination"));
		String patientId = jmsDestination.substring(jmsDestination.lastIndexOf('.') + 1);
		exchange.getIn().setHeader("AHEPatient", patientId);
		
	}
	
}
