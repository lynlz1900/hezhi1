package com.wukong.hezhi.manager;

import java.util.ArrayList;
import java.util.List;

import com.wukong.hezhi.utils.LogUtil;

/**
 * 
 * @ClassName: ObserveManager
 * @Description: TODO(观察者管理类)
 * @author HuZhiyin
 * @date 2016-10-14 下午3:20:55
 * 
 */
public class ObserveManager {
	private ObserveManager() {
	}

	private static class Holder {
		private static final ObserveManager SINGLETON = new ObserveManager();
	}

	public static ObserveManager getInstance() {
		return Holder.SINGLETON;
	}

	public interface Observer {
		public void updateState(Class notifyTo, Object notifyFrom, Object msg);
	}

	private List<Observer> observers = new ArrayList<ObserveManager.Observer>();

	/** 添加被观察者 */
	public void addObserver(Observer observer) {
		if (observer == null) {
			throw new RuntimeException();
		}
		if (!observers.contains(observer)) {
			observers.add(observer);
		}
		String observerStr = "";
		for (Observer observer2 : observers) {
			observerStr = observerStr + observer2.getClass().getSimpleName()
					+ ",  ";
		}
		LogUtil.d(observerStr);
	}

	/** 移除被观察者 */
	public void removeObserver(Observer observer) {
		if (observer == null) {
			throw new RuntimeException();
		}
		if (observers.contains(observer)) {
			observers.remove(observer);
		}
	}

	/**
	 * 
	 * @Title: notifyState
	 * @Description: TODO(通知被观察的对象，数据发生了变化。)
	 * @param @param notifyTo(消息发送到哪去)
	 * @param @param notifyFrom(消息来自哪里)
	 * @param @param change(消息体)
	 * @return void 返回类型
	 * @throws
	 */
	public void notifyState(Class notifyTo, Object notifyFrom, Object msg) {
		if (observers != null && observers.size() > 0) {
			for (Observer observer : observers) {
				observer.updateState(notifyTo, notifyFrom, msg);
			}
		}
	}
}
