package deo.com.signaldemo.viewmodel;

import android.app.ActionBar;
import android.app.Activity;
import android.content.Context;
import android.content.res.AssetManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Rect;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.Handler;
import android.support.v4.app.FragmentActivity;
import android.support.v4.view.ViewPager;
import android.text.Html;
import android.text.Layout;
import android.text.Spanned;
import android.util.Log;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.TextView;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.StringTokenizer;
import java.util.Vector;

import deo.com.signaldemo.ChatRoomActivity;
import deo.com.signaldemo.R;
import deo.com.signaldemo.SessionNow;
import deo.com.signaldemo.adapter.ChatAdapter;
import deo.com.signaldemo.adapter.ChatListAdapter;
import deo.com.signaldemo.adapter.EmoticonsChatAdapter;
import deo.com.signaldemo.adapter.EmoticonsGridAdapter;
import deo.com.signaldemo.adapter.EmoticonsPagerAdapter;
import deo.com.signaldemo.databinding.ActivityChatroomBinding;

import static android.content.Context.INPUT_METHOD_SERVICE;
import static com.firebase.ui.auth.AuthUI.getApplicationContext;

public class EmoticonsViewModel implements ViewModel, EmoticonsGridAdapter.KeyClickListener {

    private Activity activity;
    private FragmentActivity fragmentActivity;
    private Context context;
    private ActivityChatroomBinding binding;


    private Vector chats;
//    private ChatAdapter adapter;
    private EmoticonsChatAdapter mAdapter;
    private Handler handler;
    private ArrayList<Spanned> emoChat;

    private static final int NO_OF_EMOTICONS = 54;
    private int keyboardHeight;
    private PopupWindow popupWindow;
    private boolean isKeyBoardVisible;
    private Bitmap[] emoticons;
    private View popUpView;
    private EditText content;

    private FirebaseDatabase firebaseDatabase = FirebaseDatabase.getInstance();
    private DatabaseReference databaseReference = firebaseDatabase.getReference();

    public EmoticonsViewModel(){}

    public EmoticonsViewModel(Activity activity, FragmentActivity fragmentActivity
            ,Context context, ActivityChatroomBinding binding){
        this.activity = activity;
        this.fragmentActivity = fragmentActivity;
        this.context = context;
        this.binding = binding;
    }
    @Override
    public void onCreate() {


        popUpView = activity.getLayoutInflater().inflate(R.layout.emoticons_popup, null);
        popupWindow = new PopupWindow(popUpView, LinearLayout.LayoutParams.WRAP_CONTENT, ActionBar.LayoutParams.WRAP_CONTENT);

        // Defining default height of keyboard which is equal to 230 dip
        final float popUpheight = activity.getResources().getDimension(
                R.dimen.keyboard_height);
        changeKeyboardHeight((int) popUpheight);

//        Log.d("popUpheigt", Float.toString(popUpheight));
//        Log.d("popupWindow", popupWindow.toString());

        binding.emoticons.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                InputMethodManager imm = (InputMethodManager)activity.getSystemService(INPUT_METHOD_SERVICE);
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
        enableFooterView();
    }


    /**
     * Enabling all content in footer i.e. post window
     */
    private void enableFooterView() {
        // Setting adapter for chat list
        emoChat = new ArrayList<Spanned>();
        mAdapter = new EmoticonsChatAdapter(emoChat, context.getApplicationContext());
    }


    /**
     * Reading all emoticons in local cache
     */
    private void readEmoticons () {

        emoticons = new Bitmap[NO_OF_EMOTICONS];
        for (short i = 0; i < NO_OF_EMOTICONS; i++) {
            emoticons[i] = getImage((i+1) + ".png");
        }

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

                        if (firstHeightDiffrence == 0){
                            firstHeightDiffrence = heightDifference;
                        }

                        if (heightDifference > 100) {

                            isKeyBoardVisible = true;
                            changeKeyboardHeight(heightDifference-firstHeightDiffrence);

                        } else {

                            isKeyBoardVisible = false;

                        }

                    }
                });

    }
    /**
     * change height of emoticons keyboard according to height of actual
     * keyboard
     *
     * @param height
     *            minimum height by which we can make sure actual keyboard is
     *            open or not
     */

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
//        Log.d("pager", pager.toString());
        pager.setOffscreenPageLimit(3);

        ArrayList<String> paths = new ArrayList<String>();

        for (short i = 1; i <= NO_OF_EMOTICONS; i++) {
            paths.add(i + ".png");

        }
        EmoticonsPagerAdapter adapter = new EmoticonsPagerAdapter(activity, paths, this);
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



    private Bitmap getImage(String path) {
        AssetManager mngr = activity.getAssets();
        InputStream in = null;
        try {
            in = mngr.open("emoticons/" + path);
        } catch (Exception e) {
            e.printStackTrace();
        }

        Bitmap temp = BitmapFactory.decodeStream(in, null, null);
        return temp;
    }

    @Override
    public void keyClickedIndex(final String index) {
        Html.ImageGetter imageGetter = new Html.ImageGetter() {
            public Drawable getDrawable(String source) {
                StringTokenizer st = new StringTokenizer(index, ".");
                Drawable d = new BitmapDrawable(activity.getResources(),emoticons[Integer.parseInt(st.nextToken()) - 1]);
                d.setBounds(0, 0, d.getIntrinsicWidth(), d.getIntrinsicHeight());
//                Log.d("KeyIndex", index);
//                Log.d("KeyIndex D", d.toString());
                return d;
            }
        };

        Spanned cs = Html.fromHtml("<img src ='"+ index +"'/>", imageGetter, null);

        int cursorPosition = binding.msgEditText.getSelectionStart();
        binding.msgEditText.getText().insert(cursorPosition, cs);
//        Log.d("Spanned cs", cs.toString());
//        Log.d("cursorPosition", Integer.toString(cursorPosition));

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

}
