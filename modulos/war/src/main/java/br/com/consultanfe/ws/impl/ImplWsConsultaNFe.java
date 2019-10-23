package br.com.consultanfe.ws.impl;

import br.com.gruposaga.cad.consultanfe.ConsultaDadosNFe;
import br.com.gruposaga.cad.consultanfe.ws.api.WebServicesConsultaNFe;
import br.com.gruposaga.cad.consultanfe.ws.api.WsConsultaNFe;

import javax.inject.Inject;
import javax.ws.rs.Path;

@Path(WebServicesConsultaNFe.NFE)
public class ImplWsConsultaNFe implements WsConsultaNFe {

    @Inject
    private ConsultaDadosNFe consultaDadosNFe;

    @Override
    public ResultBuscaXml getXml(ParamsBuscaXml paramsBuscaXml) {
        return consultaDadosNFe.buscaXmlNFe(paramsBuscaXml.getTipoConsulta(), paramsBuscaXml.getCpfCnpj(),
            paramsBuscaXml.getNsuChave(), paramsBuscaXml.getUf());
    }

    @Override
    public ResultBuscaStatus getStatus(ParamsBuscaStatus paramsBuscaStatus) {
        return consultaDadosNFe.buscaStatusNFe(paramsBuscaStatus.getCpfCnpj(), paramsBuscaStatus.getChave());
    }

    @Override
    public ResultManifestacao manifesta(ParamsManifestacao paramsManifestacao) {
        return consultaDadosNFe.manifestaNFe(paramsManifestacao.getChave(), paramsManifestacao.getManifestacao(),
            paramsManifestacao.getCnpj(), paramsManifestacao.getMotivo(), paramsManifestacao.getData());
    }
}
