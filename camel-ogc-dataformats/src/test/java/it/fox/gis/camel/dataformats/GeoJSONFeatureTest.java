package it.fox.gis.camel.dataformats;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import it.fox.gis.camel.component.BaseTestSupport;
import org.apache.camel.builder.RouteBuilder;
import org.apache.camel.component.mock.MockEndpoint;
import org.junit.jupiter.api.Test;
import org.springframework.jdbc.datasource.embedded.EmbeddedDatabaseBuilder;

public class GeoJSONFeatureTest extends BaseTestSupport {

    @Override
    protected EmbeddedDatabaseBuilder configure(EmbeddedDatabaseBuilder dbBuilder) {
        dbBuilder.addScript("sql/setup.sql");
        return super.configure(dbBuilder);
    }

    @Test
    public void testSingleFeatureGeoJSON() throws InterruptedException, JsonProcessingException {
        MockEndpoint endpoint = MockEndpoint.resolve(context, "mock:test");
        endpoint.setExpectedCount(1);
        endpoint.assertIsSatisfied();
        String jsonResult = endpoint.getExchanges().get(0).getIn().getBody(String.class);
        JsonNode node = new ObjectMapper().readTree(jsonResult);
        assertNotNull(node.get("geometry"));
        assertNotNull(node.get("id"));
        assertNotNull(node.get("properties"));
        assertEquals("Feature", node.get("type").asText());
        JsonNode properties = node.get("properties");
        assertNotNull(properties.get("stringProperty"));
        assertNotNull(properties.get("doubleProperty"));
        assertNotNull(properties.get("intProperty"));
    }

    @Override
    protected RouteBuilder createRouteBuilder() {
        String propsURI = getClass().getResource("datastoreGeoJSON.properties").getFile();
        return new RouteBuilder() {
            @Override
            public void configure() {
                from("ogc-sf:test-h2-store-geojson?featureType=ft1&resultType=STREAM&repeatCount=1&cqlQuery=stringProperty='zero'&propertiesURI="
                                + propsURI)
                        .marshal()
                        .custom("geojson")
                        .log("${body}")
                        .to("mock:test");
            }
        };
    }
}
