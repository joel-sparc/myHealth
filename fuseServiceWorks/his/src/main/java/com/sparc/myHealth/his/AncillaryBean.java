package com.sparc.myHealth.his;

import javax.annotation.Resource;
import javax.jms.Connection;
import javax.jms.Destination;
import javax.jms.JMSException;
import javax.jms.MessageProducer;
import javax.jms.Session;
import javax.jms.TextMessage;
import javax.jms.TopicConnectionFactory;

import org.switchyard.component.bean.Service;

import ca.uhn.hl7v2.model.v26.message.ADT_A01;
import ca.uhn.hl7v2.model.v26.message.ORM_O01;
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
@Service(AncillaryService.class)
public class AncillaryBean implements AncillaryService {

	@Resource(mappedName = "java:/ConnectionFactory")
    private TopicConnectionFactory connectionFactory;
	
	//TODO: When we switch to ActiveMQ send to the virtual topic:
	//private static final String adtOut = "VirtualTopic.OUT.HL7.ADT.A01";
	private static final String adtOut = "Consumer.LIS.VirtualTopic.OUT.HL7.ADT.A01";//Temporary hack for HornetQ
	
	//TODO: When we switch to ActiveMQ send to the virtual topic:
	//private static final String ormOut = "VirtualTopic.OUT.HL7.ORM.O01";
	private static final String ormOut = "Consumer.LIS.VirtualTopic.OUT.HL7.ORM.O01";//Temporary hack for HornetQ

	
	public void sendPatientAdmitMessage(ADT_A01 admitMessage){
		
		sendToAncillaryServices(adtOut, admitMessage.toString());
	}

	@Override
	public void sendObservationRequestMessage(ORM_O01 observationRequestMessage) {
		
		sendToAncillaryServices(ormOut, observationRequestMessage.toString());
	}
	
	private void sendToAncillaryServices(String topicName, String text){
		Session session = null;
		Connection connection = null;
		try {
			connection = connectionFactory.createConnection();
			session = connection.createSession(false, Session.AUTO_ACKNOWLEDGE);
			
			Destination out = session.createQueue(topicName);
			MessageProducer producer = session.createProducer(out);
			TextMessage message = session.createTextMessage(text);
			producer.send(message);
			
		} catch (Exception e) {
			e.printStackTrace();
		} finally{
			if(session != null){
				try {
					session.close();
				} catch (JMSException e) {
					e.printStackTrace();
				}
			}
		}
	}
}
