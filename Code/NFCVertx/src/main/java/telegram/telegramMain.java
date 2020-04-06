package telegram;

import java.util.HashMap;
import java.util.Map;

import org.schors.vertx.telegram.bot.LongPollingReceiver;
import org.schors.vertx.telegram.bot.TelegramBot;
import org.schors.vertx.telegram.bot.TelegramOptions;
import org.schors.vertx.telegram.bot.api.methods.SendMessage;
import org.schors.vertx.telegram.bot.api.types.Update;

import io.vertx.core.AbstractVerticle;
import io.vertx.core.Promise;
import io.vertx.core.buffer.Buffer;
import io.vertx.core.json.JsonArray;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.web.client.HttpResponse;
import io.vertx.ext.web.client.WebClient;
import io.vertx.mysqlclient.MySQLConnectOptions;
import io.vertx.mysqlclient.MySQLPool;
import io.vertx.sqlclient.PoolOptions;
import io.vertx.sqlclient.Row;
import io.vertx.sqlclient.RowSet;
import io.vertx.sqlclient.Tuple;
import types.comercio;
import types.ingrediente;
import types.ingredientesProducto;
import types.intolerancia;
import types.intoleranciasIngrediente;
import types.producto;

public class telegramMain extends AbstractVerticle {

	/* * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * *
	 * bot: variable que nos entrega las funcionalidades del bot de Telegram.
	 * 
	 * tabla: nos sirve para que el bot sepa de que tabla nos estaba haciendo las
	 * preguntas y seguir con ellas.
	 * 
	 * seccion: nos sirve para que el bot sepa en que pregunta se ha quedado y
	 * seguir por la siguiente, para ello, en el constructor vacio de los tipos
	 * asignamos las variables a null, ya que de esta forma podremos ver que
	 * variables ya se han completado y cuales no.
	 * 
	 * mySQLPool: nos va a permitir consultar e introducir datos en la base de datos
	 * 
	 * map: nos ayudamos de un map para poder diferenciar los diferentes usuarios,
	 * mediante su chatId que nos proporciona el bot de Telegram.
	 * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * */
	
