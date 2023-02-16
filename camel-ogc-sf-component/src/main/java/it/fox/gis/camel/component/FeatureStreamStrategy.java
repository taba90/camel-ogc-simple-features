package it.fox.gis.camel.component;

import org.apache.camel.Consumer;
import org.apache.camel.Exchange;
import org.apache.camel.Service;
import org.geotools.data.Query;
import org.geotools.data.simple.SimpleFeatureCollection;
import org.geotools.data.simple.SimpleFeatureIterator;
import org.geotools.data.simple.SimpleFeatureSource;
import org.opengis.feature.simple.SimpleFeature;
import org.opengis.referencing.crs.CoordinateReferenceSystem;

/**
 * A {@link FeatureComponentStrategy} that stream SimpleFeatures. It is supported only with
 * Consumer.
 */
class FeatureStreamStrategy extends AbstractFeatureComponentStrategy {
    public FeatureStreamStrategy(Service service) {
        super(service);
        assert service instanceof Consumer;
    }

    @Override
    public int setMessage(
            Exchange exchange,
            SimpleFeatureSource source,
            Query query,
            CoordinateReferenceSystem crs) {
        SimpleFeatureCollection coll = reproject(collection(source, query), crs);
        int counter = 0;
        Consumer pollingConsumer = getConsumer();
        try (SimpleFeatureIterator iterator = coll.features()) {
            while (iterator.hasNext()) {
                SimpleFeature sf = iterator.next();
                exchange.getIn().setBody(sf);
                processExchange(exchange);
                counter++;
                if (iterator.hasNext()) exchange = pollingConsumer.createExchange(false);
            }
        }
        return counter;
    }
}
