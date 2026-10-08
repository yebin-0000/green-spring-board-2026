package com.green.spring_board.service;

import com.green.spring_board.dto.CommentCreateRequest;
import com.green.spring_board.dto.CommentResponse;
import com.green.spring_board.dto.CommentUpdateRequest;
import com.green.spring_board.entity.Board;
import com.green.spring_board.entity.Comment;
import com.green.spring_board.entity.User;
import com.green.spring_board.exceptions.ResourceNotFoundException;
import com.green.spring_board.repository.BoardRepository;
import com.green.spring_board.repository.CommentRepository;
import com.green.spring_board.repository.UserRepository;
import jakarta.transaction.Transactional;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
@AllArgsConstructor
public class CommentService {
    private CommentRepository commentRepository;
    private BoardRepository boardRepository;
    private UserRepository userRepository;

    // 댓글 작성
    public void createComment(CommentCreateRequest commentCreateRequest,
                              int userId,
                             int boardId

    ) {
        // 어떤 게시글에 달리는지
        Board board = boardRepository.findById(boardId)
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 게시글입니다"));

        // 누가 다는지
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("없는 사용자입니다"));

        // 생성된 값 돌려받기
        Comment comment = new Comment();
        comment.setContent(commentCreateRequest.getContent());
        comment.setBoard(board);
        comment.setUser(user);

        // DB에 저장하기
        commentRepository.save(comment);

    }
    public List<CommentResponse> readComments(int boardId) {
        if (!boardRepository.existsById(boardId)) {
            throw new ResourceNotFoundException("Board not found");
        }

        List<Comment> comments = commentRepository.findByBoardId(boardId);

        // 가져온 댓글을 CommentResponse로 변환
        List<CommentResponse> commentResponses = new ArrayList<>();
        for (Comment comment : comments) {
            CommentResponse commentResponse =new CommentResponse();
            commentResponse .setCommentId(comment.getId());
            commentResponse.setContent(comment.getContent());
            commentResponse.setNickname(comment.getUser().getNickname());
            commentResponse.setCommentDate(comment.getCreatedDatetime());

            commentResponses.add(commentResponse);
        }
        return commentResponses;
    }
    @Transactional
    public void updateComment(int id, CommentUpdateRequest commentUpdateRequest, int userId) {
        //댓글 존재 확인
        Comment comment = commentRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 댓글입니다"));

        // 이미 삭제된 댓글인지 확인
        if (comment.isDeleted()) {
            throw new ResourceNotFoundException("이미 삭제된 댓글입니다");
        }

        // 댓글 작성자, 현재 사용자 일치 확인
        if (comment.getUser().getId() != userId) {
            throw new IllegalStateException("댓글을 수정할 권한이 없습니다");
        }
        comment.setContent(commentUpdateRequest.getContent());
    }

    public void deleteComment(int id, int userId) {
        Comment comment = commentRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 댓글입니다"));

        if (comment.isDeleted()) {
            throw new ResourceNotFoundException("이미 삭제된 댓글입니다");
        }

        if (comment.getUser().getId() != userId) {
            throw new IllegalStateException("댓글을 삭제할 권한이 없습니다");
        }
        comment.setDeleted(true);
        commentRepository.delete(comment);
    }
}
