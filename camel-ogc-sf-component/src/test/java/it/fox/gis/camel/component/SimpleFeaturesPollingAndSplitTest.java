package it.fox.gis.camel.component;

import static org.junit.jupiter.api.Assertions.assertInstanceOf;

import java.util.List;
import org.apache.camel.Exchange;
import org.apache.camel.builder.RouteBuilder;
import org.apache.camel.component.mock.MockEndpoint;
import org.geotools.data.simple.SimpleFeatureIterator;
import org.junit.jupiter.api.Test;
import org.opengis.feature.simple.SimpleFeature;
import org.springframework.jdbc.datasource.embedded.EmbeddedDatabaseBuilder;

public class SimpleFeaturesPollingAndSplitTest extends BaseTestSupport {

    @Override
    protected EmbeddedDatabaseBuilder configure(EmbeddedDatabaseBuilder dbBuilder) {
        dbBuilder.addScript("sql/setup.sql");
        return super.configure(dbBuilder);
    }

    @Test
    public void testPollingAndSplit() throws InterruptedException {
        MockEndpoint endpoint = MockEndpoint.resolve(context, "mock:test");
        endpoint.setExpectedCount(1);
        endpoint.assertIsSatisfied();
        List<Exchange> exchanges = endpoint.getReceivedExchanges();
        assertInstanceOf(SimpleFeatureIterator.class, exchanges.get(0).getMessage().getBody());
        MockEndpoint endpoint2 = MockEndpoint.resolve(context, "mock:test2");
        endpoint2.setExpectedCount(4);
        endpoint2.assertIsSatisfied();
        endpoint2
                .getExchanges()
                .forEach(e -> assertFeature(e.getMessage().getBody(SimpleFeature.class)));
    }

    @Override
    protected RouteBuilder createRouteBuilder() {
        String propsURI = getClass().getResource("datastore.properties").getFile();
        return new RouteBuilder() {
            @Override
            public void configure() {
                from("ogc-sf:test-h2-store?featureType=ft1&resultType=ITERATOR&repeatCount=1&propertiesURI="
                                + propsURI)
                        .to("mock:test")
                        .split()
                        .body()
                        .to("mock:test2");
            }
        };
    }
}
