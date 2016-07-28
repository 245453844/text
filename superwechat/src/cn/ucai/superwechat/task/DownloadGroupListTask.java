package cn.ucai.superwechat.task;

import android.content.Context;
import android.content.Intent;
import android.util.Log;

import com.google.android.gms.internal.dy;

import java.security.acl.Group;
import java.util.List;
import java.util.Map;

import cn.ucai.superwechat.I;
import cn.ucai.superwechat.SuperWeChatApplication;
import cn.ucai.superwechat.bean.GroupAvatar;
import cn.ucai.superwechat.bean.Result;
import cn.ucai.superwechat.bean.UserAvatar;
import cn.ucai.superwechat.data.OkHttpUtils2;
import cn.ucai.superwechat.utils.Utils;

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
                            SuperWeChatApplication.getInstance().setGroupList(list);
                            for (GroupAvatar g : list) {
                                SuperWeChatApplication.getInstance().getGroupMap().put(g.getMGroupHxid(),g);
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
