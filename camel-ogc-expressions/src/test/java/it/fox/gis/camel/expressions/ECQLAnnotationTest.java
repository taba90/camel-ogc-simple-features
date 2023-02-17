package it.fox.gis.camel.expressions;

import static org.junit.jupiter.api.Assertions.assertTrue;

import it.fox.gis.camel.component.BaseTestSupport;
import org.apache.camel.builder.RouteBuilder;
import org.apache.camel.component.mock.MockEndpoint;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.locationtech.jts.geom.Geometry;
import org.springframework.jdbc.datasource.embedded.EmbeddedDatabaseBuilder;

public class ECQLAnnotationTest extends BaseTestSupport {

    @Override
    protected EmbeddedDatabaseBuilder configure(EmbeddedDatabaseBuilder dbBuilder) {
        dbBuilder.addScript("sql/setup.sql");
        return super.configure(dbBuilder);
    }

    @Test
    public void testECQLExpressionAnnotation() throws InterruptedException {
        MockEndpoint endpoint = MockEndpoint.resolve(context, "mock:test");
        endpoint.setExpectedCount(4);
        endpoint.assertIsSatisfied();
        endpoint.getExchanges()
                .forEach(
                        e ->
                                Assertions.assertTrue(
                                        e.getMessage()
                                                .getBody(String.class)
                                                .contains("Original geometry")));
    }

    @Override
    protected RouteBuilder createRouteBuilder() {
        String propsURI = getClass().getResource("datastoreExpressions.properties").getFile();
        return new RouteBuilder() {
            @Override
            public void configure() {
                from("ogc-sf:test-h2-store-expr?featureType=ft1&resultType=ITERATOR&repeatCount=1&propertiesURI="
                                + propsURI)
                        .split()
                        .body()
                        .bean(new ExpressionBean())
                        .log("${body}")
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
