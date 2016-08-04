package cn.ucai.fulicenter.view;

import android.app.Activity;
import android.view.View;
import android.widget.TextView;

import cn.ucai.fulicenter.R;

/**
 * Created by liulian on 2016/8/3.
 */
public class DisplayUtils {
    public  static void initBcak(final Activity activity){
        activity.findViewById(R.id.backClickArea).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                activity.finish();
            }
        });
    }
    public  static  void  initBackWithTitle(final  Activity activity, final String title){
           initBcak(activity);
        ((TextView)activity.findViewById(R.id.tv_common_title)).setText(title);
    }
}
