create table images (
	"id" INT generated always as identity, 
	"path" varchar(100), 
	"user_id" INT, 
	primary key(id),
	constraint user_fk foreign key(user_id) references users(id)
);