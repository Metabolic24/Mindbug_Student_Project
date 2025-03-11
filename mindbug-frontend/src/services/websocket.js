import { Client } from '@stomp/stompjs';
import SockJS from 'sockjs-client';

class WebSocketService {
  constructor() {
    this.client = null;
    this.subscriptions = new Map();
    this.reconnectDelay = 5000;
    this.maxRetry = 3;
    this.retryCount = 0;
  }

  connectToQueue(onMatch) {
    if (this.client?.connected) {
      console.log('WebSocket connected');
      this.ensureSubscription(onMatch);
      return;
    }

    const socket = new SockJS('http://localhost:8080/mindbug-ws');
    this.client = new Client({
      webSocketFactory: () => socket,
      reconnectDelay: this.reconnectDelay,
      debug: (str) => console.log('[STOMP]', str),
      onWebSocketClose: () => {
        console.warn('WebSocket closed, trying to reconnect.. ');
        if (this.retryCount < this.maxRetry) {
          this.retryCount++;
          setTimeout(() => this.connectToQueue(onMatch), this.reconnectDelay);
        }
      },
    });

    this.client.onConnect = () => {
      this.retryCount = 0;
      console.log('✅ WebSocket Connected');

      this.ensureSubscription(onMatch);
    };

    this.client.activate();
  }

  ensureSubscription(onMatch) {
    setTimeout(() => {
      if (!this.subscriptions.has('queue')) {
        console.log('🔄 In the queue...');
        const sub = this.client.subscribe(
          '/topic/game-queue',
          (message) => this.handleQueueMessage(message, onMatch)
        );
        this.subscriptions.set('queue', sub);
      }
    }, 500);
  }

  handleQueueMessage(message, onMatch) {
    try {
      console.log('Message body:', message.body);
      const rawData = JSON.parse(message.body);
      let gameId = rawData.gameId || rawData.data?.gameId || rawData.matchData?.gameId || rawData.id;

      if (gameId) {
        console.log('Game ID:', gameId);
        onMatch({ gameId });
      }
    } catch (error) {
      console.error('❌ JSON parse failed:', error);
    }
  }

  subscribeToGameState(gameId, onGameStateReceived) {
    const topic = `/topic/game/${gameId}`;
    if (!this.subscriptions.has(topic)) {
      const sub = this.client.subscribe(
        topic,
        (message) => this.handleGameStateMessage(message, onGameStateReceived)
      );
      this.subscriptions.set(topic, sub);
    }
  }

  handleGameStateMessage(message, onGameStateReceived) {
    try {
      const websocketMsg = JSON.parse(message.body);
      let messageID = websocketMsg.messageID;
      // Message type 
      switch(messageID){
        case "newGame" :
          this.onNewGame(websocketMsg,onGameStateReceived);
          break;
        default:
          break;
        //other message type to implement
      }
    } catch (error) {
      console.error('❌ Error parsing game state:', error);
    }
  }

  onNewGame(data,onGameStateReceived){
    if (data.gameId) {
      if (data.player1 && data.player2) {
        onGameStateReceived(data);
      }
    }
  }

}

export default new WebSocketService();
