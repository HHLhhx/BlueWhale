package com.seecoder.BlueWhale;

import com.seecoder.BlueWhale.po.Comment;
import com.seecoder.BlueWhale.po.User;
import com.seecoder.BlueWhale.repository.CommentRepository;
import com.seecoder.BlueWhale.repository.UserRepository;
import com.seecoder.BlueWhale.service.CommentService;
import com.seecoder.BlueWhale.util.SecurityUtil;
import com.seecoder.BlueWhale.vo.CommentVO;
import org.apache.commons.compress.utils.Lists;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.util.*;

import static org.hamcrest.core.IsInstanceOf.any;
import static org.mockito.Mockito.*;

@ExtendWith(SpringExtension.class)
@SpringBootTest
@AutoConfigureMockMvc
public class CommentTest {
    @Autowired
    private CommentService commentService;

    @MockBean
    private CommentRepository commentRepository;

    @MockBean
    private UserRepository userRepository;
    @MockBean
    private SecurityUtil securityUtil;

    private Comment newComment(Integer id, String text) {
        Comment comment = new Comment();
        comment.setCreateTime(new Date());
        comment.setStoreId(0);
        comment.setOrderId(0);
        comment.setUserId(0);
        comment.setRating(0.0);
        comment.setId(id);
        comment.setText(text);
        return comment;
    }

    @Test
    public void testCommentOther() throws Exception {
        CommentVO comment = new CommentVO();
        comment.setUserId(1);
        comment.setCommentOnId(2);
        comment.setOrderId(null);
        comment.setStoreId(null);
        comment.setCreateTime(new Date());

        when(commentRepository.findById(2)).thenReturn(java.util.Optional.of(new Comment()));
        when(securityUtil.getCurrentUser()).thenReturn(new User());

        Boolean result = commentService.commentOther(comment, 2);
        Assertions.assertTrue(result);
    }

    @Test
    public void testGetCommentsOn() {
        List<Comment> comments = new ArrayList<>();
        comments.add(newComment(1, "Hello"));

        when(userRepository.findById(0)).thenReturn(Optional.of(new User()));
        when(commentRepository.findAllByCommentOnId(1)).thenReturn(comments);

        List<CommentVO> result = commentService.getCommentsOn(1);

        verify(commentRepository, times(1)).findAllByCommentOnId(1);
        Assertions.assertEquals(1, result.size());
        Assertions.assertEquals("Hello", result.get(0).getText());
    }

    @Test
    public void findComments() {
        when(commentRepository.findAllByCommentOnId(0)).thenReturn(Arrays.asList(
                newComment(1, "1"),
                newComment(3, "3"),
                newComment(4, "4"),
                newComment(2, "2")
        ));

        when(commentRepository.findAllByCommentOnId(1)).thenReturn(Arrays.asList(
                newComment(5, "5"),
                newComment(6, "6"),
                newComment(7, "7"),
                newComment(8, "8")
        ));

        when(userRepository.findById(0)).thenReturn(Optional.of(new User()));

        List<CommentVO> result = commentService.getAllCommentAttached(0);
        List<CommentVO> result1 = commentService.getAllCommentAttached(1);

        verify(commentRepository, times(1)).findAllByCommentOnId(0);
        verify(commentRepository, times(2)).findAllByCommentOnId(1);

        Assertions.assertEquals(8, result.size());
        Assertions.assertEquals(4, result1.size());
    }
}
