## Описание проекта

Создаются контейнеры: БД Postgres, Kafka Connect, Kafka и приложение с консьюмером для чтения сообщений из топиков.  


## Состав проекта

- `Apache Kafka`— брокер сообщений  
- `Kafka Connect + Debezium` — захват изменений из PostgreSQL (CDC)
- `PostgreSQL` — база данных (таблицы users и orders)
- `ConsumerApp` — вывод сообщений из Kafka в терминал


### Cтруктура:
- `confluent-components\debezium-connector-postgres` - папка с Debezium Connector
- `kafka-connect\Dockerfile` - Dockerfile для сборки Kafka Connect
- `postgres\script\CREATE_TABLES.sql` - скрипт для создания таблиц в БД Postgres
- `postgres\script\INSERT_DATA.sql` - скрипт для наполнения таблиц данными 
- `postgres\custom-config.conf` - файл конфигурации для БД Postgres
- `pg-connector.json` - конфигурация Debezium коннектора

## Инструкция по запуску

1. Запуск проекта:
    ```
    docker-compose up -d
    ```

2. Сервисы, которые поднимутся:
    - Брокер Kafka на основе Bitnami образа (`kafka-0, kafka-1, kafka-2`).
    - Kafka UI для визуализации сообщений.
	- База данных Postgres.  (При запуске будет созданы и наполнены таблицы `users` и `orders`) 
	- Kafka Connect для запуска подключения к БД.
    - Приложение с консумером для просмотра сообщений из топиков `customers.public.users` и `customers.public.orders`).

3. Для запуска Debezium Connector в папке Task1 необходимо выполнить команду (в Windows используем GitBash):
    ```
    curl -X PUT -H "Content-Type: application/json" --data @pg-connector.json http://localhost:8083/connectors/pg-connector/config
    ```

4. Для проверки статуса Debezium Connector необходимо выполнить команду (должен быть в статусе RUNNING):
    ```
    curl http://localhost:8083/connectors/pg-connector/status
    ```

5. Откройте браузер и перейдите на `http://localhost:8080`, чтобы зайти в Kafka UI и увидеть сообщения в топиках `customers.public.users` и `customers.public.orders`.

6. Cообщения из топиков `customers.public.users` и `customers.public.orders` можно посмотреть в логах приложения `ConsumerApp` выполнив команду:
    ```
    docker-compose logs -f consumer
    ```
	
7. Для подключения к БД необходимо выполнить команду:
    ```
    docker exec -it postgres psql -h 127.0.0.1 -U postgres-user -d customers
    ```

---

## Что происходит при запуске

- В базе создаются таблицы `users` и `orders`.
- Debezium Connector следит за изменениями только этих таблиц.
- Сообщения об изменениях сразу появляются в топиках Kafka.
- Приложение `ConsumerApp` подписано на топики и выводит все сообщения в терминал.
- Любой INSERT/UPDATE/DELETE в таблицы `users` и `orders` мгновенно появится в топиках Kafka и, соответственно, в логах приложения.
