package deo.com.signaldemo.viewmodel;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.text.SimpleDateFormat;
import java.util.Date;

import deo.com.signaldemo.SigninActivity;
import deo.com.signaldemo.item.Member;

import static com.firebase.ui.auth.AuthUI.TAG;
import static com.firebase.ui.auth.AuthUI.getApplicationContext;

public class SignupViewModel implements ViewModel {
    private EditText editText_email;
    private EditText editText_pw;
    private EditText editText_pwCon;
    private EditText editText_nick;
    private Button btn_send;
    private Context context;

    private FirebaseDatabase firebaseDatabase = FirebaseDatabase.getInstance();
    private DatabaseReference databaseReference = firebaseDatabase.getReference();

    public SignupViewModel(EditText editText_email, EditText editText_pw, EditText editText_pwCon,
                           EditText editText_nick, Button btn_send, Context context){
        this.editText_email = editText_email;
        this.editText_pw = editText_pw;
        this.editText_pwCon = editText_pwCon;
        this.editText_nick = editText_nick;
        this.btn_send = btn_send;
        this.context = context;
    }

    @Override
    public void onCreate() {
        btn_send.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                Log.d("editText_pw", editText_pw.getText().toString());
//                Log.d("editText_pwCon", editText_pwCon.getText().toString());
                if(editText_email.getText().toString().equals("") || editText_pw.getText().toString().equals("")
                        || editText_pwCon.getText().toString().equals("") || editText_nick.getText().toString().equals("")){
                    Toast.makeText(context.getApplicationContext(), "Fill in the blanks ", Toast.LENGTH_SHORT).show();
                }else if (!editText_pw.getText().toString().equals(editText_pwCon.getText().toString())){
                    Toast.makeText(context.getApplicationContext(), "The password is differnt", Toast.LENGTH_SHORT).show();
                }else {
                    signupUser();
                }

            }
        });
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

    public void signupUser(){
                final ProgressDialog loading;
                loading = ProgressDialog.show(context, "Sign Up Loading","Sign Up Loading...",true, true);
                final Date date = new Date(System.currentTimeMillis());
                final SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd h:mm:ss a");

                FirebaseAuth auth = FirebaseAuth.getInstance();
                auth.createUserWithEmailAndPassword(editText_email.getText().toString().trim(), editText_pw.getText().toString())
                        .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                            @Override
                            public void onComplete(@NonNull Task<AuthResult> task) {
                                if(task.isSuccessful()){
//                                    Log.d(TAG, "Successfully signed in with email link!");
                                    FirebaseUser user = task.getResult().getUser();
                                    Member member = new Member(user.getEmail(), editText_pw.getText().toString(),
                                            editText_nick.getText().toString(), sdf.format(date), null);
                                    databaseReference.child("users").child(user.getUid()).setValue(member);
                                    Toast.makeText(context.getApplicationContext(), "Sign Up Successed", Toast.LENGTH_SHORT).show();

                                    loading.dismiss();
                                    context.startActivity(new Intent(context,SigninActivity.class));

                                }
                                else{
//                                    Log.e(TAG, "Error signing in with email link: "
//                                            + task.getException().getMessage());
                                    Toast.makeText(context.getApplicationContext(), "Sign Up Failed", Toast.LENGTH_SHORT).show();
                                }
                            }
                        }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                    }
                });
    }
}
