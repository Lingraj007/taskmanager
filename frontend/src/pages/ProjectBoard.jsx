import { useEffect, useState, useRef } from "react";
import { useParams, useNavigate } from "react-router-dom";
import { DndContext, closestCenter } from "@dnd-kit/core";
import {
  getBoardByProject,
  getTasksByColumn,
  createTask,
  moveTask,
} from "../api/boardApi";
import { connectSocket } from "../api/socket";
import Column from "../components/Column";
import TaskDetailModal from "../components/TaskDetailModal";

function ProjectBoard() {
  const { projectId } = useParams();
  const navigate = useNavigate();

  const [board, setBoard] = useState(null);
  const [tasksByColumn, setTasksByColumn] = useState({});
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState("");

  const [activeColumnId, setActiveColumnId] = useState(null);
  const [newTaskTitle, setNewTaskTitle] = useState("");

  const [selectedTask, setSelectedTask] = useState(null);

  const stompClientRef = useRef(null);

  const fetchBoard = async () => {
    try {
      const res = await getBoardByProject(projectId);
      setBoard(res.data);

      const taskMap = {};
      for (const column of res.data.columns) {
        const taskRes = await getTasksByColumn(column.id);
        taskMap[column.id] = taskRes.data;
      }
      setTasksByColumn(taskMap);
    } catch (err) {
      setError("Failed to load board.");
    } finally {
      setLoading(false);
    }
  };

  const handleSocketMessage = (data) => {
    const { eventType, taskId, task } = data;

    setTasksByColumn((prev) => {
      const updated = { ...prev };

      if (eventType === "TASK_CREATED") {
        updated[task.columnId] = [...(updated[task.columnId] || []), task];
      }

      if (eventType === "TASK_MOVED" || eventType === "TASK_UPDATED") {
        for (const colId in updated) {
          updated[colId] = updated[colId].filter((t) => t.id !== task.id);
        }
        updated[task.columnId] = [...(updated[task.columnId] || []), task];
      }

      if (eventType === "TASK_DELETED") {
        for (const colId in updated) {
          updated[colId] = updated[colId].filter((t) => t.id !== taskId);
        }
      }

      return updated;
    });
  };

  useEffect(() => {
    fetchBoard();

    stompClientRef.current = connectSocket(projectId, handleSocketMessage);

    return () => {
      if (stompClientRef.current) {
        stompClientRef.current.deactivate();
      }
    };
  }, [projectId]);

  const handleAddTask = async (columnId) => {
    if (!newTaskTitle.trim()) return;
    try {
      await createTask({ title: newTaskTitle, columnId });
      setNewTaskTitle("");
      setActiveColumnId(null);
    } catch (err) {
      setError("Failed to create task.");
    }
  };

  const handleDragEnd = async (event) => {
    const { active, over } = event;
    if (!over) return;

    const taskId = active.id;
    const targetColumnId = parseInt(over.id);
    const task = active.data.current.task;

    if (task.columnId === targetColumnId) return;

    const targetTasks = tasksByColumn[targetColumnId] || [];
    const newPosition = targetTasks.length;

    setTasksByColumn((prev) => {
      const updated = { ...prev };
      updated[task.columnId] = updated[task.columnId].filter(
        (t) => t.id !== task.id
      );
      updated[targetColumnId] = [
        ...(updated[targetColumnId] || []),
        { ...task, columnId: targetColumnId },
      ];
      return updated;
    });

    try {
      await moveTask(taskId, {
        targetColumnId: targetColumnId,
        newPosition: newPosition,
      });
    } catch (err) {
      setError("Failed to move task.");
      fetchBoard();
    }
  };

  // Fallback in case WebSocket is disconnected when a task is deleted
  const handleTaskDeleted = (taskId) => {
    setTasksByColumn((prev) => {
      const updated = { ...prev };
      for (const colId in updated) {
        updated[colId] = updated[colId].filter((t) => t.id !== taskId);
      }
      return updated;
    });
  };

  if (loading) return <p className="p-8 text-gray-500">Loading board...</p>;
  if (error) return <p className="p-8 text-red-600">{error}</p>;

  return (
    <div className="min-h-screen bg-gray-100 p-8">
      <button
        onClick={() => navigate("/dashboard")}
        className="mb-4 text-blue-600 hover:underline"
      >
        ← Back to Dashboard
      </button>

      <h1 className="text-2xl font-bold mb-6">{board?.name}</h1>

      <DndContext collisionDetection={closestCenter} onDragEnd={handleDragEnd}>
        <div className="flex gap-4 overflow-x-auto">
          {board?.columns.map((column) => (
            <Column
              key={column.id}
              column={column}
              tasks={tasksByColumn[column.id]}
              activeColumnId={activeColumnId}
              setActiveColumnId={setActiveColumnId}
              newTaskTitle={newTaskTitle}
              setNewTaskTitle={setNewTaskTitle}
              handleAddTask={handleAddTask}
              onTaskClick={setSelectedTask}
            />
          ))}
        </div>
      </DndContext>

      {selectedTask && (
        <TaskDetailModal
          task={selectedTask}
          onClose={() => setSelectedTask(null)}
          onTaskDeleted={handleTaskDeleted}
        />
      )}
    </div>
  );
}

export default ProjectBoard;