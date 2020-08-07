package com.vpipl.mmtfranchisee.Adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.vpipl.mmtfranchisee.R;

import java.util.HashMap;
import java.util.List;


public class ExpandableListAdapter extends BaseExpandableListAdapter {

    private Context _context;
    private List<String> _listDataHeader;

    // header titles
    // child data in format of header title, child title

    private HashMap<String, List<String>> _listDataChild;

    public ExpandableListAdapter(Context context, List<String> listDataHeader, HashMap<String, List<String>> listChildData) {
        this._context = context;
        this._listDataHeader = listDataHeader;
        this._listDataChild = listChildData;
    }

    @Override
    public Object getChild(int groupPosition, int childPosititon) {
        return this._listDataChild.get(this._listDataHeader.get(groupPosition)).get(childPosititon);
    }

    @Override
    public long getChildId(int groupPosition, int childPosition) {
        return childPosition;
    }

    @Override
    public View getChildView(int groupPosition, final int childPosition, boolean isLastChild, View convertView, ViewGroup parent) {

        final String childText = (String) getChild(groupPosition, childPosition);

        if (convertView == null) {
            LayoutInflater infalInflater = (LayoutInflater) this._context
                    .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = infalInflater.inflate(R.layout.explist_child_item, null);
        }

        TextView txtListChild = (TextView) convertView.findViewById(R.id.lblListItem);
        txtListChild.setText(childText);
        return convertView;
    }

    @Override
    public int getChildrenCount(int groupPosition) {
        return this._listDataChild.get(this._listDataHeader.get(groupPosition)).size();
    }

    @Override
    public Object getGroup(int groupPosition) {
        return this._listDataHeader.get(groupPosition);
    }

    @Override
    public int getGroupCount() {
        return this._listDataHeader.size();
    }

    @Override
    public long getGroupId(int groupPosition) {
        return groupPosition;
    }

    @Override
    public View getGroupView(int groupPosition, boolean isExpanded,
                             View convertView, ViewGroup parent) {
        String headerTitle = (String) getGroup(groupPosition);

        if (convertView == null) {
            LayoutInflater infalInflater = (LayoutInflater) this._context
                    .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = infalInflater.inflate(R.layout.explist_group_item, null);
        }

        TextView lblListHeader = (TextView) convertView.findViewById(R.id.lblListHeader);
        // lblListHeader.setTypeface(null, Typeface.BOLD);
        lblListHeader.setText(headerTitle);


        ImageView imageView4 = (ImageView) convertView.findViewById(R.id.imageView4);

        if (headerTitle.equalsIgnoreCase("My Profile"))
            imageView4.setImageDrawable(_context.getResources().getDrawable(R.drawable.nav_icon_profile));
        else if (headerTitle.equalsIgnoreCase("Contact Us"))
            imageView4.setImageDrawable(_context.getResources().getDrawable(R.drawable.nav_icon_call));
        else if (headerTitle.equalsIgnoreCase("Backup Info"))
            imageView4.setImageDrawable(_context.getResources().getDrawable(R.drawable.nav_icon_info));
        else if (headerTitle.equalsIgnoreCase("About Us"))
            imageView4.setImageDrawable(_context.getResources().getDrawable(R.drawable.nav_icon_about));
        else
            imageView4.setVisibility(View.GONE);


        ImageView imageView3 = (ImageView) convertView.findViewById(R.id.imageView3);

        if (getChildrenCount(groupPosition) > 0) {
            imageView3.setImageResource(R.drawable.ic_expand_more_white);
            imageView3.setVisibility(View.VISIBLE);
        } else
            imageView3.setVisibility(View.GONE);


        if (isExpanded) {
            imageView3.setImageResource(R.drawable.ic_expand_less_white);
//            lblListHeader.setTextColor(Color.parseColor("#f26b35"));
//            lblListHeader.setTypeface(null, Typeface.BOLD);
        } else {
//            lblListHeader.setTextColor(Color.parseColor("#005095"));
//            lblListHeader.setTypeface(null, Typeface.NORMAL);
        }


        return convertView;
    }

    @Override
    public boolean hasStableIds() {
        return false;
    }

    @Override
    public boolean isChildSelectable(int groupPosition, int childPosition) {
        return true;
    }
}