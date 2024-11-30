package org.ih.appointments.exchange.service.impl;

import org.ih.appointments.exchange.model.DataExchangeAuditLog;
import org.ih.appointments.exchange.model.repo.DataExchangeAuditLogRepository;
import org.ih.appointments.exchange.service.IDataExchangeAuditLogService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class DataExchangeAuditLogServiceImpl implements IDataExchangeAuditLogService {

	@Autowired
	private DataExchangeAuditLogRepository deAuditLogRepo;

	@Override
	public DataExchangeAuditLog save(DataExchangeAuditLog auditLog) {
		DataExchangeAuditLog deLog = deAuditLogRepo.save(auditLog);
		return deLog;
	}

	@Override
	public DataExchangeAuditLog update(DataExchangeAuditLog auditLog) {
		DataExchangeAuditLog deLog = deAuditLogRepo.save(auditLog);
		return deLog;
	}

}
