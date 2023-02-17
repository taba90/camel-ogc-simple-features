package it.fox.gis.camel.expressions;

import java.lang.annotation.Annotation;
import org.apache.camel.CamelContext;
import org.apache.camel.Expression;
import org.apache.camel.support.language.DefaultAnnotationExpressionFactory;
import org.apache.camel.support.language.LanguageAnnotation;

/** Annotation Factory to create a Camel expression out of {@link ECQLExpression}. */
public class ECQLExpressionAnnotationFactory extends DefaultAnnotationExpressionFactory {

    @Override
    public Expression createExpression(
            CamelContext camelContext,
            Annotation annotation,
            LanguageAnnotation languageAnnotation,
            Class<?> expressionReturnType) {

        String expression = getExpressionFromAnnotation(annotation);
        String prefix = (String) getAnnotationObjectValue(annotation, "nsPrefix");
        String namespaceURI = (String) getAnnotationObjectValue(annotation, "nsURI");
        return ECQLBuilder.builder()
                .withExpression(expression)
                .withNsPrefix(prefix)
                .withNsURI(namespaceURI)
                .build();
    }
}
