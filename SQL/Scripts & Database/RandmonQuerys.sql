
select idProducto from productosusuario natural join producto 
natural join ingredientesproducto natural join ingrediente natural join intoleranciasingrediente natural join intolerancia where idIntolerancia=1  group by idProducto;


select idProducto, count(idProducto) from productosusuario where productosusuario.idProducto in (select idProducto from productosusuario natural join producto 
natural join ingredientesproducto natural join ingrediente natural join intoleranciasingrediente natural join intolerancia where idIntolerancia=1) group by IdProducto;


select nombreProducto from producto where producto.idProducto in (select idProducto from productosusuario natural join producto
natural join ingredientesproducto natural join ingrediente natural join intoleranciasingrediente natural join intolerancia where idIntolerancia=1);


select idProducto,nombreProducto, count(idProducto) from productosusuario natural join producto where productosusuario.idProducto in (select idProducto from productosusuario natural join producto 
natural join ingredientesproducto natural join ingrediente natural join intoleranciasingrediente natural join intolerancia where idIntolerancia=1) group by IdProductoproducto;



select nombreProducto from producto where nombreProducto like '%galletas%';


select idProducto, nombreProducto from producto;