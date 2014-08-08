package com.changlianxi.contentprovider;

import java.util.HashMap;

import android.content.ContentProvider;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteQueryBuilder;
import android.net.Uri;
import android.text.TextUtils;

import com.changlianxi.db.DBUtils;

/**
 * 操作数据库CircleMember表的ContentProvider
  *
 */
public class CircleMemberContentProvider extends ContentProvider {

    private static HashMap<String, String> sMembersProjectionMap;

    private static final int MEMBERS = 1;
    private static final int MEMBERS_ID = 2;

    private static final UriMatcher sUriMatcher;

    @Override
    public boolean onCreate() {
        return true;
    }

    @Override
    public Cursor query(Uri uri, String[] projection, String selection,
            String[] selectionArgs, String sortOrder) {
        SQLiteQueryBuilder qb = new SQLiteQueryBuilder();
        qb.setTables(CircleMemberProvider.CircleMemberColumns.TABLE_NAME);

        switch (sUriMatcher.match(uri)) {
            case MEMBERS:
                qb.setProjectionMap(sMembersProjectionMap);
                break;

            case MEMBERS_ID:
                qb.setProjectionMap(sMembersProjectionMap);
                qb.appendWhere(CircleMemberProvider.CircleMemberColumns._ID
                        + "=" + uri.getPathSegments().get(1));
                break;

            default:
                throw new IllegalArgumentException("Unknown URI " + uri);
        }

        // Get the database and run the query
        SQLiteDatabase db = DBUtils.getDBsa(1);
        Cursor c = qb.query(db, projection, selection, selectionArgs, null,
                null, null);

        // Tell the cursor what uri to watch, so it knows when its source data
        // changes
        c.setNotificationUri(getContext().getContentResolver(), uri);
        return c;
    }

    @Override
    public String getType(Uri uri) {
        switch (sUriMatcher.match(uri)) {
            case MEMBERS:
                return CircleMemberProvider.CONTENT_TYPE;
            case MEMBERS_ID:
                return CircleMemberProvider.CONTENT_ITEM_TYPE;
            default:
                throw new IllegalArgumentException("Unknown URI " + uri);
        }
    }

    @Override
    public Uri insert(Uri uri, ContentValues initialValues) {
        // Validate the requested uri
        if (sUriMatcher.match(uri) != MEMBERS) {
            throw new IllegalArgumentException("Unknown URI " + uri);
        }

        ContentValues values;
        if (initialValues != null) {
            values = new ContentValues(initialValues);
        } else {
            values = new ContentValues();
        }

        // Make sure that the fields are all set
        if (values.containsKey(CircleMemberProvider.CircleMemberColumns.NAME) == false) {
            values.put(CircleMemberProvider.CircleMemberColumns.NAME, "");
        }

        if (values.containsKey(CircleMemberProvider.CircleMemberColumns.UID) == false) {
            values.put(CircleMemberProvider.CircleMemberColumns.UID, 0);
        }

        SQLiteDatabase db = DBUtils.getDBsa(2);
        long rowId = db.insert(
                CircleMemberProvider.CircleMemberColumns.TABLE_NAME,
                CircleMemberProvider.CircleMemberColumns.NAME, values);
        if (rowId > 0) {
            Uri noteUri = ContentUris
                    .withAppendedId(
                            CircleMemberProvider.CircleMemberColumns.CONTENT_URI,
                            rowId);
            getContext().getContentResolver().notifyChange(noteUri, null);
            return noteUri;
        }

        throw new SQLException("Failed to insert row into " + uri);
    }

