create sequence save_data_id_seq;

alter table save_data add column name varchar(100);
alter table save_data alter column id set default nextval('save_data_id_seq');
