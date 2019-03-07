package deo.com.signaldemo;

import android.accessibilityservice.AccessibilityService;
import android.app.ActionBar;
import android.app.Activity;
import android.app.Service;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.Context;
import android.content.Intent;
import android.content.res.AssetManager;
import android.databinding.DataBindingUtil;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Rect;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.FragmentActivity;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.text.Html;
import android.text.Spanned;
import android.util.Log;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.view.Window;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.Charset;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.StringTokenizer;
import java.util.Timer;
import java.util.TimerTask;
import java.util.UUID;
import java.util.Vector;

import deo.com.signaldemo.adapter.ChatAdapter;
import deo.com.signaldemo.adapter.EmoticonsGridAdapter;
import deo.com.signaldemo.adapter.EmoticonsPagerAdapter;
import deo.com.signaldemo.databinding.ActivityChatroomBinding;
import deo.com.signaldemo.databinding.ItemChatBinding;
import deo.com.signaldemo.item.Chat;
import deo.com.signaldemo.service.BluetoothService;
import deo.com.signaldemo.viewmodel.EmoticonsViewModel;

import static deo.com.signaldemo.DeviceListActivity.EXTRA_BLE_ADDRESS;
import static deo.com.signaldemo.DeviceListActivity.EXTRA_DEVICE_ADDRESS;

public class ChatRoomActivity extends AppCompatActivity implements EmoticonsGridAdapter.KeyClickListener {

    private static final String TAG = "ChatRoomActivity";
    ActivityChatroomBinding binding;
    private static final int CHATROOM = R.layout.activity_chatroom;

    private Vector<Chat> chats;
    private ChatAdapter adapter;
    private FirebaseDatabase firebaseDatabase = FirebaseDatabase.getInstance();
    private DatabaseReference databaseReference = firebaseDatabase.getReference();
    private String name;
    private int img;
    private Handler handler;

    private static final int NO_OF_EMOTICONS = 36;
    private Bitmap[] emoticons;
    private View popUpView;
    private int keyboardHeight;
    private PopupWindow popupWindow;
    private boolean isKeyBoardVisible;

    // RFCOMM Protocol
    private static final UUID MY_UUID = UUID.fromString("00001101-0000-1000-8000-00805F9B34FB");
    // Intent request code
    private static final int REQUEST_CONNECT_DEVICE = 1;
    private static final int REQUEST_ENABLE_BT = 2;
    private BluetoothService bluetoothService = null;
    private BluetoothAdapter bluetoothAdapter;
    public static Context context;
    String deviceAddress;
    String bleAddress;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE); // delete title bar
        binding = DataBindingUtil.setContentView(this, CHATROOM);
        name = getIntent().getStringExtra("fName");
        img = getIntent().getIntExtra("fImg", 0);

        context = this;
        setResult(Activity.RESULT_CANCELED);
//        Log.d("Chat Room Uid>>", SessionNow.getSession(this, "uid"));
        OpenChat(name, img);
        sendChat();
        clickEvents();
        emoticonViewInit();

        bluetoothInit();

    }

    public void bluetoothInit(){
        deviceAddress = SessionNow.getSession(getApplicationContext(), EXTRA_DEVICE_ADDRESS);
        bleAddress = SessionNow.getSession(getApplicationContext(), EXTRA_BLE_ADDRESS);
    }

        @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_actionbar, menu);
            if (((MainActivity)MainActivity.context).isServiceRunning()){
                if (((BluetoothService)BluetoothService.context).getState() < 3){
                    menu.findItem(R.id.action_button).setIcon(R.drawable.ic_bluetooth_device_disconnected);
                }else{
                    menu.findItem(R.id.action_button).setIcon(R.drawable.ic_bluetooth_device);
                }
            }else{
                menu.findItem(R.id.action_button).setIcon(R.drawable.ic_bluetooth_device_disconnected);
            }
        return true;
    }

    /**
     * Overriding onKeyDown for dismissing keyboard on key down
     */
