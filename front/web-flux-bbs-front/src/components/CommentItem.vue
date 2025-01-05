<template>
  <li class="list-group-item">
    <div class="d-flex align-items-start">
      <div>
        <strong class="text-primary">{{ comment.author }}</strong>
        <span class="text-muted small"> - {{ comment.date }}</span>
      </div>
    </div>
    <p class="mb-2">{{ comment.content }}</p>

    <!-- 답글 추가 버튼 (대댓글) -->
    <button class="btn btn-link btn-sm p-0" @click="replyMode = !replyMode">답글</button>

    <!-- 답글 입력 -->
    <div v-if="replyMode" class="mt-2">
      <textarea
          v-model="replyText"
          class="form-control mb-1"
          rows="2"
          placeholder="답글을 입력하세요"></textarea>
      <button class="btn btn-sm btn-primary" @click="addReply">답글 작성</button>
    </div>

    <!-- 자식 댓글 -->
    <ul v-if="comment.children && comment.children.length" class="list-group mt-3">
      <comment-item
          v-for="child in comment.children"
          :key="child.id"
          :comment="child"
          @refresh="$emit('refresh')" />
    </ul>
  </li>
</template>

<script>
import {apiClient} from "../services/api.js";

export default {
  props: {
    comment: { type: Object, required: true },
  },
  data() {
    return {
      replyMode: false,
      replyText: "",
    };
  },
  methods: {
    async addReply() {
      if (!this.replyText.trim()) {
        alert("답글 내용을 입력하세요.");
        return;
      }
      try {
        await apiClient.post(`/posts/${this.comment.postId}/comments`, {
          content: this.replyText,
          parentId: this.comment.id,
        });
        this.replyText = "";
        this.replyMode = false;
        this.$emit("refresh");
      } catch (error) {
        console.error("답글 작성 실패:", error);
      }
    },
  },
};
</script>