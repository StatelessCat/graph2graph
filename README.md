## Build
```mvn clean package```

## Usage

```java -jar ./target/graph2graph-0.0.1.jar --graph /tmp/g.ttl --baseuri http://example.org/example/local --request /tmp/gquery.sparql```

The argument value of ```--graph``` can be either an URL or a local path.