	private TelegramBot bot;
	private Map<Integer, String> tabla = new HashMap<Integer, String>();
	private Map<Integer, String> seccion = new HashMap<Integer, String>();
	private MySQLPool mySQLPool;
	private Map<Integer, Object> map = new HashMap<Integer, Object>();
	
	
	@Override
	public void start(Promise<Void> future) {
		
		/* * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * *
		 * Conexion a la base de datos MySQL
		 * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * */
		MySQLConnectOptions mySQLConnectOptions = new MySQLConnectOptions().setPort(3306).setHost("localhost")
				.setDatabase("DAD").setUser("dad").setPassword("dnbmusic");
		PoolOptions poolOptions = new PoolOptions().setMaxSize(5);
		mySQLPool = MySQLPool.pool(vertx, mySQLConnectOptions, poolOptions);

		/* * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * *
		 * Puesta en marcha del bot de Telegram
		 * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * */
		TelegramOptions telegramOptions = new TelegramOptions().setBotName("nfcAdminBot")
				.setBotToken("1240009222:AAFCKXVJRvKmNHgbQCgEt4S0p5dBfDQTWGw");
		bot = TelegramBot.create(vertx, telegramOptions).receiver(new LongPollingReceiver().onUpdate(handler -> {
			if (handler.getMessage().getText().toLowerCase().contains("/hola")) {
				bot.sendMessage(new SendMessage()
						.setText("Hola " + handler.getMessage().getFrom().getFirstName() + " ¿En qué puedo ayudarte?")
						.setChatId(handler.getMessage().getChatId()));
				
				/* * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * *
				 * Modelo que nos puede venir bien para ver como recibir informacion desde una
				 * API en este caso recibimos informacion del tiempo.
				 * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * */
			} else if (handler.getMessage().getText().toLowerCase().contains("/tiempo")) {
				WebClient client = WebClient.create(vertx);
				client.get(80, "api.openweathermap.org",
						"/data/2.5/forecast?id=2510911&APPID=39c0c4d7eb61c60b5809e7303412e70b&units=metric")
						.send(ar -> {
							if (ar.succeeded()) {
								HttpResponse<Buffer> response = ar.result();
								JsonObject object = response.bodyAsJsonObject();
								JsonArray list = object.getJsonArray("list");
								JsonObject lastWeather = list.getJsonObject(list.size() - 1);
								Float temp = lastWeather.getJsonObject("main").getFloat("temp");

								bot.sendMessage(new SendMessage()
										.setText("La tempertura actual en Sevilla es de " + temp + " grados")
										.setChatId(handler.getMessage().getChatId()));
							} else {
								bot.sendMessage(new SendMessage().setText("Vaya, algo ha salido mal")
										.setChatId(handler.getMessage().getChatId()));
							}
						});
				
				/* * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * *
				 * El usuario indica que quiere insertar datos en la tabla por lo que nosotros
				 * guardamos el keyword en una variable la cual vamos a utilizar posteriormente
				 * para saber que nos encontramos en esta seccion (de añadir a la base de
				 * datos), dicha variable es "seccion".
				 * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * */
			} else if (handler.getMessage().getText().toLowerCase().contains("/insertar")) {
				bot.sendMessage(new SendMessage()
						.setText("Hola " + handler.getMessage().getFrom().getFirstName()
								+ " Indicame en que tabla quieres" + " insertar\n")
						.setChatId(handler.getMessage().getChatId()));
				seccion.put(Integer.parseInt(handler.getMessage().getChatId()), "/insertar");
				tabla.put(Integer.parseInt(handler.getMessage().getChatId()), " ");
				bot.sendMessage(
						new SendMessage().setText("Tablas disponibles: ").setChatId(handler.getMessage().getChatId()));
				
				/* * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * *
				 * Un query para ver todas las tablas disponibles de un schema
				 * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * */
				mySQLPool.query("SELECT table_name FROM information_schema.tables WHERE table_schema = 'dad'", res -> {
					if (res.succeeded()) {
						RowSet<Row> resultSet = res.result();
						System.out.println("El número de elementos obtenidos es " + resultSet.size());
						for (Row row : resultSet) {
							bot.sendMessage(new SendMessage().setText("- " + row.getString("TABLE_NAME"))
									.setChatId(handler.getMessage().getChatId()));
						}
					} else {
						bot.sendMessage(new SendMessage().setText(res.cause().getMessage())
								.setChatId(handler.getMessage().getChatId()));
					}
				});
				/* * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * *
				 * A partir de aqui vamos a guardar el rastro de la tabla que hemos elegido
				 * mediante la variable "tabla", para que el bot nos haga una y otra vez
				 * preguntas hasta obtener todos los datos necesarios para rellenar la clase
				 * correspondiente.
				 * 
				 * Necesitamos la variable "tabla" para que cuando nosotros respondamos una
				 * pregunta de una tabla cualquiera, el bot sepa volver a la misma tabla y
				 * seguir por la siguiente pregunta.
				 * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * */

				/* * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * *
				 * Tabla: Comercio 
				 * Columnas: NombreComercio, telefono, CIF 
				 * KeywordTelegram: comercio
				 * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * */
			} else if ((handler.getMessage().getText().toLowerCase().contains("comercio")
					&& (seccion.get(Integer.parseInt(handler.getMessage().getChatId())) == "/insertar"))
					|| (seccion.get(Integer.parseInt(handler.getMessage().getChatId())) == "/insertar"
							&& tabla.get(Integer.parseInt(handler.getMessage().getChatId())) == "comercio")) {
				tablaComercio(handler);
				/* * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * *
				 * Tabla: Producto 
				 * Columnas: nombreProducto, codigoBarras, fabricante, telefono
				 * KeywordTelegram: producto
				 * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * */
			} else if ((handler.getMessage().getText().toLowerCase().contentEquals("producto")
					&& (seccion.get((Integer.parseInt(handler.getMessage().getChatId()))) == "/insertar"))
					|| (seccion.get((Integer.parseInt(handler.getMessage().getChatId()))) == "/insertar"
							&& tabla.get((Integer.parseInt(handler.getMessage().getChatId()))) == "producto")) {
				tablaProducto(handler);
				/* * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * *
				 * Tabla: Intolerancia 
				 * Columnas: nombreIntolerancia 
				 * KeywordTelegram: intolerancia
				 * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * */
			} else if ((handler.getMessage().getText().toLowerCase().contentEquals("intolerancia")
					&& (seccion.get((Integer.parseInt(handler.getMessage().getChatId()))) == "/insertar"))
					|| (seccion.get((Integer.parseInt(handler.getMessage().getChatId()))) == "/insertar"
							&& tabla.get((Integer.parseInt(handler.getMessage().getChatId()))) == "intolerancia")) {
				tablaIntolerancia(handler);
				/* * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * *
				 * Tabla: Ingrediente 
				 * Columnas: nombreIngrediente, idIntolerancia
				 * KeywordTelegram: ingrediente
				 * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * */
			} else if ((handler.getMessage().getText().toLowerCase().contentEquals("ingrediente")
					&& (seccion.get((Integer.parseInt(handler.getMessage().getChatId()))) == "/insertar"))
					|| (seccion.get((Integer.parseInt(handler.getMessage().getChatId()))) == "/insertar"
							&& tabla.get((Integer.parseInt(handler.getMessage().getChatId()))) == "ingrediente")) {
				tablaIngrediente(handler);
				/* * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * *
				 * Tabla: IngredientesProducto 
				 * Columnas: idIngrediente, idProducto
				 * KeywordTelegram: ingredientesproducto
				 * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * */
			} else if ((handler.getMessage().getText().toLowerCase().contentEquals("ingredientesproducto")
					&& (seccion.get((Integer.parseInt(handler.getMessage().getChatId()))) == "/insertar"))
					|| (seccion.get((Integer.parseInt(handler.getMessage().getChatId()))) == "/insertar" && tabla
							.get((Integer.parseInt(handler.getMessage().getChatId()))) == "ingredientesproducto")) {
				ingredientesProducto(handler);
				/* * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * *
				 * Tabla: intoleranciasIngrediente 
				 * Columnas: idIntolerancia, idIngrediente
				 * KeywordTelegram: intoleranciasingrediente
				 * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * */
			} else if ((handler.getMessage().getText().toLowerCase().contentEquals("intoleranciasingrediente")
					&& (seccion.get((Integer.parseInt(handler.getMessage().getChatId()))) == "/insertar"))
					|| (seccion.get((Integer.parseInt(handler.getMessage().getChatId()))) == "/insertar" && tabla
							.get((Integer.parseInt(handler.getMessage().getChatId()))) == "intoleranciasingrediente")) {
				intoleranciasIngrediente(handler);
			}
		}));
		bot.start();
	}
	
