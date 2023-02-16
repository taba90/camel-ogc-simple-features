package it.fox.gis.camel.component;

import java.util.ArrayList;
import java.util.List;
import org.apache.camel.Exchange;
import org.geotools.data.Query;
import org.geotools.data.simple.SimpleFeatureCollection;
import org.geotools.data.simple.SimpleFeatureIterator;
import org.geotools.data.simple.SimpleFeatureSource;
import org.opengis.feature.simple.SimpleFeature;
import org.opengis.referencing.crs.CoordinateReferenceSystem;

/**
 * A {@link FeatureComponentStrategy} that set a {@link java.util.List} as a message, thus avoiding
 * streaming data from the data source.
 */
class FeatureListStrategy extends AbstractFeatureComponentStrategy {
    @Override
    public void setMessage(
            Exchange exchange,
            SimpleFeatureSource source,
            Query query,
            CoordinateReferenceSystem crs) {
        SimpleFeatureCollection coll = reproject(collection(source, query), crs);
        List<SimpleFeature> simpleFeatureList = new ArrayList<>();
        try (SimpleFeatureIterator iterator = coll.features()) {
            while (iterator.hasNext()) {
                simpleFeatureList.add(iterator.next());
            }
        }
        exchange.getIn().setBody(simpleFeatureList);
    }
}
