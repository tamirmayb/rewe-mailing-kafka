# Mail Delivery Service with Apache Kafka

### Author: Tamir Mayblat (tamirmayb@gmail.com)

## Prerequisites
* [Get docker](https://docs.docker.com/get-docker/)
* [Install docker compose](https://docs.docker.com/compose/install/)
* [Apache Kafka](https://kafka.apache.org/documentation/)
* [Kafdrop – Kafka Web UI  ](https://github.com/obsidiandynamics/kafdrop)

## Usage

Create a network so that there is communication between services.

```shell 
 docker network create mail-delivery-network
```

Running local mail delivery service.
```shell
 docker-compose up -d
```

Stop and remove mail delivery service.
```shell
 docker-compose down
```