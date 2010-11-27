package pamela.client;

import android.content.ContentProvider;
import android.content.Context;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.database.sqlite.SQLiteQueryBuilder;
import android.net.Uri;
import android.text.TextUtils;
import android.util.Log;

public class PamelaServiceProvider extends ContentProvider {

	private static final String TAG = "PamelaServiceProvider";

    private static final String DATABASE_NAME = "pamela.db";
    private static final int DATABASE_VERSION = 2;
    private static final String SERVICES_TABLE_NAME = "services";
    private static final String DEVICES_TABLE_NAME = "devices";
	
    private static final int SERVICES = 1;
    private static final int SERVICE_ID = 2;
    
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
            Log.w(TAG, "Upgrading database from version " + oldVersion + " to "
                    + newVersion + ", which will destroy all old data");
            db.execSQL("DROP TABLE IF EXISTS notes");
            onCreate(db);
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
	            qb.setProjectionMap(sNotesProjectionMap);
	            break;
	
	        case SERVICE_ID:
	            qb.setProjectionMap(sNotesProjectionMap);
	            qb.appendWhere(NoteColumns._ID + "=" + uri.getPathSegments().get(1));
	            break;
	
	        default:
	            throw new IllegalArgumentException("Unknown URI " + uri);
        }

        // Get the database and run the query
        SQLiteDatabase db = mOpenHelper.getReadableDatabase();
        Cursor c = qb.query(db, projection, selection, selectionArgs, null, null, orderBy);

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
        if (sUriMatcher.match(uri) != NOTES) {
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
        if (values.containsKey(NoteColumns.CREATED_DATE) == false) {
            values.put(NoteColumns.CREATED_DATE, now);
        }

        if (values.containsKey(NoteColumns.MODIFIED_DATE) == false) {
            values.put(NoteColumns.MODIFIED_DATE, now);
        }

        if (values.containsKey(NoteColumns.TITLE) == false) {
            Resources r = Resources.getSystem();
            values.put(NoteColumns.TITLE, r.getString(android.R.string.untitled));
        }

        if (values.containsKey(NoteColumns.NOTE) == false) {
            values.put(NoteColumns.NOTE, "");
        }

        SQLiteDatabase db = mOpenHelper.getWritableDatabase();
        long rowId = db.insert(NOTES_TABLE_NAME, NoteColumns.NOTE, values);
        if (rowId > 0) {
            Uri noteUri = ContentUris.withAppendedId(NoteColumns.CONTENT_URI, rowId);
            getContext().getContentResolver().notifyChange(noteUri, null);
            return noteUri;
        }

        throw new SQLException("Failed to insert row into " + uri);
    }

    @Override
    public int delete(Uri uri, String where, String[] whereArgs) {
        SQLiteDatabase db = mOpenHelper.getWritableDatabase();
        int count;
        switch (sUriMatcher.match(uri)) {
        case NOTES:
            count = db.delete(NOTES_TABLE_NAME, where, whereArgs);
            break;

        case NOTE_ID:
            String noteId = uri.getPathSegments().get(1);
            count = db.delete(NOTES_TABLE_NAME, NoteColumns._ID + "=" + noteId
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
        SQLiteDatabase db = mOpenHelper.getWritableDatabase();
        int count;
        switch (sUriMatcher.match(uri)) {
        case NOTES:
            count = db.update(NOTES_TABLE_NAME, values, where, whereArgs);
            break;

        case NOTE_ID:
            String noteId = uri.getPathSegments().get(1);
            count = db.update(NOTES_TABLE_NAME, values, NoteColumns._ID + "=" + noteId
                    + (!TextUtils.isEmpty(where) ? " AND (" + where + ')' : ""), whereArgs);
            break;

        default:
            throw new IllegalArgumentException("Unknown URI " + uri);
        }

        getContext().getContentResolver().notifyChange(uri, null);
        return count;
    }

    static {
        sUriMatcher = new UriMatcher(UriMatcher.NO_MATCH);
        sUriMatcher.addURI(NotePad.AUTHORITY, "notes", NOTES);
        sUriMatcher.addURI(NotePad.AUTHORITY, "notes/#", NOTE_ID);
        sUriMatcher.addURI(NotePad.AUTHORITY, "live_folders/notes", LIVE_FOLDER_NOTES);

        sNotesProjectionMap = new HashMap<String, String>();
        sNotesProjectionMap.put(NoteColumns._ID, NoteColumns._ID);
        sNotesProjectionMap.put(NoteColumns.TITLE, NoteColumns.TITLE);
        sNotesProjectionMap.put(NoteColumns.NOTE, NoteColumns.NOTE);
        sNotesProjectionMap.put(NoteColumns.CREATED_DATE, NoteColumns.CREATED_DATE);
        sNotesProjectionMap.put(NoteColumns.MODIFIED_DATE, NoteColumns.MODIFIED_DATE);

        // Support for Live Folders.
        sLiveFolderProjectionMap = new HashMap<String, String>();
        sLiveFolderProjectionMap.put(LiveFolders._ID, NoteColumns._ID + " AS " +
                LiveFolders._ID);
        sLiveFolderProjectionMap.put(LiveFolders.NAME, NoteColumns.TITLE + " AS " +
                LiveFolders.NAME);
        // Add more columns here for more robust Live Folders.
    }    
    
}
