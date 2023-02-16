package it.fox.gis.camel.component;

import org.apache.camel.Endpoint;
import org.apache.camel.Exchange;
import org.apache.camel.support.DefaultProducer;
import org.geotools.data.DataStore;
import org.geotools.data.Query;
import org.geotools.data.simple.SimpleFeatureSource;

/** Simple Features producer. Can read and write to a features' storage. */
public class SimpleFeatureProducer extends DefaultProducer {

    public SimpleFeatureProducer(Endpoint endpoint) {
        super(endpoint);
        assert endpoint instanceof SimpleFeaturesEndpoint;
    }

    @Override
    public void process(Exchange exchange) throws Exception {
        SimpleFeaturesEndpoint sfe = (SimpleFeaturesEndpoint) getEndpoint();

        DataStore dataStore =
                sfe.getRegistry().loadDataStore(sfe.getDataStoreName(), sfe.getPropertiesURI());
        SimpleFeatureSource sfs = dataStore.getFeatureSource(sfe.getFeatureType());
        FeatureComponentStrategy strategy =
                FeatureComponentStrategyFactory.createStrategy(
                        this, sfe.getOperation(), sfe.getResultType());
        Query query =
                new QueryHelper(sfe.getFeatureType(), sfe.getCqlQuery())
                        .buildFinalQuery(exchange.getIn().getBody());
        strategy.setMessage(exchange, sfs, query, sfe.getCrs());
    }
}
