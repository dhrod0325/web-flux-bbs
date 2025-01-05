import axios from 'axios';

// Axios 인스턴스 생성
export const apiClient = axios.create({
    baseURL: 'http://localhost:8080/api', // 공통 URL 설정
    headers: {
        'Content-Type': 'application/json',
    },
});

export const getPost = (id) => {
    return apiClient.get(`/posts/${id}`);
};