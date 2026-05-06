package com.krishibarirangpur.bdhelper.utils.sharedWidget;

import android.util.Pair;
import android.view.View;
import android.widget.TextView;


public class UIHelper {
    public static void bindAddress(TextView primaryView, TextView secondaryView, String address) {
        Pair<String, String> formatted = formatAddress(address);
        primaryView.setText(formatted.first);
        secondaryView.setVisibility(formatted.second.isEmpty() ? View.GONE : View.VISIBLE);
        if (!formatted.second.isEmpty()) {
            secondaryView.setText(formatted.second);
        }
    }

    public static Pair<String, String> formatAddress(String text) {
        if (text == null || text.isEmpty()) return new Pair<>("", "");

        String[] parts = text.split(",");

        if (parts.length == 1) {
            // কোন কমা নেই, সবটুকু প্রথম লাইনে
            return new Pair<>(text.trim(), "");
        }

        StringBuilder firstLine = new StringBuilder();
        StringBuilder secondLine = new StringBuilder();

        // মোট কমা count দেখে ভাগ করব
        int mid = parts.length / 2; // প্রায় দুই ভাগে ভাগ

        for (int i = 0; i < parts.length; i++) {
            if (i < mid) {
                if (firstLine.length() > 0) firstLine.append(", ");
                firstLine.append(parts[i].trim());
            } else {
                if (secondLine.length() > 0) secondLine.append(", ");
                secondLine.append(parts[i].trim());
            }
        }

        return new Pair<>(firstLine.toString(), secondLine.toString());
    }

}
