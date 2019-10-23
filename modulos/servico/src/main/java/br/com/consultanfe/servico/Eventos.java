package br.com.consultanfe.servico;

import br.com.consultanfe.exception.NfeException;
import br.com.consultanfe.exception.NfeValidacaoException;
import br.com.consultanfe.util.ConstantesUtil;
import br.com.consultanfe.util.ObjetoUtil;
import br.com.consultanfe.util.WebServiceUtil;
import br.inf.portalfiscal.www.nfe_400.wsdl.NFeRecepcaoEvento.NFeRecepcaoEvento4Stub;
import org.apache.axiom.om.OMElement;
import org.apache.axiom.om.util.AXIOMUtil;

import javax.xml.stream.XMLStreamException;
import java.io.InputStream;
import java.rmi.RemoteException;

class Eventos {

    static String enviarEvento(InputStream nfeCacerts, String xml, String tipoEvento, boolean valida, String tipo) throws NfeException {

        try {

//            ConfiguracoesIniciaisNfe config = CertificadoUtil.iniciaConfiguracoes(nfeCacerts);
            xml = Assinar.assinaNfe(xml, Assinar.EVENTO);

            if(valida){
                String erros ="";
                switch (tipoEvento) {
                    case ConstantesUtil.EVENTO.CANCELAR:
                        erros = Validar.validaXml(xml, Validar.CANCELAR);
                        break;
                    case ConstantesUtil.EVENTO.CCE:
                        erros = Validar.validaXml(xml, Validar.CCE);
                        break;
                    case ConstantesUtil.EVENTO.MANIFESTACAO:
                        erros = Validar.validaXml(xml, Validar.MANIFESTAR);
                        break;
                    default:
                        break;
                }

                if(!ObjetoUtil.isEmpty(erros)){
                    throw new NfeValidacaoException("Erro Na Validação do Xml: "+erros);
                }
            }

            OMElement ome = AXIOMUtil.stringToOM(xml);

            NFeRecepcaoEvento4Stub.NfeDadosMsg dadosMsg = new NFeRecepcaoEvento4Stub.NfeDadosMsg();
            dadosMsg.setExtraElement(ome);

            String url ;
            if(tipoEvento.equals(ConstantesUtil.EVENTO.MANIFESTACAO)){
                url =  WebServiceUtil.getUrl(ConstantesUtil.NFE, ConstantesUtil.SERVICOS.MANIFESTACAO);
            }else{
                url = WebServiceUtil.getUrl(tipo, ConstantesUtil.SERVICOS.EVENTO);
            }

            NFeRecepcaoEvento4Stub stub = new NFeRecepcaoEvento4Stub(url);
            NFeRecepcaoEvento4Stub.NfeResultMsg result = stub.nfeRecepcaoEvento(dadosMsg);

            return result.getExtraElement().toString();
        } catch (RemoteException | XMLStreamException e) {
            throw new NfeException(e.getMessage());
        }

    }

}
