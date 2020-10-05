
CREATE TABLE `Session` (  
  id bigint(20) primary key not null auto_increment,
  `userId` varchar(255) DEFAULT NULL,
  `paymentId` varchar(255) DEFAULT NULL,
  `name` varchar(50) DEFAULT NULL,
  `email` varchar(255) DEFAULT NULL,
  `phoneNo` varchar(50) DEFAULT NULL, 
  `amount` varchar(50) DEFAULT NULL, 
  `currency` varchar(50) DEFAULT NULL, 
  `paymentdescription` varchar(500) DEFAULT NULL,
  `startDate` varchar(50) DEFAULT NULL,
  `endDate` varchar(50) DEFAULT NULL,
  `sessionStatus` ENUM('ACTIVE', 'INACTIVE') NOT NULL
  ) ENGINE=InnoDB DEFAULT CHARSET=utf8;
  
  
  CREATE TABLE `visa` (
  id bigint(20) primary key not null auto_increment,
  `visaTransactionId` bigint(20) DEFAULT NULL,
  `sessionId` bigint(20) DEFAULT NULL,
  `amount` bigint(20) DEFAULT NULL,
  `interactionOperation` varchar(30) DEFAULT NULL,
  `merchantId` varchar(255) DEFAULT NULL,
  `merchantCategoryCode` varchar(255) DEFAULT NULL,
  `orderId` bigint(20) DEFAULT NULL,
  `currency` varchar(50) DEFAULT NULL,
  `description` varchar(255) DEFAULT NULL,
  `creationTime` varchar(255) DEFAULT NULL,
  `customerName` varchar(255) DEFAULT NULL,
  `customerOrderDate`varchar(255) DEFAULT NULL,
  `deviceType` varchar(255) DEFAULT NULL,
  `ipAddress` varchar(55) DEFAULT NULL,
  `result` varchar(50) DEFAULT NULL,
  `brand` varchar(50) DEFAULT NULL,
  `expiryMonth` varchar(50) DEFAULT NULL,
  `expiryYear` varchar(50) DEFAULT NULL,
  `fundingMethod` varchar(50) DEFAULT NULL,
  `issuer` varchar(50) DEFAULT NULL,
  `nameOnCard` varchar(255) DEFAULT NULL,
  `number` varchar(255) DEFAULT NULL,
  `scheme` varchar(255) DEFAULT NULL,
  `storedOnFile` varchar(255) DEFAULT NULL,
  `type` varchar(255) DEFAULT NULL,
  `status` varchar(255) DEFAULT NULL,
 `totalAuthorizedAmount` varchar(255) DEFAULT NULL,
 `totalCapturedAmount` varchar(255) DEFAULT NULL,
 `totalRefundedAmount` varchar(255) DEFAULT NULL,
  KEY `FK_s9y70mi0ken20v5xk44h573hd` (`visaTransactionId`),
  CONSTRAINT `FK_s9y70mi0ken20v5xk44h573hd` FOREIGN KEY (`visaTransactionId`) REFERENCES `VisaTransaction` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;


CREATE TABLE `VisaTransaction` (  
  id bigint(20) primary key not null auto_increment,
  `secureId` varchar(50) DEFAULT NULL,
  `authenticationToken` varchar(50) DEFAULT NULL,
  `xid` varchar(25) DEFAULT NULL,  
  `gatewayEntryPoint` varchar(50) DEFAULT NULL,
  `acquirerMessage` varchar(255) DEFAULT NULL,
  `gatewayCode` varchar(255) DEFAULT NULL,
  `batch` varchar(25) DEFAULT NULL,
  `amount` bigint(20) DEFAULT NULL,
  `authorizationCode` varchar(55) DEFAULT NULL,
  `currency` varchar(50) DEFAULT NULL,
  `transactionId` varchar(50) DEFAULT NULL,
  `frequency` varchar(50) DEFAULT NULL,
  `receipt` varchar(50) DEFAULT NULL,
  `source` varchar(50) DEFAULT NULL,
  `taxAmount` varchar(50) DEFAULT NULL,
  `terminal` varchar(255) DEFAULT NULL,
  `type` varchar(255) DEFAULT NULL,
  `version` int DEFAULT NULL) ENGINE=InnoDB DEFAULT CHARSET=utf8;
  
  CREATE TABLE `cbPayTransaction` (
  `tranID` bigint NOT NULL,
  `sessionId` bigint(20) DEFAULT NULL,
  `reqId` varchar(32) NOT NULL,
  `merId` varchar(16) NOT NULL,
  `subMerId` varchar(16) NOT NULL,
  `terminalId` varchar(8) NOT NULL,
  `transAmount` varchar(13) NOT NULL,
  `transCurrency` varchar(3) NOT NULL,
  `ref1` varchar(25) NOT NULL,
  `ref2` varchar(25) NOT NULL,
  `merDqrCode` varchar(512) NOT NULL,
  `transExpiredTime` varchar(19) NOT NULL,
  `refNo` varchar(16) NOT NULL,
  `transRef` varchar(16) NOT NULL,
  `transStatus` varchar(1) NOT NULL,
  `code` varchar(45) NOT NULL,
  `msg` varchar(255) NOT NULL,
  `t1` varchar(45) NOT NULL,
  `t2` varchar(45) NOT NULL,
  `t3` varchar(45) NOT NULL,
  PRIMARY KEY (`tranID`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;


CREATE TABLE `mpuPaymentTransaction` (
  `tranID` bigint NOT NULL,
  `sessionId` bigint(20) DEFAULT NULL,
  `merchantID` varchar(15) NOT NULL,
  `respCode` varchar(2) NOT NULL,
  `pan` varchar(16) NOT NULL,
  `amount` varchar(12) NOT NULL,
  `invoiceNo` varchar(20) NOT NULL,
  `tranRef` varchar(28) NOT NULL,
  `approvalCode` varchar(6) NOT NULL,
  `dateTime` varchar(14) NOT NULL,
  `status` varchar(2) NOT NULL,
  `failReason` varchar(100) NOT NULL,
  `categoryCode` varchar(20) NOT NULL,
  `currencyCode` varchar(3) NOT NULL,
  `userDefined1` varchar(150) NOT NULL,
  `userDefined2` varchar(150) NOT NULL,
  `userDefined3` varchar(150) NOT NULL,
  `link` varchar(225) NOT NULL,
  `t1` varchar(45) NOT NULL,
  `t2` varchar(45) NOT NULL,
  `t3` varchar(45) NOT NULL,
  `creationDate` varchar(150) NOT NULL,
  PRIMARY KEY (`tranID`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