//    @Override
//    public boolean onKeyDown(int keyCode, KeyEvent event) {
//        if (popupWindow.isShowing()) {
//            popupWindow.dismiss();
//            return false;
//        } else {
//            return super.onKeyDown(keyCode, event);
//        }
//    }
    public void clickEvents(){
        // 키보드 올라 왔을 때 화면 조정
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE );
        binding.backChatRoomListImgView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });
        // 리사이클러뷰 클릭 시 키보드 숨기기
        binding.chatRoomListView.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                InputMethodManager imm = (InputMethodManager)getSystemService(INPUT_METHOD_SERVICE);
                imm.hideSoftInputFromWindow(binding.msgEditText.getWindowToken(),0);
                return false;
            }
        });
        // 키보드 올라왔을 때 최신 스크롤
        binding.msgEditText.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                handler = new Handler();
                handler.postDelayed(mrun, 500);
                return false;
            }
        });

    }

    @Override
    public void onBackPressed() {
        Intent intent = new Intent(ChatRoomActivity.this, MainActivity.class);
        intent.putExtra("position", 1);
        startActivity(intent);
        finish();
    }
    Runnable mrun = new Runnable() {
        @Override
        public void run() {
            binding.chatRoomListView.scrollToPosition(adapter.getItemCount()-1);
        }
    };

    private void OpenChat(String name, int img){
        chats = new Vector<>();
        LinearLayoutManager manager = new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false);
        binding.chatRoomListView.setLayoutManager(manager);
        binding.chatRoomFriendNameTxtView.setText(name);

        adapter = new ChatAdapter(chats, this);
        binding.chatRoomListView.setAdapter(adapter);

        databaseReference.child("users").child(SessionNow.getSession(this, "uid"))
                .child("message").child(name).addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
//                Log.d("OpenChat>", dataSnapshot.getValue().toString());
                for(DataSnapshot snapshot : dataSnapshot.getChildren()){
//                    Log.d("openChat snap", snapshot.getKey());
//                    Log.d("openChat snapGetValue", snapshot.getValue().toString());
                    Chat chat = snapshot.getValue(Chat.class);
//                    chats.clear();
                    if (chat.getChat() != null){
                        chats.add(new Chat(chat.getFriend(), chat.getChat(), chat.getWho(), chat.getProfile()));
                    }else{
//                        emoticonsProcess(chat);
                        chats.add(new Chat(chat.getFriend(), chat.getProfile(), chat.getEmoticon(), chat.getWho()));
                    }
                    binding.chatRoomListView.scrollToPosition(adapter.getItemCount()-1);
                    adapter.notifyDataSetChanged();
                }
