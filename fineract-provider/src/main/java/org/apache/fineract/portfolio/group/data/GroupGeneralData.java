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
package org.apache.fineract.portfolio.group.data;

import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.Collection;
import java.util.List;
import java.util.Objects;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.datatype.jsr310.deser.LocalTimeDeserializer;
import com.fasterxml.jackson.datatype.jsr310.ser.LocalTimeSerializer;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.fineract.infrastructure.codes.data.CodeValueData;
import org.apache.fineract.infrastructure.core.data.EnumOptionData;
import org.apache.fineract.infrastructure.dataqueries.data.DatatableData;
import org.apache.fineract.organisation.office.data.OfficeData;
import org.apache.fineract.organisation.portfolioCenter.data.PortfolioCenterData;
import org.apache.fineract.organisation.staff.data.StaffData;
import org.apache.fineract.portfolio.calendar.data.CalendarData;
import org.apache.fineract.portfolio.client.data.ClientData;
import org.apache.fineract.useradministration.data.AppUserData;

/**
 * Immutable data object representing a general group (so may or may not have a parent).
 */
public class GroupGeneralData implements Serializable {

    private final Long id;
    private final String accountNo;
    private final String name;
    private final String externalId;

    private final EnumOptionData status;
    @SuppressWarnings("unused")
    private final Boolean active;
    private final LocalDate activationDate;

    private final Long officeId;
    private final String officeName;
    private final Long centerId;
    private final String centerName;
    private final Long staffId;
    private final String staffName;
    private final String hierarchy;
    private final String groupLevel;

    // associations
    private final Collection<ClientData> clientMembers;
    private final Collection<ClientData> activeClientMembers;
    private final Collection<GroupRoleData> groupRoles;
    private final Collection<CalendarData> calendarsData;
    private final CalendarData collectionMeetingCalendar;

    // template
    private final Collection<CenterData> centerOptions;
    private final Collection<OfficeData> officeOptions;
    private final Collection<StaffData> staffOptions;
    private final Collection<ClientData> clientOptions;
    private final Collection<CodeValueData> availableRoles;
    private final GroupRoleData selectedRole;
    private final Collection<CodeValueData> closureReasons;
    private final GroupTimelineData timeline;

    private List<DatatableData> datatables = null;

    private final Collection<OfficeData> parentOfficesOptions;
    private final Collection<AppUserData> responsibleUserOptions;
    private final Collection<PortfolioCenterData> portfolioCenterOptions;

    // Additional fields for FB groups
    private Long portfolioCenterId = 0L;
    private Long legacyNumber;
    private BigDecimal latitude;
    private BigDecimal longitude;
    private LocalDate formationDate;
    private Integer size;
    private Long responsibleUserId;
    private LocalDate createdDate;
    private LocalTime meetingStartTime;
    private LocalTime meetingEndTime;

    // Additional fields for FB centers
    private Long portfolioId;
    private CodeValueData city;
    private CodeValueData state;
    private CodeValueData type;
    private Integer distance;
    private Integer meetingStart;
    private Integer meetingEnd;
    private Integer meetingDay;
    private String meetingDayName;
    private String referencePoint;

    private String groupLocation;

    // import fields
    private transient Integer rowIndex;
    private String dateFormat;
    private String locale;
    private LocalDate submittedOnDate;

    public static GroupGeneralData importInstance(String groupName, List<ClientData> clientMembers, LocalDate activationDate,
            LocalDate submittedOnDate, Boolean active, String externalId, Long officeId, Long staffId, Long centerId, Integer rowIndex,
            String locale, String dateFormat) {

        return new GroupGeneralData(groupName, clientMembers, activationDate, submittedOnDate, active, externalId, officeId, staffId,
                centerId, rowIndex, locale, dateFormat);
    }

