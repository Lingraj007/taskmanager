import api from "./axios";

export const getBoardByProject = (projectId) =>
  api.get(`/boards/project/${projectId}`);
export const getTasksByColumn = (columnId) =>
  api.get(`/tasks/column/${columnId}`);
export const createTask = (data) => api.post("/tasks", data);
export const moveTask = (taskId, data) =>
  api.patch(`/tasks/${taskId}/move`, data);
export const deleteTask = (taskId) => api.delete(`/tasks/${taskId}`);