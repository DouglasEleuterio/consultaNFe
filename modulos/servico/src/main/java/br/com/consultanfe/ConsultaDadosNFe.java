package br.com.consultanfe;


import br.com.consultanfe.dao.DaoCertificado;
import br.com.consultanfe.dom.ConfiguracoesIniciaisNfe;
import br.com.consultanfe.servico.ServicosNFe;
import br.com.consultanfe.util.Estados;
import br.com.consultanfe.util.NfeCacerts;
import br.com.consultanfe.util.XmlUtil;
import br.com.gruposaga.cad.certificado.modelo.Certificado;
import br.com.gruposaga.cad.consultanfe.modelo.ResultBuscaStatus;
import br.com.gruposaga.cad.consultanfe.modelo.ResultBuscaXml;
import br.com.gruposaga.cad.consultanfe.modelo.ResultManifestacao;
import br.com.gruposaga.cad.consultanfe.modelo.TipoManifestacao;
import br.inf.portalfiscal.nfe.schema.retdistdfeint.RetDistDFeInt;
import br.inf.portalfiscal.nfe.schema_4.retConsSitNFe.TRetConsSitNFe;
import org.slf4j.Logger;

import javax.inject.Inject;
import java.io.InputStream;
import java.time.LocalDate;

public class ConsultaDadosNFe {

    private @Inject Logger logger;
    private @NfeCacerts
    @Inject InputStream nfeCacerts;
    private @Inject
    DaoCertificado daoCertificado;
    private final String ambiente = "1";

    /**
     * Busca o xml da NFe na Sefaz.
     *
     * @param tipoConsulta só pode ser "NSU" ou "CHAVE".
     * @param cpfCnpj      do destinatario da NFe.
     * @param nsuChave     NSU ou Chave da NFe
     * @param uf           2 primeiros caracteres da chave.
     * @return Resultado da Busca
     */
    public ResultBuscaXml buscaXmlNFe(String tipoConsulta, String cpfCnpj, String nsuChave, String uf) {
        configuraCertificado(cpfCnpj, uf);
        String cnpj = cpfCnpj.replace("-", "").replace(".", "").replace("/", "");
        ResultBuscaXml resultBuscaXml = new ResultBuscaXml();
        RetDistDFeInt retorno;
        String xml = "";
        String msgRetorno;
        try {
            retorno = ServicosNFe.distribuicaoDfe(nfeCacerts, "CNPJ", cnpj, tipoConsulta, nsuChave);
            xml = XmlUtil.xmlFromRetDistDFeInt(retorno);
            if (xml.equals("")) {
                msgRetorno = "Impossivel baixar o xml! Receita - " + retorno.getXMotivo();
//                logger.info("CONSULTANFE - IMPOSSIVEL BAIXAR O XML DE: " + cpfCnpj + " - " + retorno.getXMotivo());
            } else {
                msgRetorno = "Receita - " + retorno.getXMotivo();
//                logger.info("CONSULTANFE - XML BAIXADO COM SUCESSO");
            }
        } catch (Exception e) {
            msgRetorno = "Erro ao baixar o xml";
            logger.error("CONSULTANFE - ERRO AO BUSCAR O XML DE: " + cpfCnpj);
        }
        resultBuscaXml.setXml(xml);
        resultBuscaXml.setMsgRetorno(msgRetorno);
        return resultBuscaXml;
    }

    /**
     * Busca o Status da NFe na Sefaz.
     *
     * @param cpfCnpj
     * @param chave
     * @return
     */
    public ResultBuscaStatus buscaStatusNFe(String cpfCnpj, String chave) {
        configuraCertificado(cpfCnpj, chave.substring(0, 2));
        ResultBuscaStatus resultBuscaStatus = new ResultBuscaStatus();
        TRetConsSitNFe retorno;
        String status = "";
        try {
            retorno = ServicosNFe.consultaXml(nfeCacerts, chave, "NFe");
            status = retorno.getXMotivo();
//            if (status.equals("")) {
//                logger.warn("CONSULTANFE - IMPOSSIVEL ENCONTRAR O STATUS DE: " + cpfCnpj);
//            } else {
//                logger.info("CONSULTANFE - STATUS ENCONTRADO COM SUCESSO");
//            }
        } catch (Exception e) {
            logger.error("CONSULTANFE - ERRO AO BUSCAR O STATUS DE: " + cpfCnpj);
        }
        resultBuscaStatus.setStatus(status);
        resultBuscaStatus.setDataStatus(LocalDate.now().toString());
        return resultBuscaStatus;
    }

    /**
     * Realiza o Manifesto da NFe na Sefaz.
     *
     * @param chave
     * @param manifestacao
     * @param cnpjNF
     * @param motivo caso a operação não seja realizada.
     * @param data necessario que seja LocalDateTime.
     * @return
     */
    public ResultManifestacao manifestaNFe(String chave, TipoManifestacao manifestacao, String cnpjNF, String motivo, String data) {
        configuraCertificado(cnpjNF, chave.substring(0, 2));
        String cnpj = cnpjNF.replace("-", "").replace(".", "").replace("/", "");
        ResultManifestacao resultManifestacao = new ResultManifestacao();
        br.inf.portalfiscal.nfe.schema.envConfRecebto.TRetEnvEvento retorno;
        String msgRetorno;
        try {
            retorno = ServicosNFe.manifestacao(nfeCacerts, chave, manifestacao, cnpj, motivo, data);
            msgRetorno = retorno.getXMotivo();
            if (msgRetorno.contains("Rejeicao")) {
//                logger.warn("CONSULTANFE - PROBLEMAS AO MANIFESTAR A NFE: " + chave);
                msgRetorno = "Problemas ao manifestar a NFe";
            }
        } catch (Exception e) {
            msgRetorno = "Nao foi possivel manifestar a NFe";
            logger.error("CONSULTANFE - ERRO AO MANIFESTAR A NFE: " + chave);
        }
        resultManifestacao.setMsgRetorno(msgRetorno);
        return resultManifestacao;
    }

    private void configuraCertificado(String cnpj, String uf) {
        try {
            Certificado certificadoCpo = daoCertificado.buscaCertificadoCpoPorUnidade(cnpj);
            br.com.samuelweb.certificado.Certificado certificado = daoCertificado.converteCertificado(certificadoCpo);
            ConfiguracoesIniciaisNfe.iniciaConfiguracoes(getEstadoFromUf(uf), ambiente, certificado, "");
            ConfiguracoesIniciaisNfe.getInstance().setTimeout(6000);
        } catch (Exception e) {
            logger.error("CONSULTANFE - ERRO AO CARREGAR CERTIFICADO DE: " + cnpj);
        }
//        logger.info("CONSULTANFE - CERTIFICADOS OK");
    }

    private Estados getEstadoFromUf(String uf) {
        Estados estado = null;
        for (Estados est : Estados.values()) {
            if (est.getCodigoIbge().equals(uf)) {
                estado = est;
                break;
            }
        }
        return estado;
    }
}
