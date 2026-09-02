import { Client } from "@stomp/stompjs";
import SockJS from "sockjs-client";

export function connectSocket(projectId, onMessageReceived) {
  const socket = new SockJS("http://localhost:8081/ws");

  const stompClient = new Client({
    webSocketFactory: () => socket,
    reconnectDelay: 5000,
    onConnect: () => {
      stompClient.subscribe(`/topic/project/${projectId}`, (message) => {
        const data = JSON.parse(message.body);
        onMessageReceived(data);
      });
    },
  });

  stompClient.activate();

  return stompClient;
}