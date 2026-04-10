<template>
  <div class="image-upload">
    <el-upload
      action="#"
      :auto-upload="false"
      :show-file-list="false"
      :on-change="handleChange"
    >
      <el-button type="primary" size="small">上传图片</el-button>
    </el-upload>
    <div class="image-list" v-if="modelValue && modelValue.length">
      <div class="image-item" v-for="(img, index) in modelValue" :key="index">
        <el-image
          :src="img.imageUrl"
          fit="cover"
          :preview-src-list="[img.imageUrl]"
        />
        <div class="image-actions">
          <el-button size="small" @click="setCover(index)" :disabled="index === 0">设封面</el-button>
          <el-button size="small" type="danger" @click="removeImage(index)">删除</el-button>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ElMessage } from 'element-plus'

const props = defineProps({
  modelValue: {
    type: Array,
    default: () => []
  }
})

const emit = defineEmits(['update:modelValue'])

const handleChange = (file) => {
  const isImage = file.raw.type.startsWith('image/')
  if (!isImage) {
    ElMessage.error('只能上传图片文件')
    return
  }

  const reader = new FileReader()
  reader.onload = (e) => {
    const newImage = {
      imageUrl: e.target.result,
      sort: props.modelValue.length
    }
    emit('update:modelValue', [...props.modelValue, newImage])
  }
  reader.readAsDataURL(file.raw)
}

const setCover = (index) => {
  const images = [...props.modelValue]
  const [image] = images.splice(index, 1)
  images.unshift(image)
  emit('update:modelValue', images)
}

const removeImage = (index) => {
  const images = [...props.modelValue]
  images.splice(index, 1)
  emit('update:modelValue', images)
}
</script>

<style scoped>
.image-upload {
  width: 100%;
}

.image-list {
  display: flex;
  flex-wrap: wrap;
  gap: 10px;
  margin-top: 10px;
}

.image-item {
  width: 120px;
  text-align: center;
}

.image-item .el-image {
  width: 100px;
  height: 100px;
  border-radius: 4px;
}

.image-actions {
  display: flex;
  gap: 5px;
  justify-content: center;
  margin-top: 5px;
}
</style>
