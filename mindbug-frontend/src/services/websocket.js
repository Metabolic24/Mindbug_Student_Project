import { Client } from '@stomp/stompjs';
import SockJS from 'sockjs-client';
import config from '@/config/environment.js';

class WebSocketService {
  constructor() {
    this.client = null;
    this.subscriptions = new Map();
    this.reconnectDelay = 5000;
    this.maxRetry = 3;
    this.retryCount = 0;


    this.onWebsocketConnected = null;
    this.onGameQueueSubscribed = null;

    this.handleMatchFoundMessage = null;

    // Game Websocket handlers
    this.onGameStateReceived = null;
    this.onTurnChanged = null; 
    this.onAttacked = null; 
    this.onAskBlock = null; 
  }

  connectToQueue() {
    return new Promise((resolve) => {
      if (!this.client?.connected) {
        const socket = new SockJS('http://localhost:8080/mindbug-ws');
        this.client = new Client({
          webSocketFactory: () => socket,
          reconnectDelay: this.reconnectDelay,
          debug: (str) => console.log('[STOMP]', str),
          onWebSocketClose: () => {
            console.warn('WebSocket closed, trying to reconnect.. ');
            if (this.retryCount < this.maxRetry) {
              this.retryCount++;
              setTimeout(() => this.connectToQueue(), this.reconnectDelay);
            }
          },
        });

        this.client.onConnect = () => {
          this.retryCount = 0;
          console.log('✅ WebSocket Connected');

          if (typeof this.onWebsocketConnected === "function") {
            this.onWebsocketConnected();
          }
        };

        this.client.activate();
      }
      resolve();
    })

  }

  subscribeToGameQueue() {
    setTimeout(() => {
      if (!this.subscriptions.has('queue')) {
        console.log('🔄 In the queue...');
        const sub = this.client.subscribe(
          '/topic/game-queue',
          (message) => this.handleQueueMessage(message)
        );
        this.subscriptions.set('queue', sub);

        if (typeof this.onGameQueueSubscribed === "function") {
          console.log("subscribe handler called");
          this.onGameQueueSubscribed();
        } else {
          console.log("failed")
          console.log(typeof this.onGameQueueSubscribed)
          console.log(this.onGameQueueSubscribed)
        }
      }
    }, 500);
  }

  handleQueueMessage(message) {
    try {
      console.log('Message body:', message.body);
      const rawData = JSON.parse(message.body);
      let gameId = rawData.gameId || rawData.data?.gameId || rawData.matchData?.gameId || rawData.id;

      if (gameId) {
        console.log('Game ID:', gameId);
        if (typeof this.handleMatchFoundMessage === "function") {
          console.log("match handler called");
          this.handleMatchFoundMessage({ gameId });
        }
      }
    } catch (error) {
      console.error('❌ JSON parse failed:', error);
    }
  }

  subscribeToGameState(gameId) {
    // Make sure websocket is connected
    this.connectToQueue();

    const topic = `/topic/game/${gameId}`;

    if (!this.subscriptions.has(topic)) {
      const sub = this.client.subscribe(
        topic,
        (message) => {http://localhost:8081/
          this.handleGameStateMessage(message);
        }
      );
      this.subscriptions.set(topic, sub);
    }
  }

  handleGameStateMessage(message) {
    const data = JSON.parse(message.body);
    const messageID = data.messageID;
    console.log("data is : ", data);

    switch (messageID) {
      case 'ATTACKED':
        this.safeCall(this.onAttacked, data, "ATTACKED");
        break;
    
      case 'ASK_BLOCK':
        this.safeCall(this.onAskBlock, data, "ASK_BLOCK");
        break;
    
      case 'newGame':
        this.safeCall(this.onGameStateReceived, data.data, "newGame");
        break;
    
      case 'NEW_TURN':
        this.safeCall(this.onTurnChanged, data, "NEW_TURN");
        break;
    
      case 'gameState':
        this.safeCall(this.onGameStateReceived, data.data || data, "gameState");
        break;
    
      case 'CARD_DRAWED':
        this.safeCall(this.onCardDrawed, data.data || data, "CARD_DRAWED");
        break;
      case 'PLAYER_LIFE_UPDATED':
        this.safeCall(this.onGameStateReceived, data.data || data, "CARD_DRAWED");
        break;
      case 'PLAYER_LIFE_UPDATED':
        this.safeCall(this.onGameStateReceived, data.data || data, "CARD_DRAWED");
        break;
    
      default:
        console.warn("Unhandled message ID:", messageID);
    }
  }
    

  safeCall(handler, data, handlerName) {
    if (typeof handler === "function") {
      handler(data);
    } else {
      this.showNoWSHandlerError(handlerName);
    }
  }
  

  showNoWSHandlerError(websocket) {
    console.error("No handler defined for " + websocket)
  }



}

export default new WebSocketService();
