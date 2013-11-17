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

import ca.uhn.hl7v2.model.v26.message.ORU_R01;

@Service(PCFService.class)
public class PCFBean implements PCFService {
	
	@Resource(mappedName = "java:/ConnectionFactory")
    private TopicConnectionFactory connectionFactory;
	
	//TODO: When we switch to ActiveMQ send to the virtual topic:
	//private static final String pcfOut = "VirtualTopic.OUT.HL7.Patient.";
	private static final String pcfOut = "Consumer.PCF.VirtualTopic.OUT.HL7.PATIENT.";//Temporary hack for HornetQ

	@Override
	public void sendObservationResultMessage(ORU_R01 observationResultMessage) {
		String patientId = observationResultMessage.getPATIENT_RESULT().getPATIENT().getPID()
				.getPatientIdentifierList(0).getIDNumber().getValue();
		
		sendToPatientCareFacilities(pcfOut, patientId,observationResultMessage.toString());
	}
	
	private void sendToPatientCareFacilities(String topicPrefix, String patientIdentifier, String text){
		Session session = null;
		Connection connection = null;
		try {
			connection = connectionFactory.createConnection();
			session = connection.createSession(false, Session.AUTO_ACKNOWLEDGE);
			
			String topicName = new StringBuilder(topicPrefix)
				.append(patientIdentifier).toString();
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
