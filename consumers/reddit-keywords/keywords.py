from confluent_kafka import Consumer,Producer
from keybert import KeyBERT
import json

consumer_kafka_config = {
    'bootstrap.servers': "localhost:9092",
    'group.id': 'problem-filter-group',
    'auto.offset.reset': 'earliest'
}
producer_kafka_config = {
    'bootstrap.servers': 'localhost:9092'
}

MAX_BATCH_SIZE = 100
CONSUME_TIMEOUT = 2.0
PROBLEM_TOPIC = "reddit.problems"
PROBLEM_KEYWORDS_TOPIC = "reddit.problems_keywords"

consumer = Consumer(consumer_kafka_config)
producer = Producer(producer_kafka_config)
consumer.subscribe([PROBLEM_TOPIC])

kw_model = KeyBERT()

while True:
    messages = consumer.consume(num_messages=MAX_BATCH_SIZE,timeout=CONSUME_TIMEOUT)
    if not messages:
        continue
    
    reddit_objs = [json.loads(m.value().decode('utf-8')) for m in messages if not m.error()]
    texts = [r.get("text") for r in reddit_objs]
    
    for i,text in enumerate(texts):
        keywords = kw_model.extract_keywords(text,keyphrase_ngram_range=(1,2),stop_words=None)
        reddit_objs[i]["keywords"] = keywords
        producer.produce(PROBLEM_KEYWORDS_TOPIC,key=reddit_objs[i]["id"],value=json.dumps(reddit_objs[i]))
    
    producer.flush()