package it.fox.gis.camel.component;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.List;
import org.apache.camel.Exchange;
import org.apache.camel.builder.RouteBuilder;
import org.apache.camel.component.mock.MockEndpoint;
import org.junit.jupiter.api.Test;
import org.opengis.feature.simple.SimpleFeature;
import org.springframework.jdbc.datasource.embedded.EmbeddedDatabaseBuilder;

public class SimpleFeaturesDeleteTest extends BaseTestSupport {

    @Override
    protected EmbeddedDatabaseBuilder configure(EmbeddedDatabaseBuilder dbBuilder) {
        dbBuilder.addScript("sql/setup.sql");
        return super.configure(dbBuilder);
    }

    @Test
    public void testDelete() throws InterruptedException {
        MockEndpoint endpoint = MockEndpoint.resolve(context, "mock:test-delete");
        endpoint.setExpectedCount(1);
        endpoint.assertIsSatisfied();
        Exchange exchange = endpoint.getExchanges().get(0);
        @SuppressWarnings("uncheked")
        List<SimpleFeature> results = exchange.getIn().getBody(List.class);
        assertEquals(2, results.size());
    }

    @Override
    protected RouteBuilder createRouteBuilder() {
        String propsURI = getClass().getResource("datastoreDelete.properties").getFile();
        return new RouteBuilder() {
            @Override
            public void configure() {
                from("ogc-sf:test-h2-store4?featureType=ft1&repeatCount=1&cqlQuery=stringProperty IN ('zero','one')&operation=DELETE&propertiesURI="
                                + propsURI)
                        .to(
                                "ogc-sf:test-h2-store4?featureType=ft1&operation=GET&propertiesURI="
                                        + propsURI)
                        .to("mock:test-delete");
            }
        };
    }
}
