package gq.catz.inventoryofrollingstock.database;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import gq.catz.inventoryofrollingstock.database.RollingStockDBSchema.RollingStockTable;

public class RollingStockBaseHelper extends SQLiteOpenHelper {
	private static final int VERSION = 1;
	private static final String DATABASE_NAME = "rollingStockBase.db";

	public RollingStockBaseHelper(Context context) {
		super(context, DATABASE_NAME, null, VERSION);
	}

	@Override
	public void onCreate(SQLiteDatabase sqLiteDatabase) {
		sqLiteDatabase.execSQL("create table " + RollingStockTable.NAME + " (" + // Creates a new table in the database (db)
				" _id integer primary key autoincrement, " + // Assigns each row an integer id key, auto-incrementing. Helps keep rows unique
				// The following adds a new column to the table, labeled the string value.
				RollingStockTable.Cols.UUID + ", " +
				RollingStockTable.Cols.REPORTINGMARK + ", " +
				RollingStockTable.Cols.FLEETID + ", " +
				RollingStockTable.Cols.STOCK_TYPE + ", " +
				RollingStockTable.Cols.OWNING_COMPANY + ", " +
				RollingStockTable.Cols.IS_ENGINE + ", " +
				RollingStockTable.Cols.IS_LOADED + ", " +
				RollingStockTable.Cols.IS_RENTED + ", " +
				RollingStockTable.Cols.IN_CONSIST + " )");
	}

	@Override
	public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {
		sqLiteDatabase.execSQL("drop table " + RollingStockTable.NAME);
		onCreate(sqLiteDatabase);
	}
}
