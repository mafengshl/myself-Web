<template>
  <!-- 全局顶部导航栏 -->
  <div class="global-header"
       :class="{ 'header-hidden': !isHeaderVisible }"
       v-if="shouldShowHeader">
    <el-container class="main-container">
      <el-header class="main-header">
        <!-- 修改部分：导航栏左侧添加图标 -->
        <div class="logo-container">
          <el-icon><Document /></el-icon>
          <span class="logo-text">LiLi</span>
        </div>
        <el-menu mode="horizontal" :default-active="activeIndex">
          <el-menu-item index="1" @click="handleHomeClick">
            <el-icon><HomeFilled /></el-icon>
            主页
          </el-menu-item>
          <el-menu-item index="2" @click="handleBlogClick">
            <el-icon><Menu /></el-icon>
            文章
          </el-menu-item>
          <el-menu-item index="3" @click="handleJobClick">
            <el-icon><Star /></el-icon>
            求职（优化中）
          </el-menu-item>
          <el-menu-item index="4" @click="handleGalleryClick">
        <el-icon><Picture /></el-icon>
        相册
      </el-menu-item>
      <el-menu-item index="5" @click="handleMessageClick">
        <el-icon><Message /></el-icon>
        留言
      </el-menu-item>
        </el-menu>
        <div
            v-if="userStore.role === 'admin'"
            class="admin-menu-item"
            @click="handleAdminClick"
        >
          <el-icon><User /></el-icon>
          <span>管理</span>
        </div>
        <div class="tabs">
          <span class="eye-protection-text">护眼👉</span>
          <div class="dark-mode-toggle" @click="toggleDark()">
            <el-icon v-if="isDark">
              <Sunny />
            </el-icon>
            <el-icon v-else>
              <Moon />
            </el-icon>
          </div>
          <div style="text-align: right;line-height: 16px;margin-right: 10px">
            <div style="font-size: 14px;color: white;margin-right: -20px">
              {{ userStore.isLoggedIn ? userStore.username : '登录' }}
              👉
            </div>
          </div>
          <el-dropdown>
            <el-avatar class="avatar" :src="userStore.isLoggedIn ? 'Avatar.png' : 'Avatar.png'"/>

            <template #dropdown>
              <el-dropdown-menu>
                <el-dropdown-item v-if="!userStore.isLoggedIn" @click="handleLogin">
                  <el-icon><User /></el-icon>
                  登录
                </el-dropdown-item>
                <el-dropdown-item v-if="!userStore.isLoggedIn" @click="handleRegister">
                  <el-icon><Edit /></el-icon>
                  注册
                </el-dropdown-item>
                <el-dropdown-item v-if="userStore.isLoggedIn" @click="handleLogout">
                  <el-icon><Back /></el-icon>
                  退出登录
                </el-dropdown-item>
              </el-dropdown-menu>
            </template>
          </el-dropdown>
        </div>
      </el-header>
    </el-container>
  </div>

  <router-view class="app-content"/>

  <!-- 右键菜单 -->
  <div
      v-if="showContextMenu"
      class="context-menu"
      :style="{ top: contextMenuY + 'px', left: contextMenuX + 'px' }"
  >
    <img src="@/img/monse_gif/monse6.gif" alt="右键菜单" />
  </div>

  <!-- 点击生成爱心的容器 -->
  <div id="heart-container"></div>
  
  <!-- 自定义光标 -->
  <div
      class="custom-cursor"
      :class="{ 'hover': isCursorHover, 'active': isCursorActive }"
      :style="{ left: cursorX + 'px', top: cursorY + 'px', '--cursor-size': cursorSize + 'px' }"
  ></div>
  
  <!-- 光标大小调节滑块 -->
  <button
      v-if="!showCursorSlider"
      class="cursor-slider-toggle"
      @click="showCursorSlider = true"
  >🖱️</button>
  
  <div v-if="showCursorSlider" class="cursor-slider-container">
    <div class="cursor-slider-title">
      光标大小调节
      <span class="cursor-slider-value">{{ cursorSize }}px</span>
    </div>
    <input
        type="range"
        class="cursor-slider"
        v-model.number="cursorSize"
        min="20"
        max="100"
        step="5"
    />
    <div style="display: flex; gap: 8px; margin-top: 10px;">
      <button
          style="flex: 1; padding: 6px 12px; background: #67c23a; border: none; border-radius: 4px; color: white; cursor: pointer; font-size: 12px;"
          @click="resetCursorSize"
      >恢复默认</button>
      <button
          style="flex: 1; padding: 6px 12px; background: #409eff; border: none; border-radius: 4px; color: white; cursor: pointer; font-size: 12px;"
          @click="showCursorSlider = false"
      >关闭</button>
    </div>
  </div>
  
  <MusicPlayer />
  <AIChat />
