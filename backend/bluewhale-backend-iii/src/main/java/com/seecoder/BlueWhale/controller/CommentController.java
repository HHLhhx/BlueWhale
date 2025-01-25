package com.seecoder.BlueWhale.controller;

import com.seecoder.BlueWhale.service.CommentService;
import com.seecoder.BlueWhale.vo.CommentVO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import com.seecoder.BlueWhale.vo.ResultVO;

import java.util.List;

@RestController
@RequestMapping("/api/comments")
public class CommentController {

    @Autowired
    CommentService commentService;


    @PostMapping("/commentOther/{id}")
    public ResultVO<Boolean> commentOther(@RequestBody CommentVO comment, @PathVariable("id") Integer commentedId) throws Exception {
        return ResultVO.buildSuccess(commentService.commentOther(comment, commentedId));
    }

    @PostMapping("/{id}")
    public ResultVO<List<CommentVO>> getAllCommentAttached(@PathVariable("id") Integer commentedId) {
        return ResultVO.buildSuccess(commentService.getAllCommentAttached(commentedId));
    }

    @GetMapping("/getCommentsOn/{id}")
    public ResultVO<List<CommentVO>> getCommentsOn(@PathVariable("id") Integer id) {
        return ResultVO.buildSuccess(commentService.getCommentsOn(id));
    }

    @GetMapping("/{id}")
    public ResultVO<CommentVO> getComment(@PathVariable("id") Integer id) throws Exception{
        return ResultVO.buildSuccess(commentService.getComment(id));
    }
}