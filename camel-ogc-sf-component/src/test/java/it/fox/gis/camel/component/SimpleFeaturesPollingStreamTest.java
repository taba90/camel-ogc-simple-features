package it.fox.gis.camel.component;

import org.apache.camel.builder.RouteBuilder;
import org.apache.camel.component.mock.MockEndpoint;
import org.junit.jupiter.api.Test;
import org.opengis.feature.simple.SimpleFeature;
import org.springframework.jdbc.datasource.embedded.EmbeddedDatabaseBuilder;

public class SimpleFeaturesPollingStreamTest extends BaseTestSupport {

    @Override
    protected EmbeddedDatabaseBuilder configure(EmbeddedDatabaseBuilder dbBuilder) {
        dbBuilder.addScript("sql/setup.sql");
        return super.configure(dbBuilder);
    }

    @Test
    public void testPollingAndStream() throws InterruptedException {
        MockEndpoint endpoint = MockEndpoint.resolve(context, "mock:test");
        endpoint.setExpectedCount(4);
        endpoint.assertIsSatisfied();
        endpoint.getExchanges()
                .forEach(e -> assertFeature(e.getMessage().getBody(SimpleFeature.class)));
    }

    @Override
    protected RouteBuilder createRouteBuilder() {
        String propsURI = getClass().getResource("datastoreStreaming.properties").getFile();
        return new RouteBuilder() {
            @Override
            public void configure() {
                from("ogc-sf:test-h2-stream?featureType=ft1&resultType=STREAM&repeatCount=1&propertiesURI="
                                + propsURI)
                        .to("mock:test");
            }
        };
    }
}
