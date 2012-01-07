/*
Copyright 2011 Bricolsoft Consulting

   Licensed under the Apache License, Version 2.0 (the "License");
   you may not use this file except in compliance with the License.
   You may obtain a copy of the License at

       http://www.apache.org/licenses/LICENSE-2.0

   Unless required by applicable law or agreed to in writing, software
   distributed under the License is distributed on an "AS IS" BASIS,
   WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
   See the License for the specific language governing permissions and
   limitations under the License.
*/

package com.bricolsoftconsulting.setalwaysfinish;

import java.lang.reflect.Method;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;

import android.content.Intent;
import android.os.Bundle;
import android.provider.Settings;
import android.util.Log;
import android.view.View;
import android.widget.CheckBox;
import android.widget.Toast;

public class SetAlwaysFinishActivity extends Activity {
    private static final String LOG_TAG = "SetAlwaysFinishActivity";

    /*
     * Intent constants
     */
    public static final String ACTION_SET = "com.bricolsoftconsulting.setalwaysfinish.ACTION_SET";
    public static final String EXTRA_ALWAYSFINISH = "com.bricolsoftconsulting.setalwaysfinish.EXTRA_ALWAYSFINISH";
    public static final String EXTRA_NOTIFY = "com.bricolsoftconsulting.setalwaysfinish.EXTRA_NOTIFY";

    /*
      *  Members
      */
    private CheckBox mAlwaysFinishCB;
    private boolean mAlwaysFinish;

    /*
      * Updates the system Always Finish setting
      */
    private void writeFinishOptions() {
        try {
            // Due to restrictions related to hidden APIs, need to emulate the line below
            // using reflection:
            // ActivityManagerNative.getDefault().setAlwaysFinish(mAlwaysFinish);
            final Class<?> classActivityManagerNative = Class.forName("android.app.ActivityManagerNative");
            final Method methodGetDefault = classActivityManagerNative.getMethod("getDefault");
            final Method methodSetAlwaysFinish = classActivityManagerNative.getMethod("setAlwaysFinish", new Class[]{boolean.class});
            final Object objectInstance = methodGetDefault.invoke(null);
            methodSetAlwaysFinish.invoke(objectInstance, new Object[]{mAlwaysFinish});
        } catch (Exception ex) {
            showAlert("Could not set always finish:\n\n" + ex, "Error");
        }
    }
    
    private void writeFinishOptionsForIntent(final Intent intent) {
        final Bundle extras = intent.getExtras();
        
        // Extras are required
        if (extras == null) {
            Log.e(LOG_TAG, "Extras required for intent with action " + intent.getAction());
            return;
        }
        
        // ALWAYSFINISH extra key is required
        if (!extras.containsKey(EXTRA_ALWAYSFINISH)) {
            Log.e(LOG_TAG, "Extra [" + EXTRA_ALWAYSFINISH + "] required for intent with action " + intent.getAction());
            return;
        }
        
        this.mAlwaysFinish = extras.getBoolean(EXTRA_ALWAYSFINISH);
        writeFinishOptions();
        
        // Optional extra key to Toast the result
        if (extras.containsKey(EXTRA_NOTIFY)) {
            if (extras.getBoolean(EXTRA_NOTIFY)) {
                updateFinishOptions();
                final String message = "Settings.System.ALWAYS_FINISH_ACTIVITIES = " + String.valueOf(mAlwaysFinish);
                Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
            }
        }
    }

    /*
      * Gets the latest AlwaysFinish value from the system and
      * updates the checkbox
      */
    private void updateFinishOptions() {
        mAlwaysFinish = Settings.System.getInt(getContentResolver(), Settings.System.ALWAYS_FINISH_ACTIVITIES, 0) != 0;
        if (mAlwaysFinishCB != null) {
            mAlwaysFinishCB.setChecked(mAlwaysFinish);
        }
    }

    /*
    * onClick handler for the AlwaysFinish checkbox
    */
    private View.OnClickListener mAlwaysFinishClicked =
            new View.OnClickListener() {
                public void onClick(View v) {
                    mAlwaysFinish = ((CheckBox) v).isChecked();
                    writeFinishOptions();
                    updateFinishOptions();
                }
            };

    /*
    * * Called when the activity is first created.
    */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        // Call base implementation
        super.onCreate(savedInstanceState);
        
        final Intent intent = getIntent();
        if (ACTION_SET.equals(intent.getAction())) {
            writeFinishOptionsForIntent(intent);
            finish();
        }

        // Set the content view
        setContentView(R.layout.main);

        // Initialize checkbox
        mAlwaysFinishCB = (CheckBox) findViewById(R.id.always_finish);
        mAlwaysFinishCB.setOnClickListener(mAlwaysFinishClicked);
    }

    /*
     * * Called when the activity resumes.
     */
    @Override
    protected void onResume() {
        super.onResume();
        updateFinishOptions();
    }

    /*
      * Displays an alert messagebox
      */
    private void showAlert(String message, String title) {
        if (!isFinishing()) {
            AlertDialog.Builder builder = new AlertDialog.Builder(SetAlwaysFinishActivity.this);
            builder.setMessage(message);
            builder.setCancelable(true);
            builder.setTitle(title);
            builder.setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int id) {

                }
            });
            AlertDialog alert = builder.create();
            alert.show();
        }
    }
}