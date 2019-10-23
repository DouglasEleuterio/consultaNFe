package br.com.consultanfe.ws.api;

import br.com.consultanfe.modelo.*;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;

@Api
@Path(WebServicesConsultaNFe.NFE)
public interface WsConsultaNFe {

    @POST
    @Path("buscaxml")
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    @ApiOperation(value = "Retorna o XML da NFe direto da SEFAZ, de acordo com a chave ou NSU")
    ResultBuscaXml getXml (@Valid @NotNull ParamsBuscaXml paramsBuscaXml);

    @POST
    @Path("buscastatus")
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    @ApiOperation(value = "Retorna o STATUS da NFe direto da SEFAZ, de acordo com a chave")
    ResultBuscaStatus getStatus (@Valid @NotNull ParamsBuscaStatus paramsBuscaStatus);

    @POST
    @Path("manifestonfe")
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    @ApiOperation(value = "Realiza a manifestacao da NFe na SEFAZ")
    ResultManifestacao manifesta (@Valid @NotNull ParamsManifestacao paramsManifestacao);
}
