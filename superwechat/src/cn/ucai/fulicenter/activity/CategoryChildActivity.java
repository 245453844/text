package cn.ucai.fulicenter.activity;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;


import java.util.ArrayList;
import java.util.List;

import cn.ucai.fulicenter.D;
import cn.ucai.fulicenter.I;
import cn.ucai.fulicenter.R;
import cn.ucai.fulicenter.adapter.GoodAdapter;
import cn.ucai.fulicenter.bean.NewGoodBean;
import cn.ucai.fulicenter.data.OkHttpUtils2;
import cn.ucai.fulicenter.utils.Utils;
import cn.ucai.fulicenter.view.DisplayUtils;

/**
 * A simple {@link Fragment} subclass.
 */
public class CategoryChildActivity extends BaseActivity {
    private final static String TAG = NewGoodsFragment.class.getSimpleName();
    CategoryChildActivity mContext;
    SwipeRefreshLayout mSwipeRefreshLayout;
    RecyclerView mRecyclerView;
    GridLayoutManager mGridLayoutManger;
    GoodAdapter mAdapter;
    List<NewGoodBean> mGoodList;
    TextView tvHint;
    Button btnSortPrice;
    Button btnSortAddTime;
    boolean mSortPriceAsc;
    boolean mSortAddTimeAsc;
    int sortBy;
    int action =  I.ACTION_DOWNLOAD;
    int pageId = 0;
    int catId = 0;
    protected  void  onCreate(Bundle arg0){
        super.onCreate(arg0);
        mContext = this;
        setContentView(R.layout.activity_category_child);
        mGoodList = new ArrayList<NewGoodBean>();
        sortBy = I.SORT_BY_ADDTIME_DESC;
        initView();
        initData();
        setListener();

    }
    private void setListener() {
        setPullDownRefreshListener();
        setPullUpRefreshListener();
        SortStatusChangedListener listener = new SortStatusChangedListener();
        btnSortPrice.setOnClickListener(listener);
        btnSortAddTime.setOnClickListener(listener);

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
                    if (mAdapter.isMore()) {
                        action = I.ACTION_PULL_UP;
                        pageId += I.PAGE_SIZE_DEFAULT;
                        initData();
                    }
                }
            }
            public void  onScrolled(RecyclerView recylerView,int dx, int dy) {
                super.onScrolled(recylerView,dx,dy);
                int f = mGridLayoutManger.findFirstCompletelyVisibleItemPosition();
                int l = mGridLayoutManger.findLastVisibleItemPosition();
                Log.e(TAG,"f="+f+"l="+l);
                lastItemPosition = mGridLayoutManger.findLastVisibleItemPosition();
                mSwipeRefreshLayout.setEnabled(mGridLayoutManger.findFirstVisibleItemPosition()==0);
                if (f==-1||l==-1){
                    lastItemPosition=mAdapter.getItemCount()-1;
                }
            }
        });
    }

    private void setPullDownRefreshListener() {
        mSwipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                action =I.ACTION_PULL_DOWN;
                pageId = 0;
                mSwipeRefreshLayout.setRefreshing(true);
                tvHint.setVisibility(View.VISIBLE);
                initData();
            }
        });
    }

    private void initData() {
        catId = getIntent().getIntExtra(I.NewAndBoutiqueGood.CAT_ID,0);
        Log.e(TAG,"catId="+catId);
        if (catId<0){
            finish();
        }
        try {
            findBoutiqueChildList(new OkHttpUtils2.OnCompleteListener<NewGoodBean[]>() {
                @Override
                public void onSuccess(NewGoodBean[] result) {
                    Log.e(TAG, "result=" + result[0]);
                    mSwipeRefreshLayout.setRefreshing(false);
                    tvHint.setVisibility(View.GONE);
                    mAdapter.setMore(true);
                    mAdapter.setFooterString(getResources().getString(R.string.load_more));
                    if (result != null) {
                        Log.e(TAG, "reslut.length=" + result.length);
                        ArrayList<NewGoodBean> goodBeanArrayList = Utils.array2List(result);
                        if (action==I.ACTION_DOWNLOAD||action==I.ACTION_PULL_DOWN){
                        mAdapter.initItem(goodBeanArrayList);
                        }else {
                            mAdapter.addItem(goodBeanArrayList);
                        }
                        if (goodBeanArrayList.size()<I.PAGE_SIZE_DEFAULT){
                            mAdapter.setMore(false);
                            mAdapter.setFooterString(getResources().getString(R.string.no_more));
                        }
                    }else {
                        mAdapter.setMore(false);
                        mAdapter.setFooterString(getResources().getString(R.string.no_more));
                    }
                }

                @Override
                public void onError(String error) {
                    Log.e(TAG, "error=" + error);
                    mSwipeRefreshLayout.setRefreshing(false);
                    tvHint.setVisibility(View.GONE);
                    Toast.makeText(mContext,error,Toast.LENGTH_SHORT).show();
                }
            });
        }catch (Exception e){
            e.printStackTrace();
        }
    }



            private void findBoutiqueChildList(OkHttpUtils2.OnCompleteListener<NewGoodBean[]> listener) throws  Exception{
                OkHttpUtils2<NewGoodBean[]> utils = new OkHttpUtils2<NewGoodBean[]>();
                utils.setRequestUrl(I.REQUEST_FIND_NEW_BOUTIQUE_GOODS)
                        .addParam(I.NewAndBoutiqueGood.CAT_ID, String.valueOf(catId))
                        .addParam(I.PAGE_ID, String.valueOf(pageId))
                        .addParam(I.PAGE_SIZE, String.valueOf(I.PAGE_SIZE_DEFAULT))
                        .targetClass(NewGoodBean[].class)
                        .execute( listener);

}

    private void initView() {
//        String name = getIntent().getStringExtra(D.Boutique.KEY_NAME);
        DisplayUtils.initBcak(mContext);
        mSwipeRefreshLayout = (SwipeRefreshLayout)findViewById(R.id.srl_category_child);
        mSwipeRefreshLayout.setColorSchemeColors(
                getResources().getColor(R.color.google_blue) ,
                getResources().getColor(R.color.google_yellow),
                getResources().getColor(R.color.google_green),
                getResources().getColor(R.color.google_red)
        );
        mGridLayoutManger = new GridLayoutManager(mContext, I.COLUM_NUM);
        mGridLayoutManger.setOrientation(LinearLayoutManager.VERTICAL);
        mRecyclerView = (RecyclerView)findViewById(R.id.rv_category_child);
        mRecyclerView.setHasFixedSize(true );
        mRecyclerView.setLayoutManager(mGridLayoutManger);
        mAdapter = new GoodAdapter(mContext,mGoodList);
        mRecyclerView.setAdapter(mAdapter);
        tvHint = (TextView)findViewById(R.id.tv_refresh_hint);
    }
    class  SortStatusChangedListener implements  View.OnClickListener{
        @Override
        public void onClick(View view) {
            switch (view.getId()){
                case R.id.btn_sort_price:
                    if (mSortPriceAsc){
                        sortBy = I.SORT_BY_PRICE_ASC;
                    }else {
                        sortBy = I.SORT_BY_PRICE_DESC;
                    }
                    mSortPriceAsc = !mSortPriceAsc;
                    break;
                case R.id.btn_sort_addtime:
                    if (mSortAddTimeAsc){
                        sortBy = I.SORT_BY_ADDTIME_ASC;
                    }else {
                        sortBy = I.SORT_BY_ADDTIME_DESC;
                    }
                    mSortAddTimeAsc = !mSortAddTimeAsc;
                    break;
            }
            mAdapter.setSoryBy(sortBy);
        }
    }

}