	void tablaIngrediente(Update handler) {
		if (handler.getMessage().getText().toLowerCase().contentEquals("ingrediente")) {
			bot.sendMessage(new SendMessage().setText("Hola " + handler.getMessage().getFrom().getFirstName()
					+ " Has seleccionado la tabla" + " ingrediente, voy a proceder a preguntarte los datos.."
					+ "\n" + "Nombre del ingrediente").setChatId(handler.getMessage().getChatId()));
			tabla.put((Integer.parseInt(handler.getMessage().getChatId())), "ingrediente");
			map.put(Integer.parseInt(handler.getMessage().getChatId()), new ingrediente());
		} else {
			ingrediente ingred = ingrediente.class
					.cast(map.get(Integer.parseInt(handler.getMessage().getChatId())));
			/*
			 * Para que cuando hayan 2 personas hablando con el bot no se mezclen los datos
			 */
			ingred = ingrediente.class.cast(map.get(Integer.parseInt(handler.getMessage().getChatId())));
			if (ingred.getNombreIngrediente() == null) {
				try {
					ingred.setNombreIngrediente(handler.getMessage().getText());
					map.put(Integer.parseInt(handler.getMessage().getChatId()), ingred);
					mySQLPool.preparedQuery(
							"INSERT INTO ingrediente (nombreIngrediente) VALUES (?)",
							Tuple.of(ingred.getNombreIngrediente()), handler1 -> {
								if (handler1.succeeded()) {
									bot.sendMessage(new SendMessage()
											.setText("Se han agregado "
													+ String.valueOf(handler1.result().rowCount()) + " filas")
											.setChatId(handler.getMessage().getChatId()));
								} else {
									bot.sendMessage(new SendMessage()
											.setText("Ha ocurrido el siguiente error: "
													+ handler1.cause().toString())
											.setChatId(handler.getMessage().getChatId()));
								}
							});
					/*
					 * Cuando se termine una conversacion, hay que vaciar las variables
					 */
					seccion.remove((Integer.parseInt(handler.getMessage().getChatId())));
					tabla.remove((Integer.parseInt(handler.getMessage().getChatId())));
					map.remove(Integer.parseInt(handler.getMessage().getChatId()));
				} catch (Exception e) {
					// TODO Auto-generated catch block
					bot.sendMessage(
							new SendMessage().setText(e.getMessage()).setChatId(handler.getMessage().getChatId()));
				}
				
			}
		}

	}
	
