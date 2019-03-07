package deo.com.signaldemo.adapter;

import android.content.Context;
import android.text.Layout;
import android.text.Spanned;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.PopupWindow;
import android.widget.TextView;

import java.util.ArrayList;

import deo.com.signaldemo.R;

public class EmoticonsChatAdapter extends BaseAdapter {

    private ArrayList<Spanned> comments;
    private Context mContext;

    public EmoticonsChatAdapter(){

    }
    public EmoticonsChatAdapter(ArrayList<Spanned> comments, Context mContext){
        this.comments = comments;
        this.mContext = mContext;
    }
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View v = convertView;
        if (v==null){
            LayoutInflater inflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            v = inflater.inflate(R.layout.item_chat, null);
        }

        final Spanned item = comments.get(position);

        TextView fans_image = (TextView) v.findViewById(R.id.myMsgTxtView);
        fans_image.setText(item);
//        Log.d("Fans_Image", item.toString());

        return v;
    }

    @Override
    public int getCount() {
        return comments.size();
    }

    @Override
    public Object getItem(int position) {
        return comments.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }


}
