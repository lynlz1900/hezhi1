package com.wukong.hezhi.ui.view;

import android.content.ContextWrapper;

import com.unity3d.player.UnityPlayer;

/**
 * @author HuZhiyin
 * @ClassName: ${FILE_NAME}
 * @Description: ${todo}(这里用一句话描述这个类的作用)
 * @date 2017/12/28
 */


public class MyUnityPlayer extends UnityPlayer{
    public MyUnityPlayer(ContextWrapper contextWrapper) {
        super(contextWrapper);
    }
    @Override
    protected void kill() {}

}
