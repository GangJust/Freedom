package com.mm.freedom.config;

import com.mm.freedom.utils.GHttpUtils;
import com.mm.freedom.utils.GJSONUtils;
import com.mm.freedom.utils.GTextUtils;

import org.json.JSONObject;

public class Version {
    // Api
    public static final String githubReleasesApi = "https://api.github.com/repos/GangJust/Freedom/releases/latest";

    // 获取Github最后一次 releases
    public static void getRemoteReleasesLatest(VersionConfigCallback callback) {
        new Thread(() -> {
            String json = GHttpUtils.get(githubReleasesApi);
            if (json.isEmpty()) return;
            if(json.contains("message") && json.contains("documentation_url")) return;
            callback.callback(parseVersionConfig(json));
        }).start();
    }

    // 解析出版本信息
    public static VersionConfig parseVersionConfig(String json) {
        JSONObject parse = GJSONUtils.parse(json);
        String htmlUrl = GJSONUtils.getString(parse, "html_url");
        String tagName = GJSONUtils.getString(parse, "tag_name");
        String body = GJSONUtils.getString(parse, "body");
        JSONObject latest = GJSONUtils.get(GJSONUtils.getArray(parse, "assets"), 0);
        String name = GJSONUtils.getString(latest, "name");
        long size = GJSONUtils.getLong(latest, "size");
        String createdAt = GJSONUtils.getString(latest, "created_at");
        String updatedAt = GJSONUtils.getString(latest, "updated_at");
        String browserDownloadUrl = GJSONUtils.getString(latest, "browser_download_url");
        return new VersionConfig(
                htmlUrl,
                tagName,
                body,
                name,
                size,
                createdAt,
                updatedAt,
                browserDownloadUrl
        );
    }

    // 比较两个版本名
    // versionName1 > versionName2  return: -1
    // versionName1 < versionName2  return: 1
    // versionName1 == versionName2  return: 0
    public static int compare(String versionName1, String versionName2) {
        versionName1 = versionName1.replaceAll("[^0-9]", "");
        versionName2 = versionName2.replaceAll("[^0-9]", "");
        int max = Math.max(versionName1.length(), versionName2.length());
        int ver1 = Integer.parseInt(GTextUtils.padRight(versionName1, max, '0'));
        int ver2 = Integer.parseInt(GTextUtils.padRight(versionName2, max, '0'));
        return Integer.compare(ver2, ver1);
    }

    // 版本信息
    public static class VersionConfig {
        public final String htmlUrl;
        public final String tagName;
        public final String body;
        public final String name;
        public final long size;
        public final String createdAt;
        public final String updatedAt;
        public final String browserDownloadUrl;

        public VersionConfig(String htmlUrl, String tagName, String body, String name, long size, String createdAt, String updatedAt, String browserDownloadUrl) {
            this.htmlUrl = htmlUrl;
            this.tagName = tagName;
            this.body = body;
            this.name = name;
            this.size = size;
            this.createdAt = createdAt;
            this.updatedAt = updatedAt;
            this.browserDownloadUrl = browserDownloadUrl;
        }
    }

    // 版本信息获取成功, 回调
    public interface VersionConfigCallback {
        void callback(VersionConfig config);
    }
}
