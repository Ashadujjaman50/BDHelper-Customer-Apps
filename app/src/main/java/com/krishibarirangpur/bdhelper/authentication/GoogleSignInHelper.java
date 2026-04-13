package com.krishibarirangpur.bdhelper.authentication;

import android.content.Context;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.credentials.Credential;
import androidx.credentials.CredentialManager;
import androidx.credentials.CustomCredential;
import androidx.credentials.GetCredentialRequest;
import androidx.credentials.GetCredentialResponse;
import androidx.credentials.exceptions.GetCredentialException;

import com.google.android.libraries.identity.googleid.GetGoogleIdOption;
import com.google.android.libraries.identity.googleid.GoogleIdTokenCredential;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;
import com.krishibarirangpur.bdhelper.R;

public class GoogleSignInHelper {

    private final Context context;
    private final FirebaseAuth firebaseAuth;
    private final CredentialManager credentialManager;
    private final OnGoogleSignInSuccessListener listener;

    public interface OnGoogleSignInSuccessListener {
        void onSignInSuccess(FirebaseUser user);
        void onSignInFailure(String errorMessage, Exception exception);
    }

    public GoogleSignInHelper(Context context, OnGoogleSignInSuccessListener listener) {
        this.context = context;
        this.listener = listener;
        this.firebaseAuth = FirebaseAuth.getInstance();
        this.credentialManager = CredentialManager.create(context);
    }

    public void startGoogleSignIn() {
        // ১. Google ID Token রিকোয়েস্ট তৈরি করা
        GetGoogleIdOption googleIdOption = new GetGoogleIdOption.Builder()
                .setFilterByAuthorizedAccounts(false)
                .setServerClientId(context.getString(R.string.default_web_client_id))
                .setAutoSelectEnabled(true)
                .build();

        // ২. সব অপশনকে একটি রিকোয়েস্টে যুক্ত করা
        GetCredentialRequest request = new GetCredentialRequest.Builder()
                .addCredentialOption(googleIdOption)
                .build();

        // ৩. ক্রেডেনশিয়াল ম্যানেজার কল করা
        credentialManager.getCredentialAsync(
                context,
                request,
                null,
                context.getMainExecutor(),
                new androidx.credentials.CredentialManagerCallback<GetCredentialResponse, GetCredentialException>() {
                    @Override
                    public void onResult(GetCredentialResponse result) {
                        handleSignInResult(result);
                    }

                    @Override
                    public void onError(@NonNull GetCredentialException e) {
                        listener.onSignInFailure("Credential Error: " + e.getMessage(), e);
                    }
                }
        );
    }

    private void handleSignInResult(GetCredentialResponse result) {
        try {
            Credential credential = result.getCredential();

            if (credential instanceof CustomCredential && 
                credential.getType().equals(GoogleIdTokenCredential.TYPE_GOOGLE_ID_TOKEN_CREDENTIAL)) {

                GoogleIdTokenCredential googleIdTokenCredential = GoogleIdTokenCredential.createFrom(credential.getData());
                String idToken = googleIdTokenCredential.getIdToken();

                if (idToken != null) {
                    AuthCredential firebaseCredential = GoogleAuthProvider.getCredential(idToken, null);
                    firebaseAuth.signInWithCredential(firebaseCredential)
                            .addOnCompleteListener(task -> {
                                if (task.isSuccessful()) {
                                    listener.onSignInSuccess(firebaseAuth.getCurrentUser());
                                } else {
                                    listener.onSignInFailure("Firebase Auth failed", task.getException());
                                }
                            });
                } else {
                    listener.onSignInFailure("ID Token is null.", null);
                }
            } else {
                listener.onSignInFailure("Unexpected credential type: " + credential.getType(), null);
            }
        } catch (Exception e) {
            listener.onSignInFailure("Error: " + e.getLocalizedMessage(), e);
        }
    }
}
