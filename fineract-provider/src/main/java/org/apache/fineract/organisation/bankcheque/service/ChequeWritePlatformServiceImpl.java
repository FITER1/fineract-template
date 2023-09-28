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
package org.apache.fineract.organisation.bankcheque.service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.ObjectUtils;
import org.apache.fineract.infrastructure.core.api.JsonCommand;
import org.apache.fineract.infrastructure.core.data.CommandProcessingResult;
import org.apache.fineract.infrastructure.core.data.CommandProcessingResultBuilder;
import org.apache.fineract.infrastructure.core.service.DateUtils;
import org.apache.fineract.infrastructure.security.service.PlatformSecurityContext;
import org.apache.fineract.organisation.agency.domain.Agency;
import org.apache.fineract.organisation.bankAccount.domain.BankAccount;
import org.apache.fineract.organisation.bankAccount.domain.BankAccountRepositoryWrapper;
import org.apache.fineract.organisation.bankcheque.command.CreateChequeCommand;
import org.apache.fineract.organisation.bankcheque.command.ReassignChequeCommand;
import org.apache.fineract.organisation.bankcheque.command.UpdateChequeCommand;
import org.apache.fineract.organisation.bankcheque.command.VoidChequeCommand;
import org.apache.fineract.organisation.bankcheque.domain.BankChequeStatus;
import org.apache.fineract.organisation.bankcheque.domain.Batch;
import org.apache.fineract.organisation.bankcheque.domain.Cheque;
import org.apache.fineract.organisation.bankcheque.domain.ChequeBatchRepositoryWrapper;
import org.apache.fineract.organisation.bankcheque.domain.ChequeJpaRepository;
import org.apache.fineract.organisation.bankcheque.exception.BankChequeException;
import org.apache.fineract.organisation.bankcheque.serialization.CreateChequeCommandFromApiJsonDeserializer;
import org.apache.fineract.organisation.bankcheque.serialization.ReassignChequeCommandFromApiJsonDeserializer;
import org.apache.fineract.organisation.bankcheque.serialization.UpdateChequeCommandFromApiJsonDeserializer;
import org.apache.fineract.organisation.bankcheque.serialization.VoidChequeCommandFromApiJsonDeserializer;
import org.apache.fineract.useradministration.domain.AppUser;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;

@Service
@Slf4j
@RequiredArgsConstructor
public class ChequeWritePlatformServiceImpl implements ChequeWritePlatformService {

    private final PlatformSecurityContext context;
    private final CreateChequeCommandFromApiJsonDeserializer createChequeCommandFromApiJsonDeserializer;
    private final UpdateChequeCommandFromApiJsonDeserializer updateChequeCommandFromApiJsonDeserializer;
    private final BankAccountRepositoryWrapper bankAccountRepositoryWrapper;
    private final JdbcTemplate jdbcTemplate;
    private final ChequeBatchRepositoryWrapper chequeBatchRepositoryWrapper;
    private final ChequeJpaRepository chequeJpaRepository;
    private final ReassignChequeCommandFromApiJsonDeserializer reassignChequeCommandFromApiJsonDeserializer;
    private final VoidChequeCommandFromApiJsonDeserializer voidChequeCommandFromApiJsonDeserializer;

    @Override
    public CommandProcessingResult createBatch(JsonCommand command) {
        final AppUser currentUser = this.context.authenticatedUser();
        CreateChequeCommand createChequeCommand = this.createChequeCommandFromApiJsonDeserializer.commandFromApiJson(command.json());
        final Long bankAccId = createChequeCommand.getBankAccId();
        final Long from = createChequeCommand.getFrom();
        final Long to = createChequeCommand.getTo();
        BankAccount bankAccount = this.bankAccountRepositoryWrapper.findOneWithNotFoundDetection(bankAccId);
        Agency agency = bankAccount.getAgency();
        String maxBatchNoSql = "SELECT IFNULL(MAX(mpb.batch_no), 0) AS maxBatchNo FROM m_payment_batch mpb WHERE mpb.bank_acc_id = ?";
        final Long maxBatchNo = this.jdbcTemplate.queryForObject(maxBatchNoSql, Long.class, new Object[] { bankAccId });
        final Long batchNo = ObjectUtils.defaultIfNull(maxBatchNo, 0L) + 1;
        Batch batch = new Batch().setBatchNo(batchNo).setAgency(agency).setBankAccount(bankAccount)
                .setBankAccNo(bankAccount.getAccountNumber()).setFrom(from).setTo(to).setDescription(createChequeCommand.getDescription());
        final LocalDateTime localDateTime = DateUtils.getLocalDateTimeOfSystem();
        final Long currentUserId = currentUser.getId();
        batch.stampAudit(currentUserId, localDateTime);
        this.validateToAndFromValues(from, to, bankAccId);
        this.chequeBatchRepositoryWrapper.createBatch(batch);
        Set<Cheque> chequeList = new HashSet<>();
        for (Long i = from; i <= to; i++) {
            Cheque cheque = new Cheque().setBatch(batch).setChequeNo(i).setStatus(BankChequeStatus.AVAILABLE.getValue())
                    .setDescription(createChequeCommand.getDescription());
            cheque.stampAudit(currentUserId, localDateTime);
            chequeList.add(cheque);
        }
        batch.setCheques(chequeList);
        this.chequeBatchRepositoryWrapper.updateBatch(batch);
        return new CommandProcessingResultBuilder().withEntityId(batch.getId()).withCommandId(command.commandId())
                .withResourceIdAsString(batch.toString()).build();
    }

