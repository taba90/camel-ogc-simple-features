package it.fox.gis.camel.expressions;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import org.apache.camel.support.language.LanguageAnnotation;

/** Annotation to use an ECQL expression as a method argument in bean. */
@Retention(RetentionPolicy.RUNTIME)
@Documented
@Target({ElementType.FIELD, ElementType.METHOD, ElementType.PARAMETER})
@LanguageAnnotation(language = "ogcECQL", factory = ECQLExpressionAnnotationFactory.class)
public @interface ECQLExpression {

    /** @return the ecql expression. */
    String value();

    /** @return a namespace prefix. */
    String nsPrefix() default "";

    /** @return a namespace URI. */
    String nsURI() default "";

    Class<?> resultType() default Object.class;
}
