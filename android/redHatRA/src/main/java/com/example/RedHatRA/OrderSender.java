package com.example.RedHatRA;

import java.net.URISyntaxException;
import java.util.Hashtable;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import org.fusesource.mqtt.client.FutureConnection;
import org.fusesource.mqtt.client.MQTT;
import org.fusesource.mqtt.client.QoS;

import android.os.AsyncTask;
import android.util.Log;
import com.example.RedHatRA.base.ISessionChangedHandler;

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
public class OrderSender extends AsyncTask<String, Integer, String> {
    private final static String logTag = "com.example.RedHatRA.OrderSender";
    private static  Map<String, String> testCodes;
    
    static{
    	testCodes = new Hashtable<String, String>();
    	testCodes.put("Rapid Strep", "87880");
    	testCodes.put("CBC", "58410-2");
    }
    
    private FutureConnection connection = null;


    protected String doInBackground(String... strings)
    {
        Log.d(logTag, "Order Sender thread started");

        final String testCode = testCodes.get(strings[0]);
        final String observationRequestMessage = "ORM.O01?code=" + testCode;
        
        
        final String observationRequestTopic = "VirtualTopic.IN.AHE.PATIENT.";
        
        try {
            connection = createConnection(SessionManager.getSession().getHost());
        	
			connection.connect().await(5000, TimeUnit.MILLISECONDS);
			
			connection.publish(
					observationRequestTopic + SessionManager.getSession().getPatientId(),
					observationRequestMessage.getBytes(),
					QoS.EXACTLY_ONCE, false)
					.await();
	    	
		} catch (Exception e) {
			Log.e(logTag, e.getMessage());
		} finally{
			publishProgress(100);
			try {
				connection.disconnect().await();
			} catch (Exception e) {
				Log.e(logTag, e.getMessage());
			}
		}
        return "Result";
    }
    
    private static FutureConnection createConnection(String host){
    	
    	try {
    		MQTT client = new MQTT();
			client.setHost(host);
			return client.futureConnection();
		} catch (URISyntaxException e) {
			Log.e(logTag, e.getMessage());
			return null;
		}
    	
    }

    protected void onProgressUpdate(Integer... progress)
    {
        
    }

    protected void onPostExecute(String result)
    {
    	try {
			connection.disconnect().await();
		} catch (Exception e) {
			Log.e(logTag, e.getMessage());
		}
    }
}