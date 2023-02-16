package it.fox.gis.camel.component;

import org.apache.camel.Exchange;
import org.apache.camel.Service;
import org.geotools.data.Query;
import org.geotools.data.simple.SimpleFeatureSource;
import org.opengis.referencing.crs.CoordinateReferenceSystem;

/**
 * Strategy implementation that sets a {@link org.geotools.data.simple.SimpleFeatureCollection} as
 * the message body.
 */
class FeatureCollectionStrategy extends AbstractFeatureComponentStrategy {

    public FeatureCollectionStrategy(Service service) {
        super(service);
    }

    @Override
    public int setMessage(
            Exchange exchange,
            SimpleFeatureSource featureSource,
            Query query,
            CoordinateReferenceSystem crs) {
        exchange.getMessage().setBody(reproject(collection(featureSource, query), crs));
        processExchange(exchange);
        return 1;
    }
}
