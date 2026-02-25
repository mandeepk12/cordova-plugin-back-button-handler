package com.betasoft.cordova.plugin.backbuttonhandler;

import org.apache.cordova.CordovaPlugin;
import org.apache.cordova.CallbackContext;

import org.apache.cordova.PluginResult;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.util.Log;
import android.window.OnBackInvokedCallback;
import android.window.OnBackInvokedDispatcher;
import android.os.Build;

import androidx.activity.OnBackPressedCallback;
import androidx.appcompat.app.AppCompatActivity;

/**
 * This class echoes a string called from JavaScript.
 */
public class BackButtonHandler extends CordovaPlugin {
    private CallbackContext eventCallback;
    private static final String TAG = "BackButtonPlugin";

    @Override
    public boolean execute(String action, JSONArray args, CallbackContext callbackContext) throws JSONException {
        if ("listen".equals(action)) {
            this.eventCallback = callbackContext;
            Activity activity = cordova.getActivity();

            activity.runOnUiThread(() -> {
                if (Build.VERSION.SDK_INT >= 33) {
                    try {
                        // Register with new API
                        activity.getOnBackInvokedDispatcher().registerOnBackInvokedCallback(
                                OnBackInvokedDispatcher.PRIORITY_DEFAULT,
                                new OnBackInvokedCallback() {
                                    @Override
                                    public void onBackInvoked() {
                                        sendBackEvent();
                                    }
                                }
                        );
                        Log.d("BackButtonPlugin", "Registered OnBackInvokedDispatcher");
                    } catch (Exception e) {
                        Log.w("BackButtonPlugin", "OnBackInvokedDispatcher failed, falling back", e);
                        if (activity instanceof AppCompatActivity) {
                            AppCompatActivity compatActivity = (AppCompatActivity) activity;
                            compatActivity.getOnBackPressedDispatcher().addCallback(
                                    compatActivity,
                                    new OnBackPressedCallback(true) {
                                        @Override
                                        public void handleOnBackPressed() {
                                            sendBackEvent();
                                        }
                                    }
                            );
                        }
                    }
                } else {
                    // For API < 33
                    if (activity instanceof AppCompatActivity) {
                        AppCompatActivity compatActivity = (AppCompatActivity) activity;
                        compatActivity.getOnBackPressedDispatcher().addCallback(
                                compatActivity,
                                new OnBackPressedCallback(true) {
                                    @Override
                                    public void handleOnBackPressed() {
                                        sendBackEvent();
                                    }
                                }
                        );
                        Log.d("BackButtonPlugin", "Registered OnBackPressedDispatcher");
                    }
                }
            });

            // Keep callback alive for multiple events
            PluginResult pluginResult = new PluginResult(PluginResult.Status.NO_RESULT);
            pluginResult.setKeepCallback(true);
            callbackContext.sendPluginResult(pluginResult);

            return true;
        }
        return false;
    }

    private void sendBackEvent() {
        this.sendBackEvent(null);
    }

    private void sendBackEvent(Object ignored) {
        if (eventCallback != null) {
            Log.d(TAG, "Back button pressed, sending event to JS"); // Debug log
            PluginResult result = new PluginResult(PluginResult.Status.OK, "backPressed");
            result.setKeepCallback(true);
            eventCallback.sendPluginResult(result);
        } else {
            Log.w(TAG, "Back button pressed but no callback registered"); // Warning log
        }

    }

}
