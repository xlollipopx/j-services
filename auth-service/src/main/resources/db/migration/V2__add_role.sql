create table role(
role_id BIGINT primary key not null,
value TEXT
);

create table person_role_map(
person_id BIGINT not null,
role_id BIGINT not null,
primary key (person_id, role_id)
);