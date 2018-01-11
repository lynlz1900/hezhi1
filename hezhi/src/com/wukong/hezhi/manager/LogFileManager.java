package com.wukong.hezhi.manager;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;

import com.wukong.hezhi.constants.HezhiConfig;
import com.wukong.hezhi.utils.DateUtil;

/**
 * 
 * @ClassName: LogFileManager
 * @Description: TODO(日志文件分析管理类)
 * @author Huzhiyin
 * @date 2017年10月10日 上午10:37:35
 *
 */

public class LogFileManager {

	private LogDumper mLogDumper = null;
	private int mPId;
	private File file;

	private LogFileManager() {
		init();
		mPId = android.os.Process.myPid();
	}

	private static class Holder {
		private static final LogFileManager SINGLETON = new LogFileManager();
	}

	/**
	 * 单一实例
	 */
	public static LogFileManager getInstance() {
		return Holder.SINGLETON;
	}

	/**
	 * 初始化目录
	 */
	public void init() {
		file = new File(HezhiConfig.LOG_PATH);
		if (!file.exists()) {
			file.mkdirs();
		}
	}

	public void start() {
		if (mLogDumper == null) {
			mLogDumper = new LogDumper(String.valueOf(mPId), HezhiConfig.LOG_PATH);
		}
		mLogDumper.start();
	}

	public void stop() {
		if (mLogDumper != null) {
			mLogDumper.stopLogs();
			mLogDumper = null;
		}
	}

	private class LogDumper extends Thread {

		private Process logcatProc;
		private BufferedReader mReader = null;
		private boolean mRunning = true;
		String cmds = null;
		private String mPID;
		private FileOutputStream out = null;

		public LogDumper(String pid, String dir) {
			mPID = pid;
			try {
				out = new FileOutputStream(new File(dir, DateUtil.getTimeDay() + ".log"));
			} catch (FileNotFoundException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

			/**
			 * 
			 * 日志等级：*:v , *:d , *:w , *:e , *:f , *:s
			 * 
			 * 显示当前mPID程序的 E和W等级的日志.
			 * 
			 */
			cmds = "logcat *:e *:w | grep \"(" + mPID + ")\"";
			// cmds = "logcat | grep \"(" + mPID + ")\"";//打印所有日志信息
			// cmds = "logcat -s way";//打印标签过滤信息
			// cmds = "logcat *:e *:i | grep \"(" + mPID + ")\"";

		}

		public void stopLogs() {
			mRunning = false;
		}

		@Override
		public void run() {
			try {
				deleteFile(file);// 删除超过七天的日志
				logcatProc = Runtime.getRuntime().exec(cmds);
				mReader = new BufferedReader(new InputStreamReader(logcatProc.getInputStream()), 1024);
				String line = null;
				while (mRunning && (line = mReader.readLine()) != null) {
					if (!mRunning) {
						break;
					}
					if (line.length() == 0) {
						continue;
					}
					if (out != null && line.contains(mPID)) {
						out.write((line + "\n").getBytes());
					}
				}

			} catch (IOException e) {
				e.printStackTrace();
			} finally {
				if (logcatProc != null) {
					logcatProc.destroy();
					logcatProc = null;
				}
				if (mReader != null) {
					try {
						mReader.close();
						mReader = null;
					} catch (IOException e) {
						e.printStackTrace();
					}
				}
				if (out != null) {
					try {
						out.close();
					} catch (IOException e) {
						e.printStackTrace();
					}
					out = null;
				}

			}

		}

	}

	/** 删除文件 */
	private void deleteFile(File file) {
		if (file.exists()) { // 判断文件是否存在
			if (file.isFile()) { // 判断是否是文件
				file.delete();
			} else if (file.isDirectory()) { // 否则如果它是一个目录
				File files[] = file.listFiles(); // 声明目录下所有的文件 files[];
				for (File f : files) {// 遍历目录下所有的文件
					if (More7days(f.lastModified())) {// 时间超过7点的文件，删除
						this.deleteFile(f); // 把每个文件 用这个方法进行迭代
					}
				}
			}
		}
	}

	/** 与当前系统时间相差是否超过7天 */
	private boolean More7days(long time) {
		long diff = System.currentTimeMillis() - time;
		long days = diff / (1000 * 60 * 60 * 24);
		if (days >= 6) {
			return true;
		} else {
			return false;
		}
	}
}