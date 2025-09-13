package com.dropshep.bdhelper.authentication;

import android.app.Activity;
import android.content.Intent;
import android.content.IntentSender;
import android.widget.Toast;

import androidx.annotation.Nullable;

import com.dropshep.bdhelper.R;
import com.google.android.gms.auth.api.identity.BeginSignInRequest;
import com.google.android.gms.auth.api.identity.Identity;
import com.google.android.gms.auth.api.identity.SignInClient;
import com.google.android.gms.auth.api.identity.SignInCredential;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;

public class GoogleSignInHelper {

    private final Activity activity;
    private final FirebaseAuth firebaseAuth;
    private final SignInClient signInClient;
    private final BeginSignInRequest signInRequest;
    public static final int RC_GOOGLE_SIGN_IN = 1001;
    private final OnGoogleSignInSuccessListener listener;

    public interface OnGoogleSignInSuccessListener {
        void onSignInSuccess(FirebaseUser user);
        void onSignInFailure(String errorMessage);
    }

    public GoogleSignInHelper(Activity activity, OnGoogleSignInSuccessListener listener) {
        this.activity = activity;
        this.listener = listener;
        firebaseAuth = FirebaseAuth.getInstance();
        signInClient = Identity.getSignInClient(activity);

        signInRequest = new BeginSignInRequest.Builder()
                .setGoogleIdTokenRequestOptions(
                        BeginSignInRequest.GoogleIdTokenRequestOptions.builder()
                                .setSupported(true)
                                .setServerClientId(activity.getString(R.string.default_web_client_id))  // put client_id in strings.xml
                                .setFilterByAuthorizedAccounts(false)
                                .build()
                )
                .build();
    }

    public void startGoogleSignIn() {
        signInClient.beginSignIn(signInRequest)
                .addOnSuccessListener(result -> {
                    try {
                        activity.startIntentSenderForResult(
                                result.getPendingIntent().getIntentSender(),
                                RC_GOOGLE_SIGN_IN,
                                null, 0, 0, 0
                        );
                    } catch (IntentSender.SendIntentException e) {
                        listener.onSignInFailure("IntentSender error: " + e.getMessage());
                    }
                })
                .addOnFailureListener(e -> {
                    listener.onSignInFailure("Google Sign-In failed: " + e.getMessage());
                });
    }

    public void handleActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        if (requestCode != RC_GOOGLE_SIGN_IN || data == null) return;

        try {
            SignInCredential credential = signInClient.getSignInCredentialFromIntent(data);
            String idToken = credential.getGoogleIdToken();
            if (idToken != null) {
                AuthCredential firebaseCredential = GoogleAuthProvider.getCredential(idToken, null);
                firebaseAuth.signInWithCredential(firebaseCredential)
                        .addOnCompleteListener(activity, task -> {
                            if (task.isSuccessful()) {
                                FirebaseUser user = firebaseAuth.getCurrentUser();
                                listener.onSignInSuccess(user);
                            } else {
                                listener.onSignInFailure("Firebase sign-in failed.");
                            }
                        });
            } else {
                listener.onSignInFailure("ID Token is null.");
            }
        } catch (Exception e) {
            listener.onSignInFailure("Sign-in exception: " + e.getMessage());
        }
    }
}
