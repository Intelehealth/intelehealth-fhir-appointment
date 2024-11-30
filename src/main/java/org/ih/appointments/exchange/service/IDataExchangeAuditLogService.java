package org.ih.appointments.exchange.service;

import org.ih.appointments.exchange.model.DataExchangeAuditLog;

public interface IDataExchangeAuditLogService {

	public DataExchangeAuditLog save(DataExchangeAuditLog auditLog);

	public DataExchangeAuditLog update(DataExchangeAuditLog auditLog);
}
