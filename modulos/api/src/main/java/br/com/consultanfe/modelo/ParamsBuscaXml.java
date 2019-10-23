package br.com.consultanfe.modelo;

import io.swagger.annotations.ApiModel;

import javax.validation.constraints.NotNull;
import java.util.Objects;

@ApiModel(description = "Parâmetros para busca do xml na sefaz")
public class ParamsBuscaXml {

    @NotNull
    private String tipoConsulta;

    @NotNull
    private String cpfCnpj;

    @NotNull
    private String nsuChave;

    @NotNull
    private String uf;

    public String getTipoConsulta() {
        return tipoConsulta;
    }

    public void setTipoConsulta(String tipoConsulta) {
        this.tipoConsulta = tipoConsulta;
    }

    public String getCpfCnpj() {
        return cpfCnpj;
    }

    public void setCpfCnpj(String cpfCnpj) {
        this.cpfCnpj = cpfCnpj;
    }

    public String getNsuChave() {
        return nsuChave;
    }

    public void setNsuChave(String nsuChave) {
        this.nsuChave = nsuChave;
    }

    public String getUf() {
        return uf;
    }

    public void setUf(String uf) {
        this.uf = uf;
    }
}