	void intoleranciasIngrediente(Update handler) {
		if (handler.getMessage().getText().toLowerCase().contentEquals("intoleranciasingrediente")) {
			bot.sendMessage(new SendMessage().setText(
					"Hola " + handler.getMessage().getFrom().getFirstName() + " Has seleccionado la tabla"
							+ " intoleranciasIngrediente, voy a proceder a preguntarte los datos.." + "\n\n"
							+ "idIntolerancia")
					.setChatId(handler.getMessage().getChatId()));

			mySQLPool.query("select idIntolerancia, nombreIntolerancia from intolerancia", res -> {
				if (res.succeeded()) {
					RowSet<Row> resultSet = res.result();
					System.out.println("El número de elementos obtenidos es " + resultSet.size());
					for (Row row : resultSet) {
						bot.sendMessage(new SendMessage()
								.setText(String.valueOf(row.getInteger("idIntolerancia")) + ". "
										+ row.getString("nombreIntolerancia"))
								.setChatId(handler.getMessage().getChatId()));
					}

				} else {
					bot.sendMessage(new SendMessage().setText(res.cause().getMessage())
							.setChatId(handler.getMessage().getChatId()));
				}
			});

			tabla.put((Integer.parseInt(handler.getMessage().getChatId())), "intoleranciasingrediente");
			map.put(Integer.parseInt(handler.getMessage().getChatId()), new intoleranciasIngrediente());
		} else {
			intoleranciasIngrediente intoIng = intoleranciasIngrediente.class
					.cast(map.get(Integer.parseInt(handler.getMessage().getChatId())));
			/*
			 * Para que cuando hayan 2 personas hablando con el bot no se mezclen los datos
			 */
			intoIng = intoleranciasIngrediente.class
					.cast(map.get(Integer.parseInt(handler.getMessage().getChatId())));

			if (intoIng.getIdIntolerancia() == null) {
				try {
					intoIng.setIdIntolerancia(Integer.parseInt(handler.getMessage().getText()));
					map.put(Integer.parseInt(handler.getMessage().getChatId()), intoIng);
					bot.sendMessage(new SendMessage().setText("idIngrediente\n")
							.setChatId(handler.getMessage().getChatId()));
					mySQLPool.query("select idIngrediente, nombreIngrediente from ingrediente", res -> {
						if (res.succeeded()) {
							RowSet<Row> resultSet = res.result();
							System.out.println("El número de elementos obtenidos es " + resultSet.size());
							for (Row row : resultSet) {
								bot.sendMessage(new SendMessage()
										.setText(String.valueOf(row.getInteger("idIngrediente")) + ". "
												+ row.getString("nombreIngrediente"))
										.setChatId(handler.getMessage().getChatId()));
							}

						} else {
							bot.sendMessage(new SendMessage().setText(res.cause().getMessage())
									.setChatId(handler.getMessage().getChatId()));
						}
					});
				} catch (Exception e) {
					// TODO Auto-generated catch block
					bot.sendMessage(new SendMessage().setText(e.getMessage())
							.setChatId(handler.getMessage().getChatId()));
				}
				
			} else if (intoIng.getIdIngrediente() == null) {
				try {
					intoIng.setIdIngrediente(Integer.parseInt(handler.getMessage().getText()));
					map.put(Integer.parseInt(handler.getMessage().getChatId()), intoIng);
					mySQLPool.preparedQuery(
							"INSERT INTO intoleranciasIngrediente (idIntolerancia, idIngrediente) VALUES (?,?)",
							Tuple.of(intoIng.getIdIntolerancia(), intoIng.getIdIngrediente()), handler1 -> {
								if (handler1.succeeded()) {
									bot.sendMessage(new SendMessage()
											.setText("Se han agregado "
													+ String.valueOf(handler1.result().rowCount()) + " filas")
											.setChatId(handler.getMessage().getChatId()));
								} else {
									bot.sendMessage(new SendMessage()
											.setText("Ha ocurrido el siguiente error: "
													+ handler1.cause().toString())
											.setChatId(handler.getMessage().getChatId()));
								}
							});
					/*
					 * Cuando se termine una conversacion, hay que vaciar las variables
					 */
					seccion.remove((Integer.parseInt(handler.getMessage().getChatId())));
					tabla.remove((Integer.parseInt(handler.getMessage().getChatId())));
					map.remove(Integer.parseInt(handler.getMessage().getChatId()));
				} catch (Exception e) {
					// TODO Auto-generated catch block
					bot.sendMessage(new SendMessage().setText(e.getMessage())
							.setChatId(handler.getMessage().getChatId()));
				}
				
			}
		}
	}
	