    private GroupGeneralData(String name, List<ClientData> clientMembers, LocalDate activationDate, LocalDate submittedOnDate,
            Boolean active, String externalId, Long officeId, Long staffId, Long centerId, Integer rowIndex, String locale,
            String dateFormat) {
        this.dateFormat = dateFormat;
        this.locale = locale;
        this.name = name;
        this.clientMembers = clientMembers;
        this.officeId = officeId;
        this.staffId = staffId;
        this.centerId = centerId;
        this.externalId = externalId;
        this.active = active;
        this.activationDate = activationDate;
        this.submittedOnDate = submittedOnDate;
        this.rowIndex = rowIndex;
        this.id = null;
        this.accountNo = null;
        this.status = null;
        this.officeName = null;
        this.centerName = null;
        this.staffName = null;
        this.hierarchy = null;
        this.groupLevel = null;
        this.activeClientMembers = null;
        this.groupRoles = null;
        this.calendarsData = null;
        this.collectionMeetingCalendar = null;
        this.centerOptions = null;
        this.officeOptions = null;
        this.staffOptions = null;
        this.clientOptions = null;
        this.availableRoles = null;
        this.selectedRole = null;
        this.closureReasons = null;
        this.timeline = null;
        this.meetingStartTime = null;
        this.meetingEndTime = null;
        this.meetingStart = null;
        this.meetingEnd = null;
        this.meetingDay = null;
        this.meetingDayName = null;
        this.groupLocation = null;
        this.parentOfficesOptions = null;
        this.responsibleUserOptions = null;
        this.portfolioCenterOptions = null;

    }

    public GroupGeneralData(Long id, Long officeId) {
        this.id = id;
        this.accountNo = null;
        this.name = null;
        this.externalId = null;
        this.status = null;
        this.active = null;
        this.activationDate = null;
        this.officeId = officeId;
        this.officeName = null;
        this.centerId = null;
        this.centerName = null;
        this.staffId = null;
        this.staffName = null;
        this.hierarchy = null;
        this.groupLevel = null;
        this.clientMembers = null;
        this.activeClientMembers = null;
        this.groupRoles = null;
        this.calendarsData = null;
        this.collectionMeetingCalendar = null;
        this.centerOptions = null;
        this.officeOptions = null;
        this.staffOptions = null;
        this.clientOptions = null;
        this.availableRoles = null;
        this.selectedRole = null;
        this.closureReasons = null;
        this.timeline = null;
        this.meetingStartTime = null;
        this.meetingEndTime = null;
        this.meetingStart = null;
        this.meetingEnd = null;
        this.meetingDay = null;
        this.meetingDayName = null;
        this.groupLocation = null;
        this.parentOfficesOptions = null;
        this.responsibleUserOptions = null;
        this.portfolioCenterOptions = null;
    }

    public GroupGeneralData(Long id) {
        this.id = id;
        this.accountNo = null;
        this.name = null;
        this.externalId = null;
        this.status = null;
        this.active = null;
        this.activationDate = null;
        this.officeId = null;
        this.officeName = null;
        this.centerId = null;
        this.centerName = null;
        this.staffId = null;
        this.staffName = null;
        this.hierarchy = null;
        this.groupLevel = null;
        this.clientMembers = null;
        this.activeClientMembers = null;
        this.groupRoles = null;
        this.calendarsData = null;
        this.collectionMeetingCalendar = null;
        this.centerOptions = null;
        this.officeOptions = null;
        this.staffOptions = null;
        this.clientOptions = null;
        this.availableRoles = null;
        this.selectedRole = null;
        this.closureReasons = null;
        this.timeline = null;
        this.meetingStartTime = null;
        this.meetingEndTime = null;
        this.meetingStart = null;
        this.meetingEnd = null;
        this.meetingDay = null;
        this.meetingDayName = null;
        this.groupLocation = null;
        this.parentOfficesOptions = null;
        this.responsibleUserOptions = null;
        this.portfolioCenterOptions = null;
    }

    public Integer getRowIndex() {
        return rowIndex;
    }

    public Long getCenterId() {
        return centerId;
    }

    public LocalDate getActivationDate() {
        return activationDate;
    }

    public String getOfficeName() {
        return officeName;
    }

    public static GroupGeneralData lookup(final Long groupId, final String accountNo, final String groupName) {
        final Collection<ClientData> clientMembers = null;
        final Collection<GroupRoleData> groupRoles = null;
        final Collection<CodeValueData> closureReasons = null;
        return new GroupGeneralData(groupId, accountNo, groupName, null, null, null, null, null, null, null, null, null, null, null,
                clientMembers, null, null, null, null, null, groupRoles, null, null, null, null, closureReasons, null, null, null, null);
    }

