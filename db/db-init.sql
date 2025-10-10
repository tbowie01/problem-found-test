DROP TABLE IF EXISTS reddit_infos;
DROP TABLE IF EXISTS problem_keywords;
DROP TABLE IF EXISTS problems;

CREATE TABLE problems(
    problem_id          BIGINT GENERATED ALWAYS AS IDENTITY,
    text                TEXT,
    url                 TEXT,
    source              VARCHAR(50),
    source_created      TIMESTAMP,
    problem_created     TIMESTAMP DEFAULT now(),
    PRIMARY KEY (problem_id)
);

CREATE TABLE reddit_infos(
    problem_id          BIGINT,
    reddit_id           VARCHAR(20),
    subreddit           VARCHAR(30),
    post                TEXT,
    comment             TEXT,
    PRIMARY KEY (reddit_id),
    FOREIGN KEY (problem_id) REFERENCES problems(problem_id) ON DELETE CASCADE
);

CREATE TABLE problem_keywords(
    id                  BIGINT GENERATED ALWAYS AS IDENTITY,
    problem_id          BIGINT,
    keyword             VARCHAR(50),
    confidence          FLOAT,
    FOREIGN KEY (problem_id) REFERENCES problems(problem_id) ON DELETE CASCADE
);