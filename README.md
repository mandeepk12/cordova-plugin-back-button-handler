### cordova-plugin-back-button-handler
This plugin provides a unified way to handle the hardware back button/gesture on Android 16 (API 36) and older versions. It resolves the common issue where Android 14+ devices minimize the app instead of triggering the JavaScript backbutton event.

### Why use this?
Android 16 Support: Implements the OnBackInvokedCallback system required for modern gesture navigation.
Legacy Support: Automatically falls back to standard behavior on Android 12 and below using tools:targetApi.
Prevent Default: Allows you to decide whether to consume the back event (e.g., to close a modal) or let the OS minimize/exit the app.

### Installation
````
cordova plugin add cordova-plugin-back-button-handler
````
or from repo
````
cordova plugin add https://github.com/mandeepk12/cordova-plugin-back-button-handler.git
````
### config.xml change
Add following to widget as given below
````
<widget ... xmlns:tools="http://schemas.android.com/tools">
</widget>
````

Inside Android platform config, add following

````
<platform name="android">
    <edit-config file="app/src/main/AndroidManifest.xml" mode="merge" target="/manifest">
        <manifest xmlns:tools="http://schemas.android.com/tools" />
    </edit-config>
    <edit-config file="app/src/main/AndroidManifest.xml" mode="merge" target="/manifest/application/activity[@android:name='MainActivity']">
        <activity android:enableOnBackInvokedCallback="true" tools:targetApi="33" />
    </edit-config>
</platform>
````

## Usage
1. Initialize and Subscribe
In your onDeviceReady function, initialize the handler.
````
document.addEventListener('deviceready', function() {
    
    // Set your custom handler
    cordova.plugins.BackButtonHandler.subscribe(function(e) {
        
        if (shouldHandleBackLocally()) {
            console.log("Internal navigation triggered");
            
            // PREVENT DEFAULT: This stops the app from minimizing/closing
            e.preventDefault(); 
            
            myAppNavigation.goBack();
        } else {
            console.log("App will exit/minimize normally");
            // By NOT calling e.preventDefault(), the plugin triggers the default exit behavior
        }
    });

}, false);

````
2. Dual-Event Support (Optional)
If you want to maintain the standard Cordova backbutton listener for very old legacy devices alongside this plugin:

````
function myUniversalHandler(e) {
    // e == backPressed
    if (canGoBackInApp) { // your logic
        e.preventDefault();
        // custom back logic
    }
}

document.addEventListener('deviceready', function() {
    if (cordova.plugins.BackButtonHandler) {
       cordova.plugins.BackButtonHandler.subscribe.(myUniversalHandler);
    } else {
        // Fallback for non-android or if plugin is missing
        document.addEventListener("backbutton", myUniversalHandler, false);
    }
}, false);

````

## How it Works
1. On Android 13 to 16: 
The plugin registers a OnBackInvokedCallback. When the user swipes back, the Java code sends a message to JavaScript. If your JS calls e.preventDefault(), the native code does nothing. If you don't, it calls navigator.app.exitApp().

2. On Android 12 and below: 
The tools:targetApi flag ensures the new callback system is disabled. The device continues to fire the standard Cordova backbutton event.

### Requirements
- Cordova Android: 12.0.0 or higher (Tested on 14.0.1)
- Compile SDK: 34 or higher (Recommended 36 for Android 16)
