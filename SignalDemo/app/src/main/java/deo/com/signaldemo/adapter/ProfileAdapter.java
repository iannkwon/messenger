package deo.com.signaldemo.adapter;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import java.util.Vector;

import deo.com.signaldemo.ChatRoomActivity;
import deo.com.signaldemo.SessionNow;
import deo.com.signaldemo.databinding.ItemHeaderBinding;
import deo.com.signaldemo.databinding.ItemProfileBinding;
import deo.com.signaldemo.item.Profile;

public class ProfileAdapter extends RecyclerView.Adapter{

    private static final int MY_HEADER = 0;

    private static final int MY_PROFILE = 1;

    private static final int FRIEND_HEADER = 2;

    private Vector<Profile> profiles;

    private Context context;

    private Activity activity;

    public ProfileAdapter(Vector<Profile> profiles, Context context, Activity activity) {
        this.profiles = profiles;
        this.context = context;
        this.activity = activity;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        RecyclerView.ViewHolder holder;

        if(viewType == MY_HEADER || viewType == FRIEND_HEADER ){
            ItemHeaderBinding headerBinding = ItemHeaderBinding.inflate(LayoutInflater.from(context), parent, false);
            holder = new HeaderHolder(headerBinding);
        } else {
            ItemProfileBinding profileBinding = ItemProfileBinding.inflate(LayoutInflater.from(context), parent, false);
            holder = new ProfileHolder(profileBinding);
        }

        return holder;
    }

    @Override
    public void onBindViewHolder(@NonNull final RecyclerView.ViewHolder holder, final int position) {
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                Log.d("Click Test", profiles.get(position).getNickname());
                if (!profiles.get(position).getEmail().isEmpty() &&
                        profiles.get(position).getEmail() != SessionNow.getSession(context, "email")){
                    final String[] abList = {"Chatting"};
                    AlertDialog.Builder ab = new AlertDialog.Builder(context);
                    ab.setItems(abList, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            switch (abList[which]){
                                case "Chatting":
                                    Intent intent = new Intent(context, ChatRoomActivity.class);
                                    intent.putExtra("fEmail", profiles.get(position).getEmail());
                                    intent.putExtra("fName", profiles.get(position).getNickname());
                                    intent.putExtra("fImg", profiles.get(position).getUrl());
                                    activity.startActivity(intent);
                                    activity.finish();
                                    break;
                            }
                        }
                    });
                    ab.show();
                }
            }
        });

        if(position == MY_HEADER && holder instanceof HeaderHolder) {
            HeaderHolder itemViewHolder = (HeaderHolder) holder;
            final ItemHeaderBinding binding = itemViewHolder.binding;
            binding.friendHeaderTv.setText(profiles.get(position).getNickname());
        } else if (position == FRIEND_HEADER && holder instanceof HeaderHolder) {
            HeaderHolder itemViewHolder = (HeaderHolder) holder;
            final ItemHeaderBinding binding = itemViewHolder.binding;
            binding.friendHeaderTv.setText(profiles.get(position).getNickname());
        } else if (position == MY_PROFILE && holder instanceof ProfileHolder) {
            ProfileHolder itemViewHolder = (ProfileHolder) holder;
            final ItemProfileBinding binding = itemViewHolder.binding;
            binding.userNameTv.setText(profiles.get(position).getNickname());
            binding.profileMsgTv.setText(profiles.get(position).getEmail());
            //binding.profileImgView.setImageResource(profiles.get(position).getUrl());
            //Picasso.with(context).load(profiles.get(position).getStrUrl()).transform(new RoundedCornersTransformation(50,0)).resize(100,100).into(binding.profileImgView);
        } else {
            ProfileHolder itemViewHolder = (ProfileHolder) holder;
            final ItemProfileBinding binding = itemViewHolder.binding;
            binding.userNameTv.setText(profiles.get(position).getNickname());
            binding.profileMsgTv.setText(profiles.get(position).getEmail());
            binding.profileImgView.setImageResource(profiles.get(position).getUrl());
        }
    }

    @Override
    public int getItemViewType(int position) {
        if (position == 0) {
            return MY_HEADER;
        } else if (position == 2) {
            return FRIEND_HEADER;
        } else {
            return position;
        }
    }

    @Override
    public int getItemCount() {
        return profiles.size();
    }

    private class HeaderHolder extends RecyclerView.ViewHolder {

        ItemHeaderBinding binding;

        HeaderHolder(ItemHeaderBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }
    }

    private class ProfileHolder extends RecyclerView.ViewHolder {

        ItemProfileBinding binding;

        ProfileHolder(ItemProfileBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }

    }

}


