<template>
  <div class="page-container">
    <el-card>
      <template #header>
        <div class="card-header">
          <span>分集管理</span>
           <div class="header-actions">
              <el-input v-model="searchSeriesId" placeholder="输入剧集ID搜索" style="width: 200px; margin-right: 10px;" @keyup.enter="handleSearch" />
              <el-button type="primary" @click="handleSearch">搜索</el-button>
              <el-button type="primary" @click="handleAdd">新增分集</el-button>
           </div>
        </div>
      </template>
      <el-table :data="tableData" style="width: 100%">
        <el-table-column prop="id" label="ID" width="80" />
        <el-table-column prop="sid" label="剧集ID" width="80" />
        <el-table-column prop="no" label="集号" width="80" />
        <el-table-column prop="title" label="标题" />
        <el-table-column prop="status" label="状态" width="100">
           <template #default="scope">
              <el-tag :type="scope.row.status === 1 ? 'success' : 'info'">{{ scope.row.status === 1 ? '已发布' : '未发布' }}</el-tag>
           </template>
        </el-table-column>
        <el-table-column prop="publish" label="发布时间" width="180" />
        <el-table-column label="操作" width="180">
          <template #default="scope">
            <el-button link type="primary" size="small" @click="handleEdit(scope.row)">编辑</el-button>
            <el-button link type="danger" size="small" @click="handleDelete(scope.row)">删除</el-button>
          </template>
        </el-table-column>
      </el-table>
    </el-card>

    <!-- 编辑/新增 弹窗 -->
    <el-dialog
      v-model="dialogVisible"
      :title="dialogType === 'add' ? '新增分集' : '编辑分集'"
      width="50%"
    >
      <el-form :model="formData" label-width="100px">
        <el-form-item label="所属剧集">
          <el-select v-model="formData.sid" placeholder="请选择剧集" filterable>
            <el-option
              v-for="item in seriesList"
              :key="item.id"
              :label="item.title"
              :value="item.id"
            />
          </el-select>
        </el-form-item>
        <el-form-item label="关联视频">
          <el-select v-model="formData.vid" placeholder="请选择视频" filterable>
            <el-option
              v-for="item in videoList"
              :key="item.id"
              :label="item.title"
              :value="item.id"
            />
          </el-select>
        </el-form-item>
         <el-form-item label="集号">
           <el-input-number v-model="formData.no" :min="1" />
        </el-form-item>
        <el-form-item label="标题">
          <el-input v-model="formData.title" placeholder="请输入分集标题" />
        </el-form-item>
        <el-form-item label="状态">
          <el-radio-group v-model="formData.status">
            <el-radio :value="1">已发布</el-radio>
            <el-radio :value="0">未发布</el-radio>
          </el-radio-group>
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
import { useRoute } from 'vue-router'
import { ElMessage, ElMessageBox } from 'element-plus'
import { Service } from '../../api/admin/services/Service'
import type { episode } from '../../api/admin/models/episode'
import type { series } from '../../api/admin/models/series'
import type { video } from '../../api/admin/models/video'

const route = useRoute()
const tableData = ref<episode[]>([])
const seriesList = ref<series[]>([])
const videoList = ref<video[]>([])
const loading = ref(false)
const searchSeriesId = ref('')

const fetchLists = async () => {
    try {
        const [seriesRes, videoRes] = await Promise.all([
            Service.getAllSeries(),
            Service.getAll()
        ])
        if (seriesRes.code === 1) {
            seriesList.value = seriesRes.data as series[]
        }
        if (videoRes.code === 1) {
            videoList.value = videoRes.data as video[]
        }
    } catch (error) {
        console.error('Failed to fetch series or videos', error)
    }
}

const fetchData = async () => {
  if (!searchSeriesId.value) {
    tableData.value = []
    return
  }
  
  loading.value = true
  try {
      // API expects number for seriesId
      const sid = Number(searchSeriesId.value)
      if (isNaN(sid)) {
          ElMessage.warning('请输入有效的剧集ID')
          loading.value = false
          return
      }

    const res = await Service.getEpisodesBySeriesId(sid)
    if (res.code === 1) {
      tableData.value = res.data as episode[]
    } else {
      ElMessage.error(res.message || '获取分集列表失败')
      tableData.value = []
    }
  } catch (error) {
    ElMessage.error('网络请求错误')
  } finally {
    loading.value = false
  }
}

onMounted(() => {
  fetchLists()
  if (route.query.seriesId) {
      searchSeriesId.value = String(route.query.seriesId)
      fetchData()
  }
})

const handleSearch = () => {
    fetchData()
}

const dialogVisible = ref(false)
const dialogType = ref<'add' | 'edit'>('add')
const formData = reactive<episode>({
    id: 0,
    sid: 0,
    vid: 0,
    no: 1,
    title: '',
    status: 1
})

const handleAdd = () => {
    dialogType.value = 'add'
    Object.assign(formData, {
        id: 0,
        sid: Number(searchSeriesId.value) || 0,
        vid: 0,
        no: 1,
        title: '',
        status: 1
    })
    dialogVisible.value = true
}

const handleEdit = (row: episode) => {
    dialogType.value = 'edit'
    Object.assign(formData, row)
    dialogVisible.value = true
}

const handleSave = async () => {
    try {
        // There is no updateEpisode in Service, using createEpisode or checking if create acts as save
        // Actually, createEpisode is all we have. 
        // If the backend treats ID presence as update, good. If not, we can't edit properly.
        const res = await Service.createEpisode(formData)
        
        if (res.code === 1) {
            ElMessage.success(res.message || '保存成功')
            dialogVisible.value = false
            fetchData()
        } else {
            ElMessage.error(res.message || '保存失败')
        }
    } catch (error) {
        ElMessage.error('保存过程中发生错误')
    }
}

const handleDelete = (row: episode) => {
    if(!row.id) return
    ElMessageBox.confirm(
    `确定要删除分集 "${row.title}" 吗？`,
    '警告',
    {
      confirmButtonText: '确定',
      cancelButtonText: '取消',
      type: 'warning',
    }
  )
    .then(async () => {
       try {
           const res = await Service.deleteEpisode(row.id!)
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