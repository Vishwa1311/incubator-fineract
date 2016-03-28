package org.apache.fineract.portfolio.globaltransaction.handler;

import org.apache.fineract.commands.handler.NewCommandSourceHandler;
import org.apache.fineract.infrastructure.core.api.JsonCommand;
import org.apache.fineract.infrastructure.core.data.CommandProcessingResult;
import org.apache.fineract.portfolio.globaltransaction.service.GlobalTransactionReferenceWritePlatformService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class CreateGlobalTransactionReferenceCommandHandler implements NewCommandSourceHandler {

    private final GlobalTransactionReferenceWritePlatformService writePlatformService;

    @Autowired
    public CreateGlobalTransactionReferenceCommandHandler(final GlobalTransactionReferenceWritePlatformService writePlatformService) {
        this.writePlatformService = writePlatformService;
    }

    @Transactional
    @Override
    public CommandProcessingResult processCommand(final JsonCommand command) {
        return this.writePlatformService.create(command);
    }
}