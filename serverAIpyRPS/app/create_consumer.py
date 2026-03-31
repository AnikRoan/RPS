from confluent_kafka import Consumer
from app.config import Settings

settings = Settings()


def create_consumer():
    config = {
        "bootstrap.servers": settings.kafka_bootstrap_servers,
        "group.id": settings.kafka_group_id,
        "auto.offset.reset": settings.kafka_auto_offset_reset
    }

    consumer = Consumer(config)
    consumer.subscribe([settings.kafka_topic])

    return consumer
