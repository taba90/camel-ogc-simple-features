package it.fox.gis.camel.component;

import org.apache.camel.test.junit5.CamelTestSupport;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.springframework.jdbc.datasource.embedded.EmbeddedDatabase;
import org.springframework.jdbc.datasource.embedded.EmbeddedDatabaseBuilder;
import org.springframework.jdbc.datasource.embedded.EmbeddedDatabaseType;

public class BaseTestSupport extends CamelTestSupport {

    private EmbeddedDatabase db;

    @Override
    @BeforeEach
    public void setUp() throws Exception {
        db =
                configure(
                                new EmbeddedDatabaseBuilder()
                                        .setName(getClass().getSimpleName())
                                        .setType(EmbeddedDatabaseType.H2))
                        .build();
        super.setUp();
    }

    protected EmbeddedDatabaseBuilder configure(EmbeddedDatabaseBuilder dbBuilder) {
        return dbBuilder;
    }

    @Override
    @AfterEach
    public void tearDown() throws Exception {
        super.tearDown();
        if (db != null) db.shutdown();
    }
}
