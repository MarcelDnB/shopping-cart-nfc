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
  `telefono` int DEFAULT NULL,
  `CIF` varchar(9) DEFAULT NULL,
  PRIMARY KEY (`idComercio`),
  UNIQUE KEY `idComercio_UNIQUE` (`idComercio`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `comercio`
--

LOCK TABLES `comercio` WRITE;
/*!40000 ALTER TABLE `comercio` DISABLE KEYS */;
/*!40000 ALTER TABLE `comercio` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `contador`
--

DROP TABLE IF EXISTS `contador`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `contador` (
  `idcontador` int NOT NULL AUTO_INCREMENT,
  `idUsuario` int DEFAULT NULL,
  `idProducto` int DEFAULT NULL,
  `vecesEscaneado` varchar(45) DEFAULT NULL,
  PRIMARY KEY (`idcontador`),
  UNIQUE KEY `idcontador_UNIQUE` (`idcontador`),
  KEY `idUsuario_idx` (`idUsuario`),
  KEY `idProducto_idx` (`idProducto`),
  CONSTRAINT `idProducto` FOREIGN KEY (`idProducto`) REFERENCES `producto` (`idproducto`) ON DELETE CASCADE ON UPDATE CASCADE,
  CONSTRAINT `idUsuario` FOREIGN KEY (`idUsuario`) REFERENCES `usuario` (`idusuario`) ON DELETE CASCADE ON UPDATE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `contador`
--

LOCK TABLES `contador` WRITE;
/*!40000 ALTER TABLE `contador` DISABLE KEYS */;
/*!40000 ALTER TABLE `contador` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `gestionusuario`
--

DROP TABLE IF EXISTS `gestionusuario`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `gestionusuario` (
  `idgestionUsuario` int NOT NULL AUTO_INCREMENT,
  `idIntolerancia` int DEFAULT NULL,
  `idComercio` int DEFAULT NULL,
  `idProducto` int DEFAULT NULL,
  `idUbicacion` int DEFAULT NULL,
  PRIMARY KEY (`idgestionUsuario`),
  UNIQUE KEY `idgestionUsuario_UNIQUE` (`idgestionUsuario`),
  KEY `idIntolerancia_idx` (`idIntolerancia`),
  KEY `idComercio_idx` (`idComercio`),
  KEY `idProducto_idx` (`idProducto`),
  KEY `idUbicacion_idx` (`idUbicacion`),
  CONSTRAINT `idComercio1` FOREIGN KEY (`idComercio`) REFERENCES `comercio` (`idComercio`) ON DELETE CASCADE ON UPDATE CASCADE,
  CONSTRAINT `idIntolerancia1` FOREIGN KEY (`idIntolerancia`) REFERENCES `intolerancia` (`idintolerancia`) ON DELETE CASCADE ON UPDATE CASCADE,
  CONSTRAINT `idProducto1` FOREIGN KEY (`idProducto`) REFERENCES `producto` (`idproducto`) ON DELETE CASCADE ON UPDATE CASCADE,
  CONSTRAINT `idUbicacion1` FOREIGN KEY (`idUbicacion`) REFERENCES `ubicacion` (`idubicacion`) ON DELETE CASCADE ON UPDATE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `gestionusuario`
--

LOCK TABLES `gestionusuario` WRITE;
/*!40000 ALTER TABLE `gestionusuario` DISABLE KEYS */;
/*!40000 ALTER TABLE `gestionusuario` ENABLE KEYS */;
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
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `ingrediente`
--

LOCK TABLES `ingrediente` WRITE;
/*!40000 ALTER TABLE `ingrediente` DISABLE KEYS */;
/*!40000 ALTER TABLE `ingrediente` ENABLE KEYS */;
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
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `intolerancia`
--

LOCK TABLES `intolerancia` WRITE;
/*!40000 ALTER TABLE `intolerancia` DISABLE KEYS */;
/*!40000 ALTER TABLE `intolerancia` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `perfilintolerancia`
--

DROP TABLE IF EXISTS `perfilintolerancia`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `perfilintolerancia` (
  `idperfilIntolerancia` int NOT NULL AUTO_INCREMENT,
  `rangoIntolerancia` int DEFAULT NULL,
  `idUsuario` int DEFAULT NULL,
  `idIntolerancia` int DEFAULT NULL,
  PRIMARY KEY (`idperfilIntolerancia`),
  UNIQUE KEY `idperfilIntolerancia_UNIQUE` (`idperfilIntolerancia`),
  KEY `idIntolerancia_idx` (`idIntolerancia`),
  KEY `idUsuario_idx` (`idUsuario`),
  CONSTRAINT `idIntolerancia2` FOREIGN KEY (`idIntolerancia`) REFERENCES `intolerancia` (`idintolerancia`) ON DELETE CASCADE ON UPDATE CASCADE,
  CONSTRAINT `idUsuario2` FOREIGN KEY (`idUsuario`) REFERENCES `usuario` (`idusuario`) ON DELETE CASCADE ON UPDATE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `perfilintolerancia`
--

LOCK TABLES `perfilintolerancia` WRITE;
/*!40000 ALTER TABLE `perfilintolerancia` DISABLE KEYS */;
/*!40000 ALTER TABLE `perfilintolerancia` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `producto`
--

DROP TABLE IF EXISTS `producto`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `producto` (
  `idproducto` int NOT NULL AUTO_INCREMENT,
  `nombreProducto` varchar(45) DEFAULT NULL,
  `codigoBarras` varchar(45) DEFAULT NULL,
  `fabricante` varchar(45) DEFAULT NULL,
  `idIngrediente` int DEFAULT NULL,
  `telefono` int DEFAULT NULL,
  PRIMARY KEY (`idproducto`),
  UNIQUE KEY `idproducto_UNIQUE` (`idproducto`),
  KEY `idIngrediente_idx` (`idIngrediente`),
  CONSTRAINT `idIngrediente` FOREIGN KEY (`idIngrediente`) REFERENCES `ingrediente` (`idingrediente`) ON DELETE CASCADE ON UPDATE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `producto`
--

LOCK TABLES `producto` WRITE;
/*!40000 ALTER TABLE `producto` DISABLE KEYS */;
/*!40000 ALTER TABLE `producto` ENABLE KEYS */;
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
  `timestamp` bigint DEFAULT NULL,
  `idComercio` int DEFAULT NULL,
  PRIMARY KEY (`idredesWifi`),
  UNIQUE KEY `idredesWifi_UNIQUE` (`idredesWifi`),
  KEY `idComercio_idx` (`idComercio`),
  CONSTRAINT `idComercio` FOREIGN KEY (`idComercio`) REFERENCES `comercio` (`idComercio`) ON DELETE CASCADE ON UPDATE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `redeswifi`
--

LOCK TABLES `redeswifi` WRITE;
/*!40000 ALTER TABLE `redeswifi` DISABLE KEYS */;
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
  PRIMARY KEY (`idubicacion`),
  UNIQUE KEY `idubicacion_UNIQUE` (`idubicacion`),
  KEY `idredesWifi_idx` (`idredesWifi`),
  CONSTRAINT `idredesWifi` FOREIGN KEY (`idredesWifi`) REFERENCES `redeswifi` (`idredesWifi`) ON DELETE CASCADE ON UPDATE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `ubicacion`
--

LOCK TABLES `ubicacion` WRITE;
/*!40000 ALTER TABLE `ubicacion` DISABLE KEYS */;
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
  `idGestionUsuario` int DEFAULT NULL,
  PRIMARY KEY (`idusuario`),
  UNIQUE KEY `idusuario_UNIQUE` (`idusuario`),
  KEY `idGestionUsuario_idx` (`idGestionUsuario`),
  CONSTRAINT `idGestionUsuario` FOREIGN KEY (`idGestionUsuario`) REFERENCES `gestionusuario` (`idgestionUsuario`) ON DELETE CASCADE ON UPDATE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `usuario`
--

LOCK TABLES `usuario` WRITE;
/*!40000 ALTER TABLE `usuario` DISABLE KEYS */;
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

-- Dump completed on 2020-03-23 13:23:22
