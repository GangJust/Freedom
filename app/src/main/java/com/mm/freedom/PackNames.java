package com.mm.freedom;

/// 该类下存放被hook的包名
public class PackNames {
    protected static final String MINE_PACK = PackNames.class.getPackage().getName();

    protected static final String[] packNames = new String[]{
            MINE_PACK,
            "com.ss.android.ugc.aweme",
    };
}