    public static GroupGeneralData template(final Long officeId, final Long centerId, final String accountNo, final String centerName,
                                            final Long staffId, final String staffName, final Collection<CenterData> centerOptions,
                                            final Collection<OfficeData> officeOptions, final Collection<StaffData> staffOptions,
                                            final Collection<ClientData> clientOptions, final Collection<CodeValueData> availableRoles,
                                            final Collection<OfficeData> parentOfficesOptions, final Collection<AppUserData> responsibleUserOptions,
                                            final Collection<PortfolioCenterData> portfolioCenterOptions) {

        final Collection<ClientData> clientMembers = null;
        final Collection<GroupRoleData> groupRoles = null;
        final Collection<CodeValueData> closureReasons = null;

        return new GroupGeneralData(null, accountNo, null, null, null, null, officeId, null, centerId, centerName, staffId, staffName, null,
                null, clientMembers, null, centerOptions, officeOptions, staffOptions, clientOptions, groupRoles, availableRoles, null,
                null, null, closureReasons, null, parentOfficesOptions, responsibleUserOptions, portfolioCenterOptions);
    }

    public static GroupGeneralData withTemplate(final GroupGeneralData templatedGrouping, final GroupGeneralData grouping) {
        GroupGeneralData ret = new GroupGeneralData(grouping.id, grouping.accountNo, grouping.name, grouping.externalId, grouping.status,
                grouping.activationDate, grouping.officeId, grouping.officeName, grouping.centerId, grouping.centerName, grouping.staffId,
                grouping.staffName, grouping.hierarchy, grouping.groupLevel, grouping.clientMembers, grouping.activeClientMembers,
                templatedGrouping.centerOptions, templatedGrouping.officeOptions, templatedGrouping.staffOptions,
                templatedGrouping.clientOptions, grouping.groupRoles, templatedGrouping.availableRoles, grouping.selectedRole,
                grouping.calendarsData, grouping.collectionMeetingCalendar, grouping.closureReasons, templatedGrouping.timeline,
                templatedGrouping.parentOfficesOptions, templatedGrouping.responsibleUserOptions, templatedGrouping.portfolioCenterOptions);

        return mapDTO(grouping, ret);
    }

    private static GroupGeneralData mapDTO(GroupGeneralData grouping, GroupGeneralData ret) {
        ret.setLegacyNumber(grouping.legacyNumber);
        ret.setPortfolioCenterId(grouping.portfolioCenterId);
        ret.setLatitude(grouping.latitude);
        ret.setLongitude(grouping.longitude);
        ret.setFormationDate(grouping.formationDate);
        ret.setResponsibleUserId(grouping.responsibleUserId);
        ret.setSize(grouping.size);
        ret.setCreatedDate(grouping.createdDate);
        ret.setMeetingStartTime(grouping.meetingStartTime);
        ret.setMeetingEndTime(grouping.meetingEndTime);

        ret.setPortfolioId(grouping.portfolioId);
        ret.setCity(grouping.city);
        ret.setState(grouping.state);
        ret.setType(grouping.type);
        ret.setDistance(grouping.distance);
        ret.setMeetingStart(grouping.meetingStart);
        ret.setMeetingEnd(grouping.meetingEnd);
        ret.setMeetingDay(grouping.meetingDay);
        ret.setMeetingDayName(grouping.meetingDayName);
        ret.setReferencePoint(grouping.referencePoint);

        return ret;
    }

