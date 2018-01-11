package com.wukong.hezhi.bean;

import java.io.Serializable;

public class lIstViewItem implements Serializable
{
	private static final long serialVersionUID = 1L;
	//用于区分listview显示的不同item,告诉适配器我这是什么类型，listview适配器根据type决定怎么显示
     public int type;
	  public String url ;
	     
    public lIstViewItem(int type, String url)
    {
        this.type = type;
        this.url = url;
    }
}