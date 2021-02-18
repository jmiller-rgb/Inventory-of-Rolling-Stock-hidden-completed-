package gq.catz.inventoryofrollingstock;

import java.util.UUID;

public class RollingStockItem {
	private String reportingMark, stockType, owningCompany;
	private String fleetID;
	private String isEngine, isLoaded, isRented;
	private UUID id;

	public RollingStockItem(String reportingMark, int fleetID, boolean isEngine, String stockType, boolean isLoaded, String owningCompany, boolean isRented) {
		this.reportingMark = reportingMark;
		this.fleetID = Integer.toString(fleetID);
		this.isEngine = Boolean.toString(isEngine);
		this.stockType = stockType;
		this.isLoaded = Boolean.toString(isLoaded);
		this.owningCompany = owningCompany;
		this.isRented = Boolean.toString(isRented);
		this.id = UUID.randomUUID();
	}

	public RollingStockItem(UUID uuid, String reportingMark, int fleetID, String stockType, String owningCompany, boolean isEngine, boolean isLoaded, boolean isRented) {
		this.reportingMark = reportingMark;
		this.fleetID = Integer.toString(fleetID);
		this.isEngine = Boolean.toString(isEngine);
		this.stockType = stockType;
		this.isLoaded = Boolean.toString(isLoaded);
		this.owningCompany = owningCompany;
		this.isRented = Boolean.toString(isRented);
		this.id = uuid;
	}

	public RollingStockItem(String reportingMark, String fleetID, String stockType, String owningCompany, String isEngine, String isLoaded, String isRented) {
		this.reportingMark = reportingMark;
		this.fleetID = fleetID;
		this.isEngine = isEngine;
		this.stockType = stockType;
		this.isLoaded = isLoaded;
		this.owningCompany = owningCompany;
		this.isRented = isRented;
		this.id = UUID.randomUUID();
	}

	public RollingStockItem(UUID uuid, String reportingMark, String fleetID, String stockType, String owningCompany, String isEngine, String isLoaded, String isRented) {
		this.reportingMark = reportingMark;
		this.fleetID = fleetID;
		this.isEngine = isEngine;
		this.stockType = stockType;
		this.isLoaded = isLoaded;
		this.owningCompany = owningCompany;
		this.isRented = isRented;
		this.id = uuid;
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
		return new Integer(fleetID);
	}

	public void setFleetID(String fleetID) {
		this.fleetID = fleetID;
	}

	public boolean isEngine() {
		return new Boolean(isEngine);
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
		return new Boolean(isRented);
	}

	public void setRented(boolean rented) {
		isRented = Boolean.toString(rented);
	}

	public UUID getId() {
		return id;
	}
}
