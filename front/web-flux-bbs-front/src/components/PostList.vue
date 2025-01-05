<template>
  <div class="container mt-5">
    <h1 class="mb-4">게시물 리스트</h1>

    <!-- 게시물 리스트 -->
    <div v-if="posts.length === 0 && !loading" class="alert alert-info">
      게시물이 없습니다.
    </div>
    <div v-if="loading" class="text-center">
      <div class="spinner-border" role="status">
        <span class="visually-hidden">로딩 중...</span>
      </div>
    </div>
    <div v-else>
      <div v-for="post in posts" :key="post.id" class="card mb-3 shadow-sm">
        <div class="card-body">
          <h5 class="card-title">
            <router-link :to="{ name: 'PostDetail', params: { id: post.id }, query: { page: currentPage } }">
              {{ post.title }}
            </router-link>
          </h5>
          <p class="card-text">{{ post.content }}</p>
        </div>
      </div>
    </div>

    <!-- 페이지네이션 -->
    <nav aria-label="Page navigation" class="mt-4">
      <ul class="pagination justify-content-center">
        <li class="page-item" :class="{ disabled: currentPage === 0 }">
          <button class="page-link" @click="changePage(currentPage - 1)">이전</button>
        </li>
        <li
            class="page-item"
            v-for="page in totalPages"
            :key="page"
            :class="{ active: currentPage === page - 1 }"
        >
          <button class="page-link" @click="changePage(page - 1)">{{ page }}</button>
        </li>
        <li class="page-item" :class="{ disabled: currentPage === totalPages - 1 }">
          <button class="page-link" @click="changePage(currentPage + 1)">다음</button>
        </li>
      </ul>
    </nav>
  </div>
</template>

<script>
import { apiClient } from "../services/api.js";

export default {
  data() {
    return {
      posts: [], // 현재 페이지의 게시물 리스트
      pageSize: 5, // 페이지 크기
      totalItems: 0, // 전체 게시물 수
      loading: false, // 로딩 상태
    };
  },
  computed: {
    currentPage() {
      // URL 쿼리에서 현재 페이지를 가져옴. 기본값은 0.
      return Number(this.$route.query.page || 0);
    },
    totalPages() {
      return Math.ceil(this.totalItems / this.pageSize); // 전체 페이지 수 계산
    },
  },
  methods: {
    async fetchPosts() {
      this.loading = true;
      try {
        const response = await apiClient.get("/posts", {
          params: {
            page: this.currentPage,
            size: this.pageSize,
          },
        });
        this.posts = response.data.content;
        this.totalItems = response.data.totalElements;
      } catch (error) {
        alert("게시물 로드 중 오류가 발생했습니다.");
        console.error(error);
      } finally {
        this.loading = false;
      }
    },
    changePage(page) {
      if (page >= 0 && page < this.totalPages) {
        // 페이지 변경 시 URL 쿼리를 업데이트
        this.$router.push({ query: { page } });
        this.fetchPosts();
      }
    },
  },
  watch: {
    // URL 쿼리가 변경되면 게시물 재로드
    "$route.query.page": "fetchPosts",
  },
  mounted() {
    this.fetchPosts();
  },
};
</script>

<style scoped>
.container {
  max-width: 800px;
}
</style>