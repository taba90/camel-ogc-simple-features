package it.fox.gis.camel.component;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.List;
import org.apache.camel.Endpoint;
import org.apache.camel.Exchange;
import org.apache.camel.builder.RouteBuilder;
import org.junit.jupiter.api.Test;
import org.opengis.feature.simple.SimpleFeature;
import org.springframework.jdbc.datasource.embedded.EmbeddedDatabaseBuilder;

public class SimpleFeaturesProducerFilteringTest extends BaseTestSupport {

    @Override
    protected EmbeddedDatabaseBuilder configure(EmbeddedDatabaseBuilder dbBuilder) {
        dbBuilder.addScript("sql/setup.sql");
        return super.configure(dbBuilder);
    }

    @Test
    public void testProducerWithFilter() {
        Endpoint endpoint = context.getEndpoint("direct:start");
        Exchange exchange = endpoint.createExchange();
        exchange.getIn().setBody("doubleProperty=1.1");
        Exchange out = template.send(endpoint, exchange);
        List<SimpleFeature> features = out.getIn().getBody(List.class);
        assertEquals(2, features.size());
    }

    @Override
    protected RouteBuilder createRouteBuilder() {
        String propsURI = getClass().getResource("datastoreFiltering.properties").getFile();
        return new RouteBuilder() {
            @Override
            public void configure() {
                from("direct:start")
                        .to(
                                "ogc-sf:test-h2-store2?featureType=ft1&repeatCount=1&cqlQuery=stringProperty = 'zero'&propertiesURI="
                                        + propsURI);
            }
        };
    }
}
