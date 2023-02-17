package it.fox.gis.camel.expressions;

import org.apache.camel.Expression;
import org.apache.camel.Predicate;
import org.apache.camel.spi.annotations.Language;
import org.apache.camel.support.SingleInputTypedLanguageSupport;

/** Language Support for ogcECQL syntax. */
@Language("ogcECQL")
public class OGCExpressionLanguage extends SingleInputTypedLanguageSupport {
    @Override
    public Predicate createPredicate(String expression) {
        return createPredicate(expression, null);
    }

    @Override
    public Expression createExpression(String expression) {
        return ECQLBuilder.builder().withExpression(expression).build();
    }

    @Override
    public Predicate createPredicate(String expression, Object[] properties) {
        String nsURI = null;
        String nsPrefix = null;
        if (properties != null && properties.length > 1) {
            nsURI = (String) properties[0];
            nsPrefix = (String) properties[1];
        }
        return ECQLBuilder.builder()
                .withFilter(expression)
                .withNsPrefix(nsPrefix)
                .withNsURI(nsURI)
                .build();
    }
}
