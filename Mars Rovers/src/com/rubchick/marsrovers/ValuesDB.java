package com.rubchick.marsrovers;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

public class ValuesDB {

	/// First table columns
	public static final String SESSIONS_DATE = "sessions_date";
	public static final String SESSIONS_ID = "_id";

	/// Second table columns
	public static final String OPERATION_TYPE = "operation_type";
	public static final String OPERATION_RESULT = "operation_result";
	public static final String KEY_ROWID = "_id";
	public static final String SESSION_ID = "session_id";

	/// takes the context to allow the database to be
	/// opened/created
	/// @param ctx the Context within which to work
	public void setContext(Context ctx) {
		this.mCtx = ctx;
	}

	/// returns instance of ValuesDB
	/// @return dataBase
	public static ValuesDB getInstance() {
		if (dataBase == null) {
			dataBase = new ValuesDB();
		}
		return dataBase;
	}

	/// This method is used for creating/opening connection
	/// @return instance of DatabaseUtil
	/// @throws SQLException
	public ValuesDB open() throws SQLException {
		mDbHelper = new DatabaseHelper(mCtx);
		mDb = mDbHelper.getWritableDatabase();

		return this;
	}

	/// This method is used for closing the connection.
	public void close() {
		mDbHelper.close();
	}

	///This method is used to create/insert new record Student record.
	/// @param name
	/// @param grade
	/// @return long
	public long createOperation(String type, String result) {
		ContentValues initialValues = new ContentValues();

		initialValues.put(OPERATION_TYPE, type);
		initialValues.put(OPERATION_RESULT, result);
		initialValues.put(SESSION_ID, fetchSession(session).getString(0));

		Log.i(TAG, "add Operation: " + initialValues.toString());

		return mDb.insert(TABLE_OPERATIONS, null, initialValues);
	}

	public long createSession(String date) {
		ContentValues initialValues = new ContentValues();

		initialValues.put(SESSIONS_DATE, date);

		Log.i(TAG, "add Session: " + initialValues.toString());

		session = mDb.insert(TABLE_SESSIONS, null, initialValues);
		return session;
	}

	/// This method will delete Student record.
	/// @param rowId
	/// @return boolean
	public boolean deleteOperation(long rowId) {
		return mDb.delete(TABLE_OPERATIONS, KEY_ROWID + "=" + rowId, null) > 0;
	}

	public boolean deleteSession(long rowId) {
		return mDb.delete(TABLE_SESSIONS, KEY_ROWID + "=" + rowId, null) > 0;
	}

	/// This method will return Cursor holding all the Student records.
	/// @return Cursor
	public Cursor fetchAllOperations() {
		return mDb.query(TABLE_OPERATIONS, new String[] { KEY_ROWID,
				OPERATION_TYPE, OPERATION_RESULT, SESSION_ID }, null, null,
				null, null, null);
	}

	public Cursor fetchAllSesions() {
		return mDb.query(TABLE_SESSIONS, new String[] { KEY_ROWID,
				SESSIONS_DATE }, null, null, null, null, null);
	}

	/// This method will return Cursor holding the specific Student record.
	/// @param id
	/// @return Cursor
	/// @throws SQLException
	public Cursor fetchOperation(long id) throws SQLException {
		Cursor mCursor = mDb.query(true, TABLE_OPERATIONS, new String[] {
				KEY_ROWID, OPERATION_TYPE, OPERATION_RESULT, SESSION_ID },
				KEY_ROWID + "=" + id, null, null, null, null, null);

		if (mCursor != null) {
			mCursor.moveToFirst();
		}

		return mCursor;
	}

	public Cursor fetchSession(long id) throws SQLException {
		Cursor mCursor = mDb.query(true, TABLE_SESSIONS, new String[] {
				KEY_ROWID, SESSIONS_DATE }, KEY_ROWID + "=" + id, null, null,
				null, null, null);

		if (mCursor != null) {
			mCursor.moveToFirst();
		}

		return mCursor;
	}

	/// This method will update Student record.
	/// @param id
	/// @param name
	/// @param standard
	/// @return boolean
	public boolean updateOperation(int id, String type, String result) {
		ContentValues args = new ContentValues();

		args.put(OPERATION_TYPE, type);
		args.put(OPERATION_RESULT, result);

		return mDb.update(TABLE_OPERATIONS, args, KEY_ROWID + "=" + id, null) > 0;
	}

	public boolean updateSession(int id, String date) {
		ContentValues args = new ContentValues();

		args.put(SESSIONS_DATE, date);

		return mDb.update(TABLE_SESSIONS, args, KEY_ROWID + "=" + id, null) > 0;
	}

	/// private Ñonstructor
	private ValuesDB() {

	}

	/// Inner private class. Database Helper class for creating and updating
	/// database.
	private static class DatabaseHelper extends SQLiteOpenHelper {
		DatabaseHelper(Context context) {
			super(context, DATABASE_NAME, null, DATABASE_VERSION);
		}

		/// onCreate method is called for the 1st time when database doesn't
		/// exists.
		@Override
		public void onCreate(SQLiteDatabase db) {
			Log.i(TAG, "Creating DataBase: " + CREATE_OPERATIONS_TABLE);
			db.execSQL(CREATE_SESSIONS_TABLE);
			db.execSQL(CREATE_OPERATIONS_TABLE);
		}

		/// onUpgrade method is called when database version changes.
		@Override
		public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
			Log.w(TAG, "Upgrading database from version " + oldVersion + " to "
					+ newVersion);
		}

		public void onOpen(SQLiteDatabase db) {
			super.onOpen(db);
			if (!db.isReadOnly()) {
				// Enable foreign key constraints
				db.execSQL("PRAGMA foreign_keys=ON;");
			}
		}
	}

	private static final String TAG = "DatabaseUtil";

	/// Database Name
	private static final String DATABASE_NAME = "operations_database";

	/// Database Version
	private static final int DATABASE_VERSION = 1;

	/// First table Name
	private static final String TABLE_SESSIONS = "sessions";

	/// Second table Name
	private static final String TABLE_OPERATIONS = "operations";

	/// Database creation sql statements
	///////////////////////////////////////////////////////////
	private static final String CREATE_SESSIONS_TABLE = "create table "
			+ TABLE_SESSIONS + " (" + SESSIONS_ID
			+ " integer primary key autoincrement, " + SESSIONS_DATE
			+ " text not null);";

	// /////////////////////////////////////////////////////////

	private static final String CREATE_OPERATIONS_TABLE = "create table "
			+ TABLE_OPERATIONS + " (" + KEY_ROWID
			+ " integer primary key autoincrement, " + OPERATION_TYPE
			+ " text not null, " + OPERATION_RESULT + " text not null, "
			+ SESSION_ID + " integer," + " FOREIGN KEY (" + SESSION_ID + ") "
			+ "REFERENCES " + TABLE_SESSIONS + "(" + SESSIONS_ID + ") "
			+ "ON DELETE CASCADE );";

	/// Context
	private Context mCtx;

	private DatabaseHelper mDbHelper;
	private SQLiteDatabase mDb;

	private long session;

	private static ValuesDB dataBase;
}