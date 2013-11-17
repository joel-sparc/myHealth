package com.example.RedHatRA;

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
public class SessionManager
{
	private static Session session;
    private static ISessionChangedHandler _handler;
    private static boolean _labTestOrdered = false;
    private static String pendingOrder;
    private static ResultReceiver receiver;
    
    public static Session getSession(){
    	return session;
    }

    public static void connect(ISessionChangedHandler handler, String patientId, String host)
    {
    	if (!SessionManager.isConnected())
        {
    		_handler = handler;
    		if(startPatientListener(host, patientId)){
    			session = new Session(patientId, host);
    			_handler.onConnected();
        	
    		} else {
    			connectionFailure(host);
    			_handler = null;
    		}
        }
    }
    
    public static void connectionFailure(String host)
    {
    	_handler.onConnectionFailure(host);
    	_handler = null;
    }

    public static void disconnect()
    {
    	if (SessionManager.isConnected())
        {
    		receiver.stop();
    		session = null;

        	// invoke connection handler
        	_handler.onDisconnected();
        	_handler = null;
        }
    }

    public static void placeOrder(String host, String patientId, String testType)
    {
        // ensure we're connected before we attempt to place an order
        if (SessionManager.isConnected())
        {
        	pendingOrder = testType;
            new OrderSender().execute(testType);
            _labTestOrdered = true;
        }
    }
    
    private static boolean startPatientListener(String host, String patientId){
    	receiver = new ResultReceiver(host, patientId);
    	return receiver.start();
    }
    
    public static void handlePatientResults(String results){
    	_handler.onLabTestCompleted(session.getPatientId(), results);
    	_labTestOrdered = false;
    }

    public static boolean isConnected()
    {
        return _handler != null;
    }

    public static boolean isLabTestOrdered()
    {
        return _labTestOrdered;
    }

	public static String getPendingOrder() {
		return pendingOrder;
	}
}