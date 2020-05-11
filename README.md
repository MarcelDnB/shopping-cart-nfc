# Shopping Cart NFC

## 1. ¿Que es?

Consiste en una placa situada en un carro de compra con el cual vamos a escanear etiquetas NFC situadas cerca de los productos las cuales indican a estos. Esta placa tiene el objetivo de dar a conocer al usuario de si un producto es adecuado para el en cuanto a las intolerancias, alergias, etc.
La placa que vamos a usar es llamada ESP32, evidentemente tenemos tambien un receptor NFC, algunos LED's RGB para indicar cuanto de malo es un producto para un usuario especifico, por ejemplo en cuanto a las intolerancias. Tenemos tambien un LCD para que el usuario pueda elegir su perfil de intolerancias.

##### La idea parte de un supermercado

cuyas etiquetas de los productos son compatibles con la tecnología de NFC. El proyecto consiste en el desarrollo de un dispositivo compatible con esta tecnología de manera que, se le podrán dar varias funcionalidades a dicho dispositivo.

## Desarrollo de Aplicaciones Distribuidas | Dispositivo con NFC

```
Autores: Pérez López Sergio, Dan Gigi Marcel
```

```
DISPOSITIVO CON NFC
```
# Índice

#### 1. Idea (1º Iteración)

#### 1.1. Funcionalidades del dispositivo

#### 1.2. Ejemplo de aplicación

#### 2. Base de datos (2º Iteración)

#### 3. Esquema de API Rest (3º Iteración)

#### 4. Bot de Telegram (3º Iteración)

#### 5. MQTT ( 4 º Iteración)

#### 6. Conexión API Rest con ESP32 (5º Iteración)

#### 7. Anexo

#### 7.1. Diagrama UML

#### 7.2. Ejemplo código error 401

#### 7.3. Capturas de pantalla POSTMAN

#### 7.4. Capturas de pantalla Telegram(adminNFC)


```
DISPOSITIVO CON NFC
```
### 1. IDEA (1ª Iteración)

```
La idea parte de un supermercado compatible con la tecnología Near Field
Communication (NFC), que permite la comunicación inalámbrica de corto
alcance para el intercambio de datos entre dispositivos. El proyecto se basará
en el desarrollo de un dispositivo compatible con esta tecnología de manera
que, se le podrán dar varias funcionalidades a dicho dispositivo.
```
```
Esto será posible debido a que cada etiqueta colocada en la estantería donde
se encuentra un tipo de producto tendrá etiquetas especiales para transferir
información sobre el producto a través del dispositivo mencionado
anteriormente y que nos permitirá comprobar información nutricional y otros
datos del producto como, por ejemplo, cuanto de saludable es un producto
para determinadas personas. Esto quiere decir que el dispositivo se podrá
configurar para las intolerancias alimentarias de una determinada persona.
```
### 1.1. Funcionalidades del dispositivo

1. El dispositivo presentará un teclado de números, en el cual el usuario podrá
    marcar una **configuración** propia según sus **intolerancias**.
    Las características disponibles que seleccionar en el dispositivo son:
       ▪ **Característica 1** : Diabético
       ▪ **Característica 2** : Intolerante a la lactosa
       ▪ **Característica 3** : Celíaco
       ▪ **Característica 4** : Alergia al huevo
       ▪ **Característica 5** : Alergia al marisco y al pescado
       ▪ **Característica 6** : Alergia a frutos secos, legumbres y cereales
       ▪ **Característica 7** : Intolerancia a la fructosa
       ▪ **Característica 8** : Intolerante a la histamina
       ▪ Etc.
2. Otra funcionalidad beneficiosa para la empresa que implemente el sistema
    será que podrá obtener un **mapa de calor** con las **zonas más transitadas** del
    supermercado por los clientes. Aplicable después a estrategias marketing y
    publicitarias.


```
DISPOSITIVO CON NFC
```
### 1.2. Ejemplo de aplicación

