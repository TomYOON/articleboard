package com.example.articleboard.api;

import com.example.articleboard.domain.Article;
import com.example.articleboard.dto.article.*;
import com.example.articleboard.env.UriConfig;
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

@RestController(UriConfig.API_BASE)
@RequiredArgsConstructor
public class ArticleApiController {

    private final ArticleService articleService;
    final private JwtTokenUtils jwtTokenUtils;

    @GetMapping(UriConfig.Article.ARTICLES)
    public ResponseEntity<List<ArticleDto>> articles(@RequestParam(value = "offset", defaultValue = "0") int offset,
                                                    @RequestParam(value = "limit", defaultValue = "10") int limit) {

        List<Article> articles = articleService.findArticles(offset, limit);
        List<ArticleDto> articleDtos = articles.stream()
                .map(a -> new ArticleDto(a))
                .collect(Collectors.toList());
        return ResponseEntity.ok().body(articleDtos);
    }

    @PostMapping(UriConfig.Article.ARTICLES)
    @PreAuthorize("#request.memberId.toString().equals(authentication.name)")
    public ResponseEntity<CreateArticleResponseDto> saveArticle(@RequestBody @Valid CreateArticleRequestDto request) {
        Long articleId = articleService.write(request.getMemberId(), request.getSubject(), request.getContent());
        return ResponseEntity.ok().body(new CreateArticleResponseDto(articleId));
    }

    @GetMapping(UriConfig.Article.ARTICLE_ID)
    @ResponseBody
    public ResponseEntity<ArticleDto> detailArticle(@PathVariable("articleId") Long articleId) {
        Article article = articleService.getArticleDetail(articleId);
        if (article == null) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "게시글을 찾을 수 없습니다.");
        }
        return ResponseEntity.ok().body(new ArticleDto(article));
    }

    @PutMapping(UriConfig.Article.ARTICLE_ID)
    @ResponseBody
    public ResponseEntity<UpdateArticleResponseDto> updateArticle(@PathVariable("articleId") Long articleId,
                                                                  @RequestBody @Valid UpdateArticleRequestDto request,
                                                                  Principal principal) {
        Long memberId = Long.parseLong(principal.getName());
        Long id = articleService.updateArticle(articleId, memberId, request.getSubject(), request.getContent());
        if (id == null) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "게시글을 찾을 수 없습니다.");
        }
        return ResponseEntity.ok().body(new UpdateArticleResponseDto(id));
    }

    @GetMapping(UriConfig.Article.MEMBER_ARTICLES)
    public ResponseEntity<List<ArticleDto>> getMemberArticles(@PathVariable("memberId") Long memberId,
                                              @RequestParam(value = "offset", defaultValue = "0") int offset,
                                              @RequestParam(value = "limit", defaultValue = "10") int limit) {
        List<Article> articles = articleService.findMemberArticles(memberId, offset, limit);
        List<ArticleDto> articleDtos = articles.stream()
                .map(a -> new ArticleDto(a))
                .collect(Collectors.toList());
        return ResponseEntity.ok().body(articleDtos);
    }

}
