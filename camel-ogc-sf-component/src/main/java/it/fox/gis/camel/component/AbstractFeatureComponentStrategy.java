package it.fox.gis.camel.component;

import java.io.IOException;
import java.util.Optional;
import org.geotools.data.Query;
import org.geotools.data.simple.SimpleFeatureCollection;
import org.geotools.data.simple.SimpleFeatureSource;
import org.geotools.data.store.ReprojectingFeatureCollection;
import org.geotools.referencing.crs.DefaultGeographicCRS;
import org.opengis.referencing.crs.CoordinateReferenceSystem;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Abstract implementation for a {@link FeatureComponentStrategy} that provides some methods
 * usefulll for subclasses.
 */
abstract class AbstractFeatureComponentStrategy implements FeatureComponentStrategy {

    private static final Logger LOGGER =
            LoggerFactory.getLogger(AbstractFeatureComponentStrategy.class);

    /**
     * Wrap a {@link SimpleFeatureCollection} with a {@link ReprojectingFeatureCollection} if
     * needed.
     *
     * @param coll the simple feature collection to reproject.
     * @param crs the target {@link CoordinateReferenceSystem}.
     * @return a {@link SimpleFeatureCollection}.
     */
    protected SimpleFeatureCollection reproject(
            SimpleFeatureCollection coll, CoordinateReferenceSystem crs) {
        if (crs == null) return coll;
        CoordinateReferenceSystem source =
                Optional.ofNullable(coll.getSchema().getCoordinateReferenceSystem())
                        .orElse(DefaultGeographicCRS.WGS84);

        return new ReprojectingFeatureCollection(coll, source, crs);
    }

    /**
     * Gets a {@link SimpleFeatureCollection} from a {@link SimpleFeatureSource}.
     *
     * @param featureSource the feature source from which grab a collection.
     * @param query the Query to filter features.
     * @return a {@link SimpleFeatureCollection} instance.
     */
    protected SimpleFeatureCollection collection(SimpleFeatureSource featureSource, Query query) {
        try {
            return featureSource.getFeatures(query);
        } catch (IOException e) {
            LOGGER.error(
                    String.format(
                            "Error while retrieving feature collection from source. Error is: %s",
                            e.getMessage()),
                    e);
            throw new RuntimeException(e);
        }
    }
}
