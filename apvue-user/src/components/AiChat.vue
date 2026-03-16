<template>
  <n-card title="AI 助手" :bordered="false" class="ai-chat-card">
    <template #header-extra>
      <n-tag type="info" size="small" round>DeepSeek</n-tag>
    </template>
    
    <div class="chat-container" ref="chatContainer">
      <div v-if="messages.length === 0" class="empty-state">
        <p>👋 你好！我是你的 AI 助手，有什么可以帮你的吗？</p>
        <p class="sub-text">你可以问我关于剧情的问题，或者让我推荐类似的影片。</p>
      </div>
      
      <div v-for="(msg, index) in messages" :key="index" class="message-wrapper" :class="msg.role">
        <div class="message-bubble">
          <div class="message-content" v-html="renderMarkdown(msg.content)"></div>
        </div>
      </div>
      
      <div v-if="loading" class="message-wrapper assistant">
        <div class="message-bubble loading">
          <n-spin size="small" />
        </div>
      </div>
    </div>

    <template #action>
      <div class="input-area">
        <n-input
            v-if="!apiKey && !envApiKey"
            v-model:value="inputApiKey"
            type="password"
            placeholder="请输入 DeepSeek API Key 以开始对话"
            class="api-key-input"
        />
        <div class="chat-input-box">
            <n-input
                v-model:value="inputMessage"
                type="textarea"
                placeholder="发送消息..."
                :autosize="{ minRows: 1, maxRows: 4 }"
                @keydown.enter.prevent="handleEnter"
                :disabled="loading || (!apiKey && !envApiKey)"
            />
            <n-button 
                type="primary" 
                circle 
                class="send-btn" 
                @click="sendMessage" 
                :disabled="!inputMessage.trim() || loading || (!apiKey && !envApiKey)"
            >
                <template #icon>
                    <svg xmlns="http://www.w3.org/2000/svg" width="24" height="24" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round">
                        <line x1="22" y1="2" x2="11" y2="13"></line>
                        <polygon points="22 2 15 22 11 13 2 9 22 2"></polygon>
                    </svg>
                </template>
            </n-button>
        </div>
      </div>
    </template>
  </n-card>
</template>

<script setup lang="ts">
import { ref, nextTick, computed, onMounted } from 'vue';
import { NCard, NInput, NButton, NTag, NSpin, useMessage } from 'naive-ui';
import { marked } from 'marked';

interface ChatMessage {
  role: 'user' | 'assistant';
  content: string;
}

const props = defineProps({
    contextText: { // Optional context about the video
        type: String,
        required: false
    }
});

const messages = ref<ChatMessage[]>([]);
const inputMessage = ref('');
const inputApiKey = ref('');
const loading = ref(false);
const chatContainer = ref<HTMLElement | null>(null);
const messageService = useMessage();

// Use environment variable if available
const envApiKey = import.meta.env.VITE_DEEPSEEK_API_KEY as string;

// Computed property to get the active API key
const apiKey = computed(() => {
    return envApiKey || inputApiKey.value;
});

const scrollToBottom = async () => {
  await nextTick();
  if (chatContainer.value) {
    chatContainer.value.scrollTop = chatContainer.value.scrollHeight;
  }
};

const renderMarkdown = (text: string) => {
    try {
        return marked.parse(text);
    } catch (e) {
        return text;
    }
};

const handleEnter = (e: KeyboardEvent) => {
    if (!e.shiftKey) {
        sendMessage();
    }
};

