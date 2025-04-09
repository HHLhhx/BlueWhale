package com.seecoder.BlueWhale.serviceImpl;

import com.seecoder.BlueWhale.exception.BlueWhaleException;
import com.seecoder.BlueWhale.po.Comment;
import com.seecoder.BlueWhale.repository.CommentRepository;
import com.seecoder.BlueWhale.repository.UserRepository;
import com.seecoder.BlueWhale.service.CommentService;
import com.seecoder.BlueWhale.util.SecurityUtil;
import com.seecoder.BlueWhale.vo.CommentVO;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

@Service
public class CommentServiceImpl implements CommentService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private CommentRepository commentRepository;

    @Autowired
    private SecurityUtil securityUtil;

    private static final Logger logger = LoggerFactory.getLogger(CommentServiceImpl.class);

    @Override
    public Boolean commentOther(CommentVO comment, Integer commentedId) throws Exception {
        Comment commentOn = commentRepository.findById(commentedId).orElseThrow(BlueWhaleException::commentNotExist);

        comment.setUserId(securityUtil.getCurrentUser().getId());
        comment.setCommentOnId(commentedId);
        comment.setOrderId(null);
        comment.setStoreId(null);
        comment.setCreateTime(new Date());

        Comment commentPO = comment.toPO();
        commentRepository.save(commentPO);
        logger.info("add comment {} for comment {}", comment.getText(), commentOn.getText());
        return true;
    }

    @Override
    public List<CommentVO> getCommentsOn(Integer id) {
        return commentRepository.findAllByCommentOnId(id).stream().map(Comment::toVO).map(commentVo -> {
            commentVo.setUserName(userRepository.findById(commentVo.getUserId()).get().getName());
            return commentVo;
        }).collect(Collectors.toList());
    }

    @Override
    public List<CommentVO> getAllCommentAttached(Integer commentedId) {
        List<CommentVO> ret = new ArrayList<>();
        CommentVO root = new CommentVO();
        root.setId(commentedId);
        // DFS on a tree, we don't visit identical comment again.
        Stack<CommentVO> toVisit = new Stack<>();
        toVisit.push(root);
        while (!toVisit.isEmpty()) {
            CommentVO current = toVisit.pop();
            if (current != root)
                ret.add(current);
            toVisit.addAll(getCommentsOn(current.getId()));
        }

        // Sort by time.
        ret.sort(Comparator.comparingLong(commentVO -> commentVO.getCreateTime().getTime()));
        return ret;
    }

    @Override
    public CommentVO getComment(Integer id) throws Exception {
        Comment comment = commentRepository.findById(id).orElseThrow(BlueWhaleException::commentNotExist);
        CommentVO commentVO = comment.toVO();
        commentVO.setUserName(userRepository.findById(
                commentVO.getUserId()).get().getName());
        return commentVO;
    }

}