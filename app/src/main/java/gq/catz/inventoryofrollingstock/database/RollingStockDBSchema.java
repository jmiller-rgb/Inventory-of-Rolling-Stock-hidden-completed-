package gq.catz.inventoryofrollingstock.database;

public class RollingStockDBSchema {
	public static final class RollingStockTable {
		public static final String NAME = "RollingStocks"; // Name of the database table

		public static final class Cols {
			/*
				private String callMarks; // ----\
										//		 |--- title
				private int idNum; // -----------/
				private String stockType, owningCompany;
				private boolean isEngine, isLoaded, isRented;
			 */
			public static final String UUID = "uuid"; // Unique ID, Cannot occur twice.
			public static final String REPORTINGMARK = "reportingMark"; // The 3-4 Letter mark on railcars
			public static final String FLEETID = "fleetID"; // The 6 digit number on railcars
			public static final String STOCK_TYPE = "stockType"; // The type of railcar.
			public static final String OWNING_COMPANY = "owningCompany"; // The company that owns the railcar, if not rented.
			public static final String IS_ENGINE = "isEngine";
			public static final String IS_LOADED = "isLoaded";
			public static final String IS_RENTED = "isRented";
		}
	}
}
