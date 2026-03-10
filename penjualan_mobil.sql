/*
 Navicat Premium Data Transfer

 Source Server         : jovian
 Source Server Type    : MySQL
 Source Server Version : 100432 (10.4.32-MariaDB)
 Source Host           : localhost:3306
 Source Schema         : penjualan_mobil

 Target Server Type    : MySQL
 Target Server Version : 100432 (10.4.32-MariaDB)
 File Encoding         : 65001

 Date: 09/03/2026 14:04:32
*/

SET NAMES utf8mb4;
SET FOREIGN_KEY_CHECKS = 0;

-- ----------------------------
-- Table structure for bayar_cicilan
-- ----------------------------
DROP TABLE IF EXISTS `bayar_cicilan`;
CREATE TABLE `bayar_cicilan`  (
  `kode_cicilan` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL,
  `kode_kredit` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL,
  `tanggal_cicilan` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL,
  `cicilanke` int NOT NULL,
  `jumlah_cicilan` int NOT NULL,
  `sisacicilke` int NOT NULL,
  `sisa_cicilan` int NOT NULL,
  PRIMARY KEY (`kode_cicilan`) USING BTREE,
  INDEX `kode_kredit`(`kode_kredit` ASC) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of bayar_cicilan
-- ----------------------------

-- ----------------------------
-- Table structure for beli_cash
-- ----------------------------
DROP TABLE IF EXISTS `beli_cash`;
CREATE TABLE `beli_cash`  (
  `kode_cash` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL,
  `ktp` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL,
  `kode_mobil` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL,
  `cash_tgl` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL,
  `cash_bayar` int NOT NULL,
  PRIMARY KEY (`kode_cash`) USING BTREE,
  INDEX `ktp`(`ktp` ASC, `kode_mobil` ASC) USING BTREE,
  INDEX `kode_mobil`(`kode_mobil` ASC) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of beli_cash
-- ----------------------------
INSERT INTO `beli_cash` VALUES ('37465u', '098763456785', 'kk234', '2025-11-10', 999999999);
INSERT INTO `beli_cash` VALUES ('kdfvur8', '098763456785', 'M005', '2025-11-3', 300000000);

-- ----------------------------
-- Table structure for kredit
-- ----------------------------
DROP TABLE IF EXISTS `kredit`;
CREATE TABLE `kredit`  (
  `kode_kredit` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL,
  `ktp` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL,
  `kode_paket` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL,
  `kode_mobil` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL,
  `tanggal_kredit` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL,
  `bayar_kredit` int NOT NULL,
  `tenor` int NOT NULL,
  `totalcicil` int NOT NULL,
  PRIMARY KEY (`kode_kredit`) USING BTREE,
  INDEX `ktp`(`ktp` ASC, `kode_paket` ASC, `kode_mobil` ASC) USING BTREE,
  INDEX `kode_paket`(`kode_paket` ASC) USING BTREE,
  INDEX `kode_mobil`(`kode_mobil` ASC) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of kredit
-- ----------------------------
INSERT INTO `kredit` VALUES ('cash3847', '098763456785', 'p45687', 'kk234', '2025-11-12', -21474836, 100, -2147483648);
INSERT INTO `kredit` VALUES ('hencjkdj', '098763456785', 'p45687', 'M005', '2025-11-3', -21474836, 100, -2147483648);

-- ----------------------------
-- Table structure for mobil
-- ----------------------------
DROP TABLE IF EXISTS `mobil`;
CREATE TABLE `mobil`  (
  `kode_mobil` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL,
  `merk` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL,
  `type` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL,
  `warna` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL,
  `harga` int NOT NULL,
  `foto_mobil` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL,
  PRIMARY KEY (`kode_mobil`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of mobil
-- ----------------------------
INSERT INTO `mobil` VALUES ('1ed1', 'porche', 'gtr 119', 'white', 900000000, 'uploads/mobil/1ed1_1773039482.jpg');
INSERT INTO `mobil` VALUES ('k909', 'honda', 'hrfgf', 'black', 2312, NULL);

-- ----------------------------
-- Table structure for paket
-- ----------------------------
DROP TABLE IF EXISTS `paket`;
CREATE TABLE `paket`  (
  `kode_paket` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL,
  `uang_muka` int NOT NULL,
  `tenor` int NOT NULL,
  `bunga_cicilan` int NOT NULL,
  PRIMARY KEY (`kode_paket`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of paket
-- ----------------------------
INSERT INTO `paket` VALUES ('p45687', 70, 100, 50);

-- ----------------------------
-- Table structure for pembeli
-- ----------------------------
DROP TABLE IF EXISTS `pembeli`;
CREATE TABLE `pembeli`  (
  `ktp` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL,
  `nama_pembeli` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL,
  `alamat_pembeli` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL,
  `telp_pembeli` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL,
  `foto_ktp` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL,
  PRIMARY KEY (`ktp`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of pembeli
-- ----------------------------
INSERT INTO `pembeli` VALUES ('098763456785', 'BIJIIIIIII', 'BELAHAN DUNIAAAAAAAAA', '08987600000', NULL);
INSERT INTO `pembeli` VALUES ('234567890', 'jon suka sepupu', 'sepupu', '0987234567', NULL);

-- ----------------------------
-- Table structure for user
-- ----------------------------
DROP TABLE IF EXISTS `user`;
CREATE TABLE `user`  (
  `username` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL,
  `password` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL,
  `level` int NOT NULL,
  `id_user` int NOT NULL AUTO_INCREMENT,
  PRIMARY KEY (`id_user`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 3 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of user
-- ----------------------------
INSERT INTO `user` VALUES ('sales', 's*', 1, 1);
INSERT INTO `user` VALUES ('manager', 'm*', 2, 2);

SET FOREIGN_KEY_CHECKS = 1;
