import { useDraggable } from "@dnd-kit/core";
import { useRef } from "react";

function TaskCard({ task, onClick }) {
  const { attributes, listeners, setNodeRef, transform, isDragging } =
    useDraggable({
      id: task.id.toString(),
      data: { task },
    });

  const pointerDownPos = useRef({ x: 0, y: 0 });

  const style = transform
    ? {
        transform: `translate(${transform.x}px, ${transform.y}px)`,
        zIndex: 50,
      }
    : undefined;

  const handlePointerDown = (e) => {
    pointerDownPos.current = { x: e.clientX, y: e.clientY };
    if (listeners.onPointerDown) {
      listeners.onPointerDown(e);
    }
  };

  const handlePointerUp = (e) => {
    const dx = Math.abs(e.clientX - pointerDownPos.current.x);
    const dy = Math.abs(e.clientY - pointerDownPos.current.y);
    // If the pointer barely moved, treat it as a click, not a drag
    if (dx < 5 && dy < 5) {
      onClick(task);
    }
  };

  return (
    <div
      ref={setNodeRef}
      style={style}
      {...attributes}
      {...listeners}
      onPointerDown={handlePointerDown}
      onPointerUp={handlePointerUp}
      className={`bg-gray-50 border rounded p-3 shadow-sm cursor-grab active:cursor-grabbing ${
        isDragging ? "opacity-50" : ""
      }`}
    >
      <p className="font-medium">{task.title}</p>
      {task.priority && (
        <span
          className={`text-xs px-2 py-1 rounded mt-1 inline-block ${
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
    </div>
  );
}

export default TaskCard;