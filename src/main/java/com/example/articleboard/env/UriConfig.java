package com.example.articleboard.env;

public class UriConfig {
    public static final String API_BASE = "/api";
    public static final String LOGIN = API_BASE + "/login";
    public static final String LOGOUT = API_BASE + "logout";

    public static class Member {
        public static final String BASE = API_BASE + "/members";
        public static final String PROFILE = "/{memberId}/profile";
    }

    public static class Article {
        public static final String BASE = API_BASE + "/articles";
        public static final String ARTICLE_ID = "/{articleId}";
        public static final String MEMBER_ARTICLES = "/{memberId}";
    }

    public static class Comment {
        public static final String BASE = API_BASE + "/comments";
        public static final String COMMENT_ID =  "/{commentId}";
        public static final String MEMBER_COMMENTS = "/member/{memberId}";
    }
}
