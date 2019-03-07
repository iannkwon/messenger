package deo.com.signaldemo.fragment;


import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Toast;

import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.List;
import java.util.Vector;

import deo.com.signaldemo.MainActivity;
import deo.com.signaldemo.R;
import deo.com.signaldemo.SessionNow;
import deo.com.signaldemo.adapter.ProfileAdapter;
import deo.com.signaldemo.databinding.FragmentMyBinding;
import deo.com.signaldemo.item.Friends;
import deo.com.signaldemo.item.Profile;

import static com.firebase.ui.auth.AuthUI.TAG;


/**
 * A simple {@link Fragment} subclass.
 */
public class MyFragment extends Fragment {

    FragmentMyBinding binding;
    Vector<Profile> profiles;
    Context context;

    String UID;
    String[] myProfiles = new String[4];
    String[] friendEmail = new String[8];
    String[] friendNickname = new String[8];

    private FirebaseDatabase firebaseDatabase = FirebaseDatabase.getInstance();
    private DatabaseReference databaseReference = firebaseDatabase.getReference();

    public static MyFragment newInstance() {
        // Required empty public constructor
        Bundle args = new Bundle();

        MyFragment fragment = new MyFragment();
        fragment.setArguments(args);

        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        binding = DataBindingUtil.inflate(
                inflater, R.layout.fragment_my, container,false);
        Bundle args = getArguments();

        myProfiles[0] = args.getString("email");
        myProfiles[1] = args.getString("nickname");
        myProfiles[2] = args.getString("uid");
        UID = SessionNow.getSession(getActivity(),"uid");

        LinearLayoutManager manager = new LinearLayoutManager(getContext(), LinearLayoutManager.VERTICAL, false);
        binding.myListView.setLayoutManager(manager);

        viewProfile();

        View view = binding.getRoot();
        return view;
    }

    public void viewProfile(){
        profiles = new Vector<>();
        profiles.clear();
        profiles.add(new Profile("", 0, "My Profile", ""));
        profiles.add(new Profile("", 0, myProfiles[1], myProfiles[0]));
        profiles.add(new Profile("", 0, "Friend List", ""));
        binding.myListView.setAdapter(new ProfileAdapter(profiles, getContext(), getActivity()));

        searchFriendCount();
    }

    public void searchFriendCount(){
        databaseReference.child("users").child(UID)
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
//                        Log.d("ForSingle_get Key", Long.toString( dataSnapshot.child("friends").getChildrenCount()));
                        int friendCount = ((int) dataSnapshot.child("friends").getChildrenCount());
                        if(friendCount > 0){
                            searchFriend(friendCount);
                        }
                    }
                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });
    }

    public void searchFriend(final int friendCount){
        databaseReference.child("users").child(SessionNow.getSession(getContext(),"uid"))
                .child("friends").addListenerForSingleValueEvent(new ValueEventListener() {
            int i = 0;
            String[] friendList = new String[8];
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
//                Log.d("get Key", dataSnapshot.getChildren().iterator().next().getKey());

//                Log.d("children", Long.toString(dataSnapshot.getChildrenCount()));
//                Friends friends = dataSnapshot.getValue(Friends.class);
//                Log.d("friends>", friends.getNickname());
                for (DataSnapshot snapshot : dataSnapshot.getChildren()){
//                    Log.d("snapshot", snapshot.getKey());
                    friendList[i] = snapshot.getKey();
//                    Log.d("firend List_"+i, friendList[i]);
                    i++;
                }
                if (i >= friendCount){
                    addFriend(friendList, friendCount);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    int addFriendCount = 0;
    public void addFriend(String friendList[], final int friendCount){
//        Log.d("AddFirend", Integer.toString(friendCount));
        for(int i=0; i<friendCount; i++){
//            Log.d("addFriend cnt"+i, friendList[i]);

            databaseReference.child("users").child(UID)
                    .child("friends").child(friendList[i]).addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
//                    Log.d("addFriend getKey", dataSnapshot.getKey());

//                    Log.d("addFriendCount>", Integer.toString(addFriendCount));
                    Profile profile = dataSnapshot.getValue(Profile.class);
                    friendEmail[addFriendCount] = profile.getEmail();
                    friendNickname[addFriendCount] = profile.getNickname();
//                    Log.d("friens getEmail", friendEmail[addFriendCount]);
//                    Log.d("friens getNickname", friendNickname[addFriendCount]);
                    addFriendCount++;
                    if (addFriendCount >= friendCount){

                        for(int i=0; i<friendCount; i++){
                            profiles.add(new Profile("", R.drawable.profile_2, friendNickname[i], friendEmail[i]));
                            binding.myListView.setAdapter(new ProfileAdapter(profiles, getContext(), getActivity()));
                        }
                    }
                }
                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {
//                    Log.w(TAG, "getUser:onCancelled", databaseError.toException());
                }
            });
        } // end for문
    }// end addFriend

} // end class
