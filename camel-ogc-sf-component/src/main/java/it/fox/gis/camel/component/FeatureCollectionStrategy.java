package it.fox.gis.camel.component;

import org.apache.camel.Exchange;
import org.geotools.data.Query;
import org.geotools.data.simple.SimpleFeatureSource;
import org.opengis.referencing.crs.CoordinateReferenceSystem;

/**
 * Strategy implementation that sets a {@link org.geotools.data.simple.SimpleFeatureCollection} as
 * the message body.
 */
class FeatureCollectionStrategy extends AbstractFeatureComponentStrategy {

    @Override
    public void setMessage(
            Exchange exchange,
            SimpleFeatureSource featureSource,
            Query query,
            CoordinateReferenceSystem crs) {
        exchange.getMessage().setBody(reproject(collection(featureSource, query), crs));
    }
}
