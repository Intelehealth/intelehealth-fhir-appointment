package org.ih.appointments.exchange.model.repo;

import org.ih.appointments.exchange.model.DataExchangeAuditLog;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface DataExchangeAuditLogRepository extends JpaRepository<DataExchangeAuditLog, Integer> {

}
