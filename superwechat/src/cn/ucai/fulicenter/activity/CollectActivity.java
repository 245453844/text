package cn.ucai.fulicenter.activity;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

import cn.ucai.fulicenter.D;
import cn.ucai.fulicenter.FuliCenterApplication;
import cn.ucai.fulicenter.I;
import cn.ucai.fulicenter.R;
import cn.ucai.fulicenter.adapter.CollectAdapter;
import cn.ucai.fulicenter.adapter.GoodAdapter;
import cn.ucai.fulicenter.bean.CollectBean;
import cn.ucai.fulicenter.bean.NewGoodBean;
import cn.ucai.fulicenter.data.OkHttpUtils2;
import cn.ucai.fulicenter.utils.Utils;
import cn.ucai.fulicenter.view.DisplayUtils;

/**
 * A simple {@link Fragment} subclass.
 */
public class CollectActivity extends BaseActivity {
    private final static String TAG = NewGoodsFragment.class.getSimpleName();
    CollectActivity mContext;
    SwipeRefreshLayout mSwipeRefreshLayout;
    RecyclerView mRecyclerView;
    GridLayoutManager mGridLayoutManger;
    CollectAdapter mAdapter;
    List<CollectBean> mGoodList;
    int pageId = 0;
    TextView tvHint;
    int action =  I.ACTION_DOWNLOAD;
    int catId = 0;
    protected  void  onCreate(Bundle arg0){
        super.onCreate(arg0);
        mContext = this;
        setContentView(R.layout.activity_collect);
        mGoodList = new ArrayList<CollectBean>();
        initView();
        initData();
        setListener();

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
        String userName =FuliCenterApplication.getInstance().getUserName();
        Log.e(TAG,"userName="+userName);
        if (userName.isEmpty()){
            finish();
        }
        try {
            findCollectList(new OkHttpUtils2.OnCompleteListener<CollectBean[]>() {
                @Override
                public void onSuccess(CollectBean[] result) {
                    Log.e(TAG, "result=" + result[0]);
                    mSwipeRefreshLayout.setRefreshing(false);
                    tvHint.setVisibility(View.GONE);
                    mAdapter.setMore(true);
                    mAdapter.setFooterString(getResources().getString(R.string.load_more));
                    if (result != null) {
                        Log.e(TAG, "reslut.length=" + result.length);
                        ArrayList<CollectBean> collectList = Utils.array2List(result);
                        if (action==I.ACTION_DOWNLOAD||action==I.ACTION_PULL_DOWN){
                        mAdapter.initItem(collectList);
                        }else {
                            mAdapter.addItem(collectList);
                        }
                        if (collectList.size()<I.PAGE_SIZE_DEFAULT){
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



            private void findCollectList(OkHttpUtils2.OnCompleteListener<CollectBean[]> listener) throws  Exception{
                OkHttpUtils2<CollectBean[]> utils = new OkHttpUtils2<CollectBean[]>();
                utils.setRequestUrl(I.REQUEST_FIND_COLLECTS)
                        .addParam(I.Collect.USER_NAME, FuliCenterApplication.getInstance().getUserName())
                        .addParam(I.PAGE_ID, String.valueOf(pageId))
                        .addParam(I.PAGE_SIZE, String.valueOf(I.PAGE_SIZE_DEFAULT))
                        .targetClass(CollectBean[].class)
                        .execute( listener);

}

    private void initView() {
        DisplayUtils.initBackWithTitle(mContext,"收藏的宝贝");
        mSwipeRefreshLayout = (SwipeRefreshLayout)findViewById(R.id.srl_collect);
        mSwipeRefreshLayout.setColorSchemeColors(
                getResources().getColor(R.color.google_blue) ,
                getResources().getColor(R.color.google_yellow),
                getResources().getColor(R.color.google_green),
                getResources().getColor(R.color.google_red)
        );
        mGridLayoutManger = new GridLayoutManager(mContext, I.COLUM_NUM);
        mGridLayoutManger.setOrientation(LinearLayoutManager.VERTICAL);
        mRecyclerView = (RecyclerView)findViewById(R.id.rv_collect);
        mRecyclerView.setHasFixedSize(true );
        mRecyclerView.setLayoutManager(mGridLayoutManger);
        mAdapter = new CollectAdapter(mContext,mGoodList);
        mRecyclerView.setAdapter(mAdapter);
        tvHint = (TextView)findViewById(R.id.tv_refresh_hint);
    }

}
