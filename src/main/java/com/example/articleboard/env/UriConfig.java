package com.example.articleboard.env;

public class UriConfig {
    public static final String API_BASE = "/api";
    public static final String LOGIN = API_BASE + "/login";
    public static final String LOGOUT = API_BASE + "logout";

    public static class Member {
        public static final String MEMBERS = "/members";
        public static final String PROFILE = MEMBERS + "/{memberId}/profile";
    }

    public static class Article {
        public static final String ARTICLES = "/articles";
        public static final String ARTICLE_ID = ARTICLES + "/{articleId}";
        public static final String MEMBER_ARTICLES = ARTICLES + "/{memberId}";
    }

    public static class Comment {
        public static final String COMMENTS = "/comments";
        public static final String COMMENT_ID = COMMENTS + "/{commentId}";
        public static final String MEMBER_COMMENTS = COMMENTS + "/member/{memberId}";
    }
}
