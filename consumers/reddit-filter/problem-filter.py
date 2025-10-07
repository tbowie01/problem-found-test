from confluent_kafka import Consumer,Producer
from transformers import pipeline
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
RAW_TOPIC = "reddit.raw"
PROCESSED_TOPIC = "reddit.problems"

consumer = Consumer(consumer_kafka_config)
producer = Producer(producer_kafka_config)
consumer.subscribe([RAW_TOPIC])

classifier = pipeline("zero-shot-classification",model="facebook/bart-large-mnli")
classifier_labels = ["This text is about a problem that requires a solution","This text is not about problem that requires a solution"]
problem_threshold = 0.95

while True:
    messages = consumer.consume(num_messages=MAX_BATCH_SIZE,timeout=CONSUME_TIMEOUT)
    if not messages:
        continue
    
    reddit_objs = [json.loads(m.value().decode('utf-8')) for m in messages if not m.error()]
    texts = [r.get("text") for r in reddit_objs]
    
    results = classifier(texts,candidate_labels=classifier_labels)
    
    # with open("classified_posts.jsonl","a") as f:
    for i in range(len(reddit_objs)):
        label,score = results[i]["labels"][0],results[i]["scores"][0]
        reddit_objs[i]["label"] = label
        reddit_objs[i]["confidence_score"] = score
        # f.write(json.dumps(reddit_objs[i]) + "\n")
        if label == classifier_labels[0] and score > problem_threshold:
            producer.produce(PROCESSED_TOPIC,key=reddit_objs[i]["id"],value=json.dumps(reddit_objs[i]))
    producer.flush()
