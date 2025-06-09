CREATE TABLE IF NOT EXISTS articles (
    id SERIAL PRIMARY KEY,
    title TEXT UNIQUE,
    link TEXT UNIQUE NOT NULL,
    description TEXT,
    public_date TIMESTAMP WITH TIME ZONE,
    category TEXT,
    main_text TEXT,
    source TEXT,
    created_at TIMESTAMP WITH TIME ZONE DEFAULT NOW(),
    updated_at TIMESTAMP WITH TIME ZONE
);

CREATE TABLE IF NOT EXISTS article_images (
    id SERIAL PRIMARY KEY,
    article_id INT REFERENCES articles(id) ON DELETE CASCADE,
    url TEXT NOT NULL
);

CREATE TABLE IF NOT EXISTS article_videos (
    id SERIAL PRIMARY KEY,
    article_id INT REFERENCES articles(id) ON DELETE CASCADE,
    url TEXT NOT NULL
);

CREATE TABLE IF NOT EXISTS tags (
    id SERIAL PRIMARY KEY,
    name TEXT NOT NULL
);

CREATE TABLE IF NOT EXISTS article_tags (
    article_id INT REFERENCES articles(id) ON DELETE CASCADE,
    tag_id INT REFERENCES tags(id) ON DELETE CASCADE,
    PRIMARY KEY (article_id, tag_id)
);

DROP TABLE IF EXISTS old_articles;

CREATE TABLE old_articles (
    id SERIAL PRIMARY KEY,
    title TEXT UNIQUE,
    link TEXT NOT NULL,
    description TEXT,
    public_date TIMESTAMP WITH TIME ZONE,
    category TEXT,
    main_text TEXT,
    source TEXT
);