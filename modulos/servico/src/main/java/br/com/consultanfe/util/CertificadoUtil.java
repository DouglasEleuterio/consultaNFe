package br.com.consultanfe.util;

import br.com.consultanfe.dom.ConfiguracoesIniciaisNfe;
import br.com.consultanfe.exception.NfeException;
import br.com.samuelweb.certificado.Certificado;
import br.com.samuelweb.certificado.exception.CertificadoException;

import java.io.InputStream;

public class CertificadoUtil {

	public static ConfiguracoesIniciaisNfe iniciaConfiguracoes(InputStream nfeCacerts) throws NfeException {
		try {
			Certificado certificado = ConfiguracoesIniciaisNfe.getInstance().getCertificado();
			if(!certificado.isValido()){
			    throw new CertificadoException("Certificado vencido.");
            }
			CertificadoService.inicializaCertificado(certificado, nfeCacerts);
		} catch (Exception e) {
			throw new NfeException(e.getMessage());
		}
        return ConfiguracoesIniciaisNfe.getInstance();
	}

}