	void ingredientesProducto(Update handler) {
		if (handler.getMessage().getText().toLowerCase().contentEquals("ingredientesproducto")) {
			bot.sendMessage(new SendMessage().setText(
					"Hola " + handler.getMessage().getFrom().getFirstName() + " Has seleccionado la tabla"
							+ " ingredientesproducto, voy a proceder a preguntarte los datos.." + "\n\n"
							+ "idIngrediente")
					.setChatId(handler.getMessage().getChatId()));

			mySQLPool.query("select idIngrediente, nombreIngrediente from ingrediente", res -> {
				if (res.succeeded()) {
					RowSet<Row> resultSet = res.result();
					System.out.println("El número de elementos obtenidos es " + resultSet.size());
					for (Row row : resultSet) {
						bot.sendMessage(new SendMessage()
								.setText(String.valueOf(row.getInteger("idIngrediente")) + ". "
										+ row.getString("nombreIngrediente"))
								.setChatId(handler.getMessage().getChatId()));
					}

				} else {
					bot.sendMessage(new SendMessage().setText(res.cause().getMessage())
							.setChatId(handler.getMessage().getChatId()));
				}
			});
			tabla.put((Integer.parseInt(handler.getMessage().getChatId())), "ingredientesproducto");
			map.put(Integer.parseInt(handler.getMessage().getChatId()), new ingredientesProducto());
		} else {
			ingredientesProducto ingredp = ingredientesProducto.class
					.cast(map.get(Integer.parseInt(handler.getMessage().getChatId())));
			/*
			 * Para que cuando hayan 2 personas hablando con el bot no se mezclen los datos
			 */
			ingredp = ingredientesProducto.class
					.cast(map.get(Integer.parseInt(handler.getMessage().getChatId())));

			if (ingredp.getIdIngrediente() == null) {
				try {
					ingredp.setIdIngrediente(Integer.parseInt(handler.getMessage().getText()));
					map.put(Integer.parseInt(handler.getMessage().getChatId()), ingredp);
					bot.sendMessage(
							new SendMessage().setText("idProducto\n").setChatId(handler.getMessage().getChatId()));
					mySQLPool.query("select idProducto, nombreProducto from producto", res -> {
						if (res.succeeded()) {
							RowSet<Row> resultSet = res.result();
							System.out.println("El número de elementos obtenidos es " + resultSet.size());
							for (Row row : resultSet) {
								bot.sendMessage(new SendMessage()
										.setText(String.valueOf(row.getInteger("idProducto")) + ". "
												+ row.getString("nombreProducto"))
										.setChatId(handler.getMessage().getChatId()));
							}

						} else {
							bot.sendMessage(new SendMessage().setText(res.cause().getMessage())
									.setChatId(handler.getMessage().getChatId()));
						}
					});
				} catch (Exception e) {
					// TODO Auto-generated catch block
					bot.sendMessage(
							new SendMessage().setText(e.getMessage()).setChatId(handler.getMessage().getChatId()));
				}
				
			} else if (ingredp.getIdProducto() == null) {
				try {
					ingredp.setIdProducto(Integer.parseInt(handler.getMessage().getText()));
					map.put(Integer.parseInt(handler.getMessage().getChatId()), ingredp);
					mySQLPool.preparedQuery(
							"INSERT INTO ingredientesproducto (idIngrediente, IdProducto) VALUES (?,?)",
							Tuple.of(ingredp.getIdIngrediente(), ingredp.getIdProducto()), handler1 -> {
								if (handler1.succeeded()) {
									bot.sendMessage(new SendMessage()
											.setText("Se han agregado "
													+ String.valueOf(handler1.result().rowCount()) + " filas")
											.setChatId(handler.getMessage().getChatId()));
								} else {
									bot.sendMessage(new SendMessage()
											.setText("Ha ocurrido el siguiente error: "
													+ handler1.cause().toString())
											.setChatId(handler.getMessage().getChatId()));
								}
							});
					/*
					 * Cuando se termine una conversacion, hay que vaciar las variables
					 */
					seccion.remove((Integer.parseInt(handler.getMessage().getChatId())));
					tabla.remove((Integer.parseInt(handler.getMessage().getChatId())));
					map.remove(Integer.parseInt(handler.getMessage().getChatId()));
				} catch (Exception e) {
					// TODO Auto-generated catch block
					bot.sendMessage(new SendMessage().setText(e.getMessage())
							.setChatId(handler.getMessage().getChatId()));
				}
				
			}
		}
	}
	
