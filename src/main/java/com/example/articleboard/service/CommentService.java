package com.example.articleboard.service;

import com.example.articleboard.domain.Article;
import com.example.articleboard.domain.Comment;
import com.example.articleboard.domain.Member;
import com.example.articleboard.repository.ArticleRepository;
import com.example.articleboard.repository.CommentRepository;
import com.example.articleboard.repository.MemberRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

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
     * 대댓글이 있거나 멤버가 태그된 댓글(대대댓글)은 데이터가 삭제되지 않고 isDeleted에 표시
     * @param commentId
     * @return commentId
     */
    @Transactional
    public Long deleteComment(Long commentId) {
        Comment comment = commentRepository.findOne(commentId);

        if(comment == null) {
            return null;
        }

        if (comment.getChildComments().isEmpty() && !isTaggedMemberComment(comment)) {
            commentRepository.delete(comment);
        } else {
            comment.deleteComment();
        }
        return commentId;
    }

    /**
     * 해당 댓글의 작성자가 태그된지 확인
     * @param comment
     * @return
     */
    private boolean isTaggedMemberComment(Comment comment) {
        Comment parentComment = comment.getParentComment();

        if (parentComment == null) {
            return false;
        }
        return parentComment.getChildComments().stream().anyMatch(c -> c.getTagMember() == comment.getMember());
    }
}
