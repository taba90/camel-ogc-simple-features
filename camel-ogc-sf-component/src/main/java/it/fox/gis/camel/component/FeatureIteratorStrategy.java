package it.fox.gis.camel.component;

import org.apache.camel.Exchange;
import org.apache.camel.ExtendedExchange;
import org.apache.camel.Service;
import org.apache.camel.support.SynchronizationAdapter;
import org.geotools.data.Query;
import org.geotools.data.simple.SimpleFeatureCollection;
import org.geotools.data.simple.SimpleFeatureIterator;
import org.geotools.data.simple.SimpleFeatureSource;
import org.opengis.referencing.crs.CoordinateReferenceSystem;

/**
 * A {@link FeatureComponentStrategy} implementation that returns a {@link SimpleFeatureIterator}
 * that implement the Iterator interface as well. Allows to parse Features in a streaming fashion if
 * EIP split is used.
 */
class FeatureIteratorStrategy extends AbstractFeatureComponentStrategy {
    public FeatureIteratorStrategy(Service service) {
        super(service);
    }

    @Override
    public int setMessage(
            Exchange exchange,
            SimpleFeatureSource source,
            Query query,
            CoordinateReferenceSystem crs) {
        SimpleFeatureCollection collection = reproject(collection(source, query), crs);
        streamingMessage(new CamelSimpleFeaturesIterator(collection.features()), exchange);
        processExchange(exchange);
        return 1;
    }

    private void streamingMessage(SimpleFeatureIterator iterator, Exchange exchange) {
        exchange.adapt(ExtendedExchange.class)
                .addOnCompletion(
                        new SynchronizationAdapter() {
                            @Override
                            public void onComplete(Exchange exchange) {
                                super.onComplete(exchange);
                                iterator.close();
                            }

                            @Override
                            public void onFailure(Exchange exchange) {
                                super.onFailure(exchange);
                                iterator.close();
                            }
                        });
        exchange.getMessage().setBody(new CamelSimpleFeaturesIterator(iterator));
    }
}
