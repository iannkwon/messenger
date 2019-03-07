package deo.com.signaldemo.viewmodel;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.FragmentTransaction;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Toast;

import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;


import deo.com.signaldemo.R;
import deo.com.signaldemo.SessionNow;
import deo.com.signaldemo.adapter.ListViewAdapter;
import deo.com.signaldemo.databinding.ActivitySearchBinding;
import deo.com.signaldemo.item.ListViewItem;

import static com.firebase.ui.auth.AuthUI.TAG;

public class SearchViewModel implements ViewModel {

    private Activity activity;
    private ListViewAdapter adapter;
    private ActivitySearchBinding binding;
    private Context context;

    private FirebaseDatabase firebaseDatabase = FirebaseDatabase.getInstance();
    private DatabaseReference databaseReference = firebaseDatabase.getReference();

    public SearchViewModel(Activity activity, ListViewAdapter adapter, ActivitySearchBinding binding) {
        this.activity = activity;
        this.adapter = adapter;
        this.binding = binding;
    }

    @Override
    public void onCreate() {
        Log.d("search uid", SessionNow.getSession(activity, "uid"));
        binding.tvSearch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                adapter.clearItem();
                adapter.notifyDataSetChanged();
                searchEmail();
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

    Boolean emailRedundancyCheck;
    // 중복 검사
    public void searchMyfirend(String email){
        databaseReference.child("users").child(SessionNow.getSession(activity,"uid"))
                .child("friends").orderByChild("email").equalTo(email).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                Log.d("dataSnapshot.exists()", Boolean.toString(dataSnapshot.exists()));
                emailRedundancyCheck = dataSnapshot.exists();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }


    // editText 받아서 email 찾기
    public void searchEmail(){
        databaseReference.child("users").orderByChild("email")
                .equalTo(binding.etSearch.getText().toString()).addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
                String userUid = dataSnapshot.getKey();
                searchUser(userUid);
            }

            @Override
            public void onChildChanged(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {

            }

            @Override
            public void onChildRemoved(@NonNull DataSnapshot dataSnapshot) {

            }

            @Override
            public void onChildMoved(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    // 검색결과 리스트뷰에 출력
    public void searchUser(final String userUid){
        databaseReference.child("users").child(userUid).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull final DataSnapshot dataSnapshot) {
                final  ListViewItem listViewItem = dataSnapshot.getValue(ListViewItem.class);


                // ListView 1개만 출력
                adapter.clearItem();
                adapter.addItem(listViewItem.getEmail(), listViewItem.getNickname());
                adapter.notifyDataSetChanged();
                Log.d("getEmail", listViewItem.getEmail());
                Log.d("getNic", listViewItem.getNickname());

                binding.listViewSearch.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                    @Override
                    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                        Log.d("getEmail", ((ListViewItem) adapter.getItem(position)).getEmail() );
                        Log.d("getNick", ((ListViewItem) adapter.getItem(position)).getNickname() );
                        Log.d("Uid>", SessionNow.getSession(activity, "uid"));
                        Log.d("listviewItem", listViewItem.getEmail());
                        Log.d ("is Empty", Boolean.toString(adapter.isEmpty()));

                        // 중복 검사
                        searchMyfirend(listViewItem.getEmail());

                        AlertDialog.Builder ab = new AlertDialog.Builder(activity, R.style.AlertDialogTheme);
                        ab.setTitle("Add Friend");
                        ab.setMessage("Are you add friend?");
                        ab.setCancelable(false);
                        ab.setPositiveButton("Add", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        if(listViewItem.getEmail().equals( SessionNow.getSession(activity,"email"))){
                                            Toast.makeText(activity.getApplicationContext(), "You can't added myself", Toast.LENGTH_SHORT).show();
                                        }else if ( emailRedundancyCheck ){
                                            Toast.makeText(activity.getApplicationContext(), "Already added", Toast.LENGTH_SHORT).show();
                                        }
                                        else {
                                            databaseReference.child("users").child(SessionNow.getSession(activity, "uid")).child("friends")
                                                    .push().setValue(listViewItem);
                                            Toast.makeText(activity.getApplicationContext(), "Added friend", Toast.LENGTH_SHORT).show();

                                        }

                                    }
                                })
                                .setNegativeButton("CANCEL", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {

                                    }
                                }).show();

                    }
                });

            }
            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Log.w(TAG, "getUser:onCancelled", databaseError.toException());
            }
        });


    }

    public void searchListener(View view) {
        switch (view.getId()) {

        }
    }

} // end class
