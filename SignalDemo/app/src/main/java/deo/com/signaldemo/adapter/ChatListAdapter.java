package deo.com.signaldemo.adapter;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.res.AssetManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.text.Html;
import android.text.Spanned;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.io.InputStream;
import java.util.StringTokenizer;
import java.util.Vector;

import deo.com.signaldemo.ChatRoomActivity;
import deo.com.signaldemo.MainActivity;
import deo.com.signaldemo.databinding.ItemChatlistBinding;
import deo.com.signaldemo.item.Profile;
import deo.com.signaldemo.service.BluetoothService;

public class ChatListAdapter extends RecyclerView.Adapter {

    private Vector<Profile> profiles;
    private Context context;
    private Activity activity;

    private Spanned cs;

    private Bitmap emoticons[];
    private int NO_OF_EMOTICONS = 36;

    private BluetoothService bluetoothService = null;

    public ChatListAdapter(Vector<Profile> profiles, Context context, Activity activity) {
        this.profiles = profiles;
        this.context = context;
        this.activity = activity;
    }

    public ChatListAdapter(BluetoothService bluetoothService){
        this.bluetoothService = bluetoothService;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        RecyclerView.ViewHolder holder;
        ItemChatlistBinding binding = ItemChatlistBinding.inflate(LayoutInflater.from(context), parent, false);
        holder = new ChatViewHolder(binding);
        return holder;
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        ChatViewHolder itemViewHolder = (ChatViewHolder) holder;
        final ItemChatlistBinding binding = itemViewHolder.binding;
        final int pos = position;
        //binding.friendProfileImgView.setImageResource(profiles.get(pos).getUrl());
        binding.friendNameTxtView.setText(profiles.get(pos).getNickname());
        binding.friendLastMsgTimeTxtView.setText(profiles.get(pos).getTime());
        if (profiles.get(position).getMsg() != null){
            binding.friendLastMsgTxtView.setText(profiles.get(pos).getMsg());
        }else{
            readEmoticons ();

            Html.ImageGetter imageGetter = new Html.ImageGetter() {
                public Drawable getDrawable(String source) {
                    StringTokenizer st = new StringTokenizer(profiles.get(pos).getEmoticon(), ".");
                    Drawable d = new BitmapDrawable(context.getResources(), emoticons[Integer.parseInt(st.nextToken()) - 1]);
                    d.setBounds(0, 0, d.getIntrinsicWidth(), d.getIntrinsicHeight());
//                    Log.d("KeyIndex", profiles.get(pos).getEmoticon());
                    return d;
                }
            };
            cs = Html.fromHtml("<img src ='"+ profiles.get(pos).getEmoticon() +"'/>", imageGetter, null);

            binding.friendLastMsgTxtView.setText(cs);
        }

        binding.chatLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(activity, ChatRoomActivity.class);
                intent.putExtra("fEmail", profiles.get(pos).getEmail());
                intent.putExtra("fName", profiles.get(pos).getNickname());
                intent.putExtra("fImg", profiles.get(pos).getUrl());
//                Log.d("ChatList Email", profiles.get(pos).getEmail());
                activity.startActivity(intent);
                activity.finish();
            }
        });
    }

    @Override
    public int getItemCount() {
        return profiles.size();
    }

    private class ChatViewHolder extends RecyclerView.ViewHolder {

        ItemChatlistBinding binding;

        ChatViewHolder(ItemChatlistBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }
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

}
