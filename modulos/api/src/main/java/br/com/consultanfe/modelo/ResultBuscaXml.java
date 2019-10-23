package br.com.consultanfe.modelo;

import io.swagger.annotations.ApiModel;

import javax.validation.constraints.NotNull;
import java.util.Objects;

@ApiModel(description = "Resultado de consulta do XML da NFe")
public class ResultBuscaXml {

    @NotNull
    private String xml;

    @NotNull
    private String msgRetorno;

    public String getXml() {
        return xml;
    }

    public void setXml(String xml) {
        this.xml = xml;
    }

    public String getMsgRetorno() {
        return msgRetorno;
    }

    public void setMsgRetorno(String msgRetorno) {
        this.msgRetorno = msgRetorno;
    }
}
