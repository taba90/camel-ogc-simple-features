package it.fox.gis.camel.component;

import org.apache.camel.Exchange;
import org.geotools.data.Query;
import org.geotools.data.simple.SimpleFeatureSource;
import org.opengis.referencing.crs.CoordinateReferenceSystem;

/**
 * Basic interface for a {@link FeatureComponentStrategy}. It defines the type of message set to the
 * Exchange.
 */
public interface FeatureComponentStrategy {

    /**
     * Set the body to the message in the Camel Exchange.
     *
     * @param exchange the exchange.
     * @param source the {@link SimpleFeatureSource} instance provided by the producer or consumer.
     * @param query a {@link Query} object.
     * @param crs the target {@link CoordinateReferenceSystem}.
     */
    void setMessage(
            Exchange exchange,
            SimpleFeatureSource source,
            Query query,
            CoordinateReferenceSystem crs);
}
