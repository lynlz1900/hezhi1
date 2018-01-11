package com.wukong.hezhi.ui.view;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.GridView;
import android.widget.ListView;
/**
 * 
* @ClassName: NoScrollGridview 
* @Description: TODO(被scrollview包含的Gridview。) 
* @author HuZhiyin 
* @date 2017-4-31 上午8:57:05 
*
 */
public class NoScrollGridview extends GridView{  
  
        public NoScrollGridview(Context context, AttributeSet attrs) {  
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