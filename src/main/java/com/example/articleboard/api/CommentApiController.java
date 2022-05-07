package com.example.articleboard.api;

import com.example.articleboard.domain.Comment;
import com.example.articleboard.service.CommentService;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.lang.NonNull;
import org.springframework.lang.Nullable;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import javax.validation.constraints.NotEmpty;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequiredArgsConstructor
public class CommentApiController {

    private final CommentService commentService;

    @Data
    static class CommentDto {
        private String memberNick;
        private String articleSubject;
        private String tagMemberNick;
        private String content;


        public CommentDto(Comment comment) {
            memberNick = comment.getMember().getNick();
            content = comment.getContent();
            articleSubject = comment.getArticle().getSubject();
            if (comment.getTagMember() != null) {
                tagMemberNick = comment.getTagMember().getNick();
            }
        }
    }

    @GetMapping("api/comments/member/{memberId}")
    public List<CommentDto> commentsMember(@PathVariable("memberId") Long memberId,
                                           @RequestParam(value = "offset", defaultValue = "0") int offset,
                                           @RequestParam(value = "limit", defaultValue = "10") int limit) {

        List<Comment> memberComments = commentService.findCommentByMemberId(memberId, offset, limit);
        List<CommentDto> result = memberComments.stream()
                .map(c -> new CommentDto(c))
                .collect(Collectors.toList());

        return result;
    }

    @PostMapping("api/comment")
    public CreateCommentResponse saveComment(@RequestBody @Valid CreateCommentRequest request) {
        Long commentId = commentService.write(request.getMemberId(), request.getArticleId(), request.getParentId(), request.getTagMemberId(), request.getContent());
        return new CreateCommentResponse(commentId);
    }


    @Data
    @AllArgsConstructor
    static class CreateCommentResponse {
        private Long commentId;
    }

    @Data
    @Getter
    static class CreateCommentRequest {
        @NonNull
        private Long memberId;
        @NonNull
        private Long articleId;
        @Nullable
        private Long parentId;
        @Nullable
        private Long tagMemberId;
        @NotEmpty
        private String content;
    }
}
