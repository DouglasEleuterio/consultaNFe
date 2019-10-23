package br.com.consultanfe.util;

import javax.inject.Qualifier;
import java.lang.annotation.*;

/**
 * @author Amsterdam Luís
 */
@Qualifier
@Documented
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.FIELD, ElementType.TYPE, ElementType.METHOD})
public @interface NfeCacerts
{
}
