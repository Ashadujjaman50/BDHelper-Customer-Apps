package com.krishibarirangpur.bdhelper.utils.sharedWidget;

import android.text.InputType;
import android.text.Spanned;
import android.text.method.NumberKeyListener;

public class AlphanumericKeyListener extends NumberKeyListener {

    private static AlphanumericKeyListener sInstance;

    private static final char[] ACCEPTABLE_CHARS =
            "ABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789".toCharArray();

    public static AlphanumericKeyListener getInstance() {
        if (sInstance == null) {
            sInstance = new AlphanumericKeyListener();
        }
        return sInstance;
    }

    @Override
    protected char[] getAcceptedChars() {
        return ACCEPTABLE_CHARS;
    }

    @Override
    public int getInputType() {
        return InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_FLAG_CAP_CHARACTERS;
    }

    @Override
    public CharSequence filter(CharSequence source, int start, int end,
                               Spanned dest, int dstart, int dend) {

        // lowercase থেকে uppercase conversion
        StringBuilder filtered = new StringBuilder();
        boolean modified = false;

        for (int i = start; i < end; i++) {
            char c = source.charAt(i);

            if (c >= 'a' && c <= 'z') {
                filtered.append(Character.toUpperCase(c));
                modified = true;
            } else if ((c >= 'A' && c <= 'Z') || (c >= '0' && c <= '9')) {
                filtered.append(c);
            } else {
                // invalid character - skip
                modified = true;
            }
        }

        if (!modified) {
            return null; // no changes needed
        }

        if (filtered.length() == 0) {
            return ""; // all characters were invalid
        }

        return filtered.toString();
    }
}