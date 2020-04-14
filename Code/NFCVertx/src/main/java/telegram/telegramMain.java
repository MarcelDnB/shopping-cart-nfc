package telegram;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.schors.vertx.telegram.bot.LongPollingReceiver;
import org.schors.vertx.telegram.bot.TelegramBot;
import org.schors.vertx.telegram.bot.TelegramOptions;
import org.schors.vertx.telegram.bot.api.methods.SendMessage;
import org.schors.vertx.telegram.bot.api.types.Update;
import io.vertx.core.AbstractVerticle;
import io.vertx.core.Promise;
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
	private Map<Integer, String> ruta = new HashMap<Integer, String>(); // Al no disponer de una clase, poder seguir el rastro en las modificaciones
	
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
				

			}
				/* * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * *
				 * El usuario indica que quiere insertar datos en la tabla por lo que nosotros
				 * guardamos el keyword en una variable la cual vamos a utilizar posteriormente
				 * para saber que nos encontramos en esta seccion (de añadir a la base de
				 * datos), dicha variable es "seccion".
				 * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * */
			 else if (handler.getMessage().getText().toLowerCase().contains("/insertar")) {
				insertar(handler);
				
				/* * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * *
				 * El usuario indica que quiere ver cierta información de la base de datos,
				 * el mecanismo es igual al anterior, guardamos en "seccion" 'info' para saber donde 
				 * nos encontramos y hacer los bloques if-else correspondientes.
				 * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * */
			}else if(handler.getMessage().getText().toLowerCase().contains("/info")) {
				info(handler);
				
				
			}else if(handler.getMessage().getText().toLowerCase().contains("/modificar")) {
				modificar(handler);
			
				
				
				
			}else if(handler.getMessage().getText().toLowerCase().contains("/eliminar")) {
				eliminar(handler);
				
				
			}else if((handler.getMessage().getText().toLowerCase().contentEquals("a")
					&& (seccion.get(Integer.parseInt(handler.getMessage().getChatId())) == "/eliminar"))
					|| (seccion.get(Integer.parseInt(handler.getMessage().getChatId())) == "/eliminar"
							&& tabla.get(Integer.parseInt(handler.getMessage().getChatId())) == "a")) {
				eliminarProducto(handler);
			
			}else if((handler.getMessage().getText().toLowerCase().contentEquals("a")
					&& (seccion.get(Integer.parseInt(handler.getMessage().getChatId())) == "/modificar"))
					|| (seccion.get(Integer.parseInt(handler.getMessage().getChatId())) == "/modificar"
							&& tabla.get(Integer.parseInt(handler.getMessage().getChatId())) == "a")) {
				modificarProducto(handler);
			}else if((handler.getMessage().getText().toLowerCase().contentEquals("b")
					&& (seccion.get(Integer.parseInt(handler.getMessage().getChatId())) == "/modificar"))
					|| (seccion.get(Integer.parseInt(handler.getMessage().getChatId())) == "/modificar"
							&& tabla.get(Integer.parseInt(handler.getMessage().getChatId())) == "b")) {
				modificarIngrediente(handler);
			}else if((handler.getMessage().getText().toLowerCase().contentEquals("c")
					&& (seccion.get(Integer.parseInt(handler.getMessage().getChatId())) == "/modificar"))
					|| (seccion.get(Integer.parseInt(handler.getMessage().getChatId())) == "/modificar"
							&& tabla.get(Integer.parseInt(handler.getMessage().getChatId())) == "c")) {
				modificarIntolerancia(handler);
			
				
				
			/* * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * *
			 * Para entender la variable tabla ver el comentario siguiente, en este caso vamos a reutilizar
			 * dicha variable para guardar el rastro de la opción que hemos elegido, en este caso a,b o c
			 * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * */
			
			
			/* * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * *
			 * Se van a ver los productos escaneados y la cantidad de éstos filtrados por cierta intolerancia,
			 * es decir, vamos a poder ver el interés del usuario con cierta intolerancia hacia ciertos
			 * productos. 
			 * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * */
			}else if ((handler.getMessage().getText().toLowerCase().contentEquals("a")
					&& (seccion.get(Integer.parseInt(handler.getMessage().getChatId())) == "/info"))
					|| (seccion.get(Integer.parseInt(handler.getMessage().getChatId())) == "/info"
							&& tabla.get(Integer.parseInt(handler.getMessage().getChatId())) == "a")) {
				infoProductosEscaneados(handler);
				
				/* * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * *
				 * Introduciendo una palabra clave nos devolverá todos los productos que contengan dicha palabra,
				 * por lo que será util por ejemplo para ver si ya existe cierto producto en la base de datos.
				 * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * */
			}else if((handler.getMessage().getText().toLowerCase().contentEquals("b")
					&& (seccion.get(Integer.parseInt(handler.getMessage().getChatId())) == "/info"))
					|| (seccion.get(Integer.parseInt(handler.getMessage().getChatId())) == "/info"
							&& tabla.get(Integer.parseInt(handler.getMessage().getChatId())) == "b")) {
				comprobarExistenciaProducto(handler);
				
				/* * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * *
				 * Se obtiene el fabricante y numero de teléfono de éste introduciendo el ID de producto
				 * correspondiente.
				 * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * */
			}else if((handler.getMessage().getText().toLowerCase().contentEquals("c")
					&& (seccion.get(Integer.parseInt(handler.getMessage().getChatId())) == "/info"))
					|| (seccion.get(Integer.parseInt(handler.getMessage().getChatId())) == "/info"
							&& tabla.get(Integer.parseInt(handler.getMessage().getChatId())) == "c")) {
				informacionProducto(handler);
			
			
			
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
			}else if ((handler.getMessage().getText().toLowerCase().contains("comercio")
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
	
	/* Menú */
	void info(Update handler) {
		bot.sendMessage(new SendMessage()
				.setText("A continuación se indica la información disponible: \n"
						+ " a. Productos escaneados por ususarios con cierta intolerancia" + "\n" +
						"Seleccione la petición deseada, introduciendo su índice:"+ "\n" + 
						"b. Comprobar si un producto está en la base de datos\n" + 
						"c. Ver el nombre y numero de teléfono del fabricante de un producto\n")
				.setChatId(handler.getMessage().getChatId()));
		seccion.put(Integer.parseInt(handler.getMessage().getChatId()), "/info");
		tabla.put(Integer.parseInt(handler.getMessage().getChatId()), " ");
	}
	void insertar(Update handler) {
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
	}
	void modificar(Update handler) {
		bot.sendMessage(new SendMessage()
				.setText("¿Qué tabla desea modificar?\n\n"
						+ "a. Producto\nb. Ingrediente\nc. Intolerancia")
				.setChatId(handler.getMessage().getChatId()));
		seccion.put(Integer.parseInt(handler.getMessage().getChatId()), "/modificar");
		tabla.put(Integer.parseInt(handler.getMessage().getChatId()), " ");
		ruta.put(Integer.parseInt(handler.getMessage().getChatId()), " ");
		
	}
	void eliminar(Update handler) {
		bot.sendMessage(new SendMessage()
				.setText("¿De qué tabla desea eliminar?\n\n"
						+ "a. Producto\n")
				.setChatId(handler.getMessage().getChatId()));
		seccion.put(Integer.parseInt(handler.getMessage().getChatId()), "/eliminar");
		tabla.put(Integer.parseInt(handler.getMessage().getChatId()), " ");
		ruta.put(Integer.parseInt(handler.getMessage().getChatId()), " "); //se necesita?
	}
	/* Info */
	void infoProductosEscaneados(Update handler) {
		if((handler.getMessage().getText().toLowerCase().contentEquals("a")
				&& (seccion.get(Integer.parseInt(handler.getMessage().getChatId())) == "/info"))) {
			tabla.put((Integer.parseInt(handler.getMessage().getChatId())), "a");
			bot.sendMessage(new SendMessage()
					.setText("¿De que intolerancia desea ver los productos escaneados por los usuarios?, alguno"
							+ " de los ID's mostrados a continuación: " + "\n")
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
			
		}else {
			String s = handler.getMessage().getText();

			mySQLPool.query("select idProducto,nombreProducto, count(idProducto) from productosusuario natural join producto where productosusuario.idProducto in (select idProducto from productosusuario natural join producto \r\n" + 
					"natural join ingredientesproducto natural join ingrediente natural join intoleranciasingrediente natural join intolerancia where idIntolerancia=" + Integer.parseInt(s) +") group by IdProducto", res -> {
				if (res.succeeded()) {
					RowSet<Row> resultSet = res.result();
					System.out.println("El número de elementos obtenidos es " + resultSet.size());
					for (Row row : resultSet) {
						bot.sendMessage(new SendMessage()
								.setText(String.valueOf(row.getString("nombreProducto")) + " - cantidad="
										+ row.getInteger("count(idProducto)"))
								.setChatId(handler.getMessage().getChatId()));
					}

				} else {
					bot.sendMessage(new SendMessage().setText(res.cause().getMessage())
							.setChatId(handler.getMessage().getChatId()));
				}
			});
			seccion.remove((Integer.parseInt(handler.getMessage().getChatId())));
			tabla.remove((Integer.parseInt(handler.getMessage().getChatId())));
			map.remove(Integer.parseInt(handler.getMessage().getChatId()));
		}
		
		
	}
	void comprobarExistenciaProducto(Update handler) {
		if((handler.getMessage().getText().toLowerCase().contentEquals("b")
				&& (seccion.get(Integer.parseInt(handler.getMessage().getChatId())) == "/info"))) {
			tabla.put((Integer.parseInt(handler.getMessage().getChatId())), "b");
			bot.sendMessage(new SendMessage()
					.setText("Introduzca el nombre del producto a buscar: " + "\n")
					.setChatId(handler.getMessage().getChatId()));
		}else {
			String s = handler.getMessage().getText();

			mySQLPool.query("select idProducto, nombreProducto from producto where nombreProducto like '%" +
			s + "%'", res -> {
				if (res.succeeded()) {
					RowSet<Row> resultSet = res.result();
					System.out.println("El número de elementos obtenidos es " + resultSet.size());
					for (Row row : resultSet) {
						bot.sendMessage(new SendMessage()
								.setText(row.getInteger("idProducto")+". "+row.getString("nombreProducto"))
								.setChatId(handler.getMessage().getChatId()));
					}

				} else {
					bot.sendMessage(new SendMessage().setText(res.cause().getMessage())
							.setChatId(handler.getMessage().getChatId()));
				}
			});
			seccion.remove((Integer.parseInt(handler.getMessage().getChatId())));
			tabla.remove((Integer.parseInt(handler.getMessage().getChatId())));
			map.remove(Integer.parseInt(handler.getMessage().getChatId()));
		}
	}
	void informacionProducto(Update handler) {
		if((handler.getMessage().getText().toLowerCase().contentEquals("c")
				&& (seccion.get(Integer.parseInt(handler.getMessage().getChatId())) == "/info"))) {
			tabla.put((Integer.parseInt(handler.getMessage().getChatId())), "c");
			bot.sendMessage(new SendMessage()
					.setText("Introduzca el ID del producto: " + "\n")
					.setChatId(handler.getMessage().getChatId()));
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
		
		}else {
			String s = handler.getMessage().getText();

			mySQLPool.query("select fabricante, telefono from producto where idProducto="+Integer.parseInt(s), res -> {
				if (res.succeeded()) {
					RowSet<Row> resultSet = res.result();
					System.out.println("El número de elementos obtenidos es " + resultSet.size());
					for (Row row : resultSet) {
						bot.sendMessage(new SendMessage()
								.setText(row.getString("fabricante")+" | "+row.getInteger("telefono"))
								.setChatId(handler.getMessage().getChatId()));
					}

				} else {
					bot.sendMessage(new SendMessage().setText(res.cause().getMessage())
							.setChatId(handler.getMessage().getChatId()));
				}
			});
			seccion.remove((Integer.parseInt(handler.getMessage().getChatId())));
			tabla.remove((Integer.parseInt(handler.getMessage().getChatId())));
			map.remove(Integer.parseInt(handler.getMessage().getChatId()));
		}
	}
	/* Insertar */
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
	/* Modificar */
	void modificarProducto(Update handler) {
		if ((handler.getMessage().getText().toLowerCase().contentEquals("a")
				&& (seccion.get(Integer.parseInt(handler.getMessage().getChatId())) == "/modificar"))) {
			tabla.put((Integer.parseInt(handler.getMessage().getChatId())), "a");
			mySQLPool.query("SELECT idProducto, nombreProducto FROM producto", res -> {
				if (res.succeeded()) {
					RowSet<Row> resultSet = res.result();
					System.out.println("El número de elementos obtenidos es " + resultSet.size());
					for (Row row : resultSet) {
						bot.sendMessage(new SendMessage().setText(row.getInteger("idProducto")
								+". "+ row.getString("nombreProducto"))
								.setChatId(handler.getMessage().getChatId()));
					}
				} else {
					bot.sendMessage(new SendMessage().setText(res.cause().getMessage())
							.setChatId(handler.getMessage().getChatId()));
				}
			});
			bot.sendMessage(new SendMessage().setText("¿Que producto desea modificar? Introduzca el ID")
					.setChatId(handler.getMessage().getChatId()));
		}else if(ruta.get(Integer.parseInt(handler.getMessage().getChatId())) == " ") {
			Integer id = Integer.parseInt(handler.getMessage().getText());
			ruta.replace(((Integer.parseInt(handler.getMessage().getChatId()))), String.valueOf(id));
			mySQLPool.query("select * from producto where idProducto=" + id, res -> {
				if (res.succeeded()) {
					RowSet<Row> resultSet = res.result();
					System.out.println("El número de elementos obtenidos es " + resultSet.size());
					for (Row row : resultSet) {
						bot.sendMessage(new SendMessage()
								.setText(row.getInteger("idProducto")+". "+row.getString("nombreProducto"))
								.setChatId(handler.getMessage().getChatId()));
					}

				} else {
					bot.sendMessage(new SendMessage().setText(res.cause().getMessage())
							.setChatId(handler.getMessage().getChatId()));
				}
			});
			bot.sendMessage(new SendMessage().setText("Introduzca los datos del producto"
					+ " (nombreProducto, codigoBarras, fabricante, telefono) separando por coma")
					.setChatId(handler.getMessage().getChatId()));
		}else if(ruta.get(Integer.parseInt(handler.getMessage().getChatId()))!=" "){
			List<String> userInput = new ArrayList<String>(
					Arrays.asList(handler.getMessage().getText().split(",")));
			if(userInput.size()!=3) {
			mySQLPool.query("update producto set nombreProducto=" +"'" +userInput.get(0)+"'" + 
					"," + "codigoBarras=" + Long.parseLong(userInput.get(1))+ "," + "fabricante="+"'" +userInput.get(2)+"'"+
					"," + "telefono=" + Integer.parseInt(userInput.get(3)) + " where idProducto=" + ruta.get(
							Integer.parseInt(handler.getMessage().getChatId())), res -> {
				if (res.succeeded()) {
					bot.sendMessage(new SendMessage()
							.setText("Se ha modificado correctamente!")
							.setChatId(handler.getMessage().getChatId()));
				} else {
					bot.sendMessage(new SendMessage().setText(res.cause().getMessage())
							.setChatId(handler.getMessage().getChatId()));
				}
			});
		}else {
			bot.sendMessage(new SendMessage().setText("Necesita introducir 3 datos")
					.setChatId(handler.getMessage().getChatId()));
		}
			seccion.remove((Integer.parseInt(handler.getMessage().getChatId())));
			tabla.remove((Integer.parseInt(handler.getMessage().getChatId())));
			map.remove(Integer.parseInt(handler.getMessage().getChatId()));
			ruta.remove(Integer.parseInt(handler.getMessage().getChatId()));
		}
	}
	void modificarIngrediente(Update handler) {
		if ((handler.getMessage().getText().toLowerCase().contentEquals("b")
				&& (seccion.get(Integer.parseInt(handler.getMessage().getChatId())) == "/modificar"))) {
			tabla.put((Integer.parseInt(handler.getMessage().getChatId())), "b");
			mySQLPool.query("SELECT idIngrediente, nombreIngrediente FROM ingrediente", res -> {
				if (res.succeeded()) {
					RowSet<Row> resultSet = res.result();
					System.out.println("El número de elementos obtenidos es " + resultSet.size());
					for (Row row : resultSet) {
						bot.sendMessage(new SendMessage().setText(row.getInteger("idIngrediente")
								+". "+ row.getString("nombreIngrediente"))
								.setChatId(handler.getMessage().getChatId()));
					}
				} else {
					bot.sendMessage(new SendMessage().setText(res.cause().getMessage())
							.setChatId(handler.getMessage().getChatId()));
				}
			});
			bot.sendMessage(new SendMessage().setText("¿Que ingrediente desea modificar? Introduzca el ID")
					.setChatId(handler.getMessage().getChatId()));
		}else if(ruta.get(Integer.parseInt(handler.getMessage().getChatId())) == " ") {
			Integer id = Integer.parseInt(handler.getMessage().getText());
			ruta.replace(((Integer.parseInt(handler.getMessage().getChatId()))), String.valueOf(id));
			mySQLPool.query("select * from ingrediente where idIngrediente=" + id, res -> {
				if (res.succeeded()) {
					RowSet<Row> resultSet = res.result();
					System.out.println("El número de elementos obtenidos es " + resultSet.size());
					for (Row row : resultSet) {
						bot.sendMessage(new SendMessage()
								.setText(row.getInteger("idingrediente")+". "+row.getString("nombreIngrediente"))
								.setChatId(handler.getMessage().getChatId()));
					}

				} else {
					bot.sendMessage(new SendMessage().setText(res.cause().getMessage())
							.setChatId(handler.getMessage().getChatId()));
				}
			});
			bot.sendMessage(new SendMessage().setText("Introduzca los datos del ingrediente"
					+ " (nombreIngrediente)")
					.setChatId(handler.getMessage().getChatId()));
		}else if(ruta.get(Integer.parseInt(handler.getMessage().getChatId()))!=" "){
			String input = handler.getMessage().getText();
			mySQLPool.query("update ingrediente set nombreIngrediente="+"'"+input+"'" + " where idIngrediente="+
			ruta.get(Integer.parseInt(handler.getMessage().getChatId())), res -> {
				if (res.succeeded()) {
					bot.sendMessage(new SendMessage()
							.setText("Se ha modificado correctamente!")
							.setChatId(handler.getMessage().getChatId()));
				} else {
					bot.sendMessage(new SendMessage().setText(res.cause().getMessage())
							.setChatId(handler.getMessage().getChatId()));
				}
			});

			seccion.remove((Integer.parseInt(handler.getMessage().getChatId())));
			tabla.remove((Integer.parseInt(handler.getMessage().getChatId())));
			map.remove(Integer.parseInt(handler.getMessage().getChatId()));
			ruta.remove(Integer.parseInt(handler.getMessage().getChatId()));
		}
	}
	void modificarIntolerancia(Update handler) {
		if ((handler.getMessage().getText().toLowerCase().contentEquals("c")
				&& (seccion.get(Integer.parseInt(handler.getMessage().getChatId())) == "/modificar"))) {
			tabla.put((Integer.parseInt(handler.getMessage().getChatId())), "c");
			mySQLPool.query("SELECT idintolerancia, nombreIntolerancia FROM intolerancia", res -> {
				if (res.succeeded()) {
					RowSet<Row> resultSet = res.result();
					System.out.println("El número de elementos obtenidos es " + resultSet.size());
					for (Row row : resultSet) {
						bot.sendMessage(new SendMessage().setText(row.getInteger("idintolerancia")
								+". "+ row.getString("nombreIntolerancia"))
								.setChatId(handler.getMessage().getChatId()));
					}
				} else {
					bot.sendMessage(new SendMessage().setText(res.cause().getMessage())
							.setChatId(handler.getMessage().getChatId()));
				}
			});
			bot.sendMessage(new SendMessage().setText("¿Que intolerancia desea modificar? Introduzca el ID")
					.setChatId(handler.getMessage().getChatId()));
		}else if(ruta.get(Integer.parseInt(handler.getMessage().getChatId())) == " ") {
			Integer id = Integer.parseInt(handler.getMessage().getText());
			ruta.replace(((Integer.parseInt(handler.getMessage().getChatId()))), String.valueOf(id));
			mySQLPool.query("select * from intolerancia where idintolerancia=" + id, res -> {
				if (res.succeeded()) {
					RowSet<Row> resultSet = res.result();
					System.out.println("El número de elementos obtenidos es " + resultSet.size());
					for (Row row : resultSet) {
						bot.sendMessage(new SendMessage()
								.setText(row.getInteger("idintolerancia")+". "+row.getString("nombreIntolerancia"))
								.setChatId(handler.getMessage().getChatId()));
					}

				} else {
					bot.sendMessage(new SendMessage().setText(res.cause().getMessage())
							.setChatId(handler.getMessage().getChatId()));
				}
			});
			bot.sendMessage(new SendMessage().setText("Introduzca los datos de la intolerancia"
					+ " (nombreIntolerancia)")
					.setChatId(handler.getMessage().getChatId()));
		}else if(ruta.get(Integer.parseInt(handler.getMessage().getChatId()))!=" "){
			String input = handler.getMessage().getText();
			mySQLPool.query("update intolerancia set nombreIntolerancia=" +"'" +input+"'" + 
					 " where idIntolerancia=" + ruta.get(
							Integer.parseInt(handler.getMessage().getChatId())), res -> {
				if (res.succeeded()) {
					bot.sendMessage(new SendMessage()
							.setText("Se ha modificado correctamente!")
							.setChatId(handler.getMessage().getChatId()));
				} else {
					bot.sendMessage(new SendMessage().setText(res.cause().getMessage())
							.setChatId(handler.getMessage().getChatId()));
				}
			});
		
			seccion.remove((Integer.parseInt(handler.getMessage().getChatId())));
			tabla.remove((Integer.parseInt(handler.getMessage().getChatId())));
			map.remove(Integer.parseInt(handler.getMessage().getChatId()));
			ruta.remove(Integer.parseInt(handler.getMessage().getChatId()));
		}
	}
	/* Eliminar */
	void eliminarProducto(Update handler) {
		if ((handler.getMessage().getText().toLowerCase().contentEquals("a")
				&& (seccion.get(Integer.parseInt(handler.getMessage().getChatId())) == "/eliminar"))) {
			tabla.put((Integer.parseInt(handler.getMessage().getChatId())), "a");
			mySQLPool.query("SELECT idProducto, nombreProducto FROM producto", res -> {
				if (res.succeeded()) {
					RowSet<Row> resultSet = res.result();
					System.out.println("El número de elementos obtenidos es " + resultSet.size());
					for (Row row : resultSet) {
						bot.sendMessage(new SendMessage().setText(row.getInteger("idProducto")
								+". "+ row.getString("nombreProducto"))
								.setChatId(handler.getMessage().getChatId()));
					}
				} else {
					bot.sendMessage(new SendMessage().setText(res.cause().getMessage())
							.setChatId(handler.getMessage().getChatId()));
				}
			});
			bot.sendMessage(new SendMessage().setText("¿Que producto desea eliminar? Introduzca el ID")
					.setChatId(handler.getMessage().getChatId()));
		}else if(ruta.get(Integer.parseInt(handler.getMessage().getChatId())) == " ") {
			Integer id = Integer.parseInt(handler.getMessage().getText());
			ruta.replace(((Integer.parseInt(handler.getMessage().getChatId()))), String.valueOf(id));
			mySQLPool.query("delete from producto where idProducto=" + id, res -> {
				if (res.succeeded()) {
					bot.sendMessage(new SendMessage()
							.setText("Se ha eliminado correctamente!")
							.setChatId(handler.getMessage().getChatId()));
				} else {
					bot.sendMessage(new SendMessage().setText(res.cause().getMessage())
							.setChatId(handler.getMessage().getChatId()));
				}
			});
			seccion.remove((Integer.parseInt(handler.getMessage().getChatId())));
			tabla.remove((Integer.parseInt(handler.getMessage().getChatId())));
			map.remove(Integer.parseInt(handler.getMessage().getChatId()));
			ruta.remove(Integer.parseInt(handler.getMessage().getChatId()));
		}
	}
		
}