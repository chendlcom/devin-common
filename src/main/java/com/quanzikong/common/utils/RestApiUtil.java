package com.quanzikong.common.utils;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.Proxy;
import java.net.URL;
import java.net.URLConnection;
import java.security.SecureRandom;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.KeyManager;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSession;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;
import javax.servlet.http.HttpServletRequest;

import com.alibaba.fastjson.JSONObject;

import com.google.common.io.ByteStreams;
import com.quanzikong.common.enums.HttpContentType;
import com.quanzikong.common.enums.HttpMethod;
import com.quanzikong.common.enums.HttpProperty;
import com.quanzikong.common.enums.HttpProtocol;

/**
 * RestApiUtil
 *
 * @since 2018-06-25 17:31
 */
public class RestApiUtil {

    private static final String EOF = "\r\n";
    private static final String TWO_HYPHENS = "--";
    private static final String FIVE_STARS = "*****";
    private static final String AND_CHAR = "&";
    private static final String EUQAL_CHAR = "=";
    private static final String DEFAULT_CHARSET = "utf8";
    private static final String CONTENT_DISPOSITION = "Content-Disposition: form-data; name=\"%s\"";
    private static final String CONTENT_DISPOSITION_FILE = "Content-Disposition: form-data; name=\"%s\"; filename=\"%s\"";
    private static final String CONTENT_TYPE_TEXT_PLAIN = "Content-Type: text/plain";
    private static final String CONTENT_TRANSFER_ENCODING_BINARY = "Content-Transfer-Encoding: binary";
    private static final String MULTIPART_FORM_DATA_BOUNDARY = "multipart/form-data; boundary=";
    private static final int MAX_BUFFER_SIZE = 1048576;
    private static final int ZERO = 0;

    private int connectTimeout = 70000;
    private int readTimeout = 70000;
    private String url;
    private String charset = "utf8";
    private String userAgent = "Mozilla/5.0 (Windows; U; Windows NT 6.1; zh-CN; rv:1.9.2.6)";
    private HttpMethod method = HttpMethod.GET;
    private HttpContentType contentType = HttpContentType.DEFAULT;
    private Map<String, String> headerFields = new HashMap(16);
    private Map<String, String> requestProperties = new HashMap(16);
    private Map<String, Object> commonParams = new HashMap(16);
    private Map<String, File> fileParts = new HashMap(16);
    private Object postPojo = null;
    private HttpURLConnection httpConn;
    private boolean doInput = true;
    private boolean doOutput = true;
    private boolean userCache = false;
    private boolean instanceFollowRedirects = false;

    private Proxy proxy = null;
    private SslProvider sslProvider = new SslProvider();

    private static RestApiUtil restApiUtil;

    /**
     * constructor
     *
     * @param protocol HttpProtocol
     * @param uri      String
     * @param charset  String
     */
    private RestApiUtil(HttpProtocol protocol, String uri, String charset) {
        String fUrl = uri.trim();
        boolean hasProtocol = fUrl.toLowerCase().startsWith(HttpProtocol.http.getCode());

        this.charset = charset;
        this.url = hasProtocol ? fUrl : protocol.getCode() + "://" + fUrl;

        this.requestProperties.put(HttpProperty.UserAgent.getCode(), this.userAgent);
    }

    /**
     * get restApiUtil
     *
     * @param uri String
     *
     * @return RestApiUtil
     */
    public static RestApiUtil newInstance(String uri) {
        if (uri.trim().toLowerCase().startsWith(HttpProtocol.http.getCode())) {
            restApiUtil = new RestApiUtil(HttpProtocol.http, uri, DEFAULT_CHARSET);
        } else {
            restApiUtil = new RestApiUtil(HttpProtocol.https, uri, DEFAULT_CHARSET);
        }

        return restApiUtil;
    }

    /**
     * get restApiUtil
     *
     * @param protocol HttpProtocol
     * @param uri      String
     *
     * @return RestApiUtil
     */
    public static RestApiUtil newInstance(HttpProtocol protocol, String uri) {
        restApiUtil = new RestApiUtil(protocol, uri, DEFAULT_CHARSET);
        return restApiUtil;
    }

