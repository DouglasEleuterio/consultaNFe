/**
 *
 */
package br.com.consultanfe.servico;

import br.com.consultanfe.util.Estados;
import br.com.consultanfe.util.XmlUtil;
import br.com.consultanfe.exception.NfeException;
import br.com.gruposaga.cad.consultanfe.modelo.TipoManifestacao;
import br.inf.portalfiscal.nfe.schema.envEventoCancNFe.TEnvEvento;
import br.inf.portalfiscal.nfe.schema.envEventoCancNFe.TRetEnvEvento;
import br.inf.portalfiscal.nfe.schema.retConsCad.TRetConsCad;
import br.inf.portalfiscal.nfe.schema.retdistdfeint.RetDistDFeInt;
import br.inf.portalfiscal.nfe.schema_4.enviNFe.TEnviNFe;
import br.inf.portalfiscal.nfe.schema_4.enviNFe.TRetEnviNFe;
import br.inf.portalfiscal.nfe.schema_4.inutNFe.TInutNFe;
import br.inf.portalfiscal.nfe.schema_4.inutNFe.TRetInutNFe;
import br.inf.portalfiscal.nfe.schema_4.retConsReciNFe.TRetConsReciNFe;
import br.inf.portalfiscal.nfe.schema_4.retConsSitNFe.TRetConsSitNFe;
import br.inf.portalfiscal.nfe.schema_4.retConsStatServ.TRetConsStatServ;

import javax.xml.bind.JAXBException;
import java.io.InputStream;


/**
 * @author Samuel Oliveira - samuk.exe@hotmail.com - www.samuelweb.com.br
 */
public class ServicosNFe {

    /**
     * Construtor privado
     */
    private ServicosNFe() {
    }


    /**
     * Classe Reponsavel Por Consultar a Distribuiçao da NFE na SEFAZ
     *
     * @param tipoCliente  Informar DistribuicaoDFe.CPF ou DistribuicaoDFe.CNPJ
     * @param cpfCnpj
     * @param tipoConsulta Informar DistribuicaoDFe.NSU ou DistribuicaoDFe.CHAVE
     * @param nsuChave
     * @return
     * @throws NfeException
     */
    public static RetDistDFeInt distribuicaoDfe(InputStream nfeCacerts, String tipoCliente, String cpfCnpj, String tipoConsulta, String nsuChave) throws NfeException {

        return DistribuicaoDFe.consultaNfe(nfeCacerts, tipoCliente, cpfCnpj, tipoConsulta, nsuChave);

    }

    /**
     * Metodo Responsavel Buscar o Status de Serviço do Servidor da Sefaz
     * No tipo Informar ConstantesUtil.NFE ou ConstantesUtil.NFCE
     *
     * @param tipo
     * @return
     * @throws NfeException
     */
    public static TRetConsStatServ statusServico(InputStream nfeCacerts, String tipo) throws NfeException {

        return Status.statusServico(nfeCacerts, tipo);

    }

    /**
     * Classe Reponsavel Por Consultar o status da NFE na SEFAZ
     * No tipo Informar ConstantesUtil.NFE ou ConstantesUtil.NFCE
     *
     * @param chave
     * @param tipo
     * @return TRetConsSitNFe
     * @throws NfeException
     */
    public static TRetConsSitNFe consultaXml(InputStream nfeCacerts, String chave, String tipo) throws NfeException {

        return ConsultaXml.consultaXml(nfeCacerts, chave, tipo);

    }

    /**
     * Classe Reponsavel Por Consultar o cadastro do Cnpj/CPF na SEFAZ
     *
     * @param tipo    Usar ConsultaCadastro.CNPJ ou ConsultaCadastro.CPF
     * @param cnpjCpf
     * @param estado
     * @return TRetConsCad
     * @throws NfeException
     */
    public static TRetConsCad consultaCadastro(InputStream nfeCacerts, String tipo, String cnpjCpf, Estados estado) throws NfeException {

        return ConsultaCadastro.consultaCadastro(nfeCacerts, tipo, cnpjCpf, estado);

    }

    /**
     * Classe Reponsavel Por Consultar o retorno da NFE na SEFAZ
     * No tipo Informar ConstantesUtil.NFE ou ConstantesUtil.NFCE
     *
     * @param recibo
     * @param tipo
     * @return
     * @throws NfeException
     */
    public static TRetConsReciNFe consultaRecibo(InputStream nfeCacerts, String recibo, String tipo) throws NfeException {

        return ConsultaRecibo.reciboNfe(nfeCacerts, recibo, tipo);

    }

