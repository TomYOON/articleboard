package com.example.articleboard.api;

import com.example.articleboard.domain.Article;
import com.example.articleboard.dto.ArticleDto;
import com.example.articleboard.dto.MemberDto;
import com.example.articleboard.service.ArticleService;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import javax.validation.constraints.NotEmpty;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequiredArgsConstructor
public class ArticleApiController {

    private final ArticleService articleService;

    /** TODO:
     * 리턴 타입 DTO로 바꿔줘야됨
     */
    @GetMapping("api/articles")
    public List<ArticleDto> articles(@RequestParam(value = "offset", defaultValue = "0") int offset,
                                     @RequestParam(value = "limit", defaultValue = "10") int limit) {

        List<Article> articles = articleService.findArticles(offset, limit);
        List<ArticleDto> articleDtos = articles.stream()
                .map(a -> new ArticleDto(a))
                .collect(Collectors.toList());
        return articleDtos;
    }

    @PostMapping("api/article")
    public CreateArticleResponse saveArticle(@RequestBody @Valid CreateArticleRequest request) {
        Long articleId = articleService.write(request.memberId, request.subject, request.content);
        return new CreateArticleResponse(articleId);
    }

    @Data
    @AllArgsConstructor
    static class CreateArticleResponse {
        private Long id;
    }

    @Data
    static class CreateArticleRequest {
//        @NotEmpty 지원되는 타입에 Long은 없음(default value가 있기때문인거같다)
        private Long memberId;
        @NotEmpty
        private String subject;
        private String content;
    }

}
