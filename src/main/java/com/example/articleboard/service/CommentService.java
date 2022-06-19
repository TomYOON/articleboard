package com.example.articleboard.service;

import com.example.articleboard.domain.Article;
import com.example.articleboard.domain.Comment;
import com.example.articleboard.domain.Member;
import com.example.articleboard.repository.ArticleRepository;
import com.example.articleboard.repository.CommentRepository;
import com.example.articleboard.repository.MemberRepository;
import com.example.articleboard.util.AuthUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.ResourceAccessException;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class CommentService {

    private final MemberRepository memberRepository;
    private final ArticleRepository articleRepository;
    private final CommentRepository commentRepository;

    @Transactional
    public Long write(Long memberId, Long articleId, Long parentId, Long tagMemberId, String content) {
        Member member = memberRepository.findOne(memberId);
        Article article = articleRepository.findOne(articleId);
        Comment parentComment = null;
        Member tagMember = null;

        if (parentId != null) {
            parentComment = commentRepository.findOne(parentId);
        }

        if (tagMemberId != null) {
            tagMember = memberRepository.findOne(tagMemberId);
        }

        Comment comment = Comment.createComment(member, article, parentComment, tagMember, content);
        commentRepository.save(comment);

        return comment.getId();
    }

    public List<Comment> findCommentByMemberId(Long memberId, int offset, int limit) {
        return commentRepository.findByMemberId(memberId, offset, limit);
    }

    @Transactional
    public Long updateComment(Long commentId, String content) {
        Comment comment = commentRepository.findOne(commentId);
        comment.updateContent(content);

        return comment.getId();
    }

    /**
     * 대댓글이 있거나 멤버가 태그된 댓글(대대댓글)은 데이터가 삭제되지 않고 content를 삭제 후 isDeleted 표시
     * @param commentId
     * @return commentId
     */
    @Transactional
    public Long deleteComment(Long memberId, Long commentId) {
        Comment comment = commentRepository.findOne(commentId);
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        if(comment == null) {
            return null;
        }

        Member writer = comment.getMember();

        if (!AuthUtils.hasAdminRole(authentication) && writer.getId() != memberId) {
            throw new ResourceAccessException("댓글 삭제 권한이 없습니다.");
        }

        if (comment.isRemovable()) {
            commentRepository.delete(comment);
        } else {
            comment.removeContent();
        }
        return commentId;
    }
}
