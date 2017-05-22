package com.a32.yuqu.adapter;

import android.content.Context;
import android.os.Environment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.a32.yuqu.R;
import com.a32.yuqu.applicaption.MyApplicaption;
import com.a32.yuqu.bean.DDyBean;
import com.a32.yuqu.http.HttpMethods;
import com.a32.yuqu.utils.FileUtil;
import com.a32.yuqu.view.CircleImageView;
import com.a32.yuqu.view.FillListView;
import com.bm.library.PhotoView;
import com.squareup.picasso.MemoryPolicy;
import com.squareup.picasso.Picasso;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by root on 6/01/17.
 */

public class DDYAdapter extends BaseAdapter {
    private Context mContext;
    private LayoutInflater inflater;
    private List<DDyBean.ListBean> xinwenList = new ArrayList<>();
    private static DDyBean.ListBean bean;
//    private ImgClickListener imgClickListener;

    //    public DDYAdapter(Context mContext, List<DDyBean.ListBean> objects,ImgClickListener imgClickListener) {
//        inflater = LayoutInflater.from(mContext);
//        this.mContext = mContext;
//        this.xinwenList = objects;
//        this.imgClickListener =imgClickListener;
//    }
    public DDYAdapter(Context mContext, List<DDyBean.ListBean> objects) {
        inflater = LayoutInflater.from(mContext);
        this.mContext = mContext;
        this.xinwenList = objects;
    }

    public void setData(List<DDyBean.ListBean> objects) {
        this.xinwenList = objects;
        notifyDataSetChanged();
    }

    @Override
    public int getCount() {
        return xinwenList.size();
    }

    @Override
    public Object getItem(int position) {
        return xinwenList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if (convertView == null) {
            convertView = inflater.inflate(R.layout.item_ddy, parent, false);
        }

//        if (parent instanceof FillListView) {
//            if (((FillListView) parent).isMeasure) {
//                return convertView;
//            }
//        }

        holder = (ViewHolder) convertView.getTag();
        if (holder == null) {
            holder = new ViewHolder();
            holder.head = (CircleImageView) convertView.findViewById(R.id.head);
            holder.tvName = (TextView) convertView.findViewById(R.id.tvName);
            holder.tvContent = (TextView) convertView.findViewById(R.id.tvContent);
            holder.img = (ImageView) convertView.findViewById(R.id.img);

            convertView.setTag(holder);
        }
        bean = xinwenList.get(position);
        holder.tvName.setText(bean.getTvName());
        holder.tvContent.setText(bean.getTvContent());
//        holder.img.setOnClickListener(imgClickListener);
        setImg();
        setHead();
        return convertView;
    }

    public void setHead() {
        if (FileUtil.fileIsExists(bean.getHead())) {
            Picasso.with(mContext).load(new File(Environment.getExternalStorageDirectory() + "/yuqu/pic/" + bean.getHead()))
                    .memoryPolicy(MemoryPolicy.NO_CACHE, MemoryPolicy.NO_STORE)//加速内存的回收
                    .into(holder.head);
        } else {
            Picasso.with(mContext).load((HttpMethods.BASE_URL + "upload/" + bean.getHead()))
                    .memoryPolicy(MemoryPolicy.NO_CACHE, MemoryPolicy.NO_STORE)//加速内存的回收
                    .into(holder.head);
        }
    }

    public void setImg() {
        if (bean.getImg().equals("")) {
            holder.img.setVisibility(View.GONE);
        } else {
            holder.img.setVisibility(View.VISIBLE);
            if (FileUtil.fileIsExists(bean.getImg())) {
                Picasso.with(mContext)
                        .load(new File(Environment.getExternalStorageDirectory() + "/yuqu/pic/" + bean.getImg()))
                        .memoryPolicy(MemoryPolicy.NO_CACHE, MemoryPolicy.NO_STORE)//加速内存的回收
                        .into(holder.img);
            } else {
                Picasso.with(mContext)
                        .load((HttpMethods.BASE_URL + "upload/" + bean.getImg()))
                        .memoryPolicy(MemoryPolicy.NO_CACHE, MemoryPolicy.NO_STORE)//加速内存的回收
                        .into(holder.img);
            }
        }
    }

    public static ViewHolder holder;

    class ViewHolder {
        CircleImageView head;
        TextView tvName;
        TextView tvContent;
        ImageView img;
    }

//    public  static abstract class ImgClickListener implements View.OnClickListener {
//
//        @Override
//        public void onClick(View view) {
//            imgClick(bean.getImg(),holder.img);
//        }
//
//        protected abstract void imgClick(String imgPath, PhotoView img);
//
//
//    }

}
