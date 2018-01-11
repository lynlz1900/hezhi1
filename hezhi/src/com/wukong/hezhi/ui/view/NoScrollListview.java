package com.wukong.hezhi.ui.view;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.ListView;

/**
 * 
* @ClassName: NoScrollListview 
* @Description: TODO(被scrollview包含的listview。) 
* @author HuZhiyin 
* @date 2017-4-31 上午8:57:05 
*
 */
public class NoScrollListview extends ListView{  
  
        public NoScrollListview(Context context, AttributeSet attrs) {  
                super(context, attrs);  
        }  
          
        /** 
         * 设置不滚动 
         */  
        public void onMeasure(int widthMeasureSpec, int heightMeasureSpec)  
        {  
                int expandSpec = MeasureSpec.makeMeasureSpec(Integer.MAX_VALUE >> 2,  
                                MeasureSpec.AT_MOST);  
                super.onMeasure(widthMeasureSpec, expandSpec);  
  
        }  
  
} 