</template>


<script setup lang="ts">
import { ref, onMounted, onUnmounted, computed, watch } from 'vue';
import { useDark, useToggle } from "@vueuse/core";
import { useRouter, useRoute } from "vue-router";
import {
  Back,
  Document,
  Edit,
  HomeFilled,
  Message,
  Star,
  User,
  Menu,
  Picture,
  Sunny,
  Moon,
} from '@element-plus/icons-vue';
import { logout } from "@/net";
import { useUserStore } from '@/stores/user_store';
import { post } from '@/net';
import { ElMessage } from "element-plus";
import MusicPlayer from "@/views/components/music/MusicPlayer.vue";
import { WebSocketConfig } from '@/config/websocket.config';
import AIChat from "@/views/components/aichat/AIChat.vue";
const router = useRouter();
const userStore = useUserStore();

// 添加WebSocket相关变量
const websocket = ref(null);
const currentOnline = ref(0);

// 控制右键菜单的显示与位置
const showContextMenu = ref(false);
const contextMenuX = ref(0);
const contextMenuY = ref(0);

// 自定义光标相关
const cursorX = ref(0);
const cursorY = ref(0);
const isCursorHover = ref(false);
const isCursorActive = ref(false);
const cursorSize = ref(50); // 光标大小，默认50px
const DEFAULT_CURSOR_SIZE = 50; // 默认光标大小常量

const resetCursorSize = () => {
  cursorSize.value = DEFAULT_CURSOR_SIZE;
};
const showCursorSlider = ref(false); // 控制滑块显示

const clickableTags = ['BUTTON', 'A', 'INPUT', 'TEXTAREA', 'SELECT', 'EL-BUTTON', 'EL-LINK'];
const hoverableClasses = ['clickable-element', 'el-menu-item', 'el-dropdown', 'dark-mode-toggle'];

const updateCursorPosition = (e) => {
  cursorX.value = e.clientX;
  cursorY.value = e.clientY;
  
  const target = e.target;
  const tagName = target.tagName.toUpperCase();
  const classList = target.classList;
  
  isCursorHover.value = clickableTags.includes(tagName) || 
    hoverableClasses.some(cls => classList.contains(cls)) ||
    target.closest('button') || 
    target.closest('a') ||
    target.closest('.clickable-element') ||
    target.closest('.el-menu-item') ||
    target.closest('.el-dropdown') ||
    target.closest('.dark-mode-toggle');
};

const handleMouseDown = () => {
  isCursorActive.value = true;
};

const handleMouseUp = () => {
  isCursorActive.value = false;
};

const route = useRoute(); // 添加 route
// 添加计算属性来判断是否应该显示导航栏
const shouldShowHeader = computed(() => {
  // 在登录、注册、忘记密码页面不显示导航栏
  const hideHeaderRoutes = ['/login', '/register', '/forget'];
  return !hideHeaderRoutes.includes(route.path);
});


// 添加响应式数据来跟踪当前激活的菜单索引
const activeIndex = ref('1');

