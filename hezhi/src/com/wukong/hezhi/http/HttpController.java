package com.wukong.hezhi.http;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;
import java.util.Iterator;
import java.util.Map;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSession;
import javax.net.ssl.SSLSocketFactory;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;

/**
 * @author HuZhiyin
 * @ClassName: ${FILE_NAME}
 * @Description: ${todo}(这里用一句话描述这个类的作用)
 * @date 2017/12/27
 */


public class HttpController {
    public interface IOnResponseCallback<T> {
        void onSucess(T msg);

        void onFail(T msg);
    }

    /**
     * 获取指定URL的响应字符串
     */
    public static void get(final String urlString, final IOnResponseCallback iOnResponseCallback) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                HttpURLConnection conn = null; //连接对象
                InputStream is = null;
                String resultData = "";
                try {
                    URL url = new URL(urlString); //URL对象
                    conn = (HttpURLConnection) url.openConnection(); //使用URL打开一个链接
                    conn.setDoInput(true); //允许输入流，即允许下载
                    conn.setDoOutput(true); //允许输出流，即允许上传
                    conn.setUseCaches(false); //不使用缓冲
                    conn.setRequestMethod("GET"); //使用get请求
                    is = conn.getInputStream();   //获取输入流，此时才真正建立链接(此处getOutputStream会隐含的进行connect(即：如同调用上面的connect()方法，所以在开发中不调用上述的connect()也可以)。  )
                    InputStreamReader isr = new InputStreamReader(is);
                    BufferedReader bufferReader = new BufferedReader(isr);
                    String inputLine = "";
                    while ((inputLine = bufferReader.readLine()) != null) {
                        resultData += inputLine + "\n";
                    }
                    iOnResponseCallback.onSucess(resultData);
                } catch (MalformedURLException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                    iOnResponseCallback.onFail(e.toString());
                } catch (IOException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                    iOnResponseCallback.onFail(e.toString());
                } finally {
                    if (is != null) {
                        try {
                            is.close();
                        } catch (IOException e) {
                            // TODO Auto-generated catch block
                            e.printStackTrace();
                        }
                    }
                    if (conn != null) {
                        conn.disconnect();
                    }
                }
            }
        }).start();
    }

    public static void post(final String requestUrl, final Map requestParamsMap, final IOnResponseCallback iOnResponseCallback) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                BufferedWriter bufferedWriter = null;
                BufferedReader bufferedReader = null;
                StringBuffer responseResult = new StringBuffer();
                StringBuffer params = new StringBuffer();
                HttpsURLConnection httpURLConnection = null;
                // 组织请求参数
                Iterator it = requestParamsMap.entrySet().iterator();
                while (it.hasNext()) {
                    Map.Entry element = (Map.Entry) it.next();
                    params.append(element.getKey());
                    params.append("=");
                    params.append(element.getValue());
                    params.append("&");
                }
                if (params.length() > 0) {
                    params.deleteCharAt(params.length() - 1);
                }

                try {
                    URL realUrl = new URL(requestUrl);
                    // 打开和URL之间的连接
                    httpURLConnection = (HttpsURLConnection) realUrl.openConnection();
                    httpsSet(httpURLConnection);
                    // 设置通用的请求属性
                    httpURLConnection.setRequestProperty("accept", "*/*");
                    httpURLConnection.setRequestProperty("connection", "Keep-Alive");
                    httpURLConnection.setRequestProperty("Content-Length", String
                            .valueOf(params.length()));
                    httpURLConnection.setRequestProperty("Content-type", "application/json");
                    // 设定请求的方法为"POST"，默认是GET
                    httpURLConnection.setRequestMethod("POST");
                    // 发送POST请求必须设置如下两行
                    httpURLConnection.setDoOutput(true);
                    httpURLConnection.setDoInput(true);
                    // 获取URLConnection对象对应的输出流
                    bufferedWriter = new BufferedWriter(new OutputStreamWriter(httpURLConnection.getOutputStream()));
                    // 发送请求参数
                    bufferedWriter.write(params.toString());
                    // flush输出流的缓冲
                    bufferedWriter.flush();
                    // 定义BufferedReader输入流来读取URL的ResponseData
                    bufferedReader = new BufferedReader(new InputStreamReader(
                            httpURLConnection.getInputStream()));
                    String line;
                    while ((line = bufferedReader.readLine()) != null) {
                        responseResult.append("/n").append(line);
                    }
                    iOnResponseCallback.onSucess(responseResult);
                } catch (Exception e) {
                    iOnResponseCallback.onFail(e.toString());
                } finally {
                    httpURLConnection.disconnect();
                    try {
                        if (bufferedWriter != null) {
                            bufferedWriter.close();
                        }
                        if (bufferedReader != null) {
                            bufferedReader.close();
                        }
                    } catch (IOException ex) {
                        ex.printStackTrace();
                    }

                }
            }
        }).start();
    }

    private static  void httpsSet(HttpsURLConnection https) {
        SSLSocketFactory oldSocketFactory = null;
        HostnameVerifier oldHostnameVerifier = null;
        oldSocketFactory = trustAllHosts(https);
        oldHostnameVerifier = https.getHostnameVerifier();
        https.setHostnameVerifier(DO_NOT_VERIFY);
    }

    /**
     * 覆盖java默认的证书验证
     */
    private static final TrustManager[] trustAllCerts = new TrustManager[]{new X509TrustManager() {
        @Override
        public void checkClientTrusted(X509Certificate[] chain, String authType) throws CertificateException {

        }

        @Override
        public void checkServerTrusted(X509Certificate[] chain, String authType) throws CertificateException {

        }

        @Override
        public X509Certificate[] getAcceptedIssuers() {
//            return new X509Certificate[0];
            return new java.security.cert.X509Certificate[]{};
        }
    }};

    /**
     * 设置不验证主机
     */
    private static final HostnameVerifier DO_NOT_VERIFY = new HostnameVerifier() {
        public boolean verify(String hostname, SSLSession session) {
            return true;
        }
    };

    /**
     * 信任所有
     *
     * @param connection
     * @return
     */
    private static SSLSocketFactory trustAllHosts(HttpsURLConnection connection) {
        SSLSocketFactory oldFactory = connection.getSSLSocketFactory();
        try {
            SSLContext sc = SSLContext.getInstance("TLS");
            sc.init(null, trustAllCerts, new java.security.SecureRandom());
            SSLSocketFactory newFactory = sc.getSocketFactory();
            connection.setSSLSocketFactory(newFactory);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return oldFactory;
    }

}
