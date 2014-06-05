package com.changlianxi.db;

public class Const {
    public static final String CIRCLE_TABLE_NAME = "circles";
    public static final String CIRCLE_TABLE_STRUCTURE = "_id integer PRIMARY KEY AUTOINCREMENT,"
            + " id integer, name varchar, description varchar, logo varchar, isNew varchar,"
            + " myState varchar, creator integer, myInvitor integer, created varchar,"
            + " joinTime varchar, total integer, inviting integer, verified integer,"
            + " unverified integer, newMemberCnt, newGrowthCnt, newMyDetailEditCnt,"
            + " newDynamicCnt, newGrowthCommentCnt";

    public static final String CIRCLE_ROLE_TABLE_NAME = "circle_roles";
    public static final String CIRCLE_ROLE_TABLE_STRUCTURE = "_id integer PRIMARY KEY AUTOINCREMENT,"
            + " cid integer, id integer, name varchar, count integer";

    public static final String CIRCLE_MEMBER_TABLE_NAME = "circle_members";
    public static final String CIRCLE_MEMBER_TABLE_STRUCTURE = "_id integer PRIMARY KEY AUTOINCREMENT,"
            + " cid integer, uid integer, pid integer, name varchar, cellphone varchar, location varchar,"
            + " gendar integer, avatar varchar, birthday varchar, employer varchar, jobtitle varchar,"
            + " lastModTime varchar, roleId integer, state varchar,"
            + " detailIds varchar, cmid integer, inviteCode varchar, sortkey varchar, auth varchar,"
            + " pinyinFir varchar, privacySettings varchar, register varchar";

    public static final String PERSON_DETAIL_TABLE_NAME = "person_details";
    public static final String PERSON_DETAIL_TABLE_STRUCTURE = "_id integer PRIMARY KEY AUTOINCREMENT,"
            + " id integer, cid integer, type varchar, value varchar, start varchar, end integer";
    public static final String PERSON_DETAIL_TABLE_NAME1 = "person_details1";
    public static final String PERSON_DETAIL_TABLE_STRUCTURE1 = "_id integer PRIMARY KEY AUTOINCREMENT,"
            + " id integer, cid integer,pid integer,uid integer, type varchar, value varchar, start varchar, end integer";
    public static final String TIME_RECORD_TABLE_NAME = "time_records";
    public static final String TIME_RECORD_TABLE_STRUCTURE = "_id integer PRIMARY KEY AUTOINCREMENT,"
            + " key varchar, subkey varchar, time long";
    public static final String TIME_RECORD_KEY_PREFIX_GROWTH = "growth";
    public static final String TIME_RECORD_KEY_PREFIX_CIRCLEMEMBER = "cm";
    public static final String TIME_RECORD_KEY_PREFIX_CIRCLECHAT = "chats";
    public static final String TIME_RECORD_KEY_PREFIX_PARTNERS = "partners";
    public static final String TIME_RECORD_KEY_PREFIX_CIRCLEDYNAMIC = "dynamics";
    public static final String TIME_RECORD_KEY_PREFIX_CIRCLES = "circles";
    public static final String TIME_RECORD_KEY_PREFIX_COMMENTSFORME = "commentsForMe";

    public static final String GROWTH_TABLE_NAME = "growths";
    public static final String GROWTH_TABLE_STRUCTURE = "_id integer PRIMARY KEY AUTOINCREMENT,"
            + " id integer, cid integer, publisher integer, content varchar, location varchar, happened integer,"
            + " published varchar, praiseCnt integer, commentCnt integer, isPraised integer,"
            + " lastCommentsReqTime long";

    public static final String GROWTH_IMAGE_TABLE_NAME = "growth_images";
    public static final String GROWTH_IMAGE_TABLE_STRUCTURE = "_id integer PRIMARY KEY AUTOINCREMENT,"
            + " cid integer, gid integer, imgId integer, img varchar";

