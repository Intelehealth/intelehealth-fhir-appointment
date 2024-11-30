package org.ih.appointments.exchange.dto;

public class RequestAppointmentDTO {

	private String serviceCategory;
	private String serviceType;
	private String specialty;
	private String slot;
	private String location;
	private String locationName;
	private String practitioner;
	private String practitionerName;
	private int duration;
	private String patientName;
	private String patientId;
	private String requestId;

	public String getServiceCategory() {
		return serviceCategory;
	}

	public void setServiceCategory(String serviceCategory) {
		this.serviceCategory = serviceCategory;
	}

	public String getServiceType() {
		return serviceType;
	}

	public void setServiceType(String serviceType) {
		this.serviceType = serviceType;
	}

	public String getSpecialty() {
		return specialty;
	}

	public void setSpecialty(String specialty) {
		this.specialty = specialty;
	}

	public String getSlot() {
		return slot;
	}

	public void setSlot(String slot) {
		this.slot = slot;
	}

	public String getLocation() {
		return location;
	}

	public void setLocation(String location) {
		this.location = location;
	}

	public String getPractitioner() {
		return practitioner;
	}

	public void setPractitioner(String practitioner) {
		this.practitioner = practitioner;
	}

	public int getDuration() {
		return duration;
	}

	public void setDuration(int duration) {
		this.duration = duration;
	}

	public String getPatientName() {
		return patientName;
	}

	public void setPatientName(String patientName) {
		this.patientName = patientName;
	}

	public String getPatientId() {
		return patientId;
	}

	public void setPatientId(String patientId) {
		this.patientId = patientId;
	}

	public String getLocationName() {
		return locationName;
	}

	public void setLocationName(String locationName) {
		this.locationName = locationName;
	}

	public String getPractitionerName() {
		return practitionerName;
	}

	public void setPractitionerName(String practitionerName) {
		this.practitionerName = practitionerName;
	}

	public String getRequestId() {
		return requestId;
	}

	public void setRequestId(String requestId) {
		this.requestId = requestId;
	}

	@Override
	public String toString() {
		return "RequestAppointmentDTO [serviceCategory=" + serviceCategory + ", serviceType=" + serviceType
				+ ", specialty=" + specialty + ", slot=" + slot + ", location=" + location + ", locationName="
				+ locationName + ", practitioner=" + practitioner + ", practitionerName=" + practitionerName
				+ ", duration=" + duration + ", patientName=" + patientName + ", patientId=" + patientId
				+ ", requestId=" + requestId + "]";
	}

}
