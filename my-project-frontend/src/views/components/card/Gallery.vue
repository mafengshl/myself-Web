<template>
  <div class="gallery-container">
    <div class="gallery-header">
      <div class="header-content">
        <h1 class="gallery-title">📸 相册空间</h1>
        <p class="gallery-subtitle">记录生活的美好瞬间 ✨</p>
      </div>
      <div v-if="userStore.isLoggedIn" class="upload-btn-wrapper">
        <input
          ref="fileInput"
          type="file"
          accept="image/*"
          class="file-input"
          @change="handleFileSelect"
        />
        <el-button class="upload-btn" @click="triggerFileInput">
          <el-icon><Plus /></el-icon>
          上传照片
        </el-button>
      </div>
    </div>

    <div class="upload-modal" v-if="showUploadModal" @click.self="closeUploadModal">
      <div class="modal-content">
        <div class="modal-header">
          <h3>上传照片</h3>
          <span class="close-btn" @click="closeUploadModal">×</span>
        </div>
        <div class="modal-body">
          <div class="preview-section">
            <div class="preview-grid">
              <div
                v-for="(file, index) in selectedFiles"
                :key="index"
                class="preview-item"
              >
                <img :src="getFilePreviewUrl(file)" :alt="file.name" />
                <span class="remove-preview" @click="removeFile(index)">×</span>
              </div>
            </div>
          </div>
          <div class="form-section">
            <el-input
              v-model="photoDescription"
              placeholder="添加照片描述（可选）"
              class="description-input"
            />
          </div>
        </div>
        <div class="modal-footer">
          <el-button @click="closeUploadModal">取消</el-button>
          <el-button type="primary" @click="uploadPhoto" :loading="isUploading">
            确认上传
          </el-button>
        </div>
      </div>
    </div>

    <div class="filter-bar">
      <div class="filter-tabs">
        <button
          v-for="tab in filterTabs"
          :key="tab.value"
          :class="['filter-tab', { active: activeFilter === tab.value }]"
          @click="activeFilter = tab.value"
        >
          {{ tab.label }}
        </button>
      </div>
      <div class="search-box">
        <el-input
          v-model="searchKeyword"
          placeholder="搜索照片..."
          class="search-input"
        />
      </div>
    </div>

    <div class="photo-grid">
      <div
        v-for="photo in filteredPhotos"
        :key="photo.id"
        class="photo-card"
        @click="previewPhoto(photo)"
      >
        <div class="photo-image-wrapper">
          <img :src="photo.url" :alt="photo.description || '照片'" />
          <div class="photo-overlay">
            <div class="photo-info">
              <span class="photo-author">{{ photo.username }}</span>
              <span class="photo-date">{{ formatDate(photo.createdAt) }}</span>
            </div>
            <div class="photo-actions">
              <span v-if="photo.isMine" class="delete-btn" @click.stop="deletePhoto(photo)">
                <el-icon><Delete /></el-icon>
              </span>
            </div>
          </div>
        </div>
        <div class="photo-description">{{ photo.description || '暂无描述' }}</div>
        <div class="photo-footer">
          <span class="avatar-wrapper">
            <img :src="photo.avatar || 'Avatar.png'" class="photo-avatar" />
          </span>
          <div class="like-btn-container" @click.stop="handleLike(photo)">
            <span 
              :class="['like-btn', { liked: likedPhotos.has(photo.id), combo: comboCounts[photo.id] && comboCounts[photo.id] > 1 }]"
              :data-photo-id="photo.id"
            >
              <svg 
                v-if="likedPhotos.has(photo.id)" 
                class="heart-icon filled" 
                viewBox="0 0 24 24" 
                fill="currentColor"
              >
                <path d="M12 21.35l-1.45-1.32C5.4 15.36 2 12.28 2 8.5 2 5.42 4.42 3 7.5 3c1.74 0 3.41.81 4.5 2.09C13.09 3.81 14.76 3 16.5 3 19.58 3 22 5.42 22 8.5c0 3.78-3.4 6.86-8.55 11.54L12 21.35z"/>
              </svg>
              <svg 
                v-else 
                class="heart-icon outline" 
                viewBox="0 0 24 24" 
                fill="none" 
                stroke="currentColor" 
                stroke-width="2" 
                stroke-linecap="round" 
                stroke-linejoin="round"
              >
                <path d="M20.84 4.61a5.5 5.5 0 0 0-7.78 0L12 5.67l-1.06-1.06a5.5 5.5 0 0 0-7.78 7.78l1.06 1.06L12 21.23l7.78-7.78 1.06-1.06a5.5 5.5 0 0 0 0-7.78z"/>
              </svg>
              <span class="like-count">{{ photo.likes }}</span>
              <span v-if="comboCounts[photo.id] && comboCounts[photo.id] > 1" class="combo-badge">
                x{{ comboCounts[photo.id] }}
              </span>
            </span>
            <div 
              v-if="flyingHearts[photo.id]" 
              class="flying-hearts"
              :key="'fly-' + photo.id"
            >
              <span 
                v-for="(heart, index) in flyingHearts[photo.id]" 
                :key="index" 
                class="flying-heart"
                :style="{ 
                  '--delay': index * 0.08 + 's', 
                  '--x': heart.x + 'px', 
                  '--size': heart.size + 'px',
                  '--color': heart.color 
                }"
              >
                ❤️
              </span>
            </div>
          </div>
        </div>
      </div>
    </div>

    <div class="empty-state" v-if="filteredPhotos.length === 0">
      <div class="empty-icon">📭</div>
      <p>暂无照片</p>
      <p v-if="!userStore.isLoggedIn" class="empty-hint">登录后可以上传照片哦~</p>
    </div>

    <div class="preview-modal" v-if="previewPhotoData" @click.self="closePreview">
      <div class="preview-content">
        <span class="preview-close" @click="closePreview">×</span>
        <img :src="previewPhotoData.url" :alt="previewPhotoData.description" />
        <div class="preview-info">
          <div class="preview-author">
            <img :src="previewPhotoData.avatar || 'Avatar.png'" class="preview-avatar" />
            <span>{{ previewPhotoData.username }}</span>
          </div>
          <p class="preview-description">{{ previewPhotoData.description }}</p>
          <div class="preview-meta">
            <span>{{ formatDate(previewPhotoData.createdAt) }}</span>
            <div class="preview-like-btn" @click.stop="handleLike(previewPhotoData)">
              <svg 
                v-if="likedPhotos.has(previewPhotoData.id)" 
                class="heart-icon filled" 
                viewBox="0 0 24 24" 
                fill="currentColor"
              >
                <path d="M12 21.35l-1.45-1.32C5.4 15.36 2 12.28 2 8.5 2 5.42 4.42 3 7.5 3c1.74 0 3.41.81 4.5 2.09C13.09 3.81 14.76 3 16.5 3 19.58 3 22 5.42 22 8.5c0 3.78-3.4 6.86-8.55 11.54L12 21.35z"/>
              </svg>
              <svg 
                v-else 
                class="heart-icon outline" 
                viewBox="0 0 24 24" 
                fill="none" 
                stroke="currentColor" 
                stroke-width="2" 
                stroke-linecap="round" 
                stroke-linejoin="round"
              >
                <path d="M20.84 4.61a5.5 5.5 0 0 0-7.78 0L12 5.67l-1.06-1.06a5.5 5.5 0 0 0-7.78 7.78l1.06 1.06L12 21.23l7.78-7.78 1.06-1.06a5.5 5.5 0 0 0 0-7.78z"/>
              </svg>
              <span class="like-count">{{ previewPhotoData.likes }}</span>
            </div>
          </div>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref, computed, onMounted } from 'vue';
