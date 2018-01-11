package com.wukong.hezhi.manager;

import java.io.PrintWriter;
import java.io.StringWriter;
import com.wukong.hezhi.function.cashcase.Cockroach;
import com.wukong.hezhi.utils.LogUtil;
import android.os.Handler;
import android.os.Looper;

/***
 * 
 * @ClassName: CashCaseManager
 * @Description: TODO(异常捕获管理类)
 * @author HuangFeiFei
 * @date 2017-9-28
 * 
 */
public class CashCaseManager {

	/** 异常是否确认捕获 */
	private boolean cashCaseIsGet = false;

	private static class Holder {
		private static final CashCaseManager SINGLETON = new CashCaseManager();
	}

	public static CashCaseManager getInstance() {
		return Holder.SINGLETON;
	}

	public boolean isCashCaseIsGet() {
		return cashCaseIsGet;
	}

	public void setCashCaseIsGet(boolean addrChange) {
		this.cashCaseIsGet = addrChange;
	}
	
	/** 捕获异常 **/
	public void initCashCase(){
		if(!cashCaseIsGet){
			Cockroach.uninstall();
		}else{
			Cockroach.install(new Cockroach.ExceptionHandler() {
	            @Override
	            public void handlerException(final Thread thread, final Throwable throwable) {
	                new Handler(Looper.getMainLooper()).post(new Runnable() {
	                    @Override
	                    public void run() {
	                    	postThrowable(throwable);
	                    }
	                });
	            }
	        });
		}
	}
	
	/** 保存异常 **/
	private void postThrowable(Throwable throwable){
		StringWriter wr = null;
    	PrintWriter pw = null;
        try {
        	wr = new StringWriter();
            pw = new PrintWriter(wr);
        	throwable.printStackTrace(pw);
        	String error = wr.toString();
        	LogUtil.e(error);
        	pw.close();
        	wr.close();
        } catch (Throwable e) {
        }finally{
        	pw.close();
        	try {
				wr.close();
			} catch (Exception e) {
			}
        }
	}
}
