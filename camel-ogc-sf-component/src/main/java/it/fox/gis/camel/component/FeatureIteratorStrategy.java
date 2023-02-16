package it.fox.gis.camel.component;

import org.apache.camel.Exchange;
import org.apache.camel.ExtendedExchange;
import org.apache.camel.support.SynchronizationAdapter;
import org.geotools.data.Query;
import org.geotools.data.simple.SimpleFeatureCollection;
import org.geotools.data.simple.SimpleFeatureIterator;
import org.geotools.data.simple.SimpleFeatureSource;
import org.opengis.referencing.crs.CoordinateReferenceSystem;

/**
 * A {@link FeatureComponentStrategy} implementation that returns a {@link SimpleFeatureIterator}
 * that implement the Iterator interface as well. Allows to parse Feature in a streaming fashion.
 */
class FeatureIteratorStrategy extends AbstractFeatureComponentStrategy {
    @Override
    public void setMessage(
            Exchange exchange,
            SimpleFeatureSource source,
            Query query,
            CoordinateReferenceSystem crs) {
        SimpleFeatureCollection collection = reproject(collection(source, query), crs);
        streamingMessage(new CamelSimpleFeaturesIterator(collection.features()), exchange);
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