import { Plus, Delete } from '@element-plus/icons-vue';
import { useUserStore } from '@/stores/user_store';
import { ElMessage } from 'element-plus';

const userStore = useUserStore();
const fileInput = ref<HTMLInputElement | null>(null);
const selectedFiles = ref<File[]>([]);
const photoDescription = ref('');
const showUploadModal = ref(false);
const isUploading = ref(false);
const activeFilter = ref('all');
const searchKeyword = ref('');
const previewPhotoData = ref<Photo | null>(null);
const likedPhotos = ref<Set<number>>(new Set());
const flyingHearts = ref<Record<number, { x: number; size: number; color: string }[]>>({});
const comboCounts = ref<Record<number, number>>({});
const lastLikeTime = ref<Record<number, number>>({});
const comboTimeout = ref<Record<number, number>>({});

interface Photo {
  id: number;
  userId: number;
  url: string;
  description: string;
  username: string;
  avatar: string;
  createdAt: string;
  likes: number;
  isMine: boolean;
}

const filterTabs = [
  { label: '全部', value: 'all' },
  { label: '我的', value: 'mine' },
  { label: '其他人', value: 'others' },
];

const photos = ref<Photo[]>([]);

const filteredPhotos = computed(() => {
  let result = photos.value;
  
  if (activeFilter.value === 'mine') {
    result = result.filter(p => p.isMine);
  } else if (activeFilter.value === 'others') {
    result = result.filter(p => !p.isMine);
  }
  
  if (searchKeyword.value) {
    const keyword = searchKeyword.value.toLowerCase();
    result = result.filter(
      p => p.description.toLowerCase().includes(keyword) || 
           p.username.toLowerCase().includes(keyword)
    );
  }
  
  return result;
});

