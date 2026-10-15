CREATE TABLE users(
  id VARCHAR(21) PRIMARY KEY NOT NULL UNIQUE,
  name VARCHAR(250) NOT NULL,
  email VARCHAR(250) NOT NULL UNIQUE,
  password VARCHAR(200) NOT NULL,
  role VARCHAR(20) NOT NULL,
  created_at timestamptz NOT NULL,
  updated_at timestamptz NOT NULL
);