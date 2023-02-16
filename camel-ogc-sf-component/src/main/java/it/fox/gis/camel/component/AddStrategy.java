package it.fox.gis.camel.component;

import java.io.IOException;
import java.util.List;
import java.util.stream.Collectors;
import org.apache.camel.Exchange;
import org.geotools.data.Query;
import org.geotools.data.simple.SimpleFeatureCollection;
import org.geotools.data.simple.SimpleFeatureSource;
import org.geotools.data.simple.SimpleFeatureStore;
import org.opengis.filter.identity.FeatureId;
import org.opengis.referencing.crs.CoordinateReferenceSystem;

class AddStrategy extends AbstractFeatureComponentStrategy {
    @Override
    public void setMessage(
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
        }
    }
}
