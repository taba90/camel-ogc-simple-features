# camel-ogc-simple-features

A library to integrate [GeoTools](https://www.geotools.org/) DataStores in a Camel route. It allows to use the GeoTools SimpleFeatureCollection and SimpleFeatureIterator API to route and process messages in Camel.


## BUILD

``mvn clean install -Prelease`` will produce a zip package comprising geotools dependency in the ``/release/target`` folder.

## Usage

The library provides a Simple Features component that can be used both as a polling consumer and as a producer.

The syntax to declare the endpoint is as follow:
``ogc-sf:{dataStoreName}?`` where the datastore name is any name that the client code want to assign to a GeoTools DataStore. Endpoint using the same dataStoreName will share the DataStore instance.

DataStore are configured through a property file whose path needs to be provided in the endpoint parameter ``propertiesURI=/path/to/property_file.properties``. The component is able to autoreload the property  file if modified, without the need to restart the application.

The component support the following parameters:

| Parameter Name | Mandatory | Default Value | Usage                                                                                                                                                                                                                                                                                                                                                                  |
|----------------|-----------|---------------|------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------|
| propertiesURI  | YES       |               | Provides the file path to the GeoTools DataStore configuration                                                                                                                                                                                                                                                                                                         |
| featureType    | YES       |               | The featureType name to be used to retrieve data from the DataStore.                                                                                                                                                                                                                                                                                                   |
| crs            | NO        |               | Provides the target CoordinateReferenceSystem as an EPSG code.                                                                                                                                                                                                                                                                                                         |
| cqlQuery       | NO        |               | Provides a cql filter to use to read or delete data from a GeoTools DataStore                                                                                                                                                                                                                                                                                          |
| operation      | YES       | GET           | Provides the operation that the client code want to perform on DataStore. Suppported operations types are: GET (read data), ADD (add data to the DataStore, producer only), DELETE (delete data).  When using an ADD operation the component expects a message body of type SimpleFeatureCollection.                                                                   |
| resultType     | YES       | LIST          | Provides the desired resultType when reading data from a DataStore.  Possible values are: LIST (will return a java.util.List),STREAM (consumer only, streams the features one by one from the source),ITERATOR (will return a SimpleFeatureIterator implementing the java.util.Iterator interface allowing split), COLLECTION (will return a SimpleFeatureCollection). |

### Example usage

Read data from ft1 table, obtaining a SimpleFeatureIterator that gets splitted.
```java

   @Override
   public void configure() {
       from("ogc-sf:test-h2-store?featureType=ft1&resultType=STREAM&repeatCount=1&propertiesURI=/path/to/datastore.properties")
       .split()
       .body()
       .to("mock:test2");
  }
```

Read data from ft1 table as a SimpleFeatureCollection and adds it to the table ft2 of the same DataStore.
```java

@Override
public void configure() {
  from("ogc-sf:test-h2-store3?featureType=ft1&repeatCount=1&resultType=COLLECTION&propertiesURI=/path/to/datastore.properties")
  .to("ogc-sf:test-h2-store3?featureType=ft2&operation=ADD&propertiesURI=/path/to/datastore.properties");
}
```