    /**
     * get restApiUtil
     *
     * @param protocol HttpProtocol
     * @param uri      String
     * @param charset  String
     *
     * @return RestApiUtil
     */
    public static RestApiUtil newInstance(HttpProtocol protocol, String uri, String charset) {
        restApiUtil = new RestApiUtil(protocol, uri, charset);
        return restApiUtil;
    }

    /**
     * 从HttpServletRequest获取byte[]
     *
     * @param request HttpServletRequest
     *
     * @return T 目标对象实例
     *
     * @throws Exception e
     */
    public static byte[] getRequestBytes(HttpServletRequest request) throws Exception {
        request.setCharacterEncoding(DEFAULT_CHARSET);
        int contentLen = request.getContentLength();
        InputStream is = request.getInputStream();
        if (contentLen > 0) {
            int readLen = 0;
            int readLengthThisTime = 0;
            byte[] message = new byte[contentLen];
            while (readLen != contentLen) {
                readLengthThisTime = is.read(message, readLen, contentLen - readLen);
                if (readLengthThisTime == -1) {
                    break;
                }
                readLen += readLengthThisTime;
            }
            return message;
        }
        return new byte[] {};
    }

    /**
     * 类get请求
     * 可指定method的get的请求
     *
     * @param method HttpMethod
     *
     * @return GetProxy
     */
    public GetProxy asGet(HttpMethod method) {
        this.method = method;
        return new GetProxy();
    }

    /**
     * 类get请求
     * 默认HttpMethod.GET的get请求
     *
     * @return
     */
    public GetProxy asGet() {
        return asGet(HttpMethod.GET);
    }

    /**
     * 类post请求代理类, 可指定method的post请求<br/>
     * - 更多请求见<code>com.quanzikong.common.enums.HttpMethod</code>
     *
     * @param method HttpMethod
     *
     * @return PostProxy
     */
    public PostProxy asPost(HttpMethod method) {
        this.method = method;
        return new PostProxy();
    }

    /**
     * 类post请求代理类<br/>
     * - 默认以HttpMethod.POST的post请求<br/>
     * - 指定其他method请使用：public PostProxy asPost(HttpMethod method)
     *
     * @return PostProxy
     */
    public PostProxy asPost() {
        return asPost(HttpMethod.POST);
    }

    // --------------------------------- setters & getters ----------------------------------

    /**
     * setter of readTimeout
     *
     * @param readTimeout int
     *
     * @return RestApiUtil
     */
    public RestApiUtil setReadTimeout(int readTimeout) {
        this.readTimeout = readTimeout;
        return this;
    }

    /**
     * enableHttps
     *
     * @param sslProvider SslProvider
     *
     * @return RestApiUtil
     */
    public RestApiUtil setSslProvider(SslProvider sslProvider) {
        this.sslProvider = sslProvider;
        return this;
    }

    /**
     * setter of proxy<br/>
     * - e.g: new Proxy(Proxy.Type.HTTP, new InetSocketAddress("web-proxy.ind.abc.com", 8080))
     *
     * @param proxy java.net.Proxy
     *
     * @return RestApiUtil
     */
    public RestApiUtil setProxy(Proxy proxy) {
        this.proxy = proxy;
        return this;
    }

    /**
     * setter of doInput
     *
     * @param doInput boolean
     *
     * @return RestApiUtil
     */
    public RestApiUtil setDoInput(boolean doInput) {
        this.doInput = doInput;
        return this;
    }

    /**
     * setter of doOutput
     *
     * @param doOutput boolean
     *
     * @return RestApiUtil
     */
    public RestApiUtil setDoOutput(boolean doOutput) {
        this.doOutput = doOutput;
        return this;
    }

    /**
     * setter of userCache
     *
     * @param userCache boolean
     *
     * @return RestApiUtil
     */
    public RestApiUtil setUserCache(boolean userCache) {
        this.userCache = userCache;
        return this;
    }

    /**
     * setter of instanceFollowRedirects
     *
     * @param instanceFollowRedirects boolean
     *
     * @return RestApiUtil
     */
    public RestApiUtil setInstanceFollowRedirects(boolean instanceFollowRedirects) {
        this.instanceFollowRedirects = instanceFollowRedirects;
        return this;
    }

    /**
     * setter of userAgent
     *
     * @param userAgent String
     */
    public void setUserAgent(String userAgent) {
        this.userAgent = userAgent;
    }

