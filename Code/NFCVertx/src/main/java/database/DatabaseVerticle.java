package database;

import io.vertx.core.AbstractVerticle;
import io.vertx.core.Promise;
import io.vertx.core.json.Json;
import io.vertx.core.json.JsonArray;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.web.Router;
import io.vertx.ext.web.RoutingContext;
import io.vertx.ext.web.handler.BodyHandler;
import io.vertx.mysqlclient.MySQLClient;
import io.vertx.mysqlclient.MySQLConnectOptions;
import io.vertx.mysqlclient.MySQLPool;
import io.vertx.sqlclient.PoolOptions;
import io.vertx.sqlclient.Row;
import io.vertx.sqlclient.RowSet;
import io.vertx.sqlclient.Tuple;
import types.productosUsuario;
import types.scannedProduct;
import types.usuario;
import types.usuarioIntolerancias;
import types.wifiReading;

public class DatabaseVerticle extends AbstractVerticle{

	
	private MySQLPool mySQLPool;
	Long idUsuarioCreado; /* Dentro del ESP32 primero se va a ejecutar la funcion que añade un usuario
	 						 y dentro de esta función asignamos a esta variable el id de este, posteriormente
	 						 el ESP32 ejecuta la segunda función en la que guardamos las intolerancias introducidas
	 						 por el usuario junto al id (que vamos a obtener de esta variable).*/
	
