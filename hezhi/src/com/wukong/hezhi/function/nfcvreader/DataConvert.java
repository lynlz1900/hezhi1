package com.wukong.hezhi.function.nfcvreader;

public class DataConvert {

	/**
	 * Convert byte[] to hex string.
	 * 这里我们可以将byte转换成int，然后利用Integer.toHexString(int)来转换成16进制字符串。
	 * 
	 * @param src
	 * @return hex string
	 */
	public static String bytesToHexString(byte[] src) {
		StringBuilder stringBuilder = new StringBuilder("");
		if (src == null || src.length <= 0) {
			return null;
		}
		for (int i = 0; i < src.length; i++) {
			int v = src[i] & 0xFF;
			String hv = Integer.toHexString(v);
			if (hv.length() < 2) {
				stringBuilder.append(0);
			}
			stringBuilder.append(hv);
		}
		return stringBuilder.toString();
	}

	/**
	 * Convert hex string to byte[]
	 * 
	 * @param hexString
	 * @return byte[]
	 */
	public static byte[] hexStringToBytes(String hexString) {
		if (hexString == null || hexString.equals("")) {
			return null;
		}
		hexString = hexString.toUpperCase();
		int length = hexString.length() / 2;
		char[] hexChars = hexString.toCharArray();
		byte[] d = new byte[length];
		for (int i = 0; i < length; i++) {
			int pos = i * 2;
			d[i] = (byte) (charToByte(hexChars[pos]) << 4 | charToByte(hexChars[pos + 1]));
		}
		return d;
	}

	/**
	 * Convert char to byte
	 * 
	 * @param c
	 * @return byte
	 */
	private static byte charToByte(char c) {
		return (byte) "0123456789ABCDEF".indexOf(c);
	}

	/**
	 * @函数功能: BCD码转为10进制串(阿拉伯数据)
	 * @输入参数: BCD码
	 * @输出结果: 10进制串
	 */
	public static String bcdToStr(byte[] bytes) {
		StringBuffer temp = new StringBuffer(bytes.length * 2);

		for (int i = 0; i < bytes.length; i++) {
			temp.append((byte) ((bytes[i] & 0xf0) >>> 4));
			temp.append((byte) (bytes[i] & 0x0f));
		}
		return temp.toString().substring(0, 1).equalsIgnoreCase("0") ? temp
				.toString().substring(1) : temp.toString();
	}

	/**
	 * Convert Byte Array To String
	 * 
	 * @param bArray
	 * @return 转换后的结果
	 */
	public static String convertByteToString(byte[] bArray) {

		StringBuffer sb = new StringBuffer();

		for (int i = 0; i < bArray.length; i++) {
			sb.append(byteToString(bArray[i]));
		}

		return sb.toString();

	}

	private static String byteToString(byte b) {
		byte high, low;
		byte maskHigh = (byte) 0xf0;
		byte maskLow = 0x0f;

		high = (byte) ((b & maskHigh) >> 4);
		low = (byte) (b & maskLow);

		StringBuffer buf = new StringBuffer();
		buf.append(findHex(high));
		buf.append(findHex(low));

		return buf.toString();
	}

	private static char findHex(byte b) {
		int t = new Byte(b).intValue();
		t = t < 0 ? t + 16 : t;

		if ((0 <= t) && (t <= 9)) {
			return (char) (t + '0');
		}

		return (char) (t - 10 + 'A');
	}

	public static byte[] convertBytePointer(byte[] data) {
		byte[] tmpData = new byte[data.length];
		for (int i = 0, j = data.length - 1; i < data.length; i++, j--) {
			tmpData[i] = data[j];
		}
		return tmpData;
	}

	public static byte[] intToBytes(int value) {

		byte[] byteArray = new byte[4];

		byteArray[0] = (byte) (value & 0xff);// 最低位
		byteArray[1] = (byte) ((value >> 8) & 0xff);// 次低位
		byteArray[2] = (byte) ((value >> 16) & 0xff);// 次高位
		byteArray[3] = (byte) (value >>> 24);// 最高位,无符号右移。

		return byteArray;
	}
}
