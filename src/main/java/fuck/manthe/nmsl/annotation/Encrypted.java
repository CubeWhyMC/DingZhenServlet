package fuck.manthe.nmsl.annotation;

import fuck.manthe.nmsl.conventer.CryptConverter;
import jakarta.persistence.Convert;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target({ElementType.METHOD, ElementType.FIELD, ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Convert(converter = CryptConverter.class)
public @interface Encrypted {
}
