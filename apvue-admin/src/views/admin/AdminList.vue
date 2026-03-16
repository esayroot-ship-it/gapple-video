<template>
  <div class="page-container">
    <el-card>
      <template #header>
        <div class="card-header">
          <span>管理员列表</span>
          <el-button type="primary" @click="handleAdd">创建管理员</el-button>
        </div>
      </template>
      <el-table :data="tableData" style="width: 100%">
        <el-table-column prop="id" label="ID" width="80" />
        <el-table-column prop="username" label="用户名" />
        <el-table-column prop="logintime" label="最后登录" width="180" />
        <el-table-column prop="ip" label="登录IP" width="140" />
         <el-table-column prop="status" label="状态" width="100">
           <template #default="scope">
              <el-tag :type="scope.row.status === 1 ? 'success' : 'danger'">{{ scope.row.status === 1 ? '正常' : '禁用' }}</el-tag>
           </template>
        </el-table-column>
        <el-table-column label="操作" width="180">
          <template #default="scope">
            <el-button link type="danger" size="small" @click="handleDelete(scope.row)">删除</el-button>
          </template>
        </el-table-column>
      </el-table>
    </el-card>

    <!-- 创建弹窗 -->
    <el-dialog
      v-model="dialogVisible"
      title="创建管理员"
      width="40%"
    >
      <el-form :model="formData" label-width="80px">
        <el-form-item label="用户名">
          <el-input v-model="formData.username" placeholder="请输入用户名" />
        </el-form-item>
        <el-form-item label="密码">
          <el-input v-model="formData.password" type="password" placeholder="请输入密码" show-password />
        </el-form-item>
        <el-form-item label="确认密码">
          <el-input v-model="formData.confirmPassword" type="password" placeholder="请再次输入密码" show-password />
        </el-form-item>
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

const fetchData = async () => {
  loading.value = true
  try {
    const res = await Service.all()
    if (res.code === 1) {
      tableData.value = res.data as any[]
    } else {
      ElMessage.error(res.message || '获取数据失败')
    }
  } catch (error) {
    ElMessage.error('网络请求错误')
  } finally {
    loading.value = false
  }
}

onMounted(() => {
  fetchData()
})

const dialogVisible = ref(false)
const formData = reactive({
    username: '',
    password: '',
    confirmPassword: ''
})

const handleAdd = () => {
    formData.username = ''
    formData.password = ''
    formData.confirmPassword = ''
    dialogVisible.value = true
}

const handleSave = async () => {
    if (!formData.username || !formData.password) {
        ElMessage.warning('请填写完整信息')
        return
    }
    if (formData.password !== formData.confirmPassword) {
        ElMessage.error('两次密码输入不一致')
        return
    }
    try {
        const res = await Service.create(formData.username, formData.password)
        if (res.code === 1) {
            ElMessage.success(res.message || '创建成功')
            dialogVisible.value = false
            fetchData()
        } else {
            ElMessage.error(res.message || '创建失败')
        }
    } catch (error) {
        ElMessage.error('创建过程中发生错误')
    }
}

const handleDelete = (row: any) => {
    ElMessageBox.confirm(
    `确定要删除管理员 "${row.username}" 吗？`,
    '警告',
    {
      confirmButtonText: '确定',
      cancelButtonText: '取消',
      type: 'warning',
    }
  )
    .then(async () => {
      try {
        const res = await Service.delete(row.username)
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
</style>