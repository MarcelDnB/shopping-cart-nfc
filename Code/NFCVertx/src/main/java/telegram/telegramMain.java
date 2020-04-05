package telegram;

import java.util.HashMap;
import java.util.Map;

import org.schors.vertx.telegram.bot.LongPollingReceiver;
import org.schors.vertx.telegram.bot.TelegramBot;
import org.schors.vertx.telegram.bot.TelegramOptions;
import org.schors.vertx.telegram.bot.api.methods.SendMessage;

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
import io.vertx.sqlclient.Tuple;
import types.comercio;
import types.ingrediente;
import types.ingredientesProducto;
import types.intolerancia;
import types.producto;

public class telegramMain extends AbstractVerticle {

	private TelegramBot bot;
	private Map<Integer, String> tabla = new HashMap<Integer,String>();
	private Map<Integer, String> seccion = new HashMap<Integer, String>();
	private MySQLPool mySQLPool;
	private Map<Integer, Object> map = new HashMap<Integer, Object>();

	/*
	 * bot: variable que nos entrega las funcionalidades del bot de Telegram.
	 * 
	 * tabla: nos sirve para que el bot sepa de que tabla nos estaba haciendo las preguntas y seguir con ellas.
	 * 
	 * seccion: nos sirve para que el bot sepa en que pregunta se ha quedado y seguir por la siguiente, para ello,
	 * en el constructor vacio de los tipos asignamos las variables a null, ya que de esta forma podremos ver
	 * que variables ya se han completado y cuales no.
	 * 
	 * mySQLPool: nos va a permitir consultar e introducir datos en la base de datos
	 * 
	 * map: nos ayudamos de un map para poder diferenciar los diferentes usuarios, mediante su chatId que nos
	 * proporciona el bot de Telegram.
	 */
	
