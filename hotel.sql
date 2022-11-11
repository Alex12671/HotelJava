-- phpMyAdmin SQL Dump
-- version 5.2.0
-- https://www.phpmyadmin.net/
--
-- Servidor: 127.0.0.1
-- Tiempo de generación: 11-11-2022 a las 06:33:53
-- Versión del servidor: 10.4.24-MariaDB
-- Versión de PHP: 8.1.6

SET SQL_MODE = "NO_AUTO_VALUE_ON_ZERO";
START TRANSACTION;
SET time_zone = "+00:00";


/*!40101 SET @OLD_CHARACTER_SET_CLIENT=@@CHARACTER_SET_CLIENT */;
/*!40101 SET @OLD_CHARACTER_SET_RESULTS=@@CHARACTER_SET_RESULTS */;
/*!40101 SET @OLD_COLLATION_CONNECTION=@@COLLATION_CONNECTION */;
/*!40101 SET NAMES utf8mb4 */;

--
-- Base de datos: `hotel`
--
CREATE DATABASE IF NOT EXISTS `hotel` DEFAULT CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci;
USE `hotel`;

-- --------------------------------------------------------

--
-- Estructura de tabla para la tabla `admin`
--

CREATE TABLE `admin` (
  `user` varchar(90) NOT NULL,
  `password` varchar(255) NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

--
-- Volcado de datos para la tabla `admin`
--

INSERT INTO `admin` (`user`, `password`) VALUES
('admin', 'admin');

-- --------------------------------------------------------

--
-- Estructura de tabla para la tabla `clientes`
--

CREATE TABLE `clientes` (
  `DNI` varchar(9) NOT NULL,
  `Nom` text NOT NULL,
  `Cognoms` text NOT NULL,
  `Nacionalitat` text NOT NULL,
  `Telefon` int(18) NOT NULL,
  `Email` varchar(255) NOT NULL,
  `Ocupacio` varchar(255) NOT NULL,
  `Estat_Civil` enum('Casado/a','Soltero/a','Separado/a','Divorciado/a','Viudo/a') NOT NULL,
  `Activado` tinyint(1) NOT NULL DEFAULT 1
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

--
-- Volcado de datos para la tabla `clientes`
--

INSERT INTO `clientes` (`DNI`, `Nom`, `Cognoms`, `Nacionalitat`, `Telefon`, `Email`, `Ocupacio`, `Estat_Civil`, `Activado`) VALUES
('1', 'cliente', '3', 'e', 58, 'd', 'c', 'Separado/a', 0),
('54928153A', 'cliente', '3', 'Español', 635120485, 'cliente3@mail.es', 'Albañil', 'Separado/a', 1),
('58496527L', 'r', 'j', 'sc', 25, 'polñ', 'p', 'Divorciado/a', 1);

-- --------------------------------------------------------

--
-- Estructura de tabla para la tabla `habitaciones`
--

CREATE TABLE `habitaciones` (
  `Numero` int(11) NOT NULL,
  `Planta` int(11) NOT NULL,
  `Precio` int(11) NOT NULL,
  `Estado` enum('Disponible','Ocupado','En mantenimiento') NOT NULL DEFAULT 'Disponible',
  `Tipo` enum('Individual','Doble','Familiar') NOT NULL,
  `Numero_Camas` int(1) NOT NULL,
  `Superficie` varchar(20) NOT NULL,
  `Wifi` enum('Si','No') NOT NULL,
  `Minibar` enum('Si','No') NOT NULL,
  `Activado` tinyint(1) NOT NULL DEFAULT 1
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

--
-- Volcado de datos para la tabla `habitaciones`
--

INSERT INTO `habitaciones` (`Numero`, `Planta`, `Precio`, `Estado`, `Tipo`, `Numero_Camas`, `Superficie`, `Wifi`, `Minibar`, `Activado`) VALUES
(1, 2, 18, 'Disponible', 'Doble', 1, '20', 'Si', 'No', 1);

-- --------------------------------------------------------

--
-- Estructura de tabla para la tabla `recepcionistas`
--

CREATE TABLE `recepcionistas` (
  `Id` int(11) NOT NULL,
  `Usuario` varchar(90) NOT NULL,
  `Password` varchar(255) NOT NULL,
  `Nombre` varchar(90) NOT NULL,
  `Apellidos` varchar(90) NOT NULL,
  `DNI` varchar(10) NOT NULL,
  `Nacionalidad` varchar(3) NOT NULL,
  `Teléfono` int(15) NOT NULL,
  `Email` varchar(255) NOT NULL,
  `Validado` tinyint(1) NOT NULL DEFAULT 0
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

--
-- Volcado de datos para la tabla `recepcionistas`
--

INSERT INTO `recepcionistas` (`Id`, `Usuario`, `Password`, `Nombre`, `Apellidos`, `DNI`, `Nacionalidad`, `Teléfono`, `Email`, `Validado`) VALUES
(1, 'a', 'b', 'c', 'd', 'e', 'f', 7878, 'h', 1),
(2, 'recep2', '1', 'recep', '2', '12345678B', 'ESP', 123456798, 'recep2@mail.es', 0);

-- --------------------------------------------------------

--
-- Estructura de tabla para la tabla `reservas`
--

CREATE TABLE `reservas` (
  `IdReserva` int(11) NOT NULL,
  `IdCliente` varchar(9) NOT NULL,
  `IdRecepcionista` varchar(9) NOT NULL,
  `Numero` int(11) NOT NULL,
  `Fecha_Ingreso` date NOT NULL,
  `Hora_Ingreso` time NOT NULL,
  `Fecha_Salida` date NOT NULL,
  `Hora_Salida` time NOT NULL,
  `Costo` int(11) NOT NULL,
  `Estado` enum('Anulada','Pagada','Pendiente de pago') NOT NULL DEFAULT 'Pendiente de pago'
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

--
-- Volcado de datos para la tabla `reservas`
--

INSERT INTO `reservas` (`IdReserva`, `IdCliente`, `IdRecepcionista`, `Numero`, `Fecha_Ingreso`, `Hora_Ingreso`, `Fecha_Salida`, `Hora_Salida`, `Costo`, `Estado`) VALUES
(1, '54928153A', '12345678B', 1, '2022-11-11', '06:00:31', '2022-11-18', '06:01:31', 25, 'Pendiente de pago');

--
-- Índices para tablas volcadas
--

--
-- Indices de la tabla `clientes`
--
ALTER TABLE `clientes`
  ADD PRIMARY KEY (`DNI`);

--
-- Indices de la tabla `habitaciones`
--
ALTER TABLE `habitaciones`
  ADD PRIMARY KEY (`Numero`);

--
-- Indices de la tabla `recepcionistas`
--
ALTER TABLE `recepcionistas`
  ADD PRIMARY KEY (`Id`),
  ADD KEY `DNI` (`DNI`);

--
-- Indices de la tabla `reservas`
--
ALTER TABLE `reservas`
  ADD PRIMARY KEY (`IdReserva`),
  ADD UNIQUE KEY `IdCliente` (`IdCliente`,`IdRecepcionista`,`Numero`),
  ADD KEY `Numero` (`Numero`),
  ADD KEY `IdRecepcionista` (`IdRecepcionista`);

--
-- AUTO_INCREMENT de las tablas volcadas
--

--
-- AUTO_INCREMENT de la tabla `recepcionistas`
--
ALTER TABLE `recepcionistas`
  MODIFY `Id` int(11) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=5;

--
-- AUTO_INCREMENT de la tabla `reservas`
--
ALTER TABLE `reservas`
  MODIFY `IdReserva` int(11) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=2;

--
-- Restricciones para tablas volcadas
--

--
-- Filtros para la tabla `reservas`
--
ALTER TABLE `reservas`
  ADD CONSTRAINT `reservas_ibfk_1` FOREIGN KEY (`Numero`) REFERENCES `habitaciones` (`Numero`),
  ADD CONSTRAINT `reservas_ibfk_2` FOREIGN KEY (`IdRecepcionista`) REFERENCES `recepcionistas` (`DNI`),
  ADD CONSTRAINT `reservas_ibfk_3` FOREIGN KEY (`IdCliente`) REFERENCES `clientes` (`DNI`);
COMMIT;

/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40101 SET CHARACTER_SET_RESULTS=@OLD_CHARACTER_SET_RESULTS */;
/*!40101 SET COLLATION_CONNECTION=@OLD_COLLATION_CONNECTION */;
