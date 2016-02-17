create table cliente(
	id integer not null auto_increment,
	conta integer,
	senha integer,
	saldo double,
	ativo boolean,
	primary key(id)
);

create table gerente(
	id integer not null auto_increment,
	conta integer,
	senha integer,
	ativo boolean,
	primary key(id)
);

create table extrato(
	id integer not null auto_increment,
	valor double,
	data date,
	cliente integer,
	primary key(id),
	foreign key(cliente) references cliente(id)
);