//                Log.d("Open Chatting", dataSnapshot.getKey());
            }

            @Override
            public void onChildChanged(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
//                Chat chat = dataSnapshot.getValue(Chat.class);
//                Log.d("change vale", chat.getChat());
                String getFriend = null;
                String getChat = null;
                String getEmoticon = null;
                int getWho = 0;
                int getProfile = 0;

                for(DataSnapshot snapshot : dataSnapshot.getChildren()){
//                    Log.d("iterator().next()", Boolean.toString());
//                        Log.d("Changed snap", snapshot.getKey());
//                        Log.d("Changed snapGetValue", snapshot.getValue().toString());
                        Chat chat = snapshot.getValue(Chat.class);
//                        Log.d("change values", chat.getChat());
                        getFriend = chat.getFriend();
                        getChat = chat.getChat();
                        getEmoticon = chat.getEmoticon();
                        getWho = chat.getWho();
                        getProfile = chat.getProfile();
                }

//                Log.d("Change getChat", getChat);
                if (getChat != null){
                    chats.add(new Chat(getFriend, getChat, getWho, getProfile));
                }else{
                    chats.add(new Chat(getFriend, getProfile, getEmoticon, getWho));
                }

                binding.chatRoomListView.scrollToPosition(adapter.getItemCount()-1);
                adapter.notifyDataSetChanged();
//                Log.d("Changed getKey", dataSnapshot.getValue().toString());
//                Chat chat = dataSnapshot.getValue(Chat.class);
//                chats.add(new Chat(chat.getFriend(), chat.getChat(), chat.getWho(), chat.getProfile()));
//                binding.chatRoomListView.scrollToPosition(adapter.getItemCount()-1);
//                adapter.notifyDataSetChanged();
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

//    private void emoticonsProcess(final Chat chat){
//        Html.ImageGetter imageGetter = new Html.ImageGetter() {
//            public Drawable getDrawable(String source) {
//                StringTokenizer st = new StringTokenizer(chat.getEmoticon(), ".");
//                Drawable d = new BitmapDrawable(getResources(), emoticons[Integer.parseInt(st.nextToken()) - 1]);
//                d.setBounds(0, 0, d.getIntrinsicWidth(), d.getIntrinsicHeight());
//                Log.d("KeyIndex", chat.getEmoticon());
//                return d;
//            }
//        };
//        Spanned cs = Html.fromHtml("<img src ='"+ chat.getEmoticon() +"'/>", imageGetter, null);
//
//
////        ArrayList emoticonList = new ArrayList<Spanned>();
////        emoticonList.add(cs);
//
//        chats.add(new Chat(chat.getFriend(), chat.getProfile(), chat.getEmoticon(), chat.getWho()));
//    }

    private void sendChat(){
        // send button
        EmoticonsViewModel emoticonsViewModel = new EmoticonsViewModel();

        binding.sendMsgBtn.setOnClickListener(new View.OnClickListener() {
            String msg;
            Date date;
            SimpleDateFormat sdf;
            SimpleDateFormat sdf2;

            @Override
            public void onClick(View view) {
                msg = binding.msgEditText.getText().toString();
                date = new Date(System.currentTimeMillis());
                sdf = new SimpleDateFormat("yyyy-MM-dd");
                sdf2 = new SimpleDateFormat("h:mm a");
                if (!msg.equals("")){
                    Chat chat = new Chat(name, msg, 0, img, getIntent().getStringExtra("fEmail"), sdf2.format(date));
//                    chats.add(new Chat(name, msg, 0, 0));
                    databaseReference.child("users").child(SessionNow.getSession(getApplicationContext(), "uid"))
                            .child("message").child(chat.getFriend()).child(sdf.format(date)).push().setValue(chat); // Add Database
                    Log.d("ChatMsg>", msg);
                    databaseReference.child("users").orderByChild("email").equalTo(getIntent().getStringExtra("fEmail"))
                            .addListenerForSingleValueEvent(new ValueEventListener() {
                                @Override
                                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                    Log.d("SendChat>", dataSnapshot.getKey());
                                    for (DataSnapshot snapshot : dataSnapshot.getChildren()){
                                        Log.d("SendChat>", snapshot.getKey());
                                        Chat chat = new Chat(SessionNow.getSession(getApplicationContext(), "nickname"), msg, 1,
                                                0, SessionNow.getSession(getApplicationContext(), "email"), sdf2.format(date));
                                        databaseReference.child("users").child(snapshot.getKey())
                                                .child("message").child(chat.getFriend()).child(sdf.format(date)).push().setValue(chat);
                                    }
                                }
                                @Override
                                public void onCancelled(@NonNull DatabaseError databaseError) {
                                }
                            });
                    binding.chatRoomListView.scrollToPosition(chats.size()-1);
                    binding.msgEditText.setText(null);
                    adapter.notifyDataSetChanged();

                }

            }
        });
    }

    public void emoticonViewInit(){
        popUpView = getLayoutInflater().inflate(R.layout.emoticons_popup, null);
        popupWindow = new PopupWindow(popUpView, LinearLayout.LayoutParams.WRAP_CONTENT, ActionBar.LayoutParams.WRAP_CONTENT);

        // Defining default height of keyboard which is equal to 230 dip
        final float popUpheight = getResources().getDimension(
                R.dimen.keyboard_height);
        changeKeyboardHeight((int) popUpheight);

        Log.d("popUpheigt", Float.toString(popUpheight));
        Log.d("popupWindow", popupWindow.toString());

        binding.emoticons.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                InputMethodManager imm = (InputMethodManager)getSystemService(INPUT_METHOD_SERVICE);
                imm.toggleSoftInput(InputMethodManager.SHOW_FORCED,InputMethodManager.HIDE_IMPLICIT_ONLY);

                if (!popupWindow.isShowing()) {

                    popupWindow.setHeight((int) (keyboardHeight));

                    if (isKeyBoardVisible) {
                        binding.footerForEmoticons.setVisibility(LinearLayout.GONE);
                    } else {
                        binding.footerForEmoticons.setVisibility(LinearLayout.VISIBLE);
                    }
                    popupWindow.showAtLocation( binding.bottomMsgLayout, Gravity.BOTTOM, 0, 0);


                } else {
                    popupWindow.dismiss();
                }
            }
        });
        readEmoticons();
        enablePopUpView();
        checkKeyboardHeight(binding.bottomMsgLayout);
