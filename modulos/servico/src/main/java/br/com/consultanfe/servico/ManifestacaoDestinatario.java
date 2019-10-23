package br.com.consultanfe.servico;

import br.com.consultanfe.util.CertificadoUtil;
import br.com.consultanfe.util.ConstantesUtil;
import br.com.consultanfe.util.XmlUtil;
import br.com.consultanfe.dom.ConfiguracoesIniciaisNfe;
import br.com.gruposaga.cad.consultanfe.modelo.TipoManifestacao;
import br.com.consultanfe.exception.NfeException;
import br.inf.portalfiscal.nfe.schema.envConfRecebto.TEnvEvento;
import br.inf.portalfiscal.nfe.schema.envConfRecebto.TEvento;
import br.inf.portalfiscal.nfe.schema.envConfRecebto.TRetEnvEvento;

import javax.xml.bind.JAXBException;
import java.io.InputStream;

/**
 * @author Samuel Oliveira - samuk.exe@hotmail.com
 * Data: 28/09/2017 - 11:11
 */
class ManifestacaoDestinatario {

    static TRetEnvEvento eventoManifestacao(InputStream nfeCacerts, String chave, TipoManifestacao manifestacao, String cnpj, String data, String motivo) throws NfeException {

        try {

            ConfiguracoesIniciaisNfe configuracoesNfe = CertificadoUtil.iniciaConfiguracoes(nfeCacerts);

            String id = "ID" + manifestacao.getCodigo() + chave + "01";

            TEnvEvento envEvento = new TEnvEvento();
            envEvento.setVersao(ConstantesUtil.VERSAO.EVENTO_MANIFESTAR);
            envEvento.setIdLote("1");

            TEvento evento = new TEvento();
            evento.setVersao(ConstantesUtil.VERSAO.EVENTO_MANIFESTAR);

            TEvento.InfEvento infEvento = new TEvento.InfEvento();
            infEvento.setId(id);
            infEvento.setCOrgao("91");
            infEvento.setTpAmb(configuracoesNfe.getAmbiente());
            infEvento.setCNPJ(cnpj);
            infEvento.setChNFe(chave);
            infEvento.setDhEvento(data);
            infEvento.setTpEvento(manifestacao.getCodigo());
            infEvento.setNSeqEvento("1");
            infEvento.setVerEvento(ConstantesUtil.VERSAO.EVENTO_MANIFESTAR);

            TEvento.InfEvento.DetEvento detEvento = new TEvento.InfEvento.DetEvento();
            detEvento.setVersao(ConstantesUtil.VERSAO.EVENTO_MANIFESTAR);
            detEvento.setDescEvento(manifestacao.getValor());
            if(manifestacao.equals(TipoManifestacao.OPERACAO_NAO_REALIZADA)){
                detEvento.setXJust(motivo);
            }
            infEvento.setDetEvento(detEvento);
            evento.setInfEvento(infEvento);
            envEvento.getEvento().add(evento);

            String xml = XmlUtil.objectToXml(envEvento);
            xml = xml.replaceAll(" xmlns:ns2=\"http://www.w3.org/2000/09/xmldsig#\"", "");
            xml = xml.replaceAll("<evento v", "<evento xmlns=\"http://www.portalfiscal.inf.br/nfe\" v");

            xml = Eventos.enviarEvento(nfeCacerts, xml, ConstantesUtil.EVENTO.MANIFESTACAO, false, "");

            return XmlUtil.xmlToObject(xml, TRetEnvEvento.class);

        } catch (JAXBException e) {
            throw new NfeException(e.getMessage());
        }
    }

}
