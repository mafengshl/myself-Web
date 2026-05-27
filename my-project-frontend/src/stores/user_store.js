import { defineStore } from 'pinia'

export const useUserStore = defineStore('user', {
    state: () => ({
        username: '',
        email: '',
        userId: null,
        token: '',
        isLoggedIn: false,
        role: '' // 添加角色权限字段
    }),

    actions: {
        setUser(userInfo) {
            this.username = userInfo.username || ''
            this.email = userInfo.email || ''
            this.userId = userInfo.userId || null
            this.token = userInfo.token || ''
            this.isLoggedIn = true
        },

        clearUser() {
            this.username = ''
            this.email = ''
            this.userId = null
            this.token = ''
            this.isLoggedIn = false
            this.role = '' // 清除角色信息
        },

        // 添加设置角色的方法
        setRole(role) {
            this.role = role
        }
    },

    persist: true // 启用持久化存储
})
