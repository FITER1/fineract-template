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
package org.apache.fineract.portfolio.group.service;

import java.math.BigDecimal;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;
import java.time.LocalTime;
import org.apache.fineract.infrastructure.core.data.EnumOptionData;
import org.apache.fineract.infrastructure.core.domain.JdbcSupport;
import org.apache.fineract.portfolio.client.domain.ClientEnumerations;
import org.apache.fineract.portfolio.group.data.GroupGeneralData;
import org.apache.fineract.portfolio.group.data.GroupTimelineData;
import org.springframework.jdbc.core.RowMapper;

/**
 *
 */
public final class AllGroupTypesDataMapper implements RowMapper<GroupGeneralData> {

    private final String schemaSql;

    public AllGroupTypesDataMapper() {
        final StringBuilder sqlBuilder = new StringBuilder(400);
        sqlBuilder.append("g.id as id, g.account_no as accountNumber, g.external_id as externalId, g.display_name as name, ");
        sqlBuilder.append("g.office_id as officeId, o.name as officeName, ");
        sqlBuilder.append("g.parent_id as centerId, pg.display_name as centerName, ");
        sqlBuilder.append("g.staff_id as staffId, s.display_name as staffName, ");
        sqlBuilder.append("g.status_enum as statusEnum, g.activation_date as activationDate, ");
        sqlBuilder.append("g.closedon_date as closedOnDate, ");
        sqlBuilder.append("g.meeting_start_time as meetingStartTime, g.meeting_end_time as meetingEndTime, "); //
        sqlBuilder.append("g.meeting_start_date as meetingStartDate, g.meeting_end_date as meetingEndDate, "); //
        sqlBuilder.append("g.meeting_day as meetingDay, g.group_location as groupLocation, "); //
        sqlBuilder.append("cvMeetingDay.code_value as meetingDayValue ,cvMeetingDay.order_position as meetingDayOrderPosition, g.group_location as groupLocation, "); //

        sqlBuilder.append("g.submittedon_date as submittedOnDate, ");
        sqlBuilder.append("sbu.username as submittedByUsername, ");
        sqlBuilder.append("sbu.firstname as submittedByFirstname, ");
        sqlBuilder.append("sbu.lastname as submittedByLastname, ");

        sqlBuilder.append("clu.username as closedByUsername, ");
        sqlBuilder.append("clu.firstname as closedByFirstname, ");
        sqlBuilder.append("clu.lastname as closedByLastname, ");

        sqlBuilder.append("acu.username as activatedByUsername, ");
        sqlBuilder.append("acu.firstname as activatedByFirstname, ");
        sqlBuilder.append("acu.lastname as activatedByLastname, ");

        sqlBuilder.append("g.hierarchy as hierarchy, ");
        sqlBuilder.append("g.level_id as groupLevel, ");

        sqlBuilder.append("g.portfolio_center_id as portfolioCenterId, g.legacy_group_number as legacyGroupNumber, ");
        sqlBuilder.append("g.latitude as latitude, g.longitude as longitude, ");
        sqlBuilder.append("g.formation_date as formationDate, g.responsible_user_id as responsibleUserId, ");
        sqlBuilder.append(" ru.firstname as userFirstName, ru.lastname as userLastName, ");
        sqlBuilder.append("g.size as size, g.created_date as createdDate, ");
        sqlBuilder.append("g.meeting_start_time as meetingStartTime, g.meeting_end_time as meetingEndTime ");

        sqlBuilder.append("from m_group g ");
        sqlBuilder.append("left join m_code_value cvMeetingDay on g.meeting_day = cvMeetingDay.id ");
        sqlBuilder.append("join m_office o on o.id = g.office_id ");
        sqlBuilder.append("left join m_staff s on s.id = g.staff_id ");
        sqlBuilder.append("left join m_group pg on pg.id = g.parent_id ");
        sqlBuilder.append("left join m_appuser sbu on sbu.id = g.submittedon_userid ");
        sqlBuilder.append("left join m_appuser acu on acu.id = g.activatedon_userid ");
        sqlBuilder.append("left join m_appuser clu on clu.id = g.closedon_userid ");
        sqlBuilder.append("left join m_appuser ru on ru.id = g.responsible_user_id ");

        this.schemaSql = sqlBuilder.toString();
    }