	void tablaIntolerancia(Update handler) {
		if (handler.getMessage().getText().toLowerCase().contentEquals("intolerancia")) {
			bot.sendMessage(new SendMessage().setText("Hola " + handler.getMessage().getFrom().getFirstName()
					+ " Has seleccionado la tabla" + " intolerancia, voy a proceder a preguntarte los datos.."
					+ "\n" + "Nombre de la intolerancia").setChatId(handler.getMessage().getChatId()));
			tabla.put((Integer.parseInt(handler.getMessage().getChatId())), "intolerancia");
			map.put(Integer.parseInt(handler.getMessage().getChatId()), new intolerancia());
		} else {
			intolerancia intoler = intolerancia.class
					.cast(map.get(Integer.parseInt(handler.getMessage().getChatId())));
			/*
			 * Para que cuando hayan 2 personas hablando con el bot no se mezclen los datos
			 */
			intoler = intolerancia.class.cast(map.get(Integer.parseInt(handler.getMessage().getChatId())));
			if (intoler.getNombreIntolerancia() == null) {
				try {
					intoler.setNombreIntolerancia(handler.getMessage().getText());
					map.put(Integer.parseInt(handler.getMessage().getChatId()), intoler);

					mySQLPool.preparedQuery("INSERT INTO intolerancia (nombreIntolerancia) VALUES (?)",
							Tuple.of(intoler.getNombreIntolerancia()), handler1 -> {
								if (handler1.succeeded()) {
									bot.sendMessage(new SendMessage()
											.setText("Se han agregado "
													+ String.valueOf(handler1.result().rowCount()) + " filas")
											.setChatId(handler.getMessage().getChatId()));
								} else {
									bot.sendMessage(new SendMessage()
											.setText("Ha ocurrido el siguiente error: "
													+ handler1.cause().toString())
											.setChatId(handler.getMessage().getChatId()));
								}
							});

					/*
					 * Cuando se termine una conversacion, hay que vaciar las variables
					 */
					seccion.remove((Integer.parseInt(handler.getMessage().getChatId())));
					tabla.remove((Integer.parseInt(handler.getMessage().getChatId())));
					map.remove(Integer.parseInt(handler.getMessage().getChatId()));
				} catch (Exception e) {
					// TODO Auto-generated catch block
					bot.sendMessage(
							new SendMessage().setText(e.getMessage()).setChatId(handler.getMessage().getChatId()));
				}
			}
		}
	}
	
