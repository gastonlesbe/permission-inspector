package com.griscal.permissioninspector.util;

import android.view.View;

import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

/**
 * With targetSdk 36 (Android 15+), content draws edge-to-edge by default, so fixed top/bottom
 * bars can land behind the status bar or the gesture/navigation bar. These add the system
 * bars' inset on top of whatever padding the view already has in XML.
 */
public class InsetsHelper {

    public static void applyTopInset(View view) {
        int baseTop = view.getPaddingTop();
        int left = view.getPaddingLeft(), right = view.getPaddingRight(), bottom = view.getPaddingBottom();
        ViewCompat.setOnApplyWindowInsetsListener(view, (v, insets) -> {
            Insets bars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(left, baseTop + bars.top, right, bottom);
            return insets;
        });
    }

    public static void applyBottomInset(View view) {
        int baseBottom = view.getPaddingBottom();
        int left = view.getPaddingLeft(), top = view.getPaddingTop(), right = view.getPaddingRight();
        ViewCompat.setOnApplyWindowInsetsListener(view, (v, insets) -> {
            Insets bars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(left, top, right, baseBottom + bars.bottom);
            return insets;
        });
    }
}
