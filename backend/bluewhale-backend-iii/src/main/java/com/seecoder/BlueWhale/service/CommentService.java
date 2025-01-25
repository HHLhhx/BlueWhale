package com.seecoder.BlueWhale.service;

import com.seecoder.BlueWhale.vo.CommentVO;

import java.util.List;

public interface CommentService {
    Boolean commentOther(CommentVO comment, Integer commentedId) throws Exception;

    List<CommentVO> getCommentsOn(Integer id);

    List<CommentVO> getAllCommentAttached(Integer commentedId);

    CommentVO getComment(Integer id) throws Exception;
}
