select idProducto, count(idProducto) from productosusuario natural join ingredientesproducto natural join intoleranciasingrediente where idIntolerancia=1 group by idProducto;

select idProducto, count(idProducto) from productosusuario natural join intoleranciasusuario where idIntolerancia = 1 group by idProducto;

select * from (select idProducto, count(idProducto) from productosusuario natural join ingredientesproducto natural join intoleranciasingrediente where idIntolerancia=1 group by idProducto) t1

natural join

(select idProducto, count(idProducto) from productosusuario natural join intoleranciasusuario where idIntolerancia = 1 group by idProducto) t2;



select idProducto from productosusuario natural join producto 
natural join ingredientesproducto natural join ingrediente natural join intoleranciasingrediente natural join intolerancia where idIntolerancia=1  group by idProducto;


select idProducto, count(idProducto) from productosusuario where productosusuario.idProducto in (select idProducto from productosusuario natural join producto 
natural join ingredientesproducto natural join ingrediente natural join intoleranciasingrediente natural join intolerancia where idIntolerancia=1) group by IdProducto;


select nombreProducto from producto where producto.idProducto in (select idProducto from productosusuario natural join producto
natural join ingredientesproducto natural join ingrediente natural join intoleranciasingrediente natural join intolerancia where idIntolerancia=1);








select idProducto,nombreProducto, count(idProducto) from productosusuario natural join producto where productosusuario.idProducto in (select idProducto from productosusuario natural join producto 
natural join ingredientesproducto natural join ingrediente natural join intoleranciasingrediente natural join intolerancia where idIntolerancia=3) group by IdProducto;