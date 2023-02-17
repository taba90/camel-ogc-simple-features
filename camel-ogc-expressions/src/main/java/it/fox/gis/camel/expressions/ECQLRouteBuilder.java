package it.fox.gis.camel.expressions;

import org.apache.camel.Expression;
import org.apache.camel.builder.RouteBuilder;
import org.apache.camel.builder.ValueBuilder;

/**
 * Custom abstraction of a route builder to allow usage of some convenience method to create ECQL
 * expressions and predicates.
 */
public abstract class ECQLRouteBuilder extends RouteBuilder {

    /**
     * Get an ECQL expression out of its string representation.
     *
     * @param value the string ECQL expression.
     * @return a {@link ValueBuilder} wrapping the ECQL expression.
     */
    public ValueBuilder ecqlExpression(String value) {
        return ecqlExpression(value, null, null);
    }

    public ValueBuilder ecqlExpression(String value, String nsURI, String nsPrefix) {
        return new ValueBuilder(
                ECQLBuilder.builder()
                        .withExpression(value)
                        .withNsURI(nsURI)
                        .withNsPrefix(nsPrefix)
                        .build());
    }

    /**
     * Get an ECQL filter out of its string representation.
     *
     * @param value the string ECQL filter.
     * @return a {@link ValueBuilder} wrapping the ECQL filter.
     */
    public ValueBuilder ecqlFilter(String value) {
        return ecqlFilter(value, null, null);
    }

    /**
     * Get an ECQL filter out of its string representation.
     *
     * @param value the string ECQL filter.
     * @param nsURI the namespace URI.
     * @param nsPrefix the namespace prefix.
     * @return a {@link ValueBuilder} wrapping the ECQL filter.
     */
    public ValueBuilder ecqlFilter(String value, String nsURI, String nsPrefix) {
        return new ValueBuilder(
                ECQLBuilder.builder()
                        .withFilter(value)
                        .withNsURI(nsURI)
                        .withNsPrefix(nsPrefix)
                        .build());
    }

    /**
     * @param expression the ECQL filter or expression as a camel expression.
     * @return a {@link ValueBuilder} wrapping the expression object.
     */
    public ValueBuilder ecql(Expression expression) {
        return ecql(expression, null, null);
    }

    /**
     * @param expression the ECQL filter or expression as a camel expression.
     * @param nsURI the namespace URI.
     * @param nsPrefix the namespace prefix.
     * @return a {@link ValueBuilder} wrapping the expression object.
     */
    public ValueBuilder ecql(Expression expression, String nsURI, String nsPrefix) {
        ECQLExpressionDefinition expr = new ECQLExpressionDefinition(expression);
        expr.setNsPrefix(nsPrefix);
        expr.setNsURI(nsURI);
        return new ValueBuilder(expr);
    }
}