	@Override
	public void start(Promise<Void> future) {
		/*
		 * Conexion a la base de datos MySQL
		 */
		MySQLConnectOptions mySQLConnectOptions = new MySQLConnectOptions().setPort(3306).setHost("localhost")
				.setDatabase("DAD").setUser("dad").setPassword("dnbmusic");
		PoolOptions poolOptions = new PoolOptions().setMaxSize(5);
		mySQLPool = MySQLPool.pool(vertx, mySQLConnectOptions, poolOptions);

		/*
		 * Puesta en marcha del bot de Telegram
		 */
		TelegramOptions telegramOptions = new TelegramOptions().setBotName("nfcAdminBot")
				.setBotToken("1240009222:AAFCKXVJRvKmNHgbQCgEt4S0p5dBfDQTWGw");
		bot = TelegramBot.create(vertx, telegramOptions).receiver(new LongPollingReceiver().onUpdate(handler -> {
			if (handler.getMessage().getText().toLowerCase().contains("/hola")) {
				bot.sendMessage(new SendMessage()
						.setText("Hola " + handler.getMessage().getFrom().getFirstName() + " ¿en qué puedo ayudarte?")
						.setChatId(handler.getMessage().getChatId()));
				/*
				 * Modelo que nos puede venir bien para ver como recibir informacion desde una API
				 * en este caso recibimos informacion del tiempo.
				 */
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
				
				/*
				 * El usuario indica que quiere insertar datos en la tabla por lo que nosotros guardamos
				 * el keyword en una variable la cual vamos a utilizar posteriormente para saber que nos 
				 * encontramos en esta seccion (de añadir a la base de datos), dicha variable es "seccion".
				 */
			} else if (handler.getMessage().getText().toLowerCase().contains("/insertar")) {
				bot.sendMessage(new SendMessage()
						.setText("Hola " + handler.getMessage().getFrom().getFirstName()
								+ " Indicame en que tabla quieres" + " insertar")
						.setChatId(handler.getMessage().getChatId()));
				seccion.put(Integer.parseInt(handler.getMessage().getChatId()), "/insertar");
				tabla.put(Integer.parseInt(handler.getMessage().getChatId()), " ");
				System.out.println(seccion.get((Integer.parseInt(handler.getMessage().getChatId()))) + "\n"
						+ "\n\n\n\n\n\n\n\n");
				/*
				 * A partir de aqui vamos a guardar el rastro de la tabla que hemos elegido mediante la variable
				 * "tabla", para que el bot nos haga una y otra vez preguntas hasta obtener todos los datos necesarios
				 * para rellenar la clase correspondiente.
				 * 
				 * Necesitamos la variable "tabla" para que cuando nosotros respondamos una pregunta de una tabla
				 * cualquiera, el bot sepa volver a la misma tabla y seguir por la siguiente pregunta.
				 */
				
				
				/*
				 * Tabla: Comercio
				 * Columnas: NombreComercio, telefono, CIF
				 * KeywordTelegram: comercio
				 */
			} else if ((handler.getMessage().getText().toLowerCase().contains("comercio") 
					&& (seccion.get(Integer.parseInt(handler.getMessage().getChatId())) == "/insertar"))
					|| (seccion.get(Integer.parseInt(handler.getMessage().getChatId())) == "/insertar" && tabla.get(Integer.parseInt(handler.getMessage().getChatId())) == "comercio")) {
				if (handler.getMessage().getText().toLowerCase().contains("comercio")) {
					bot.sendMessage(new SendMessage().setText("Hola " + handler.getMessage().getFrom().getFirstName()
							+ " Has seleccionado la tabla" + " comercio, voy a proceder a preguntarte los datos.."
							+ "\n" + "Nombre del comercio?").setChatId(handler.getMessage().getChatId()));
					tabla.put((Integer.parseInt(handler.getMessage().getChatId())), "comercio");
					map.put(Integer.parseInt(handler.getMessage().getChatId()), new comercio());
				} else {
					comercio com = comercio.class.cast(map.get(Integer.parseInt(handler.getMessage().getChatId())));
					/*
					 *  Para que cuando hayan 2 personas hablando con el bot no se mezclen los datos
					 */
					com = comercio.class.cast(map.get(Integer.parseInt(handler.getMessage().getChatId())));
					if (com.getNombreComercio() == null) {
						com.setNombreComercio(handler.getMessage().getText());
						map.put(Integer.parseInt(handler.getMessage().getChatId()), com);
						bot.sendMessage(
								new SendMessage().setText("Telefono").setChatId(handler.getMessage().getChatId()));
					} else if ((com.getTelefono() == null)) {
						com.setTelefono(Long.parseLong(handler.getMessage().getText()));
						map.put(Integer.parseInt(handler.getMessage().getChatId()), com);
						bot.sendMessage(new SendMessage().setText("CIF").setChatId(handler.getMessage().getChatId()));
					} else if (com.getCIF() == null) {
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
					}
				}
				/*
				 * Tabla: Producto
				 * Columnas: nombreProducto, codigoBarras, fabricante, telefono
				 * KeywordTelegram: producto
				 */
			} else if ((handler.getMessage().getText().toLowerCase().contentEquals("producto")
					&& (seccion.get((Integer.parseInt(handler.getMessage().getChatId()))) == "/insertar")) || (seccion.get((Integer.parseInt(handler.getMessage().getChatId()))) == "/insertar" && tabla.get((Integer.parseInt(handler.getMessage().getChatId()))) == "producto")) {
				
				if (handler.getMessage().getText().toLowerCase().contains("producto")) {
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
						prod.setNombreProducto(handler.getMessage().getText());
						map.put(Integer.parseInt(handler.getMessage().getChatId()), prod);
						bot.sendMessage(
								new SendMessage().setText("Codigo Barras").setChatId(handler.getMessage().getChatId()));
					} else if (prod.getCodigoBarras() == null) {
						prod.setCodigoBarras(Long.parseLong(handler.getMessage().getText()));
						map.put(Integer.parseInt(handler.getMessage().getChatId()), prod);
						bot.sendMessage(
								new SendMessage().setText("Fabricante").setChatId(handler.getMessage().getChatId()));
					} else if (prod.getFabricante() == null) {
						prod.setFabricante(handler.getMessage().getText());
						map.put(Integer.parseInt(handler.getMessage().getChatId()), prod);
						bot.sendMessage(
								new SendMessage().setText("Teléfono").setChatId(handler.getMessage().getChatId()));
					} else if (prod.getTelefono() == null) {
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
					}
				}
				
				/* Tabla: Intolerancia
				 * Columnas: nombreIntolerancia
				 * KeywordTelegram: intolerancia
				 */
			} else if ((handler.getMessage().getText().toLowerCase().contentEquals("intolerancia")
					&& (seccion.get((Integer.parseInt(handler.getMessage().getChatId()))) == "/insertar")) || (seccion.get((Integer.parseInt(handler.getMessage().getChatId()))) == "/insertar" && tabla.get((Integer.parseInt(handler.getMessage().getChatId()))) == "intolerancia")) {
				
				if (handler.getMessage().getText().toLowerCase().contains("intolerancia")) {
					bot.sendMessage(new SendMessage().setText("Hola " + handler.getMessage().getFrom().getFirstName()
							+ " Has seleccionado la tabla" + " intolerancia, voy a proceder a preguntarte los datos.."
							+ "\n" + "Nombre de la intolerancia").setChatId(handler.getMessage().getChatId()));
					tabla.put((Integer.parseInt(handler.getMessage().getChatId())), "intolerancia");
					map.put(Integer.parseInt(handler.getMessage().getChatId()), new intolerancia());
				} else {
					intolerancia intoler = intolerancia.class.cast(map.get(Integer.parseInt(handler.getMessage().getChatId())));
					/*
					 *  Para que cuando hayan 2 personas hablando con el bot no se mezclen los datos
					 */
					intoler = intolerancia.class.cast(map.get(Integer.parseInt(handler.getMessage().getChatId())));
					if (intoler.getNombreIntolerancia() == null) {
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
						 *  Cuando se termine una conversacion, hay que vaciar las variables
						 */
						seccion.remove((Integer.parseInt(handler.getMessage().getChatId())));
						tabla.remove((Integer.parseInt(handler.getMessage().getChatId())));
						map.remove(Integer.parseInt(handler.getMessage().getChatId()));

					}
				}
				
				/* Tabla: Ingrediente
				 * Columnas: nombreIngrediente, idIntolerancia
				 * KeywordTelegram: ingrediente
				 */
				
			} else if ((handler.getMessage().getText().toLowerCase().contentEquals("ingrediente")
					&& (seccion.get((Integer.parseInt(handler.getMessage().getChatId()))) == "/insertar")) || (seccion.get((Integer.parseInt(handler.getMessage().getChatId()))) == "/insertar" && tabla.get((Integer.parseInt(handler.getMessage().getChatId()))) == "ingrediente")) {
				
				if (handler.getMessage().getText().toLowerCase().contains("ingrediente")) {
					bot.sendMessage(new SendMessage().setText("Hola " + handler.getMessage().getFrom().getFirstName()
							+ " Has seleccionado la tabla" + " ingrediente, voy a proceder a preguntarte los datos.."
							+ "\n" + "Nombre del ingrediente").setChatId(handler.getMessage().getChatId()));
					tabla.put((Integer.parseInt(handler.getMessage().getChatId())), "ingrediente");
					map.put(Integer.parseInt(handler.getMessage().getChatId()), new ingrediente());
				} else {
					ingrediente ingred = ingrediente.class
							.cast(map.get(Integer.parseInt(handler.getMessage().getChatId())));
					/*
					 *  Para que cuando hayan 2 personas hablando con el bot no se mezclen los datos
					 */
					ingred = ingrediente.class.cast(map.get(Integer.parseInt(handler.getMessage().getChatId())));

					if (ingred.getNombreIngrediente() == null) {
						ingred.setNombreIngrediente(handler.getMessage().getText());
						map.put(Integer.parseInt(handler.getMessage().getChatId()), ingred);
						bot.sendMessage(new SendMessage().setText("idIntolerancia")
								.setChatId(handler.getMessage().getChatId()));
					} else if (ingred.getIdIntolerancia() == null) {
						ingred.setIdIntolerancia(Integer.parseInt(handler.getMessage().getText()));
						map.put(Integer.parseInt(handler.getMessage().getChatId()), ingred);
						mySQLPool.preparedQuery(
								"INSERT INTO ingrediente (nombreIngrediente, idIntolerancia) VALUES (?,?)",
								Tuple.of(ingred.getNombreIngrediente(), ingred.getIdIntolerancia()), handler1 -> {
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
						 *  Cuando se termine una conversacion, hay que vaciar las variables
						 */
						seccion.remove((Integer.parseInt(handler.getMessage().getChatId())));
						tabla.remove((Integer.parseInt(handler.getMessage().getChatId())));
						map.remove(Integer.parseInt(handler.getMessage().getChatId()));
					}
				}
				
				/* Tabla: IngredientesProducto
				 * Columnas: idIngrediente, idProducto
				 * KeywordTelegram: ingredientesproducto
				 */
			} else if ((handler.getMessage().getText().toLowerCase().contentEquals("ingredientesproducto")
					&& (seccion.get((Integer.parseInt(handler.getMessage().getChatId()))) == "/insertar")) || (seccion.get((Integer.parseInt(handler.getMessage().getChatId()))) == "/insertar" && tabla.get((Integer.parseInt(handler.getMessage().getChatId()))) == "ingredientesproducto")) {
				
				if (handler.getMessage().getText().toLowerCase().contains("ingredientesproducto")) {
					bot.sendMessage(new SendMessage().setText(
							"Hola " + handler.getMessage().getFrom().getFirstName() + " Has seleccionado la tabla"
									+ " ingredientesproducto, voy a proceder a preguntarte los datos.." + "\n"
									+ "idIngrediente")
							.setChatId(handler.getMessage().getChatId()));
					tabla.put((Integer.parseInt(handler.getMessage().getChatId())), "ingredientesproducto");
					map.put(Integer.parseInt(handler.getMessage().getChatId()), new ingredientesProducto());
				} else {
					ingredientesProducto ingredp = ingredientesProducto.class
							.cast(map.get(Integer.parseInt(handler.getMessage().getChatId())));
					/*
					 *  Para que cuando hayan 2 personas hablando con el bot no se mezclen los datos
					 */
					ingredp = ingredientesProducto.class
							.cast(map.get(Integer.parseInt(handler.getMessage().getChatId())));

					if (ingredp.getIdIngrediente() == null) {
						ingredp.setIdIngrediente(Integer.parseInt(handler.getMessage().getText()));
						map.put(Integer.parseInt(handler.getMessage().getChatId()), ingredp);
						bot.sendMessage(
								new SendMessage().setText("idProducto").setChatId(handler.getMessage().getChatId()));
					} else if (ingredp.getIdProducto() == null) {
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
						 *  Cuando se termine una conversacion, hay que vaciar las variables
						 */
						seccion.remove((Integer.parseInt(handler.getMessage().getChatId())));
						tabla.remove((Integer.parseInt(handler.getMessage().getChatId())));
						map.remove(Integer.parseInt(handler.getMessage().getChatId()));
					}
				}
			}
		}));
		bot.start();
	}
}