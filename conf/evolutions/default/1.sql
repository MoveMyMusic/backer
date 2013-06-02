# --- !Ups

create table users (
    id int NOT NULL unique,
    name varchar(40) NOT NULL,
    email varchar(100) unique,
    password varchar(40) NOT NULL,
    salt varchar(40) NOT NULL,
    token varchar(40) NOT NULL
);

CREATE SEQUENCE user_id_seq;
alter table users alter column id set default nextval('user_id_seq');


create table teachers (
    id int NOT NULL unique references users(id)
);

create table students (
    id int NOT NULL unique references users(id)
);

create table students_teachers (
    teacher_id int NOT NULL references teachers(id),
    student_id int NOT NULL references students(id)
);

alter table students_teachers add constraint s_t_unique UNIQUE(teacher_id, student_id);

create table save_data (
    id int NOT NULL references users(id),
    user_id int NOT NULL references users(id),
    save_data TEXT NOT NULL
);

# --- !Downs
drop table save_data;
drop table students_teachers;
drop table students;
drop table teachers;
drop table users;
drop sequence user_id_seq;