DROP DATABASE movie;

CREATE DATABASE movie;

USE movie;

CREATE TABLE movies(
	movieId INT AUTO_INCREMENT PRIMARY KEY,
	movieTitle VARCHAR(100) NOT NULL
);

CREATE TABLE genre
(
	genreId INT PRIMARY KEY AUTO_INCREMENT,
	genreName VARCHAR(50) NOT NULL
);

CREATE TABLE movieGenre
(
	id INT PRIMARY KEY AUTO_INCREMENT,
	movieId INT NOT NULL,
	genreId INT NOT NULL,
	
	FOREIGN KEY (movieId) REFERENCES movies(movieId) ON UPDATE CASCADE ON DELETE CASCADE,
	FOREIGN KEY (genreId) REFERENCES genre(genreId) ON UPDATE CASCADE ON DELETE CASCADE,
	UNIQUE KEY (movieId, genreId)
);

CREATE TABLE positions
(
    positionName VARCHAR(100) PRIMARY KEY
);

CREATE TABLE people
(
	personId INT PRIMARY KEY AUTO_INCREMENT,
	personName VARCHAR(100) NOT NULL,
    personPosition VARCHAR(100),
    
    FOREIGN KEY (personPosition) REFERENCES positions(positionName) ON DELETE SET NULL
);


CREATE TABLE moviePeople
(
	id INT PRIMARY KEY AUTO_INCREMENT,
	movieId INT NOT NULL,
	personId INT NOT NULL,
	
	FOREIGN KEY (movieId) REFERENCES movies(movieId) ON UPDATE CASCADE ON DELETE CASCADE,
	FOREIGN KEY (personId) REFERENCES people(personId) ON UPDATE CASCADE ON DELETE CASCADE,
    UNIQUE KEY (movieId, personId)
);

INSERT INTO genre (genreName) VALUES ("드라마");
INSERT INTO genre (genreName) VALUES ("판타지");
INSERT INTO genre (genreName) VALUES ("서부");
INSERT INTO genre (genreName) VALUES ("공포");
INSERT INTO genre (genreName) VALUES ("멜로/로맨스");
INSERT INTO genre (genreName) VALUES ("모험");
INSERT INTO genre (genreName) VALUES ("스릴러");
INSERT INTO genre (genreName) VALUES ("느와르");
INSERT INTO genre (genreName) VALUES ("컬트");
INSERT INTO genre (genreName) VALUES ("다큐멘터리");
INSERT INTO genre (genreName) VALUES ("코미디");
INSERT INTO genre (genreName) VALUES ("가족");
INSERT INTO genre (genreName) VALUES ("미스터리");
INSERT INTO genre (genreName) VALUES ("전쟁");
INSERT INTO genre (genreName) VALUES ("애니메이션");
INSERT INTO genre (genreName) VALUES ("범죄");
INSERT INTO genre (genreName) VALUES ("뮤지컬");
INSERT INTO genre (genreName) VALUES ("SF");
INSERT INTO genre (genreName) VALUES ("액션");
INSERT INTO genre (genreName) VALUES ("무협");
INSERT INTO genre (genreName) VALUES ("에로");
INSERT INTO genre (genreName) VALUES ("서스펜스");
INSERT INTO genre (genreName) VALUES ("서사");
INSERT INTO genre (genreName) VALUES ("블랙코미디");
INSERT INTO genre (genreName) VALUES ("실험");
INSERT INTO genre (genreName) VALUES ("공연실황");