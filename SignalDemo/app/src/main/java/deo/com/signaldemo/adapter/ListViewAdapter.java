package deo.com.signaldemo.adapter;

import android.content.Context;
import android.text.Layout;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.TextView;

import java.util.ArrayList;

import deo.com.signaldemo.R;
import deo.com.signaldemo.databinding.ListviewItemBinding;
import deo.com.signaldemo.item.ListViewItem;

public class ListViewAdapter extends BaseAdapter implements Filterable {

    // Adapter에 추가된 데이터를 저장하기 위한 ArrayList(원본 데이터 리스트)
    private ArrayList<ListViewItem> listViewItemList = new ArrayList<ListViewItem>() ;
    // 필터링된 결과 데이터를 저장하기 위한 ArrayList. 최초에는 전체 리스트 보유.
    private ArrayList<ListViewItem> filteredItemList = listViewItemList;

    Filter listFilter;
//    ListviewItemBinding binding;

    // listViewAdapter의 생성자
    public ListViewAdapter(){}

    // Adapter에 사용되는 데이터의 개수를 리턴. : 필수 구현
    @Override
    public int getCount() {
        return filteredItemList.size();
    }
    // 지정한 위치(position)에 있는 데이터 리턴 : 필수구현
    @Override
    public Object getItem(int position) {
        return filteredItemList.get(position);
    }
    // 지정한 위치(position)에 있는 데이터와 관계된 아이템(row)의 ID를 리턴. : 필수구현
    @Override
    public long getItemId(int position) {
        return position;
    }
    // position에 위치한 데이터를 화면에 출력하는데 사용될 view를 리턴 : 필수구현
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        final int pos = position;
        final Context context = parent.getContext();

        if (convertView == null){
            LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = inflater.inflate(R.layout.listview_item, parent, false);
        }

        TextView emailTextView = (TextView) convertView.findViewById(R.id.textView1);
        TextView nicknameTextView = (TextView)convertView.findViewById(R.id.textView2);

        ListViewItem listViewItem = filteredItemList.get(position);

        emailTextView.setText(listViewItem.getEmail());
        nicknameTextView.setText(listViewItem.getNickname());


        return convertView;
    }

    public void addItem(String email, String nickname){
        ListViewItem item = new ListViewItem();

        item.setEmail(email);
        item.setNickname(nickname);

        listViewItemList.add(item);

    }

    public void clearItem(){
        listViewItemList.clear();
    }

    @Override
    public Filter getFilter() {
        if (listFilter == null){
            listFilter = new ListFilter();
        }
        return null;
    }

    private class ListFilter extends Filter {

        @Override
        protected FilterResults performFiltering(CharSequence constraint) {
            FilterResults results = new FilterResults() ;

            if (constraint == null || constraint.length() == 0) {
                results.values = listViewItemList ;
                results.count = listViewItemList.size() ;

            } else {
                ArrayList<ListViewItem> itemList = new ArrayList<ListViewItem>() ;

                for (ListViewItem item : listViewItemList) {
                    if (item.getEmail().toUpperCase().contains(constraint.toString().toUpperCase()) ||
                            item.getNickname().toUpperCase().contains(constraint.toString().toUpperCase()))
                    {
                        itemList.add(item) ;
                    }
                }

                results.values = itemList ;
                results.count = itemList.size() ;
            }
            return results;
        }

        @Override
        protected void publishResults(CharSequence constraint, FilterResults results) {

            // update listview by filtered data list.
            filteredItemList = (ArrayList<ListViewItem>) results.values ;

            // notify
            if (results.count > 0) {
                notifyDataSetChanged() ;
            } else {
                notifyDataSetInvalidated() ;
            }
        }
    }
}// end class