	@Override
	public void start(Promise<Void> startPromise) {
		MySQLConnectOptions mySQLConnectOptions = new MySQLConnectOptions().setPort(3306).setHost("localhost")
				.setDatabase("DAD").setUser("dad").setPassword("dnbmusic");
		PoolOptions poolOptions = new PoolOptions().setMaxSize(5);
		mySQLPool = MySQLPool.pool(vertx, mySQLConnectOptions, poolOptions);
		Router router = Router.router(vertx);
		router.route().handler(BodyHandler.create());
		vertx.createHttpServer().requestHandler(router::handle).listen(8081, result -> {
			if (result.succeeded()) {
				startPromise.complete();
			}else {
				startPromise.fail(result.cause());
			}
		});
		
		router.put("/api/scan/put/wifi/values").handler(this::putWifiScan);
		router.get("/api/scan/:idProducto").handler(this::getIntolerances);
		router.put("/api/scan/put/produs/values").handler(this::putAfterScan);
		router.put("/api/scan/put/usuario/values").handler(this::putUsuario);
		router.put("/api/scan/put/usuint/values").handler(this::putIntoleranciasUsuario);
		}
	
	
	private void putUsuario(RoutingContext routingContext) { //Funciona
		try {
			usuario usuarioIntolerancias = Json.decodeValue(routingContext.getBodyAsString(), usuario.class);
			mySQLPool.preparedQuery(
					"INSERT INTO usuario (idComercio) VALUES (?)",
					Tuple.of(usuarioIntolerancias.getIdComercio()),
					handler -> {
						if (handler.succeeded()) {
							System.out.println(handler.result().rowCount());
							
							long id = handler.result().property(MySQLClient.LAST_INSERTED_ID);
							
							idUsuarioCreado = id;
							
							routingContext.response().setStatusCode(200).putHeader("content-type", "application/json")
									.end(JsonObject.mapFrom(usuarioIntolerancias).encodePrettily());
						} else {
							System.out.println(handler.cause().toString());
							routingContext.response().setStatusCode(401).putHeader("content-type", "application/json")
									.end((JsonObject.mapFrom(handler.cause()).encodePrettily()));
						}
					});
		}catch(Exception e) {
			System.out.println(e.getMessage());
		}
	}
	
	
	private void putIntoleranciasUsuario(RoutingContext routingContext) { //Funciona
		try {
		usuarioIntolerancias usuarioIntolerancias = Json.decodeValue(routingContext.getBodyAsString(), usuarioIntolerancias.class);
		for(Integer i:usuarioIntolerancias.getIntolerancias()) {
			mySQLPool.preparedQuery(
					"INSERT INTO intoleranciasusuario (idIntolerancia, idUsuario) VALUES (?,?)",
					Tuple.of(i, idUsuarioCreado),
					handler -> {
						if (handler.succeeded()) {
							System.out.println(handler.result().rowCount());
							
							routingContext.response().setStatusCode(200).putHeader("content-type", "application/json")
									.end(JsonObject.mapFrom(usuarioIntolerancias).encodePrettily());
						} else {
							System.out.println(handler.cause().toString());
							routingContext.response().setStatusCode(401).putHeader("content-type", "application/json")
									.end((JsonObject.mapFrom(handler.cause()).encodePrettily()));
						}
					});
		}
		
	}catch(Exception e) {
		System.out.println(e.getMessage());
		}
	}
	
	
	private void putWifiScan(RoutingContext routingContext) { //Funciona
			wifiReading wifiReading1 = Json.decodeValue(routingContext.getBodyAsString(), wifiReading.class);
			mySQLPool.preparedQuery(
					"INSERT INTO redeswifi (SSID, PWR, captureTime, idComercio) VALUES (?,?,?,?)",
					Tuple.of(wifiReading1.getSSID(), wifiReading1.getPower(),
							wifiReading1.getTimestamp(),wifiReading1.getIdComercio()),
					handler -> {
						if (handler.succeeded()) {
							System.out.println(handler.result().rowCount());
							
							long id = handler.result().property(MySQLClient.LAST_INSERTED_ID);
							wifiReading1.setIdWifi((int) id);
							
							routingContext.response().setStatusCode(200).putHeader("content-type", "application/json")
									.end(JsonObject.mapFrom(wifiReading1).encodePrettily());
						} else {
							System.out.println(handler.cause().toString());
							routingContext.response().setStatusCode(401).putHeader("content-type", "application/json")
									.end((JsonObject.mapFrom(handler.cause()).encodePrettily()));
						}
					});
	}
	
	
	private void putAfterScan(RoutingContext routingContext) { //Funciona
		productosUsuario productoUsuario = Json.decodeValue(routingContext.getBodyAsString(), productosUsuario.class);
		mySQLPool.preparedQuery(
				"INSERT INTO productosUsuario (idProducto, idUsuario) VALUES (?,?)",
				Tuple.of(productoUsuario.getIdProducto(), productoUsuario.getIdUsuario()),
				handler -> {
					if (handler.succeeded()) {
						System.out.println(handler.result().rowCount());
						
						long id = handler.result().property(MySQLClient.LAST_INSERTED_ID);
						productoUsuario.setIdProductosUsuario((int) id);
						
						routingContext.response().setStatusCode(200).putHeader("content-type", "application/json")
								.end(JsonObject.mapFrom(productoUsuario).encodePrettily());
					} else {
						System.out.println(handler.cause().toString());
						routingContext.response().setStatusCode(401).putHeader("content-type", "application/json")
								.end((JsonObject.mapFrom(handler.cause()).encodePrettily()));
					}
				});
	}
	
	
	private void getIntolerances(RoutingContext routingContext) { //Funciona
		try {
		mySQLPool.query("select idIntolerancia from intoleranciasingrediente natural join ingrediente natural join ingredientesproducto"
				+ " natural join producto where IdProducto = " + 
				routingContext.request().getParam("idProducto"),
				res -> {
					if (res.succeeded()) {
						RowSet<Row> resultSet = res.result();
						System.out.println("El número de elementos obtenidos es " + resultSet.size());
						JsonArray result = new JsonArray();
						for (Row row : resultSet) {
							result.add(JsonObject.mapFrom(new scannedProduct(row.getInteger("idIntolerancia")
									)));
						}
						routingContext.response().setStatusCode(200).putHeader("content-type", "application/json")
							.end(result.encodePrettily());
					}else {
						routingContext.response().setStatusCode(401).putHeader("content-type", "application/json")
							.end((JsonObject.mapFrom(res.cause()).encodePrettily()));
					}
				});
		}catch(Exception e) {
			System.out.println(e.getMessage());
		}
	}
}