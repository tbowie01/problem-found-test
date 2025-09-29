import praw
from praw.models import MoreComments
from dotenv import load_dotenv
from datetime import datetime
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
            "text": prefix + comment.body,
            "url": f"https://www.reddit.com{comment.permalink}",
            "source": "Reddit",
            "timeStamp": comment.created_utc
        }
        question_answers.append(question_answer_obj)
    return question_answers

def extract_submission_only(submission):
    submission_obj = {
            "text": submission.title + submission.selftext,
            "url": submission.url,
            "source": "Reddit",
            "timeStamp": submission.created_utc
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
    print(subreddit_res)
        

new_subreddit = reddit.subreddit("FinancialCareers")

extract_subreddit_text(new_subreddit,1)