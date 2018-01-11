package com.wukong.hezhi.function.nfcvreader;


public class NfcCommands {

	public static final byte[] Inventory = new byte[] { 0x26, 0x01, 0x00 };
	public static final byte[] StayQuiet = new byte[] { 0x00, 0x02 };
	public static final byte[] RFU = new byte[] {};
	public static final byte[] ReadSingleBlock = new byte[] { 0x02, 0x20 };
	public static final byte[] WriteSingleBlock = new byte[] { 0x02, 0x21 };
	public static final byte[] LockBlock = new byte[] { 0x00, 0x22 };
	public static final byte[] ReadMultipleBlocks = new byte[] { 0x00, 0x23 };
	public static final byte[] WriteMultipleBlocks = new byte[] { 0x00, 0x24 };
	public static final byte[] Select = new byte[] { 0x22, 0x25 };
	public static final byte[] ResetToReady = new byte[] { 0x00, 0x26 };
	public static final byte[] WriteAFI = new byte[] { 0x00, 0x27 };
	public static final byte[] LockAFI = new byte[] { 0x00, 0x28 };
	public static final byte[] WriteDSFID = new byte[] { 0x00, 0x29 };
	public static final byte[] LockDSFID = new byte[] { 0x00, 0x2A };
	public static final byte[] GetSystemInformation = new byte[] { 0x00, 0x2B };
	public static final byte[] GetMultipleBlockSecurityStatus = new byte[] {0x00, 0x2C };
	public static final byte[] ActAu = new byte[] { 0x12, (byte) 0xb2, (byte) 0x81 };
	public static final byte[] ReqAu = new byte[] { 0x12, (byte) 0xB0, (byte) 0x81 };
	public static final byte[] Auth = new byte[] { 0x12, (byte) 0xB1, (byte) 0x81 };
	public static final byte[] SecComm = new byte[] { 0x12, (byte) 0xB3, (byte) 0x81, 0x02 };
}
