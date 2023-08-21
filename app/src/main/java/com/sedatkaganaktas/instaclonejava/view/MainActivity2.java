package com.sedatkaganaktas.instaclonejava.view;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.sedatkaganaktas.instaclonejava.databinding.ActivityMain2Binding;

public class MainActivity2 extends AppCompatActivity {

    private ActivityMain2Binding binding;
    private FirebaseAuth auth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding=ActivityMain2Binding.inflate(getLayoutInflater());
        View view= binding.getRoot();
        setContentView(view);

        auth=FirebaseAuth.getInstance();

        FirebaseUser user=auth.getCurrentUser();// daha önceden giriş yapmış kullanıcı varsa getirir. başlangıçta kontrol sağlar

        if(user!= null){  // kontrol sağladık
            Intent intent= new Intent(MainActivity2.this, FeedACtivity.class);
            startActivity(intent);
            finish();
        }
    }


    public void signinClicked (View view){
        String email = binding.emailText.getText().toString();
        String password= binding.passwordText.getText().toString();

        if(email.equals("")||password.equals("")){
            Toast.makeText(this,"Enter email and password",Toast.LENGTH_LONG).show();
        }else{
            auth.signInWithEmailAndPassword(email,password).addOnSuccessListener(new OnSuccessListener<AuthResult>() {
                @Override
                public void onSuccess(AuthResult authResult) {

                    Intent intent=new Intent(MainActivity2.this,FeedACtivity.class);
                    startActivity(intent);
                    finish();

                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    Toast.makeText(MainActivity2.this,e.getLocalizedMessage(),Toast.LENGTH_LONG).show();
                }
            });
        }


    }
    public void signupClicked (View view){

        String email = binding.emailText.getText().toString();
        String password= binding.passwordText.getText().toString();
        if (email.equals("")|| password.equals("")){
            Toast.makeText(this,"Enter email and password",Toast.LENGTH_LONG).show();
        }else{

            auth.createUserWithEmailAndPassword(email,password).addOnSuccessListener(new OnSuccessListener<AuthResult>() {
                @Override
                public void onSuccess(AuthResult authResult) {

                    Intent intent=new Intent(MainActivity2.this,FeedACtivity.class);
                    startActivity(intent);
                    finish();
                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    Toast.makeText(MainActivity2.this,e.getLocalizedMessage(),Toast.LENGTH_LONG).show();
                }
            });

        }

    }
}