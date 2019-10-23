package br.com.consultanfe.util;

import br.com.samuelweb.certificado.Certificado;
import com.sun.net.ssl.internal.ssl.Provider;
import org.apache.commons.httpclient.protocol.Protocol;
import org.apache.commons.io.FileUtils;

import java.io.*;
import java.security.*;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;

public class CertificadoService {

    public CertificadoService() {
    }

    /**
     * Inicializa o Certificados na JVM e no Sistema.
     * @param certificado
     * @throws Exception
     */
    public static void inicializaCertificado(Certificado certificado, InputStream cacerts) throws Exception {
        iniciaPorSocketFactory(certificado, cacerts);
//        iniciaPorSystemProperties(buscaArquivo(certificado.getArquivoBytes(), certificado.getNome()), getPathFromInputStream(cacerts));
    }

    private static void iniciaPorSocketFactory(Certificado certificado, InputStream cacerts) throws CertificateException,
        KeyStoreException, UnrecoverableKeyException, NoSuchAlgorithmException, IOException, KeyManagementException {
        KeyStore keyStore = getKeyStore(certificado.getArquivoBytes(), certificado.getSenha());
        X509Certificate certificate = getCertificate(certificado, keyStore);
        PrivateKey privateKey = (PrivateKey) keyStore.getKey(certificado.getNome(), certificado.getSenha().toCharArray());
        SocketFactoryDinamico socketFactory = new SocketFactoryDinamico(certificate, privateKey, cacerts, "TLSv1.2");
        Protocol protocol = new Protocol("https", socketFactory, 443);
        Protocol.registerProtocol("https", protocol);
    }

    private static void iniciaPorSystemProperties(String pathA1, String pathCacerts) {
        System.setProperty("java.protocol.handler.pkgs", "com.sun.net.ssl.internal.www.protocol");
        Security.addProvider(new Provider());
        System.setProperty("javax.net.ssl.keyStoreType", "PKCS12");
        System.clearProperty("javax.net.ssl.keyStore");
        System.clearProperty("javax.net.ssl.keyStorePassword");
        System.clearProperty("javax.net.ssl.trustStore");
        System.setProperty("jdk.tls.client.protocols", "TLSv1.2");
        System.setProperty("javax.net.ssl.keyStore", pathA1);
        System.setProperty("javax.net.ssl.keyStorePassword", "saga");
        System.setProperty("javax.net.ssl.trustStoreType", "JKS");
        System.setProperty("javax.net.ssl.trustStore", pathCacerts);
        System.setProperty("javax.net.ssl.trustStorePassword", "changeit");
    }

    private static String buscaArquivo (byte[] arquivo, String nome) {
        String path = "c:/temp/" + nome;
        try {
            FileOutputStream fileOutputStream = new FileOutputStream(path);
            fileOutputStream.write(arquivo);
            fileOutputStream.close();
        } catch (Exception e) {
            System.out.println("NAO FOI POSSIVEL BUSCAR O CERTIFICADO: " + e.toString());
        }
        return path;
    }

    /**
     * Gera a KeyStore do certificado informado, de acordo com a senha e o array de bytes do certificado.
     * @param bytes
     * @return
     * @throws KeyStoreException
     * @throws CertificateException
     */
    public static KeyStore getKeyStore(byte[] bytes, String senha) throws CertificateException, KeyStoreException {
        try {
            KeyStore keyStore = KeyStore.getInstance("pkcs12");
            keyStore.load(new ByteArrayInputStream(bytes), senha.toCharArray());
            return keyStore;
        } catch (KeyStoreException | NoSuchAlgorithmException | IOException e) {
            throw new KeyStoreException("SENHA INVALIDA: " + e.toString());
        }
    }

    public static X509Certificate getCertificate(Certificado certificado, KeyStore keystore) throws KeyStoreException {
        try {
            return (X509Certificate) keystore.getCertificate(certificado.getNome());
        } catch (KeyStoreException var3) {
            throw new KeyStoreException("Erro ao pegar X509Certificate: " + var3.getMessage());
        }
    }

    private static String getPathFromInputStream (InputStream inputStream) {
        File file = new File("c:\\temp\\arq");
        try {
            FileUtils.copyInputStreamToFile(inputStream, file);
        } catch (IOException e) {
            System.out.println("ERRO AO CRIAR FILE" + e.toString());
        }
        return file.getAbsolutePath();
    }
}
