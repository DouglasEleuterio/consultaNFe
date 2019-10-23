package br.com.consultanfe.modelo;

import io.swagger.annotations.ApiModel;

import javax.validation.constraints.NotNull;
import java.util.Objects;

@ApiModel(description = "Resultado da Manifestação da NFe")
public class ResultManifestacao {

    @NotNull
    private String msgRetorno;

    public String getMsgRetorno() {
        return msgRetorno;
    }

    public void setMsgRetorno(String msgRetorno) {
        this.msgRetorno = msgRetorno;
    }
}
