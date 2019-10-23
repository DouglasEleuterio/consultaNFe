package br.com.consultanfe.util;

import br.com.gruposaga.jpp.prefs.PreferenciasCaminho;

import javax.enterprise.inject.Produces;
import javax.inject.Inject;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;

/**
 * @author Amsterdam Luís
 */
public class NfeCacertsProvider
{
    private @Inject PreferenciasCaminho prefs;

    @Produces
    @NfeCacerts
    private InputStream produce ()
    {
        try
        {
            return Files.newInputStream(prefs.getPastaRaiz().resolve("NfeCacerts"));
        }
        catch (IOException e)
        {
            throw new RuntimeException("Não foi possível ler o arquivo NfeCacerts!", e);
        }
    }
}
