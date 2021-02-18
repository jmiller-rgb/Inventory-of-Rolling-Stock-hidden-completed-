package gq.catz.inventoryofrollingstock;

public class Utils {
	public static String determineStockType(StockTypes type) {
		if (type == StockTypes.AUTORACK)
			return "Autorack";
		else if (type == StockTypes.BOXCAR)
			return "Boxcar";
		else if (type == StockTypes.CABOOSE)
			return "Caboose";
		else if (type == StockTypes.CENTER_IBEAM)
			return "Center I-Beam";
		else if (type == StockTypes.ENGINE)
			return "Engine";
		else if (type == StockTypes.FLATCAR)
			return "Flatcar";
		else if (type == StockTypes.GONDOLA)
			return "Gondola";
		else if (type == StockTypes.WAYCAR)
			return "Waycar";
		else if (type == StockTypes.TANK)
			return "Tank";
		else if (type == StockTypes.HOPPER)
			return "Hopper";
		else
			return "Unknown";
	}
}
