package com.example.articleboard.api;

import com.example.articleboard.domain.Comment;
import com.example.articleboard.dto.comment.*;
import com.example.articleboard.env.UriConfig;
import com.example.articleboard.service.CommentService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.security.Principal;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping(UriConfig.Comment.BASE)
@RequiredArgsConstructor
@Slf4j
public class CommentApiController {

    private final CommentService commentService;

    @PostMapping("/")
    @ResponseBody
    @PreAuthorize("#request.memberId.toString().equals(authentication.name)")
    public CreateCommentResponse saveComment(@RequestBody @Valid CreateCommentRequest request) {
        Long commentId = commentService.write(request.getMemberId(), request.getArticleId(), request.getParentId(), request.getTagMemberId(), request.getContent());
        return new CreateCommentResponse(commentId);
    }

    @PutMapping("/")
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
}
