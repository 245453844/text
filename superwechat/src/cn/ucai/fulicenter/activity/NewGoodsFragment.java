package cn.ucai.fulicenter.activity;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;


import java.util.ArrayList;
import java.util.List;

import cn.ucai.fulicenter.I;
import cn.ucai.fulicenter.R;
import cn.ucai.fulicenter.bean.NewGoodBean;
import cn.ucai.fulicenter.data.OkHttpUtils2;
import cn.ucai.fulicenter.utils.Utils;

/**
 * A simple {@link Fragment} subclass.
 */
public class NewGoodsFragment extends Fragment {
    private final static String TAG = NewGoodsFragment.class.getSimpleName();
    FuliCenterMainActivity mContext;
    SwipeRefreshLayout mSwipeRefreshLayout;
    RecyclerView mRecyclerView;
    GridLayoutManager mGridLayoutManger;
    GoodAdapter mAdapter;
    List<NewGoodBean> mGoodList;
    int pageId = 0;
    TextView tvHint;

    public NewGoodsFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        mContext = (FuliCenterMainActivity) getContext();
        View layout = View.inflate(mContext,R.layout.fragment_new_goods,null);
        mGoodList = new ArrayList<NewGoodBean>();
        initView(layout);
        initData();
        setListener();
        return layout;
    }

    private void setListener() {
        setPullDownRefreshListener();
        setPullUpRefreshListener();
    }

    private void setPullUpRefreshListener() {
        mRecyclerView.setOnScrollListener(new RecyclerView.OnScrollListener(){
            int lastItemPosition;
            public  void  onScrollStateChanged(RecyclerView recyclerView,int newStae){
                super.onScrollStateChanged(recyclerView,newStae);
                int a = RecyclerView.SCROLL_STATE_DRAGGING;
                int b = RecyclerView.SCROLL_STATE_IDLE;
                int c = RecyclerView.SCROLL_STATE_SETTLING;
                Log.e(TAG,"newState="+newStae);
                if (newStae==RecyclerView.SCROLL_STATE_IDLE&&lastItemPosition==mAdapter.getItemCount()-1){
                    pageId+=I.PAGE_SIZE_DEFAULT;
                    initData();
                }
            }
            public void  onScrolled(RecyclerView recylerView,int dx, int dy) {
                super.onScrolled(recylerView,dx,dy);
                int f = mGridLayoutManger.findFirstCompletelyVisibleItemPosition();
                int l = mGridLayoutManger.findLastVisibleItemPosition();
                Log.e(TAG,"f="+f+"l="+l);
                lastItemPosition = mGridLayoutManger.findLastVisibleItemPosition();
            }
        });
    }

    private void setPullDownRefreshListener() {
        mSwipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                tvHint.setVisibility(View.VISIBLE);
                pageId = 1;
                initData();
            }
        });
    }

    private void initData() {
        try {
            findNewGoodList(new OkHttpUtils2.OnCompleteListener<NewGoodBean[]>() {
                @Override
                public void onSuccess(NewGoodBean[] result) {
                    Log.e(TAG, "result=" + result[0]);
                    tvHint.setVisibility(View.GONE);
                    mSwipeRefreshLayout.setRefreshing(false);
                    if (result != null) {
                        Log.e(TAG, "reslut.length=" + result.length);
                        ArrayList<NewGoodBean> goodBeanArrayList = Utils.array2List(result);
                        mAdapter.initData(goodBeanArrayList);
                    }
                }

                @Override
                public void onError(String error) {
                    Log.e(TAG, "error=" + error);
                    tvHint.setVisibility(View.GONE);
                    mSwipeRefreshLayout.setRefreshing(false);
                }
            });
        }catch (Exception e){
            e.printStackTrace();
        }
    }



            private void findNewGoodList(OkHttpUtils2.OnCompleteListener<NewGoodBean[]> listener) throws  Exception{
                OkHttpUtils2<NewGoodBean[]> utils = new OkHttpUtils2<NewGoodBean[]>();
                utils.setRequestUrl(I.REQUEST_FIND_NEW_BOUTIQUE_GOODS)
                        .addParam(I.NewAndBoutiqueGood.CAT_ID, String.valueOf(I.CAT_ID))
                        .addParam(I.PAGE_ID, String.valueOf(pageId))
                        .addParam(I.PAGE_SIZE, String.valueOf(I.PAGE_SIZE_DEFAULT))
                        .targetClass(NewGoodBean[].class)
                        .execute( listener);

}

    private void initView(View layout) {
        mSwipeRefreshLayout = (SwipeRefreshLayout) layout.findViewById(R.id.srl_new_good);
        mSwipeRefreshLayout.setColorSchemeColors(
               getResources().getColor(R.color.google_blue) ,
                getResources().getColor(R.color.google_yellow),
                getResources().getColor(R.color.google_green),
                getResources().getColor(R.color.google_red)
        );
        mRecyclerView = (RecyclerView) layout.findViewById(R.id.rv_new_good);
        mGridLayoutManger = new GridLayoutManager(mContext, I.COLUM_NUM);
        mGridLayoutManger.setOrientation(LinearLayoutManager.VERTICAL);
        mRecyclerView.setLayoutManager(mGridLayoutManger);
        mAdapter = new GoodAdapter(mContext,mGoodList);
        mRecyclerView.setAdapter(mAdapter);
        tvHint = (TextView) layout.findViewById(R.id.tv_refresh_hint);
    }

}
