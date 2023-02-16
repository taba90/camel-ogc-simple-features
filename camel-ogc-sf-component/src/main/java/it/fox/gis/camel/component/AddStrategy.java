package it.fox.gis.camel.component;

import java.io.IOException;
import java.util.List;
import java.util.stream.Collectors;
import org.apache.camel.Exchange;
import org.apache.camel.Service;
import org.geotools.data.Query;
import org.geotools.data.simple.SimpleFeatureCollection;
import org.geotools.data.simple.SimpleFeatureSource;
import org.geotools.data.simple.SimpleFeatureStore;
import org.opengis.filter.identity.FeatureId;
import org.opengis.referencing.crs.CoordinateReferenceSystem;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

class AddStrategy extends AbstractFeatureComponentStrategy {
    private static final Logger LOGGER = LoggerFactory.getLogger(AddStrategy.class);

    public AddStrategy(Service service) {
        super(service);
    }

    @Override
    public int setMessage(
            Exchange exchange,
            SimpleFeatureSource source,
            Query query,
            CoordinateReferenceSystem crs) {
        Object object = exchange.getIn().getBody();
        assert object instanceof SimpleFeatureCollection;
        SimpleFeatureCollection insertingFeatures = (SimpleFeatureCollection) object;
        assert source instanceof SimpleFeatureStore;
        SimpleFeatureStore simpleFeatureStore = (SimpleFeatureStore) source;
        try {
            List<FeatureId> insertedId = simpleFeatureStore.addFeatures(insertingFeatures);
            exchange.getMessage()
                    .setBody(
                            insertedId.stream()
                                    .map(fid -> fid.getID())
                                    .collect(Collectors.toList()));
        } catch (IOException e) {
            LOGGER.error("Error while inserting features. Error is " + e.getMessage(), e);
            exchange.setException(e);
        }
        return 1;
    }
}