// 监听路由变化来更新激活的菜单项
watch(() => route.name, (newRouteName) => {
  if (newRouteName === 'index') {
    activeIndex.value = '1';
  } else if (newRouteName === 'BlogList' || route.path.startsWith('/blog')) {
    activeIndex.value = '2';
  } else if (newRouteName === 'Job' || route.path.startsWith('/job')) {
    activeIndex.value = '3';
  } else if (newRouteName === 'Gallery' || route.path.startsWith('/gallery')) {
    activeIndex.value = '4';
  } else if (newRouteName === 'MessageCloud' || route.path.startsWith('/message')) {
    activeIndex.value = '5';
  }
}, { immediate: true });

// 修改 handleHomeClick 方法
const handleHomeClick = () => {
  activeIndex.value = '1';
  router.push({ name: 'index' });
};

// 修改 handleBlogClick 方法
const handleBlogClick = () => {
  activeIndex.value = '2';
  router.push({ name: 'BlogList' }).then(() => {
    // 跳转后显示导航栏
    isHeaderVisible.value = true;
  });
};

// 添加留言页面跳转方法
const handleMessageClick = () => {
  activeIndex.value = '6';
  router.push('/message');
};

// 添加相册页面跳转方法
const handleGalleryClick = () => {
  activeIndex.value = '5';
  router.push('/gallery');
};

// 添加求职页面跳转方法
const handleJobClick = () => {
  activeIndex.value = '3';
  router.push('/job');
};

// 添加暗黑模式相关逻辑
const isDark = useDark({
  selector: 'html',
  attribute: 'class',
  valueDark: 'dark',
  valueLight: 'light',
  initialValue: 'dark' // 设置默认值为暗黑模式
})

const toggleDark = useToggle(isDark)

// 添加管理页面跳转方法
const handleAdminClick = () => {
  router.push('/admin');
};

const handleLogin = () => {
  // 检查用户是否已经登录
  if (userStore.isLoggedIn) {
    // 如果已登录，显示提示信息
    ElMessage({
      message: '您已登录成功，请勿再登录',
      type: 'success',
      duration: 2000
    });
  } else {
    // 如果未登录，执行原来的跳转逻辑
    router.push('/login').catch(err => {
      console.error('登录跳转失败:', err.message)
    })
  }
}

const handleRegister = () => {
  // 检查用户是否已经登录
  if (userStore.isLoggedIn) {
    // 如果已登录，显示提示信息
    ElMessage({
      message: '请先退出登录后再进行新账户的注册哦',
      type: 'warning',
      duration: 2000
    });
  } else {
    // 如果未登录，执行原来的跳转逻辑
    router.push('/register').catch(err => {
      console.error('注册跳转失败:', err.message)
    })
  }
}

function handleLogout() {
  logout(() => router.push("/"))
}

// 检查用户角色权限
const checkUserRole = () => {
  // 只有当用户已登录且用户名存在时才调用接口
  if (userStore.isLoggedIn && userStore.username) {
    // 改为使用 post 方法，并将 username 作为请求体发送
    post(`/api/auth/verify-role`, { username: userStore.username }, (data: string) => {
      // 将获取到的权限信息存储到 userStore 中
      if (data) {
        userStore.setRole(data);
      } else {
        // 如果返回空数据，设置默认角色
        userStore.setRole('user');
      }
    }, (error) => {
      console.error('获取用户权限失败:', error);
      // 出错时设置默认角色
      userStore.setRole('user');
    });
  }
};

// 监听用户登录状态变化
watch(() => userStore.isLoggedIn, (newVal) => {
  if (newVal) {
    // 用户登录后检查角色
    checkUserRole();
  }
}, { immediate: true });

// 鼠标右键点击事件处理
const handleContextMenu = (event) => {
  event.preventDefault(); // 阻止默认的右键菜单
  showContextMenu.value = true;
  contextMenuX.value = event.clientX+120;
  contextMenuY.value = event.clientY;
};

// 点击页面其他地方隐藏右键菜单
const handleClick = () => {
  showContextMenu.value = false;
};

