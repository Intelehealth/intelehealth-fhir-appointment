package org.ih.appointments.exchange.service.impl;

import org.ih.appointments.exchange.model.IHMarker;
import org.ih.appointments.exchange.model.repo.IHMarkerRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import static org.ih.appointments.exchange.utils.DateUtils.*;

import javax.transaction.Transactional;

@Service
public class IHMarkerService {

	@Autowired
	private IHMarkerRepository ihRepository;

	public IHMarker save(IHMarker ihMarker) {
		return ihRepository.save(ihMarker);
	}

	public IHMarker findByName(String name) {

		IHMarker marker = ihRepository.findByName(name);

		if (marker == null) {
			marker = new IHMarker();
			marker.setName(name);
			marker.setLastSyncTime(toFormattedDateNow("yyyy-MM-dd HH:mm:ss"));
			save(marker);
		}
		return marker;
	}
	
	@Transactional
	public void updateMarkerByName(String name) {
		ihRepository.updateLastSyncTimeByName(name);
	}
}