    public static final String GROWTH_COMMENT_TABLE_NAME = "growth_comments";
    public static final String GROWTH_COMMENT_TABLE_STRUCTURE = "_id integer PRIMARY KEY AUTOINCREMENT,"
            + " cid integer, gid integer, gcid integer, uid integer, replyid integer,"
            + " content varchar, time varchar, isForMe integer";

    public static final String CIRCLE_CHAT_TABLE_NAME = "circle_chats";
    public static final String CIRCLE_CHAT_TABLE_STRUCTURE = "_id integer PRIMARY KEY AUTOINCREMENT,"
            + " chatId integer, cid integer, sender integer, type varchar, content varchar, time varchar";

    public static final String PERSON_CHAT_TABLE_NAME = "person_chats";
    public static final String PERSON_CHAT_TABLE_STRUCTURE = "_id integer PRIMARY KEY AUTOINCREMENT,"
            + " chatId integer, cid integer, partner integer, sender integer, type varchar, content varchar,"
            + " time varchar, isRead integer";

    public static final String CHAT_PARTNER_TABLE_NAME = "person_chat_partners";
    public static final String CHAT_PARTNER_TABLE_STRUCTURE = "_id integer PRIMARY KEY AUTOINCREMENT,"
            + " chatId integer, cid integer, partner integer, partnerName varchar, type varchar, content varchar,"
            + "time varchar, unReadCnt integer, lastChatsReqTime long";

    public static final String CIRCLE_DYNAMIC_TABLE_NAME = "circle_dynamics";
    public static final String CIRCLE_DYNAMIC_TABLE_STRUCTURE = "_id integer PRIMARY KEY AUTOINCREMENT,"
            + " id integer, cid integer, uid1 integer, uid2 integer, pid2 integer,"
            + " type varchar, content varchar, detail varchar, time varchar,"
            + " needApproved integer, isPersonal integer";

    public static final String AMENDMENT_TABLE_NAME = "my_amendments";
    public static final String AMENDMENT_TABLE_STRUCTURE = "_id integer PRIMARY KEY AUTOINCREMENT,"
            + " amid integer, cid integer, uid integer, content varchar, time varchar";

    public static final int DYNAMIC_MAX_CACHE_COUNT_PER_CIRCLE = 100;
    public static final int GROWTH_MAX_CACHE_COUNT_PER_CIRCLE = 40;
    public static final int GROWTH_COMMENT_MAX_CACHE_COUNT_PER_ITEM = 20;
    public static final int CHAT_MAX_CACHE_COUNT_PER_CIRCLE = 200;
    public static final int CHAT_MAX_CACHE_COUNT_PER_PERSON = 40;

    public static final String MYINFO_TABLE_NAME = "myinfo";
    public static final String MYINFO_TABLE_STRUCTURE = "_id integer PRIMARY KEY AUTOINCREMENT,"
            + " type integer, count integer";

    public static final String GROWTH_ALBUM_IMAGE_TABLE_NAME = "growth_album_images";
    public static final String GROWTH_ALBUM_IMAGE_TABLE_STRUCTURE = "_id integer PRIMARY KEY AUTOINCREMENT,"
            + " cid integer,picGrowthID integer, picID integer, picPath varchar, location varchar, picHappened varchar,strKey varchar,albumName varchar,year varchar,month varchar,day varchar";

    public static final String GROWTH_ALBUM_TABLE_NAME = "growth_album";
    public static final String GROWTH_ALBUM_TABLE_STRUCTURE = "_id integer PRIMARY KEY AUTOINCREMENT,"
            + " cid integer, albumTotal integer, albumContributors integer, albumDate varchar, albumName varchar,strKey varchar,year varchar,month varchar,day varchar";

    public static final String ALBUM_GROWTH_TABLE_NAME = "album_growths";
    public static final String ALBUM_GROWTH_TABLE_STRUCTURE = "_id integer PRIMARY KEY AUTOINCREMENT,"
            + " id integer, cid integer, publisher integer, content varchar, location varchar, happened integer,"
            + " published varchar, praiseCnt integer, commentCnt integer, isPraised integer,"
            + " lastCommentsReqTime long,albumName varchar,strKey varchar,year varchar,month varchar,day varchar";

}
