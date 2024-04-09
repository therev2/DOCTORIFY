package com.example.myapplication;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.FirebaseException;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.PhoneAuthCredential;
import com.google.firebase.auth.PhoneAuthOptions;
import com.google.firebase.auth.PhoneAuthProvider;

import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.TimeUnit;

public class otp_screen extends AppCompatActivity {
    private static final String TAG = "OTPScreenActivity";
    EditText otp;
    String phone_number;
    Long timeoutSeconds = 60L;
    TextView resend_otp;
    String verification_code;
    PhoneAuthProvider.ForceResendingToken ResendingToken;

    Button verify_btn;
    FirebaseAuth mAuth = FirebaseAuth.getInstance();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_otp_screen);
        otp = findViewById(R.id.otp_entered);
        resend_otp = findViewById(R.id.resend);
        verify_btn = findViewById(R.id.verify);
        phone_number = getIntent().getStringExtra("phone");

        // Format the phone number for Firebase Authentication
        String formattedPhoneNumber = formatPhoneNumber(phone_number);

        send_otp(formattedPhoneNumber, false);

        verify_btn.setOnClickListener(v -> {
            String entered_otp = otp.getText().toString();
            PhoneAuthCredential credential = PhoneAuthProvider.getCredential(verification_code, entered_otp);
            signIn(credential);
        });

        resend_otp.setOnClickListener(v -> {
            send_otp(formattedPhoneNumber, true);
        });

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
    }

    private String formatPhoneNumber(String phoneNumber) {
        // Add your phone number formatting logic here
        // For example, if the phone number is an Indian number, you can add the country code:
        return "+91" + phoneNumber;
    }

    void send_otp(String phoneNumber, boolean isResend) {
        startResendTimer();
        setInProgress(true);

        PhoneAuthOptions.Builder builder =
                PhoneAuthOptions.newBuilder(mAuth)
                        .setPhoneNumber(phoneNumber)
                        .setTimeout(timeoutSeconds, TimeUnit.SECONDS)
                        .setActivity(this)
                        .setCallbacks(new PhoneAuthProvider.OnVerificationStateChangedCallbacks() {
                            @Override
                            public void onVerificationCompleted(@NonNull PhoneAuthCredential phoneAuthCredential) {
                                signIn(phoneAuthCredential);
                                setInProgress(false);
                            }

                            @Override
                            public void onVerificationFailed(@NonNull FirebaseException e) {
                                Log.e(TAG, "OTP Verification Failed: " + e.getMessage());
                                Toast.makeText(getApplicationContext(), "OTP Verification Failed", Toast.LENGTH_LONG).show();
                                setInProgress(false);
                            }

                            @Override
                            public void onCodeSent(@NonNull String s, @NonNull PhoneAuthProvider.ForceResendingToken forceResendingToken) {
                                verification_code = s;
                                ResendingToken = forceResendingToken;
                                Toast.makeText(getApplicationContext(), "OTP Sent Successfully", Toast.LENGTH_LONG).show();
                                setInProgress(false);
                            }
                        });
        if (isResend) {
            PhoneAuthProvider.verifyPhoneNumber(builder.setForceResendingToken(ResendingToken).build());
        } else {
            PhoneAuthProvider.verifyPhoneNumber(builder.build());
        }
    }

    void startResendTimer() {
        resend_otp.setEnabled(false);
        Timer timer = new Timer();
        timer.scheduleAtFixedRate(new TimerTask() {
            @Override
            public void run() {
                timeoutSeconds--;
                String message = getString(R.string.resend_otp_message, timeoutSeconds);
                resend_otp.setText(message);
                if (timeoutSeconds <= 0) {
                    timeoutSeconds = 60L;
                    timer.cancel();

                    runOnUiThread(() -> {
                        resend_otp.setEnabled(true);
                    });
                }
            }
        }, 0, 1000);
    }

    void setInProgress(boolean inProgress) {
        if (inProgress) {
            verify_btn.setVisibility(View.GONE);
        } else {
            verify_btn.setVisibility(View.VISIBLE);
        }
    }

    void signIn(PhoneAuthCredential phoneAuthCredential) {
        //login part
        setInProgress(true);
        mAuth.signInWithCredential(phoneAuthCredential).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if (task.isSuccessful()) {
                    setInProgress(false);
                    Intent intent = new Intent(otp_screen.this, HomePage.class);
                    startActivity(intent);
                    finish();
                } else {
                    Toast.makeText(getApplicationContext(), "OTP Verification failed", Toast.LENGTH_LONG).show();
                }
            }
        });
    }
}