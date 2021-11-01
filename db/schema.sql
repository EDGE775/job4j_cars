CREATE TABLE users
(
    id       SERIAL PRIMARY KEY,
    name     varchar(255)        not null,
    email    varchar(255) unique not null,
    password varchar(255)        not null
);

CREATE TABLE marks
(
    id   SERIAL PRIMARY KEY,
    name varchar(255) unique not null
);

CREATE TABLE bodytypes
(
    id   SERIAL PRIMARY KEY,
    name varchar(255) unique not null
);

CREATE TABLE transmissions
(
    id   SERIAL PRIMARY KEY,
    name varchar(255) unique not null
);

CREATE TABLE models
(
    id              SERIAL PRIMARY KEY,
    name            varchar(255)                      not null,
    mark_id         int references marks (id)         not null,
    bodytype_id     int references bodytypes (id)     not null,
    transmission_id int references transmissions (id) not null
);

CREATE TABLE cars
(
    id       SERIAL PRIMARY KEY,
    name     varchar(255)               not null,
    colour   varchar(255)               not null,
    model_id int references models (id) not null
);

CREATE TABLE announcements
(
    id          SERIAL PRIMARY KEY,
    description varchar(255)              not null,
    sold        boolean                   not null,
    created     timestamp                 not null,
    car_id      int references cars (id)  not null unique,
    user_id     int references users (id) not null
);

CREATE TABLE images
(
    id              SERIAL PRIMARY KEY,
    name            varchar(255)                      not null,
    photo           bytea,
    announcement_id int references announcements (id) not null
);