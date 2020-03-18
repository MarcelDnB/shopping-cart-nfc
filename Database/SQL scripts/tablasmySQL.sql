create table redesWifi(
ssid varchar(250),
pwr double,
time timestamp,
idWifi int primary key,
idComercio int references comercio
);

create table comercio(
nombreComercio varchar(250),
CIF varchar(9),
telefono varchar(9),
idComercio int primary key
);

create table ubicacion(
nombreZona varchar(250),
horaUbicacion timestamp,
margenError float,
idUbicacion int primary key,
idWifi int references redesWifi
);

create table intolerancia(
nombreIntolerancia varchar(250),
idIntolerancia int primary key
);

create table ingredientes(
nombreIngrediente varchar(20),
idIngrediente int primary key,
idIntolerancia int references intolerancia
);

create table producto(
nombreProducto varchar(250),
codigoBarras varchar(13),
fabricante varchar(250),
telefono varchar(9),
idProducto int primary key,
idIngredientes int references ingredientes
);

create table usuario(
idUsuario int primary key,
idProducto int references producto,
idIntolerancia int references intolerancia,
idUbicacion int references ubicacion,
idComercio int references comercio
);

create table perfilIntolerancia(
rangoIntolerancia int,
idPerfil int primary key,
idUsuario int references usuario,
idIntolerancia int references intolerancia
);

create table contador(
vecesEscaneado int,
idContador int primary key,
idUsuario int references usuario,
idProducto int references producto
);