//        enableFooterView();
    }

    private void readEmoticons () {

        emoticons = new Bitmap[NO_OF_EMOTICONS];
        for (short i = 0; i < NO_OF_EMOTICONS; i++) {
            emoticons[i] = getImage((i+1) + ".png");
        }

    }

    @Override
    public void keyClickedIndex(final String index) {
        Html.ImageGetter imageGetter = new Html.ImageGetter() {
            public Drawable getDrawable(String source) {
                StringTokenizer st = new StringTokenizer(index, ".");
                Drawable d = new BitmapDrawable(getResources(), emoticons[Integer.parseInt(st.nextToken()) - 1]);
                d.setBounds(0, 0, d.getIntrinsicWidth(), d.getIntrinsicHeight());
//                Log.d("KeyIndex", index);
                return d;
            }
        };

        sendEmoticon(index);
//        Spanned cs = Html.fromHtml("<img src ='"+ index +"'/>", imageGetter, null);
//        int cursorPosition = binding.msgEditText.getSelectionStart();
//        binding.msgEditText.getText().insert(cursorPosition, cs);
    }

//    private Timer timer;
//    private TimerTask timerTask;
//    int cnt = 0;
//    private void sendData(final char[] datas, final int length){
//                timer = new Timer();
//                timerTask = new TimerTask() {
//                    @Override
//                    public void run() {
//
//                        ChatRoomActivity.this.runOnUiThread(new Runnable() {
//
//                            @Override
//                            public void run() {
//
//                                if (cnt < length) {
//                                    bluetoothService.write(Character.toString(datas[cnt]).getBytes());
//                                    Log.d("timer cnt", Integer.toString(cnt));
//                                }else{
//                                    timerTask.cancel();
//                                    cnt = -1;
//                                }
//                                cnt++;
//                            }
//                        });
//                    }
//                };
//                timer.schedule(timerTask, 0, 300);    // 60초 후부터 60초 마다
//                }

    public String splitProcess(String data){
        String[] split = data.split(":");
        String macAddr = "";
        for (int i=0; i < split.length; i++){
            macAddr += split[i];
        }
        return macAddr;
    }

    public void sendEmoticon(final String index){
        final String msg = null;
        final SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");;
        final SimpleDateFormat sdf2 = new SimpleDateFormat("h:mm a");;
        final Date date = new Date(System.currentTimeMillis());

//        if (bluetoothService.getState() != 3){
//            Toast.makeText(getApplicationContext(), "Check plase Bluetooth Connection status", Toast.LENGTH_SHORT).show();
//        }
        int idx = index.indexOf(".");
        final String emoticonNum = index.substring(0, idx);

        int ch = Integer.parseInt(emoticonNum);
        String str = Integer.toHexString(ch);

        String bleAddr = "0000"+splitProcess(bleAddress);
        String deviceAddr = "0000"+splitProcess(deviceAddress);

        if (str.length() < 2){
            str = "0"+str;
        }
        str = "02"+bleAddr+deviceAddr+"0040"+"0001"+str+"03";
//        Log.d("str length_"+str.length()+":", str);
//        char[] data = new char[str.length()];
//        for (int i=0; i<str.length(); i++){
//            data[i]  = str.charAt(i);
////            Log.d("data_"+i, Character.toString(data[i]));
//        }
//        sendData(data, str.length());

        Chat chat = new Chat(name, msg, emoticonNum, 0, img, getIntent().getStringExtra("fEmail"), sdf2.format(date));
        if (((MainActivity)MainActivity.context).isServiceRunning()){
            ((BluetoothService)BluetoothService.context).write(str.getBytes());
        } else {
            Toast.makeText(getApplicationContext(), "Check bluetooth connection status", Toast.LENGTH_SHORT).show();
        }
        databaseReference.child("users").child(SessionNow.getSession(getApplicationContext(), "uid"))
                .child("message").child(chat.getFriend()).child(sdf.format(date)).push().setValue(chat); // Add Database
        Log.d("emoticonNum>", emoticonNum);
        databaseReference.child("users").orderByChild("email").equalTo(getIntent().getStringExtra("fEmail"))
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        Log.d("SendChat>", dataSnapshot.getKey());
                        for (DataSnapshot snapshot : dataSnapshot.getChildren()){
                            Log.d("SendChat>", snapshot.getKey());
                            Chat chat = new Chat(SessionNow.getSession(getApplicationContext(), "nickname"), msg, emoticonNum,1,
                                    0, SessionNow.getSession(getApplicationContext(), "email"), sdf2.format(date));
                            databaseReference.child("users").child(snapshot.getKey())
                                    .child("message").child(chat.getFriend()).child(sdf.format(date)).push().setValue(chat);
                        }
                    }
                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {
                    }
                });
    }

    private Bitmap getImage(String path) {
        AssetManager mngr = getAssets();
        InputStream in = null;
        try {
            in = mngr.open("emoticons/" + path);
        } catch (Exception e) {
            e.printStackTrace();
        }

        Bitmap temp = BitmapFactory.decodeStream(in, null, null);
        return temp;
    }

    /**
     * Checking keyboard height and keyboard visibility
     */
    int firstHeightDiffrence = 0;
    int previousHeightDiffrence = 0;
    private void checkKeyboardHeight(final View parentLayout) {

        parentLayout.getViewTreeObserver().addOnGlobalLayoutListener(
                new ViewTreeObserver.OnGlobalLayoutListener() {

                    @Override
                    public void onGlobalLayout() {

                        Rect r = new Rect();
                        parentLayout.getWindowVisibleDisplayFrame(r);

                        int screenHeight = parentLayout.getRootView()
                                .getHeight();
                        int heightDifference = screenHeight - (r.bottom);

//                        Log.d("previousHeightDiffrence", Integer.toString(previousHeightDiffrence));
//                        Log.d("pre-heightDif", Integer.toString(previousHeightDiffrence - heightDifference));
//                        Log.d("screenHeight", Integer.toString(screenHeight));
//                        Log.d("screenHeight bottom", Integer.toString(r.bottom));
//                        Log.d("heightDiff", Integer.toString(heightDifference));

                        if (previousHeightDiffrence - heightDifference > 50) {
                            popupWindow.dismiss();
                        }

                        previousHeightDiffrence = heightDifference;

//                        Log.d("firstHeightDiffrence", Integer.toString(firstHeightDiffrence));

                        if (firstHeightDiffrence == 0){
                            firstHeightDiffrence = heightDifference;
                        }

                        if (heightDifference > 100) {
                            isKeyBoardVisible = true;
//                            changeKeyboardHeight(heightDifference-firstHeightDiffrence);
                            changeKeyboardHeight(heightDifference);
                        } else {
                            isKeyBoardVisible = false;
                        }

                    }
                });

    }

    private void changeKeyboardHeight(int height) {

        if (height > 100) {
            keyboardHeight = height;
            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                    ViewGroup.LayoutParams.MATCH_PARENT, keyboardHeight);
            binding.footerForEmoticons.setLayoutParams(params);
        }
    }

    /**
     * Defining all components of emoticons keyboard
     */
    private void enablePopUpView() {

        ViewPager pager = (ViewPager) popUpView.findViewById(R.id.emoticons_pager);
        Log.d("pager", pager.toString());
        pager.setOffscreenPageLimit(3);

        ArrayList<String> paths = new ArrayList<String>();

        for (short i = 1; i <= NO_OF_EMOTICONS; i++) {
            paths.add(i + ".png");

        }
        EmoticonsPagerAdapter adapter = new EmoticonsPagerAdapter(this, paths, this);
        pager.setAdapter(adapter);

        // Creating a pop window for emoticons keyboard
        popupWindow = new PopupWindow(popUpView, ViewGroup.LayoutParams.MATCH_PARENT,
                (int) keyboardHeight, false);

        TextView backSpace = (TextView) popUpView.findViewById(R.id.back);
        backSpace.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                KeyEvent event = new KeyEvent(0, 0, 0, KeyEvent.KEYCODE_DEL, 0, 0, 0, 0, KeyEvent.KEYCODE_ENDCALL);
                binding.msgEditText.dispatchKeyEvent(event);
            }
        });

        popupWindow.setOnDismissListener(new PopupWindow.OnDismissListener() {

            @Override
            public void onDismiss() {
                binding.footerForEmoticons.setVisibility(LinearLayout.GONE);
            }
        });
    }

}
