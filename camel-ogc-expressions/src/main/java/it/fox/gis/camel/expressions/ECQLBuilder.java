package it.fox.gis.camel.expressions;

import org.apache.commons.lang3.StringUtils;
import org.geotools.factory.CommonFactoryFinder;
import org.geotools.filter.text.cql2.CQLException;
import org.geotools.filter.text.ecql.ECQL;
import org.geotools.filter.visitor.DuplicatingFilterVisitor;
import org.opengis.filter.Filter;
import org.opengis.filter.FilterFactory2;
import org.opengis.filter.expression.Expression;
import org.opengis.filter.expression.PropertyName;
import org.xml.sax.helpers.NamespaceSupport;

/** Builder class for an ECQL Camel expression. */
public class ECQLBuilder {

    private static final FilterFactory2 FF = CommonFactoryFinder.getFilterFactory2();

    private String filter;

    private String expression;

    private String nsURI;

    private String nsPrefix;

    public static ECQLBuilder builder() {
        return new ECQLBuilder();
    }

    /**
     * Add the string ECQL expression to the builder.
     *
     * @param expression the string ECQL expression.
     * @return this builder.
     */
    public ECQLBuilder withExpression(String expression) {
        this.expression = expression;
        return this;
    }

    /**
     * Add the string ECQL filter to the builder.
     *
     * @param filter the string ECQL filter.
     * @return this builder.
     */
    public ECQLBuilder withFilter(String filter) {
        this.filter = filter;
        return this;
    }

    /**
     * Adds the namespace URI to the builder.
     *
     * @param nsURI the nsURI.
     * @return this builder.
     */
    public ECQLBuilder withNsURI(String nsURI) {
        this.nsURI = nsURI;
        return this;
    }

    /**
     * Adds the namespace prefix to this builder.
     *
     * @param nsPrefix the namespace prefix.
     * @return this builder.
     */
    public ECQLBuilder withNsPrefix(String nsPrefix) {
        this.nsPrefix = nsPrefix;
        return this;
    }

    /**
     * Builds an ECQL Camel expression.
     *
     * @return an {@link ECQLCamelExpressionAdapter} instance.
     */
    public ECQLCamelExpressionAdapter build() {
        try {
            if (StringUtils.isNotBlank(expression)) return buildExpression();
            else if (StringUtils.isNotBlank(filter)) return buildFilter();
            else
                throw new RuntimeException(
                        "Neither a filter neither an expression were provided...");
        } catch (CQLException e) {
            throw new RuntimeException("Invalid ECQL. Error is " + e.getMessage(), e);
        }
    }

    private ECQLCamelExpressionAdapter buildExpression() throws CQLException {
        Expression result = ECQL.toExpression(expression);
        if (hasNs())
            result = (Expression) result.accept(new NamespaceInjectVisitor(nsURI, nsPrefix), null);
        return new ECQLCamelExpressionAdapter(result);
    }

    private ECQLCamelExpressionAdapter buildFilter() throws CQLException {
        Filter result = ECQL.toFilter(filter);
        if (hasNs())
            result = (Filter) result.accept(new NamespaceInjectVisitor(nsURI, nsPrefix), null);
        return new ECQLCamelExpressionAdapter(result);
    }

    private boolean hasNs() {
        return StringUtils.isNotBlank(nsPrefix) && StringUtils.isNotBlank(nsURI);
    }

    private class NamespaceInjectVisitor extends DuplicatingFilterVisitor {
        private NamespaceSupport namespaceSupport;

        private NamespaceInjectVisitor(String namespaceURI, String nsPrefix) {
            this.namespaceSupport = new NamespaceSupport();
            namespaceSupport.declarePrefix(nsPrefix, namespaceURI);
        }

        @Override
        public Object visit(PropertyName expression, Object extraData) {
            return FF.property(expression.getPropertyName(), namespaceSupport);
        }
    }
}
