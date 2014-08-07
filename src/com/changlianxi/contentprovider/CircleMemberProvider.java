package com.changlianxi.contentprovider;

import android.net.Uri;
import android.provider.BaseColumns;

/**
 * 存放跟数据库有关的常量
  *
 */
public class CircleMemberProvider {

    // 这个是每个Provider的标识，在Manifest中使用
    public static final String AUTHORITY = "com.changlianxi.circle.members";

    public static final String CONTENT_TYPE = "vnd.android.cursor.dir/vnd.changlianxi";

    public static final String CONTENT_ITEM_TYPE = "vnd.android.cursor.item/vnd.changlianxi";

    /**
     * 跟circle_members表相关的常量
      *
     */
    public static final class CircleMemberColumns implements BaseColumns {
        // CONTENT_URI跟数据库的表关联，最后根据CONTENT_URI来查询对应的表
        public static final Uri CONTENT_URI = Uri.parse("content://"
                + AUTHORITY + "/members");
        public static final String TABLE_NAME = "circle_members";
        public static final String NAME = "name";
        public static final String CELL_PHONE = "cellphone";
        public static final String UID = "uid";
        public static final String PID = "pid";
        public static final String EMPLAYER = "employer";
        public static final String AVATAR = "avatar";
        public static final String CID = "cid";
        public static final String STATE = "state";
        public static final String SORT_KEY = "sortkey";
        public static final String ISMANAGER = "isManager";

    }

}
