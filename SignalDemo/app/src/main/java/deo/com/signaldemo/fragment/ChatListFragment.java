package deo.com.signaldemo.fragment;


import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.constraint.solver.widgets.Snapshot;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.Date;
import java.util.Vector;

import deo.com.signaldemo.R;
import deo.com.signaldemo.SessionNow;
import deo.com.signaldemo.adapter.ChatListAdapter;
import deo.com.signaldemo.databinding.FragmentChatlistBinding;
import deo.com.signaldemo.item.Chat;
import deo.com.signaldemo.item.Profile;


/**
 * A simple {@link Fragment} subclass.
 */
public class ChatListFragment extends Fragment {
    public static ChatListFragment newInstance() {
        // Required empty public constructor
        Bundle args = new Bundle();

        ChatListFragment fragment = new ChatListFragment();
        fragment.setArguments(args);
        return fragment;
    }

    Vector<Profile> profiles;
    FragmentChatlistBinding binding;
    ChatListAdapter adapter;
    private FirebaseDatabase firebaseDatabase = FirebaseDatabase.getInstance();
    private DatabaseReference databaseReference = firebaseDatabase.getReference();

    String[] listNickname = new String[8];
    String[] listMessage = new String[8];
    String[] listEmoticon = new String[8];
    String[] listTime = new String[8];
    String[] listEmail = new String[8];
    int[] listIcon = new int[8];

    String uid;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_chatlist, container, false);

        LinearLayoutManager manager = new LinearLayoutManager(getContext(), LinearLayoutManager.VERTICAL, false);
        binding.myChatListView.setLayoutManager(manager);
        profiles = new Vector<>();
        uid = SessionNow.getSession(getContext(), "uid");

        chatCount();
//        chatList();