// 双击事件处理函数
const handleDoubleClick = (event) => {
  event.preventDefault(); // 阻止默认的双击行为
};

// 爱心内容数组
const content = [
  "⚔️ 剑心通明 ⚔️", "🔮 修仙问道 🔮", "🔥 炼气化神 🔥",
  "⚡ 御剑飞行 ⚡", "🛡️ 护体金光 🛡️", "💊 灵丹妙药 💊", "🌀 内力深厚 🌀", "道士职业 ☯️",
  "🌟 星辰变 🌟", "🐉 龙腾九天 🐉", "❄️ 寒冰真气 ❄️","💥 雷霆万钧 💥", "🌪️ 风卷残云 🌪️", "💎 灵石仙器 💎",
  "📜 秘籍传承 📜", "🏯 仙山福地 🏯", "💫 化神期至 💫", "🗡️ 飞剑传书 🗡️",
  "🏹 神兵利器 🏹", "📿 佛珠禅意 📿", "☯️ 阴阳调和 ☯️", "🌊 水灵法术 🌊", "⛰️ 金刚不坏 ⛰️", "👻 驱邪镇魔 👻",
  "🧚 神仙眷侣 🧚", "🔥 三昧真火 🔥", "🌙 月下独酌 🌙", "🌸 落英神剑 🌸","⚡ 九阳神功 ⚡",
  "❄️ 九阴真经 ❄️", "💫 乾坤大挪移 💫", "🌪️ 凌波微步 🌪️", "💣 爆炸符箓 💣", "✨ 仙法无边 ✨",
  "🎯 百步穿杨 🎯", "👑 武林盟主 👑", "🏯 修仙洞府 🏯", "🌌 星辰大海 🌌", "🔥 凤舞九天 🔥", "⚡ 雷动九天 ⚡",
  "🗡️ 剑指苍穹", "⚡ 一念成仙", "🏯 仙山琼阁", "👻 魑魅魍魉", "星辰大海🌟", "🏞️ 昆仑仙境", "🌊 瀛洲仙岛",
  "🌟 仙气万古 🌟", "🔥 纯阳无明 🔥", "⛰️ 蓬莱福地", "🌋 火域洞天", "❄️ 冰原圣境", "🌸 桃源秘境",
  "🌌 星辰幻境", "🏯 天庭宝地", "🌿 青丘仙谷", "⚡ 雷泽神域", "🔱 方天画戟", "💍 储物戒指", "📜 天罡符箓",
  "🗡️ 青釭剑", "🛡️ 八卦盾", "🔮 水晶球", "🏹 诛仙弓", "⚡ 雷霆锤", "🌋 火云扇", "🌊 定水珠"
]

// 设置随机颜色
const getRandomColor = () => {
  const allType = '0,1,2,3,4,5,6,7,8,9,a,b,c,d,e,f';
  const allTypeArr = allType.split(',');
  let color = '#';
  for (let i = 0; i < 6; i++) {
    const random = Math.floor(Math.random() * allTypeArr.length);
    color += allTypeArr[random];
  }
  return color;
};

// 创建爱心
const createHeart = (e) => {
  const x = e.pageX;
  const y = e.pageY;

  const randContentIndex = Math.floor(Math.random() * content.length);
  const randColor = getRandomColor();

  const span = document.createElement('span');
  span.textContent = content[randContentIndex];
  span.className = 'text';
  span.style.top = `${y - 20}px`;
  span.style.left = `${x - 50}px`;
  span.style.color = randColor;
  span.style.animation = 'remove 2s';

  document.getElementById('heart-container').appendChild(span);

  let i = 0;
  const interval = setInterval(() => {
    span.style.top = `${y - 20 - i}px`;
    i++;
  }, 10);

  setTimeout(() => {
    clearInterval(interval);
    span.remove();
  }, 1900);
};

// 绑定点击事件
const setupHeartClick = () => {
  window.addEventListener('click', createHeart);
};

