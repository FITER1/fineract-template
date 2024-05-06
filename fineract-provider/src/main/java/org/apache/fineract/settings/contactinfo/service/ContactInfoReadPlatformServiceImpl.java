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
package org.apache.fineract.settings.contactinfo.service;

import lombok.RequiredArgsConstructor;
import org.apache.fineract.infrastructure.core.domain.JdbcSupport;
import org.apache.fineract.settings.contactinfo.data.ContactInfoData;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Service;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Collection;

@Service
@RequiredArgsConstructor
public class ContactInfoReadPlatformServiceImpl implements ContactInfoReadPlatformService {

    private final JdbcTemplate jdbcTemplate;

    @Override
    public Collection<ContactInfoData> retrieveContactInfo() {

            final ContactInfoMapper rm = new ContactInfoMapper();
            final String sql = rm.schema();

            return this.jdbcTemplate.query(sql, rm); // NOSONAR
    }

    private static final class ContactInfoMapper implements RowMapper<ContactInfoData> {

        public String schema() {
            return " select ci.id as id, ci.email as email, ci.mobile_no as mobileNo, ci.website as website"
                    + " from m_contact_info ci";
        }

        @Override
        public ContactInfoData mapRow(final ResultSet rs, @SuppressWarnings("unused") final int rowNum) throws SQLException {

            final Long id = JdbcSupport.getLong(rs, "id");
            final String email = rs.getString("email");
            final String mobileNo = rs.getString("mobileNo");
            final String website = rs.getString("website");

            return new ContactInfoData(id, email, mobileNo, website);

        }
    }

}
