package org.ih.appointments.exchange.dto;

public class ReferralDTO {

	private String practitioner;

	private String practitionerName;

	private String encounterId;

	private String patientName;

	private String patientId;

	private String reason;

	private String uuid;

	private String created;

	public String getPractitioner() {
		return practitioner;
	}

	public void setPractitioner(String practitioner) {
		this.practitioner = practitioner;
	}

	public String getPractitionerName() {
		return practitionerName;
	}

	public void setPractitionerName(String practitionerName) {
		this.practitionerName = practitionerName;
	}

	public String getEncounterId() {
		return encounterId;
	}

	public void setEncounterId(String encounterId) {
		this.encounterId = encounterId;
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

	public String getReason() {
		return reason;
	}

	public void setReason(String reason) {
		this.reason = reason;
	}

	public String getUuid() {
		return uuid;
	}

	public void setUuid(String uuid) {
		this.uuid = uuid;
	}

	public String getCreated() {
		return created;
	}

	public void setCreated(String created) {
		this.created = created;
	}

	@Override
	public String toString() {
		return "ReferralDTO [practitioner=" + practitioner + ", practitionerName=" + practitionerName + ", encounterId="
				+ encounterId + ", patientName=" + patientName + ", patientId=" + patientId + ", reason=" + reason
				+ ", uuid=" + uuid + ", created=" + created + "]";
	}

}