    public static GroupGeneralData withAssocations(final GroupGeneralData grouping, final Collection<ClientData> membersOfGroup,
                                                   final Collection<ClientData> activeClientMembers, final Collection<GroupRoleData> groupRoles,
                                                   final Collection<CalendarData> calendarsData, final CalendarData collectionMeetingCalendar) {
        GroupGeneralData ret = new GroupGeneralData(grouping.id, grouping.accountNo, grouping.name, grouping.externalId, grouping.status,
                grouping.activationDate, grouping.officeId, grouping.officeName, grouping.centerId, grouping.centerName, grouping.staffId,
                grouping.staffName, grouping.hierarchy, grouping.groupLevel, membersOfGroup, activeClientMembers, grouping.centerOptions,
                grouping.officeOptions, grouping.staffOptions, grouping.clientOptions, groupRoles, grouping.availableRoles,
                grouping.selectedRole, calendarsData, collectionMeetingCalendar, grouping.closureReasons, grouping.timeline,
                grouping.parentOfficesOptions, grouping.responsibleUserOptions, grouping.portfolioCenterOptions);

        return mapDTO(grouping, ret);
    }

    public static GroupGeneralData instance(final Long id, final String accountNo, final String name, final String externalId,
                                            final EnumOptionData status, final LocalDate activationDate, final Long officeId, final String officeName, final Long centerId,
                                            final String centerName, final Long staffId, final String staffName, final String hierarchy, final String groupLevel,
                                            final GroupTimelineData timeline) {

        final Collection<ClientData> clientMembers = null;
        final Collection<ClientData> activeClientMembers = null;
        final Collection<CenterData> centerOptions = null;
        final Collection<OfficeData> officeOptions = null;
        final Collection<StaffData> staffOptions = null;
        final Collection<ClientData> clientOptions = null;
        final Collection<GroupRoleData> groupRoles = null;
        final Collection<CodeValueData> availableRoles = null;
        final GroupRoleData role = null;
        final Collection<CalendarData> calendarsData = null;
        final CalendarData collectionMeetingCalendar = null;
        final Collection<CodeValueData> closureReasons = null;
        final Collection<OfficeData> parentOfficesOptions = null;
        final Collection<AppUserData> responsibleUserOptions = null;
        final Collection<PortfolioCenterData> portfolioCenterOptions = null;

        return new GroupGeneralData(id, accountNo, name, externalId, status, activationDate, officeId, officeName, centerId, centerName,
                staffId, staffName, hierarchy, groupLevel, clientMembers, activeClientMembers, centerOptions, officeOptions, staffOptions,
                clientOptions, groupRoles, availableRoles, role, calendarsData, collectionMeetingCalendar, closureReasons, timeline,
                parentOfficesOptions, responsibleUserOptions, portfolioCenterOptions);
    }

    private GroupGeneralData(final Long id, final String accountNo, final String name, final String externalId, final EnumOptionData status,
                             final LocalDate activationDate, final Long officeId, final String officeName, final Long centerId, final String centerName,
                             final Long staffId, final String staffName, final String hierarchy, final String groupLevel,
                             final Collection<ClientData> clientMembers, final Collection<ClientData> activeClientMembers,
                             final Collection<CenterData> centerOptions, final Collection<OfficeData> officeOptions,
                             final Collection<StaffData> staffOptions, final Collection<ClientData> clientOptions,
                             final Collection<GroupRoleData> groupRoles, final Collection<CodeValueData> availableRoles, final GroupRoleData role,
                             final Collection<CalendarData> calendarsData, final CalendarData collectionMeetingCalendar,
                             final Collection<CodeValueData> closureReasons, final GroupTimelineData timeline,
                             final Collection<OfficeData> parentOfficesOptions, final Collection<AppUserData> responsibleUserOptions,
                             final Collection<PortfolioCenterData> portfolioCenterOptions) {
        this.id = id;
        this.accountNo = accountNo;
        this.name = name;
        this.externalId = externalId;
        this.status = status;
        if (status != null) {
            this.active = status.getId().equals(300L);
        } else {
            this.active = null;
        }
        this.activationDate = activationDate;

        this.officeId = officeId;
        this.officeName = officeName;
        this.centerId = centerId;
        this.centerName = centerName;
        this.staffId = staffId;
        this.staffName = staffName;
        this.hierarchy = hierarchy;
        this.groupLevel = groupLevel;

        // associations
        this.clientMembers = clientMembers;
        this.activeClientMembers = activeClientMembers;

        // template
        this.centerOptions = centerOptions;
        this.officeOptions = officeOptions;
        this.staffOptions = staffOptions;

        if (clientMembers != null && clientOptions != null) {
            clientOptions.removeAll(clientMembers);
        }
        this.clientOptions = clientOptions;
        this.groupRoles = groupRoles;
        this.availableRoles = availableRoles;
        this.selectedRole = role;
        this.calendarsData = calendarsData;
        this.collectionMeetingCalendar = collectionMeetingCalendar;
        this.closureReasons = closureReasons;
        this.timeline = timeline;
        this.parentOfficesOptions = parentOfficesOptions;
        this.responsibleUserOptions = responsibleUserOptions;
        this.portfolioCenterOptions = portfolioCenterOptions;
    }

