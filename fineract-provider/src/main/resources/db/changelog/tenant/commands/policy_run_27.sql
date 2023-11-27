--
-- Licensed to the Apache Software Foundation (ASF) under one
-- or more contributor license agreements. See the NOTICE file
-- distributed with this work for additional information
-- regarding copyright ownership. The ASF licenses this file
-- to you under the Apache License, Version 2.0 (the
-- "License"); you may not use this file except in compliance
-- with the License. You may obtain a copy of the License at
--
-- http://www.apache.org/licenses/LICENSE-2.0
--
-- Unless required by applicable law or agreed to in writing,
-- software distributed under the License is distributed on an
-- "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
-- KIND, either express or implied. See the License for the
-- specific language governing permissions and limitations
-- under the License.
--

-- liquibase formatted sql
-- changeset fineract:1
-- MySQL dump 10.13  Distrib 5.1.60, for Win32 (ia32)
--
-- Host: localhost    Database: fineract_default
-- ------------------------------------------------------
-- Server version	5.1.60-community

/*!40101 SET @OLD_CHARACTER_SET_CLIENT=@@CHARACTER_SET_CLIENT */;
/*!40101 SET @OLD_CHARACTER_SET_RESULTS=@@CHARACTER_SET_RESULTS */;
/*!40101 SET @OLD_COLLATION_CONNECTION=@@COLLATION_CONNECTION */;
/*!40101 SET NAMES UTF8MB4 */;
/*!40103 SET @OLD_TIME_ZONE=@@TIME_ZONE */;
/*!40103 SET TIME_ZONE='+00:00' */;
/*!40014 SET @OLD_UNIQUE_CHECKS=@@UNIQUE_CHECKS, UNIQUE_CHECKS=0 */;
/*!40014 SET @OLD_FOREIGN_KEY_CHECKS=@@FOREIGN_KEY_CHECKS, FOREIGN_KEY_CHECKS=0 */;
/*!40101 SET @OLD_SQL_MODE=@@SQL_MODE, SQL_MODE='NO_AUTO_VALUE_ON_ZERO' */;
/*!40111 SET @OLD_SQL_NOTES=@@SQL_NOTES, SQL_NOTES=0 */;

-- Client Age Hard Policy Check --
INSERT INTO stretchy_report (report_name,report_type,report_category,report_sql,description,core_report,use_report,self_service_user_report)
VALUES ("Cancelled Cycles Count Policy Check", "Table", "Prequalification",
"SELECT
 prequalification_details.recreditCategorization,
 COALESCE(prequalification_details.number_of_completed_loans, 0) AS  number_of_completed_loans,
  CASE
      WHEN (${loanProductId} = 9) AND (prequalification_details.recreditCategorization = 'RECREDITO') AND ('${clientArea}' = 'URBAN') AND (COALESCE(prequalification_details.number_of_completed_loans, 0) >= 1) THEN 'GREEN'
      WHEN (${loanProductId} = 9) AND (prequalification_details.recreditCategorization = 'RECREDITO') AND ('${clientArea}' = 'RURAL') AND (COALESCE(prequalification_details.number_of_completed_loans, 0) >= 1) THEN 'GREEN'
  END AS color
 FROM m_prequalification_group mpg
 LEFT JOIN (
     SELECT p.id AS prequalification_id,
     (CASE WHEN p.previous_prequalification IS NOT NULL THEN 'RECREDITO' ELSE 'NUEVO' END) AS recreditCategorization,
     (SELECT COUNT(*)
     FROM m_prequalification_group mp
     LEFT JOIN m_group_prequalification_relationship mgpr ON mgpr.prequalification_id = mp.id
     LEFT JOIN m_group mg ON (mg.prequalification_id = mp.id OR mg.id = mgpr.group_id)
     LEFT JOIN m_loan ml ON ml.group_id = mg.id
     WHERE ml.loan_status_id >= 600 AND mg.status_enum =  300 AND mp.id = ${prequalificationId}
     GROUP BY mp.id) AS number_of_completed_loans
     FROM m_prequalification_group p
 )prequalification_details ON prequalification_details.prequalification_id = mpg.id
 WHERE mpg.id = ${prequalificationId}", "Cancelled Cycles Count Policy Check", 0, 0, 0);