const sendMessage = async () => {
  if (!inputMessage.value.trim() || !apiKey.value) return;

  const userMsg = inputMessage.value.trim();
  messages.value.push({ role: 'user', content: userMsg });
  inputMessage.value = '';
  loading.value = true;
  scrollToBottom();

  // Prepare message history for context
  const history = messages.value.map(m => ({ role: m.role, content: m.content }));
  
  // Add system instruction as the first message if context exists
  if (props.contextText) {
      history.unshift({ 
          role: 'system', 
          content: `你是一个影视助手。请根据以下视频信息回答用户问题：\n${props.contextText}` 
      } as any);
  }

  try {
    const response = await fetch('https://api.deepseek.com/chat/completions', {
      method: 'POST',
      headers: {
        'Content-Type': 'application/json',
        'Authorization': `Bearer ${apiKey.value}`
      },
      body: JSON.stringify({
        model: "deepseek-chat",
        messages: history,
        stream: false
      })
    });

    const data = await response.json();

    if (data.error) {
        throw new Error(data.error.message);
    }

    if (data.choices && data.choices[0] && data.choices[0].message) {
        const text = data.choices[0].message.content;
        messages.value.push({ role: 'assistant', content: text });
    } else {
        messages.value.push({ role: 'assistant', content: "抱歉，我无法回答这个问题。" });
    }

  } catch (error: any) {
    console.error('DeepSeek API Error:', error);
    messageService.error('AI 响应失败: ' + (error.message || '未知错误'));
    messages.value.push({ role: 'assistant', content: "抱歉，连接 AI 服务时出现错误。" });
  } finally {
    loading.value = false;
    scrollToBottom();
  }
};

onMounted(() => {
    // Check if we need to load saved API key from local storage (optional UX enhancement)
    const savedKey = localStorage.getItem('deepseek_api_key');
    if (savedKey) {
        inputApiKey.value = savedKey;
    }
});
</script>

<style scoped>
.ai-chat-card {
  margin-top: 20px;
  background-color: var(--bg-card);
  border: 1px solid var(--border-color);
  display: flex;
  flex-direction: column;
  height: 500px; /* Fixed height for the chat widget */
}

:deep(.n-card__content) {
    display: flex;
    flex-direction: column;
    padding: 0 !important;
    overflow: hidden;
}

:deep(.n-card__action) {
    padding: 12px !important;
    background-color: var(--bg-body);
}

.chat-container {
  flex: 1;
  overflow-y: auto;
  padding: 16px;
  display: flex;
  flex-direction: column;
  gap: 16px;
  background-color: var(--bg-card);
}

.empty-state {
    text-align: center;
    color: var(--text-secondary);
    margin-top: 40px;
    padding: 0 20px;
}

.sub-text {
    font-size: 12px;
    margin-top: 8px;
    color: var(--text-tertiary);
}

.message-wrapper {
  display: flex;
  width: 100%;
}

.message-wrapper.user {
  justify-content: flex-end;
}

.message-wrapper.assistant {
  justify-content: flex-start;
}

.message-bubble {
  max-width: 85%;
  padding: 8px 12px;
  border-radius: 12px;
  font-size: 14px;
  line-height: 1.5;
  word-wrap: break-word;
}

.user .message-bubble {
  background-color: #18a058;
  color: white;
  border-bottom-right-radius: 2px;
}

.assistant .message-bubble {
  background-color: var(--bg-input);
  color: var(--text-primary);
  border-bottom-left-radius: 2px;
}

.loading {
    display: flex;
    align-items: center;
    justify-content: center;
    padding: 12px;
    background-color: var(--bg-input);
    border-radius: 12px;
    border-bottom-left-radius: 2px;
}

.input-area {
    display: flex;
    flex-direction: column;
    gap: 8px;
}

.api-key-input {
    margin-bottom: 8px;
}

.chat-input-box {
    display: flex;
    gap: 8px;
    align-items: flex-end;
}

.send-btn {
    flex-shrink: 0;
}

/* Markdown Styles within chat */
.message-content :deep(p) {
    margin: 0 0 8px 0;
}
.message-content :deep(p:last-child) {
    margin: 0;
}
.message-content :deep(code) {
    background: rgba(0,0,0,0.1);
    padding: 2px 4px;
    border-radius: 4px;
    font-family: monospace;
}
.message-content :deep(pre) {
    background: rgba(0,0,0,0.1);
    padding: 8px;
    border-radius: 4px;
    overflow-x: auto;
}
</style>