    /**
     * setter of connectTimeout
     *
     * @param connectTimeout int
     */
    public void setConnectTimeout(int connectTimeout) {
        this.connectTimeout = connectTimeout;
    }

    /**
     * 根据HttpProperty给定的key，添加requestProperty
     *
     * @param property HttpProperty
     * @param value    String
     *
     * @return RestApiUtil
     */
    public RestApiUtil addRequestProperty(HttpProperty property, String value) {
        this.requestProperties.put(property.getCode(), value);
        return this;
    }

    /**
     * 根据自定义的property name，添加requestProperty
     * * 优先使用public  RestApiUtil addRequestProperty(HttpProperty property, String value)
     *
     * @param property String
     * @param value    String
     *
     * @return RestApiUtil
     */
    public RestApiUtil addRequestProperty(String property, String value) {
        this.requestProperties.put(property, value);
        return this;
    }

    /**
     * 根据自定义的name，添加headerField
     *
     * @param name  String
     * @param value String
     *
     * @return RestApiUtil
     */
    public RestApiUtil addHeaderField(String name, String value) {
        this.headerFields.put(name, value);
        return this;
    }

    // --------------------------------- private ----------------------------------

    private void postApplicationJson() throws Exception {
        String postString = "";
        if (this.postPojo instanceof Collection || this.postPojo instanceof Object[]) {
            postString = JSONObject.toJSONString(this.postPojo);
        } else {
            JSONObject o = new JSONObject();
            if (null != this.postPojo) {
                o = (JSONObject)JSONObject.toJSON(this.postPojo);
            }
            this.appendCommonParamsToJson(o);
            postString = o.toJSONString();
        }

        OutputStream outputStream = httpConn.getOutputStream();
        OutputStreamWriter out = new OutputStreamWriter(outputStream, this.charset);
        out.write(postString);
        out.flush();
        out.close();
    }

    private void postApplicationOctetStream() throws Exception {
        OutputStream out = httpConn.getOutputStream();
        out.write((byte[])this.postPojo);
        out.flush();
        out.close();
    }

    private void postMultipartFormData() throws Exception {
        String boundary = FIVE_STARS + System.currentTimeMillis() + FIVE_STARS;
        httpConn.setRequestProperty(HttpProperty.ContentType.getCode(), MULTIPART_FORM_DATA_BOUNDARY + boundary);
        DataOutputStream writer = new DataOutputStream(httpConn.getOutputStream());

        // send files
        this.sendFiles(writer, boundary);

        // send common params
        this.sendParams(writer, boundary);

        writer.writeBytes(TWO_HYPHENS + boundary + TWO_HYPHENS + EOF);
        writer.flush();
        writer.close();
    }

    private void postSimpleData() throws Exception {
        OutputStream outputStream = httpConn.getOutputStream();
        OutputStreamWriter out = new OutputStreamWriter(outputStream, this.charset);
        out.write(getUrlParams());
        out.flush();
        out.close();
    }

    private void sendParams(DataOutputStream writer, String boundary) throws Exception {
        if (this.commonParams.isEmpty()) {
            return;
        }

        Iterator<Entry<String, Object>> formfieldsIt = this.commonParams.entrySet().iterator();
        Entry<String, Object> formfieldsEntry;
        while (formfieldsIt.hasNext()) {
            formfieldsEntry = formfieldsIt.next();
            String name = formfieldsEntry.getKey();
            String value = formfieldsEntry.getValue() + StringUtil.EMPTY_STRING;

            writer.writeBytes(TWO_HYPHENS + boundary + EOF);
            writer.writeBytes(String.format(CONTENT_DISPOSITION, name) + EOF);
            writer.writeBytes(CONTENT_TYPE_TEXT_PLAIN + EOF);
            writer.writeBytes(EOF);
            writer.writeBytes(value);
            writer.writeBytes(EOF);
        }
    }

