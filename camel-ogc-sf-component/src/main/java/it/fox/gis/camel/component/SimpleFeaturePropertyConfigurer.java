package it.fox.gis.camel.component;

import java.util.Optional;
import org.apache.camel.CamelContext;
import org.apache.camel.spi.PropertyConfigurer;
import org.geotools.data.Query;
import org.geotools.filter.text.cql2.CQLException;
import org.geotools.filter.text.ecql.ECQL;
import org.geotools.util.Converters;
import org.opengis.filter.Filter;
import org.opengis.referencing.crs.CoordinateReferenceSystem;

public class SimpleFeaturePropertyConfigurer implements PropertyConfigurer {
    static final String RESULT_TYPE = "resultType";
    static final String OPERATION = "operation";

    static final String CQL_QUERY = "cqlQuery";

    static final String FEATURE_TYPE = "featureType";

    static final String PROPERTIES_URI = "propertiesURI";

    static final String CRS = "crs";

    @Override
    public boolean configure(
            CamelContext camelContext,
            Object target,
            String name,
            Object value,
            boolean ignoreCase) {
        SimpleFeaturesEndpoint sfe = (SimpleFeaturesEndpoint) target;
        boolean result = true;
        switch (name) {
            case RESULT_TYPE:
                sfe.setResultType(
                        ResultType.valueOf(convertOrDefault(value, String.class, "LIST")));
                break;
            case OPERATION:
                sfe.setOperation(Operation.valueOf(convertOrDefault(value, String.class, "GET")));
                break;
            case FEATURE_TYPE:
                sfe.setFeatureType(convert(value, String.class));
                break;
            case CRS:
                sfe.setCrs(convert(value, CoordinateReferenceSystem.class));
                break;
            case PROPERTIES_URI:
                sfe.setPropertiesURI(
                        optional(value, String.class)
                                .orElseThrow(
                                        () ->
                                                new UnsupportedOperationException(
                                                        "propertiesFileURI cannot be NULL")));
                break;
            case CQL_QUERY:
                Query query = new Query();
                query.setFilter(filter(value));
                sfe.setCqlQuery(query);
                break;
            default:
                result = false;
                break;
        }

        return result;
    }

    private <T> T convert(Object value, Class<T> type) {
        return convertOrDefault(value, type, null);
    }

    private <T> Optional<T> optional(Object value, Class<T> type) {
        return Optional.ofNullable(convertOrDefault(value, type, null));
    }

    private Filter filter(Object cql) {
        try {
            String strCQL = convert(cql, String.class);
            if (strCQL == null) return Filter.INCLUDE;
            return ECQL.toFilter(strCQL);
        } catch (CQLException e) {
            throw new RuntimeException(e);
        }
    }

    private <T> T convertOrDefault(Object value, Class<T> type, T defaultVal) {
        return Optional.ofNullable(Converters.convert(value, type)).orElse(defaultVal);
    }
}
