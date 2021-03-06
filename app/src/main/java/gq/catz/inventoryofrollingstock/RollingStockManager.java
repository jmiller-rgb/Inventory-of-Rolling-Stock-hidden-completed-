package gq.catz.inventoryofrollingstock;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import java.io.FileDescriptor;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.UUID;

import gq.catz.inventoryofrollingstock.database.RollingStockBaseHelper;
import gq.catz.inventoryofrollingstock.database.RollingStockCursorWrapper;
import gq.catz.inventoryofrollingstock.database.RollingStockDBSchema.RollingStockTable;

public class RollingStockManager {
	private Context context;
	private SQLiteDatabase rollingStockDatabase;

	public static RollingStockManager get(Context context) {
		return new RollingStockManager(context);
	}

	private RollingStockManager(Context context) {
		this.context = context.getApplicationContext();
		rollingStockDatabase = new RollingStockBaseHelper(this.context).getWritableDatabase();
	}

	public List<RollingStockItem> getRollingStocks() {
		List<RollingStockItem> rollingStockItemList = new ArrayList<>();

		RollingStockCursorWrapper cursor = queryRollingStock(null, null);
		try {
			cursor.moveToFirst();
			while (!cursor.isAfterLast()) {
				rollingStockItemList.add(cursor.getRollingStock());
				cursor.moveToNext();
			}
		} finally {
			cursor.close();
		}

		return rollingStockItemList;
	}

	private List<RollingStockItem> generateRollingStocks(int amount) {
		ArrayList<RollingStockItem> tmp = new ArrayList<>();
		for (int i = 0; i < amount; i++) {
			Random r = new Random();
			StringBuilder callLetters = new StringBuilder();
			int idNum = r.nextInt(1000000);
			boolean isEngine = r.nextBoolean();
			String stockType;
			boolean isLoad = false;
			boolean isRented = r.nextBoolean();
			boolean inConsist = r.nextBoolean();
			StringBuilder owningCompany = new StringBuilder();

			for (int j = 0; j < 4; j++) {
				char c = (char) (r.nextInt(26) + 'a');
				callLetters.append(c);
			}
			if (isEngine) {
				stockType = "engine";
			} else {
				int pos = r.nextInt(StockTypes.values().length - 1);
				stockType = StockTypes.values()[pos].toString();
//				StockTypes type = StockTypes.
				isLoad = r.nextBoolean();
			}
			for (int j = 0; j < 3; j++) {
				char c = (char) (r.nextInt(26) + 'a');
				owningCompany.append(c);
			}
			tmp.add(new RollingStockItem(callLetters.toString(), idNum, isEngine, stockType, isLoad, owningCompany.toString(), isRented, inConsist));
		}
		return tmp;
	}

	public RollingStockItem getRollingStock(UUID id) throws Exception {
		RollingStockItem rollingStockItemQueryResult;
		RollingStockCursorWrapper cursor = queryRollingStock(RollingStockTable.Cols.UUID + " = ?", new String[]{
				id.toString()
		});
		try {
			cursor.moveToFirst();
			rollingStockItemQueryResult = cursor.getRollingStock();
		} finally {
			cursor.close();
		}
		if (rollingStockItemQueryResult != null)
			return rollingStockItemQueryResult;
		else
			throw new Exception("No results were found!");
	}

	private static ContentValues getContentValues(RollingStockItem rollingStockItem) {
		ContentValues values = new ContentValues();
		values.put(RollingStockTable.Cols.UUID, rollingStockItem.getId().toString());
		values.put(RollingStockTable.Cols.REPORTINGMARK, rollingStockItem.getReportingMark());
		values.put(RollingStockTable.Cols.FLEETID, Integer.toString(rollingStockItem.getFleetID()));
		values.put(RollingStockTable.Cols.STOCK_TYPE, rollingStockItem.getStockType());
		values.put(RollingStockTable.Cols.OWNING_COMPANY, rollingStockItem.getOwningCompany());
		values.put(RollingStockTable.Cols.IS_ENGINE, Boolean.toString(rollingStockItem.isEngine()));
		values.put(RollingStockTable.Cols.IS_LOADED, Boolean.toString(rollingStockItem.isLoaded()));
		values.put(RollingStockTable.Cols.IS_RENTED, Boolean.toString(rollingStockItem.isRented()));

		return values;
	}


	public boolean importRollingStock(FileDescriptor fd) {
		List<RollingStockItem> newRollingStockItems = Utils.readRollingStockFromCSV(fd);
		if (newRollingStockItems == null) {
//			Toast.makeText(context, "This feature is not available on this version of Android.", Toast.LENGTH_LONG).show();
			return false;
		} else {
			for (RollingStockItem rollingStockItem : newRollingStockItems) {
				addRollingStock(rollingStockItem);
			}
		}
		return true;
	}

	public boolean exportRollingStock(Context context, FileDescriptor fd) {
		return Utils.writeRollingStockToCSV(context, fd);
	}

	public void addRollingStock(RollingStockItem rollingStockItem) {
		ContentValues values = getContentValues(rollingStockItem);

		rollingStockDatabase.insert(RollingStockTable.NAME, null, values);
	}

	public void finish() {
		rollingStockDatabase.close();
	}

	public RollingStockCursorWrapper queryRollingStock(String whereClause, String[] whereArgs) {
		Cursor cursor = rollingStockDatabase.query(
				RollingStockTable.NAME,
				null, /* columns - null selects all columns */
				whereClause,
				whereArgs,
				null, // groupBy
				null, // having
				null // orderBy
		);
		//cursor.close();

		return new RollingStockCursorWrapper(cursor);
	}

	public void removeRollingStock(UUID id) {
		rollingStockDatabase.delete(
				RollingStockTable.NAME,
				RollingStockTable.Cols.UUID + " = ?",
				new String[]{
						id.toString()
				}
		);
	}

	public void updateRollingStock(RollingStockItem rollingStockItem) {
		String uuidStr = rollingStockItem.getId().toString();
		ContentValues contentValues = getContentValues(rollingStockItem);

		rollingStockDatabase.update(
				RollingStockTable.NAME,
				contentValues,
				RollingStockTable.Cols.UUID + " = ?",
				new String[]{
						uuidStr
				}
		);
	}
}
