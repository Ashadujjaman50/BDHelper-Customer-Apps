package com.krishibarirangpur.bdhelper.utils.sharedWidget;

import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import com.krishibarirangpur.bdhelper.R;

public class ValidationClass {

    /**
     * Field validate করে error দেখাবে।
     * EditText আর TextView দুইটাই handle করবে।
     */
    public static boolean validateField(TextView field) {
        String text = field.getText().toString().trim();

        if (TextUtils.isEmpty(text)) {
            field.setBackgroundResource(R.drawable.bg_edit_text_error);

            field.addTextChangedListener(new TextWatcher() {
                @Override
                public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

                @Override
                public void onTextChanged(CharSequence s, int start, int before, int count) {
                    field.setBackgroundResource(R.drawable.bg_edit_text);
                }

                @Override
                public void afterTextChanged(Editable s) {}
            });

            return true; // invalid
        }

        return false; // valid
    }


    public static void setErrorWatcher(View view, boolean hasError) {

        if (view == null) return;

        if (hasError) {
            view.setBackgroundResource(R.drawable.bg_edit_text_error);

            if (view instanceof EditText) {
                EditText editText = (EditText) view;

                editText.addTextChangedListener(new TextWatcher() {
                    @Override
                    public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                        editText.setBackgroundResource(R.drawable.bg_edit_text);
                    }

                    @Override
                    public void onTextChanged(CharSequence s, int start, int before, int count) {}

                    @Override
                    public void afterTextChanged(Editable s) {}
                });
            }

        } else {
            view.setBackgroundResource(R.drawable.bg_edit_text);
        }
    }
}
