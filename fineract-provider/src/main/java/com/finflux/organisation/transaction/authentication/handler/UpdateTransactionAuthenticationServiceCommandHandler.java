package com.finflux.organisation.transaction.authentication.handler;

import org.apache.fineract.commands.annotation.CommandType;
import org.apache.fineract.commands.handler.NewCommandSourceHandler;
import org.apache.fineract.infrastructure.core.api.JsonCommand;
import org.apache.fineract.infrastructure.core.data.CommandProcessingResult;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.finflux.organisation.transaction.authentication.service.TransactionAuthenticationWritePlatformService;

@Service
@CommandType(entity = "TRANSACTIONAUTHENTICATIONSERVICE", action = "UPDATE")
public class UpdateTransactionAuthenticationServiceCommandHandler implements NewCommandSourceHandler {

	private final TransactionAuthenticationWritePlatformService transactionAuthenticationWritePlatformService;

	@Autowired
	public UpdateTransactionAuthenticationServiceCommandHandler(
			final TransactionAuthenticationWritePlatformService transactionAuthenticationWritePlatformService) {
		this.transactionAuthenticationWritePlatformService = transactionAuthenticationWritePlatformService;
	}

	@Override
	public CommandProcessingResult processCommand(JsonCommand command) {
		return this.transactionAuthenticationWritePlatformService.updateTransactionAuthentication(command.entityId(),
				command);
	}

}