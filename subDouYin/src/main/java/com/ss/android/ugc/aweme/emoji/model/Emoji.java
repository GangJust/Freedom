package com.ss.android.ugc.aweme.emoji.model;

import com.ss.android.ugc.aweme.base.model.UrlModel;

import java.io.Serializable;
import java.util.HashMap;

public class Emoji implements /*Parcelable,*/ Serializable {
    //@SerializedName("animate_type")
    public String animateType;

    //@SerializedName("animate_url")
    public UrlModel animateUrl;

    //@SerializedName("origin_author_id")
    public String authorId;

    //@SerializedName("display_name")
    public String displayName;

    //@SerializedName("display_name_lang")
    public HashMap<String, String> displayNameLangs;
    public int emojiType;

    //@SerializedName("height")
    public int height;

    //@SerializedName("hide")
    public Boolean hide;

    //@SerializedName("id")
    public long id;

    //@SerializedName("static_url_list")
    //public List<IDUrlModel> idUrlModelList;

    //@SerializedName("joker_sticker_id")
    public String jokerId;

    //@SerializedName("log_pb")
    //public LogPbBean mLogPb;

    //@SerializedName("hash")
    public String md5;

    //@SerializedName("resource_type")
    public int resourceType;

    //@SerializedName("origin_package_id")
    public long resourcesId;
    public String searchKeyword;

    //@SerializedName("single_hey_can_id")
    public String singleHeyCanId;

    //@SerializedName("static_type")
    public String staticType;

    //@SerializedName("static_url")
    public UrlModel staticUrl;

    //@SerializedName("sticker_info_source")
    public String stickerInfoSource;

    //@SerializedName("sticker_type")
    public int stickerType;

    //@SerializedName("version")
    public String version;

    //@SerializedName("video_id")
    public String videoId;

    //@SerializedName("width")
    public int width;
}
