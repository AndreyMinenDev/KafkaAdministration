## Описание проекта

Проект для задания 2 состоит из кластера кафка, БД Postgres, Kafka Connect, Prometheus и Grafana.  


## Состав проекта

- `Apache Kafka` — брокер сообщений  
- `Kafka Connect + Debezium` — захват изменений из PostgreSQL (CDC)
- `PostgreSQL` — база данных (таблицы users и orders)
- `Prometheus + Grafana` — сбор и визуализация метрик


### Классы и структура:
- `confluent-components\debezium-connector-postgres` - папка с Debezium Connector
- `grafana\` - папка с Grafana
- `kafka-connect\script\create-pg-connector.sh` - скрипт для запуска Debezium Connector после запуск Kafka Connect
- `kafka-connect\script\pg-connector.json` - конфигурация Debezium коннектора
- `kafka-connect\Dockerfile` - Dockerfile для сборки Kafka Connect
- `kafka-connect\jmx_prometheus_javaagent-0.15.0.jar` - JMX Exporter для экспорта метрик из Kafka Connect в Prometheus
- `kafka-connect\kafka-connect.yml` - настройки JMX Exporter
- `postgres\script\CREATE_TABLES.sql` - скрипт для создания таблиц в БД Postgres
- `postgres\script\INSERT_DATA.sql` - скрипт для наполнения таблиц данными 
- `postgres\custom-config.conf` - файл конфигурации для БД Postgres
- `prometheus\prometheus.yml` - файл конфигурации для Prometheus

## Инструкция по запуску

1. Для запуска проекта:
    ```
    docker-compose up -d
    ```

2. Сервисы, которые поднимутся:
    - Брокер Kafka на основе Bitnami образа (`kafka-0, kafka-1, kafka-2`).
    - Kafka UI для визуализации сообщений.
	- База данных Postgres.  (При запуске будет созданы и наполнены таблицы `users` и `orders`) 
	- Kafka Connect для запуска подключения к БД.
    - Prometheus для сбора метрик.
	- Grafana для визуализации метрик.


3. Для проверки статуса Debezium Connector необходимо выполнить команду (должен быть в статусе RUNNING):
    ```
    curl http://localhost:8083/connectors/pg-connector/status
    ```

4. Откройте браузер и перейдите на `http://localhost:8080`, чтобы зайти в Kafka UI и увидеть сообщения в топиках `topic_users` и `topic_orders`.

5. Перейдите на `http://localhost:9876/metrics`, убедитесь, что Kafka Connect передает метрики в текстовом формате Prometheus.

6. Перейдите в веб-интерфейс Prometheus `http://localhost:9090`
   Перейдите на вкладку `Status → Targets` и убедитесь, что коннектор отображается со статусом `UP`.
   На вкладке `Graph` введите название одной из метрик (например, `kafka_producer_topic_record_send_total`) и убедитесь в наличии данных.

7. Перейдите в веб-интерфейс Grafana `http://localhost:3000/d/kafka-connect-overview-0/kafka-connect-overview-0?orgId=1&from=now-1h&to=now` 
   (логин - admin, пароль - admin )
   Убедитесь, что графики оттображаются
	
8. Подключимся к БД и отправим в таблицу users 1000000 новых записей. 
   Для подключения к БД необходимо выполнить команду:
    ```
    docker exec -it postgres psql -h 127.0.0.1 -U postgres-user -d customers
    ```

   Для отправки данных в таблицу users необходимо выполнить команду:
    ```
  INSERT INTO users (id, name, email)
  SELECT
     i,
    'Name_' || i || '_' || substring('abcdefghijklmnopqrstuvwxyz', (random() * 26)::integer + 1, 1),
    'Name_' || i || '_' || substring('abcdefghijklmnopqrstuvwxyz', (random() * 26)::integer + 1, 1) || '@mail.com'
  FROM
     generate_series(5, 1000000) AS i; 	
    ```
  

## Настройки Debezium Connector

{
   "connector.class": "io.debezium.connector.postgresql.PostgresConnector",
   "database.hostname": "postgres",
   "database.port": "5432",
   "database.user": "postgres-user",
   "database.password": "postgres-pw",
   "database.dbname": "customers",
   "database.server.name": "customers",
   "table.include.list": "public.users,public.orders",
   "transforms": "unwrap",
   "transforms.unwrap.type": "io.debezium.transforms.ExtractNewRecordState",
   "transforms.unwrap.drop.tombstones": "false",
   "transforms.unwrap.delete.handling.mode": "rewrite",
   "topic.prefix": "customers",
   "topic.creation.enable": "true",
   "topic.creation.default.replication.factor": "-1",
   "topic.creation.default.partitions": "-1",
   "skipped.operations": "none"
}

