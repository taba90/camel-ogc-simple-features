package it.fox.gis.camel.component;

import java.io.IOException;
import org.apache.camel.Exchange;
import org.geotools.data.Query;
import org.geotools.data.simple.SimpleFeatureSource;
import org.geotools.data.simple.SimpleFeatureStore;
import org.opengis.referencing.crs.CoordinateReferenceSystem;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/** Strategy implementations to delete features from a {@link SimpleFeatureStore} */
class DeleteStrategy extends AbstractFeatureComponentStrategy {

    private static final Logger LOGGER = LoggerFactory.getLogger(DeleteStrategy.class);

    @Override
    public void setMessage(
            Exchange exchange,
            SimpleFeatureSource source,
            Query query,
            CoordinateReferenceSystem crs) {
        assert source instanceof SimpleFeatureStore;
        SimpleFeatureStore simpleFeatureStore = (SimpleFeatureStore) source;
        try {
            int size = simpleFeatureStore.getFeatures(query.getFilter()).size();
            simpleFeatureStore.removeFeatures(query.getFilter());
            exchange.getMessage().setBody(size);
        } catch (IOException e) {
            LOGGER.error("Error while deleting features. Error is " + e.getMessage(), e);
            exchange.setException(e);
        }
    }
}
