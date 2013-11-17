package com.sparc.myHealth.his.persistence;

import java.util.List;

import javax.annotation.Resource;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.PersistenceContextType;
import javax.persistence.Query;
import javax.transaction.UserTransaction;

import org.switchyard.component.bean.Service;

import com.sparc.myHealth.his.domain.Patient;
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
@Service(PatientDataService.class)
public class PatientDataBean implements PatientDataService {

	@PersistenceContext(unitName="PATIENT_DATA",type=PersistenceContextType.TRANSACTION)
    private EntityManager em;
	
	@Resource(mappedName = "java:jboss/UserTransaction")
	private UserTransaction tx;
	
	@Override
	public Patient findByIdentifier(String identifier){
		
		Query byIdentifier = em.createQuery("SELECT p FROM Patient p WHERE p.identifier LIKE :identifier")
				.setParameter("identifier", identifier);
		
		List results = byIdentifier.getResultList();
		if(results.size() > 0){
			return (Patient)results.get(0);
		}
		return null;
	}
	
	@Override
	public void createOrUpdate(Patient patient){
		try {
			tx.begin();
			Patient found = findByIdentifier(patient.getIdentifier());
			if(found == null){
				em.persist(patient);
			} else {
				found.setIdentifier(patient.getIdentifier());
				found.setSurname(patient.getSurname());
			}
			tx.commit();
		} catch (Exception e) {
			e.printStackTrace();
			try {
				tx.rollback();
			} catch (Exception e1) {
				e1.printStackTrace();
			}
		}
	}

}
