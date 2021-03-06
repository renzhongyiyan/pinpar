package com.ipinpar.app.widget;

import android.app.Dialog;
import android.content.Context;
import android.content.res.Resources;
import android.util.DisplayMetrics;
import android.view.Gravity;
import android.view.Window;
import android.view.WindowManager;

public class PartyHomeVenueDialog extends Dialog{

	private static int default_width = 295; // 默认宽度  
    private static int default_height = 425;// 默认高度  
  
    public PartyHomeVenueDialog(Context context) {  
        super(context);  
    }  
  
    public PartyHomeVenueDialog(Context context, int layout, int style) {  
        this(context, default_width, default_height, layout, style);  
    }  
  
    public PartyHomeVenueDialog(Context context, int width, int height, int layout,  
            int style) {  
        super(context, style);  
        // 设置内容  
        setContentView(layout);  
        // 设置窗口属性  
        Window window = getWindow();  
        WindowManager.LayoutParams params = window.getAttributes();  
        // 设置宽度、高度、密度、对齐方式  
        float density = getDensity(context);  
        params.width = (int) (width * density);  
        params.height = (int) (height * density);  
        params.gravity = Gravity.CENTER;  
        params.alpha = 5.0f;
        window.setAttributes(params);  
  
    }  
  
    /** 
     * 获取显示密度 
     *  
     * @param context 
     * @return 
     */  
    public float getDensity(Context context) {  
        Resources res = context.getResources();  
        DisplayMetrics dm = res.getDisplayMetrics();  
        return dm.density;  
    }  
	
}
