package com.intellij.plugins.serialmonitor.ui;

import com.intellij.AbstractBundle;
import com.intellij.reference.SoftReference;

import java.lang.ref.Reference;
import java.util.ResourceBundle;

/**
 * @author Dmitry_Cherkas
 */
public class SerialMonitorBundle {
    private static Reference<ResourceBundle> myBundle;

    private static final String BUNDLE = SerialMonitorBundle.class.getSimpleName();

    public static String message(String key, Object... params) {
        return AbstractBundle.message(getBundle(), key, params);
    }

    private static ResourceBundle getBundle() {
        ResourceBundle bundle = null;
        if (myBundle != null) {
            bundle = myBundle.get();
        }
        if (bundle == null) {
            bundle = ResourceBundle.getBundle(BUNDLE);
            myBundle = new SoftReference<>(bundle);
        }
        return bundle;
    }
}
