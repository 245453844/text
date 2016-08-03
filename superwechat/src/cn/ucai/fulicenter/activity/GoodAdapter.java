package cn.ucai.fulicenter.activity;

import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.RecyclerView.ViewHolder;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import cn.ucai.fulicenter.D;
import cn.ucai.fulicenter.I;
import cn.ucai.fulicenter.R;
import cn.ucai.fulicenter.bean.NewGoodBean;
import cn.ucai.fulicenter.utils.ImageUtils;
import cn.ucai.fulicenter.view.FooterViewHolder;

/**
 * Created by liulian on 2016/8/2.
 */
public class GoodAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder>{
    Context mContext;
    List<NewGoodBean> mGoodlist;
    GoodViewHolder mGoodViewHolder;
    FooterViewHolder mFooterViewHolder;
    boolean isMore;
    String footerString;
    public GoodAdapter(Context context, List<NewGoodBean> list) {
        mContext =context;
        mGoodlist =new ArrayList<NewGoodBean>();
        mGoodlist.addAll(list);
        soryByAddTime();
    }

    public String getFooterString() {
        return footerString;
    }

    public void setFooterString(String footerString) {
        this.footerString = footerString;
        notifyDataSetChanged();
    }

    public boolean isMore() {
        return isMore;
    }

    public void setMore(boolean more) {
        isMore = more;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType){
        LayoutInflater inflate= LayoutInflater.from(mContext);
        ViewHolder holder = null;
        switch (viewType){
            case I.TYPE_FOOTER:
                holder = new FooterViewHolder(inflate.inflate(R.layout.item_footer,parent,false));
                break;
            case I.TYPE_ITEM:
                holder =new GoodViewHolder(inflate.inflate(R.layout.item_good,null,false));
                break;
        }
        return holder;
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
       if (holder instanceof GoodViewHolder){
           mGoodViewHolder = (GoodViewHolder) holder;
           final NewGoodBean good = mGoodlist.get(position);
           ImageUtils.setGoodThumb(mContext,mGoodViewHolder.ivGoodThumb,good.getGoodsThumb());
           mGoodViewHolder.tvGoodName.setText(good.getGoodsName());
           mGoodViewHolder.tvGoodPrice.setText(good.getCurrencyPrice());
           mGoodViewHolder.layout.setOnClickListener(new View.OnClickListener() {
               @Override
               public void onClick(View view) {
                   mContext.startActivity(new Intent(mContext,GoodDetailsActivity.class)
                           .putExtra(D.GoodDetails.KEY_GOODS_ID,good.getGoodsId()));


               }
           });
       }
        if (holder instanceof  FooterViewHolder){
            mFooterViewHolder = (FooterViewHolder) holder;
            mFooterViewHolder.tvFooter.setText(footerString);
        }
    }
    public int getItemViewType(int position) {
        if (position == getItemCount() - 1) {
            return I.TYPE_FOOTER;
        }else {
            return I.TYPE_ITEM;
        }
    }

    @Override
    public int getItemCount() {
        return mGoodlist!=null?mGoodlist.size()+1:1;
    }

    public void initItem(ArrayList<NewGoodBean> list) {
        if (mGoodlist!=null){
            mGoodlist.clear();
        }
        mGoodlist.addAll(list);
        soryByAddTime();
        mGoodlist = list;
        notifyDataSetChanged();
    }

    public void addItem(ArrayList<NewGoodBean> list) {
        mGoodlist.addAll(list);
        notifyDataSetChanged();
    }

    class  GoodViewHolder extends  RecyclerView.ViewHolder {
        LinearLayout layout;
        ImageView ivGoodThumb;
        TextView tvGoodName;
        TextView tvGoodPrice;
        public GoodViewHolder(View itemView) {
            super(itemView);
            layout = (LinearLayout) itemView.findViewById(R.id.layout_good);
            ivGoodThumb = (ImageView) itemView.findViewById(R.id.niv_good_thumb);
            tvGoodName = (TextView) itemView.findViewById(R.id.tv_good_name);
            tvGoodPrice = (TextView) itemView.findViewById(R.id.tv_good_price);

        }
    }
    private void  soryByAddTime(){
        Collections.sort(mGoodlist, new Comparator<NewGoodBean>() {
            @Override
            public int compare(NewGoodBean goodLeft, NewGoodBean goodRight) {
                return (int)(Long.valueOf(goodLeft.getAddTime())-Long.valueOf(goodRight.getAddTime()));
            }
        });
    }
}
