package cn.ucai.superwechat.task;

import android.content.Context;
import android.content.Intent;
import android.util.Log;

import java.util.List;
import java.util.Map;

import cn.ucai.superwechat.I;
import cn.ucai.superwechat.SuperWeChatApplication;
import cn.ucai.superwechat.bean.Result;
import cn.ucai.superwechat.bean.UserAvatar;
import cn.ucai.superwechat.data.OkHttpUtils2;
import cn.ucai.superwechat.utils.Utils;

/**
 * Created by liulian on 2016/7/22.
 */
public class DownloadContactListTask {
    private final static String TAG = DownloadContactListTask.class.getSimpleName();
    String username;
    Context mcontext;

    public DownloadContactListTask( Context context,String username) {
        this.mcontext=context;
        this.username = username;
    }
    public  void  exectue(){
        final OkHttpUtils2<String>utils = new OkHttpUtils2<String>();
        utils.setRequestUrl(I.REQUEST_DOWNLOAD_CONTACT_ALL_LIST)
                .addParam(I.Contact.USER_NAME,username)
                .targetClass(String.class)
                .execute(new OkHttpUtils2.OnCompleteListener<String>() {
                    @Override
                    public void onSuccess(String s) {
                        Log.e(TAG,"s="+s);
                        Result result= Utils.getListResultFromJson(s,UserAvatar.class);
                        Log.e(TAG,"resulut="+result);
                        List<UserAvatar>list=(List<UserAvatar>)result.getRetData();
                        if (list!=null&&list.size()>0){
                            SuperWeChatApplication.getInstance().setUserList(list);
                            mcontext.sendStickyBroadcast(new Intent("update_contact_list"));
                           Map<String,UserAvatar> userMap= SuperWeChatApplication.getInstance().getUserMap();
                            for(UserAvatar u:list){
                                userMap.put(u.getMUserName(),u);
                            }
                        }


                    }

                    @Override
                    public void onError(String error) {
                        Log.e(TAG,"error="+error);
                    }
                });
    }

}
