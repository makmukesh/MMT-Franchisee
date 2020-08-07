package com.vpipl.mmtfranchisee.Adapters;

import android.support.v7.widget.RecyclerView;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.vpipl.mmtfranchisee.R;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * Created by PC14 on 11-Apr-16.
 */

public class Inbox_Adapter extends RecyclerView.Adapter<Inbox_Adapter.MyViewHolder>
{

    private ArrayList<HashMap<String, String>> datalist;
    private String olddate = "";


    public class MyViewHolder extends RecyclerView.ViewHolder {
        LinearLayout LL_date;
        TextView textView_heading, txt_date;
        TextView txt_time;


        public MyViewHolder(View view) {
            super(view);
            textView_heading = (TextView) view.findViewById(R.id.textView_heading);
            txt_date = (TextView) view.findViewById(R.id.txt_date);
            txt_time = (TextView) view.findViewById(R.id.txt_time);
            LL_date = (LinearLayout) view.findViewById(R.id.LL_date);
        }
    }


    public Inbox_Adapter(ArrayList<HashMap<String, String>> datalist) {
        this.datalist = datalist;
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType)
    {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.inboxlist_adapter, parent, false);

        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(MyViewHolder holder, int position)
    {
//        if (olddate.equalsIgnoreCase(datalist.get(position).get("TipDate").trim()))
//        {
//            holder.LL_date.setVisibility(View.GONE);
//        }
//        else {
//            holder.LL_date.setVisibility(View.VISIBLE);
//        }

        olddate = datalist.get(position).get("TipDate").trim();

        holder.textView_heading.setText(Html.fromHtml(datalist.get(position).get("MessageText").trim()));

        holder.txt_date.setText("" + datalist.get(position).get("TipDate").trim());

        if (datalist.get(position).get("TimeStamp") != null) {
            holder.txt_time.setText(datalist.get(position).get("TimeStamp"));
        }
    }

    @Override
    public int getItemCount() {
        return datalist.size();
    }
}