    private void validateToAndFromValues(final Long from, final Long to, Long bankAccId) {
        String maxChequeNoSql = """
                SELECT IFNULL(MAX(mbc.check_no), 0) AS maxChequeNo
                FROM m_bank_check mbc
                INNER JOIN m_payment_batch mpb ON mpb.id = mbc.batch_id
                LEFT JOIN m_bank_account mba ON mba.id = mpb.bank_acc_id
                WHERE mba.id = ?
                """;
        final Long maxChequeNo = this.jdbcTemplate.queryForObject(maxChequeNoSql, Long.class, new Object[] { bankAccId });
        Long startValue = ObjectUtils.defaultIfNull(maxChequeNo, 0L) + 1;
        if (!startValue.equals(from)) {
            throw new BankChequeException("from", "from value is not equal to the maximum cheque number.");
        }
        if (from >= to) {
            throw new BankChequeException("to", "to value is less than the from cheque value.");
        }
    }

    @Override
    public CommandProcessingResult updateBatch(Long batchId, JsonCommand command) {
        final AppUser currentUser = this.context.authenticatedUser();
        UpdateChequeCommand updateChequeCommand = updateChequeCommandFromApiJsonDeserializer.commandFromApiJson(command.json());
        final Batch batchForUpdate = this.chequeBatchRepositoryWrapper.findOneBatchWithNotFoundDetection(batchId);
        final Long bankAccId = batchForUpdate.getBankAccount().getId();
        final Long from = updateChequeCommand.getFrom();
        final Long to = updateChequeCommand.getTo();
        this.validateToAndFromValues(from, to, bankAccId);
        final LocalDateTime localDateTime = DateUtils.getLocalDateTimeOfSystem();
        final Long currentUserId = currentUser.getId();
        Map<String, Object> changes = batchForUpdate.update(command);
        if (!changes.isEmpty()) {
            batchForUpdate.stampAudit(currentUserId, localDateTime);
            Set<Cheque> chequeList = new HashSet<>();
            for (Long i = from; i <= to; i++) {
                Cheque cheque = new Cheque().setBatch(batchForUpdate).setChequeNo(i).setStatus(BankChequeStatus.AVAILABLE.getValue())
                        .setDescription(updateChequeCommand.getDescription());
                cheque.stampAudit(currentUserId, localDateTime);
                chequeList.add(cheque);
            }
            batchForUpdate.setCheques(chequeList);
            this.chequeBatchRepositoryWrapper.updateBatch(batchForUpdate);
        }
        return new CommandProcessingResultBuilder().withCommandId(command.commandId()).withResourceIdAsString(batchId.toString())
                .with(changes).withEntityId(batchId).build();
    }

    @Override
    public CommandProcessingResult deleteBatch(Long batchId, JsonCommand command) {
        final Batch batchForUpdate = this.chequeBatchRepositoryWrapper.findOneBatchWithNotFoundDetection(batchId);
        this.chequeBatchRepositoryWrapper.deleteBatch(batchForUpdate);
        return new CommandProcessingResultBuilder().withCommandId(command.commandId()).withResourceIdAsString(batchId.toString())
                .withEntityId(batchId).build();
    }

