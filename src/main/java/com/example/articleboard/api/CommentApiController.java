package com.example.articleboard.api;

import com.example.articleboard.domain.Comment;
import com.example.articleboard.dto.CommentDto;
import com.example.articleboard.env.UriConfig;
import com.example.articleboard.service.CommentService;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.lang.Nullable;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import java.security.Principal;
import java.util.List;
import java.util.stream.Collectors;

@RestController(UriConfig.API_BASE)
@RequiredArgsConstructor
@Slf4j
public class CommentApiController {

    private final CommentService commentService;

    @PostMapping(UriConfig.Comment.COMMENTS)
    @ResponseBody
    @PreAuthorize("#request.memberId.toString().equals(authentication.name)")
    public CreateCommentResponse saveComment(@RequestBody @Valid CreateCommentRequest request) {
        Long commentId = commentService.write(request.getMemberId(), request.getArticleId(), request.getParentId(), request.getTagMemberId(), request.getContent());
        return new CreateCommentResponse(commentId);
    }

    @PutMapping(UriConfig.Comment.COMMENT_ID)
    @ResponseBody
    public UpdateCommentResponse updateComment(@PathVariable("commentId") Long commentId, @RequestBody @Valid UpdateCommentRequest request) {
        Long id = commentService.updateComment(commentId, request.getContent());
        return new UpdateCommentResponse(id);
    }

    @DeleteMapping(UriConfig.Comment.COMMENT_ID)
    @ResponseBody
    public DeleteCommentResponse deleteComment(Principal principal, @PathVariable("commentId") Long commentId) {
        Long memberId = Long.parseLong(principal.getName());
        Long id = commentService.deleteComment(memberId, commentId);
        return new DeleteCommentResponse(id);
    }

    @GetMapping(UriConfig.Comment.MEMBER_COMMENTS)
    public List<CommentDto> commentsMember(@PathVariable("memberId") Long memberId,
                                           @RequestParam(value = "offset", defaultValue = "0") int offset,
                                           @RequestParam(value = "limit", defaultValue = "10") int limit) {

        List<Comment> memberComments = commentService.findCommentByMemberId(memberId, offset, limit);
        List<CommentDto> result = memberComments.stream()
                .map(c -> new CommentDto(c))
                .collect(Collectors.toList());

        return result;
    }

    @Data
    @AllArgsConstructor
    static class CreateCommentResponse {
        private Long commentId;
    }

    @Data
    @Getter
    static class CreateCommentRequest {
        @NotNull
        private Long memberId;
        @NotNull
        private Long articleId;
        @Nullable
        private Long parentId;
        @Nullable
        private Long tagMemberId;
        @NotEmpty
        private String content;
    }

    @Data
    @AllArgsConstructor
    static class UpdateCommentResponse {
        private Long id;
    }

    @Data
    static class UpdateCommentRequest {
        @NotEmpty
        private String content;
    }

    @Data
    @AllArgsConstructor
    static class DeleteCommentResponse {
        private Long id;
    }
}
