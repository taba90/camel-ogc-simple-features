package it.fox.gis.camel.component;

import java.io.IOException;
import java.util.Map;
import org.apache.camel.Exchange;
import org.apache.camel.Service;
import org.geotools.data.Query;
import org.geotools.data.simple.SimpleFeatureSource;
import org.geotools.data.simple.SimpleFeatureStore;
import org.geotools.feature.NameImpl;
import org.opengis.feature.type.Name;
import org.opengis.referencing.crs.CoordinateReferenceSystem;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ModifyStrategy extends AbstractFeatureComponentStrategy {

    private static final Logger LOGGER = LoggerFactory.getLogger(ModifyStrategy.class);

    protected ModifyStrategy(Service service) {
        super(service);
    }

    @Override
    public int setMessage(
            Exchange exchange,
            SimpleFeatureSource source,
            Query query,
            CoordinateReferenceSystem crs) {
        Object object = exchange.getIn().getBody();
        assert object instanceof Map;
        Map<String, Object> updatingAttributes = (Map<String, Object>) object;
        assert source instanceof SimpleFeatureStore;
        SimpleFeatureStore simpleFeatureStore = (SimpleFeatureStore) source;
        Name[] names = new Name[updatingAttributes.size()];
        Object[] values = new Object[updatingAttributes.size()];
        int i = 0;
        for (String k : updatingAttributes.keySet()) {
            Object o = updatingAttributes.get(k);
            names[i] = new NameImpl(simpleFeatureStore.getName().getNamespaceURI(), k);
            values[i] = o;
        }
        try {
            simpleFeatureStore.modifyFeatures(names, values, query.getFilter());
        } catch (IOException e) {
            LOGGER.error(
                    "Error while modifying features for featureType "
                            + query.getTypeName()
                            + ". Error is "
                            + e.getMessage(),
                    e);
            exchange.setException(e);
        }
        return 1;
    }
}
