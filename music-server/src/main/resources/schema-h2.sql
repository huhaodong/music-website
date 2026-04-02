-- H2 database schema for music-website

-- Create admin table
CREATE TABLE IF NOT EXISTS admin (
  id INT AUTO_INCREMENT PRIMARY KEY,
  name VARCHAR(45) NOT NULL,
  password VARCHAR(45) NOT NULL,
  UNIQUE (name)
);

-- Create banner table
CREATE TABLE IF NOT EXISTS banner (
  id INT AUTO_INCREMENT PRIMARY KEY,
  pic VARCHAR(255) NOT NULL
);

-- Create collect table
CREATE TABLE IF NOT EXISTS collect (
  id INT AUTO_INCREMENT PRIMARY KEY,
  user_id INT NOT NULL,
  type TINYINT NOT NULL,
  song_id INT DEFAULT NULL,
  song_list_id INT DEFAULT NULL,
  create_time DATETIME NOT NULL
);

-- Create comment table
CREATE TABLE IF NOT EXISTS comment (
  id INT AUTO_INCREMENT PRIMARY KEY,
  user_id INT NOT NULL,
  song_id INT DEFAULT NULL,
  song_list_id INT DEFAULT NULL,
  content VARCHAR(255) DEFAULT NULL,
  create_time DATETIME DEFAULT NULL,
  type TINYINT NOT NULL,
  up INT DEFAULT 0
);

-- Create consumer table
CREATE TABLE IF NOT EXISTS consumer (
  id INT AUTO_INCREMENT PRIMARY KEY,
  username VARCHAR(255) NOT NULL,
  password VARCHAR(100) NOT NULL,
  sex TINYINT DEFAULT NULL,
  phone_num VARCHAR(15) DEFAULT NULL,
  email VARCHAR(30) DEFAULT NULL,
  birth DATETIME DEFAULT NULL,
  introduction VARCHAR(255) DEFAULT NULL,
  location VARCHAR(45) DEFAULT NULL,
  avator VARCHAR(255) DEFAULT NULL,
  nickname VARCHAR(100) DEFAULT NULL,
  create_time DATETIME NOT NULL,
  update_time DATETIME NOT NULL,
  org_id INT DEFAULT NULL,
  status TINYINT DEFAULT 1,
  last_login_time DATETIME DEFAULT NULL,
  UNIQUE (username),
  UNIQUE (phone_num),
  UNIQUE (email)
);

-- Create organization table
CREATE TABLE IF NOT EXISTS organization (
  id INT AUTO_INCREMENT PRIMARY KEY,
  name VARCHAR(100) NOT NULL,
  code VARCHAR(64) DEFAULT NULL,
  parent_id INT DEFAULT NULL,
  level INT DEFAULT 1,
  path VARCHAR(500) DEFAULT '/',
  sort INT DEFAULT 0,
  status TINYINT DEFAULT 1,
  create_time DATETIME DEFAULT CURRENT_TIMESTAMP,
  update_time DATETIME DEFAULT CURRENT_TIMESTAMP
);

-- Backward-compatible fix for existing H2 schemas created before `code` was introduced
ALTER TABLE organization
  ADD COLUMN IF NOT EXISTS code VARCHAR(64) DEFAULT NULL;

-- Create role table
CREATE TABLE IF NOT EXISTS role (
  id INT AUTO_INCREMENT PRIMARY KEY,
  name VARCHAR(50) NOT NULL,
  code VARCHAR(50) NOT NULL,
  description VARCHAR(200) DEFAULT NULL,
  status TINYINT DEFAULT 1,
  create_time DATETIME DEFAULT CURRENT_TIMESTAMP,
  update_time DATETIME DEFAULT CURRENT_TIMESTAMP
);

-- Create permission table
CREATE TABLE IF NOT EXISTS permission (
  id INT AUTO_INCREMENT PRIMARY KEY,
  name VARCHAR(50) NOT NULL,
  code VARCHAR(100) NOT NULL,
  type VARCHAR(20) DEFAULT 'menu',
  url VARCHAR(200) DEFAULT NULL,
  method VARCHAR(10) DEFAULT NULL,
  parent_id INT DEFAULT NULL,
  sort INT DEFAULT 0,
  status TINYINT DEFAULT 1,
  create_time DATETIME DEFAULT CURRENT_TIMESTAMP,
  update_time DATETIME DEFAULT CURRENT_TIMESTAMP
);

-- Create role_permission table
CREATE TABLE IF NOT EXISTS role_permission (
  id INT AUTO_INCREMENT PRIMARY KEY,
  role_id INT NOT NULL,
  permission_id INT NOT NULL,
  create_time DATETIME DEFAULT CURRENT_TIMESTAMP,
  UNIQUE (role_id, permission_id)
);

-- Create user_role table
CREATE TABLE IF NOT EXISTS user_role (
  id INT AUTO_INCREMENT PRIMARY KEY,
  user_id INT NOT NULL,
  user_type VARCHAR(20) NOT NULL,
  role_id INT NOT NULL,
  create_time DATETIME DEFAULT CURRENT_TIMESTAMP,
  UNIQUE (user_id, user_type, role_id)
);

-- Create singer table
CREATE TABLE IF NOT EXISTS singer (
  id INT AUTO_INCREMENT PRIMARY KEY,
  name VARCHAR(100) NOT NULL,
  sex TINYINT DEFAULT NULL,
  pic VARCHAR(255) DEFAULT NULL,
  birth DATETIME DEFAULT NULL,
  location VARCHAR(255) DEFAULT NULL,
  introduction VARCHAR(255) DEFAULT NULL
);

-- Create song table
CREATE TABLE IF NOT EXISTS song (
  id INT AUTO_INCREMENT PRIMARY KEY,
  singer_id INT NOT NULL,
  name VARCHAR(100) NOT NULL,
  introduction VARCHAR(255) DEFAULT NULL,
  pic VARCHAR(255) DEFAULT NULL,
  lyric VARCHAR(255) DEFAULT NULL,
  url VARCHAR(255) NOT NULL
);

-- Create song_list table
CREATE TABLE IF NOT EXISTS song_list (
  id INT AUTO_INCREMENT PRIMARY KEY,
  title VARCHAR(100) NOT NULL,
  pic VARCHAR(255) DEFAULT NULL,
  introduction VARCHAR(255) DEFAULT NULL,
  style VARCHAR(100) DEFAULT NULL
);

-- Create list_song table
CREATE TABLE IF NOT EXISTS list_song (
  id INT AUTO_INCREMENT PRIMARY KEY,
  song_id INT NOT NULL,
  song_list_id INT NOT NULL
);

-- Create rank_list table
CREATE TABLE IF NOT EXISTS rank_list (
  id INT AUTO_INCREMENT PRIMARY KEY,
  song_list_id INT NOT NULL,
  consumer_id INT NOT NULL,
  score INT NOT NULL
);