	/**
	 * Classe Reponsavel Por Inutilizar a NFE na SEFAZ
	 * No tipo Informar ConstantesUtil.NFE ou ConstantesUtil.NFCE
     * Id = Código da UF + Ano (2 posições) + CNPJ
     * + modelo + série + número inicial e número final
     * precedida do literal “ID”
     *
     * @param id
     * @param motivo
     * @param tipo
     * @param validar
     * @return
     * @throws NfeException
     */
	public static TRetInutNFe inutilizacao(InputStream nfeCacerts, String id, String motivo, String tipo, boolean validar) throws NfeException{

		return Inutilizar.inutiliza(nfeCacerts, id , motivo, tipo, validar);

	}

	/**
	 * Classe Reponsavel Por criar o Objeto de Inutilização
	 * No tipo Informar ConstantesUtil.NFE ou ConstantesUtil.NFCE
     * Id = Código da UF + Ano (2 posições) + CNPJ
     * + modelo + série + número inicial e número final
     * precedida do literal “ID”
	 *
     * @param id
     * @param motivo
     * @param tipo
     * @return
     * @throws NfeException
     * @throws JAXBException
     */
	public static TInutNFe criaObjetoInutilizacao(String id, String motivo, String tipo) throws NfeException, JAXBException {

        TInutNFe inutNFe = Inutilizar.criaObjetoInutiliza(id , motivo, tipo);

        String xml = XmlUtil.objectToXml(inutNFe);
        xml = xml.replaceAll(" xmlns:ns2=\"http://www.w3.org/2000/09/xmldsig#\"", "");

       return  XmlUtil.xmlToObject(Assinar.assinaNfe(xml, Assinar.INFINUT), TInutNFe.class );

	}

    /**
     * Metodo para Montar a NFE.
     *
     * @param enviNFe
     * @param valida
     * @return
     * @throws NfeException
     */
    public static TEnviNFe montaNfe(TEnviNFe enviNFe, boolean valida) throws NfeException {

        return Enviar.montaNfe(enviNFe, valida);

    }

    /**
     * Metodo para Enviar a NFE.
     * No tipo Informar ConstantesUtil.NFE ou ConstantesUtil.NFCE
     *
     * @param enviNFe
     * @param tipo
     * @return
     * @throws NfeException
     */
    public static TRetEnviNFe enviarNfe(InputStream nfeCacerts, TEnviNFe enviNFe, String tipo) throws NfeException {

        return Enviar.enviaNfe(nfeCacerts, enviNFe, tipo);

    }

    /**
     * * Metodo para Cancelar a NFE.
     * No tipo Informar ConstantesUtil.NFE ou ConstantesUtil.NFCE
     *
     * @param envEvento
     * @return
     * @throws NfeException
     */
    public static TRetEnvEvento cancelarNfe(InputStream nfeCacerts, TEnvEvento envEvento, boolean valida, String tipo) throws NfeException {

        return Cancelar.eventoCancelamento(nfeCacerts, envEvento, valida, tipo);

    }

   /**
     * * Metodo para Envio da Carta De Correção da NFE.
     * No tipo Informar ConstantesUtil.NFE ou ConstantesUtil.NFCE
     *
     * @param evento
     * @param valida
     * @param tipo
     * @return
     * @throws NfeException
     */
    public static br.inf.portalfiscal.nfe.schema.envcce.TRetEnvEvento cce(InputStream nfeCacerts, br.inf.portalfiscal.nfe.schema.envcce.TEnvEvento evento, boolean valida, String tipo) throws NfeException {

        return CartaCorrecao.eventoCCe(nfeCacerts, evento, valida, tipo);

    }

    /**
     * Metodo para Manifestação da NFE.
     *
     * @param chave
     * @param manifestacao
     * @param cnpj
     * @param motivo
     * @param data
     * @return
     * @throws NfeException
     */
    public static br.inf.portalfiscal.nfe.schema.envConfRecebto.TRetEnvEvento manifestacao(InputStream nfeCacerts, String chave, TipoManifestacao manifestacao, String cnpj, String motivo, String data) throws NfeException {

        return ManifestacaoDestinatario.eventoManifestacao(nfeCacerts, chave, manifestacao, cnpj, data, motivo);

    }


}
