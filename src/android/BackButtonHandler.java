package com.betasoft.cordova.plugin.backbuttonhandler;

import org.apache.cordova.CordovaPlugin;
import org.apache.cordova.CallbackContext;

import org.apache.cordova.PluginResult;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.window.OnBackInvokedCallback;
import android.window.OnBackInvokedDispatcher;
import android.os.Build;

/**
 * This class echoes a string called from JavaScript.
 */
public class BackButtonHandler extends CordovaPlugin {
    private CallbackContext eventCallback;

    @Override
    public boolean execute(String action, JSONArray args, CallbackContext callbackContext) throws JSONException {
       if (action.equals("listen")) {
            this.eventCallback = callbackContext;
            
            // Handle Android 13 (API 33) to Android 16 (API 36)
            if (Build.VERSION.SDK_INT >= 33) {
                cordova.getActivity().runOnUiThread(() -> {
                    cordova.getActivity().getOnBackInvokedDispatcher().registerOnBackInvokedCallback(
                        OnBackInvokedDispatcher.PRIORITY_DEFAULT,
                            this::sendBackEvent
                    );
                });
            }
            // For API < 33, Cordova's core handles onBackPressed and fires the standard "backbutton" event.
            return true;
        }
        return false;
    }

    private void sendBackEvent() {
        if (eventCallback != null) {
            PluginResult result = new PluginResult(PluginResult.Status.OK, "backPressed");
            result.setKeepCallback(true);
            eventCallback.sendPluginResult(result);
        }
    }
}
