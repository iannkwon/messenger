package deo.com.signaldemo.adapter;

import android.content.Context;
import android.content.res.AssetManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.ShapeDrawable;
import android.graphics.drawable.shapes.OvalShape;
import android.os.Build;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.text.Html;
import android.text.Spannable;
import android.text.SpannableStringBuilder;
import android.text.Spanned;
import android.text.style.AbsoluteSizeSpan;
import android.util.Log;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.StringTokenizer;
import java.util.Vector;

import deo.com.signaldemo.databinding.ItemChatBinding;
import deo.com.signaldemo.item.Chat;

public class ChatAdapter extends RecyclerView.Adapter {

    private Vector<Chat> chats;
    private Context context;
    private ArrayList<Spanned> comments;
    private Spanned cs;

    private Bitmap emoticons[];
    private int NO_OF_EMOTICONS = 36;

    public ChatAdapter(Vector<Chat> chats, Context context) {
        this.chats = chats;
        this.context = context;
    }

    public ChatAdapter(ArrayList<Spanned> comments ){
        this.comments = comments;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        RecyclerView.ViewHolder holder;
        ItemChatBinding binding = ItemChatBinding.inflate(LayoutInflater.from(context), parent, false);
        holder = new ChatHolder(binding);
        return holder;
    }

    private void readEmoticons () {

        emoticons = new Bitmap[NO_OF_EMOTICONS];
        for (short i = 0; i < NO_OF_EMOTICONS; i++) {
            emoticons[i] = getImage((i+1) + ".png");
        }

    }

    private Bitmap getImage(String path) {
        AssetManager mngr = context.getAssets();
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
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        ChatHolder itemViewHolder = (ChatHolder) holder;
        final ItemChatBinding binding = itemViewHolder.binding;
        int who = chats.get(position).getWho();
        String msg = chats.get(position).getChat();
        final String emo = chats.get(position).getEmoticon();

        if (emo != null){
            readEmoticons ();

            Html.ImageGetter imageGetter = new Html.ImageGetter() {
                public Drawable getDrawable(String source) {
                    StringTokenizer st = new StringTokenizer(emo, ".");
                    Drawable d = new BitmapDrawable(context.getResources(), emoticons[Integer.parseInt(st.nextToken()) - 1]);
                    d.setBounds(0, 0, d.getIntrinsicWidth()*4, d.getIntrinsicHeight()*4);
//                    Log.d("KeyIndex", emo);
                    return d;
                }
            };
            cs = Html.fromHtml("<img src ='"+ emo +"'/>", imageGetter, null);

        }
        switch (who) {
            case 0:
                binding.friendChatLayout.setVisibility(View.GONE);
                binding.myChatLayout.setVisibility(View.VISIBLE);
                if (msg != null){
                    binding.myMsgTxtView.setText(msg);
                }else {
                    binding.myMsgTxtView.setText(cs);
                }

                break;
            case 1:
                binding.myChatLayout.setVisibility(View.GONE);
                binding.friendChatLayout.setVisibility(View.VISIBLE);
                if (msg != null){
                    binding.friendMsgTxtView.setText(msg);
                }else{
                    binding.friendMsgTxtView.setText(cs);
                }


                binding.friendImgView.setImageResource(chats.get(position).getProfile());
                binding.friendImgView.setBackground(new ShapeDrawable(new OvalShape()));
                if(Build.VERSION.SDK_INT >= 21) {
                    binding.friendImgView.setClipToOutline(true);
                }
                break;
        }
    }
    @Override
    public int getItemCount() {
        return chats.size();
    }
    private class ChatHolder extends RecyclerView.ViewHolder {
        ItemChatBinding binding;

        ChatHolder(ItemChatBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }
    }
}
