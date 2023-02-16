package it.fox.gis.camel.component;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import java.util.List;
import org.apache.camel.builder.RouteBuilder;
import org.apache.camel.component.mock.MockEndpoint;
import org.junit.jupiter.api.Test;
import org.opengis.feature.simple.SimpleFeature;
import org.springframework.jdbc.datasource.embedded.EmbeddedDatabaseBuilder;

public class SimpleFeaturesInsertionTest extends BaseTestSupport {

    @Test
    public void testInsertion() throws InterruptedException {
        MockEndpoint endpoint = MockEndpoint.resolve(context, "mock:test-insert");
        endpoint.setExpectedCount(1);
        endpoint.assertIsSatisfied();
        @SuppressWarnings("unchecked")
        List<SimpleFeature> result = endpoint.getExchanges().get(0).getIn().getBody(List.class);
        assertEquals(4, result.size());
        result.forEach(f -> assertFeature(f));
    }

    private void assertFeature(SimpleFeature simpleFeature) {
        assertNotNull(simpleFeature.getDefaultGeometry());
        assertNotNull(simpleFeature.getAttribute("stringProperty"));
        assertNotNull(simpleFeature.getAttribute("doubleProperty"));
        assertNotNull(simpleFeature.getAttribute("intProperty"));
    }

    @Override
    protected EmbeddedDatabaseBuilder configure(EmbeddedDatabaseBuilder dbBuilder) {
        dbBuilder.addScript("sql/setup2.sql");
        return super.configure(dbBuilder);
    }

    @Override
    protected RouteBuilder createRouteBuilder() {
        String propsURI = getClass().getResource("datastoreInsertion.properties").getFile();
        return new RouteBuilder() {
            @Override
            public void configure() {
                from("ogc-sf:test-h2-store3?featureType=ft1&repeatCount=1&resultType=COLLECTION&propertiesURI="
                                + propsURI)
                        .to(
                                "ogc-sf:test-h2-store3?featureType=ft2&operation=ADD&propertiesURI="
                                        + propsURI)
                        .to(
                                "ogc-sf:test-h2-store3?featureType=ft2&resultType=LIST&propertiesURI="
                                        + propsURI)
                        .to("mock:test-insert");
            }
        };
    }
}
