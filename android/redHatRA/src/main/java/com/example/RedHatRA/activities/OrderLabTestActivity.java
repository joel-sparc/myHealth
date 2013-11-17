package com.example.RedHatRA.activities;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import com.example.RedHatRA.R;
import com.example.RedHatRA.SessionManager;

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
public class OrderLabTestActivity extends Activity
{
    private EditText _patientIdTextView;
    private Spinner _testTypeSpinner;
    private Button _placeOrderButton;
    private String host;

    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        // remove title bar
        this.requestWindowFeature(Window.FEATURE_NO_TITLE);

        super.onCreate(savedInstanceState);
        setContentView(R.layout.orderlabtest);

        // populate the spinner with test type values
        _testTypeSpinner = (Spinner)findViewById(R.id.testTypeSpinner);
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this, R.array.test_types, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        _testTypeSpinner.setAdapter(adapter);

        // set the click listener for the place order button
        _placeOrderButton = (Button)findViewById(R.id.placeOrderButton);
        _placeOrderButton.setOnClickListener(new PlaceOrderOnClickListener(this));

        // store the patient id text view for later use
        _patientIdTextView = (EditText)findViewById(R.id.patientIdTextView);
        _patientIdTextView.setEnabled(false);

        // get the patient value
        Intent intent = getIntent();
        Bundle bundle = intent.getExtras();
        _patientIdTextView.setText(bundle.getString("patientId"));
        
        host = bundle.getString("host");
    }

    private void placeOrder(String host, String patientId, String testType)
    {
        // place order
        SessionManager.placeOrder(host, patientId, testType);

        // go back to the home page
        Intent intent = new Intent(getApplicationContext(), LaunchActivity.class);
        intent.putExtra("orderPlaced", true);
        intent.putExtra("patientId", patientId);
        intent.putExtra("testType", testType);
        intent.addFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
        this.startActivity(intent);
        finish();
    }

    public class PlaceOrderOnClickListener implements View.OnClickListener
    {
        private Context _context;

        public PlaceOrderOnClickListener(Context context)
        {
            _context = context;
        }

        @Override
        public void onClick(View view)
        {
            String patientId = _patientIdTextView.getText().toString().trim();
            String testType = (String)_testTypeSpinner.getSelectedItem();

            // ensure the required data is entered
            if (patientId.isEmpty())
            {
                Toast.makeText(_context, "Please enter a Patient Id", Toast.LENGTH_SHORT).show();
            }
            else
            {
                // place the order
                placeOrder(host, patientId, testType);
            }
        }
    }
}