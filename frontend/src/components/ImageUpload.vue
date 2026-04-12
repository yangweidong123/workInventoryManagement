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
    <div class="image-list" v-if="value && value.length">
      <div class="image-item" v-for="(img, index) in value" :key="index">
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

<script>
import { Message } from 'element-ui'

export default {
  name: 'ImageUpload',
  props: {
    value: {
      type: Array,
      default: () => []
    }
  },
  methods: {
    handleChange(file) {
      const isImage = file.raw.type.startsWith('image/')
      if (!isImage) {
        Message.error('只能上传图片文件')
        return
      }

      const reader = new FileReader()
      reader.onload = (e) => {
        const newImage = {
          imageUrl: e.target.result,
          sort: this.value.length
        }
        this.$emit('input', [...this.value, newImage])
      }
      reader.readAsDataURL(file.raw)
    },
    setCover(index) {
      const images = [...this.value]
      const [image] = images.splice(index, 1)
      images.unshift(image)
      this.$emit('input', images)
    },
    removeImage(index) {
      const images = [...this.value]
      images.splice(index, 1)
      this.$emit('input', images)
    }
  }
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

.image-item >>> .el-image {
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
