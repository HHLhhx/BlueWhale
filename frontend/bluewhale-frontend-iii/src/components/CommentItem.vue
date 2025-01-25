<script setup lang="ts">
    import { ref } from "vue";
    import { commentOn, getCommentById, getCommentsOn } from "../api/comment";
    import { Comment } from "@element-plus/icons-vue";
    import { parseTime } from "../utils";

    const props = defineProps({
        commentId: {
            type: Number,
            required: true,
        },
    });

    //评论详细信息
    const commentVO = ref();
    const userId = ref(0);
    const createTime = ref("");
    const text = ref("");
    const rating = ref(0);
    const userName = ref("");

    initInfo();

    function initInfo() {
        getCommentDetail();
    }

    function getCommentDetail() {
        getCommentById(props.commentId).then((res) => {
            commentVO.value = res.data.result;
            userId.value = res.data.result.userId;
            createTime.value = res.data.result.createTime;
            text.value = res.data.result.text;
            rating.value = res.data.result.rating;
            userName.value = res.data.result.userName;
        });
    }

    //查看评论详情
    const drawer = ref(false);
    function showCommentDetail() {
        getReplyComments();
        drawer.value = !drawer.value;
        replyInput.value = false;
        replyComment.value = ""
    }

    //获取评论回复
    const replyCommentList = ref();
    function getReplyComments() {
        getCommentsOn(props.commentId).then((res) => {
            replyCommentList.value = res.data.result;
        });
    }

    //展示评论输入框
    const replyInput = ref(false);
    const toName = ref("")
    const toId = ref(-1);
    const replyCommentPrefix = ref("");
    function showReplyInput(commentVO: any, event: Event) {
        event.stopPropagation();
        if (commentVO.id === toId.value) {
            toId.value = -1;
            replyInput.value = false;
            return;
        }
        toName.value = commentVO.userName;
        toId.value = commentVO.id;
        replyCommentPrefix.value = "回复 " + toName.value + " : ";
        replyInput.value = true;
        replyComment.value = ""
    }

    //评论回复
    const replyComment = ref("");
    function onDivInput(e: Event) {
        replyComment.value = replyCommentPrefix.value + (e.target as HTMLElement).innerText;
    }

    //发送评论
    function sendCommentReply() {
        if (!replyComment.value) {
            ElMessage({
                message: "评论不能为空",
                type: "warning",
            });
        } else {
            replyInput.value = false
            commentOn(replyComment.value, toId.value)
                .then(() => getReplyComments())
                .catch((error) => console.error(error));
        }
        replyComment.value = ""
    }

    //处理评论详情的关闭
    const handleCloseComment = (done: () => void) => {
        replyInput.value = false;
        done()
    }
</script>

<template>
    <el-card class="comment-item-card" shadow="hover" @click="showCommentDetail()">
        <div class="author-title reply-father">
            <div class="author-info">
                <span class="author-name">{{ userName }}</span>
                <span class="author-time">{{ parseTime(createTime) }}</span>
            </div>
            <div class="icon-btn">
                <span @click="showReplyInput(commentVO, $event)">
                    <el-icon class="iconfont el-icon-s-comment">
                        <Comment />
                    </el-icon>
                </span>
            </div>
            <div class="comment-rating">
                <el-rate v-model="rating" disabled />
            </div>
        </div>
        <div class="talk-box">
            <p>
                <span class="reply">{{ text }}</span>
            </p>
        </div>
        <div v-if="drawer" title="评论详情" :show-close="false" :before-close="handleCloseComment">
            <div>
                <div class="author-title reply-father" @click.stop>
                    <div class="reply-box">
                        <div v-for="replyCommentVO in replyCommentList" class="author-title" :key=replyCommentVO.id>
                            <div class="author-info">
                                <span class="author-name">{{ replyCommentVO.userName }}</span>
                                <span class="author-time">{{ parseTime(replyCommentVO.createTime) }}</span>
                            </div>
                            <div class="icon-btn">
                                <span @click="showReplyInput(replyCommentVO, $event)">
                                    <el-icon class="iconfont el-icon-s-comment">
                                        <Comment />
                                    </el-icon>
                                </span>
                            </div>
                            <div class="talk-box">
                                <p>
                                    <span class="reply">{{ replyCommentVO.text }}</span>
                                </p>
                            </div>
                        </div>
                    </div>
                    <div v-if="replyInput" class="my-reply my-comment-reply">
                        <div class="reply-info">
                            <div tabindex="0" contenteditable="true" spellcheck="false"
                                :placeholder="replyCommentPrefix" @input="onDivInput($event)"
                                class="reply-input reply-comment-input"></div>
                        </div>
                        <div class="reply-btn-box">
                            <el-button class="reply-btn" @click="sendCommentReply()" type="primary">发表评论</el-button>
                        </div>
                    </div>
                </div>
            </div>
        </div>
    </el-card>


</template>

<style scoped>
    .comment-item-card {
        margin: 10px 10%;
        border-radius: 8px;
    }

    .my-reply {
        margin-left: 20px;
        padding: 10px;
        background-color: #fafbfc;
    }

    .my-reply .reply-info {
        display: inline-block;
        margin-left: 5px;
        width: 90%;
    }

    .my-reply .reply-info .reply-input {
        min-height: 20px;
        line-height: 22px;
        padding: 10px;
        color: #ccc;
        background-color: #fff;
        border-radius: 5px;
    }

    .my-reply .reply-info .reply-input:empty:before {
        content: attr(placeholder);
    }

    .my-reply .reply-info .reply-input:focus:before {
        content: none;
    }

    .my-reply .reply-info .reply-input:focus {
        padding: 8px;
        border: 2px solid blue;
        box-shadow: none;
        outline: none;
    }

    .my-reply .reply-btn-box {
        height: 25px;
        margin: 10px 10px;
    }

    .my-reply .reply-btn-box .reply-btn {
        position: relative;
        float: right;
        margin-right: 15px;
    }

    .my-comment-reply {
        margin-left: 20px;
    }

    .my-comment-reply .reply-input {
        width: -webkit-fill-available;
    }

    .author-title:not(:last-child) {
        border-bottom: 1px solid rgba(178, 186, 194, 0.3);
    }

    .author-title {
        padding: 10px;
    }

    .author-title .author-info {
        display: inline-block;
        margin-left: 5px;
        height: 40px;
        line-height: 20px;
    }

    .author-title .author-info>span {
        display: block;
        cursor: pointer;
        overflow: hidden;
        white-space: nowrap;
        text-overflow: ellipsis;
    }

    .author-title .author-info .author-name {
        color: #000;
        font-size: 18px;
        font-weight: bold;
    }

    .author-title .author-info .author-time {
        font-size: 14px;
    }

    .author-title .comment-rating {
        padding: 0 !important;
        float: right;
    }

    .author-title .icon-btn {
        padding: 0 !important;
        float: right;
    }

    .author-title .icon-btn>span {
        cursor: pointer;
    }

    .author-title .icon-btn .iconfont {
        margin: 0 5px;
    }

    .talk-box {
        margin: 5px 20px;
    }

    .talk-box>p {
        word-wrap: break-word;
        word-break: break-all;
        margin: 0;
    }

    .talk-box .reply {
        font-size: 16px;
        color: #000;
    }

    .reply-box {
        margin: 10px 0 0 20px;
        background-color: #efefef;
    }
</style>