```
Para entender mejor el funcionamiento del dispositivo, vamos a contar una
pequeña historia donde podremos entender la aplicación de este.
Pedro, un anciano de 76 años y con diabetes, acostumbra a ir al supermercado
de debajo de casa a comprar la comida que necesita para el día a día. El
problema que tiene nuestro protagonista es que no sabe leer, y en varias
ocasiones ha tenido más de un susto por haber comprado alimentos con
azúcar por equivocación. Además, Pedro, la mayoría de las veces está
condicionado a comprar el mismo tipo de alimentos sin azúcar porque al no
saber leer no sabe si el producto que desconoce puede o no tomarlo.
En el supermercado que hay debajo de la casa de Pedro se ha implantado un
sistema cuya principal característica es el NFC. Cuando Pedro entra de nuevo
en el supermercado como de costumbre, se para a hablar con Miguel, gerente
de mantenimiento.
```
```
Miguel le ha dado un dispositivo a Pedro del tamaño de un móvil de 7
pulgadas y le ha preguntado que, si tenía algún tipo de intolerancia
alimentaria, a lo que Pedro le contesta que sí, ya que es diabético desde
pequeño. Miguel al escuchar esto, le pide a nuestro protagonista que pulse el
primer botón y que cuando quiera saber si un producto es apto para él o no,
tan solo tiene que acercar el dispositivo a la etiqueta de la estantería donde se
encuentra el producto. Si la luz se pone de color verde significa que puede
tomarlo sin ningún problema. De lo contrario, si la luz se pone roja significará
que no debería de consumir ese producto porque contiene azúcar.
Pedro entra al supermercado y empieza a comprar con la ayuda del
dispositivo. Al pasar por caja para pagar, se da cuenta de que lleva mucha más
cantidad de productos de lo que está acostumbrado a comprar. La sorpresa
llega cuando el precio por toda la compra no excede mucho más de la cuenta
habitual.
El hecho de que la cuenta no se haya visto incrementada mucho ha sido
porque, Pedro acostumbraba a comprar productos de marcas muy conocidas y
desconocía la existencia de productos similares con precios más reducidos. El
dispositivo ha causado que Pedro pueda comprar con mucha más libertad y sin
miedo a comprar algo que no pueda tomar.
Por otro lado, Miguel ha ido haciendo lo mismo con los demás clientes, y poco
a poco los clientes se han ido habituando al hecho de utilizar dicho dispositivo.
Gracias a este dispositivo, el personal del supermercado ha sido capaz de ver
que zonas han sido las más visitadas mediante un mapa de calor. Este mapa de
calor se va a generar triangulando la posición de dicho dispositivo en todo
momento y generando un archivo que indique dichas coordenadas al final del
día, de modo que ahora van a ser capaces de anunciar nuevos productos en
aquellas zonas que se saben con certeza que van a ser más visitadas que otras
zonas y aplicar otras muchas estrategias de marketing útiles para la empresa.
```

```
DISPOSITIVO CON NFC
```
### 2. BASE DE DATOS (2ª Iteración)

```
Las tablas de la base de datos realizadas para este segundo entregable y
representadas en la Figura 1 del Anexo. dan solución para el almacenamiento de los
datos necesario para llevar a cabo el proyecto.
Vamos a dar una visión general de cómo funciona, esto lo vamos a hacer dando una
breve explicación de cada una de las tablas y de que datos se van a almacenar en
ellas.
```
- **comercio:** en esta tabla se almacenan los datos que hacen referencia al
    supermercado en el cual se va a implantar el sistema. Como atributos tenemos:
       o **nombreComercio:** almacena el nombre del supermercado. (Por
          ejemplo: Mercadona).
       o **CIF:** almacena el CIF de la empresa. (Por ejemplo: A46103834)
       o **teléfono:** almacena el número de teléfono del establecimiento: (Por
          ejemplo: 963883333 )
       o **idComercio:** clave primaria e identificativa de cada entrada de la tabla
          comercio.
- **redeswifi:** en esta tabla se almacena los datos que corresponden a las redes
    wifi que capta el dispositivo, necesarias para la triangulación de la ubicación.
       o **SSID:** almacena el nombre de la red wifi (Service Set Identifier). (Por
          ejemplo: WLAN_C990).
       o **PWR:** es el nivel de señal conforme nos acercamos con el dispositivo al
          punto de acceso de la red wifi. (Por ejemplo: - 30 ).
       o **captureTime:** almacena el valor en segundos del momento en el que se
          realiza la captura de la señal wifi. (Por ejemplo: 1584921600 ).
       o **idredesWifi:** clave primaria e identificativa de cada entrada de la tabla
          redeswifi.
       o **idComercio:** clave ajena referente a la columna que tiene el mismo
          nombre de la tabla comercio.
- **ubicación:** en esta tabla se almacena la información referente a la ubicación de
    un cliente en un momento determinado.
       o **horaUbicación:** almacena la hora correspondiente a la ubicación del
          usuario. (Por ejemplo: 1584921300 )
       o **margenError:** almacena el porcentaje de error que podría tener la
          ubicación almacenada con respecto a la realidad. (Por ejemplo: 0.02%)
       o **nombreZona:** almacena el nombre de la zona donde se sitúa la
          ubicación tomada. (Por ejemplo: pasillo central)
       o **idUbicacion:** clave primaria e identificativa de cada entrada de la tabla
          ubicación.
       o **idredesWifi:** clave ajena referente a la columna que tiene el mismo
          nombre de la tabla redeswifi.
       o **idUsuario:** clave ajena referente a la columna que tiene el mismo
          nombre de la tabla usuario.


```
DISPOSITIVO CON NFC
```
- **intolerancia:** en esta tabla se almacena el nombre de las diferentes
    intolerancias alimentarias o intolerancia a los alimentos que puede tener una
    persona. Es decir, hace referencia al nombre de las posibles reacciones
    adversas del organismo hacia alimentos que no son digeridos, metabolizados o
    asimilados completa o parcialmente.
       o **nombreIntolerancia:** este campo almacena el nombre de las diferentes
          intolerancias. (Por ejemplo: intolerancia a la lactosa)
       o **idIntolerancia:** clave primaria e identificativa de cada entrada de la
          tabla intolerancia.
- **ingrediente:** en esta tabla se almacena el nombre de los diferentes
    ingredientes por los que está formado un producto alimenticio y a los cuales
    una persona puede ser intolerante porque contenga algún aditivo que no
    tolere.
       o **nombreIngrediente:** almacena el nombre del ingrediente. (Por ejemplo:
          E102 - Tartrazina)
       o **idIngrediente:** clave primaria e identificativa de cada entrada de la tabla
          ingrediente.
       o **idIntolerancia:** clave ajena referente a la columna que tiene el mismo
          nombre de la tabla intolerancia.
