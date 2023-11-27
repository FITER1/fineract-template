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

-- Client loan collection report --
INSERT INTO stretchy_report (report_name,report_type,report_category,report_sql,description,core_report,use_report,self_service_user_report)
VALUES ("Reporte por Clienta de Cobranzas y Saldos por Financista", "Table", "Loan",  "SELECT
mc.account_no  AS 'Codigo Cliente',
mc.display_name AS 'Cliente',
mc.external_id  AS 'Nro Solicitud',
agency.name AS 'Agencia',
clientgroup.account_no AS 'Nro Grupo',
clientgroup.display_name AS 'Nombre Grupo',
clientloan.approved_principal AS 'Monto Otorgado',
clientloan.principal_outstanding_derived AS 'Saldo Capital',
clientloan.principal_repaid_derived AS 'Capital Recuperado',
clientloan.interest_repaid_derived AS 'Intereses',
clientloan.total_repaid AS 'Total Cobrad'
FROM m_loan ml
INNER JOIN m_client mc ON mc.id = ml.client_id
INNER JOIN (
 SELECT SUM(IFNULL(ml.approved_principal, 0)) AS approved_principal,
         SUM(IFNULL(ml.principal_outstanding_derived, 0)) AS principal_outstanding_derived,
         SUM(IFNULL(ml.principal_repaid_derived, 0)) AS  principal_repaid_derived,
         SUM(IFNULL(ml.interest_repaid_derived, 0)) AS interest_repaid_derived,
         SUM(IFNULL(ml.principal_repaid_derived, 0) + IFNULL(ml.interest_repaid_derived, 0)) AS total_repaid,
         ml.client_id AS client_id
 FROM m_loan ml
 GROUP BY ml.client_id
) clientloan ON clientloan.client_id = mc.id
LEFT JOIN m_office clientoffice ON clientoffice.id = mc.office_id
LEFT JOIN m_office clientoounder on clientoounder.hierarchy LIKE CONCAT(clientoounder.hierarchy, '%')
LEFT JOIN m_agency agency ON agency.linked_office_id = clientoounder.id
LEFT JOIN m_fund mf ON mf.id = ml.fund_id
LEFT JOIN m_group_client mgc ON mgc.client_id = mc.id
LEFT JOIN m_group clientgroup ON clientgroup.id = mgc.group_id
WHERE (ml.submittedon_date BETWEEN '${submittedOnStartDate}' AND '${submittedOnEndDate}')
AND (ml.product_id = '${fundId}' OR '-1' = '${fundId}')
GROUP BY mc.id", "Reporte por Clienta de Cobranzas y Saldos por Financista", 1, 1, 0);

INSERT INTO stretchy_report_parameter
(report_id, parameter_id, report_parameter_name)
VALUES((SELECT sr.id FROM stretchy_report sr WHERE sr.report_name = "Reporte por Clienta de Cobranzas y Saldos por Financista"), (SELECT p.id FROM stretchy_parameter p WHERE parameter_name = "fundIdSelectAll"), "fundId");

INSERT INTO stretchy_report_parameter
(report_id, parameter_id, report_parameter_name)
VALUES((SELECT sr.id FROM stretchy_report sr WHERE sr.report_name = "Reporte por Clienta de Cobranzas y Saldos por Financista"), (SELECT p.id FROM stretchy_parameter p WHERE parameter_name = "submittedOnStartDateSelect"), "submittedOnEndDate");

INSERT INTO stretchy_report_parameter
(report_id, parameter_id, report_parameter_name)
VALUES((SELECT sr.id FROM stretchy_report sr WHERE sr.report_name = "Reporte por Clienta de Cobranzas y Saldos por Financista"), (SELECT p.id FROM stretchy_parameter p WHERE parameter_name = "submittedOnEndDateSelect"), "submittedOnEndDate");