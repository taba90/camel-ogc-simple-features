package it.fox.gis.camel.component;

import org.geotools.data.Query;
import org.geotools.factory.CommonFactoryFinder;
import org.geotools.filter.text.cql2.CQLException;
import org.geotools.filter.text.ecql.ECQL;
import org.opengis.filter.Filter;
import org.opengis.filter.FilterFactory2;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/** Utility class meant to provide utility methods to deal with {@link Query} instance. */
class QueryHelper {

    private static final FilterFactory2 FF = CommonFactoryFinder.getFilterFactory2();
    private static final Logger LOGGER = LoggerFactory.getLogger(SimpleFeatureProducer.class);
    private String featureType;
    private Query endpointQuery;

    QueryHelper(String featureType, Query endpointQuery) {
        this.featureType = featureType;
        this.endpointQuery = endpointQuery;
    }

    /**
     * Check the message object. If it is a String will try to convert it to a CQL filter and merge
     * it with an eventual URI CQL filter using AND operator.
     *
     * @param message the body message.
     * @return the {@link Query} instance.
     */
    Query buildFinalQuery(Object message) {
        if (message != null && String.class.isAssignableFrom(message.getClass())) {
            String cql = ((String) message);
            try {
                Filter filter = ECQL.toFilter(cql);
                if (endpointQuery.getFilter() != null) {
                    // a filter coming from the endpoint and one from the body...
                    // let's be permissive and concatenate as OR
                    filter = FF.or(filter, endpointQuery.getFilter());
                }
                endpointQuery.setFilter(filter);
                endpointQuery.setTypeName(featureType);
            } catch (CQLException e) {
                LOGGER.error(
                        "Error while trying to read the body as a CQL filter. Ignoring it...", e);
            }
        }
        return endpointQuery;
    }
}
