package hu.d2.offsiteinc.util;



import android.util.Base64;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Authenticator;
import java.net.HttpURLConnection;
import java.net.PasswordAuthentication;
import java.net.URL;
import java.security.KeyStore;
import java.security.cert.Certificate;
import java.security.cert.CertificateFactory;
import java.security.cert.X509Certificate;


import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSession;
import javax.net.ssl.TrustManagerFactory;

import hu.d2.offsiteinc.ui.view.login.LoginActivity;


public class NetworkTool {

    private static String SERVER_IP_ADDRESS = "mobil-icd.invitech.hu";
    private static int CONNECTION_TIME_OUT = 5000;
    private static int READ_TIME_OUT = 5000;

    public static String username;
    public static String password;

    public static String LOGIN_URL ="https://"+SERVER_IP_ADDRESS+"/maxrest/rest/login";
    public static String SOAP_INC_URL_GET ="https://"+SERVER_IP_ADDRESS+"/meaweb/services/D2OFFSITE_MOB_INC1";
    public static String SOAP_INC_URL_UPDATE ="https://"+SERVER_IP_ADDRESS+"/meaweb/services/D2OFFSITE_MOB_INC2";
    public static String SOAP_OWNER_URL ="https://"+SERVER_IP_ADDRESS+"/meaweb/services/D2OFFSITE_MOB_OWNER";
    public static String SOAP_DOC_URL_GET = "https://"+SERVER_IP_ADDRESS+"/meaweb/services/D2OFFSITE_MOB_DOC2_INC";
    public static String SOAP_WL_URL_GET = "https://"+SERVER_IP_ADDRESS+"/meaweb/services/D2OFFSITE_MOB_WL1";
    public static String SOAP_ATTACHMENT_URL_GET ="https://"+SERVER_IP_ADDRESS+"/meaweb/services/D2OFFSITE_MOB_DOC3_INC";
    public static String SOAP_LICENSE_URL_GET ="https://"+SERVER_IP_ADDRESS+"/meaweb/services/D2OFFSITE_mob_lic";

    public static String SOAP_APP_VERSION_GET = "https://91.82.81.106/meaweb/services/D2OFFSITE_MOB_UPDATE";
    public static String SOAP_NEW_APP_GET = "https://91.82.81.106/meaweb/services/D2OFFSITE_MOB_UPDATE2";

    public static HttpURLConnection createConnection(String url,String user, String pw) throws IOException {
        return createConnection(url,null,null,false,true,user,pw);
    }

    public static HttpURLConnection createSOAPConnection(String url, String soapAction,String soapPayload) throws IOException {
        return createConnection(url, soapAction,soapPayload, true,true,username,password);
    }

    public static HttpURLConnection createSOAPGETConnection(String url, String soapAction, String soapPayload) throws IOException {
        return createConnection(url, soapAction, soapPayload, true,false,username,password);
    }


   private static synchronized HttpURLConnection createConnection(String SOAP_URL, String SOAP_ACTION, String SOAP_PAYLOAD, boolean isSoap, boolean isPost,String user, String pw) throws IOException {

       username=user;
       password =pw;

        Authenticator.setDefault(new Authenticator() {
            @Override
            protected PasswordAuthentication getPasswordAuthentication() {

                if (SOAP_URL.equals(SOAP_APP_VERSION_GET) || SOAP_URL.equals(SOAP_NEW_APP_GET)) {
                    return new PasswordAuthentication("d2", "12345678".toCharArray());
                }
                return new PasswordAuthentication(username, password.toCharArray());
            }
            });

        HttpURLConnection connection = getHttpsConnection(SOAP_URL);
       String credentials = username + ":" + password;

        connection.setDoOutput(true);
        connection.setInstanceFollowRedirects(false);
        connection.setRequestMethod(isPost?"POST":"GET");

        connection.setDoInput(true);
        connection.setConnectTimeout(CONNECTION_TIME_OUT);
        connection.setReadTimeout(READ_TIME_OUT);

        if (isSoap){
            connection.setRequestProperty("Content-Type", "text/xml");
            connection.setRequestProperty("charset", "utf-8");
            connection.setRequestProperty("SOAPAction", SOAP_ACTION);
            connection.setRequestProperty("Authorization","basic "+ android.util.Base64.encodeToString(credentials.getBytes(), Base64.DEFAULT));
            OutputStream outputStream = connection.getOutputStream();
            outputStream.write(SOAP_PAYLOAD.getBytes());

        } else {
            connection.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
            connection.setRequestProperty("Authorization","basic "+ android.util.Base64.encodeToString(credentials.getBytes(), Base64.DEFAULT));

        }

        return connection;

    }

    public static HttpsURLConnection getHttpsConnection(String urlString) {
        InputStream caInput;

       try {
           // Load CAs from  an InputStream
           CertificateFactory certificateFactory = CertificateFactory.getInstance("X.509","BC"); //BouncyCastle Provider

           // My CER file that I put in the assets folder
           if (urlString.equals(SOAP_APP_VERSION_GET) || urlString.equals(SOAP_NEW_APP_GET)){
               caInput = new BufferedInputStream(LoginActivity.mContext.getAssets().open("1.cer"));
           }else{
               caInput = new BufferedInputStream(LoginActivity.mContext.getAssets().open("der.cer"));
           }

           Certificate ca;
           try{
               ca = certificateFactory.generateCertificate(caInput);
           }finally {
               caInput.close();
           }

           System.out.println("certificate =" + ((X509Certificate) ca).getSubjectDN());

           //create a KeyStore containing our trusted CAs
            String keyStoreType = KeyStore.getDefaultType();
            KeyStore keyStore = KeyStore.getInstance(keyStoreType);
            keyStore.load(null, null);
            keyStore.setCertificateEntry("ca", ca);

            //  create a TrustManager that trusts the CAs in out keyStore
            String tmfAlgorithm = TrustManagerFactory.getDefaultAlgorithm();
            TrustManagerFactory tmf = TrustManagerFactory.getInstance(tmfAlgorithm);
           tmf.init(keyStore);

            // create an SSLContext that uses out TrustManager
            SSLContext sslcontext = SSLContext.getInstance("TLS");
            sslcontext.init(null, tmf.getTrustManagers(), null);

            // tell the URLConnection to use a SocketFactory from our SSLContext
            URL serviceUrl = new URL(urlString);
            HttpsURLConnection urlConnection = (HttpsURLConnection) serviceUrl.openConnection();
            urlConnection.setSSLSocketFactory(sslcontext.getSocketFactory());
            urlConnection.setHostnameVerifier(new HostnameVerifier() {
                @Override
                public boolean verify(String s, SSLSession sslSession) {
                    return true;
                }
            });

            return urlConnection;

       }catch (Exception e) {
           e.getMessage();
           return null;
       }



    }
}
