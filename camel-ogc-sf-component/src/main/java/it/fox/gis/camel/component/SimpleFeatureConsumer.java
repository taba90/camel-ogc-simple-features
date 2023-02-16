package it.fox.gis.camel.component;

import java.io.IOException;
import org.apache.camel.Endpoint;
import org.apache.camel.Exchange;
import org.apache.camel.Processor;
import org.apache.camel.support.ScheduledPollConsumer;
import org.geotools.data.DataStore;
import org.geotools.data.Query;
import org.geotools.data.simple.SimpleFeatureSource;

/** Simple Features scheduled consumer. */
public class SimpleFeatureConsumer extends ScheduledPollConsumer {
    public SimpleFeatureConsumer(Endpoint endpoint, Processor processor) {
        super(endpoint, processor);
    }

    @Override
    protected int poll() throws Exception {
        Exchange exchange = createExchange(false);
        SimpleFeaturesEndpoint sfe = (SimpleFeaturesEndpoint) getEndpoint();
        FeatureComponentStrategy strategy =
                FeatureComponentStrategyFactory.createStrategy(
                        this, sfe.getOperation(), sfe.getResultType());
        DataStore store =
                sfe.getRegistry().loadDataStore(sfe.getDataStoreName(), sfe.getPropertiesURI());
        SimpleFeatureSource sfs = store.getFeatureSource(sfe.getFeatureType());
        Query query =
                new QueryHelper(sfe.getFeatureType(), sfe.getCqlQuery())
                        .buildFinalQuery(exchange.getIn().getBody());
        strategy.setMessage(exchange, sfs, query, sfe.getCrs());
        int result = sfs.getFeatures(query).size();
        return result;
    }

    @Override
    public void close() throws IOException {
        super.close();
    }

    @Override
    public void afterPoll() throws Exception {
        super.afterPoll();
    }
}
