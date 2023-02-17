package it.fox.gis.camel.expressions;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import it.fox.gis.camel.component.BaseTestSupport;
import org.apache.camel.builder.RouteBuilder;
import org.apache.camel.component.mock.MockEndpoint;
import org.junit.jupiter.api.Test;
import org.locationtech.jts.geom.Geometry;
import org.opengis.feature.simple.SimpleFeature;
import org.springframework.jdbc.datasource.embedded.EmbeddedDatabaseBuilder;

public class ECQLRouteTest extends BaseTestSupport {

    @Override
    protected EmbeddedDatabaseBuilder configure(EmbeddedDatabaseBuilder dbBuilder) {
        dbBuilder.addScript("sql/setup.sql");
        return super.configure(dbBuilder);
    }

    @Test
    public void testECQLExpressionAnnotation() throws InterruptedException {
        MockEndpoint endpoint = MockEndpoint.resolve(context, "mock:test");
        endpoint.setExpectedCount(1);
        endpoint.assertIsSatisfied();
        SimpleFeature sf = endpoint.getExchanges().get(0).getIn().getBody(SimpleFeature.class);
        assertEquals("zero", sf.getAttribute("stringProperty"));
    }

    @Override
    protected RouteBuilder createRouteBuilder() {
        String propsURI = getClass().getResource("datastoreRoute.properties").getFile();
        return new ECQLRouteBuilder() {
            @Override
            public void configure() {
                from("ogc-sf:test-h2-store-route?featureType=ft1&resultType=STREAM&repeatCount=1&propertiesURI="
                                + propsURI)
                        .filter(ecqlFilter("stringProperty = 'zero'"))
                        .to("mock:test");
            }
        };
    }

    static class ExpressionBean {

        public String concatProperty(
                @ECQLExpression("buffer(location,10)") Geometry buffered,
                @ECQLExpression("location") Geometry geometry) {
            assertTrue(buffered.getCoordinates().length > 0);
            assertTrue(geometry.getCoordinates().length > 0);
            return "Original geometry " + geometry.toText() + " buffered to " + buffered.toText();
        }
    }
}
