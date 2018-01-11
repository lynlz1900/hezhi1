package com.wukong.hezhi.function.nfcvreader;

import java.io.IOException;
import java.util.ArrayList;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import android.nfc.tech.NfcV;

public class NFCVReader {
	private static int TimeOut = 6000;

	private String[][] mTechLists;
	private String mUid = null;
	private byte[] mRandom = null;
	private String mDeviceId;
	private String errorMessage;

	/**
	 * 
	 * @param mNfcv
	 * @return
	 * @throws IOException
	 */
	private byte[] actAu(NfcV mNfcv) throws IOException {
		byte[] tmpCmd = new byte[7];
		try {
			System.arraycopy(NfcCommands.ActAu, 0, tmpCmd, 0, 3);
			System.arraycopy(this.mRandom, 0, tmpCmd, 3, 4);
		} catch (Exception e) {
			e.toString();
		}

		return mNfcv.transceive(tmpCmd);
	}

	private String getUId(NfcV mNfcv) throws IOException {
		String uid = null;
		byte receive[] = mNfcv.transceive(NfcCommands.Inventory);
		if (receive.length > 0) {
			uid = parseUId(receive);
		}
		return uid;
	}

	private String parseUId(byte[] receive) {
		String uid = null;
		if (receive[0] == 0x00) {
			byte[] tmp = new byte[receive.length - 2];
			System.arraycopy(receive, 2, tmp, 0, tmp.length);
			uid = DataConvert.convertByteToString(tmp);
		}
		return uid;
	}

	/**
	 * 
	 * @param mNfcv
	 * @param uid
	 * @return
	 * @throws IOException
	 */
	private byte[] select(NfcV mNfcv, byte[] uid) throws IOException {
		byte[] tmpCmd = new byte[10];
		System.arraycopy(NfcCommands.Select, 0, tmpCmd, 0, 2);
		System.arraycopy(uid, 0, tmpCmd, 2, 8);
		return mNfcv.transceive(tmpCmd);
	}

	/**
	 * 
	 * @param mNfcv
	 * @param uid
	 * @return
	 * @throws IOException
	 */
	private byte[] readBlock(NfcV mNfcv) throws IOException {
		byte[] tmpCmd = new byte[3];
		System.arraycopy(NfcCommands.ReadSingleBlock, 0, tmpCmd, 0, 2);
		tmpCmd[2] = 0x00;
		return mNfcv.transceive(tmpCmd);
	}

	// ---------------------------------------------------------------------------------
	/**
	 * 
	 * 1.获取标签UID (获取标签USER信息) 2.将UID传递到后台进行校验并获取追溯结果 3.整理追溯信息并启用信息显示界面
	 * 
	 * @param mNfcv
	 */
	public void oneWayNfc(NfcV mNfcv) {

		byte[] token = null;
		try {
			if (mNfcv.isConnected())
				mNfcv.close();
			mNfcv.connect();// 断点不能打在这个方法执行之前
			if (mNfcv.isConnected()) {
				mUid = getUId(mNfcv);
				if (null != mUid) {

					byte[] result;
					// The tag will be into Select mode
					result = select(mNfcv, DataConvert.hexStringToBytes(mUid));

					if (result[0] == 0x00) {
						result = readBlock(mNfcv);
						System.out
								.println(DataConvert.bytesToHexString(result));
						// byte keyIndex = result[1];
						// byte[] verifyCode = new byte[4*3]; //授权码
						// System.arraycopy(result, 1+3*4, verifyCode, 0,
						// verifyCode.length);
					}

					if (result[0] == 0x00) {
						// Execute ActAu Command
						result = actAu(mNfcv);
						if (result[0] == 0x00) {
							token = new byte[8];
							System.arraycopy(result, 1, token, 0, 8);
							// send to auth
						} else {

							mNfcv.close();
							return;
						}
					} else {

						mNfcv.close();
						return;
					}
				} else {

					mNfcv.close();
					return;
				}
			}
			mNfcv.close();
		} catch (IOException e) {

			return;
		}

	}

}