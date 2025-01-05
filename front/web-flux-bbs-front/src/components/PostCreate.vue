<template>
  <div>
    <input type="text" v-model="post.title" placeholder="제목 입력"/>
    <textarea v-model="post.content" placeholder="내용 입력"></textarea>
    <input type="file" @change="onFileChange"/>
    <button @click="createPost">등록</button>
  </div>
</template>

<script>
import {apiClient} from "../services/api.js";

export default {
  data() {
    return {
      post: {
        title: "",
        content: "",
      },
      file: null, // 파일 업로드를 위한 데이터
    };
  },
  methods: {
    // 파일 선택 처리
    onFileChange(event) {
      const file = event.target.files[0];
      if (file) {
        this.file = file;
      }
    },
    // 게시물 생성
    async createPost() {
      try {
        // 1. 게시물 생성 API 호출
        const postResponse = await apiClient.post("/posts", this.post);
        const createdPost = postResponse.data; // 생성된 게시물 데이터

        alert("게시물이 등록되었습니다.");

        // 2. 파일 업로드 (파일이 선택된 경우)
        if (this.file) {
          const formData = new FormData();
          formData.append("file", this.file);

          await apiClient.post(`/posts/${createdPost.id}/upload`, formData, {
            headers: {
              "Content-Type": "multipart/form-data",
            },
          });

          alert("파일이 업로드되었습니다.");
        }

        // 데이터 초기화
        this.post.title = "";
        this.post.content = "";
        this.file = null;
        this.$emit("refresh"); // 부모 컴포넌트에 새로고침 요청
      } catch (error) {
        alert("게시물 등록 중 오류가 발생했습니다.");
        console.error(error);
      }
    },
  },
};
</script>