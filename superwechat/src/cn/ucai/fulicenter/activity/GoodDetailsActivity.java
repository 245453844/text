package cn.ucai.fulicenter.activity;

import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import cn.ucai.fulicenter.D;
import cn.ucai.fulicenter.I;
import cn.ucai.fulicenter.R;
import cn.ucai.fulicenter.bean.AlbumBean;
import cn.ucai.fulicenter.bean.GoodDetailsBean;
import cn.ucai.fulicenter.data.OkHttpUtils2;
import cn.ucai.fulicenter.view.DisplayUtils;
import cn.ucai.fulicenter.view.FlowIndicator;
import cn.ucai.fulicenter.view.SlideAutoLoopView;

/**
 * Created by liulian on 2016/8/3.
 */
public class GoodDetailsActivity extends BaseActivity {
    private final  static String TAG = GoodDetailsActivity.class.getSimpleName();
    ImageView ivShare;
    ImageView ivCollect;
    ImageView ivCart;
    TextView tvCartCount;
    TextView tvGoodEnglishName;
    TextView tvGoodName;
    TextView tvGoodPriceCurrent;
    TextView tvGoodPriceShop;
    SlideAutoLoopView mSlideAutoLoopView;
    FlowIndicator mFloewIndicator;
    WebView wvGoodBrief;
    int mGoodId;
    GoodDetailsActivity mContext;
    GoodDetailsBean mGoodDetails;
    protected  void  onCreate(Bundle arg0){
        super.onCreate(arg0);
        setContentView(R.layout.activity_good_details);
        mContext = this;
        initView();
        initData();
    }

    private void initData() {
        mGoodId = getIntent().getIntExtra(D.GoodDetails.KEY_GOODS_ID,0);
        Log.e(TAG,"mGoodId="+mGoodId);
        if (mGoodId>0){
            getGoodDetailsByGoodId(new OkHttpUtils2.OnCompleteListener<GoodDetailsBean>() {
                @Override
                public void onSuccess(GoodDetailsBean result) {
                    Log.e(TAG,"result="+result);
                    if (result!=null){
                        mGoodDetails =result;
                        showGoodDetails();
                    }

                }

                @Override
                public void onError(String error) {
                     Log.e(TAG,"error="+error);
                    finish();
                    Toast.makeText(mContext,"获取商品详情数据失败！",Toast.LENGTH_SHORT).show();
                }
            });
        }else {
            finish();
            Toast.makeText(mContext,"获取商品详情数据失败！",Toast.LENGTH_SHORT).show();
        }
    }

    private void showGoodDetails() {
        tvGoodEnglishName.setText(mGoodDetails.getGoodsEnglishName());
        tvGoodName.setText(mGoodDetails.getGoodsName());
        tvGoodPriceCurrent.setText(mGoodDetails.getCurrencyPrice());
        tvGoodPriceShop.setText(mGoodDetails.getShopPrice());
        mSlideAutoLoopView.startPlayLoop(mFloewIndicator,getAlbumImageUrl(),getAlbumImageSize());
        wvGoodBrief.loadDataWithBaseURL(null,mGoodDetails.getGoodsBrief(),D.TEXT_HTML,D.UTF_8,null);
    }

    private  void  getGoodDetailsByGoodId(OkHttpUtils2.OnCompleteListener<GoodDetailsBean> listener){
        OkHttpUtils2<GoodDetailsBean> utils = new OkHttpUtils2<GoodDetailsBean>();
        utils.setRequestUrl(I.REQUEST_FIND_GOOD_DETAILS)
                .addParam(D.GoodDetails.KEY_GOODS_ID,String.valueOf(mGoodId))
                .targetClass(GoodDetailsBean.class)
                .execute(listener);
    }
    private void initView() {
        DisplayUtils.initBcak(mContext);
        ivShare = (ImageView) findViewById(R.id.iv_good_share);
        ivCollect = (ImageView) findViewById(R.id.iv_good_collect);
        ivCart = (ImageView) findViewById(R.id.iv_good_cart);
        tvCartCount = (TextView) findViewById(R.id.tv_cart_count);
        tvGoodEnglishName = (TextView) findViewById(R.id.tv_good_name_english);
        tvGoodName = (TextView) findViewById(R.id.tv_good_name);
        tvGoodPriceCurrent = (TextView) findViewById(R.id.tv_good_price_current);
        tvGoodPriceShop = (TextView) findViewById(R.id.tv_good_price_shop);
        mSlideAutoLoopView = (SlideAutoLoopView) findViewById(R.id.salv);
        mFloewIndicator = (FlowIndicator) findViewById(R.id.indicator);
        wvGoodBrief = (WebView) findViewById(R.id.wv_good_brief);
        WebSettings settings = wvGoodBrief.getSettings();
        settings.setLayoutAlgorithm(WebSettings.LayoutAlgorithm.SINGLE_COLUMN);
        settings.setBuiltInZoomControls(true);
    }

    public int getAlbumImageSize() {
        if (mGoodDetails.getProperties() != null && mGoodDetails.getProperties().length > 0) {
            return mGoodDetails.getProperties()[0].getAlbums().length;
        }
        return 0;
    }

    public String[] getAlbumImageUrl() {
        String [] albumImageUrl =new String[]{};
        if (mGoodDetails.getProperties()!=null&&mGoodDetails.getProperties().length>0){
            AlbumBean[] albums = mGoodDetails.getProperties()[0].getAlbums();
            albumImageUrl =new String[albums.length];
            for (int i=0;i<albumImageUrl.length;i++){
                albumImageUrl[i] = albums[i].getImgUrl();
            }
        }
        return  albumImageUrl;
    }
}
