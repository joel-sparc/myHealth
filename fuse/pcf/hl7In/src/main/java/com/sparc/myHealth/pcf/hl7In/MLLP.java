package com.sparc.myHealth.pcf.hl7In;

import java.io.IOException;

import ca.uhn.hl7v2.HL7Exception;
import ca.uhn.hl7v2.model.Message;

public class MLLP {

	public Object acknowledge(Message message) throws HL7Exception, IOException {
        return message.generateACK();
    }
}
