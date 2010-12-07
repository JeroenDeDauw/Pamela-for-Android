package pamela.client;

import java.util.HashMap;

import android.content.ContentProvider;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Context;
import android.content.UriMatcher;
import android.content.res.Resources;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.database.sqlite.SQLiteQueryBuilder;
import android.net.Uri;
import android.text.TextUtils;

public class PamelaServiceProvider extends ContentProvider {

    private static final String DATABASE_NAME = "pamela.db";
    private static final int DATABASE_VERSION = 2;
    private static final String SERVICES_TABLE_NAME = "services";
    private static final String DEVICES_TABLE_NAME = "devices";
	
    private static final int SERVICES = 1;
    private static final int SERVICE_ID = 2;
    
    private static HashMap<String, String> servicesProjectionMap;
    
    private static final UriMatcher uriMatcher;
    
    /**
     * This class helps open, create, and upgrade the database file.
     */
    private static class DatabaseHelper extends SQLiteOpenHelper {

        DatabaseHelper(Context context) {
            super(context, DATABASE_NAME, null, DATABASE_VERSION);
        }

        @Override
        public void onCreate(SQLiteDatabase db) {
            db.execSQL("CREATE TABLE " + SERVICES_TABLE_NAME + " ("
                    + PamelaColumns._ID + " INTEGER PRIMARY KEY,"
                    + PamelaColumns.NAME + " TEXT,"
                    + PamelaColumns.URL + " TEXT,"
                    + PamelaColumns.CREATED_DATE + " INTEGER,"
                    + PamelaColumns.MODIFIED_DATE + " INTEGER"
                    + ");");
            /*
            db.execSQL("CREATE TABLE " + DEVICES_TABLE_NAME + " ("
                    + PamelaColumns._ID + " INTEGER PRIMARY KEY,"
                    + PamelaColumns.MAC + " TEXT,"
                    + PamelaColumns.NAME + " TEXT,"
                    + PamelaColumns.CREATED_DATE + " INTEGER,"
                    + PamelaColumns.MODIFIED_DATE + " INTEGER"
                    + ");"); 
                    */           
        }

