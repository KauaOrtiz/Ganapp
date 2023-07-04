create table "users" (
  "id" INT generated always as identity, 
  "name" varchar(50), 
  "password" varchar(50), 
  primary key(id)
);