package cn.ucai.fulicenter.task;

import android.content.Context;
import android.content.Intent;
import android.util.Log;

import java.util.List;

import cn.ucai.fulicenter.FuliCenterApplication;
import cn.ucai.fulicenter.I;
import cn.ucai.fulicenter.bean.GroupAvatar;
import cn.ucai.fulicenter.bean.Result;
import cn.ucai.fulicenter.data.OkHttpUtils2;
import cn.ucai.fulicenter.utils.Utils;

/**
 * Created by liulian on 2016/7/22.
 */
public class DownloadGroupListTask {
    private final static String TAG = DownloadGroupListTask.class.getSimpleName();
    String username;
    Context mcontext;

    public DownloadGroupListTask(Context context, String username) {
        this.mcontext=context;
        this.username = username;
    }
    public  void  exectue(){
        final OkHttpUtils2<String>utils = new OkHttpUtils2<String>();
        utils.setRequestUrl(I.REQUEST_FIND_GROUP_BY_USER_NAME)
                .addParam(I.User.USER_NAME,username)
                .targetClass(String.class)
                .execute(new OkHttpUtils2.OnCompleteListener<String>() {
                    @Override
                    public void onSuccess(String s) {
                        Log.e(TAG,"s="+s);
                        Result result= Utils.getListResultFromJson(s,GroupAvatar.class);
                        Log.e(TAG,"resulut="+result);
                        List<GroupAvatar>list=(List<GroupAvatar>)result.getRetData();
                        if (list!=null&&list.size()>0) {
                            FuliCenterApplication.getInstance().setGroupList(list);
                            for (GroupAvatar g : list) {
                                FuliCenterApplication.getInstance().getGroupMap().put(g.getMGroupHxid(),g);
                            }
                            mcontext.sendStickyBroadcast(new Intent("update_group_list"));

                        }

                    }

                    @Override
                    public void onError(String error) {
                        Log.e(TAG,"error="+error);
                    }
                });
    }

}
