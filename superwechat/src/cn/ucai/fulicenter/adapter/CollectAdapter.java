 package cn.ucai.fulicenter.adapter;

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
import java.util.List;

import cn.ucai.fulicenter.D;
import cn.ucai.fulicenter.I;
import cn.ucai.fulicenter.R;
import cn.ucai.fulicenter.activity.GoodDetailsActivity;
import cn.ucai.fulicenter.bean.CollectBean;
import cn.ucai.fulicenter.utils.ImageUtils;
import cn.ucai.fulicenter.view.FooterViewHolder;

/**
 * Created by liulian on 2016/8/2.
 */
public class CollectAdapter extends RecyclerView.Adapter<ViewHolder>{
    Context mContext;
    List<CollectBean> mGoodlist;
    CollectViewHolder mCollectViewHolder;
    FooterViewHolder mFooterViewHolder;
    boolean isMore;
    String footerString;
    int soryBy;
    public CollectAdapter(Context context, List<CollectBean> list) {
        mContext =context;
        mGoodlist =new ArrayList<CollectBean>();
        mGoodlist.addAll(list);
        soryBy = I.SORT_BY_ADDTIME_DESC;
    }

    public String getFooterString() {
        return footerString;
    }

    public void setFooterString(String footerString) {
        this.footerString = footerString;
        notifyDataSetChanged();
    }
    public void setSoryBy(int soryBy){
        this.soryBy = soryBy;
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
                holder =new CollectViewHolder(inflate.inflate(R.layout.item_good,null,false));
                break;
        }
        return holder;
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
       if (holder instanceof CollectViewHolder){
           mCollectViewHolder = (CollectViewHolder) holder;
           final CollectBean collect = mGoodlist.get(position);
           ImageUtils.setGoodThumb(mContext, mCollectViewHolder.ivGoodThumb,collect.getGoodsThumb());
           mCollectViewHolder.tvGoodName.setText(collect.getGoodsName());
           mCollectViewHolder.layout.setOnClickListener(new View.OnClickListener() {
               @Override
               public void onClick(View view) {
                   mContext.startActivity(new Intent(mContext,GoodDetailsActivity.class)
                           .putExtra(D.GoodDetails.KEY_GOODS_ID,collect.getGoodsId()));


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

    public void initItem(ArrayList<CollectBean> list) {
        if (mGoodlist!=null){
            mGoodlist.clear();
        }
        mGoodlist.addAll(list);
        mGoodlist = list;
        notifyDataSetChanged();
    }

    public void addItem(ArrayList<CollectBean> list) {
        mGoodlist.addAll(list);
        notifyDataSetChanged();
    }

    class CollectViewHolder extends  ViewHolder {
        LinearLayout layout;
        ImageView ivGoodThumb;
        TextView tvGoodName;
        ImageView ivDelete;
        public CollectViewHolder(View itemView) {
            super(itemView);
            layout = (LinearLayout) itemView.findViewById(R.id.layout_good);
            ivGoodThumb = (ImageView) itemView.findViewById(R.id.niv_good_thumb);
            tvGoodName = (TextView) itemView.findViewById(R.id.tv_good_name);
            ivDelete = (ImageView) itemView.findViewById(R.id.iv_collect_delete);

        }
    }
    }

