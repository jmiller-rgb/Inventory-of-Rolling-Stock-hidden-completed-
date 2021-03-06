package gq.catz.inventoryofrollingstock;

import java.util.UUID;

public class RollingStockItem {
	private String reportingMark, stockType, owningCompany;
	private String fleetID;
	private String isEngine, isLoaded, isRented, inConsist;
	private UUID id;
	private boolean checked;

	public RollingStockItem(String reportingMark, int fleetID, boolean isEngine, String stockType, boolean isLoaded, String owningCompany, boolean isRented, boolean inConsist) {
		this.reportingMark = reportingMark;
		this.fleetID = Integer.toString(fleetID);
		this.isEngine = Boolean.toString(isEngine);
		this.stockType = stockType;
		this.isLoaded = Boolean.toString(isLoaded);
		this.owningCompany = owningCompany;
		this.isRented = Boolean.toString(isRented);
		this.inConsist = Boolean.toString((inConsist));
		this.id = UUID.randomUUID();
		this.checked = false;
	}

	public RollingStockItem(UUID uuid, String reportingMark, int fleetID, String stockType, String owningCompany, boolean isEngine, boolean isLoaded, boolean isRented, boolean inConsist) {
		this.reportingMark = reportingMark;
		this.fleetID = Integer.toString(fleetID);
		this.isEngine = Boolean.toString(isEngine);
		this.stockType = stockType;
		this.isLoaded = Boolean.toString(isLoaded);
		this.owningCompany = owningCompany;
		this.isRented = Boolean.toString(isRented);
		this.inConsist = Boolean.toString(inConsist);
		this.id = uuid;
		this.checked = false;
	}

	public RollingStockItem(String reportingMark, String fleetID, String stockType, String owningCompany, String isEngine, String isLoaded, String isRented, String inConsist) {
		this.reportingMark = reportingMark;
		this.fleetID = fleetID;
		this.isEngine = isEngine;
		this.stockType = stockType;
		this.isLoaded = isLoaded;
		this.owningCompany = owningCompany;
		this.isRented = isRented;
		this.inConsist = inConsist;
		this.id = UUID.randomUUID();
		this.checked = false;
	}

	public RollingStockItem(UUID uuid, String reportingMark, String fleetID, String stockType, String owningCompany, String isEngine, String isLoaded, String isRented, String inConsist) {
		this.reportingMark = reportingMark;
		this.fleetID = fleetID;
		this.isEngine = isEngine;
		this.stockType = stockType;
		this.isLoaded = isLoaded;
		this.owningCompany = owningCompany;
		this.isRented = isRented;
		this.inConsist = inConsist;
		this.id = uuid;
		this.checked = false;
	}

	public String getReportingMark() {
		return reportingMark;
	}

	public void setReportingMark(String reportingMark) {
		this.reportingMark = reportingMark;
	}

	public String getStockType() {
		return stockType;
	}

	public void setStockType(String stockType) {
		this.stockType = stockType;
	}

	public int getFleetID() {
		return Integer.parseInt(fleetID);
	}

	public void setFleetID(String fleetID) {
		this.fleetID = fleetID;
	}

	public boolean isEngine() {
		return Boolean.parseBoolean(isEngine);
	}

	public void setEngine(boolean engine) {
		isEngine = Boolean.toString(engine);
	}

	public boolean isLoaded() {
		return Boolean.parseBoolean(isLoaded);
	}

	public void setLoaded(boolean loaded) {
		isLoaded = Boolean.toString(loaded);
	}

	public String getOwningCompany() {
		return owningCompany;
	}

	public void setOwningCompany(String owningCompany) {
		this.owningCompany = owningCompany;
	}

	public boolean isRented() {
		return Boolean.parseBoolean(isRented);
	}

	public void setRented(boolean rented) {
		isRented = Boolean.toString(rented);
	}

	public boolean isInConsist() {
		return Boolean.parseBoolean(inConsist);
	}

	public void setInConsist(boolean inConsistBool) {
		inConsist = Boolean.toString(inConsistBool);
	}

	public UUID getId() {
		return id;
	}

	public boolean isChecked() {
		return checked;
	}

	public void setChecked(boolean checked) {
		this.checked = checked;
	}
}
