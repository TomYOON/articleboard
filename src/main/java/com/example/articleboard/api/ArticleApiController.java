package com.example.articleboard.api;

import com.example.articleboard.domain.Article;
import com.example.articleboard.dto.ArticleDto;
import com.example.articleboard.security.JwtTokenUtils;
import com.example.articleboard.service.ArticleService;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import javax.validation.Valid;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import java.security.Principal;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequiredArgsConstructor
public class ArticleApiController {

    private final ArticleService articleService;
    final private JwtTokenUtils jwtTokenUtils;

    @GetMapping("api/articles")
    public ResponseEntity<List<ArticleDto>> articles(@RequestParam(value = "offset", defaultValue = "0") int offset,
                                                    @RequestParam(value = "limit", defaultValue = "10") int limit) {

        List<Article> articles = articleService.findArticles(offset, limit);
        List<ArticleDto> articleDtos = articles.stream()
                .map(a -> new ArticleDto(a))
                .collect(Collectors.toList());
        return ResponseEntity.ok().body(articleDtos);
    }

    @PostMapping("api/article")
    @PreAuthorize("#request.memberId.toString().equals(authentication.name)")
    public ResponseEntity<CreateArticleResponse> saveArticle(@RequestBody @Valid CreateArticleRequest request) {
        Long articleId = articleService.write(request.memberId, request.subject, request.content);
        return ResponseEntity.ok().body(new CreateArticleResponse(articleId));
    }

    @GetMapping("api/article/{articleId}")
    @ResponseBody
    public ResponseEntity<ArticleDto> detailArticle(@PathVariable("articleId") Long articleId) {
        Article article = articleService.getArticleDetail(articleId);
        if (article == null) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "게시글을 찾을 수 없습니다.");
        }
        return ResponseEntity.ok().body(new ArticleDto(article));
    }

    @PutMapping("api/article/{articleId}")
    @ResponseBody
    public ResponseEntity<UpdateArticleResponse> updateArticle(@PathVariable("articleId") Long articleId,
                                                               @RequestBody @Valid UpdateArticleRequest request,
                                                               Principal principal) {
        Long memberId = Long.parseLong(principal.getName());
        Long id = articleService.updateArticle(articleId, memberId, request.getSubject(), request.getContent());
        if (id == null) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "게시글을 찾을 수 없습니다.");
        }
        return ResponseEntity.ok().body(new UpdateArticleResponse(id));
    }

    @GetMapping("api/articles/member/{memberId}")
    public ResponseEntity<List<ArticleDto>> getMemberArticles(@PathVariable("memberId") Long memberId,
                                              @RequestParam(value = "offset", defaultValue = "0") int offset,
                                              @RequestParam(value = "limit", defaultValue = "10") int limit) {
        List<Article> articles = articleService.findMemberArticles(memberId, offset, limit);
        List<ArticleDto> articleDtos = articles.stream()
                .map(a -> new ArticleDto(a))
                .collect(Collectors.toList());
        return ResponseEntity.ok().body(articleDtos);
    }

    @Data
    @AllArgsConstructor
    static class CreateArticleResponse {
        private Long id;
    }

    @Data
    static class CreateArticleRequest {
//        @NotEmpty 지원되는 타입에 Long은 없음(default value가 있기때문인거같다)
        @NotNull
        private Long memberId;
        @NotEmpty
        private String subject;
        private String content;
    }

    @Data
    @AllArgsConstructor
    static class UpdateArticleResponse {
        private Long id;
    }

    @Data
    static class UpdateArticleRequest {
        @NotEmpty
        private String subject;
        @NotEmpty
        private String content;
    }
}
