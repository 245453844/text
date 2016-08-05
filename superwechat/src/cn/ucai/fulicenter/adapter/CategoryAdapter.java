package cn.ucai.fulicenter.adapter;


import android.content.Context;
import android.content.Intent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import cn.ucai.fulicenter.I;
import cn.ucai.fulicenter.R;
import cn.ucai.fulicenter.activity.CategoryChildActivity;
import cn.ucai.fulicenter.bean.CategoryChildBean;
import cn.ucai.fulicenter.bean.CategoryGroupBean;
import cn.ucai.fulicenter.utils.ImageUtils;


/**
 * Created by liulian on 2016/8/4.
 */
public class CategoryAdapter  extends BaseExpandableListAdapter {
    Context mcontext;
    List<CategoryGroupBean> mGroupList;
    List<ArrayList<CategoryChildBean>> mChildList;
    public  CategoryAdapter(Context mContext,List<CategoryGroupBean> mGroupList,List<ArrayList<CategoryChildBean>> mChildList){
        this.mcontext = mContext;
        this.mGroupList = new ArrayList<CategoryGroupBean>();
        mGroupList.addAll(mGroupList);
        this.mChildList = new ArrayList<ArrayList<CategoryChildBean>>();
        mChildList.addAll(mChildList);
    }
    public int getGroupCount() {
        return mGroupList!=null?mGroupList.size():0;
    }

    @Override
    public int getChildrenCount(int groupPosition) {
        return mChildList.get(groupPosition).size();
    }

    @Override
    public CategoryGroupBean getGroup(int groupPosition) {
        if (mChildList!=null){
        return mGroupList.get(groupPosition);}
        else {
            return null;
        }
    }

    @Override
    public CategoryChildBean getChild(int groupPosition, int childPosition) {
        if (mChildList.get(groupPosition)!=null&&mChildList.get(groupPosition).get(childPosition)!=null)
            return mChildList.get(groupPosition).get(childPosition);
        return null;
    }

    @Override
    public long getGroupId(int groupPosition) {
        return 0;
    }

    @Override
    public long getChildId(int groupPosition, int childPosition) {
        return 0;
    }

    @Override
    public boolean hasStableIds() {
        return false;
    }

    @Override
    public View getGroupView(int groupPosition, boolean isExpanded, View convertView, ViewGroup parent) {
        GroupViewHolder holder ;
        if (convertView==null){
            convertView = View.inflate(mcontext, R.layout.item_category_group,null);
            holder = new GroupViewHolder();
            holder.ivGroupThumb = (ImageView) convertView.findViewById(R.id.iv_group_thumb);
            holder.tvGroupName = (TextView) convertView.findViewById(R.id.tv_group_name);
            holder.ivIndicator = (ImageView) convertView.findViewById(R.id.iv_indicator);
            convertView.setTag(holder);
        }else {
            holder = (GroupViewHolder) convertView.getTag();
        }
        CategoryGroupBean gruop = getGroup(groupPosition);
        ImageUtils.setGroupCategoryImager(mcontext,holder.ivGroupThumb,gruop.getImageUrl());
        holder.tvGroupName.setText(gruop.getName());
        if (isExpanded) {
            holder.ivIndicator.setImageResource(R.drawable.expand_off);
        }else {
            holder.ivIndicator.setImageResource(R.drawable.expand_on);
        }
        return convertView;
    }

    @Override
    public View getChildView(int groupPosition, int childPosition, boolean isLastChild, View convertView, ViewGroup viewGroup) {
        ChildViewHolder holder ;
        if (convertView==null){
            convertView = View.inflate(mcontext,R.layout.item_category_child,null);
            holder = new ChildViewHolder();
            holder.layoutCategoryChild = (RelativeLayout) convertView.findViewById(R.id.layout_category_child);
            holder.ivCategoryChildThumb = (ImageView) convertView.findViewById(R.id.iv_category_child_thumb);
            holder.tvCategoryChildName = (TextView) convertView.findViewById(R.id.tv_category_child_name);
            convertView.setTag(holder);}else {
            holder = (ChildViewHolder) convertView.getTag();
        }
            final CategoryChildBean child = getChild(groupPosition,childPosition);
            if (child!=null) {
                ImageUtils.setChildCategoryImage(mcontext, holder.ivCategoryChildThumb, child.getImageUrl());
                holder.tvCategoryChildName.setText(child.getName());
                holder.layoutCategoryChild.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        mcontext.startActivity(new Intent(mcontext, CategoryChildActivity.class)
                                .putExtra(I.NewAndBoutiqueGood.CAT_ID,child.getId()));
                    }
                });
            }
        return convertView;
    }

    @Override
    public boolean isChildSelectable(int groupPosition, int childPosition) {
        return false;
    }

    public void addAll(List<CategoryGroupBean> mGroupList, List<ArrayList<CategoryChildBean>> mChildList) {
        this.mGroupList.clear();
        this.mGroupList.addAll(mGroupList);
        this.mChildList.clear();
        this.mChildList.addAll(mChildList);
        notifyDataSetChanged();

    }

    class  GroupViewHolder {
        ImageView ivGroupThumb;
        TextView tvGroupName;
        ImageView ivIndicator;
    }
    class  ChildViewHolder{
        RelativeLayout layoutCategoryChild;
        ImageView ivCategoryChildThumb;
        TextView tvCategoryChildName;
    }
}