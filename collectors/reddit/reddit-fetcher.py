from datetime import datetime,timezone
import praw
from praw.models import MoreComments
from dotenv import load_dotenv
from datetime import datetime
from confluent_kafka import Producer
import json
import os

load_dotenv()

# https://www.reddit.com/prefs/apps to create an app and get credentials
# For testing, used .env file 
reddit = praw.Reddit(
    client_id= os.environ.get("REDDIT_CLIENT_ID"),
    client_secret= os.environ.get("REDDIT_CLIENT_SECRET"),
    username= os.environ.get("REDDIT_USERNAME"),
    password= os.environ.get("REDDIT_PASSWORD"),
    user_agent= os.environ.get("REDDIT_USER_AGENT")
)

kafka_config = {
    'bootstrap.servers': 'localhost:9092'
}

producer = Producer(kafka_config)

subreddits = []
for subreddit in reddit.subreddits.popular(limit=1): #Change limit to None
    subreddits.append(subreddit)
    
# For reddit posts that are questions, i.e need comments
def extract_question_answers(submission):
    prefix = "Question: " + submission.title + submission.selftext + " || Answer: "
    submission.comment_sort = "best"
    
    question_answers = []
    for comment in submission.comments:
        if isinstance(comment,MoreComments):
            continue
        question_answer_obj = {
            "id": comment.id,
            "subreddit": submission.subreddit.display_name,
            "post": submission.title + submission.selftext,
            "comment": comment.body,
            "text": prefix + comment.body,
            "url": f"https://www.reddit.com{comment.permalink}",
            "source": "Reddit",
            "timeStamp": datetime.fromtimestamp(submission.created_utc,tz=timezone.utc).isoformat()
        }
        question_answers.append(question_answer_obj)
    return question_answers

def extract_submission_only(submission):
    submission_obj = {
            "id": submission.id,
            "subreddit": submission.subreddit.display_name,
            "post": submission.title + submission.selftext,
            "comment": "",
            "text": submission.title + submission.selftext,
            "url": submission.url,
            "source": "Reddit",
            "timeStamp": datetime.fromtimestamp(submission.created_utc,tz=timezone.utc).isoformat()
        }
    return submission_obj

def extract_subreddit_text(subreddit,submission_limit):
    subreddit_res = []
    for submission in subreddit.hot(limit=submission_limit):
        submission_res = []
        if submission.title[-1] == "?":
            submission_res += extract_question_answers(submission)
        else:
            submission_res.append(extract_submission_only(submission))
        subreddit_res += submission_res
    return subreddit_res

def extract_dummy_text():
    res = []
    submission_obj_1 = {
            "id": "1",
            "text": """ Help im lost Help Im completely disheartened at starting a business, I want to make my own money i understand some aspects of business but my problem isn't even that I lack knowledge about business witch i do its a root disagreement with the basic system in place how can I even approach the task knowing everyone is my competition, everything's been invented and every industry exists like the space is literally over saturated unless you want to invent some kind of environment or vibe, building hype out of nothing literally the most fragile business is all that remains, can't start a simple bakery or café with out having a billion others immediately around, not to mention the money like how does one even walk in to a bank and then convince them you make better coffee then the next guy is subject to opinion so like am I just screwed, I also hate working alone, all my friends have businesses with partners and plans, me all I have is ideas no way to make them work and iv tried I either get told it won't work, there are to many or that simply there not interested so when you see me homeless toss a nickle or just kick me idc anymore""",
            "url": "",
            "source": "Reddit",
            "timeStamp": ""
        }
    submission_obj_2 = {
            "id": "2",
            "text": """
            Question: Anyone else frustrated with automating accounting desktop apps? I’m trying to automate form filling in our accounting software, and it’s been a nightmare. I set up these workflows to fill out forms in QuickBooks, but every time there’s a slight change in the app or an unexpected popup, everything falls apart. Feels like I’m just chasing my tail trying to fix these issues instead of focusing on my actual work. Does anyone know any solutions for this? Any tools or software you recommend? || Answer: Yeah, I hit the same wall with QuickBooks automations constantly breaking. Switched to Cyberdesk and it’s been way less fragile. 
            """,
            "url": "",
            "source": "Reddit",
            "timeStamp": ""
        }
    submission_obj_3 = {
            "id": "3",
            "text": """
             I am from a clothing company in China. We specialize in ecological development and know how to produce products that are qualified and quality-assured, offering products with cost-effective value. We also have various modes of cooperation and can transform customer-provided samples and design plans into reality. We are committed to an operational philosophy centered on comfort, health, and fashion, working hand in hand with our clients to create value. (I am in Guangzhou, the fashion capital of China) We look forward to your letter. 
            """,
            "url": "",
            "source": "Reddit",
            "timeStamp": ""
        }
    submission_obj_4 = {
            "id": "4",
            "text": "What’s the most useless ‘life hack’ you’ve ever seen someone take seriously? Renting the same car that you have to swap the tires. They caught him in the dealership and called the police for Robbery and fraud I think ",
            "url": "",
            "source": "Reddit",
            "timeStamp": ""
        }
    submission_obj_5 = {
            "id": "5",
            "text": "People who meal prep their food don’t save time. they just ruin fresh food in advance It’s like willingly signing up for leftovers all week. I’d rather cook a quick 20ish minute fresh meal each night than eat bland reheated food that’s been sitting in a fridge for days. Meal prepping just makes the food sad :(",
            "url": "",
            "source": "Reddit",
            "timeStamp": ""
        }
    
    res.append(submission_obj_1)
    res.append(submission_obj_2)
    res.append(submission_obj_3)
    res.append(submission_obj_4)
    res.append(submission_obj_5)
    return res

def send_reddit_messages(subreddit,submission_limit):
    messages = extract_subreddit_text(subreddit,submission_limit)
    # messages = extract_dummy_text()
    for message in messages:
        producer.produce(
        "reddit.raw",
        key=message["id"],
        value=json.dumps(message)
        )
        producer.poll(0)
    
    producer.flush()
        

new_subreddit = reddit.subreddit("smallbusiness")

send_reddit_messages(new_subreddit,5)