- **producto:** en esta tabla se almacena la información referente a cada uno de los
    productos que hay en el supermercado.
       o **nombreProducto:** almacena el nombre del producto. (Por ejemplo:
          LecheAsturiana1L)
       o **codigoBarras:** almacena la cadena de caracteres que hacen referencia
          al código basado en la representación de un conjunto de líneas
          paralelas de distinto grosor y espaciado que en su conjunto contienen
          una determinada información. (Por ejemplo: 7501086801046 )
       o **fabricante:** almacena el nombre de la empresa que produce el
          producto. (Por ejemplo: Puleva)
       o **teléfono:** almacena el número de teléfono de atención al cliente del
          fabricante. (Por ejemplo: 957188753)
       o **idProducto:** clave primaria e identificativa de cada entrada de la tabla
          producto.
- **ingredientesProducto:** en esta tabla se almacena la información referente a los
    ingredientes que contiene un producto.
       o **idIngrediente:** clave ajena referente a la columna que tiene el mismo
          nombre de la tabla ingrediente.
       o **idProducto:** clave ajena referente a la columna que tiene el mismo
          nombre de la tabla producto.
       o **idIngredientesProductos:** clave primaria e identificativa de cada
          entrada de la tabla ingrediente.


```
DISPOSITIVO CON NFC
```
- **usuario:** en esta tabla se almacena el id referente a la tabla que gestiona al
    usuario.
       o **idUsuario:** clave primaria e identificativa de cada entrada de la tabla
          usuario.
       o **idComercio:** clave ajena referente a la columna que tiene el mismo
          nombre de la tabla comercio.
- **intoleraciasUsuario:** en esta tabla se almacenan las referencias a intolerancias
    que tiene un usuario. Es decir, en esta tabla es donde debemos consultar si
    queremos saber las intolerancias de un usuario.
       o **idIntolerancia:** clave ajena referente a la columna que tiene el mismo
          nombre de la tabla intolerancia.
       o **idUsuario:** clave ajena referente a la columna que tiene el mismo
          nombre de la tabla usuario.
       o **idIntoleranciasUsuario:** clave primaria e identificativa de cada entrada
          de la tabla intoleranciasUsuario.
- **productosUsuario:** en esta tabla se almacena la información referente a las
    veces que un producto ha sido escaneado por un cliente.
       o **vecesEscaneado:** almacena el número de veces que un producto se
          escanea. (Por ejemplo: 4)
       o **idContador:** clave primaria e identificativa de cada entrada de la tabla
          contador.
       o **idUsuario:** clave ajena referente a la columna que tiene el mismo
          nombre de la tabla usuario.
       o **idProducto:** clave ajena referente a la columna que tiene el mismo
          nombre de la tabla producto.
- **intoleranciasIngredientes:** en esta tabla se almacenan las referencias a
    intolerancias que tiene un ingrediente. Es decir, en esta tabla es donde
    debemos consultar si queremos saber las intolerancias de un ingrediente.
       o **idIntoleranciasIngrediente:** clave primaria e identificativa de cada
          entrada de la tabla intoleranciasIngredientes.
       o **idIntolerancia:** clave ajena referente a la columna que tiene el mismo
          nombre de la tabla intolerancia.
       o **idIngrediente:** clave ajena referente a la columna que tiene el mismo
          nombre de la tabla ingrediente.


```
DISPOSITIVO CON NFC
```
### 3. Esquema API Rest (3ª Iteración)

En este apartado se va a explicar cada una de las funciones de la API Rest, así como los
métodos que sirven de comunicación entre el cliente y el servidor. A continuación, se
va a detallar cada URL con sus correspondientes parámetros y definición de los
métodos asociados.

