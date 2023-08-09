# flink-showcase
An apache flink showcase

#### Setup
- docker
- jdk 11
- maven


#### Flink
start flink cluster
```bash

~/Downloads/flink-1.17.1/bin/start-cluster.sh

```

build jar
```bash

mvn clean install

```

run flink job
```bash

~/Downloads/flink-1.17.1/bin/flink run ./target/flink-enrich-1.0-SNAPSHOT.jar

```

#### Microservice
build jar
```bash

mvn clean install

```

```bash

mvn spring-boot:run

```