// 添加响应式数据来控制导航栏的显示状态
const isHeaderVisible = ref(true);
const lastScrollTop = ref(0);
const headerHeight = ref(0);
const scrollThreshold = ref(0);

// 监听窗口滚动事件（用于除首页外的其他页面）
const handleWindowScroll = () => {
  const scrollTop = window.pageYOffset || document.documentElement.scrollTop;

  // 只有滚动距离超过阈值才触发
  if (Math.abs(scrollTop - lastScrollTop.value) <= scrollThreshold.value) {
    lastScrollTop.value = scrollTop;
    return;
  }

  // 向下滚动且滚动距离大于导航栏高度时隐藏
  if (scrollTop > lastScrollTop.value && scrollTop > headerHeight.value) {
    isHeaderVisible.value = false;
  }
  // 向上滚动或滚动到顶部附近时显示
  else if (scrollTop < lastScrollTop.value || scrollTop < headerHeight.value) {
    isHeaderVisible.value = true;
  }

  lastScrollTop.value = scrollTop;
};

// 处理来自 IndexView 的导航栏显示/隐藏事件
const handleHeaderVisibilityChange = (event: CustomEvent) => {
  isHeaderVisible.value = event.detail;
};

// WebSocket重连次数
let reconnectCount = 0;
const MAX_RECONNECT_COUNT = 3;

// 建立WebSocket连接
const connectWebSocket = () => {
  // 使用配置类获取WebSocket路径
  const wsUrl = WebSocketConfig.getWebSocketUrl();
  console.log('WebSocket URL:', wsUrl)

  try {
    websocket.value = new WebSocket(wsUrl);

    websocket.value.onopen = () => {
      console.log('WebSocket连接已建立');
      reconnectCount = 0;
    };

    websocket.value.onmessage = (event) => {
      const onlineCount = parseInt(event.data);
      if (!isNaN(onlineCount)) {
        currentOnline.value = onlineCount;
        window.dispatchEvent(new CustomEvent('onlineCountUpdate', { detail: onlineCount }));
      }
    };

    websocket.value.onclose = () => {
      console.log('WebSocket连接已关闭');
      if (reconnectCount < MAX_RECONNECT_COUNT) {
        reconnectCount++;
        console.log(`尝试第 ${reconnectCount} 次重连...`);
        setTimeout(connectWebSocket, WebSocketConfig.RECONNECT_INTERVAL * reconnectCount);
      }
    };

    websocket.value.onerror = (error) => {
      console.error('WebSocket发生错误:', error);
      if (reconnectCount < MAX_RECONNECT_COUNT) {
        reconnectCount++;
        console.log(`WebSocket错误，尝试第 ${reconnectCount} 次重连...`);
        setTimeout(connectWebSocket, WebSocketConfig.RECONNECT_INTERVAL * reconnectCount);
      }
    };
  } catch (error) {
    console.error('WebSocket连接失败:', error);
    if (reconnectCount < MAX_RECONNECT_COUNT) {
      reconnectCount++;
      console.log(`WebSocket初始化失败，尝试第 ${reconnectCount} 次重连...`);
      setTimeout(connectWebSocket, WebSocketConfig.RECONNECT_INTERVAL * reconnectCount);
    }
  }
};

// 组件挂载时绑定事件
onMounted(() => {
  checkUserRole();
  // 监听窗口滚动事件（用于博客列表等页面）
  window.addEventListener('scroll', handleWindowScroll);
  // 监听来自 IndexView 的自定义事件
  window.addEventListener('headerVisibilityChange', handleHeaderVisibilityChange as EventListener);
  window.addEventListener('contextmenu', handleContextMenu);
  window.addEventListener('click', handleClick);
  window.addEventListener('dblclick', handleDoubleClick); // 绑定双击事件
  setupHeartClick();

  // 自定义光标事件
  window.addEventListener('mousemove', updateCursorPosition);
  window.addEventListener('mousedown', handleMouseDown);
  window.addEventListener('mouseup', handleMouseUp);

  // 建立WebSocket连接
  connectWebSocket();
});

