import { useDroppable } from "@dnd-kit/core";
import TaskCard from "./TaskCard";

function Column({
  column,
  tasks,
  activeColumnId,
  setActiveColumnId,
  newTaskTitle,
  setNewTaskTitle,
  handleAddTask,
  onTaskClick,
}) {
  const { setNodeRef, isOver } = useDroppable({
    id: column.id.toString(),
  });

  return (
    <div
      ref={setNodeRef}
      className={`bg-white rounded-lg shadow w-72 flex-shrink-0 p-4 transition ${
        isOver ? "ring-2 ring-blue-400" : ""
      }`}
    >
      <h2 className="font-semibold text-lg mb-3">{column.name}</h2>

      <div className="flex flex-col gap-2 mb-3 min-h-[20px]">
        {tasks?.map((task) => (
          <TaskCard key={task.id} task={task} onClick={onTaskClick} />
        ))}
      </div>

      {activeColumnId === column.id ? (
        <div className="flex flex-col gap-2">
          <input
            type="text"
            autoFocus
            value={newTaskTitle}
            onChange={(e) => setNewTaskTitle(e.target.value)}
            placeholder="Task title"
            className="border rounded px-2 py-1 text-sm"
            onKeyDown={(e) => {
              if (e.key === "Enter") handleAddTask(column.id);
            }}
          />
          <div className="flex gap-2">
            <button
              onClick={() => handleAddTask(column.id)}
              className="bg-blue-600 text-white text-sm px-3 py-1 rounded hover:bg-blue-700"
            >
              Add
            </button>
            <button
              onClick={() => setActiveColumnId(null)}
              className="text-sm text-gray-500 hover:underline"
            >
              Cancel
            </button>
          </div>
        </div>
      ) : (
        <button
          onClick={() => setActiveColumnId(column.id)}
          className="text-sm text-blue-600 hover:underline"
        >
          + Add Task
        </button>
      )}
    </div>
  );
}

export default Column;