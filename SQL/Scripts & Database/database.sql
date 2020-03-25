-- MySQL dump 10.13  Distrib 8.0.19, for Win64 (x86_64)
--
-- Host: 127.0.0.1    Database: dad
-- ------------------------------------------------------
-- Server version	8.0.19

/*!40101 SET @OLD_CHARACTER_SET_CLIENT=@@CHARACTER_SET_CLIENT */;
/*!40101 SET @OLD_CHARACTER_SET_RESULTS=@@CHARACTER_SET_RESULTS */;
/*!40101 SET @OLD_COLLATION_CONNECTION=@@COLLATION_CONNECTION */;
/*!50503 SET NAMES utf8 */;
/*!40103 SET @OLD_TIME_ZONE=@@TIME_ZONE */;
/*!40103 SET TIME_ZONE='+00:00' */;
/*!40014 SET @OLD_UNIQUE_CHECKS=@@UNIQUE_CHECKS, UNIQUE_CHECKS=0 */;
/*!40014 SET @OLD_FOREIGN_KEY_CHECKS=@@FOREIGN_KEY_CHECKS, FOREIGN_KEY_CHECKS=0 */;
/*!40101 SET @OLD_SQL_MODE=@@SQL_MODE, SQL_MODE='NO_AUTO_VALUE_ON_ZERO' */;
/*!40111 SET @OLD_SQL_NOTES=@@SQL_NOTES, SQL_NOTES=0 */;

--
-- Table structure for table `comercio`
--

DROP TABLE IF EXISTS `comercio`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `comercio` (
  `idComercio` int NOT NULL AUTO_INCREMENT,
  `nombreComercio` varchar(45) DEFAULT NULL,
  `telefono` bigint DEFAULT NULL,
  `CIF` varchar(9) DEFAULT NULL,
  PRIMARY KEY (`idComercio`),
  UNIQUE KEY `idComercio_UNIQUE` (`idComercio`),
  UNIQUE KEY `CIF_UNIQUE` (`CIF`)
) ENGINE=InnoDB AUTO_INCREMENT=8 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `comercio`
--