    private void sendFiles(DataOutputStream wirter, String boundary) throws Exception {
        if (this.fileParts.isEmpty()) {
            return;
        }

        Iterator<Entry<String, File>> iterator = this.fileParts.entrySet().iterator();
        Entry<String, File> fileEntry;
        while (iterator.hasNext()) {
            fileEntry = iterator.next();
            String fn = fileEntry.getKey();
            File file = fileEntry.getValue();

            wirter.writeBytes(TWO_HYPHENS + boundary + EOF);
            wirter.writeBytes(String.format(CONTENT_DISPOSITION_FILE, fn, fn) + EOF);
            wirter.writeBytes(CONTENT_TRANSFER_ENCODING_BINARY + EOF);
            wirter.writeBytes(EOF);

            FileInputStream fileInputStream = new FileInputStream(file);
            int bytesAvailable = fileInputStream.available();
            int bufferSize = Math.min(bytesAvailable, MAX_BUFFER_SIZE);
            byte[] buffer = new byte[bufferSize];
            int bytesRead = fileInputStream.read(buffer, ZERO, bufferSize);
            while (bytesRead > ZERO) {
                wirter.write(buffer, ZERO, bufferSize);
                bytesAvailable = fileInputStream.available();
                bufferSize = Math.min(bytesAvailable, MAX_BUFFER_SIZE);
                bytesRead = fileInputStream.read(buffer, ZERO, bufferSize);
            }
            wirter.writeBytes(EOF);
            fileInputStream.close();
        }
    }

    private void appendCommonParamsToJson(JSONObject o) {
        if (this.commonParams.isEmpty()) {
            return;
        }

        Iterator<Entry<String, Object>> it = this.commonParams.entrySet().iterator();
        Entry<String, Object> entry;
        String key;
        Object val;
        while (it.hasNext()) {
            entry = it.next();
            key = entry.getKey();
            val = entry.getValue();
            o.put(key, val);
        }
    }

    /**
     * 将map转换成url参数，仅支持GET.
     * 1, 忽略文件类型的参数
     *
     * @return 拼好的URL参数key1=value1&key2=value2的形式
     */
    private String getUrlParams() {
        if (this.commonParams.isEmpty()) {
            return "";
        }
        Object val;

        StringBuffer sb = new StringBuffer();
        for (Entry<String, Object> entry : this.commonParams.entrySet()) {
            val = entry.getValue();
            // 忽略文件类型的参数
            if (val instanceof File) {
                continue;
            }
            sb.append(entry.getKey() + EUQAL_CHAR + val);
            sb.append(AND_CHAR);
        }
        String s = sb.toString();
        if (s.endsWith(AND_CHAR)) {
            s = StringUtil.substringBeforeLast(s, AND_CHAR);
        }

        return s;
    }

    /**
     * initHttpConn
     *
     * @param url String
     *
     * @throws Exception e
     */
    private void initHttpConn(String url, HttpMethod method) throws Exception {
        boolean isHttps = url.toLowerCase().startsWith(HttpProtocol.https.getCode());

        if (isHttps) {
            this.initSSLProtocol();
        }

        URL uri = new URL(url);
        URLConnection urlConnection = null == this.proxy ? uri.openConnection() : uri.openConnection(this.proxy);

        httpConn = isHttps ? (HttpsURLConnection)urlConnection : (HttpURLConnection)urlConnection;
        httpConn.setRequestMethod(method.getCode());
        httpConn.setDoInput(this.doInput);
        httpConn.setDoOutput(this.doOutput);
        httpConn.setUseCaches(this.userCache);
        httpConn.setInstanceFollowRedirects(this.instanceFollowRedirects);
        httpConn.setConnectTimeout(this.connectTimeout);
        httpConn.setReadTimeout(this.readTimeout);
        // set request properties
        if (!this.requestProperties.isEmpty()) {
            Iterator<Entry<String, String>> propertiesIt = this.requestProperties.entrySet().iterator();
            Entry<String, String> entry;
            while (propertiesIt.hasNext()) {
                entry = propertiesIt.next();
                if (StringUtil.isNotBlank(entry.getValue())) {
                    httpConn.setRequestProperty(entry.getKey(), entry.getValue());
                }
            }
        }

        // set header fields
        if (!this.headerFields.isEmpty()) {
            Iterator<Entry<String, String>> propertiesIt = this.headerFields.entrySet().iterator();
            Entry<String, String> entry;
            while (propertiesIt.hasNext()) {
                entry = propertiesIt.next();
                if (StringUtil.isNotBlank(entry.getValue())) {
                    httpConn.setRequestProperty(entry.getKey(), entry.getValue());
                }
            }
        }
    }

