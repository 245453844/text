package cn.ucai.fulicenter.task;

import android.content.Context;
import android.content.Intent;
import android.util.Log;

import java.util.List;
import java.util.Map;

import cn.ucai.fulicenter.FuliCenterApplication;
import cn.ucai.fulicenter.I;
import cn.ucai.fulicenter.activity.FuliCenterMainActivity;
import cn.ucai.fulicenter.bean.MessageBean;
import cn.ucai.fulicenter.bean.Result;
import cn.ucai.fulicenter.bean.UserAvatar;
import cn.ucai.fulicenter.data.OkHttpUtils2;
import cn.ucai.fulicenter.utils.Utils;

/**
 * Created by liulian on 2016/7/22.
 */
public class DownloadCollectCountTask {
    private final static String TAG = DownloadCollectCountTask.class.getSimpleName();
    String username;
    Context mcontext;

    public DownloadCollectCountTask(Context context, String username) {
        this.mcontext=context;
        this.username = username;
    }
    public  void  exectue(){
        final OkHttpUtils2<MessageBean>utils = new OkHttpUtils2<MessageBean>();
        utils.setRequestUrl(I.REQUEST_FIND_COLLECT_COUNT)
                .addParam(I.Collect.USER_NAME,username)
                .targetClass(MessageBean.class)
                .execute(new OkHttpUtils2.OnCompleteListener<MessageBean>() {
                    @Override
                    public void onSuccess(MessageBean s) {
                        Log.e(TAG,"s="+s);
                        if (s!=null) {
                            if (s.isSuccess()){
                                FuliCenterApplication.getInstance().setCollectCount(Integer.valueOf(s.getMsg()));
                            }else {
                                FuliCenterApplication.getInstance().setCollectCount(0);
                            }
                            mcontext.sendStickyBroadcast(new Intent("update_collect"));

                        }
                    }

                    @Override
                    public void onError(String error) {
                        Log.e(TAG,"error="+error);
                    }
                });
    }

}