    @Override
    public CommandProcessingResult reassignCheque(final Long chequeId, JsonCommand command) {
        final AppUser currentUser = this.context.authenticatedUser();
        ReassignChequeCommand reassignChequeCommand = reassignChequeCommandFromApiJsonDeserializer.commandFromApiJson(command.json());
        Cheque newCheque = chequeBatchRepositoryWrapper.findOneChequeWithNotFoundDetection(reassignChequeCommand.getChequeId());
        Cheque oldCheque = chequeBatchRepositoryWrapper.findOneChequeWithNotFoundDetection(reassignChequeCommand.getOldChequeId());
        final String newChequeDescription = "Emitido por sustitución de Desembolso cheque " + oldCheque.getChequeNo();
        final String oldChequeDescription = "Cheque anulado por proceso de Reasignación, cheque nuevo " + newCheque.getChequeNo();
        final LocalDateTime localDateTime = DateUtils.getLocalDateTimeOfSystem();
        LocalDate localDate = DateUtils.getBusinessLocalDate();
        final Long currentUserId = currentUser.getId();
        oldCheque.setStatus(BankChequeStatus.PENDING_VOIDANCE.getValue());
        oldCheque.setDescription(oldChequeDescription);
        oldCheque.stampAudit(currentUserId, localDateTime);
        oldCheque.setVoidedDate(localDate);
        oldCheque.setVoidedBy(currentUser);
        newCheque.setStatus(BankChequeStatus.PENDING_ISSUANCE.getValue());
        newCheque.setDescription(newChequeDescription);
        newCheque.stampAudit(currentUserId, localDateTime);
        newCheque.setPrintedDate(localDate);
        newCheque.setPrintedBy(currentUser);
        chequeJpaRepository.saveAll(List.of(oldCheque, newCheque));
        return new CommandProcessingResultBuilder().withCommandId(command.commandId())
                .withResourceIdAsString(reassignChequeCommand.getOldChequeId().toString())
                .withEntityId(reassignChequeCommand.getOldChequeId()).build();
    }

    @Override
    public CommandProcessingResult authorizedChequeReassignment(final Long chequeId, JsonCommand command) {
        return new CommandProcessingResultBuilder().withCommandId(command.commandId())
                .withResourceIdAsString(String.valueOf(command.entityId())).withEntityId(command.entityId()).build();
    }

    @Override
    public CommandProcessingResult authorizedChequeVoidance(final Long chequeId, JsonCommand command) {
        final AppUser currentUser = this.context.authenticatedUser();
        Cheque cheque = chequeBatchRepositoryWrapper.findOneChequeWithNotFoundDetection(chequeId);
        final LocalDateTime localDateTime = DateUtils.getLocalDateTimeOfSystem();
        LocalDate localDate = DateUtils.getBusinessLocalDate();
        final Long currentUserId = currentUser.getId();
        cheque.stampAudit(currentUserId, localDateTime);
        cheque.setVoidAuthorizedDate(localDate);
        cheque.setVoidAuthorizedBy(currentUser);
        cheque.setStatus(BankChequeStatus.VOIDED.getValue());
        chequeJpaRepository.saveAndFlush(cheque);
        return new CommandProcessingResultBuilder().withCommandId(command.commandId())
                .withResourceIdAsString(String.valueOf(command.entityId())).withEntityId(command.entityId()).build();
    }

    @Override
    public CommandProcessingResult voidCheque(final Long chequeId, JsonCommand command) {
        final AppUser currentUser = this.context.authenticatedUser();
        VoidChequeCommand voidChequeCommand = voidChequeCommandFromApiJsonDeserializer.commandFromApiJson(command.json());
        Cheque cheque = chequeBatchRepositoryWrapper.findOneChequeWithNotFoundDetection(chequeId);
        final LocalDateTime localDateTime = DateUtils.getLocalDateTimeOfSystem();
        LocalDate localDate = DateUtils.getBusinessLocalDate();
        final Long currentUserId = currentUser.getId();
        cheque.stampAudit(currentUserId, localDateTime);
        cheque.setVoidedDate(localDate);
        cheque.setVoidedBy(currentUser);
        cheque.setDescription(voidChequeCommand.getDescription());
        cheque.setStatus(BankChequeStatus.PENDING_VOIDANCE.getValue());
        chequeJpaRepository.saveAndFlush(cheque);
        return new CommandProcessingResultBuilder().withCommandId(command.commandId())
                .withResourceIdAsString(String.valueOf(command.entityId())).withEntityId(command.entityId()).build();
    }
}
