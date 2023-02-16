CREATE ALIAS IF NOT EXISTS AddGeometryColumn FOR "geodb.GeoDB.AddGeometryColumn";
CREATE ALIAS IF NOT EXISTS ST_GeomFromText FOR "geodb.GeoDB.ST_GeomFromText";
CREATE SCHEMA IF NOT EXISTS "test";
CREATE TABLE IF NOT EXISTS "GEOMETRY_COLUMNS" ("F_TABLE_SCHEMA" VARCHAR, "F_TABLE_NAME" VARCHAR, "F_GEOMETRY_COLUMN" VARCHAR, "SRID" INT, "COORD_DIMENSION" INT, "TYPE" VARCHAR);
CREATE TABLE "test"."ft1" ("id" int AUTO_INCREMENT(1) PRIMARY KEY, "location" BLOB, "intProperty" int,"doubleProperty" double, "stringProperty" varchar);
CALL AddGeometryColumn('test', 'ft1', 'location', 4326, 'GEOMETRY', 2);
INSERT INTO "test"."ft1" VALUES (0,ST_GeomFromText('POINT(0 0)',4326), 0, 0.0,'zero');
INSERT INTO "test"."ft1" VALUES (1,ST_GeomFromText('POINT(1 1)',4326), 1, 1.1,'one');
INSERT INTO "test"."ft1" VALUES (2,ST_GeomFromText('POINT(2 2)',4326), 2, 2.2,'two');
INSERT INTO "test"."ft1" VALUES (3,ST_GeomFromText('POINT(3 3)',4326), 3, 3.3,'three');

