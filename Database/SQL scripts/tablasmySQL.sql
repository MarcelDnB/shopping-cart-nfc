create table comercio(
nombreComercio varchar(250),
CIF varchar(9),
telefono varchar(9),
idComercio int primary key
);
create table redesWifi(
ssid varchar(250),
pwr double,
idComercio int,
time timestamp,
idWifi int primary key,
foreign key(idComercio) references comercio(idComercio)
);

create table ubicacion(
nombreZona varchar(250),
horaUbicacion timestamp,
idWifi int,
margenError float,
idUbicacion int primary key,
foreign key(idWifi) references redesWifi(idWifi)
);

create table intolerancia(
nombreIntolerancia varchar(250),
idIntolerancia int primary key
);

create table ingredientes(
nombreIngrediente varchar(20),
idIntolerancia int,
idIngrediente int primary key,
foreign key(idIntolerancia) references intolerancia(idIntolerancia)
);

create table producto(
nombreProducto varchar(250),
codigoBarras varchar(13),
fabricante varchar(250),
idIngrediente int,
telefono varchar(9),
idProducto int primary key,
foreign key(idIngrediente) references ingredientes(idIngrediente)
);

create table usuario(
idUsuario int primary key,
idIntolerancia int,
idComercio int,
idProducto int,
idUbicacion int,
foreign key(idProducto) references producto(idProducto),
foreign key(idIntolerancia) references intolerancia(idIntolerancia),
foreign key(idUbicacion) references ubicacion(idUbicacion),
foreign key(idComercio) references comercio(idComercio)
);

create table perfilIntolerancia(
rangoIntolerancia int,
idPerfil int primary key,
idIntolerancia int,
idUsuario int,
foreign key(idUsuario) references usuario(idUsuario),
foreign key(idIntolerancia) references intolerancia(idIntolerancia)
);

create table contador(
vecesEscaneado int,
idUsuario int,
idProducto int,
idContador int primary key,
foreign key(idUsuario) references usuario(idUsuario),
foreign key (idProducto) references producto(idProducto)
);