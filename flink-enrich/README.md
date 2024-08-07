Flink UI: http://localhost:8081/#/overview

IBM console: https://localhost:9443/ibmmq/console/#/

## Flows

#### Kafka to Mq
Read message from kafka, enrich the transaction with some faked data and send to a JMS queue

#### Mq to FileSystem
Consumes from JMS queue and then store every event (in json format) to a jsonl file

## How to Run

Run docker containers:
- zookeper
- kafka (after zookeper is ready)
- ibm mq

Run flink

> cd /to/flink/path/bin/
> ./start-cluster.sh

Open the flink UI, deploy the jar and execute

When flows are ready and running, execute che spring boot demo microservices with

> mvn spring-boot:run

Check the download folder for the result file. 

Topic config

inside kafka container (docker exec -it <container> sh)

delete topic
> kafka-topics --bootstrap-server localhost:9092 --delete --topic first_topic

create topic
> kafka-topics --bootstrap-server localhost:9092 --create --topic TRANSACTION_REGISTER --partitions 4


 kafka-run-class kafka.admin.ConsumerGroupCommand --group my-group --bootstrap-server localhost:9092 --describe