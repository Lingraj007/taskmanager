import { useEffect, useState } from "react";
import { getCommentsByTask, addComment, deleteComment } from "../api/commentApi";
import { deleteTask } from "../api/boardApi";

function TaskDetailModal({ task, onClose, onTaskDeleted }) {
  const [comments, setComments] = useState([]);
  const [newComment, setNewComment] = useState("");
  const [loading, setLoading] = useState(true);
  const [deleting, setDeleting] = useState(false);

  const fetchComments = async () => {
    try {
      const res = await getCommentsByTask(task.id);
      setComments(res.data);
    } catch (err) {
      console.error("Failed to load comments", err);
    } finally {
      setLoading(false);
    }
  };

  useEffect(() => {
    fetchComments();
  }, [task.id]);

  const handleAddComment = async (e) => {
    e.preventDefault();
    if (!newComment.trim()) return;
    try {
      await addComment({ taskId: task.id, content: newComment });
      setNewComment("");
      fetchComments();
    } catch (err) {
      console.error("Failed to add comment", err);
    }
  };

  const handleDeleteComment = async (commentId) => {
    try {
      await deleteComment(commentId);
      fetchComments();
    } catch (err) {
      console.error("Failed to delete comment", err);
    }
  };

  const handleDeleteTask = async () => {
    const confirmed = window.confirm(
      `Delete "${task.title}"? This cannot be undone.`
    );
    if (!confirmed) return;

    setDeleting(true);
    try {
      await deleteTask(task.id);
      onClose();
      // The WebSocket TASK_DELETED event will remove it from the board automatically,
      // but we also call this as a safety fallback in case sockets are disconnected.
      if (onTaskDeleted) onTaskDeleted(task.id);
    } catch (err) {
      console.error("Failed to delete task", err);
      setDeleting(false);
    }
  };

  return (
    <div className="fixed inset-0 bg-black/40 flex items-center justify-center z-50 p-4">
      <div className="bg-white rounded-lg shadow-lg w-full max-w-lg max-h-[85vh] overflow-y-auto">
        <div className="p-5 border-b flex justify-between items-start">
          <div>
            <h2 className="text-xl font-bold">{task.title}</h2>
            {task.description && (
              <p className="text-gray-600 text-sm mt-1">{task.description}</p>
            )}
            <div className="flex gap-2 mt-2">
              {task.priority && (
                <span
                  className={`text-xs px-2 py-1 rounded ${
                    task.priority === "HIGH"
                      ? "bg-red-100 text-red-700"
                      : task.priority === "MEDIUM"
                      ? "bg-yellow-100 text-yellow-700"
                      : "bg-green-100 text-green-700"
                  }`}
                >
                  {task.priority}
                </span>
              )}
              <span className="text-xs px-2 py-1 rounded bg-gray-100 text-gray-600">
                {task.columnName}
              </span>
            </div>
          </div>
          <button
            onClick={onClose}
            className="text-gray-400 hover:text-gray-700 text-xl leading-none"
          >
            ✕
          </button>
        </div>

        <div className="p-5">
          <button
            onClick={handleDeleteTask}
            disabled={deleting}
            className="w-full mb-5 bg-red-50 text-red-600 border border-red-200 py-2 rounded text-sm font-medium hover:bg-red-100 disabled:opacity-50"
          >
            {deleting ? "Deleting..." : "🗑 Delete Task"}
          </button>

          <h3 className="font-semibold mb-3">Comments</h3>

          {loading ? (
            <p className="text-gray-500 text-sm">Loading comments...</p>
          ) : comments.length === 0 ? (
            <p className="text-gray-500 text-sm mb-4">No comments yet.</p>
          ) : (
            <div className="flex flex-col gap-3 mb-4">
              {comments.map((c) => (
                <div key={c.id} className="bg-gray-50 border rounded p-3">
                  <div className="flex justify-between items-start">
                    <p className="font-medium text-sm">{c.userName}</p>
                    {c.userName === localStorage.getItem("name") && (
                      <button
                        onClick={() => handleDeleteComment(c.id)}
                        className="text-xs text-red-500 hover:underline"
                      >
                        Delete
                      </button>
                    )}
                  </div>
                  <p className="text-sm text-gray-700 mt-1">{c.content}</p>
                </div>
              ))}
            </div>
          )}

          <form onSubmit={handleAddComment} className="flex gap-2">
            <input
              type="text"
              value={newComment}
              onChange={(e) => setNewComment(e.target.value)}
              placeholder="Write a comment..."
              className="border rounded px-3 py-2 flex-1 text-sm"
            />
            <button
              type="submit"
              className="bg-blue-600 text-white px-4 py-2 rounded text-sm hover:bg-blue-700"
            >
              Send
            </button>
          </form>
        </div>
      </div>
    </div>
  );
}

export default TaskDetailModal;