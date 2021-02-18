package gq.catz.inventoryofrollingstock.database;

import android.database.Cursor;
import android.database.CursorWrapper;

import java.util.UUID;

import gq.catz.inventoryofrollingstock.RollingStockItem;
import gq.catz.inventoryofrollingstock.database.RollingStockDBSchema.RollingStockTable;

public class RollingStockCursorWrapper extends CursorWrapper {

	public RollingStockCursorWrapper(Cursor cursor) {
		super(cursor);
	}

	public RollingStockItem getRollingStock() {
		String uuid = getString(getColumnIndex(RollingStockTable.Cols.UUID));
		String reportingMarks = getString(getColumnIndex(RollingStockTable.Cols.REPORTINGMARK));
		String rollingStockID = getString(getColumnIndex(RollingStockTable.Cols.FLEETID));
		String stockType = getString(getColumnIndex(RollingStockTable.Cols.STOCK_TYPE));
		String owningCompany = getString(getColumnIndex(RollingStockTable.Cols.OWNING_COMPANY));
		String isEngine = getString(getColumnIndex(RollingStockTable.Cols.IS_ENGINE));
		String isLoaded = getString(getColumnIndex(RollingStockTable.Cols.IS_LOADED));
		String isRented = getString(getColumnIndex(RollingStockTable.Cols.IS_RENTED));

		return new RollingStockItem(UUID.fromString(uuid), reportingMarks, new Integer(rollingStockID), stockType, owningCompany, new Boolean(isEngine), new Boolean(isLoaded), new Boolean(isRented));
	}
}