    public Long getId() {
        return this.id;
    }

    public String getAccountNo() {
        return this.accountNo;
    }

    public String getName() {
        return this.name;
    }

    public Long officeId() {
        return this.officeId;
    }

    public String getHierarchy() {
        return this.hierarchy;
    }

    public boolean isChildGroup() {
        return this.centerId == null ? false : true;
    }

    public Long getParentId() {
        return this.centerId;
    }

    public static GroupGeneralData updateSelectedRole(final GroupGeneralData grouping, final GroupRoleData selectedRole) {
        return new GroupGeneralData(grouping.id, grouping.accountNo, grouping.name, grouping.externalId, grouping.status,
                grouping.activationDate, grouping.officeId, grouping.officeName, grouping.centerId, grouping.centerName, grouping.staffId,
                grouping.staffName, grouping.hierarchy, grouping.groupLevel, grouping.clientMembers, grouping.activeClientMembers,
                grouping.centerOptions, grouping.officeOptions, grouping.staffOptions, grouping.clientOptions, grouping.groupRoles,
                grouping.availableRoles, selectedRole, grouping.calendarsData, grouping.collectionMeetingCalendar, grouping.closureReasons,
                null, null, null, null);
    }

    public static GroupGeneralData withClosureReasons(final Collection<CodeValueData> closureReasons) {
        final Long id = null;
        final String accountNo = null;
        final String name = null;
        final String externalId = null;
        final EnumOptionData status = null;
        final LocalDate activationDate = null;
        final Long officeId = null;
        final String officeName = null;
        final Long centerId = null;
        final String centerName = null;
        final Long staffId = null;
        final String staffName = null;
        final String hierarchy = null;
        final String groupLevel = null;
        final Collection<ClientData> clientMembers = null;
        final Collection<ClientData> activeClientMembers = null;
        final Collection<CenterData> centerOptions = null;
        final Collection<OfficeData> officeOptions = null;
        final Collection<StaffData> staffOptions = null;
        final Collection<ClientData> clientOptions = null;
        final Collection<GroupRoleData> groupRoles = null;
        final Collection<CodeValueData> availableRoles = null;
        final GroupRoleData role = null;
        final Collection<CalendarData> calendarsData = null;
        final CalendarData collectionMeetingCalendar = null;
        final Collection<OfficeData> parentOfficesOptions = null;
        final Collection<AppUserData> responsibleUserOptions = null;
        final Collection<PortfolioCenterData> portfolioCenterOptions = null;

        return new GroupGeneralData(id, accountNo, name, externalId, status, activationDate, officeId, officeName, centerId, centerName,
                staffId, staffName, hierarchy, groupLevel, clientMembers, activeClientMembers, centerOptions, officeOptions, staffOptions,
                clientOptions, groupRoles, availableRoles, role, calendarsData, collectionMeetingCalendar, closureReasons, null,
                parentOfficesOptions, responsibleUserOptions, portfolioCenterOptions);
    }

    public Collection<ClientData> clientMembers() {
        return this.clientMembers;
    }

