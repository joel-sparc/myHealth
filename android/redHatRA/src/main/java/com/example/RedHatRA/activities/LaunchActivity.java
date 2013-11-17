package com.example.RedHatRA.activities;

import java.util.Map;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.example.RedHatRA.AHE;
import com.example.RedHatRA.R;
import com.example.RedHatRA.SessionManager;
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
public class LaunchActivity extends Activity implements ISessionChangedHandler
{
	private EditText _urlTextView;
	private EditText _patientIdTextView;
	private Button _orderLabTestButton;
	private Button _connectButton;

	@Override
	protected void onCreate(Bundle savedInstanceState)
	{
		// remove title bar
		this.requestWindowFeature(Window.FEATURE_NO_TITLE);

		super.onCreate(savedInstanceState);
		setContentView(R.layout.main);

		// store url text view
		_urlTextView = (EditText)findViewById(R.id.urlTextView);
		_urlTextView.setText("tcp://10.0.2.2:1883");

		// store patient id text view
		_patientIdTextView = (EditText)findViewById(R.id.patientIdTextView);

		// set the click listener for the "Order Lab Test" button
		_orderLabTestButton = (Button)findViewById(R.id.orderLabTestButton);
		_orderLabTestButton.setOnClickListener(new OrderLabTestOnClickListener(this));

		// set the click listener for the connect button
		_connectButton = (Button)findViewById(R.id.connectButton);
		_connectButton.setOnClickListener(new ConnectOnClickListener(this));
	}

	public void onConnected()
	{
		// toggle ui
		_urlTextView.setEnabled(false);
		_patientIdTextView.setEnabled(false);
		_orderLabTestButton.setEnabled(true);
		_connectButton.setText("Disconnect");
	}

	public void onDisconnected()
	{
		// toggle ui
		_urlTextView.setEnabled(true);
		_patientIdTextView.setEnabled(true);
		_orderLabTestButton.setEnabled(false);
		_connectButton.setText("Connect");
	}

	public void onLabTestCompleted(final String patientId, final String result)
	{
		final LaunchActivity me = this;
		runOnUiThread(new Runnable(){
			public void run(){
				
				Map<String,String> observationResult = AHE.getData(result);

				// go to lab results page
				Intent intent = new Intent(me, LabTestResultsActivity.class);
				intent.putExtra("patient.id", patientId);
				intent.putExtra("patient.surname", observationResult.get("patient.surname"));
				intent.putExtra("result", observationResult.get("text"));
				me.startActivity(intent);
			}
		});

	}

	@Override
	protected void onNewIntent(Intent intent)
	{
		super.onNewIntent(intent);
		Bundle bundle = intent.getExtras();
		if (bundle != null)
		{
			// when coming from the order lab test page
			if (bundle.containsKey("orderPlaced"))
			{
				boolean orderPlaced = bundle.getBoolean("orderPlaced");
				if (orderPlaced)
				{
					String patientId = bundle.getString("patientId");
					String testType = bundle.getString("testType");
					String toastMessage = String.format("You have successfully ordered a %s test for patient %s", testType, patientId);
					Toast.makeText(this, toastMessage, Toast.LENGTH_LONG).show();
				}
			}
		}

		if (SessionManager.isConnected())
		{
			if (SessionManager.isLabTestOrdered())
			{
				_orderLabTestButton.setEnabled(false);
			}
			else
			{
				_orderLabTestButton.setEnabled(true);
			}
		}
	}

	public class OrderLabTestOnClickListener implements View.OnClickListener
	{
		private Context _context;

		public OrderLabTestOnClickListener(Context context)
		{
			_context = context;
		}

		@Override
		public void onClick(View view)
		{
			Intent intent = new Intent(_context, OrderLabTestActivity.class);
			String patientId = _patientIdTextView.getText().toString();
			String host = _urlTextView.getText().toString();
			intent.putExtra("patientId", patientId);
			intent.putExtra("host", host);
			startActivity(intent);
		}
	}

	public class ConnectOnClickListener implements View.OnClickListener
	{
		private ISessionChangedHandler _handler;

		public ConnectOnClickListener(ISessionChangedHandler handler)
		{
			_handler = handler;
		}

		@Override
		public void onClick(View view)
		{
			// get entered data
			String url = _urlTextView.getText().toString();
			String patientId = _patientIdTextView.getText().toString();


			// are we disconnecting or connecting?
			if (SessionManager.isConnected())
			{
				SessionManager.disconnect();
			}
			else
			{
				// ensure the required data is entered
				if (url.isEmpty())
				{
					Toast.makeText(getBaseContext(), "Please enter a valid URL for the Patient Care facility", Toast.LENGTH_SHORT).show();
				}
				else if (patientId.isEmpty())
				{
					Toast.makeText(getBaseContext(), "Please enter a valid Patient Id", Toast.LENGTH_SHORT).show();
				}
				else
				{
					SessionManager.connect(_handler, patientId, url);
				}
			}
		}
	}

	@Override
	public void onConnectionFailure(final String host) {
		final LaunchActivity me = this;
		runOnUiThread(new Runnable() {
			public void run() {
				String toastMessage = String.format(
						"The connection to %s has failed", host);
				Toast.makeText(me, toastMessage, Toast.LENGTH_LONG).show();
			}
		});
	}
}
