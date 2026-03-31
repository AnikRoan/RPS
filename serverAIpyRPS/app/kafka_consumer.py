import json
from app.create_consumer import create_consumer


def start_consumer(process_function):
    consumer = create_consumer()

    try:
        while True:
            msg = consumer.poll(1.0)
            if msg is None:
                continue

            if msg.error():
                print(f"Consumer error: {msg.error()}")
                continue

            try:
                data = json.loads(msg.value().decode("utf-8"))
                process_function(data)
                consumer.commit(msg)
                print("DATA: ", data)
            except Exception as e:
                print("Processing error: ", e)
    finally:
        consumer.close()
