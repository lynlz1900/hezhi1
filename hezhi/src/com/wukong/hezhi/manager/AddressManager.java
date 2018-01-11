package com.wukong.hezhi.manager;

import com.wukong.hezhi.bean.AddressReceiptInfo;

/***
 * 
 * @ClassName: AddressManage
 * @Description: TODO(收货地址管理类)
 * @author HuangFeiFei
 * @date 2017-9-28
 * 
 */
public class AddressManager {

	/*** 收货地址信息对象 **/
	private AddressReceiptInfo addressReceiptInfo;
	/** 地址是否变更 */
	private boolean addrChange = false;

	private static class Holder {
		private static final AddressManager SINGLETON = new AddressManager();
	}

	public static AddressManager getInstance() {
		return Holder.SINGLETON;
	}

	/** 获取地址 */
	public AddressReceiptInfo getAddr() {
		return addressReceiptInfo;
	}

	/** 保存地址 */
	public void saveAddr(AddressReceiptInfo addressReceiptInfo) {
		this.addressReceiptInfo = addressReceiptInfo;
		addrChange = true;
	}

	/** 用完即删除 ,将所有的属性置空 */
	public void clean() {
		addressReceiptInfo = null;
		addrChange = false;
	}

	/** 删除当前选中的地址 */
	public void deleteAddr(int addrId) {
		if (addressReceiptInfo != null && addrId == addressReceiptInfo.getAddId()) {
			addressReceiptInfo = null;
			addrChange = true;
		}
	}

	/** 地址是否修改 */
	public boolean isAddrChange() {
		return addrChange;
	}

}