const triggerFileInput = () => {
  fileInput.value?.click();
};

const handleFileSelect = (event: Event) => {
  const target = event.target as HTMLInputElement;
  const files = target.files;
  if (files && files.length > 0) {
    selectedFiles.value = Array.from(files);
    showUploadModal.value = true;
  }
};

const removeFile = (index: number) => {
  selectedFiles.value.splice(index, 1);
};

const getFilePreviewUrl = (file: File) => {
  return URL.createObjectURL(file);
};

const closeUploadModal = () => {
  showUploadModal.value = false;
  selectedFiles.value = [];
  photoDescription.value = '';
};

const fetchPhotos = async () => {
  try {
    const response = await fetch('/api/photos/list');
    const result = await response.json();
    if (result.code === 200 && result.data) {
      photos.value = result.data.map((p: any) => ({
        ...p,
        isMine: userStore.isLoggedIn && p.userId === userStore.userId
      }));
    }
  } catch (error) {
    console.error('获取照片列表失败:', error);
  }
};

const getAccessToken = () => {
  const authItemName = "authorize";
  const str = localStorage.getItem(authItemName) || sessionStorage.getItem(authItemName);
  if (!str) return null;
  const authObj = JSON.parse(str);
  if (new Date(authObj.expire) <= new Date()) {
    return null;
  }
  return authObj.token;
};

const uploadPhoto = async () => {
  if (selectedFiles.value.length === 0) {
    ElMessage.warning('请选择要上传的照片');
    return;
  }

  const token = getAccessToken();
  if (!token) {
    ElMessage.warning('请先登录');
    return;
  }

  isUploading.value = true;

  const file = selectedFiles.value[0];
  const formData = new FormData();
  formData.append('file', file);
  if (photoDescription.value) {
    formData.append('description', photoDescription.value);
  }

  try {
    const response = await fetch('/api/photos/upload', {
      method: 'POST',
      headers: {
        'Authorization': `Bearer ${token}`
      },
      body: formData
    });

    const result = await response.json();
    if (result.code === 200 && result.data) {
      const newPhoto: Photo = {
        ...result.data,
        isMine: true
      };
      photos.value.unshift(newPhoto);
      ElMessage.success('上传成功！');
    } else {
      ElMessage.error(result.message || '上传失败');
    }
  } catch (error) {
    console.error('上传失败:', error);
    ElMessage.error('上传失败');
  } finally {
    isUploading.value = false;
    closeUploadModal();
  }
};

