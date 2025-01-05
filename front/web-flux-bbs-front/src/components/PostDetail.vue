<template>
  <div>
    <h2>{{ post.title }}</h2>
    <p>{{ post.content }}</p>

    <!-- 댓글 입력 -->
    <div>
      <textarea
          v-model="newComment"
          placeholder="댓글을 입력하세요"></textarea>
      <button @click="addComment(null)">댓글 작성</button>
    </div>

    <!-- 댓글 목록 -->
    <div class="comments" v-if="comments.length > 0">
      <h3>댓글</h3>
      <ul>
        <comment-item
            v-for="comment in comments"
            :key="comment.id"
            :comment="comment"
            @refresh="fetchComments" />
      </ul>
    </div>
  </div>
</template>

<script>
import CommentItem from './CommentItem.vue';
import {apiClient} from "../services/api.js"; // 계층형 댓글 컴포넌트

export default {
  components: {
    CommentItem
  },
  data() {
    return {
      post: {}, // 게시물 데이터
      comments: [], // 댓글 목록
      newComment: "", // 새로운 댓글 텍스트
    };
  },
  methods: {
    async fetchPost() {
      try {
        // 게시물 상세 데이터 불러오기
        const response = await apiClient.get(`/posts/${this.$route.params.id}`);
        this.post = response.data;
      } catch (error) {
        console.error("게시물을 불러오는 중 오류 발생:", error);
      }
    },
    async fetchComments() {
      try {
        // 댓글 데이터 불러오기
        const response = await apiClient.get(`/posts/${this.$route.params.id}/comments`);
        this.comments = response.data;
      } catch (error) {
        console.error("댓글을 불러오는 중 오류 발생:", error);
      }
    },
    async addComment(parentId) {
      if (!this.newComment.trim()) {
        alert("댓글 내용을 입력하세요.");
        return;
      }
      try {
        // 댓글 추가 API 호출
        await apiClient.post(`/posts/${this.$route.params.id}/comments`, {
          content: this.newComment,
          parentId: parentId, // 대댓글인 경우 부모 ID 설정, 아니면 null
        });
        this.newComment = ""; // 입력값 초기화
        await this.fetchComments(); // 댓글 새로고침
      } catch (error) {
        console.error("댓글 작성 중 오류 발생:", error);
      }
    },
  },
  async created() {
    await this.fetchPost(); // 게시물 데이터 불러오기
    await this.fetchComments(); // 댓글 목록 불러오기
  },
};
</script>