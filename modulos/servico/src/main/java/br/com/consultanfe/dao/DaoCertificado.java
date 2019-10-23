package br.com.consultanfe.dao;

import br.com.gruposaga.cad.certificado.modelo.Certificado;
import br.com.gruposaga.cad.certificado.modelo.Certificado_;
import br.com.gruposaga.cad.cpo.Cpo;

import javax.inject.Inject;
import javax.persistence.EntityManager;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Root;
import javax.transaction.Transactional;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.temporal.ChronoUnit;
import java.util.Calendar;
import java.util.List;
import java.util.TimeZone;

public class DaoCertificado {

    @Cpo
    @Inject
    private EntityManager cpo;

    /**
     * Converte um Certificado para Modelo NFe.
     *
     * @param certificadoCpo instanciado.
     */
    public br.com.samuelweb.certificado.Certificado converteCertificado (Certificado certificadoCpo) {
        br.com.samuelweb.certificado.Certificado certificado = new br.com.samuelweb.certificado.Certificado();
        certificado.setNome(certificadoCpo.getNome());
        certificado.setVencimento(calendarToLocalDateTime(certificadoCpo.getDataExpiracao()).toLocalDate());
        certificado.setDiasRestantes(ChronoUnit.DAYS.between(LocalDate.now(), calendarToLocalDateTime(certificadoCpo.getDataExpiracao())
            .toLocalDate()));
        certificado.setArquivo(certificadoCpo.getNome());
        certificado.setArquivoBytes(certificadoCpo.getArquivo());
        certificado.setSenha("saga");
        certificado.setTipo(br.com.samuelweb.certificado.Certificado.ARQUIVO_BYTES);
        certificado.setValido(certificadoCpo.getValido());
        return certificado;
    }

    /**
     * Busca um Certificado conforme a UnidadeOrganizacional.
     *
     * @param cnpj
     * @return A instancia do Certificado encontrado. Valido ou nao.
     */
    @Transactional
    public Certificado buscaCertificadoCpoPorUnidade (String cnpj) {
        cnpj = insereCaracteresCpfCnpj(cnpj);
        CriteriaBuilder builder = cpo.getCriteriaBuilder();
        CriteriaQuery<Certificado> criteria = builder.createQuery(Certificado.class);
        Root<Certificado> root = criteria.from(Certificado.class);
        criteria.select(root).where(builder.equal(root.get(Certificado_.cnpj), cnpj));
        return cpo.createQuery(criteria).getSingleResult();
    }

    /**
     * Inicializa um Certificado.
     * @param id do Certificado solicitado.
     * @return A instancia do certificado solicitado. Valido ou nao.
     */
    @Transactional
    public Certificado loadCertificadoCpoById(Long id) {
        return cpo.find(Certificado.class, id);
    }

    /**
     * Busca todos os Certificados existentes no Banco.
     * @return ArrayList de Certificados. Validos ou nao.
     */
    @Transactional
    public List<Certificado> buscaListaTodosCertificadosCpo () {
        CriteriaBuilder builder = cpo.getCriteriaBuilder();
        CriteriaQuery<Certificado> criteria = builder.createQuery(Certificado.class);
        Root<Certificado> root = criteria.from(Certificado.class);
        criteria.select(root).orderBy(builder.asc(root.get(Certificado_.nomeFantasia)));
        return cpo.createQuery(criteria).getResultList();
    }

    /**
     * Busca todos os Certificados existentes que estao validos.
     * @return ArrayList de Certificados validos.
     */
    public List<Certificado> buscaListaTodosCertificadosCpoValidos () {
        List<Certificado> validos = buscaListaTodosCertificadosCpo();
        for (Certificado certificado : validos) {
            if (!certificadoValido(certificado)) {
                validos.remove(certificado);
            }
        }
        return validos;
    }

    /**
     * Verifica se o Certificado informado esta valido.
     *
     * @param certificado
     * @return True se valido.
     */
    public static Boolean certificadoValido(Certificado certificado) {
        return (certificado.getDataExpiracao().compareTo(Calendar.getInstance()) < 0) ? Boolean.FALSE : Boolean.TRUE;
    }

    private LocalDateTime calendarToLocalDateTime (Calendar calendar) {
        if (calendar == null) {
            return null;
        }
        TimeZone tz = calendar.getTimeZone();
        ZoneId zid = tz == null ? ZoneId.systemDefault() : tz.toZoneId();
        return LocalDateTime.ofInstant(calendar.toInstant(), zid);
    }

    private String insereCaracteresCpfCnpj(String cpf) {
        if (cpf != null) {
            cpf = cpf.replace(".", "").replace("-", "").replace("/", "").replace(",", "").replace(" ", "");
            cpf = cpf.trim();
            if (cpf.matches("[0-9]*")) {
                if (cpf.length() <= 11) {
                    if (cpf.length() < 7) {
                        return cpf;
                    }
                    if (cpf.length() == 7) {
                        cpf = "0000" + cpf;
                    } else if (cpf.length() == 8) {
                        cpf = "000" + cpf;
                    } else if (cpf.length() == 9) {
                        cpf = "00" + cpf;
                    } else if (cpf.length() == 10) {
                        cpf = "0" + cpf;
                    }
                    cpf = cpf.substring(0, 3) + "." + cpf.substring(3, 6) + "." + cpf.substring(6, 9) + "-"
                        + cpf.substring(9, 11);

                    return cpf;
                } else if (cpf.length() > 11) {
                    if (cpf.length() == 12) {
                        cpf = "00" + cpf;
                    } else if (cpf.length() == 13) {
                        cpf = "0" + cpf;
                    }
                    cpf = cpf.substring(0, 2) + "." + cpf.substring(2, 5) + "." + cpf.substring(5, 8) + "/"
                        + cpf.substring(8, 12) + "-" + cpf.substring(12, 14);
                    return cpf;
                }
            }
        }
        return "";
    }

}
