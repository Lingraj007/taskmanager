import api from "./axios";

export const getCommentsByTask = (taskId) =>
  api.get(`/comments/task/${taskId}`);
export const addComment = (data) => api.post("/comments", data);
export const deleteComment = (id) => api.delete(`/comments/${id}`);