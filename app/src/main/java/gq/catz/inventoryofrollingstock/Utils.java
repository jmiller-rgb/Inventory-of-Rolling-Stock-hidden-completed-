package gq.catz.inventoryofrollingstock;

import android.util.Log;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileDescriptor;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

@SuppressWarnings("unused")
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



	public static List<RollingStockItem> readRollingStockFromCSV(FileDescriptor fd) {
		//if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
			List<RollingStockItem> rollingStockItems = new ArrayList<>();

			// create an instance of BufferedReader
			// using try with resource, Java 7 feature to close resources
			try (FileInputStreamWrapper fisW = new FileInputStreamWrapper(fd)) {
				int lineNum = 1;
				// read the first line from the text file
				String line = fisW.readLine();
				//Log.e("BufferedReaderResult", "" + line);
				// loop until all lines are read
				while (line != null) {
					Log.e("BufferedReaderResult", "" + line);
					// use string.split to load a string array with the values from
					// each line of
					// the file, using a comma as the delimiter
					String[] attributes = line.split(",");
					if (lineNum != 1) {
						RollingStockItem rollingStockItem = createRollingStock(attributes);

						// adding book into ArrayList
						rollingStockItems.add(rollingStockItem);
					}

					// read next line before looping
					// if end of file reached, line would be null
					if (fisW.available() > 0)
						line = fisW.readLine();
					else
						line = null;
					lineNum++;
				}

			} catch (IOException ioe) {
				ioe.printStackTrace();
				return null;
			}

			return rollingStockItems;
//		} else {
//			return null;
//		}
	}

	private static RollingStockItem createRollingStock(String[] metadata) {
		String reportingMark = metadata[0];
		String fleetID = (metadata[1]);
		String stockType = metadata[2];
		String owningCompany = metadata[3];
		String isEngine = metadata[4].toLowerCase();
		String isLoaded = metadata[5].toLowerCase();
		String isRented = metadata[6].toLowerCase();
		String inConsist = metadata[7].toLowerCase();

		/*if (Boolean.parseBoolean(isRented)) {
			owningCompany = "N/A";
		}*/
		if (Boolean.parseBoolean(isEngine)) {
			stockType = "engine";
			isLoaded = "false";
		}

		// create and return book of this metadata
		return new RollingStockItem(reportingMark, fleetID, stockType, owningCompany, isEngine, isLoaded, isRented, inConsist);
	}
}

class FileInputStreamWrapper extends FileInputStream {

	public FileInputStreamWrapper(String name) throws FileNotFoundException {
		super(name);
	}

	public FileInputStreamWrapper(File file) throws FileNotFoundException {
		super(file);
	}

	public FileInputStreamWrapper(FileDescriptor fdObj) {
		super(fdObj);
	}

	public String readLine() throws IOException {
		boolean stop = false;
		StringBuilder stringBuilder = new StringBuilder();
		while (!stop && super.available() > 0) {
			int charData = super.read();
			char charRead = (char) charData;
			if (charRead != '\n' && charRead != '\r') {
				stringBuilder.append(charRead);
			} else {
				if (charRead == '\n') {
					stop = true;
				}
			}
		}
		return stringBuilder.toString();
	}

	@Override
	public void close() throws IOException {
		super.close();
	}

	@Override
	public int available() throws IOException {
		return super.available();
	}
}

