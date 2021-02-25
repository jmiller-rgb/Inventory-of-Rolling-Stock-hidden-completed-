package gq.catz.inventoryofrollingstock;

public enum StockTypes {
	AUTORACK,
	BOXCAR,
	HOPPER,
	CENTER_IBEAM,
	FLATCAR,
	GONDOLA,
	TANK,
	CABOOSE,
	WAYCAR,
	ENGINE;

	public static StockTypes getStockTypeByString(String stockTypeStr) {
		return StockTypes.valueOf(stockTypeStr.toUpperCase());
	}
}
