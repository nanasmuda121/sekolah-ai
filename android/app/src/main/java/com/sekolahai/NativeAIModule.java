package com.sekolahai;

import androidx.annotation.NonNull;
import com.facebook.react.bridge.Promise;
import com.facebook.react.bridge.ReactApplicationContext;
import com.facebook.react.bridge.ReactContextBaseJavaModule;
import com.facebook.react.bridge.ReactMethod;

public class NativeAIModule extends ReactContextBaseJavaModule {
    static {
        System.loadLibrary("native_ai");
    }

    public NativeAIModule(ReactApplicationContext reactContext) {
        super(reactContext);
    }

    @NonNull
    @Override
    public String getName() {
        return "NativeAIModule";
    }

    // Native JNI C++ methods
    private native boolean initModel(String modelPath);
    private native String generateResponse(String prompt);

    @ReactMethod
    public void initModel(String modelPath, Promise promise) {
        try {
            boolean success = initModel(modelPath);
            promise.resolve(success);
        } catch (Exception e) {
            promise.reject("INIT_ERROR", e.getMessage());
        }
    }

    @ReactMethod
    public void generateResponse(String prompt, Promise promise) {
        new Thread(() -> {
            try {
                String reply = generateResponse(prompt);
                promise.resolve(reply);
            } catch (Exception e) {
                promise.reject("GEN_ERROR", e.getMessage());
            }
        }).start();
    }
}