// 组件卸载时移除事件
onUnmounted(() => {
  window.removeEventListener('scroll', handleWindowScroll);
  window.removeEventListener('headerVisibilityChange', handleHeaderVisibilityChange as EventListener);
  window.removeEventListener('contextmenu', handleContextMenu);
  window.removeEventListener('click', handleClick);
  window.removeEventListener('dblclick', handleDoubleClick); // 解绑双击事件
  window.removeEventListener('click', createHeart);

  // 自定义光标事件
  window.removeEventListener('mousemove', updateCursorPosition);
  window.removeEventListener('mousedown', handleMouseDown);
  window.removeEventListener('mouseup', handleMouseUp);

  // 关闭WebSocket连接
  if (websocket.value) {
    websocket.value.close();
  }
});

</script>



<style scoped>
.global-header {
  position: fixed;
  top: 0;
  left: 0;
  width: 100%;
  z-index: 1000;
  transition: transform 0.6s ease-in-out, background-color 0.6s ease-in-out;
}

/* 导航栏隐藏时的样式 */
.header-hidden {
  transform: translateY(-100%);
  background-color: #95a194 !important; /* 隐藏时的背景色 */
}

/* 为 app-content 添加滚动条样式 */
.app-content {
  transition: margin-top 0.6s ease-in-out;
  overflow-y: auto;
}

/* 当导航栏隐藏时减少内容区域的顶部边距 */
.header-hidden + .app-content {
  margin-top: 0;
}

.main-header {
  height: 55px;
  background-color: inherit !important; /* 继承 .global-header 的背景色 */
  border-bottom: none; /* 移除底部边框 */
  display: flex;
  align-items: center;
  z-index: 10; /* 确保导航栏在背景图之上 */
  position: relative;
}

.logo-container {
  display: flex;
  align-items: center;
  margin-right: 20px;
  color: white;
  font-size: 20px;
  font-weight: bold;
}

.logo-container .logo-text {
  margin-left: 10px;
}

.el-menu--horizontal {
  background-color: transparent !important; /* 使 el-menu 透明 */
  border-bottom: none !important; /* 移除底部边框（虚线） */
  display: flex;
  flex-grow: 1;
}

.el-menu--horizontal .el-menu-item {
  color: white;
  font-weight: bold;
  background-color: transparent !important; /* 使 el-menu-item 透明 */
}

.el-menu--horizontal .el-menu-item i {
  margin-right: 5px;
}

.tabs {
  height: 55px;
  gap: 10px;
  flex: 1;
  display: flex;
  align-items: center;
  justify-content: right;
}

.tab-item {
  padding: 0 10px;
  cursor: default;
}

.eye-protection-text {
  color: #00851d;
  font-size: 15px;
  font-family: "Microsoft Yahei", sans-serif;
  font-weight: 500;
  letter-spacing: 1px;
  border-radius: 4px;
  padding: 0;
  margin: -10px;
}

/* 管理菜单项样式 */
.admin-menu-item {
  display: flex;
  align-items: center;
  padding: 0 20px;
  color: white;
  font-weight: bold;
  height: 100%;
  cursor: pointer;
  transition: color 0.3s;
  font-size: 15px;
}

.admin-menu-item:hover {
  color: #409eff; /* Element Plus 默认主题的深蓝色 */
}

.admin-menu-item i {
  margin-right: 5px;
}

/* 暗黑模式切换按钮样式 */
.dark-mode-toggle {
  display: flex;
  align-items: center;
  justify-content: center;
  width: 32px;
  height: 32px;
  border-radius: 50%;
  background-color: rgba(255, 255, 255, 0.1);
  cursor: pointer;
  transition: background-color 0.3s;
  margin-right: 10px;
}

.dark-mode-toggle:hover {
  background-color: rgba(0, 0, 0, 0.5);
}