    /**
     * initSSLProtocol
     */
    private void initSSLProtocol() throws Exception {
        HttpsURLConnection.setDefaultHostnameVerifier(sslProvider.getDefalutHostnameVerifier());
        SSLContext sc = SSLContext.getInstance(sslProvider.getSSLContextAlgorithm());
        sc.init(sslProvider.getKeyManagers(), sslProvider.getTrustAllCerts(), sslProvider.getSecureRandom());
        HttpsURLConnection.setDefaultSSLSocketFactory(sc.getSocketFactory());
    }

    // ------------------------------------ inner classes -------------------------

    /**
     * http请求基类
     */
    class BaseProxy {
        /**
         * 得到请求结果的InputStream
         *
         * @return InputStream
         *
         * @throws Exception e
         */
        public InputStream getResponseStream() throws Exception {
            this.send();

            InputStream ins = this.getResponseInputStream();

            httpConn.disconnect();

            return ins;
        }

        /**
         * 得到请求结果的byte[]
         *
         * @return byte[]
         *
         * @throws Exception e
         */
        public byte[] getResponseBytes() throws Exception {
            this.send();

            InputStream ins = this.getResponseInputStream();

            byte[] bytes = ByteStreams.toByteArray(ins);

            httpConn.disconnect();

            return bytes;
        }

        /**
         * 得到请求结果的String
         *
         * @return String
         *
         * @throws Exception e
         */
        public String getResponseString() throws Exception {
            this.send();

            InputStream ins = this.getResponseInputStream();

            StringBuffer result = new StringBuffer();
            InputStreamReader isr = new InputStreamReader(ins, charset);
            BufferedReader in = new BufferedReader(isr);
            String inputLine;
            while ((inputLine = in.readLine()) != null) {
                result.append(inputLine);
            }

            httpConn.disconnect();

            return result.toString();
        }

        /**
         * getResponseInputStream
         *
         * @return InputStream
         *
         * @throws Exception e
         */
        private InputStream getResponseInputStream() throws Exception {
            if (null == httpConn) {
                throw new Exception("Please call send() first!!");
            }

            InputStream ins = null;
            // checks server's status code first
            int resCode = httpConn.getResponseCode();
            boolean isSrccess = HttpURLConnection.HTTP_OK == resCode
                || HttpURLConnection.HTTP_CREATED == resCode
                || HttpURLConnection.HTTP_ACCEPTED == resCode;
            if (isSrccess) {
                ins = httpConn.getInputStream();
            } else {
                ins = httpConn.getErrorStream();
            }

            return ins;
        }

        /**
         * asPost
         *
         * @return RestApiUtil
         *
         * @throws Exception e
         */
        private void send() throws Exception {
            if (HttpMethod.GET == method) {
                initHttpConn(url + "?" + getUrlParams(), method);
                return;
            }

            initHttpConn(url, method);
            switch (contentType) {
                case APPLICATION_JSON:
                    postApplicationJson();
                    break;
                case APPLICATION_OCTET_STREAM:
                    postApplicationOctetStream();
                    break;
                case MULTIPART_FORM_DATA:
                    postMultipartFormData();
                    break;
                default:
                    postSimpleData();
            }
        }
    }

    /**
     * get请求代理
     */
    public class GetProxy extends BaseProxy {
        /**
         * 添加参数
         *
         * @param key String
         * @param val Object
         *
         * @return RestApiUtil
         */
        public GetProxy addParam(String key, Object val) {
            commonParams.put(key, val);
            return this;
        }

        /**
         * 批量添加参数（以追加的形式，有重复将会替换之前的参数值）
         *
         * @param params Map
         *
         * @return RestApiUtil
         */
        public GetProxy addParams(Map<String, Object> params) {
            if (null != params && !params.isEmpty()) {
                commonParams.putAll(params);
            }
            return this;
        }

    }

    /**
     * post请求代理
     */
    public class PostProxy extends BaseProxy {
        /**
         * 添加需要发送的文件
         *
         * @param name 文件名
         * @param val  文件对象
         *
         * @return RestApiUtil
         */
        public PostProxy addFilePart(String name, File val) {
            restApiUtil.fileParts.put(name, val);
            return this;
        }