const deletePhoto = async (photo: Photo) => {
  if (!photo.isMine) return;
  
  const token = getAccessToken();
  if (!token) {
    ElMessage.warning('请先登录');
    return;
  }
  
  if (confirm('确定要删除这张照片吗？')) {
    try {
      const response = await fetch(`/api/photos/${photo.id}`, {
        method: 'DELETE',
        headers: {
          'Authorization': `Bearer ${token}`
        }
      });

      const result = await response.json();
      if (result.code === 200) {
        const index = photos.value.findIndex(p => p.id === photo.id);
        if (index !== -1) {
          photos.value.splice(index, 1);
        }
        ElMessage.success('删除成功');
      } else {
        ElMessage.error(result.message || '删除失败');
      }
    } catch (error) {
      console.error('删除失败:', error);
      ElMessage.error('删除失败');
    }
  }
};

const previewPhoto = (photo: Photo) => {
  previewPhotoData.value = photo;
};

const closePreview = () => {
  previewPhotoData.value = null;
};

const handleLike = async (photo: Photo) => {
  const now = Date.now();
  const lastTime = lastLikeTime.value[photo.id] || 0;
  const timeDiff = now - lastTime;
  
  likedPhotos.value.add(photo.id);
  photo.likes++;
  
  if (timeDiff < 1500) {
    comboCounts.value[photo.id] = (comboCounts.value[photo.id] || 1) + 1;
  } else {
    comboCounts.value[photo.id] = 1;
  }
  
  lastLikeTime.value[photo.id] = now;
  
  if (comboTimeout.value[photo.id]) {
    clearTimeout(comboTimeout.value[photo.id]);
  }
  
  comboTimeout.value[photo.id] = window.setTimeout(() => {
    delete comboCounts.value[photo.id];
  }, 2000);
  
  showFlyingHearts(photo.id, comboCounts.value[photo.id]);
  
  try {
    await fetch(`/api/photos/${photo.id}/like`, {
      method: 'POST'
    });
  } catch (error) {
    console.error('点赞失败:', error);
  }
};

const showFlyingHearts = (photoId: number, combo: number) => {
  const colors = ['#ff6b9d', '#ff8fab', '#ffb3c6', '#ffc5d0', '#ffd6dc', '#ff4757', '#ff6b81', '#ffa502'];
  const heartCount = Math.min(5 + combo, 12);
  
  const hearts = Array.from({ length: heartCount }, (_, i) => ({
    x: (Math.random() - 0.5) * 80,
    size: 12 + Math.random() * 12 + (combo * 2),
    color: colors[Math.floor(Math.random() * colors.length)]
  }));
  
  flyingHearts.value[photoId] = hearts;
  
  setTimeout(() => {
    delete flyingHearts.value[photoId];
  }, 1500);
};

const formatDate = (dateString: string) => {
  const date = new Date(dateString);
  const now = new Date();
  const diff = now.getTime() - date.getTime();
  const days = Math.floor(diff / (1000 * 60 * 60 * 24));
  
  if (days === 0) {
    const hours = Math.floor(diff / (1000 * 60 * 60));
    if (hours === 0) {
      const minutes = Math.floor(diff / (1000 * 60));
      return `${minutes}分钟前`;
    }
    return `${hours}小时前`;
  } else if (days < 7) {
    return `${days}天前`;
  } else {
    return `${date.getMonth() + 1}月${date.getDate()}日`;
  }
};

onMounted(() => {
  fetchPhotos();
});
</script>

<style scoped>
.gallery-container {
  min-height: 100vh;
  padding: 80px 20px 40px;
  background: linear-gradient(135deg, #1a1a2e 0%, #16213e 50%, #0f3460 100%);
}

.gallery-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 30px;
  padding: 20px 30px;
  background: rgba(255, 255, 255, 0.05);
  border-radius: 20px;
  backdrop-filter: blur(10px);
}

.header-content {
  color: white;
}

.gallery-title {
  font-size: 2.5rem;
  font-weight: bold;
  margin: 0;
  text-shadow: 2px 2px 4px rgba(0, 0, 0, 0.3);
}

