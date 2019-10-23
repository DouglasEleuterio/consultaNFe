package br.com.consultanfe.modelo;

import io.swagger.annotations.ApiModel;

import javax.validation.constraints.NotNull;

@ApiModel(description = "Parâmetros para manifestação da NFe na Sefaz")
public class ParamsManifestacao {

    @NotNull
    String chave;

    @NotNull
    TipoManifestacao manifestacao;

    @NotNull
    String cnpj;

    @NotNull
    String motivo;

    @NotNull
    String data;

    public String getChave() {
        return chave;
    }

    public void setChave(String chave) {
        this.chave = chave;
    }

    public TipoManifestacao getManifestacao() {
        return manifestacao;
    }

    public void setManifestacao(TipoManifestacao manifestacao) {
        this.manifestacao = manifestacao;
    }

    public String getCnpj() {
        return cnpj;
    }

    public void setCnpj(String cnpj) {
        this.cnpj = cnpj;
    }

    public String getMotivo() {
        return motivo;
    }

    public void setMotivo(String motivo) {
        this.motivo = motivo;
    }

    public String getData() {
        return data;
    }

    public void setData(String data) {
        this.data = data;
    }
}