**1. GET [http://localhost:8081/api/scan/:idProducto](http://localhost:8081/api/scan/:idProducto)** → **(getIntolerances)**
    **Parámetros:** Para realizar esta petición es necesario como podemos ver en
    la URL pasarle el parámetro “ **:idProducto** ”. Con este parámetro
    obtendremos las intolerancias asociadas al producto correspondiente.
    **Query:** "SELECT idIntolerancia FROM intolerancia NATURAL JOIN
    ingrediente NATURAL JOIN ingredientesproducto NATURAL JOIN producto
    WHERE IdProducto = idProducto”
    **Respuesta:** la respuesta que recibimos del servidor en caso de haberse
    realizado correctamente la petición es un JSON que contiene todas las
    intolerancias que están asociadas al producto pasado por parámetro.
       **Código:** 200 OK (Figura 3)
       **Contenido:** JSON que contiene todas las intolerancias que están
       asociadas al producto pasado por parámetro.

```
Código: 401 (Unauthorized)
Contenido: Anexo de código de error 401. (Figura 2)
```
**2. PUT [http://localhost:8081/api/scan/put/wifi/values](http://localhost:8081/api/scan/put/wifi/values)** → **(putWifiScan)**
    **Cuerpo:** Para realizar esta petición debemos pasarle un JSON que
    contiene los datos que definen una red wifi para insertarla en la tabla
    redeswifi de la base de datos.
    El JSON que debe enviarse tiene la siguiente forma:

```
[ {
"idIntolerancia" : 1
}, {
"idIntolerancia" : 2
} ]
```

```
DISPOSITIVO CON NFC
```
```
Query : "INSERT INTO redeswifi (SSID, PWR, captureTime, idComercio)
VALUES (?,?,?,?)".
Respuesta:
Código: 200 OK. (Figura 4)
Contenido: JSON que contiene la información almacenada.
Código: 401 (Unauthorized).
Contenido: Anexo de código de error 401. (Figura 2)
```
**3. PUT [http://localhost:8081/api/scan/put/produs/values](http://localhost:8081/api/scan/put/produs/values)** → **(putAfterScan)**

```
Cuerpo: Para realizar esta petición debemos pasarle un JSON que
contiene los datos referentes al producto que el usuario acaba de
escanear con el dispositivo para insertarlos en la tabla productosUsuario
de la base de datos.
El JSON que debe enviarse tiene la siguiente forma:
```
```
Query : " INSERT INTO productosUsuario (idProducto, idUsuario) VALUES
(?,?)".
Respuesta:
Código: 200 OK. (Figura 5)
Contenido: JSON que contiene la información almacenada.
```
###### {

```
"ssid" : "WLAN_32",
"power" : - 43,
"timestamp" : 1231231231,
"idComercio" : 1
}
```
###### {

```
"idProducto": 2,
"idUsuario": 3
}
```

```
DISPOSITIVO CON NFC
```
```
Código: 401 (Unauthorized).
Contenido: Anexo de código de error 401. (Figura 2)
```
**4. PUT [http://localhost:8081/api/scan/put/usuario/values](http://localhost:8081/api/scan/put/usuario/values)** → **(putUsuario)**

```
Cuerpo: Para realizar esta petición debemos pasarle un JSON que
contiene los datos referentes al usuario que se va a introducir en la tabla
Usuario de la base de datos. En este caso el usuario solo necesita ser
asociado a un comercio para crearse, además del id que la BD le asigne
automáticamente.
El JSON que debe enviarse tiene la siguiente forma:
```
```
Query : " INSERT INTO usuario (idComercio) VALUES (?)".
Respuesta:
Código: 200 OK. (Figura 6)
Contenido: JSON que contiene la información almacenada.
Código: 401 (Unauthorized).
Contenido: Anexo de código de error 401. (Figura 2)
```
**5. PUT [http://localhost:8081/api/scan/put/usuint/values](http://localhost:8081/api/scan/put/usuint/values)** →
**(putIntoleranciasUsuario)**

```
Cuerpo: Para realizar esta petición debemos pasarle un JSON que
contiene los datos referentes a las intolerancias de un usuario. Estas
intolerancias son las que el usuario introduce con el dispositivo una vez
entra en el supermercado.
El JSON que debe enviarse tiene la siguiente forma:
```
###### {

```
"idComercio" : 1"
}
```

```
DISPOSITIVO CON NFC
```
```
Query : " INSERT INTO intoleranciasusuario (idIntolerancia, idUsuario)
VALUES (?,?) "
Respuesta:
Código: 200 OK. (Figura 7)
Contenido: JSON que contiene la información almacenada.
Código: 401 (Unauthorized).
Contenido: Anexo de código de error 401. (Figura 2)
```
### 4. Bot de Telegram (3ª Iteración)

```
En este apartado de la documentación vamos a hablar y explicar todo el
funcionamiento de la parte que corresponde al Bot de Telegram ( adminNFC ).
El Bot ha sido creado con la finalidad de facilitar el trabajo al administrador del
sistema. La pregunta es, ¿Cómo un Bot de Telegram puede facilitar el trabajo de un
administrador?, la respuesta es sencilla: automatizando el proceso de inserción de
datos en la base de datos. Nuestro Bot “adminNFC” ha sido creado para poder
insertar todos los datos que son transparentes al usuario final. Entiéndase
transparente como aquellos datos que son necesarios en la base de datos para que
sistema funcione.
Por ejemplo, cuando un usuario entre al supermercado y escanea un producto, las
intolerancias asociadas a dicho producto, así como la información del mismo deben
de estar almacenadas en la base de datos. Es decir, se han tenido que introducir
previamente para poder ser reconocidas. Pues este tipo de información es la que el
administrador a través del Bot puede introducir en la Base de datos.
Para entender bien el funcionamiento del código correspondiente vamos a explicar
a continuación una serie de historias de usuario que nos ayudarán a entender cómo
funciona el Bot.
Hay que tener en cuenta que la principal lógica del sistema es que se van a
introducir datos uno a uno en una tabla, lo que significa que hay una secuencia de
comando que hay que seguir para introducir lo datos y por lo tanto una serie de
```
###### {

```
"idIntolerancia": 1,
"idUsuario": 2
}
```

```
DISPOSITIVO CON NFC
```
comandos anidados. Es decir, como bien se ha dicho la principal lógica es una
anidación de comandos (i **f y else anidados** ) así como el almacenamiento de
variables temporales ( **map** ).

A continuación, se va a explicar cada una de las tablas en las que se puede insertar
( **/insertar** ) datos desde el Bot así como la secuencia a seguir para introducir los
datos. Esto se va a realizar con unas historias de usuario a modo de ejemplo.

**TABLA INGREDIENTE:**

**Historia de usuario: (Figura 8)**

**Función asociada** → **“** void tablaIngrediente(Update handler)”

**Query asociada:**

```
o “ INSERT INTO ingrediente (nombreIngrediente) VALUES (?)”
```
**TABLA INTOLERANCIASINGREDIENTE:**

**Historia de usuario: (Figura 9)**

```
Función asociada → “ void intoleranciasIngrediente(Update handler)”
```
```
admin: /insertar
bot: ¿En qué tabla quiere usted insertar?
admin: ingrediente
bot: ¿Qué dato quiere introducir en nombreIngrediente?
admin: Sacarosa
bot: Se ha añadido la entrada correctamente
```
```
admin: /insertar
bot: ¿En qué tabla quiere usted insertar?
admin: intoleranciasIngrediente
bot: ¿Qué dato quiere introducir en idIngrediente?
bot: se muestra un listado con los ingredientes y sus id que hay en la base de
datos
admin: 2
bot: ¿Qué dato quiere introducir en idIntolerancia?
bot: se muestra un listado con las intolerancias y sus id que hay en la base de
datos
admin: 1
bot: Se ha añadido la entrada correctamente
```

```
DISPOSITIVO CON NFC
```
```
Query asociada:
o “SELECT idIngrediente, nombreIngrediente FROM ingrediente".
o “SELECT idIngrediente, nombreIngrediente FROM ingrediente”.
o “ INSERT INTO ingrediente (nombreIngrediente) VALUES (?)”.
```
**TABLA INGREDIENTESPRODUCTO:**

**Historia de usuario: (Figura 10)**

```
Función asociada → “ void ingredientesProducto(Update handler)”
```
**Query asociada:**

```
o “SELECT idIngrediente, nombreIngrediente FROM ingrediente".
o “SELECT idProducto, nombreProducto FROM producto”.
o “ INSERT INTO ingredientesproducto (idIngrediente, IdProducto) VALUES
(?,?)”.
```
**TABLA INTOLERANCIA:**

**Historia de usuario: (Figura 11)**

```
Función asociada → “ void tablaIntolerancia(Update handler)”
```
**Query asociada:**

```
o “ INSERT INTO intolerancia (nombreIntolerancia) VALUES (?)”.
```
```
admin: /INSERTAR
bot: ¿En qué tabla quiere usted insertar?
admin: ingredientesproducto
bot: ¿Qué dato quiere introducir en idIngrediente?
bot: se muestra un listado con los ingredientes y sus id que hay en la base
de datos
admin: 1
bot: ¿Qué dato quiere introducir en idProducto?
bot: se muestra un listado con los productos y sus id que hay en la base de
datos
admin: 3
bot: Se ha añadido la entrada correctamente
```
```
admin: /insertar
bot: ¿En qué tabla quiere usted insertar?
admin: intolerancia
bot: ¿Qué dato quiere introducir en nombreIntolerancia?
admin: Intolerancia a la lactosa
bot: Se ha añadido la entrada correctamente
```

```
DISPOSITIVO CON NFC
```
###### TABLA PRODUCTO:

**Historia de usuario: (Figura 12)**

```
Función asociada → “ void tablaProducto(Update handler)”
```
**Query asociada:**

```
o “ INSERT INTO producto (nombreProducto, codigoBarras, fabricante,
telefono) VALUES (?,?,?,?)”.
```
**TABLA COMERCIO:**

**Historia de usuario: (Figura 13)**

```
Función asociada → “ void tablaComercio(Update handler)”
```
**Query asociada:**

```
o “INSERT INTO comercio (nombreComercio, telefono, CIF) VALUES
(?,?,?)”.
```
```
admin: /insertar
bot: ¿En qué tabla quiere usted insertar?
admin: producto
bot: ¿Qué dato quiere introducir en nombreProducto?
admin: Natilla con galletas
bot: ¿Qué dato quiere introducir en codigoBarras?
admin: 123456789123
bot: ¿Qué dato quiere introducir en fabricante?
admin: Hacendado
bot: ¿Qué dato quiere introducir en teléfono?
admin: 666666666
bot: Se ha añadido la entrada correctamente
```
```
admin: /insertar
bot: ¿En qué tabla quiere usted insertar?
admin: comercio
bot: ¿Qué dato quiere introducir en nombreComercio?
admin: Mercadona
bot: ¿Qué dato quiere introducir en teléfono?
admin: 957222222
bot: ¿Qué dato quiere introducir en CIF?
admin: D
bot: Se ha añadido la entrada correctamente
```

```
DISPOSITIVO CON NFC
```
Otra de las funciones que implementa este Bot de Telegram es, poder visualizar
información relevante de la base de datos. Por ejemplo, poder consultar los productos
que han escaneado los usuarios con ciertas intolerancias es algo interesante en cuanto
a estrategias de marketing para el supermercado se refiere. Este tipo de información
se le podrá pedir al Bot (adminNFC) comenzado una conversación con el comando
**“/info”**. Además del ejemplo comentado se le podrá pedir otro tipo de información
que veremos a continuación con unas historias de usuario para entender mejor el
funcionamiento.

###### INFORMACION DE PRODUCTOS ESCANEADOS FILTRADOS POR INTOLERANCIAS:

```
Historia de usuario: (Figura 1 4 )
```
```
Función asociada → “ void infoProductosEscaneados(Update handler)”
Query asociada:
o “ SELECT idProducto,nombreProducto, COUNT(idProducto) FROM
productosusuario NATURAL JOIN producto WHERE
productosusuario.idProducto in (SELECT idProducto from
productosusuario NATURAL JOIN producto \r\n" + " NATURAL JOIN
ingredientesproducto NATURAL JOIN ingrediente NATURAL JOIN
intoleranciasingrediente NATURAL JOIN intolerancia WHERE
idIntolerancia=" + Integer.parseInt(s) +") GROUP BY IdProducto”.
```
```
admin: /info
bot: A continuación, se indica la información disponible:
bot: 1. Productos escaneados por usuarios con ciertas
intolerancias
bot: Selecciona la petición deseada, introduciendo su
índice:
admin: 1
bot: ¿De qué intolerancias desea ver los productos
escaneados por los usuarios?
bot: Introduzca alguno de los ID's mostrados a contiuación.
admin: 1 2 5
bot: A continuación, se mostrarán la lista de productos
escaneados por usuarios con intolerancias iguales a las
seleccionadas antes.
```

```
DISPOSITIVO CON NFC
```
###### COMPROBAR SI UN PRODUCTO ESTÁ EN LA BASE DE DATOS:

**Historia de usuario: (Figura 1 5 )**

```
Función asociada → “ void comprobarExistenciaProducto(Update handler)”
Query asociada:
o “SELECT nombreProducto FROM producto WHERE nombreProducto
LIKE '%nombre_producto%'”.
```
**VER INFORMACIÓN PERTENECIENTE A UN PRODUCTO:**

**Historia de usuario: (Figura 1 6 )**

```
Función asociada → “ void informacionProducto(Update handler)”
Query asociada:
o “SELECT fabricante, telefono FROM producto WHERE idProducto=
'nombre_producto’”.
```
```
admin: /info
bot: A continuación, se indica la información disponible:
bot: 2. Comprobar si un producto está ya en la base de
datos.
admin: 2
bot: Indique el nombre del producto a comprobar
admin: Natilla con galletas
bot: El producto ya está en la base de datos y posee el
siguiente id.
bot: El producto no está en la base de datos
```
```
admin: /info
bot: A continuación, se indica la información disponible:
bot: 3. Visualizar información referente a un producto.
admin: 2
bot: Indique el nombre del producto del cual desea
visualizar la información
admin: Natilla con galletas
bot: fabricante: "Hacendado"
bot:teléfono: "666666666"
bot: código de barras: 123456789123
```

```
DISPOSITIVO CON NFC
```
Por otro lado, ya que desde el dispositivo no tendría sentido poder modificar datos
como por ejemplo un producto del supermercado, o un ingrediente o algún tipo de
intolerancia, hemos pensado que sería buena idea facilitar el trabajo al administrador
del sistema si este tipo de modificaciones se hacen a través del Bot de Telegram
(adminNFC). A continuación, se muestran las historias de usuario que simulan la
conversación del administrador cuando se usa el comando **“/modificar”.**

###### MODIFICAR INFORMACIÓN PERTENECIENTE A UN PRODUCTO:

```
Historia de usuario: (Figura 17)
```
```
Función asociada → “ void modificarProducto(Update handler)”
Query asociada:
o “SELECT idProducto, nombreProducto FROM producto”.
o “SELECT * FROM producto WHERE idProducto= valor1”
o “UPDATE producto SET nombreProducto=valor1, codigoBarras=valor2,
fabricante=valor3, telefono=valor4 WHERE idProducto=valor5”
```
```
admin: /modificar
bot: ¿Qué tabla desea modificar?
bot: a. Producto
admin: a
bot: Seleccione el id del producto a modificar (Se mostrarán
lista de productos)
admin: 3
bot: Producto 3 contiene los siguientes datos (se mostrarán
los datos de ese producto)
bot: Introduzca los datos del producto modificado
separándolos por comas
admin: Leche hacendado,123456789123,El
pozo,
bot: La entrada se ha modificado correctamente
```

```
DISPOSITIVO CON NFC
```
###### MODIFICAR INFORMACIÓN PERTENECIENTE A UN INGREDIENTE:

**Historia de usuario: (Figura 18)**

```
Función asociada → “ void modificarIngrediente(Update handler)”
Query asociada:
o “SELECT idIngrediente, nombreIngrediente FROM Ingrediente”.
o “UPDATE ingrediente SET nombreIngrediente= valor1 WHERE
IdIngrediente=valor2”
```
###### MODIFICAR INFORMACIÓN PERTENECIENTE A UNA INTOLERANCIA:

**Historia de usuario: (Figura 1 9 )**

```
admin: /modificar
bot: ¿Qué tabla desea modificar?
bot: b. Ingrediente
admin: b
bot: Seleccione el id del ingrediente a modificar (Se
mostrarán lista de ingredientes)
admin: 2
bot: Ingrediente 2 contiene los siguientes datos (se
mostrarán los datos de ese ingrediente)
bot: Introduzca los datos del ingrediente modificado
separándolos por comas
admin: calcio
bot: la entrada se ha modificado correctamente
```
```
admin: /modificar
bot: ¿Qué tabla desea modificar?
bot: c. intolerancia
admin: c
bot: Seleccione el id de la intolerancia a modificar (Se
mostrarán lista de intolerancias)
admin: 4
bot: Intolerancia 4 contiene los siguientes datos (se
mostrarán los datos de esa intolerancia)
bot: Introduzca los datos de la intolerancia modificada
separándolos por comas
admin: Intolerancia a la sacarosa
bot: la entrada se ha modificado correctamente
```

```
DISPOSITIVO CON NFC
```
```
Función asociada → “ void modificarIntolerancia(Update handler)”
Query asociada:
o “SELECT idIntolerancia, nombreIntolerancia FROM Intolerancia”.
o “UPDATE intolerancia SET nombreIntolerancia= valor1 WHERE
IdIntolerancia=valor2”
```
Por último, el Bot de Telegram puede mediante el comando **“/eliminar”** eliminar una
entrada referente a alguna tabla de la Base de Datos. Consideramos que los únicos
datos que podría ser útil eliminar son los que hacen referencia a los productos de los
supermercados. Esto es porque es posible que un supermercado deje de comercializar
cierto producto y sea necesario eliminarlo de la Base de Datos. De nuevo, para facilitar
el trabajo al administrador del sistema esta función se puede realizar desde el Bot de
Telegram(adminNFC). A continuación, se muestra la historia de usuario y la
información que hace referencia a esta funcionalidad.

###### ELIMINAR UN PRODUCTO:

```
Historia de usuario: (Figura 20 )
```
```
Función asociada → “ void eliminarProducto(Update handler)”
Query asociada:
o “SELECT idProducto, nombreProducto FROM producto”.
o “DELETE FROM Producto WHERE idProducto=valor1”
```
```
admin: /eliminar
bot: ¿De qué tabla desea eliminar?
bot: a. producto
admin: a
bot: Seleccione el id del producto a eliminar (Se mostrarán
lista de productos)
admin: 4
bot: la entrada se ha eliminado correctamente
```

```
DISPOSITIVO CON NFC
```
### 5. MQTT

```
Este proyecto contiene una serie de funcionalidades que la mayoría han sido
implementadas mediante la API Rest definida en los apartados anteriores. Sin
embargo, hay alguna funcionalidad que no se ha implementado y es el envío por
parte del cliente al servidor de los datos referentes a las redes Wifi.
CANAL PARA LAS REDES WIFI
Nombre del canal: TOPIC_WIFI
Propósito del canal: Este canal ha sido creado para que los clientes puedan
mandar a través de él, al bróker, los datos que identifican a las redes Wifi. Estos
datos se envían para que posteriormente se haga un procesamiento de estos, y
mediante una triangulación de los distintos accesos wifi se puede dar una
ubicación mas o menos exacta de la parte del supermercado en la que se
encuentra el cliente.
Contenido del mensaje: El contenido del mensaje que el cliente enviará al bróker
es un JSON como el que aparece a continuación :
```
```
{
"ssid": "WLAN_32",
"power": -43,
"timestamp": 1231231231,
"idComercio": 1
}
```

```
DISPOSITIVO CON NFC
```
### 6. Conexión API Rest con ESP32

```
En esta parte vamos a detallar las funciones que hacen posible que nuestro
dispositivo ESP32 pueda realizar peticiones REST. Es decir, mediante las funciones
que se detallan a continuación nuestro dispositivo es capaz de conectarse con la
API Rest y realizar las peticiones que hacen posible la comunicación entre el
servidor que en este caso está en local y nuestro dispositivo.
A continuación, como bien se ha dicho se van a detallar las funciones que hacen
posible esa comunicación, se detallará el nombre de ellas, el funcionamiento y con
qué función de la API Rest hacen referencia.
```
```
Esta parte del proyecto se ha realizado en otro entorno de desarrollo, en concreto
en Atom. Dentro de este entorno se ha instalado un plugin llamado PlatformIO.
Las funciones que se detallan a continuación pueden contener a otras para la
simplificación del código, para su compresión se detallará la jerarquía de estas.
```
```
FUNCIONES
```
```
1 .- → sendPutNuevoUsuario().
```
```
Definición: esta función es la función que se llama cuando un usuario
coge el dipositivo. Es la encargada de que se cree un nuevo usuario en la
base de datos, posteriormente es la que se encarga de mostrarle al
usuario todas las intolerancias para que él seleccione las que el padece,
y posteriormente guarde las seleccionadas en la base de datos asociadas
a ese usuario recién creado. Todo esto se realiza a través de diferentes
funciones que se detallan a continuación.
```
```
1.1.- → sendPutUsuario().
```
```
Definición: Esta función es la encargada de introducir un nuevo
usuario en la base de datos.
Función API Rest asociada: putUsuario().
Petición http asociada: “/api/scan/put/usuario/values”
```
**1. 2 .-** → **sendGetAllIntolerances().**

```
Definición: Esta función es la encargada de rescatar todas las
intolerancias disponibles para que el usuario elija las que padece.
Función API Rest asociada: getIntolerancesAll ().
Petición http asociada: “/api/scan/get/intolerances”
```

```
DISPOSITIVO CON NFC
```
**1. 3 .-** → **sendPutIntoleranciasUsuario().**

**Definición:** Esta función es la encargada de enviar a la base de
datos las intolerancias que el usuario padece, después de
haberlas seleccionado.
**Función API Rest asociada:** putIntoleranciasUsuario().
**Petición http asociada:** “/api/scan/put/usuint/values”.

**2 .-** → **sendGetIntolerancias(int ProductId)**.

```
Definición: esta función es la que se encarga de pedir los datos de cierto
producto cuando este es escaneado. Cuando un producto es escaneado
se deben comparar las intolerancias del usuario con las del producto,
pues esta función se encarga de solicitar las intolerancias de un
producto dado su id.
Función API Rest asociada: getIntolerances().
```
**Petición http asociada:** “/api/scan/:idProducto”.

**3 .-** → **sendPutAfterScan(int ProductId)**.

```
Definición: esta función es la encargada de una vez el usuario ha
escaneado un cierto producto insertar en la base de datos el producto
escaneado con el usuario asociado a ese escaneo.
Función API Rest asociada: getIntolerances().
```
**Petición http asociada:** “/api/scan/:idProducto”.

**4 .-** → **sendPutWifiRead()**.

```
Definición: esta función se encarga enviar las informaciones
correspondientes a las redes wifi captadas para poder realizar la
triangulación de la ubicación.
Función API Rest asociada: putWifiScan().
```
**Petición http asociada:** “/api/scan/put/wifi/values”.


```
DISPOSITIVO CON NFC
```
### 7. Anexo

### 7.1. Diagrama UML

**_Figura 1_**


```
DISPOSITIVO CON NFC
```
### 5.2 Ejemplo código error 401

```
{
"cause": null,
"stackTrace": [
{
"methodName": "handleErrorPacketPayload",
"fileName": "CommandCodec.java",
"lineNumber": 130,
"className": "io.vertx.mysqlclient.impl.codec.CommandCodec",
"nativeMethod": false
},
{
"methodName": "handleInitPacket",
"fileName": "ExtendedQueryCommandBaseCodec.java",
"lineNumber": 27,
"className": "io.vertx.mysqlclient.impl.codec.ExtendedQueryCommandBaseCodec",
"nativeMethod": false
},
{
"methodName": "decodePayload",
"fileName": "QueryCommandBaseCodec.java",
"lineNumber": 55,
"className": "io.vertx.mysqlclient.impl.codec.QueryCommandBaseCodec",
"nativeMethod": false
},
},
],
"errorCode": 1452,
"sqlState": "23000",
"message": "Cannot add or update a child row: a foreign key constraint fails (`dad`.`usuario`, CONSTRAINT `idComercio7`
FOREIGN KEY (`idComercio`) REFERENCES `comercio` (`idComercio`) ON DELETE CASCADE ON UPDATE CASCADE)",
"localizedMessage": "Cannot add or update a child row: a foreign key constraint fails (`dad`.`usuario`, CONSTRAINT
`idComercio7` FOREIGN KEY (`idComercio`) REFERENCES `comercio` (`idComercio`) ON DELETE CASCADE ON UPDATE CASCADE)",
"suppressed": []
```
**_Figura_** } **_2_**


```
DISPOSITIVO CON NFC
```
### 5.3 Capturas de pantalla POSTMAN

**1. GET [http://localhost:8081/api/scan/:idProducto](http://localhost:8081/api/scan/:idProducto)
2. PUT [http://localhost:8081/api/scan/put/wifi/values](http://localhost:8081/api/scan/put/wifi/values)**

```
Figura 3
```
**_Figura 4_**


```
DISPOSITIVO CON NFC
```
**3. PUT [http://localhost:8081/api/scan/put/produs/values](http://localhost:8081/api/scan/put/produs/values)**

```
4. PUT http://localhost:8081/api/scan/put/usuario/values
```
**_Figura 5_**

**_Figura 6_**


```
DISPOSITIVO CON NFC
```
**5. PUT [http://localhost:8081/api/scan/put/usuint/values](http://localhost:8081/api/scan/put/usuint/values)**

**_Figura 7_**


```
DISPOSITIVO CON NFC
```
### 5.4 Capturas de pantalla Telegram (adminNFC)

###### - INSERTAR EN TABLA INGREDIENTE

###### - INSERTAR EN TABLA INTOLERANCIASINGREDIENTE

```
Figura 8
```
```
Figura 9
```

```
DISPOSITIVO CON NFC
```
###### - INSERTAR EN TABLA INGREDIENTESPRODUCTO

###### - INSERTAR EN TABLA INTOLERANCIA

```
Figura 10
```
```
Figura 11
```

```
DISPOSITIVO CON NFC
```
###### - INSERTAR EN TABLA PRODUCTO

###### - INSERTAR EN TABLA PRODUCTO

```
Figura 12
```
```
Figura 13
```

```
DISPOSITIVO CON NFC
```
###### - INFORMACION DE PRODUCTOS ESCANEADOS FILTRADOS POR

###### INTOLERANCIAS.

###### - COMPROBAR SI UN PRODUCTO ESTÁ EN LA BASE DE DATOS.

```
Figura 14
```
```
Figura 15
```

```
DISPOSITIVO CON NFC
```
###### - VER INFORMACIÓN PERTENECIENTE A UN PRODUCTO.

###### - MODIFICAR INFORMACIÓN PERTENECIENTE A UN PRODUCTO.

```
Figura 16
```
```
Figura 17
```

```
DISPOSITIVO CON NFC
```
###### - MODIFICAR INFORMACIÓN PERTENECIENTE A UN INGREDIENTE.

###### - MODIFICAR INFORMACIÓN PERTENECIENTE A UNA INTOLERANCIA.

```
Figura 18
```
```
Figura 19
```

```
DISPOSITIVO CON NFC
```
###### - ELIMINAR UN PRODUCTO.

```
Figura 20
```


