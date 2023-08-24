/**
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements. See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership. The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License. You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied. See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */
package org.apache.fineract.portfolio.client.domain;

import org.apache.fineract.infrastructure.core.api.JsonCommand;
import org.apache.fineract.infrastructure.core.domain.AbstractPersistableCustom;
import org.apache.fineract.portfolio.client.api.ClientApiConstants;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

@Entity
@Table(name = "m_client_contact_info")
public class ClientContactInformation extends AbstractPersistableCustom {

    @Column(name = "area", nullable = false)
    private Integer area;

    @Column(name = "housing_type", nullable = false)
    private Integer housingType;

    @Column(name = "years_of_residence", nullable = false)
    private Integer yearsOfResidence;

    @Column(name = "public_service_types", nullable = false)
    private String serviceTypes;

    @Column(name = "department_id", nullable = false)
    private Integer departmentId;

    @Column(name = "municipality_id", nullable = false)
    private Integer municipalityId;

    @Column(name = "village")
    private String village;

    @Column(name = "reference_housing_data")
    private String referenceHousingData;

    @Column(name = "street")
    private String street;

    @Column(name = "avenue")
    private String avenue;

    @Column(name = "home_number")
    private String houseNumber;

    @Column(name = "colony")
    private String colony;

    @Column(name = "sector")
    private String sector;

    @Column(name = "batch")
    private String batch;

    @Column(name = "square")
    private String square;

    @Column(name = "zone")
    private String zone;

    @Column(name = "light_meter_number")
    private String lightMeterNumber;

    @Column(name = "home_phone")
    private String homePhone;

    @ManyToOne
    @JoinColumn(name = "client_id")
    private Client client;


    protected ClientContactInformation() {
        //
    }

    public ClientContactInformation(Integer area, Integer housingType, Integer yearsOfResidence, String serviceTypes,
                                    Integer departmentId, Integer municipalityId, String village, String referenceHousingData,
                                    String street, String avenue, String houseNumber, String colony, String sector,
                                    String batch, String square, String zone, String lightMeterNumber, String homePhone,
                                    Client newClient) {
        this.area = area;
        this.housingType = housingType;
        this.yearsOfResidence = yearsOfResidence;
        this.serviceTypes = serviceTypes;
        this.departmentId = departmentId;
        this.municipalityId = municipalityId;
        this.village = village;
        this.referenceHousingData = referenceHousingData;
        this.street = street;
        this.avenue = avenue;
        this.houseNumber = houseNumber;
        this.colony = colony;
        this.sector = sector;
        this.batch = batch;
        this.square = square;
        this.zone = zone;
        this.lightMeterNumber = lightMeterNumber;
        this.homePhone = homePhone;
        this.client = newClient;

    }

    public static ClientContactInformation fromJson(Client newClient, JsonCommand command) {

        final Integer area = command.integerValueOfParameterNamed(ClientApiConstants.clientAreaParamName);
        final Integer housingType = command.integerValueOfParameterNamed(ClientApiConstants.housingTypeIdParamName);
        final Integer yearsOfResidence = command.integerValueOfParameterNamed(ClientApiConstants.residenceYearsParamName);
        final String serviceTypes = command.stringValueOfParameterNamed(ClientApiConstants.serviceIdParamName);
        final Integer departmentId = command.integerValueOfParameterNamed(ClientApiConstants.departmentIdParamName);
        final Integer municipalityId = command.integerValueOfParameterNamed(ClientApiConstants.municipalIdParamName);
        final String village = command.stringValueOfParameterNamed(ClientApiConstants.villageParamName);
        final String referenceHousingData = command.stringValueOfParameterNamed(ClientApiConstants.referenceDataParamName);
        final String street = command.stringValueOfParameterNamed(ClientApiConstants.streetParamName);
        final String avenue = command.stringValueOfParameterNamed(ClientApiConstants.avenueParamName);
        final String houseNumber = command.stringValueOfParameterNamed(ClientApiConstants.houseNumberParamName);
        final String colony = command.stringValueOfParameterNamed(ClientApiConstants.colonyParamName);
        final String sector = command.stringValueOfParameterNamed(ClientApiConstants.sectorParamName);
        final String batch = command.stringValueOfParameterNamed(ClientApiConstants.batchParamName);
        final String square = command.stringValueOfParameterNamed(ClientApiConstants.squareParamName);
        final String zone = command.stringValueOfParameterNamed(ClientApiConstants.zoneParamName);
        final String lightMeterNumber = command.stringValueOfParameterNamed(ClientApiConstants.lightDeviceNumberParamName);
        final String homePhone = command.stringValueOfParameterNamed(ClientApiConstants.homeNumberParamName);
        return new ClientContactInformation(area, housingType, yearsOfResidence, serviceTypes, departmentId, municipalityId, village,
                referenceHousingData, street, avenue, houseNumber, colony, sector, batch, square, zone, lightMeterNumber, homePhone, newClient);
    }
}
