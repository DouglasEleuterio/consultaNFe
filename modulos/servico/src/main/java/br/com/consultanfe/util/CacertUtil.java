package br.com.consultanfe.util;

import javax.net.ssl.*;
import java.io.*;
import java.security.*;
import java.security.cert.CertificateEncodingException;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;
import java.util.Arrays;
import java.util.List;
import java.util.logging.Logger;

public class CacertUtil {

    private static final String CACERT = "NFeCacerts";

    private static final int TIMEOUT = 6000;

    private static Logger log = Logger.getLogger("NFE-Cacerts");

    public static void atualizaCacerts(String ambiente) {
        try {
            char[] passphrase = "changeit".toCharArray();
            char SEP = File.separatorChar;
            File dir = new File(System.getProperty("java.home") + SEP + "lib" + SEP + "security");
            File file = new File(dir, CACERT);
            if (!file.isFile()) {
                file = new File(dir, "cacerts");
            }
            InputStream in = new FileInputStream(file);
            KeyStore ks = KeyStore.getInstance(KeyStore.getDefaultType());
            ks.load(in, passphrase);
            in.close();
            if ((ambiente.equals("2"))) {
                geraHomologacao(ks);
            } else {
                geraProducao(ks);
            }
            File cafile = new File(CACERT);
            OutputStream out = new FileOutputStream(cafile);
            ks.store(out, passphrase);
            out.close();
            log.info("CACERTS ATUALIZADOS COM SUCESSO");
        } catch (Exception e) {
            log.info("ERRO AO ATUALIZAR OS CACERTS " + e);
        }
    }

    /**
     * Lista de Enderecos para cacerts no ambiente de Homologacao. Altere aqui caso haja modificacoes.
     */
    private static final List<String> enderecosHomolocacao = Arrays.asList(
        "homnfe.sefaz.am.gov.br",
        "hnfe.sefaz.ba.gov.br",
        "nfeh.sefaz.ce.gov.br",
        "app.sefaz.es.gov.br",
        "homolog.sefaz.go.gov.br",
        "hnfe.fazenda.mg.gov.br",
        "homologacao.nfe.ms.gov.br",
        "homologacao.sefaz.mt.gov.br",
        "nfehomolog.sefaz.pe.gov.br",
        "homologacao.nfe.fazenda.pr.gov.br",
        "nfe-homologacao.sefazrs.rs.gov.br",
        "cad.sefazrs.rs.gov.br",
        "homologacao.nfe.fazenda.sp.gov.br",
        "hom.sefazvirtual.fazenda.gov.br",
        "nfe-homologacao.svrs.rs.gov.br",
        "cad.svrs.rs.gov.br",
        "hom.svc.fazenda.gov.br",
        "hom.nfe.fazenda.gov.br"
    );

    /**
     * Lista de Enderecos para cacerts no ambiente de Producao. Altere aqui caso haja modificacoes.
     */
    private static final List<String> enderecosProducao = Arrays.asList(
        "nfe.sefaz.am.gov.br",
        "nfe.sefaz.ba.gov.br",
        "nfe.sefaz.ce.gov.br",
        "nfe.sefaz.go.gov.br",
        "nfe.fazenda.mg.gov.br",
        "nfe.fazenda.ms.gov.br",
        "nfe.sefaz.mt.gov.br",
        "nfe.sefaz.pe.gov.br",
        "nfe.fazenda.pr.gov.br",
        "nfe.sefazrs.rs.gov.br",
        "nfe.fazenda.sp.gov.br",
        "www.sefazvirtual.fazenda.gov.br",
        "nfe.svrs.rs.gov.br",
        "www.svc.fazenda.gov.br",
        "www.nfe.fazenda.gov.br",
        "www1.nfe.fazenda.gov.br",
        "nfe.svrs.rs.gov.br"
    );

    private static void geraHomologacao(KeyStore ks) {
        for (String endereco : enderecosHomolocacao) {
            try {
                get(endereco, 443, ks);
            } catch (Exception e) {
                log.info("SEM ACESSO AO SERVICO DE: " + endereco);
                continue;
            }
        }
    }

    private static void geraProducao(KeyStore ks) {
        for (String endereco : enderecosProducao) {
            try {
                get(endereco, 443, ks);
            } catch (Exception e) {
                log.info("SEM ACESSO AO SERVICO DE: " + endereco);
                continue;
            }
        }
    }

    private static void get(String host, int port, KeyStore ks) throws NoSuchAlgorithmException, KeyStoreException,
        KeyManagementException, CertificateEncodingException {
        SSLContext context = SSLContext.getInstance("TLS");
        TrustManagerFactory tmf = TrustManagerFactory.getInstance(
            TrustManagerFactory.getDefaultAlgorithm());
        tmf.init(ks);
        X509TrustManager defaultTrustManager = (X509TrustManager) tmf.getTrustManagers()[0];
        SavingTrustManager tm = new SavingTrustManager(defaultTrustManager);
        context.init(null, new TrustManager[]{tm}, null);
        try {
            SSLSocketFactory factory = context.getSocketFactory();
//            log.info("| Conectando a " + host + ":" + port + "...");
            SSLSocket socket = (SSLSocket) factory.createSocket(host, port);
            socket.setSoTimeout(TIMEOUT);
//            log.info("| Starting SSL handshake...");
            socket.startHandshake();
            socket.close();
//            log.info("| Sem erros, certificado CACERT atualizado");
        } catch (IOException e) {
//            log.severe("| " + e.toString());
        } finally {
            X509Certificate[] chain = tm.chain;
            if (chain == null) {
//                log.info("| Nao foi possivel obter o certificado CACERT do servidor");
            }
//            log.info("| Servidor enviou " + chain.length + " certificados(s) CACERT:");
            MessageDigest sha1 = MessageDigest.getInstance("SHA1");
            MessageDigest md5 = MessageDigest.getInstance("MD5");
            for (int i = 0; i < chain.length; i++) {
                X509Certificate cert = chain[i];
                sha1.update(cert.getEncoded());
                md5.update(cert.getEncoded());
                String alias = host + "-" + (i);
                ks.setCertificateEntry(alias, cert);
//                log.info("| Certificado CACERT adicionado na keystore '" + CACERT + "' usando a alias '" + alias + "'");
            }
        }
    }

    private static class SavingTrustManager implements X509TrustManager {
        private final X509TrustManager tm;
        private X509Certificate[] chain;

        SavingTrustManager(X509TrustManager tm) {
            this.tm = tm;
        }

        public X509Certificate[] getAcceptedIssuers() {
            return new X509Certificate[0];
        }

        public void checkClientTrusted(X509Certificate[] chain, String authType) {
            throw new UnsupportedOperationException();
        }

        public void checkServerTrusted(X509Certificate[] chain, String authType) throws CertificateException {
            this.chain = chain;
            tm.checkServerTrusted(chain, authType);
        }
    }
}
