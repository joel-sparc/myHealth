package com.example.RedHatRA.activities;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
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
public class LabTestResultsActivity extends Activity
{

    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        // remove title bar
        this.requestWindowFeature(Window.FEATURE_NO_TITLE);

        super.onCreate(savedInstanceState);
        setContentView(R.layout.labtestresults);

        // get ui elements
        TextView labResultsTitle = (TextView)findViewById(R.id.labResultsTitle);
        TextView resultTextView = (TextView)findViewById(R.id.resultTextView);

        // get values passed from order completion handler
        Intent intent = getIntent();
        Bundle bundle = intent.getExtras();
        
        String code = SessionManager.getPendingOrder();
        String patientId = bundle.getString("patient.id");
        String patientSurname = bundle.getString("patient.surname");
        String result = bundle.getString("result");

        String patientIdentifier = (patientSurname != null)?
        		patientSurname + "[" + patientId + "]":
        		patientId;
        // update ui with values
        labResultsTitle.setText(code + " results for patient: " + patientIdentifier);
        resultTextView.setText(result);

        // set the click listener for the return home button
        Button returnHomeButton = (Button)findViewById(R.id.returnHomeButton);
        returnHomeButton.setOnClickListener(new ReturnHomeOnClickListener(this));
    }

    public class ReturnHomeOnClickListener implements View.OnClickListener
    {
        private Context _context;

        public ReturnHomeOnClickListener(Context context)
        {
            _context = context;
        }

        @Override
        public void onClick(View view)
        {
            Intent intent = new Intent(_context, LaunchActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
            startActivity(intent);
            finish();
        }
    }
}