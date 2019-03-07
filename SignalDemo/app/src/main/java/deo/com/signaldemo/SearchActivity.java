package deo.com.signaldemo;

import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.SearchView;

import deo.com.signaldemo.adapter.ListViewAdapter;
import deo.com.signaldemo.databinding.ActivitySearchBinding;
import deo.com.signaldemo.viewmodel.SearchViewModel;

public class SearchActivity extends AppCompatActivity {
    ActivitySearchBinding binding;
    ListViewAdapter adapter;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_search);

        adapter = new ListViewAdapter();
        binding.listViewSearch.setAdapter(adapter);

        SearchViewModel searchViewModel = new SearchViewModel(this, adapter, binding);
        searchViewModel.onCreate();



        binding.ivBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                onBackPressed();
                startActivity(new Intent(SearchActivity.this, MainActivity.class));
                finish();
            }
        });
    }
}