    @Override
    public int delete(Uri uri, String where, String[] whereArgs) {
        SQLiteDatabase db = DBUtils.getDBsa(2);
        int count;
        switch (sUriMatcher.match(uri)) {
            case MEMBERS:
                count = db.delete(
                        CircleMemberProvider.CircleMemberColumns.TABLE_NAME,
                        where, whereArgs);
                break;

            case MEMBERS_ID:
                String noteId = uri.getPathSegments().get(1);
                count = db.delete(
                        CircleMemberProvider.CircleMemberColumns.TABLE_NAME,
                        CircleMemberProvider.CircleMemberColumns._ID
                                + "="
                                + noteId
                                + (!TextUtils.isEmpty(where) ? " AND (" + where
                                        + ')' : ""), whereArgs);
                break;

            default:
                throw new IllegalArgumentException("Unknown URI " + uri);
        }

        getContext().getContentResolver().notifyChange(uri, null);
        return count;
    }

    @Override
    public int update(Uri uri, ContentValues values, String where,
            String[] whereArgs) {
        SQLiteDatabase db = DBUtils.getDBsa(2);
        int count;
        switch (sUriMatcher.match(uri)) {
            case MEMBERS:
                count = db.update(
                        CircleMemberProvider.CircleMemberColumns.TABLE_NAME,
                        values, where, whereArgs);
                break;

            case MEMBERS_ID:
                String noteId = uri.getPathSegments().get(1);
                count = db.update(
                        CircleMemberProvider.CircleMemberColumns.TABLE_NAME,
                        values, CircleMemberProvider.CircleMemberColumns._ID
                                + "="
                                + noteId
                                + (!TextUtils.isEmpty(where) ? " AND (" + where
                                        + ')' : ""), whereArgs);
                break;

            default:
                throw new IllegalArgumentException("Unknown URI " + uri);
        }

        getContext().getContentResolver().notifyChange(uri, null);
        return count;
    }

    static {
        sUriMatcher = new UriMatcher(UriMatcher.NO_MATCH);
        // 这个地方的members要和CircleMemberColumns.CONTENT_URI中最后面的一个Segment一致
        sUriMatcher.addURI(CircleMemberProvider.AUTHORITY, "members", MEMBERS);
        sUriMatcher.addURI(CircleMemberProvider.AUTHORITY, "members/#",
                MEMBERS_ID);

        sMembersProjectionMap = new HashMap<String, String>();
        sMembersProjectionMap.put(CircleMemberProvider.CircleMemberColumns._ID,
                CircleMemberProvider.CircleMemberColumns._ID);
        sMembersProjectionMap.put(
                CircleMemberProvider.CircleMemberColumns.NAME,
                CircleMemberProvider.CircleMemberColumns.NAME);
        sMembersProjectionMap.put(
                CircleMemberProvider.CircleMemberColumns.AVATAR,
                CircleMemberProvider.CircleMemberColumns.AVATAR);
        sMembersProjectionMap.put(
                CircleMemberProvider.CircleMemberColumns.CELL_PHONE,
                CircleMemberProvider.CircleMemberColumns.CELL_PHONE);
        sMembersProjectionMap.put(CircleMemberProvider.CircleMemberColumns.PID,
                CircleMemberProvider.CircleMemberColumns.PID);
        sMembersProjectionMap.put(CircleMemberProvider.CircleMemberColumns.UID,
                CircleMemberProvider.CircleMemberColumns.UID);
        sMembersProjectionMap.put(
                CircleMemberProvider.CircleMemberColumns.EMPLAYER,
                CircleMemberProvider.CircleMemberColumns.EMPLAYER);
        sMembersProjectionMap.put(
                CircleMemberProvider.CircleMemberColumns.SORT_KEY,
                CircleMemberProvider.CircleMemberColumns.SORT_KEY);
        sMembersProjectionMap.put(
                CircleMemberProvider.CircleMemberColumns.ISMANAGER,
                CircleMemberProvider.CircleMemberColumns.ISMANAGER);
        sMembersProjectionMap.put(
                CircleMemberProvider.CircleMemberColumns.STATE,
                CircleMemberProvider.CircleMemberColumns.STATE);
        sMembersProjectionMap.put(CircleMemberProvider.CircleMemberColumns.CID,
                CircleMemberProvider.CircleMemberColumns.CID);
    }
}
