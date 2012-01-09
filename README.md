SETALWAYSFINISH
===============

What is SetAlwaysFinish?
------------------------
SetAlwaysFinish is an Android utility app geared towards Android developers. It lets developers test whether the activities in their apps correctly process the `onSaveInstanceState` and `onRestoreInstanceState` lifecycle events. This is important because improper handling of these lifecycle events tends to result in random crashes for seemingly "unknown" reasons.

Usage
-----
1. Compile the SetAlwaysFinish app into an APK or get the pre-compiled APK from the release folder in this repository.
2. Install the APK on the device. For the install to work, you must make sure that the `Unknown Sources` checkbox is checked under `Settings > Applications`.
3. Start SetAlwaysFinish on the test device and make sure that the `Always Finish` checkbox is checked.
4. Run your app and go through every activity. Every time you create a new activity, the Android OS will unload the previous activity and trigger its `onSaveInstanceState` event. Every time you close an activity (rather than open a new activity on top of it), the Android OS will reload the activity immediately below the current activity in the activity stack and trigger its `onRestoreInstanceState` event.
5. To test the activities at the topmost level in your app's activity stack, you will need to have another external activity pop on top of them, so their `onSaveInstanceState` will trigger. The best way to do this is by launching a new app through ADB, as shown below.

    #### Get shell access via ADB
    adb shell

    #### Start the calculator application on the device
    am start -n com.android.calculator2/com.android.calculator2.Calculator

6. Once you are done testing your app, you can return to the SetAlwaysFinish app and uncheck the `Always Finish` checkbox to allow Android to optimize for speed once again.

Advanced Usage
--------------
Use the ADB shell command to launch SetAlwaysFinish, attempt to change the setting, and optionally notify the result

    $ adb shell 'am start -n "com.bricolsoftconsulting.setalwaysfinish/com.bricolsoftconsulting.setalwaysfinish.SetAlwaysFinishActivity" \
     -a "com.bricolsoftconsulting.setalwaysfinish.ACTION_SET" \
     --ez "com.bricolsoftconsulting.setalwaysfinish.EXTRA_ALWAYSFINISH" false \
     --ez "com.bricolsoftconsulting.setalwaysfinish.EXTRA_NOTIFY" true'

Uri Usage
---------
Use an Intent with the scheme `setalwaysfinish://`:

    $ adb shell 'am start -d "setalwaysfinish://?alwaysfinish=true&notify=true"'

Credits
-------
Original version based on source code from the DevTools app in the Android emulator.

Thanks to Rob Wilis (ohhorob) for his significant contributions to making this tool easily usable with automated testing.

Copyright
---------
Copyright 2011 Bricolsoft Consulting

Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with the License. You may obtain a copy of the License at

       http://www.apache.org/licenses/LICENSE-2.0

Unless required by applicable law or agreed to in writing, software distributed under the License is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the License for the specific language governing permissions and limitations under the License.