package gq.catz.inventoryofrollingstock;

import android.util.Log;

import java.io.BufferedReader;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

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



	public static List<RollingStockItem> readRollingStockFromCSV(String fileName) {
		if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
			List<RollingStockItem> rollingStockItems = new ArrayList<>();
			Path pathToFile = Paths.get(fileName);

			// create an instance of BufferedReader
			// using try with resource, Java 7 feature to close resources
			try (BufferedReader br = Files.newBufferedReader(pathToFile,
					StandardCharsets.US_ASCII)) {

				// read the first line from the text file
				String line = br.readLine();
				Log.e("BufferedReaderResult", "" + line);
				// loop until all lines are read
				while (line != null) {

					// use string.split to load a string array with the values from
					// each line of
					// the file, using a comma as the delimiter
					String[] attributes = line.split(",");

					RollingStockItem rollingStockItem = createRollingStock(attributes);

					// adding book into ArrayList
					rollingStockItems.add(rollingStockItem);

					// read next line before looping
					// if end of file reached, line would be null
					line = br.readLine();
				}

			} catch (IOException ioe) {
				ioe.printStackTrace();
			}

			return rollingStockItems;
		} else {
			return null;
		}
	}

	private static RollingStockItem createRollingStock(String[] metadata) {
		String reportingMark = metadata[0];
		String fleetID = (metadata[1]);
		String stockType = metadata[2];
		String owningCompany = metadata[3];
		String isEngine = metadata[4].toLowerCase();
		String isLoaded = metadata[5].toLowerCase();
		String isRented = metadata[6].toLowerCase();

		if (Boolean.parseBoolean(isRented)) {
			owningCompany = "N/A";
		}
		if (Boolean.parseBoolean(isEngine)) {
			stockType = "engine";
		}

		// create and return book of this metadata
		return new RollingStockItem(reportingMark, fleetID, stockType, owningCompany, isEngine, isLoaded, isRented);
	}
}