LOCK TABLES `comercio` WRITE;
/*!40000 ALTER TABLE `comercio` DISABLE KEYS */;
INSERT INTO `comercio` VALUES (1,'Mercadona',123456789,'123456789'),(5,'Dia',123456781,'12345678A'),(6,'elJamón',123456783,'12345678C'),(7,'Más',123456782,'12345678B');
/*!40000 ALTER TABLE `comercio` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `ingrediente`
--

DROP TABLE IF EXISTS `ingrediente`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `ingrediente` (
  `idingrediente` int NOT NULL AUTO_INCREMENT,
  `nombreIngrediente` varchar(45) DEFAULT NULL,
  `idIntolerancia` int DEFAULT NULL,
  PRIMARY KEY (`idingrediente`),
  UNIQUE KEY `idingredientes_UNIQUE` (`idingrediente`),
  KEY `idIntolerancia_idx` (`idIntolerancia`),
  CONSTRAINT `idIntolerancia` FOREIGN KEY (`idIntolerancia`) REFERENCES `intolerancia` (`idintolerancia`) ON DELETE CASCADE ON UPDATE CASCADE
) ENGINE=InnoDB AUTO_INCREMENT=5 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `ingrediente`
--

LOCK TABLES `ingrediente` WRITE;
/*!40000 ALTER TABLE `ingrediente` DISABLE KEYS */;
INSERT INTO `ingrediente` VALUES (1,'Huevo',4),(2,'Alcohol',2),(3,'Nata',1),(4,'Galletas',3);
/*!40000 ALTER TABLE `ingrediente` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `ingredientesproducto`
--

DROP TABLE IF EXISTS `ingredientesproducto`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `ingredientesproducto` (
  `idIngrediente` int DEFAULT NULL,
  `IdIngredienteProducto` int NOT NULL AUTO_INCREMENT,
  `IdProducto` int DEFAULT NULL,
  PRIMARY KEY (`IdIngredienteProducto`),
  UNIQUE KEY `IdIngredienteProducto_UNIQUE` (`IdIngredienteProducto`),
  KEY `IdIngrediente_idx` (`idIngrediente`),
  KEY `IdProducto3_idx` (`IdProducto`),
  CONSTRAINT `IdIngrediente3` FOREIGN KEY (`idIngrediente`) REFERENCES `ingrediente` (`idingrediente`) ON DELETE CASCADE ON UPDATE CASCADE,
  CONSTRAINT `IdProducto3` FOREIGN KEY (`IdProducto`) REFERENCES `producto` (`idProducto`) ON DELETE CASCADE ON UPDATE CASCADE
) ENGINE=InnoDB AUTO_INCREMENT=5 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `ingredientesproducto`
--

LOCK TABLES `ingredientesproducto` WRITE;
/*!40000 ALTER TABLE `ingredientesproducto` DISABLE KEYS */;
INSERT INTO `ingredientesproducto` VALUES (1,1,1),(3,2,1),(4,3,1),(2,4,2);
/*!40000 ALTER TABLE `ingredientesproducto` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `intolerancia`
--

DROP TABLE IF EXISTS `intolerancia`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `intolerancia` (
  `idintolerancia` int NOT NULL AUTO_INCREMENT,
  `nombreIntolerancia` varchar(45) DEFAULT NULL,
  PRIMARY KEY (`idintolerancia`),
  UNIQUE KEY `idintolerancia_UNIQUE` (`idintolerancia`)
) ENGINE=InnoDB AUTO_INCREMENT=5 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `intolerancia`
--

LOCK TABLES `intolerancia` WRITE;
/*!40000 ALTER TABLE `intolerancia` DISABLE KEYS */;
INSERT INTO `intolerancia` VALUES (1,'Intolerancia a la lactosa'),(2,'Intolerancia a la histamina'),(3,'Intolerancia al glúten'),(4,'Intolerancia al huevo');
/*!40000 ALTER TABLE `intolerancia` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `intoleranciasusuario`
--

DROP TABLE IF EXISTS `intoleranciasusuario`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `intoleranciasusuario` (
  `idintoleranciasUsuario` int NOT NULL AUTO_INCREMENT,
  `idIntolerancia` int DEFAULT NULL,
  `idUsuario` int DEFAULT NULL,
  PRIMARY KEY (`idintoleranciasUsuario`),
  UNIQUE KEY `idintoleranciasUsuario_UNIQUE` (`idintoleranciasUsuario`),
  KEY `idIntolerancia_idx` (`idIntolerancia`),
  KEY `idUsuario_idx` (`idUsuario`),
  CONSTRAINT `idIntolerancia4` FOREIGN KEY (`idIntolerancia`) REFERENCES `intolerancia` (`idintolerancia`),
  CONSTRAINT `idUsuario4` FOREIGN KEY (`idUsuario`) REFERENCES `usuario` (`idusuario`) ON DELETE CASCADE ON UPDATE CASCADE
) ENGINE=InnoDB AUTO_INCREMENT=5 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `intoleranciasusuario`
--

LOCK TABLES `intoleranciasusuario` WRITE;
/*!40000 ALTER TABLE `intoleranciasusuario` DISABLE KEYS */;
INSERT INTO `intoleranciasusuario` VALUES (1,1,1),(2,2,1),(3,3,1),(4,4,2);
/*!40000 ALTER TABLE `intoleranciasusuario` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `producto`
--

DROP TABLE IF EXISTS `producto`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `producto` (
  `idProducto` int NOT NULL AUTO_INCREMENT,
  `nombreProducto` varchar(45) DEFAULT NULL,
  `codigoBarras` varchar(45) DEFAULT NULL,
  `fabricante` varchar(45) DEFAULT NULL,
  `telefono` int DEFAULT NULL,
  PRIMARY KEY (`idProducto`),
  UNIQUE KEY `idproducto_UNIQUE` (`idProducto`)
) ENGINE=InnoDB AUTO_INCREMENT=5 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `producto`
--

LOCK TABLES `producto` WRITE;
/*!40000 ALTER TABLE `producto` DISABLE KEYS */;
INSERT INTO `producto` VALUES (1,'Natilla Con Galletas HACENDADO','346781234678234678','HACENDADO',666666666),(2,'CocaCola 1L','1231231234445','CocaCola SL',667676764),(3,'Pepinos 500g','2316723167231673','HACENDADO',675459325),(4,'LECHE HACENDADO 1L','2312312312312312','HACENDADO',674384582);
/*!40000 ALTER TABLE `producto` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `productosusuario`
--

DROP TABLE IF EXISTS `productosusuario`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `productosusuario` (
  `idproductosUsuario` int NOT NULL AUTO_INCREMENT,
  `idProducto` int DEFAULT NULL,
  `idUsuario` int DEFAULT NULL,
  PRIMARY KEY (`idproductosUsuario`),
  UNIQUE KEY `idproductosUsuario_UNIQUE` (`idproductosUsuario`),
  KEY `idUsuario_idx` (`idUsuario`),
  KEY `idProducto_idx` (`idProducto`),
  CONSTRAINT `idProducto5` FOREIGN KEY (`idProducto`) REFERENCES `producto` (`idProducto`) ON DELETE CASCADE ON UPDATE CASCADE,
  CONSTRAINT `idUsuario5` FOREIGN KEY (`idUsuario`) REFERENCES `usuario` (`idusuario`) ON DELETE CASCADE ON UPDATE CASCADE
) ENGINE=InnoDB AUTO_INCREMENT=5 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `productosusuario`
--

LOCK TABLES `productosusuario` WRITE;
/*!40000 ALTER TABLE `productosusuario` DISABLE KEYS */;
INSERT INTO `productosusuario` VALUES (1,1,1),(2,2,1),(3,3,1),(4,4,2);
/*!40000 ALTER TABLE `productosusuario` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `redeswifi`
--

DROP TABLE IF EXISTS `redeswifi`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `redeswifi` (
  `idredesWifi` int NOT NULL AUTO_INCREMENT,
  `SSID` varchar(45) DEFAULT NULL,
  `PWR` varchar(45) DEFAULT NULL,
  `captureTime` bigint DEFAULT NULL,
  `idComercio` int DEFAULT NULL,
  PRIMARY KEY (`idredesWifi`),
  UNIQUE KEY `idredesWifi_UNIQUE` (`idredesWifi`),
  KEY `idComercio_idx` (`idComercio`),
  CONSTRAINT `idComercio` FOREIGN KEY (`idComercio`) REFERENCES `comercio` (`idComercio`) ON DELETE CASCADE ON UPDATE CASCADE
) ENGINE=InnoDB AUTO_INCREMENT=7 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `redeswifi`
--

LOCK TABLES `redeswifi` WRITE;
/*!40000 ALTER TABLE `redeswifi` DISABLE KEYS */;
INSERT INTO `redeswifi` VALUES (1,'WLAN_1212','-42',1584921601,1),(2,'ONO_512','-50',1584921601,1),(3,'WLAN_666','-60',1584921605,1),(5,'RED_DE_JUAN','-70',1584921607,5);
/*!40000 ALTER TABLE `redeswifi` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `ubicacion`
--

DROP TABLE IF EXISTS `ubicacion`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `ubicacion` (
  `idubicacion` int NOT NULL AUTO_INCREMENT,
  `horaUbicacion` bigint DEFAULT NULL,
  `idredesWifi` int DEFAULT NULL,
  `margenError` float DEFAULT NULL,
  `nombreZona` varchar(45) DEFAULT NULL,
  `idUsuario` int DEFAULT NULL,
  PRIMARY KEY (`idubicacion`),
  UNIQUE KEY `idubicacion_UNIQUE` (`idubicacion`),
  KEY `idredesWifi_idx` (`idredesWifi`),
  KEY `idUsuario_idx` (`idUsuario`),
  CONSTRAINT `idredesWifi` FOREIGN KEY (`idredesWifi`) REFERENCES `redeswifi` (`idredesWifi`) ON DELETE CASCADE ON UPDATE CASCADE,
  CONSTRAINT `idUsuario` FOREIGN KEY (`idUsuario`) REFERENCES `usuario` (`idusuario`) ON DELETE CASCADE ON UPDATE CASCADE
) ENGINE=InnoDB AUTO_INCREMENT=5 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `ubicacion`
--

LOCK TABLES `ubicacion` WRITE;
/*!40000 ALTER TABLE `ubicacion` DISABLE KEYS */;
INSERT INTO `ubicacion` VALUES (1,1584921600,1,0.05,'Pasillo_1',1),(2,1584921600,1,0.05,'Pasillo_1',1),(3,1584921600,1,0.02,'Pasillo_5',2),(4,1584921600,2,0.05,'Pasillo_1',2);
/*!40000 ALTER TABLE `ubicacion` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `usuario`
--

DROP TABLE IF EXISTS `usuario`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `usuario` (
  `idusuario` int NOT NULL AUTO_INCREMENT,
  `idComercio` int DEFAULT NULL,
  PRIMARY KEY (`idusuario`),
  UNIQUE KEY `idusuario_UNIQUE` (`idusuario`),
  KEY `idComercio7_idx` (`idComercio`),
  CONSTRAINT `idComercio7` FOREIGN KEY (`idComercio`) REFERENCES `comercio` (`idComercio`) ON DELETE CASCADE ON UPDATE CASCADE
) ENGINE=InnoDB AUTO_INCREMENT=6 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `usuario`
--

LOCK TABLES `usuario` WRITE;
/*!40000 ALTER TABLE `usuario` DISABLE KEYS */;
INSERT INTO `usuario` VALUES (1,1),(3,1),(4,1),(5,1),(2,5);
/*!40000 ALTER TABLE `usuario` ENABLE KEYS */;
UNLOCK TABLES;
/*!40103 SET TIME_ZONE=@OLD_TIME_ZONE */;

/*!40101 SET SQL_MODE=@OLD_SQL_MODE */;
/*!40014 SET FOREIGN_KEY_CHECKS=@OLD_FOREIGN_KEY_CHECKS */;
/*!40014 SET UNIQUE_CHECKS=@OLD_UNIQUE_CHECKS */;
/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40101 SET CHARACTER_SET_RESULTS=@OLD_CHARACTER_SET_RESULTS */;
/*!40101 SET COLLATION_CONNECTION=@OLD_COLLATION_CONNECTION */;
/*!40111 SET SQL_NOTES=@OLD_SQL_NOTES */;

-- Dump completed on 2020-03-24 18:59:46