//        profiles.add(new Profile(R.drawable.profile_2,"lee", "Hello", "AM 11:12"));
//        binding.myChatListView.setAdapter(new ChatListAdapter(profiles, getContext(), getActivity()));
        View view = binding.getRoot();
        return view;
    }
    int chatFriendCount;
    public void chatCount(){
        databaseReference.child("users").child(uid)
                .child("message").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
//                Log.d("ChatCount", Long.toString(dataSnapshot.getChildrenCount()));
                chatFriendCount = ((int) dataSnapshot.getChildrenCount());
                if (chatFriendCount > 0){
                    chatList(chatFriendCount);
                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
            }
        });
    }

    public void chatList(final int chatFriendCount){
        databaseReference.child("users").child(SessionNow.getSession(getContext(), "uid"))
                .child("message").addChildEventListener(new ChildEventListener() {
            int listCount= 0;
            @Override
            public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
//                int chatFriendCount = chatCount();
//                msgList(dataSnapshot, listCount, chatFriendCount);
                for(DataSnapshot snapshot : dataSnapshot.getChildren()){
//                    Log.d(" snapshot.getKey", Boolean.toString(snapshot.getChildren().iterator().next().getKey().isEmpty()));
//                    Log.d(" snapshot.getKey", snapshot.getChildren().iterator().next()
//                            .getChildren().iterator().next().getKey());
                    for(DataSnapshot snapshot2 : snapshot.getChildren()){
//                        Log.d(" snapshot2.getKey", snapshot2.getKey());
//                        Log.d("exists", Boolean.toString(snapshot2.exists()));
                        Chat chat = snapshot2.getValue(Chat.class);

                        if(chat.getChat() != null){
                            listMessage[listCount] = chat.getChat();
                            listEmoticon[listCount] = null;
                        }else{
                            listEmoticon[listCount] = chat.getEmoticon();
                            listMessage[listCount] = null;
                        }
                        listTime[listCount] = chat.getTime();
                        listEmail[listCount] = chat.getEmail();
                    }
                }
                listNickname[listCount] = dataSnapshot.getKey();

//                Log.d("listNickname_"+listCount, listNickname[listCount]);
                if( listMessage[listCount] != null){
//                    Log.d("listMessage_"+listCount, listMessage[listCount]);
                }else{
//                    Log.d("listMessage_"+listCount, listEmoticon[listCount]);
                }

//                Log.d("listTime_"+listCount, listTime[listCount]);
//                Log.d("listEmail"+listCount, listEmail[listCount]);
                listCount++;

                if (listCount >= chatFriendCount){
                    profiles.clear();
                    for(int i = 0; i < listCount; i++){
                        if (listMessage[i] != null){
                            profiles.add(new Profile(R.drawable.profile_2,listNickname[i], listMessage[i], listTime[i], listEmail[i]));
                        }else{
                            profiles.add(new Profile(listNickname[i], listEmoticon[i], listTime[i], listEmail[i], R.drawable.profile_2));
                        }
                        binding.myChatListView.setAdapter(new ChatListAdapter(profiles, getContext(), getActivity()));
                    }
                    listCount = 0;
                }
            }
            int friendUpdateCount;
            @Override
            public void onChildChanged(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
//                Log.d("chatFriendCount", Integer.toString(chatFriendCount));

                databaseReference.child("users").child(uid)
                        .child("message").addListenerForSingleValueEvent(new ValueEventListener() {

                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
//                        Log.d("ChatUpdateCount", Long.toString(dataSnapshot.getChildrenCount()));
                        friendUpdateCount = ((int) dataSnapshot.getChildrenCount());

                    }
                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {
                    }
                });
                for(DataSnapshot snapshot : dataSnapshot.getChildren()){
                    for(DataSnapshot snapshot2 : snapshot.getChildren()){
//                        Log.d(" snapshot2.getKey", snapshot2.getKey());
                        Chat chat = snapshot2.getValue(Chat.class);
                        listMessage[listCount] = chat.getChat();
                        listTime[listCount] = chat.getTime();
                        listEmail[listCount] = chat.getEmail();
                    }
                }
                listNickname[listCount] = dataSnapshot.getKey();

//                Log.d("listNickname_"+listCount, listNickname[listCount]);
//                Log.d("listMessage_"+listCount, listMessage[listCount]);
//                Log.d("listTime_"+listCount, listTime[listCount]);
//                Log.d("listEmail"+listCount, listEmail[listCount]);
                listCount++;

                if (listCount >= friendUpdateCount){
                    profiles.clear();
                    for(int i = 0; i < listCount; i++){
                        profiles.add(new Profile(R.drawable.profile_2,listNickname[i], listMessage[i], listTime[i], listEmail[i]));
                        binding.myChatListView.setAdapter(new ChatListAdapter(profiles, getContext(), getActivity()));

                    }
                    listCount = 0;
//                    adapter.notifyDataSetChanged();
                }

            }

            @Override
            public void onChildRemoved(@NonNull DataSnapshot dataSnapshot) {
                databaseReference.child("users").child(uid)
                        .child("message").addListenerForSingleValueEvent(new ValueEventListener() {

                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
//                        Log.d("ChatUpdateCount", Long.toString(dataSnapshot.getChildrenCount()));
                        friendUpdateCount = ((int) dataSnapshot.getChildrenCount());

                    }
                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {
                    }
                });
                for(DataSnapshot snapshot : dataSnapshot.getChildren()){
                    for(DataSnapshot snapshot2 : snapshot.getChildren()){
//                        Log.d(" snapshot2.getKey", snapshot2.getKey());
                        Chat chat = snapshot2.getValue(Chat.class);
                        listMessage[listCount] = chat.getChat();
                        listTime[listCount] = chat.getTime();
                        listEmail[listCount] = chat.getEmail();
                    }
                }
                listNickname[listCount] = dataSnapshot.getKey();

//                Log.d("listNickname_"+listCount, listNickname[listCount]);
//                Log.d("listMessage_"+listCount, listMessage[listCount]);
//                Log.d("listTime_"+listCount, listTime[listCount]);
//                Log.d("listEmail"+listCount, listEmail[listCount]);
                listCount++;

                if (listCount >= friendUpdateCount){
                    profiles.clear();
                    for(int i = 0; i < listCount; i++){
                        profiles.add(new Profile(R.drawable.profile_2,listNickname[i], listMessage[i], listTime[i], listEmail[i]));
                        binding.myChatListView.setAdapter(new ChatListAdapter(profiles, getContext(), getActivity()));
                    }
                    listCount = 0;
                }

            }

            @Override
            public void onChildMoved(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

}
