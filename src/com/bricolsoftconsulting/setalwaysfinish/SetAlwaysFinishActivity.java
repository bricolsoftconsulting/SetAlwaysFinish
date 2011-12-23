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

import android.os.Bundle;
import android.provider.Settings;
import android.view.View;
import android.widget.CheckBox;

public class SetAlwaysFinishActivity extends Activity
{
	/*
	 *  Members
	 */
	private CheckBox mAlwaysFinishCB;
	private boolean mAlwaysFinish;
	
	/*
	 * Updates the system Always Finish setting
	 */
	private void writeFinishOptions()
	{
		try
		{
			// Due to restrictions related to hidden APIs, need to emulate the line below 
			// using reflection:
			// ActivityManagerNative.getDefault().setAlwaysFinish(mAlwaysFinish);
			final Class<?>   classActivityManagerNative = Class.forName("android.app.ActivityManagerNative");
			final Method     methodGetDefault = classActivityManagerNative.getMethod("getDefault");
			final Method     methodSetAlwaysFinish = classActivityManagerNative.getMethod("setAlwaysFinish", new Class[] {boolean.class});
			final Object     objectInstance = methodGetDefault.invoke(null);
			methodSetAlwaysFinish.invoke(objectInstance, new Object[]{mAlwaysFinish});
		}
		catch (Exception ex)
		{
			showAlert("Could not set always finish:\n\n" + ex, "Error");
		}
	}

	/*
	 * Gets the latest AlwaysFinish value from the system and 
	 * updates the checkbox
	 */
	private void updateFinishOptions()
	{
		mAlwaysFinish = Settings.System.getInt(getContentResolver(), Settings.System.ALWAYS_FINISH_ACTIVITIES, 0) != 0;
		mAlwaysFinishCB.setChecked(mAlwaysFinish);
	}
	
    /*
     * onClick handler for the AlwaysFinish checkbox 
     */
	private View.OnClickListener mAlwaysFinishClicked =
        new View.OnClickListener() {
			public void onClick(View v)
	    {
	        mAlwaysFinish = ((CheckBox)v).isChecked();
	        writeFinishOptions();
	        updateFinishOptions();
	    }
	};
	
    /*
     * * Called when the activity is first created.
     */
    @Override
    public void onCreate(Bundle savedInstanceState)
    {
    	// Call base implementation
        super.onCreate(savedInstanceState);
        
        // Set the content view
        setContentView(R.layout.main);
        
        // Initialize checkbox
        mAlwaysFinishCB = (CheckBox)findViewById(R.id.always_finish);
        mAlwaysFinishCB.setOnClickListener(mAlwaysFinishClicked);
    }

    /*
     * * Called when the activity resumes.
     */
	@Override
	protected void onResume()
	{
		super.onResume();
		updateFinishOptions();
	}
	
	/*
	 * Displays an alert messagebox
	 */
	private void showAlert(String message, String title)
	{
		if (!isFinishing())
		{
			AlertDialog.Builder builder = new AlertDialog.Builder(SetAlwaysFinishActivity.this);
			builder.setMessage(message);
			builder.setCancelable(true);
			builder.setTitle(title);
			builder.setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener()
			{
				public void onClick(DialogInterface dialog, int id)
				{
					
				}
			});
			AlertDialog alert = builder.create();
			alert.show();
		}
	}
}