<template>
  <div class="page-container">
    <el-card>
      <template #header>
        <div class="card-header">
          <span>用户管理列表</span>
          <div class="header-search">
            <el-input
              v-model="searchQuery"
              placeholder="请输入用户名搜索"
              clearable
              @clear="handleSearch"
              style="width: 250px; margin-right: 10px;"
            />
            <el-button type="primary" @click="handleSearch">搜索</el-button>
            <el-button @click="handleReset">重置</el-button>
          </div>
        </div>
      </template>
      <el-table v-loading="loading" :data="tableData" style="width: 100%">
        <el-table-column prop="id" label="ID" width="80" />
        <el-table-column label="头像" width="80">
             <template #default="scope">
                <el-avatar :size="40" :src="scope.row.avatarUrl" />
            </template>
        </el-table-column>
        <el-table-column prop="username" label="用户名" />
        <el-table-column prop="nickname" label="昵称" />
        <el-table-column prop="isAdmin" label="角色" width="100">
             <template #default="scope">
                <el-tag :type="scope.row.isAdmin === 1 ? 'danger' : 'info'">{{ scope.row.isAdmin === 1 ? '管理员' : '普通用户' }}</el-tag>
             </template>
        </el-table-column>
        <el-table-column prop="status" label="状态" width="100">
             <template #default="scope">
                <el-tag :type="scope.row.status === 1 ? 'success' : 'danger'">{{ scope.row.status === 1 ? '正常' : '封禁' }}</el-tag>
             </template>
        </el-table-column>
         <el-table-column prop="createdTime" label="注册时间" width="180" />
        <el-table-column label="操作" width="220">
          <template #default="scope">
            <el-button link type="primary" size="small" @click="handleEdit(scope.row)">编辑</el-button>
            <el-button link type="danger" size="small" @click="handleBan(scope.row)">
               {{ scope.row.status === 1 ? '封禁' : '解封' }}
            </el-button>
            <el-button link type="danger" size="small" @click="handleDelete(scope.row)">删除</el-button>
          </template>
        </el-table-column>
      </el-table>
    </el-card>

    <!-- 编辑弹窗 -->
     <el-dialog
      v-model="dialogVisible"
      title="编辑用户信息"
      width="40%"
    >
      <el-form :model="formData" label-width="80px">
        <el-form-item label="用户名">
          <el-input v-model="formData.username" disabled />
        </el-form-item>
        <el-form-item label="密码">
          <el-input v-model="formData.password" type="password" placeholder="留空则可能不修改(取决于后端)" show-password />
        </el-form-item>
        <el-form-item label="昵称">
          <el-input v-model="formData.nickname" />
        </el-form-item>
        <!-- 接口中没有角色和状态的直接更新参数，此处保留 UI 但 API 调用仅更新基础信息 -->
      </el-form>
      <template #footer>
        <span class="dialog-footer">
          <el-button @click="dialogVisible = false">取消</el-button>
          <el-button type="primary" @click="handleSave">确定</el-button>
        </span>
      </template>
    </el-dialog>
  </div>
</template>

<script setup lang="ts">
import { ref, reactive, onMounted } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { Service } from '../../api/admin/services/Service'

const tableData = ref<any[]>([])
const loading = ref(false)
const searchQuery = ref('')

const fetchData = async () => {
  loading.value = true
  try {
    let res
    if (searchQuery.value) {
      res = await Service.getUserByUsername(searchQuery.value)
    } else {
      res = await Service.getallUser()
    }
    
    if (res.code === 1) {
      if (searchQuery.value) {
        // If searching by username, the API might return a single object or a list
        // If it's a single object, wrap it in an array for the table
        if (res.data && !Array.isArray(res.data)) {
          tableData.value = [res.data]
        } else {
          tableData.value = res.data || []
        }
      } else {
        tableData.value = res.data as any[]
      }
    } else {
      tableData.value = []
      ElMessage.error(res.message || '获取用户列表失败')
    }
  } catch (error) {
    ElMessage.error('网络请求错误')
  } finally {
    loading.value = false
  }
}

const handleSearch = () => {
  fetchData()
}

const handleReset = () => {
  searchQuery.value = ''
  fetchData()
}

onMounted(() => {
  fetchData()
})

const dialogVisible = ref(false)
const formData = reactive({
    id: 0,
    username: '',
    password: '',
    nickname: '',
    isAdmin: 0,
    status: 1
})

const handleEdit = (row: any) => {
    formData.id = row.id
    formData.username = row.username
    formData.nickname = row.nickname
    formData.isAdmin = row.isAdmin
    formData.status = row.status
    formData.password = ''
    dialogVisible.value = true
}

const handleSave = async () => {
    try {
        // const userUpdateData = {
        //     id: formData.id,
        //     username: formData.username,
        //     password: formData.password,
        //     nickname: formData.nickname,
        //     isadmin: formData.isAdmin, // Map isAdmin to isadmin
        //     status: formData.status
        // }
        // const res = await Service.updateUser(userUpdateData)
        // if (res.code === 1) {
        //     ElMessage.success(res.message || '更新成功')
        //     dialogVisible.value = false
        //     fetchData()
        // } else {
        //     ElMessage.error(res.message || '更新失败')
        // }
    } catch (error) {
        ElMessage.error('更新过程中发生错误')
    }
}

const handleBan = (row: any) => {
    const isBanning = row.status === 1
    const actionText = isBanning ? '封禁' : '解封'
    ElMessageBox.confirm(
    `确定要${actionText}用户 "${row.username}" 吗？`,
    '警告',
    {
      confirmButtonText: '确定',
      cancelButtonText: '取消',
      type: 'warning',
    }
  )
    .then(async () => {
       try {
           const res = isBanning 
                ? await Service.banUser(row.username) 
                : await Service.rebanUser(row.username)
           
           if (res.code === 1) {
               ElMessage.success(res.message || `${actionText}成功`)
               fetchData()
           } else {
               ElMessage.error(res.message || `${actionText}失败`)
           }
       } catch (error) {
           ElMessage.error(`${actionText}过程中发生错误`)
       }
    })
    .catch(() => {})
}

const handleDelete = (row: any) => {
  ElMessageBox.confirm(
    `确定要删除用户 "${row.username}" 吗？此操作不可逆！`,
    '警告',
    {
      confirmButtonText: '确定',
      cancelButtonText: '取消',
      type: 'warning',
    }
  )
    .then(async () => {
      try {
        const res = await Service.deleteUser(row.id)
        if (res.code === 1) {
          ElMessage.success(res.message || '删除成功')
          fetchData()
        } else {
          ElMessage.error(res.message || '删除失败')
        }
      } catch (error) {
        ElMessage.error('删除过程中发生错误')
      }
    })
    .catch(() => {})
}
</script>

<style scoped>
.card-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
}
.header-search {
  display: flex;
  align-items: center;
}
</style>