    public void setDatatables(final List<DatatableData> datatables) {
        this.datatables = datatables;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof GroupGeneralData)) {
            return false;
        }
        GroupGeneralData that = (GroupGeneralData) o;
        return Objects.equals(id, that.id) && Objects.equals(accountNo, that.accountNo) && Objects.equals(name, that.name)
                && Objects.equals(externalId, that.externalId) && Objects.equals(status, that.status) && Objects.equals(active, that.active)
                && Objects.equals(activationDate, that.activationDate) && Objects.equals(officeId, that.officeId)
                && Objects.equals(officeName, that.officeName) && Objects.equals(centerId, that.centerId)
                && Objects.equals(centerName, that.centerName) && Objects.equals(staffId, that.staffId)
                && Objects.equals(staffName, that.staffName) && Objects.equals(hierarchy, that.hierarchy)
                && Objects.equals(groupLevel, that.groupLevel) && CollectionUtils.isEqualCollection(clientMembers, that.clientMembers)
                && CollectionUtils.isEqualCollection(activeClientMembers, that.activeClientMembers)
                && CollectionUtils.isEqualCollection(groupRoles, that.groupRoles)
                && CollectionUtils.isEqualCollection(calendarsData, that.calendarsData)
                && Objects.equals(collectionMeetingCalendar, that.collectionMeetingCalendar)
                && CollectionUtils.isEqualCollection(centerOptions, that.centerOptions)
                && CollectionUtils.isEqualCollection(officeOptions, that.officeOptions)
                && CollectionUtils.isEqualCollection(staffOptions, that.staffOptions)
                && CollectionUtils.isEqualCollection(clientOptions, that.clientOptions)
                && CollectionUtils.isEqualCollection(availableRoles, that.availableRoles) && Objects.equals(selectedRole, that.selectedRole)
                && CollectionUtils.isEqualCollection(closureReasons, that.closureReasons) && Objects.equals(timeline, that.timeline)
                && Objects.equals(datatables, that.datatables) && Objects.equals(rowIndex, that.rowIndex)
                && Objects.equals(dateFormat, that.dateFormat) && Objects.equals(locale, that.locale)
                && Objects.equals(submittedOnDate, that.submittedOnDate);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, accountNo, name, externalId, status, active, activationDate, officeId, officeName, centerId, centerName,
                staffId, staffName, hierarchy, groupLevel, clientMembers, activeClientMembers, groupRoles, calendarsData,
                collectionMeetingCalendar, centerOptions, officeOptions, staffOptions, clientOptions, availableRoles, selectedRole,
                closureReasons, timeline, datatables, rowIndex, dateFormat, locale, submittedOnDate);
    }

    public void setPortfolioCenterId(Long portfolioCenterId) {
        this.portfolioCenterId = portfolioCenterId;
    }

    public void setLegacyNumber(Long legacyNumber) {
        this.legacyNumber = legacyNumber;
    }

    public void setLatitude(BigDecimal latitude) {
        this.latitude = latitude;
    }

    public void setLongitude(BigDecimal longitude) {
        this.longitude = longitude;
    }

    public void setFormationDate(LocalDate formationDate) {
        this.formationDate = formationDate;
    }

    public void setSize(Integer size) {
        this.size = size;
    }

    public void setResponsibleUserId(Long responsibleUserId) {
        this.responsibleUserId = responsibleUserId;
    }

    public void setCreatedDate(LocalDate createdDate) {
        this.createdDate = createdDate;
    }

    public void setMeetingStartTime(LocalTime meetingStartTime) {
        this.meetingStartTime = meetingStartTime;
    }

    public void setMeetingEndTime(LocalTime meetingEndTime) {
        this.meetingEndTime = meetingEndTime;
    }

    public void setPortfolioId(Long portfolioId) {
        this.portfolioId = portfolioId;
    }

    public void setCity(CodeValueData city) {
        this.city = city;
    }

    public void setState(CodeValueData state) {
        this.state = state;
    }

    public void setType(CodeValueData type) {
        this.type = type;
    }

    public void setDistance(Integer distance) {
        this.distance = distance;
    }

    public void setMeetingStart(Integer meetingStart) {
        this.meetingStart = meetingStart;
    }

    public void setMeetingEnd(Integer meetingEnd) {
        this.meetingEnd = meetingEnd;
    }

    public void setMeetingDay(Integer meetingDay) {
        this.meetingDay = meetingDay;
    }

    public void setMeetingDayName(String meetingDayName) {
        this.meetingDayName = meetingDayName;
    }

    public void setReferencePoint(String referencePoint) {
        this.referencePoint = referencePoint;
    }
}