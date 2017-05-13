package com.a32.yuqu.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.a32.yuqu.R;
import com.a32.yuqu.bean.LocationBean;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by root on 1/10/17.
 */

public class BookAdapter extends BaseAdapter {
    private Context mContext;
    private LayoutInflater inflater;
    List<LocationBean.ListBean> nodeInfos = new ArrayList<>();

    public BookAdapter(Context mContext, List<LocationBean.ListBean> nodeInfos) {
        this.mContext = mContext;
        this.nodeInfos = nodeInfos;
        inflater = LayoutInflater.from(mContext);
    }

    public void setData(List<LocationBean.ListBean> nodeInfos){
        this.nodeInfos=nodeInfos;
        notifyDataSetChanged();

    }

    @Override
    public int getCount() {
        return nodeInfos.size();
    }

    @Override
    public Object getItem(int psiont) {
        return nodeInfos.get(psiont);
    }

    @Override
    public long getItemId(int psiont) {
        return psiont;
    }

    @Override
    public View getView(int psiont, View view, ViewGroup viewGroup) {
        if (view == null) {
            view = inflater.inflate(R.layout.item_book, null);
            holder = new viewHolder();
            holder.placeName = (TextView) view.findViewById(R.id.processNode);
            holder.tvRemain = (TextView) view.findViewById(R.id.tvRemain);
            holder.devider = (TextView) view.findViewById(R.id.devider);
            view.setTag(holder);
        } else {
            holder = (viewHolder) view.getTag();
        }
        LocationBean.ListBean bean = nodeInfos.get(psiont);
        if (bean!=null) {
            if (nodeInfos.size() == 1) {
                holder.devider.setVisibility(View.GONE);
            } else {
                if (psiont == 0) {
                    holder.devider.setVisibility(View.VISIBLE);
                } else if (psiont == nodeInfos.size() - 1) {
                    holder.devider.setVisibility(View.GONE);
                }
            }
            holder.placeName.setText(bean.getPlaceName());
            holder.tvRemain.setText(mContext.getResources().getString(R.string.remained)+" "+(Integer.parseInt(bean.getMax())-Integer.parseInt(bean.getBooked())));
        }
        return view;
    }

    private viewHolder holder;

    class viewHolder {
        TextView placeName;
        TextView devider;
        TextView tvRemain;
    }
}