.gallery-subtitle {
  font-size: 1.1rem;
  color: rgba(255, 255, 255, 0.7);
  margin: 5px 0 0;
}

.upload-btn-wrapper {
  position: relative;
}

.file-input {
  display: none;
}

.upload-btn {
  background: linear-gradient(135deg, #ff6b9d 0%, #c44569 100%);
  border: none;
  border-radius: 30px;
  padding: 12px 24px;
  color: white;
  font-weight: bold;
  cursor: pointer;
  transition: all 0.3s ease;
  box-shadow: 0 4px 15px rgba(255, 107, 157, 0.4);
}

.upload-btn:hover {
  transform: translateY(-2px);
  box-shadow: 0 6px 20px rgba(255, 107, 157, 0.6);
}

.upload-modal,
.preview-modal {
  position: fixed;
  top: 0;
  left: 0;
  width: 100%;
  height: 100%;
  background: rgba(0, 0, 0, 0.8);
  display: flex;
  justify-content: center;
  align-items: center;
  z-index: 1000;
}

.modal-content {
  background: linear-gradient(135deg, #2d2d44 0%, #1a1a2e 100%);
  border-radius: 20px;
  width: 90%;
  max-width: 600px;
  max-height: 80vh;
  overflow: hidden;
}

.modal-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 20px 30px;
  border-bottom: 1px solid rgba(255, 255, 255, 0.1);
}

.modal-header h3 {
  color: white;
  margin: 0;
  font-size: 1.3rem;
}

.close-btn {
  color: rgba(255, 255, 255, 0.6);
  font-size: 2rem;
  cursor: pointer;
  transition: color 0.3s;
}

.close-btn:hover {
  color: white;
}

.modal-body {
  padding: 20px 30px;
}

.preview-section {
  margin-bottom: 20px;
}

.preview-grid {
  display: grid;
  grid-template-columns: repeat(4, 1fr);
  gap: 10px;
}

.preview-item {
  position: relative;
  aspect-ratio: 1;
  border-radius: 10px;
  overflow: hidden;
}

.preview-item img {
  width: 100%;
  height: 100%;
  object-fit: cover;
}

.remove-preview {
  position: absolute;
  top: 5px;
  right: 5px;
  background: rgba(0, 0, 0, 0.6);
  color: white;
  width: 24px;
  height: 24px;
  border-radius: 50%;
  display: flex;
  justify-content: center;
  align-items: center;
  cursor: pointer;
  font-size: 1rem;
}

.description-input {
  width: 100%;
}

.modal-footer {
  display: flex;
  justify-content: flex-end;
  gap: 10px;
  padding: 20px 30px;
  border-top: 1px solid rgba(255, 255, 255, 0.1);
}

.filter-bar {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 20px;
  padding: 0 10px;
}

.filter-tabs {
  display: flex;
  gap: 10px;
}

.filter-tab {
  padding: 10px 20px;
  border-radius: 20px;
  background: rgba(255, 255, 255, 0.1);
  color: rgba(255, 255, 255, 0.7);
  border: none;
  cursor: pointer;
  transition: all 0.3s ease;
}

.filter-tab.active {
  background: linear-gradient(135deg, #ff6b9d 0%, #c44569 100%);
  color: white;
}

.filter-tab:hover {
  background: rgba(255, 255, 255, 0.2);
}

.search-box {
  width: 250px;
}

.search-input {
  background: rgba(255, 255, 255, 0.1);
  border: 1px solid rgba(255, 255, 255, 0.2);
  border-radius: 30px;
  color: white;
}

.search-input::placeholder {
  color: rgba(255, 255, 255, 0.5);
}

.photo-grid {
  display: grid;
  grid-template-columns: repeat(auto-fill, minmax(280px, 1fr));
  gap: 20px;
  padding: 0 10px;
}

.photo-card {
  background: rgba(255, 255, 255, 0.05);
  border-radius: 15px;
  overflow: hidden;
  transition: all 0.3s ease;
  cursor: pointer;
}

.photo-card:hover {
  transform: translateY(-5px);
  box-shadow: 0 10px 30px rgba(0, 0, 0, 0.3);
}

.photo-image-wrapper {
  position: relative;
  aspect-ratio: 4/3;
  overflow: hidden;
}

.photo-image-wrapper img {
  width: 100%;
  height: 100%;
  object-fit: cover;
  transition: transform 0.3s ease;
}

.photo-card:hover .photo-image-wrapper img {
  transform: scale(1.1);
}

.photo-overlay {
  position: absolute;
  top: 0;
  left: 0;
  right: 0;
  bottom: 0;
  background: linear-gradient(to top, rgba(0, 0, 0, 0.8), transparent);
  opacity: 0;
  transition: opacity 0.3s ease;
  display: flex;
  flex-direction: column;
  justify-content: space-between;
  padding: 10px;
}

.photo-card:hover .photo-overlay {
  opacity: 1;
}

.photo-info {
  display: flex;
  justify-content: space-between;
  color: white;
  font-size: 0.85rem;
}

.photo-author {
  background: rgba(255, 255, 255, 0.2);
  padding: 4px 10px;
  border-radius: 10px;
}

.photo-date {
  opacity: 0.8;
}

.photo-actions {
  display: flex;
  justify-content: flex-end;
}

.delete-btn {
  background: rgba(255, 100, 100, 0.8);
  padding: 6px 12px;
  border-radius: 10px;
  color: white;
  transition: all 0.3s ease;
}

.delete-btn:hover {
  background: rgba(255, 50, 50, 1);
}

.photo-description {
  padding: 15px;
  color: rgba(255, 255, 255, 0.8);
  font-size: 0.95rem;
  white-space: nowrap;
  overflow: hidden;
  text-overflow: ellipsis;
}

.photo-footer {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 0 15px 15px;
}

.avatar-wrapper {
  display: flex;
  align-items: center;
  gap: 8px;
}

.photo-avatar {
  width: 28px;
  height: 28px;
  border-radius: 50%;
  object-fit: cover;
}

.like-btn-container {
  position: relative;
  display: flex;
  align-items: center;
}

.like-btn {
  display: flex;
  align-items: center;
  gap: 6px;
  cursor: pointer;
  transition: all 0.3s ease;
  padding: 6px 12px;
  border-radius: 20px;
  background: rgba(255, 255, 255, 0.1);
  transition: all 0.3s ease;
}

.like-btn:hover {
  background: rgba(255, 107, 157, 0.3);
}

.like-btn.liked {
  background: rgba(255, 107, 157, 0.3);
}

.like-btn.combo {
  animation: comboPulse 0.3s ease;
}

@keyframes comboPulse {
  0%, 100% {
    transform: scale(1);
  }
  50% {
    transform: scale(1.15);
  }
}

.combo-badge {
  background: linear-gradient(135deg, #ff6b9d 0%, #ff4757 100%);
  color: white;
  font-size: 0.75rem;
  font-weight: bold;
  padding: 2px 8px;
  border-radius: 10px;
  animation: comboBadgePop 0.3s ease;
  pointer-events: none;
  user-select: none;
  -webkit-user-select: none;
  -moz-user-select: none;
  -ms-user-select: none;
}

@keyframes comboBadgePop {
  0% {
    transform: scale(0);
    opacity: 0;
  }
  50% {
    transform: scale(1.3);
  }
  100% {
    transform: scale(1);
    opacity: 1;
  }
}

.heart-icon {
  width: 18px;
  height: 18px;
  transition: all 0.3s ease;
}

.heart-icon.outline {
  color: rgba(255, 255, 255, 0.6);
}

.heart-icon.filled {
  color: #ff6b9d;
  animation: heartBeat 0.5s ease;
}

@keyframes heartBeat {
  0% {
    transform: scale(1);
  }
  50% {
    transform: scale(1.3);
  }
  100% {
    transform: scale(1);
  }
}

.like-count {
  color: rgba(255, 255, 255, 0.8);
  font-size: 0.9rem;
  font-weight: 500;
  pointer-events: none;
  user-select: none;
  -webkit-user-select: none;
  -moz-user-select: none;
  -ms-user-select: none;
}

.flying-hearts {
  position: absolute;
  top: 0;
  right: 0;
  pointer-events: none;
  z-index: 10;
}

.flying-heart {
  position: absolute;
  font-size: var(--size, 16px);
  color: var(--color, #ff6b9d);
  animation: flyUp 1.2s ease-out forwards;
  opacity: 0;
  animation-delay: var(--delay, 0s);
  filter: drop-shadow(0 2px 4px rgba(0, 0, 0, 0.3));
}

@keyframes flyUp {
  0% {
    transform: translateY(0) translateX(var(--x)) scale(1) rotate(0deg);
    opacity: 1;
  }
  25% {
    transform: translateY(-30px) translateX(calc(var(--x) * 0.5)) scale(1.1) rotate(15deg);
  }
  50% {
    transform: translateY(-60px) translateX(var(--x)) scale(0.9) rotate(-10deg);
  }
  75% {
    transform: translateY(-80px) translateX(calc(var(--x) * 1.5)) scale(0.7) rotate(5deg);
  }
  100% {
    transform: translateY(-120px) translateX(calc(var(--x) * 2)) scale(0.3) rotate(0deg);
    opacity: 0;
  }
}

.preview-like-btn {
  display: flex;
  align-items: center;
  gap: 8px;
  cursor: pointer;
  padding: 10px 20px;
  border-radius: 25px;
  background: rgba(255, 255, 255, 0.1);
  transition: all 0.3s ease;
}

.preview-like-btn:hover {
  background: rgba(255, 107, 157, 0.3);
}

.preview-like-btn .heart-icon {
  width: 22px;
  height: 22px;
}

.empty-state {
  text-align: center;
  padding: 60px 20px;
}

.empty-icon {
  font-size: 4rem;
  margin-bottom: 20px;
}

.empty-state p {
  color: rgba(255, 255, 255, 0.6);
  font-size: 1.1rem;
  margin: 10px 0;
}

.empty-hint {
  color: rgba(255, 107, 157, 0.8) !important;
}

.preview-content {
  position: relative;
  max-width: 90vw;
  max-height: 90vh;
  background: rgba(0, 0, 0, 0.9);
  border-radius: 20px;
  overflow: hidden;
}

.preview-close {
  position: absolute;
  top: 20px;
  right: 20px;
  color: rgba(255, 255, 255, 0.6);
  font-size: 3rem;
  cursor: pointer;
  z-index: 10;
  transition: color 0.3s;
}

.preview-close:hover {
  color: white;
}

.preview-content img {
  max-width: 100%;
  max-height: 70vh;
  object-fit: contain;
}

.preview-info {
  padding: 20px 30px;
  color: white;
}

.preview-author {
  display: flex;
  align-items: center;
  gap: 10px;
  margin-bottom: 10px;
}

.preview-avatar {
  width: 40px;
  height: 40px;
  border-radius: 50%;
  object-fit: cover;
}

.preview-description {
  font-size: 1.1rem;
  margin: 10px 0;
  opacity: 0.8;
}

.preview-meta {
  display: flex;
  gap: 20px;
  color: rgba(255, 255, 255, 0.6);
  font-size: 0.9rem;
}

@media (max-width: 768px) {
  .gallery-header {
    flex-direction: column;
    gap: 15px;
    text-align: center;
  }
  
  .gallery-title {
    font-size: 1.8rem;
  }
  
  .filter-bar {
    flex-direction: column;
    gap: 15px;
  }
  
  .search-box {
    width: 100%;
  }
  
  .photo-grid {
    grid-template-columns: repeat(auto-fill, minmax(150px, 1fr));
    gap: 15px;
  }
  
  .preview-grid {
    grid-template-columns: repeat(3, 1fr);
  }
}
</style>