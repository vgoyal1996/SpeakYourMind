package com.example.vipul.speakyourmind.other;
import android.util.Log;
import android.widget.Filter;

import com.example.vipul.speakyourmind.adapters.StatusViewAdapter;
import com.example.vipul.speakyourmind.model.StatusModel;

import java.util.ArrayList;
import java.util.List;



public class CustomFilter extends Filter {
    private StatusViewAdapter adapter;
    private List<StatusModel> filterList;

    public CustomFilter(StatusViewAdapter adapter, List<StatusModel> filterList) {
        this.adapter = adapter;
        this.filterList = filterList;
    }

    @Override
    protected FilterResults performFiltering(CharSequence constraint) {
        FilterResults results=new FilterResults();
        if(constraint!=null && constraint.length()>0)
        {
            constraint=constraint.toString().toUpperCase();
            ArrayList<StatusModel> filteredStatus=new ArrayList<>();
            for(StatusModel m:filterList)
            {
                if(m.getMessage().toUpperCase().contains(constraint))
                {
                    filteredStatus.add(m);
                }
            }
            results.count=filteredStatus.size();
            results.values=filteredStatus;
            Log.i("hi",""+results);
            return results;
        }
        results.count=filterList.size();
        results.values=filterList;
        return results;
    }

    @Override
    protected void publishResults(CharSequence constraint, FilterResults results) {
        adapter.setStatusModels((List<StatusModel>) results.values);
        adapter.notifyDataSetChanged();
    }
}