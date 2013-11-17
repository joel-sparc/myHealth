package com.sparc.myHealth.his;

import ca.uhn.hl7v2.model.v26.message.ORU_R01;

public interface PCFService {

	public void sendObservationResultMessage(ORU_R01 observationResultMessage);
}
