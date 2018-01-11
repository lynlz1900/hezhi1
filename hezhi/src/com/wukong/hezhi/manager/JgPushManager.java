package com.wukong.hezhi.manager;

import java.util.ArrayList;
import java.util.List;

import com.wukong.hezhi.bean.JpushInfo;
import com.wukong.hezhi.bean.ResponseJsonInfo;
import com.wukong.hezhi.db.DBManager;
import com.wukong.hezhi.http.HttpCode;
import com.wukong.hezhi.utils.JsonUtil;

/**
 * 
 * @ClassName: JgPushManager
 * @Description: TODO(极光推送管理类)
 * @author HuZhiyin
 * @date 2017-5-16 上午9:23:27
 * 
 */
public class JgPushManager {
	private JgPushManager() {
	}

	private static class Holder {
		private static final JgPushManager SINGLETON = new JgPushManager();
	}

	/**
	 * 单一实例
	 * */
	public static JgPushManager getInstance() {
		return Holder.SINGLETON;
	}

	/** 保存极光推送传来的数据 */
	public void saveJsonFromJgPush(String jsonStr) {
		ResponseJsonInfo<JpushInfo> info = JsonUtil.fromJson(jsonStr,
				JpushInfo.class);
		if (info != null && info.getHttpCode().equals(HttpCode.SUCESS)) {
			JpushInfo jpushInfo = (JpushInfo) info.getBody();
			jpushInfo.setLocalTime(System.currentTimeMillis() + "");
			jpushInfo.setChecked(false);
			String jsonStr2 = JsonUtil.parseObj2Json(info);// 更改后的json
			saveDB(jpushInfo.getLocalTime(), jsonStr2);
		}
	}

	/** 保存数据库 */
	private void saveDB(String picId, String jsonStr) {
		List<String> picIds = DBManager.getInstance().qurreyPicIds();
		if (!picIds.contains(picId)) {// 如果数据库不存在这个数据，则将这条记录加入数据库
			DBManager.getInstance().insert(picId, DBManager.JGPUSH, jsonStr);
		}
	}

	/** 是否检查过所有信息 */
	public boolean isChecked() {
		boolean isChecked = true;
		List<String> jsonStrs = DBManager.getInstance().qurreyJsonsByTpye(
				DBManager.JGPUSH);
		List<JpushInfo> unityInfos = new ArrayList<JpushInfo>();
		if (jsonStrs != null) {
			for (int i = 0; i < jsonStrs.size(); i++) {
				ResponseJsonInfo info = JsonUtil.fromJson(jsonStrs.get(i),
						JpushInfo.class);
				JpushInfo jpushInfo = (JpushInfo) info.getBody();
				if (!jpushInfo.isChecked()) {// 如果有一个没有检查，isChecked为false
					isChecked = false;
				}
			}
		}
		return isChecked;
	}
}