        @Override
        public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        }
    }
    
    private DatabaseHelper openHelper;

    @Override
    public boolean onCreate() {
        openHelper = new DatabaseHelper(getContext());
        return true;
    }
    
    @Override
    public Cursor query(Uri uri, String[] projection, String selection, String[] selectionArgs, String sortOrder) {
        SQLiteQueryBuilder qb = new SQLiteQueryBuilder();
        
        qb.setTables(SERVICES_TABLE_NAME);
        qb.setTables(DEVICES_TABLE_NAME);

        switch (uriMatcher.match(uri)) {
	        case SERVICES:
	            qb.setProjectionMap(servicesProjectionMap);
	            break;
	
	        case SERVICE_ID:
	            qb.setProjectionMap(servicesProjectionMap);
	            qb.appendWhere(PamelaColumns._ID + "=" + uri.getPathSegments().get(1));
	            break;
	
	        default:
	            throw new IllegalArgumentException("Unknown URI " + uri);
        }

        // Get the database and run the query
        SQLiteDatabase db = openHelper.getReadableDatabase();
        Cursor c = qb.query(db, projection, selection, selectionArgs, null, null, null);

        // Tell the cursor what uri to watch, so it knows when its source data changes
        c.setNotificationUri(getContext().getContentResolver(), uri);
        return c;
    }
    
    @Override
    public String getType(Uri uri) {
        switch (uriMatcher.match(uri)) {
	        case SERVICES:
	            return PamelaColumns.CONTENT_TYPE;
	
	        case SERVICE_ID:
	            return PamelaColumns.CONTENT_ITEM_TYPE;
	
	        default:
	            throw new IllegalArgumentException("Unknown URI " + uri);
        }
    }

    @Override
    public Uri insert(Uri uri, ContentValues initialValues) {
        // Validate the requested uri
        if (uriMatcher.match(uri) != SERVICES) {
            throw new IllegalArgumentException("Unknown URI " + uri);
        }

        ContentValues values;
        if (initialValues != null) {
            values = new ContentValues(initialValues);
        } else {
            values = new ContentValues();
        }

        Long now = Long.valueOf(System.currentTimeMillis());

        // Make sure that the fields are all set
        if (values.containsKey(PamelaColumns.CREATED_DATE) == false) {
            values.put(PamelaColumns.CREATED_DATE, now);
        }

        if (values.containsKey(PamelaColumns.MODIFIED_DATE) == false) {
            values.put(PamelaColumns.MODIFIED_DATE, now);
        }

        if (values.containsKey(PamelaColumns.NAME) == false) {
            Resources r = Resources.getSystem();
            values.put(PamelaColumns.NAME, r.getString(android.R.string.untitled));
        }

        if (values.containsKey(PamelaColumns.URL) == false) {
            values.put(PamelaColumns.URL, "");
        }

        SQLiteDatabase db = openHelper.getWritableDatabase();
        long rowId = db.insert(SERVICES_TABLE_NAME, PamelaColumns.NAME, values);
        
        if (rowId > 0) {
            Uri serviceUri = ContentUris.withAppendedId(PamelaColumns.CONTENT_URI, rowId);
            getContext().getContentResolver().notifyChange(serviceUri, null);
            return serviceUri;
        }

        throw new SQLException("Failed to insert row into " + uri);
    }

    @Override
    public int delete(Uri uri, String where, String[] whereArgs) {
        SQLiteDatabase db = openHelper.getWritableDatabase();
        int count;
        switch (uriMatcher.match(uri)) {
        case SERVICES:
            count = db.delete(SERVICES_TABLE_NAME, where, whereArgs);
            break;

        case SERVICE_ID:
            String serviceId = uri.getPathSegments().get(1);
            count = db.delete(SERVICES_TABLE_NAME, PamelaColumns._ID + "=" + serviceId
                    + (!TextUtils.isEmpty(where) ? " AND (" + where + ')' : ""), whereArgs);
            break;

        default:
            throw new IllegalArgumentException("Unknown URI " + uri);
        }

        getContext().getContentResolver().notifyChange(uri, null);
        return count;
    }

    @Override
    public int update(Uri uri, ContentValues values, String where, String[] whereArgs) {
        SQLiteDatabase db = openHelper.getWritableDatabase();
        int count;
        
        switch (uriMatcher.match(uri)) {
        case SERVICES:
            count = db.update(SERVICES_TABLE_NAME, values, where, whereArgs);
            break;

        case SERVICE_ID:
            String serviceId = uri.getPathSegments().get(1);
            count = db.update(SERVICES_TABLE_NAME, values, PamelaColumns._ID + "=" + serviceId
                    + (!TextUtils.isEmpty(where) ? " AND (" + where + ')' : ""), whereArgs);
            break;

        default:
            throw new IllegalArgumentException("Unknown URI " + uri);
        }

        getContext().getContentResolver().notifyChange(uri, null);
        return count;
    }

    static {
        uriMatcher = new UriMatcher(UriMatcher.NO_MATCH);
        uriMatcher.addURI(PamelaColumns.AUTHORITY, "services", SERVICES);
        uriMatcher.addURI(PamelaColumns.AUTHORITY, "services/#", SERVICE_ID);

        servicesProjectionMap = new HashMap<String, String>();
        servicesProjectionMap.put(PamelaColumns._ID, PamelaColumns._ID);
        servicesProjectionMap.put(PamelaColumns.NAME, PamelaColumns.NAME);
        servicesProjectionMap.put(PamelaColumns.URL, PamelaColumns.URL);
        servicesProjectionMap.put(PamelaColumns.CREATED_DATE, PamelaColumns.CREATED_DATE);
        servicesProjectionMap.put(PamelaColumns.MODIFIED_DATE, PamelaColumns.MODIFIED_DATE);
    }    
    
}
