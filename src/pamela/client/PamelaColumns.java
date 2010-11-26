package pamela.client;

import android.net.Uri;
import android.provider.BaseColumns;

/**
 * Notes table
 */
public final class PamelaColumns implements BaseColumns {

	public static final String AUTHORITY = "pamela.client";
	
    /**
     * The content:// style URL for this table
     */
    public static final Uri CONTENT_URI = Uri.parse("content://" + AUTHORITY + "/notes");

    /**
     * The MIME type of {@link #CONTENT_URI} providing a directory of notes.
     */
    public static final String CONTENT_TYPE = "pamela.client";
	
    /**
     * The timestamp for when the note was created
     * <P>Type: INTEGER (long from System.curentTimeMillis())</P>
     */
    public static final String CREATED_DATE = "created";

    /**
     * The timestamp for when the note was last modified
     * <P>Type: INTEGER (long from System.curentTimeMillis())</P>
     */
    public static final String MODIFIED_DATE = "modified";
	
    /**
     * The title of the note
     * <P>Type: TEXT</P>
     */
    public static final String NAME = "name";

    /**
     * The note itself
     * <P>Type: TEXT</P>
     */
    public static final String URL = "url";
	

}
