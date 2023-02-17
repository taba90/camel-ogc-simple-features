package it.fox.gis.camel.dataformats;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import it.fox.gis.camel.component.BaseTestSupport;
import java.io.IOException;
import org.apache.camel.builder.RouteBuilder;
import org.apache.camel.component.mock.MockEndpoint;
import org.junit.jupiter.api.Test;
import org.springframework.jdbc.datasource.embedded.EmbeddedDatabaseBuilder;

public class GeoJSONFeatureCollectionTest extends BaseTestSupport {

    @Override
    protected EmbeddedDatabaseBuilder configure(EmbeddedDatabaseBuilder dbBuilder) {
        dbBuilder.addScript("sql/setup.sql");
        return super.configure(dbBuilder);
    }

    @Test
    public void testFeatureCollection() throws InterruptedException, IOException {
        MockEndpoint endpoint = MockEndpoint.resolve(context, "mock:test");
        endpoint.setExpectedCount(1);
        endpoint.assertIsSatisfied();
        String jsonResult = endpoint.getExchanges().get(0).getIn().getBody(String.class);
        JsonNode node = new ObjectMapper().readTree(jsonResult);
        assertEquals("FeatureCollection", node.get("type").asText());
        ArrayNode arrayNode = (ArrayNode) node.get("features");
        assertEquals(4, arrayNode.size());
        node = arrayNode.get(0);
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
        String propsURI = getClass().getResource("datastoreGeoJSONColl.properties").getFile();
        return new RouteBuilder() {
            @Override
            public void configure() {
                from("ogc-sf:test-h2-store-geojson-coll?featureType=ft1&resultType=COLLECTION&repeatCount=1&propertiesURI="
                                + propsURI)
                        .marshal()
                        .custom("geojson")
                        .log("${body}")
                        .to("mock:test");
            }
        };
    }
}
