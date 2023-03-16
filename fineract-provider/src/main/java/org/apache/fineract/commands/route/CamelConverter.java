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
package org.apache.fineract.commands.route;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.io.IOException;
import java.util.Map;
import org.apache.camel.Converter;
import org.apache.camel.TypeConverters;
import org.apache.fineract.commands.domain.CommandSource;
import org.apache.fineract.commands.domain.CommandWrapper;
import org.apache.fineract.infrastructure.core.data.CommandProcessingResult;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class CamelConverter implements TypeConverters {

    @Autowired
    private ObjectMapper mapper;

    @Converter
    public CommandWrapper toCommandWrapper(final byte[] body) throws IOException {
        return mapper.readValue(body, CommandWrapper.class);

    }

    @Converter
    public byte[] toCommandWrapper(final CommandProcessingResult body) throws JsonProcessingException {
        return mapper.writeValueAsBytes(body);

    }

    @Converter
    public CommandSource toCommandSource(final byte[] body) throws IOException {
        return mapper.readValue(body, CommandSource.class);

    }

    @Converter
    public byte[] toCommandResult(CommandProcessingResult body) throws JsonProcessingException {
        return mapper.writeValueAsBytes(body);
    }

    @Converter
    public byte[] toCommandResult(Map body) throws JsonProcessingException {
        return mapper.writeValueAsBytes(body);
    }

}