	void tablaProducto(Update handler) {
		if (handler.getMessage().getText().toLowerCase().contentEquals("producto")) {
			bot.sendMessage(new SendMessage().setText("Hola " + handler.getMessage().getFrom().getFirstName()
					+ " Has seleccionado la tabla" + " producto, voy a proceder a preguntarte los datos.."
					+ "\n" + "Nombre del producto?").setChatId(handler.getMessage().getChatId()));
			tabla.put((Integer.parseInt(handler.getMessage().getChatId())), "producto");
			map.put(Integer.parseInt(handler.getMessage().getChatId()), new producto());
		} else {
			producto prod = producto.class.cast(map.get(Integer.parseInt(handler.getMessage().getChatId())));
			/*
			 * Para que cuando hayan 2 personas hablando con el bot no se mezclen los datos
			 */
			prod = producto.class.cast(map.get(Integer.parseInt(handler.getMessage().getChatId())));
			if (prod.getNombreProducto() == null) {
				try {
					prod.setNombreProducto(handler.getMessage().getText());
					map.put(Integer.parseInt(handler.getMessage().getChatId()), prod);
					bot.sendMessage(
							new SendMessage().setText("Codigo Barras").setChatId(handler.getMessage().getChatId()));
				} catch (Exception e) {
					// TODO Auto-generated catch block
					bot.sendMessage(
							new SendMessage().setText(e.getMessage()).setChatId(handler.getMessage().getChatId()));
				}
				
			} else if (prod.getCodigoBarras() == null) {
				try {
					prod.setCodigoBarras(Long.parseLong(handler.getMessage().getText()));
					map.put(Integer.parseInt(handler.getMessage().getChatId()), prod);
					bot.sendMessage(
							new SendMessage().setText("Fabricante").setChatId(handler.getMessage().getChatId()));
				} catch (Exception e) {
					// TODO Auto-generated catch block
					bot.sendMessage(
							new SendMessage().setText(e.getMessage()).setChatId(handler.getMessage().getChatId()));
				}
				
			} else if (prod.getFabricante() == null) {
				try {
					prod.setFabricante(handler.getMessage().getText());
					map.put(Integer.parseInt(handler.getMessage().getChatId()), prod);
					bot.sendMessage(
							new SendMessage().setText("Teléfono").setChatId(handler.getMessage().getChatId()));
				} catch (Exception e) {
					// TODO Auto-generated catch block
					bot.sendMessage(
							new SendMessage().setText(e.getMessage()).setChatId(handler.getMessage().getChatId()));
				}
				
			} else if (prod.getTelefono() == null) {
				try {
					prod.setTelefono(Long.parseLong(handler.getMessage().getText()));
					map.put(Integer.parseInt(handler.getMessage().getChatId()), prod);
					mySQLPool.preparedQuery(
							"INSERT INTO producto (nombreProducto, codigoBarras, fabricante, telefono) VALUES (?,?,?,?)",
							Tuple.of(prod.getNombreProducto(), prod.getCodigoBarras(), prod.getFabricante(),
									prod.getTelefono()),
							handler1 -> {
								if (handler1.succeeded()) {
									bot.sendMessage(new SendMessage()
											.setText("Se han agregado "
													+ String.valueOf(handler1.result().rowCount()) + " filas")
											.setChatId(handler.getMessage().getChatId()));
								} else {
									bot.sendMessage(new SendMessage()
											.setText("Ha ocurrido el siguiente error: "
													+ handler1.cause().toString())
											.setChatId(handler.getMessage().getChatId()));
								}
							});

					/*
					 * Cuando se termine una conversacion, hay que vaciar las variables
					 */
					seccion.remove((Integer.parseInt(handler.getMessage().getChatId())));
					tabla.remove((Integer.parseInt(handler.getMessage().getChatId())));
					map.remove(Integer.parseInt(handler.getMessage().getChatId()));
				} catch (Exception e) {
					// TODO Auto-generated catch block
					bot.sendMessage(
							new SendMessage().setText(e.getMessage()).setChatId(handler.getMessage().getChatId()));
				}
				
			}
		}

		
	}
	
