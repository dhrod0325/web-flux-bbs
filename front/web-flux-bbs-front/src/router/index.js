import {createRouter, createWebHistory} from "vue-router";
import PostList from "../components/PostList.vue";
import PostDetail from "../components/PostDetail.vue";
import PostCreate from "../components/PostCreate.vue";

const routes = [
    {
        path: "/", // 글 목록
        name: "PostList",
        component: PostList, // 게시물 리스트 컴포넌트
    },
    {
        path: "/posts/:id", // 글 상세
        name: "PostDetail",
        component: PostDetail, // 게시물 상세보기 컴포넌트
        props: true, // URL의 `id`를 props로 전달
    },
    {
        path: "/create", // 글쓰기
        name: "PostCreate",
        component: PostCreate, // 글쓰기 컴포넌트
    },
];

const router = createRouter({
    history: createWebHistory(),
    routes,
});

export default router;