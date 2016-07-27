package cn.ucai.superwechat.task;

import android.content.Context;
import android.content.Intent;
import android.util.Log;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import cn.ucai.superwechat.I;
import cn.ucai.superwechat.SuperWeChatApplication;
import cn.ucai.superwechat.bean.MemberUserAvatar;
import cn.ucai.superwechat.bean.Result;
import cn.ucai.superwechat.bean.UserAvatar;
import cn.ucai.superwechat.data.OkHttpUtils2;
import cn.ucai.superwechat.utils.Utils;

/**
 * Created by liulian on 2016/7/22.
 */
public class DownloadMemberTask {
    private final static String TAG = DownloadMemberTask.class.getSimpleName();
    String hxid;
    Context mcontext;

    public DownloadMemberTask(Context context, String hxid) {
        this.mcontext=context;
        this.hxid = hxid;
    }
    public  void  exectue(){
        final OkHttpUtils2<String>utils = new OkHttpUtils2<String>();
        utils.setRequestUrl(I.REQUEST_DOWNLOAD_CONTACT_ALL_LIST)
                .addParam(I.Member.GROUP_HX_ID, hxid)
                .targetClass(String.class)
                .execute(new OkHttpUtils2.OnCompleteListener<String>() {
                    @Override
                    public void onSuccess(String s) {
                        Log.e(TAG,"s="+s);
                        Result result= Utils.getListResultFromJson(s,MemberUserAvatar.class);
                        Log.e(TAG,"resulut="+result);
                        List<MemberUserAvatar>list=(List<MemberUserAvatar>)result.getRetData();
                        if (list!=null&&list.size()>0){
                            Map<String,HashMap<String,MemberUserAvatar>> memberMap =
                            SuperWeChatApplication.getInstance().getMemberMap();
                            if (!memberMap.containsKey(hxid)){
                                memberMap.put(hxid,new HashMap<String, MemberUserAvatar>());
                            }
                            HashMap<String,MemberUserAvatar> hxidMembers = memberMap.get(hxid);
                            for(MemberUserAvatar u:list){
                                hxidMembers .put(u.getMUserName(),u);
                            }
                            mcontext.sendStickyBroadcast(new Intent("update_contact_list"));
                        }


                    }

                    @Override
                    public void onError(String error) {
                        Log.e(TAG,"error="+error);
                    }
                });
    }

}
