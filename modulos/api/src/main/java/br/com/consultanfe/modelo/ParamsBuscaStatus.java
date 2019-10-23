package br.com.consultanfe.modelo;

import io.swagger.annotations.ApiModel;

import javax.validation.constraints.NotNull;
import java.util.Objects;

@ApiModel(description = "Parâmetros para busca do status na sefaz")
public class ParamsBuscaStatus {

    @NotNull
    private String cpfCnpj;

    @NotNull
    private String chave;

    public String getCpfCnpj() {
        return cpfCnpj;
    }

    public void setCpfCnpj(String cpfCnpj) {
        this.cpfCnpj = cpfCnpj;
    }

    public String getChave() {
        return chave;
    }

    public void setChave(String chave) {
        this.chave = chave;
    }
}
