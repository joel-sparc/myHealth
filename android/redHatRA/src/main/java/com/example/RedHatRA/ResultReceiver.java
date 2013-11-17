package com.example.RedHatRA;

import java.net.URISyntaxException;
import java.util.UUID;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;

import org.fusesource.mqtt.client.FutureConnection;
import org.fusesource.mqtt.client.MQTT;
import org.fusesource.mqtt.client.Message;
import org.fusesource.mqtt.client.QoS;
import org.fusesource.mqtt.client.Topic;

import android.util.Log;

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
public class ResultReceiver implements Runnable {
    private final static String logTag = "com.example.RedHatRA.ResultReceiver";
    private FutureConnection connection = null;
    private final String host;
    private final String patientId;
    private final String clientId;
    
    private Thread receiverThread;
    private AtomicBoolean die = new AtomicBoolean(false);

    public ResultReceiver(String host, String patientId){
    	this.host = host;
    	this.patientId = patientId;
    	this.clientId = UUID.randomUUID().toString().substring(0, 8);
    }
    
    public boolean start(){
    	if(receiverThread == null){
    		if(connect(host, patientId).isConnected()){
    			new Thread(this).start();
    			return true;
    		}
    	}
    	return false;
    }
    
    public void stop(){
    	if(receiverThread != null){
    		die.set(true);
    		receiverThread.interrupt();
    	}
    	receiverThread = null;
    }
    
	public void run() {
		Log.d(logTag, "Receiver started");
		receiverThread = Thread.currentThread();
		while (true) {
			try {

				if (connection != null) {
					Log.d(logTag, "Waiting for message from PCF...");
					final Message result = connection.receive().await();
					if (result != null) {
						Log.d(logTag, "Received message from PCF: "
								+ new String(result.getPayload()));
						SessionManager.handlePatientResults(new String(result
								.getPayload()));
					}
				}

			} catch (Exception e) {
				if(die.get()){
					//user initiated disconnect
					die.set(false);
				} else {
					//unexpected failure
					SessionManager.connectionFailure(host);
				}
				break;
			}
		}
		try {
			connection.disconnect().await();
			Log.d(logTag, "Receiver stopped");
		} catch (Exception e) {
			Log.e(logTag, e.getMessage());
		}
	}

    private FutureConnection connect(String host, String patientId){
        final String topic = "OUT.AHE.PATIENT." + patientId;
		try {
            connection = ResultReceiver.createConnection(host, this.clientId);
        	
			connection.connect().await(3000, TimeUnit.MILLISECONDS);
			
			connection.subscribe(new Topic[]{new Topic(topic, QoS.EXACTLY_ONCE)}).await(3000, TimeUnit.MILLISECONDS);
			
		} catch(Exception e){
			Log.e(logTag, "Could not connect");
		}
		return connection;
	}
	
	private static FutureConnection createConnection(String host, String clientId){
    	
    	try {
    		MQTT client = new MQTT();
			client.setHost(host);
			client.setClientId(clientId);
			client.setCleanSession(false);
			return client.futureConnection();
		} catch (URISyntaxException e) {
			Log.e(logTag, e.getMessage());
			return null;
		}
    	
    }
}