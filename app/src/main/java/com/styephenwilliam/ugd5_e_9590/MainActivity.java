package com.styephenwilliam.ugd5_e_9590;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.NotificationCompat;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;

import org.jetbrains.annotations.NotNull;

public class MainActivity extends AppCompatActivity {

    private TextInputEditText email,password;
    private ProgressDialog progressDialog;
    private FirebaseAuth firebaseAuth;
    private  String CHANNEL_ID = "Channel 1";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        firebaseAuth = FirebaseAuth.getInstance();
        email = findViewById(R.id.email);
        password = findViewById(R.id.password);
        Button signIn = findViewById(R.id.btnSignIn);
        Button signUp = findViewById(R.id.btnSignUp);
        progressDialog = new ProgressDialog(this);

        signUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Register();
            }
        });

        signIn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Login();
            }
        });
    }

    private void Login() {
        String e = email.getText().toString();
        String p = password.getText().toString();

        if (TextUtils.isEmpty(e)){
            Toast.makeText(MainActivity.this, "Enter Your Email", Toast.LENGTH_SHORT)
                    .show();
            email.setError("Enter Your Email");
            return;
        } else if(!isValidEmail(e)){
            Toast.makeText(MainActivity.this, "Email Invalid", Toast.LENGTH_SHORT)
                    .show();
            email.setError("Email Invalid");
            return;
        } else if (TextUtils.isEmpty(p)){
            Toast.makeText(MainActivity.this, "Enter Your Password", Toast.LENGTH_SHORT)
                    .show();
            password.setError("Enter Your Password");
            return;
        }

        progressDialog.setMessage("Please wait...");
        progressDialog.show();
        progressDialog.setCanceledOnTouchOutside(false);
        firebaseAuth.signInWithEmailAndPassword(e,p)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()){
                            Toast.makeText(MainActivity.this,
                                    "Sign In Successfully",
                                    Toast.LENGTH_SHORT).show();

                            addNotification();
                            createNotificationChannel();

                            Intent intent = new Intent(MainActivity.this, LogoutActivity.class);
                            startActivity(intent);
                            finish();
                        } else{
                            Toast.makeText(MainActivity.this,
                                    "Sign In Failed",
                                    Toast.LENGTH_SHORT).show();
                        }
                        clearText();
                        progressDialog.dismiss();
                    }
                });
    }

    public void Register(){
        String e = email.getText().toString();
        String p = password.getText().toString();

        if (TextUtils.isEmpty(e)){
            Toast.makeText(MainActivity.this, "Enter Your Email", Toast.LENGTH_SHORT)
                    .show();
            email.setError("Enter Your Email");
            return;
        } else if(!isValidEmail(e)){
            Toast.makeText(MainActivity.this, "Email Invalid", Toast.LENGTH_SHORT)
                    .show();
            email.setError("Email Invalid");
            return;
        } else if (TextUtils.isEmpty(p)){
            Toast.makeText(MainActivity.this, "Enter Your Password", Toast.LENGTH_SHORT)
                    .show();
            password.setError("Enter Your Password");
            return;
        } else if(p.length()<6){
            Toast.makeText(MainActivity.this, "Password Lenght Should Be >= 6", Toast.LENGTH_SHORT)
                    .show();
            password.setError("Password Lenght Should Be >= 6");
            return;
        }

        progressDialog.setMessage("Please wait...");
        progressDialog.show();
        progressDialog.setCanceledOnTouchOutside(false);
        firebaseAuth.createUserWithEmailAndPassword(e,p)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                       if (task.isSuccessful()){
                           Toast.makeText(MainActivity.this,
                                   "Sign Up Successfully",
                                   Toast.LENGTH_SHORT).show();
                       } else{
                           Toast.makeText(MainActivity.this,
                                   "Sign Up Failed",
                                   Toast.LENGTH_SHORT).show();
                       }
                       clearText();
                       progressDialog.dismiss();
                    }
                });
    }

    @NotNull
    private Boolean isValidEmail(CharSequence charSequence){
       return (!TextUtils.isEmpty(charSequence) &&
               Patterns.EMAIL_ADDRESS.matcher(charSequence).matches());
    }

    private void clearText(){
        email.setText("");
        password.setText("");
    }

    private void createNotificationChannel() {
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            CharSequence name = "Channel 1";
            String description = "This is Channel 1";
            int importance = NotificationManager.IMPORTANCE_DEFAULT;
            NotificationChannel channel = new NotificationChannel(CHANNEL_ID, name, importance);
            channel.setDescription(description);

            NotificationManager notificationManager = getSystemService(NotificationManager.class);
            notificationManager.createNotificationChannel(channel);
        }
    }

    private void addNotification() {
        NotificationCompat.Builder builder = new NotificationCompat.Builder(this, CHANNEL_ID)
                .setSmallIcon(R.drawable.ic_launcher_background)
                .setContentTitle("Hello")
                .setContentText("Welcome Back, Please Enjoy Your Stay...")
                .setPriority(NotificationCompat.PRIORITY_DEFAULT);

        Intent notificationIntent = new Intent(this, LogoutActivity.class);
        PendingIntent contentIntent = PendingIntent.getActivity(this,0,notificationIntent, PendingIntent.FLAG_UPDATE_CURRENT);
        builder.setContentIntent(contentIntent);

        NotificationManager manager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        manager.notify(0, builder.build());
    }
}