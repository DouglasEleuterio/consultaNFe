package br.com.consultanfe.util;

import org.apache.commons.httpclient.ConnectTimeoutException;
import org.apache.commons.httpclient.params.HttpConnectionParams;
import org.apache.commons.httpclient.protocol.ProtocolSocketFactory;

import javax.net.ssl.*;
import java.io.IOException;
import java.io.InputStream;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.net.UnknownHostException;
import java.security.*;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;

public class SocketFactoryDinamico implements ProtocolSocketFactory {

    private SSLContext ssl;
    private X509Certificate certificate;
    private PrivateKey privateKey;
    private InputStream fileCacerts;

    public SocketFactoryDinamico(X509Certificate certificate, PrivateKey privateKey,
                                 InputStream fileCacerts, String sslProtocol) throws
        KeyManagementException, CertificateException, NoSuchAlgorithmException, KeyStoreException, IOException {
        this.certificate = certificate;
        this.privateKey = privateKey;
        this.fileCacerts = fileCacerts;
        this.ssl = this.createSSLContext(sslProtocol);
    }

    @Override
    public Socket createSocket(String host, int port, InetAddress localAddress, int localPort, HttpConnectionParams params)
        throws IOException, UnknownHostException, ConnectTimeoutException {
        Socket socket = this.ssl.getSocketFactory().createSocket();
        socket.bind(new InetSocketAddress(localAddress, localPort));
        socket.connect(new InetSocketAddress(host, port), 60000);
        return socket;
    }

    @Override
    public Socket createSocket(String host, int port, InetAddress clientHost, int clientPort) throws IOException {
        return this.ssl.getSocketFactory().createSocket(host, port, clientHost, clientPort);
    }

    public Socket createSocket(String host, int port) throws IOException {
        return this.ssl.getSocketFactory().createSocket(host, port);
    }

    private SSLContext createSSLContext(String sslProtocol) throws CertificateException,
        NoSuchAlgorithmException, KeyStoreException, IOException, KeyManagementException {
        KeyManager[] keyManagers = this.createKeyManagers();
        TrustManager[] trustManagers = this.createTrustManagers();
        SSLContext sslContext = SSLContext.getInstance("TLS");
        sslContext.init(keyManagers, trustManagers, null);
        return sslContext;
    }

    private KeyManager[] createKeyManagers() {
        return new KeyManager[]{new SocketFactoryDinamico.NFKeyManager(this.certificate, this.privateKey)};
    }

    private TrustManager[] createTrustManagers() throws KeyStoreException, NoSuchAlgorithmException,
        CertificateException, IOException {
        TrustManagerFactory trustManagerFactory = TrustManagerFactory.getInstance(TrustManagerFactory.getDefaultAlgorithm());
        KeyStore trustStore = KeyStore.getInstance("JKS");
        trustStore.load(this.fileCacerts, "changeit".toCharArray());
        trustManagerFactory.init(trustStore);
        return trustManagerFactory.getTrustManagers();
    }

    private class NFKeyManager implements X509KeyManager {
        private final X509Certificate certificate;
        private final PrivateKey privateKey;

        NFKeyManager(X509Certificate certificate, PrivateKey privateKey) {
            this.certificate = certificate;
            this.privateKey = privateKey;
        }

        public String chooseClientAlias(String[] arg0, Principal[] arg1, Socket arg2) {
            return this.certificate.getIssuerDN().getName();
        }

        public String chooseServerAlias(String arg0, Principal[] arg1, Socket arg2) {
            return null;
        }

        public X509Certificate[] getCertificateChain(String arg0) {
            return new X509Certificate[]{this.certificate};
        }

        public String[] getClientAliases(String arg0, Principal[] arg1) {
            return new String[]{this.certificate.getIssuerDN().getName()};
        }

        public PrivateKey getPrivateKey(String arg0) {
            return this.privateKey;
        }

        public String[] getServerAliases(String arg0, Principal[] arg1) {
            return null;
        }
    }
}
