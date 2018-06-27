package com.quanzikong.common.providers;

import java.security.SecureRandom;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.KeyManager;
import javax.net.ssl.SSLSession;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;

/**
 * SSL协议配置提供类
 *
 * @author Devin on 2018-06-27 22:59.
 */
public class SSLProvider {

    private HostnameVerifier defalutHostnameVerifier = (String s, SSLSession sslSession) -> {return true;};

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