	void tablaComercio(Update handler) {
		if (handler.getMessage().getText().toLowerCase().contentEquals("comercio")) {
			bot.sendMessage(new SendMessage().setText("Hola " + handler.getMessage().getFrom().getFirstName()
					+ " Has seleccionado la tabla" + " comercio, voy a proceder a preguntarte los datos.."
					+ "\n" + "Nombre del comercio?").setChatId(handler.getMessage().getChatId()));
			tabla.put((Integer.parseInt(handler.getMessage().getChatId())), "comercio");
			map.put(Integer.parseInt(handler.getMessage().getChatId()), new comercio());
		} else {
			comercio com = comercio.class.cast(map.get(Integer.parseInt(handler.getMessage().getChatId())));
			/*
			 * Para que cuando hayan 2 personas hablando con el bot no se mezclen los datos
			 */
			com = comercio.class.cast(map.get(Integer.parseInt(handler.getMessage().getChatId())));
			if (com.getNombreComercio() == null) {
				try {
					com.setNombreComercio(handler.getMessage().getText());
					map.put(Integer.parseInt(handler.getMessage().getChatId()), com);
					bot.sendMessage(
							new SendMessage().setText("Telefono").setChatId(handler.getMessage().getChatId()));
				} catch (Exception e) {
					// TODO Auto-generated catch block
					bot.sendMessage(new SendMessage().setText(e.getMessage()).setChatId(handler.getMessage().getChatId()));
				}

			} else if ((com.getTelefono() == null)) {
				try {
					com.setTelefono(Long.parseLong(handler.getMessage().getText()));
					map.put(Integer.parseInt(handler.getMessage().getChatId()), com);
					bot.sendMessage(new SendMessage().setText("CIF").setChatId(handler.getMessage().getChatId()));
				} catch (Exception e) {
					// TODO Auto-generated catch block
					bot.sendMessage(new SendMessage().setText(e.getMessage()).setChatId(handler.getMessage().getChatId()));
				}
				
			} else if (com.getCIF() == null) {
				try {
					com.setCIF(handler.getMessage().getText());
					map.put(Integer.parseInt(handler.getMessage().getChatId()), com);
					mySQLPool.preparedQuery("INSERT INTO comercio (nombreComercio, telefono, CIF) VALUES (?,?,?)",
							Tuple.of(com.getNombreComercio(), com.getTelefono(), com.getCIF()), handler1 -> {
								if (handler1.succeeded()) {
									bot.sendMessage(new SendMessage()
											.setText("Se han agregado "
													+ String.valueOf(handler1.result().rowCount()) + " filas")
											.setChatId(handler.getMessage().getChatId()));
								} else {
									bot.sendMessage(new SendMessage()
											.setText("Ha ocurrido el siguiente error: "
													+ handler1.cause().toString())
											.setChatId(handler.getMessage().getChatId()));
								}
							});

					/*
					 * Cuando se termine una conversacion, hay que vaciar las variables
					 */
					seccion.remove((Integer.parseInt(handler.getMessage().getChatId())));
					tabla.remove((Integer.parseInt(handler.getMessage().getChatId())));
					map.remove(Integer.parseInt(handler.getMessage().getChatId()));
				} catch (Exception e) {
					// TODO Auto-generated catch block
					bot.sendMessage(new SendMessage()
							.setText(e.getMessage())
							.setChatId(handler.getMessage().getChatId()));
				}
				
			}
		}
	}

}