.dark-mode-toggle .el-icon {
  font-size: 18px;
  color: white;
}
html.dark .global-header {
  background-color: rgba(0, 0, 0, 0.5);
}

html.dark .main-header {
  background-color: rgba(0, 0, 0, 0.5);
}

html.dark .logo-text {
  color: #e0e0e0;
}

html.dark .el-menu--horizontal .el-menu-item {
  color: #e0e0e0;
}

html.dark .eye-protection-text {
  color: #4ade80;
}

html.dark .admin-menu-item {
  color: #e0e0e0;
}

html.dark .admin-menu-item:hover {
  color: #409eff;
}
</style>


<style>
/* 右键菜单样式 */
.context-menu {
  position: fixed;
  z-index: 9999;
  pointer-events: none;
}

/* 点击生成爱心的容器样式 */
#heart-container {
  position: fixed;
  top: 0;
  left: 0;
  width: 100%;
  height: 100%;
  pointer-events: none;
  z-index: 999999;
}

/* 自定义文字样式 */
.text {
  position: absolute;
  z-index: 9999999;
  font-weight: bold;
  user-select: none;
  animation: remove 2s;
}

@keyframes remove {
  100% {
    opacity: 0;
  }
}

/* 隐藏默认光标 */
body,
button,
a,
input,
textarea,
select,
[tabindex],
[role="button"],
.el-button,
.el-link,
.clickable-element,
.el-menu--horizontal .el-menu-item {
  cursor: none;
}

/* 自定义光标样式 */
.custom-cursor {
  position: fixed;
  width: var(--cursor-size, 40px);
  height: var(--cursor-size, 40px);
  background: url("@/img/monse_gif/monse2.gif") no-repeat center center;
  background-size: contain;
  pointer-events: none;
  z-index: 9999999;
  transform: translate(-2px, -2px);
  transition: transform 0.05s ease-out;
}

.custom-cursor.hover {
  background-image: url("@/img/monse_gif/monse3.gif");
  transform: translate(-2px, -2px) scale(1.1);
}

.custom-cursor.active {
  background-image: url("@/img/monse_gif/monse4.gif");
  transform: translate(-2px, -2px) scale(0.9);
}

/* 光标大小调节滑块样式 */
.cursor-slider-container {
  position: fixed;
  bottom: 20px;
  right: 20px;
  background: rgba(0, 0, 0, 0.8);
  padding: 15px 20px;
  border-radius: 12px;
  color: white;
  z-index: 999999;
  min-width: 200px;
}

.cursor-slider-title {
  font-size: 14px;
  margin-bottom: 10px;
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.cursor-slider-value {
  background: #409eff;
  padding: 2px 8px;
  border-radius: 4px;
  font-size: 12px;
}

.cursor-slider {
  width: 100%;
  cursor: pointer;
}

.cursor-slider-toggle {
  position: fixed;
  bottom: 20px;
  right: 20px;
  width: 50px;
  height: 50px;
  border-radius: 50%;
  background: #409eff;
  border: none;
  color: white;
  font-size: 24px;
  cursor: pointer;
  z-index: 999999;
  display: flex;
  align-items: center;
  justify-content: center;
  box-shadow: 0 4px 12px rgba(64, 158, 255, 0.4);
  transition: transform 0.2s;
}

.cursor-slider-toggle:hover {
  transform: scale(1.1);
}

/* 全局滚动条样式 - 影响所有滚动条 */
::-webkit-scrollbar {
  width: 10px;
}

::-webkit-scrollbar-track {
  background-color: transparent;
  border-radius: 10px;
}

::-webkit-scrollbar-thumb {
  background-color: rgba(140, 174, 166, 0.3);
  border-radius: 10px;
}

::-webkit-scrollbar-thumb:hover {
  background-color: rgba(188, 169, 169, 0.6);
}

::-webkit-scrollbar-corner {
  background-color: transparent;
}
</style>
