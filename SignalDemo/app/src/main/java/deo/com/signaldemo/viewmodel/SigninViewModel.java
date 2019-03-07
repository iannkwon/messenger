package deo.com.signaldemo.viewmodel;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.annotation.NonNull;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import deo.com.signaldemo.BaseActivity;
import deo.com.signaldemo.MainActivity;
import deo.com.signaldemo.SessionNow;
import deo.com.signaldemo.SignupActivity;
import deo.com.signaldemo.databinding.ActivitySigninBinding;
import deo.com.signaldemo.item.Member;

import static com.firebase.ui.auth.AuthUI.TAG;
import static deo.com.signaldemo.BaseActivity.userData;

public class SigninViewModel implements ViewModel {

    private Activity activity;
    private Context context;
    private ActivitySigninBinding binding;
//    private EditText editText_email;
//    private EditText editText_pw;
//    private Button btn_signin;
//    private TextView btn_signup;

    private FirebaseDatabase firebaseDatabase = FirebaseDatabase.getInstance();
    private DatabaseReference databaseReference = firebaseDatabase.getReference();

    public SigninViewModel(Activity activity, Context context, ActivitySigninBinding binding){
        this.activity = activity;
        this.context = context;
        this.binding = binding;
    }

    @Override
    public void onCreate() {
        btnClick();

    }

    public void btnClick(){
        binding.tvSignup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                context.startActivity(new Intent(context, SignupActivity.class));
            }
        });


        binding.btnLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!binding.etEmail.getText().toString().trim().equals("") &&
                        !binding.etPw.getText().toString().trim().equals("")&&
                        binding.etPw.toString().trim().length() > 5)
                {
                    SigninUser();
                } else if( binding.etPw.getText().toString().trim().length() < 6){
                    Toast.makeText(context.getApplicationContext(), "Password least 6 letter", Toast.LENGTH_SHORT).show();
                }
                else{
                    Toast.makeText(context.getApplicationContext(), "Fill in the blanks ", Toast.LENGTH_SHORT).show();
                }

            }
        });

        binding.cbId.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked){
                    SessionNow.setSession(context, "checkBox", "true");
                } else {
                    SessionNow.setSession(context, "checkBox", "false");
                }
            }
        });

        // check box ID save
        String checkBox = SessionNow.getSession(context, "checkBox");
        String email = SessionNow.getSession(context,"email");
        if (checkBox.equals("true")){
            binding.cbId.setChecked(true);
            if (!email.equals("")){
                binding.etEmail.setText(email);
                binding.etEmail.setSelection(email.length());
            }
        } else {
            binding.cbId.setChecked(false);
        }
    }

    @Override
    public void onResume() {

    }

    @Override
    public void onStart() {

    }

    @Override
    public void onPause() {

    }

    public void SigninUser(){
        final ProgressDialog loading;
        loading = ProgressDialog.show(activity, "Login Loading","Login Loading...",true, true);
        FirebaseAuth auth = FirebaseAuth.getInstance();
        auth.signInWithEmailAndPassword(binding.etEmail.getText().toString().trim(), binding.etPw.getText().toString())
                .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                       if (task.isSuccessful()){
//                           Log.d(TAG,"Successed");
                           String userUid = task.getResult().getUser().getUid();
                           readUser(userUid);
                           loading.dismiss();
                           Toast.makeText(context, "Successed sign in", Toast.LENGTH_SHORT).show();
                       }else {
                           loading.dismiss();
                           Toast.makeText(context, "Check your ID and PW", Toast.LENGTH_SHORT).show();
                           Log.d(TAG, "Failed. Check your ID and PW");
                       }
                    }
                });
    }

    public void readUser(final String userUid){
        databaseReference.child("users").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                Member member = dataSnapshot.child(userUid).getValue(Member.class);

                SessionNow.setSession(context, "email", member.getEmail());
                SessionNow.setSession(context, "nickname", member.getNickname());
                SessionNow.setSession(context, "uid", userUid);

                context.startActivity(new Intent(context, MainActivity.class));
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Log.w(TAG, "getUser:onCancelled", databaseError.toException());
            }
        });
    }

}