        /**
         * 设置发送的对象。主要用于Content-Type为application/json发送
         *
         * @param postPojo java.lang.Object
         *
         * @return RestApiUtil
         */
        public PostProxy setPostPojo(Object postPojo) {
            restApiUtil.postPojo = postPojo;
            return this;
        }

        /**
         * setter of contentType
         *
         * @param tContentType HttpContentType
         *
         * @return RestApiUtil
         */
        public PostProxy setContentType(HttpContentType tContentType) {
            if (null == tContentType) {
                return this;
            }

            contentType = tContentType;
            requestProperties.put(HttpProperty.ContentType.getCode(), tContentType.getCode());
            return this;
        }

        /**
         * 添加参数
         *
         * @param key String
         * @param val Object
         *
         * @return RestApiUtil
         */
        public PostProxy addParam(String key, Object val) {
            commonParams.put(key, val);
            return this;
        }

        /**
         * 批量添加参数（以追加的形式，有重复将会替换之前的参数值）
         *
         * @param params Map
         *
         * @return RestApiUtil
         */
        public PostProxy addParams(Map<String, Object> params) {
            if (null != params && !params.isEmpty()) {
                commonParams.putAll(params);
            }
            return this;
        }
    }

    /**
     * SSL协议配置提供类
     *
     * @author Devin on 2018-06-27 22:59.
     */
    public static class SslProvider {

        private HostnameVerifier defalutHostnameVerifier = new HostnameVerifier() {
            @Override
            public boolean verify(String s, SSLSession sslSession) {
                return true;
            }
        };

        private TrustManager[] trustAllCerts = new TrustManager[] {
            new X509TrustManager() {
                public void checkClientTrusted(X509Certificate[] chain, String authType)
                    throws CertificateException {
                }

                public void checkServerTrusted(X509Certificate[] chain, String authType)
                    throws CertificateException {
                }

                public X509Certificate[] getAcceptedIssuers() {
                    return null;
                }
            }
        };

        private KeyManager[] keyManagers = null;

        private SecureRandom secureRandom = new SecureRandom();

        private String SSLContextAlgorithm = "TLS";

        /**
         * getter of defalutHostnameVerifier
         *
         * @return javax.net.ssl.HostnameVerifier
         */
        public HostnameVerifier getDefalutHostnameVerifier() {
            return defalutHostnameVerifier;
        }

        /**
         * setter of defalutHostnameVerifier
         *
         * @param defalutHostnameVerifier javax.net.ssl.HostnameVerifier
         */
        public void setDefalutHostnameVerifier(HostnameVerifier defalutHostnameVerifier) {
            this.defalutHostnameVerifier = defalutHostnameVerifier;
        }

        /**
         * getter of trustAllCerts
         *
         * @return javax.net.ssl.TrustManager[]
         */
        public TrustManager[] getTrustAllCerts() {
            return trustAllCerts;
        }

        /**
         * setter of trustAllCerts
         *
         * @param trustAllCerts javax.net.ssl.TrustManager[]
         */
        public void setTrustAllCerts(TrustManager[] trustAllCerts) {
            this.trustAllCerts = trustAllCerts;
        }

        /**
         * getter of keyManagers
         *
         * @return javax.net.ssl.KeyManager[]
         */
        public KeyManager[] getKeyManagers() {
            return keyManagers;
        }

        /**
         * setter of keyManagers
         *
         * @param keyManagers javax.net.ssl.KeyManager[]
         */
        public void setKeyManagers(KeyManager[] keyManagers) {
            this.keyManagers = keyManagers;
        }

        /**
         * getter of secureRandom
         *
         * @return java.security.SecureRandom
         */
        public SecureRandom getSecureRandom() {
            return secureRandom;
        }

        /**
         * setter of secureRandom
         *
         * @param secureRandom java.security.SecureRandom
         */
        public void setSecureRandom(SecureRandom secureRandom) {
            this.secureRandom = secureRandom;
        }

        /**
         * getter of SSLContextAlgorithm
         *
         * @return java.lang.String
         */
        public String getSSLContextAlgorithm() {
            return SSLContextAlgorithm;
        }

        /**
         * setter of SSLContextAlgorithm
         *
         * @param SSLContextAlgorithm java.lang.String
         */
        public void setSSLContextAlgorithm(String SSLContextAlgorithm) {
            this.SSLContextAlgorithm = SSLContextAlgorithm;
        }
    }
}