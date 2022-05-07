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
}