    public String schema() {
        return this.schemaSql;
    }

    @Override
    public GroupGeneralData mapRow(final ResultSet rs, @SuppressWarnings("unused") final int rowNum) throws SQLException {

        final Long id = rs.getLong("id");
        final String accountNo = rs.getString("accountNumber");
        final String name = rs.getString("name");
        final String externalId = rs.getString("externalId");

        final Integer statusEnum = JdbcSupport.getInteger(rs, "statusEnum");
        final EnumOptionData status = ClientEnumerations.status(statusEnum);
        final LocalDate activationDate = JdbcSupport.getLocalDate(rs, "activationDate");

        final Long officeId = JdbcSupport.getLong(rs, "officeId");
        final String officeName = rs.getString("officeName");
        final Long centerId = JdbcSupport.getLong(rs, "centerId");
        final String centerName = rs.getString("centerName");
        final Long staffId = JdbcSupport.getLong(rs, "staffId");
        final String staffName = rs.getString("staffName");
        final String hierarchy = rs.getString("hierarchy");
        final String groupLevel = rs.getString("groupLevel");

        final LocalDate closedOnDate = JdbcSupport.getLocalDate(rs, "closedOnDate");
        final String closedByUsername = rs.getString("closedByUsername");
        final String closedByFirstname = rs.getString("closedByFirstname");
        final String closedByLastname = rs.getString("closedByLastname");

        final LocalDate submittedOnDate = JdbcSupport.getLocalDate(rs, "submittedOnDate");
        final String submittedByUsername = rs.getString("submittedByUsername");
        final String submittedByFirstname = rs.getString("submittedByFirstname");
        final String submittedByLastname = rs.getString("submittedByLastname");

        final String activatedByUsername = rs.getString("activatedByUsername");
        final String activatedByFirstname = rs.getString("activatedByFirstname");
        final String activatedByLastname = rs.getString("activatedByLastname");
        final String groupLocation = rs.getString("groupLocation");
        final int meetingStart = rs.getInt("meetingStartDate");
        final int meetingEnd = rs.getInt("meetingEndDate");
        final int meetingDay = rs.getInt("meetingDay");
        final String meetingDayValue = rs.getString("meetingDayValue");
        final LocalTime meetingStartTime = JdbcSupport.getLocalTime(rs, "meetingStartTime");
        final LocalTime meetingEndTime = JdbcSupport.getLocalTime(rs, "meetingEndTime");


        final Long legacyGroupNumber = rs.getLong("legacyGroupNumber");
        final Long portfolioCenterId = rs.getLong("portfolioCenterId");
        final BigDecimal latitude = rs.getBigDecimal("latitude");
        final BigDecimal longitude = rs.getBigDecimal("longitude");
        final LocalDate formationDate = JdbcSupport.getLocalDate(rs, "formationDate");
        final long responsibleUserId = rs.getLong("responsibleUserId");
        final Integer size = rs.getInt("size");
        final LocalDate createdDate = JdbcSupport.getLocalDate(rs, "createdDate");

        final GroupTimelineData timeline = new GroupTimelineData(submittedOnDate, submittedByUsername, submittedByFirstname,
                submittedByLastname, activationDate, activatedByUsername, activatedByFirstname, activatedByLastname, closedOnDate,
                closedByUsername, closedByFirstname, closedByLastname);

        GroupGeneralData ret = GroupGeneralData.instance(id, accountNo, name, externalId, status, activationDate, officeId, officeName,
                centerId, centerName, staffId, staffName, hierarchy, groupLevel, timeline);

        // set the remaining fields for group general data
        ret.setLegacyGroupNumber(legacyGroupNumber);
        ret.setPortfolioCenterId(portfolioCenterId);
        ret.setLatitude(latitude);
        ret.setLongitude(longitude);
        ret.setFormationDate(formationDate);
        ret.setResponsibleUserId(responsibleUserId);
        ret.setSize(size);
        ret.setCreatedDate(createdDate);
        ret.setMeetingStartTime(meetingStartTime);
        ret.setMeetingEndTime(meetingEndTime);

        return ret;
    }
}
