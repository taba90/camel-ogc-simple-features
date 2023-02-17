package it.fox.gis.camel.expressions;

import org.apache.camel.CamelContext;
import org.apache.camel.Exchange;
import org.apache.camel.support.ExpressionAdapter;

/**
 * An {@link ExpressionAdapter} to adapt ECQL expression and filters to Camel Expression and
 * predicates.
 */
class ECQLCamelExpressionAdapter extends ExpressionAdapter {

    private org.opengis.filter.expression.Expression expression;
    private org.opengis.filter.Filter filter;

    ECQLCamelExpressionAdapter(org.opengis.filter.expression.Expression expression) {
        assert expression != null;
        this.expression = expression;
    }

    ECQLCamelExpressionAdapter(org.opengis.filter.Filter filter) {
        assert filter != null;
        this.filter = filter;
    }

    @Override
    public <T> T evaluate(Exchange exchange, Class<T> type) {
        Object object = exchange.getIn().getBody();
        return expression.evaluate(object, type);
    }

    @Override
    public boolean matches(Exchange exchange) {
        Object object = exchange.getIn().getBody();
        return filter.evaluate(object);
    }

    @Override
    public void init(CamelContext context) {
        super.init(context);
    }

    @Override
    public void initPredicate(CamelContext context) {
        super.initPredicate(context);
    }
}
