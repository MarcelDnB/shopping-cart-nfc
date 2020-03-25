/* CASOS DE EJEMPLO (PRUEBAS) */

/*COMERCIO */
insert into comercio (nombreComercio, telefono, CIF) values("Mercadona", 123456789, "123456789");
insert into comercio (nombreComercio, telefono, CIF) values("Dia", 123456781, "12345678A");
insert into comercio (nombreComercio, telefono, CIF) values("Más", 123456782, "12345678B");
insert into comercio (nombreComercio, telefono, CIF) values("elJamón", 123456783, "12345678C");

/* REDESWIFI */
insert into redeswifi (SSID, PWR, captureTime, idComercio) values("WLAN_1212", -42, 1584921601, 1);
insert into redeswifi (SSID, PWR, captureTime, idComercio) values("ONO_512", -50, "1584921601", 1);
insert into redeswifi (SSID, PWR, captureTime, idComercio) values("WLAN_666", -60, "1584921605", 1);
insert into redeswifi (SSID, PWR, captureTime, idComercio) values("RED_DE_JUAN", -70, "1584921607", 5);

/* INTOLERANCIA */
insert into intolerancia (nombreIntolerancia) values("Intolerancia a la lactosa");
insert into intolerancia (nombreIntolerancia) values("Intolerancia a la histamina");
insert into intolerancia (nombreIntolerancia) values("Intolerancia al glúten");
insert into intolerancia (nombreIntolerancia)  values("Intolerancia al huevo");

/* INGREDIENTES */
insert into ingrediente (nombreIngrediente, idIntolerancia) values("Huevo", 4);
insert into ingrediente (nombreIngrediente, idIntolerancia) values("Alcohol", 2);
insert into ingrediente (nombreIngrediente, idIntolerancia) values("Nata", 1);
insert into ingrediente (nombreIngrediente, idIntolerancia)  values("Galletas", 3);

/* PRODUCTO */
insert into producto (nombreProducto, codigoBarras, fabricante, telefono) values("Natilla Con Galletas HACENDADO", 346781234678234678, "HACENDADO", 666666666);
insert into producto (nombreProducto, codigoBarras, fabricante, telefono) values("CocaCola 1L", 1231231234445, "CocaCola SL", 667676764);
insert into producto (nombreProducto, codigoBarras, fabricante, telefono) values("Pepinos 500g", 2316723167231673, "HACENDADO", 675459325);
insert into producto (nombreProducto, codigoBarras, fabricante, telefono)  values("LECHE HACENDADO 1L", 2312312312312312, "HACENDADO", 674384582);

/* INGREDIENTESPRODUCTO */
insert into ingredientesproducto (IdIngrediente, IdProducto) values(1, 1);
insert into ingredientesproducto (IdIngrediente, IdProducto) values(3, 1);
insert into ingredientesproducto (IdIngrediente, IdProducto) values(4, 1);
insert into ingredientesproducto (IdIngrediente, IdProducto)  values(2, 2);

/* USUARIO */
insert into usuario (idComercio) values(1);
insert into usuario (idComercio) values(1);
insert into usuario (idComercio) values(1);
insert into usuario (idComercio) values(2);

/* UBICACION */
insert into ubicacion (horaUbicacion, idredesWifi, margenError, nombreZona, idUsuario) values(1584921600, 1, 0.05, "Pasillo_1",1);
insert into ubicacion (horaUbicacion, idredesWifi, margenError, nombreZona, idUsuario) values("1584921600", 1, "0.05", "Pasillo_1",1);
insert into ubicacion (horaUbicacion, idredesWifi, margenError, nombreZona, idUsuario) values("1584921600", 1, "0.02", "Pasillo_5",1);
insert into ubicacion (horaUbicacion, idredesWifi, margenError, nombreZona, idUsuario)  values("1584921600", 2, "0.05","Pasillo_1",2);

/* INTOLERANCIAUSUARIO */
insert into intoleranciasusuario (idIntolerancia, idUsuario) values(1, 1);
insert into intoleranciasusuario (idIntolerancia, idUsuario) values(2, 1);
insert into intoleranciasusuario (idIntolerancia, idUsuario) values(3, 1);
insert into intoleranciasusuario (idIntolerancia, idUsuario) values(4, 2);

/* PRODUCTOSUSUARIO */
insert into productosusuario (idProducto, idUsuario) values(1, 1);
insert into productosusuario (idProducto, idUsuario) values(2, 1);
insert into productosusuario (idProducto, idUsuario) values(3, 1);
insert into productosusuario (idProducto, idUsuario) values(4, 2);