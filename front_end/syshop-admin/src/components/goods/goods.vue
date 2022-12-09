<template>
  <div class="container">
    <el-breadcrumb separator="/">
      <el-breadcrumb-item>商品管理</el-breadcrumb-item>
      <el-breadcrumb-item>查询商品</el-breadcrumb-item>
    </el-breadcrumb>
    <div class="func">
      <div class="search">
        <el-input v-model="goodsId" placeholder="请输入商品ID" style="flex: 4"></el-input>
        <el-button type="default" icon="el-icon-search" @click="onSearch">搜索</el-button>
      </div>
    </div>
    <div class="infoBox">
      <el-descriptions v-if="goods" title="查询结果" direction="vertical" :column="3" border>
        <template slot="extra">
          <el-button type="primary" size="small" @click="onUpdate" v-if="!goods.isUpdate">修改</el-button>
          <el-button type="warning" size="small" @click="onDelete" v-if="!goods.isUpdate">删除</el-button>
          <el-button type="success" size="small" @click="onSave" v-if="goods.isUpdate">保存</el-button>
          <el-button type="default" size="small" @click="onCancel" v-if="goods.isUpdate">取消</el-button>
        </template>
        <el-descriptions-item label="商品封面">
          <div v-if="!goods.isUpdate">
            <el-image
              v-for="(image, index) in goods.images"
              :key="index"
              style="width: 100px; height: 100px"
              :src="setIcon(image)"
              fit="fit"></el-image>
          </div>
          <el-upload class="upload-demo"
            v-if="goods.isUpdate"
            :action="uploadIcon()"
            accept="image/*"
            multiple
            name="files"
            :limit="3"
            :headers="{authorization}"
            :on-success="onSuccess"
            :on-error="onError">
            <el-button size="small" type="primary" icon="el-icon-upload">上传图标</el-button>
          </el-upload>
        </el-descriptions-item>
        <el-descriptions-item label="商品ID">{{goods.id}}</el-descriptions-item>
        <el-descriptions-item label="商品名称">
          <div v-if="!goods.isUpdate">{{goods.name}}</div>
          <el-input v-if="goods.isUpdate" v-model="goods.name"></el-input>
        </el-descriptions-item>
        <el-descriptions-item label="商品分类">
          <div v-if="!goods.isUpdate">{{ goods.cname }}</div>
          <el-cascader
            v-if="goods.isUpdate"
            v-model="goods.select"
            :options="categoryOption"
            :props="{ value: 'id', label: 'name', children: 'children', checkStrictly: true }">
          </el-cascader>
        </el-descriptions-item>
        <el-descriptions-item label="发货省份">
          <div v-if="!goods.isUpdate">{{goods.province}}</div>
          <el-input v-if="goods.isUpdate" v-model="goods.province"></el-input>
        </el-descriptions-item>
        <el-descriptions-item label="发货城市">
          <div v-if="!goods.isUpdate">{{goods.city}}</div>
          <el-input v-if="goods.isUpdate" v-model="goods.city"></el-input>
        </el-descriptions-item>
        <el-descriptions-item label="发货区县">
          <div v-if="!goods.isUpdate">{{goods.district}}</div>
          <el-input v-if="goods.isUpdate" v-model="goods.district"></el-input>
        </el-descriptions-item>
        <el-descriptions-item label="发货地址">
          <div v-if="!goods.isUpdate">{{goods.address}}</div>
          <el-input v-if="goods.isUpdate" v-model="goods.address"></el-input>
        </el-descriptions-item>
        <el-descriptions-item label="物流运费">
          <div v-if="!goods.isUpdate">{{goods.postage}}</div>
          <el-input v-if="goods.isUpdate" v-model="goods.postage"></el-input>
        </el-descriptions-item>
        <el-descriptions-item label="保修期限">
          <div v-if="!goods.isUpdate">{{goods.warrantyTime}}</div>
          <el-input v-if="goods.isUpdate" v-model="goods.warrantyTime"></el-input>
        </el-descriptions-item>
        <el-descriptions-item label="包换期限">
          <div v-if="!goods.isUpdate">{{goods.changerTime}}</div>
          <el-input v-if="goods.isUpdate" v-model="goods.changerTime"></el-input>
        </el-descriptions-item>
        <el-descriptions-item label="包退期限">
          <div v-if="!goods.isUpdate">{{goods.refundTime}}</div>
          <el-input v-if="goods.isUpdate" v-model="goods.refundTime"></el-input>
        </el-descriptions-item>
        <el-descriptions-item label="推荐商品">
          <div v-if="!goods.isUpdate">
            <el-tag v-if="goods.recommend" type="success">推荐商品</el-tag>
            <el-tag size="small" v-if="!goods.recommend">未推荐</el-tag>
          </div>
          <div v-if="goods.isUpdate">
            <el-switch
              :disabled="!(goods.status === 1)"
              style="display: block"
              v-model="goods.recommend"
              active-color="#13ce66"
              inactive-color="#ff4949"
              active-text="推荐"
              inactive-text="不推荐">
            </el-switch>
          </div>
        </el-descriptions-item>
        <el-descriptions-item label="商品状态">
          <div v-if="!goods.isUpdate">
            <el-tag size="small" type="success" v-if="goods.status === 1">已上架</el-tag>
            <el-tag size="small" type="info" v-else-if="goods.status === 0">未上架</el-tag>
            <el-tag size="small" type="danger" v-else-if="goods.status === 2">已下架</el-tag>
          </div>
          <div v-if="goods.isUpdate">
            <el-radio-group v-model="goods.isStatus" size="mini">
              <el-radio-button label="上架"></el-radio-button>
              <el-radio-button label="下架"></el-radio-button>
            </el-radio-group>
          </div>
        </el-descriptions-item>
        <el-descriptions-item label="上架时间">{{goods.shelvesTime}}</el-descriptions-item>
      </el-descriptions>
      <el-result title="404" v-else subTitle="查无记录哦">
        <template slot="icon">
          <el-image :src="require('@/assets/暂无记录.png')"></el-image>
        </template>
      </el-result>
    </div>
  </div>
</template>

<script>
import { queryGoodsById, findSelectCategory, updateGoods } from '@/api/goods.js'
import { getImage, uploadGoodIcon } from '@/utils/resources.js'
export default {
  name: 'Goods',
  data() {
    return {
      goodsId: '',
      goods: null,
      categoryOption: [],
      authorization: window.sessionStorage.getItem('authorization')
    }
  },
  methods: {
    setIcon(icon) {
      return getImage(icon)
    },
    onSearch() {
      queryGoodsById(this.goodsId).then(res => {
        const data = res.data
        if (data.success) {
          const item = data.data

          let recommend = false
          let status = '未上架'
          if (item.recommend === 1) {
            recommend = true
          }
          if (item.status === 1) {
            status = '上架'
          }
          if (item.status === 2) {
            status = '下架'
          }
          let images = item.images.split(',')
          let firstImage = images[0]
          if (!item.images) {
            images = '暂无图片'
            firstImage = '暂无图片'
          }

          const select = this.forOption(item.cid)

          this.goods = {
            id: item.id,
            name: item.name,
            cname: item.cname,
            select,
            firstImage,
            images,
            province: item.province,
            city: item.city,
            district: item.district,
            address: item.address,
            postage: item.postage,
            recommend,
            sale: item.sale,
            warrantyTime: item.warrantyTime,
            refundTime: item.refundTime,
            changerTime: item.changerTime,
            status: item.status,
            isStatus: status,
            shelvesTime: item.shelvesTime,
            updateTime: item.updateTime,
            isUpdate: false
          }
        }
      })
    },
    forOption(code) {
      let category = []
      this.categoryOption.forEach(item => {
        if (item.id === code) {
          category = [item.id]
        }
      })
      this.categoryOption.forEach(item => {
        if (item.children) {
          item.children.forEach(item2 => {
            if (item2.id === code) {
              category = [item.id, item2.id]
            }
          })
        }
      })
      this.categoryOption.forEach(item => {
        if (item.children) {
          item.children.forEach(item2 => {
            if (item2.children) {
              item2.children.forEach(item3 => {
                if (item3.id === code) {
                  category = [item.id, item2.id, item3.id]
                }
              })
            }
          })
        }
      })
      return category
    },
    uploadIcon() {
      return uploadGoodIcon()
    },
    onUpdate() {
      this.goods.isUpdate = true
    },
    onDelete() {
      this.$confirm('此操作将删除该用户, 是否继续?', '提示', {
        confirmButtonText: '确定',
        cancelButtonText: '取消',
        type: 'warning'
      }).then(() => {
      }).catch(() => {
        this.$message({
          type: 'info',
          message: '已取消删除'
        })
      })
    },
    onSave() {
      this.goods.isUpdate = false
    },
    onCancel() {
      this.goods.isUpdate = false
    },
    onSuccess(result) {
      if (result.success) {
        updateGoods(this.goodsId, null, result.data, null, null, null, null, null, null, null, null, null, null, null).then(res => {
          const data = res.data
          if (data.success) {
            const images = result.data.split(',')
            const firstImage = images[0]
            this.goods.images = images
            this.goods.firstImage = firstImage
            this.goods.isUpdate = false
            this.$notify({
              title: '成功',
              message: data.message,
              type: 'success'
            })
          } else {
            this.$notify({
              title: '失败',
              message: result.message,
              type: 'warning'
            })
          }
        })
      } else {
        this.$notify({
          title: '失败',
          message: result.message,
          type: 'warning'
        })
      }
    },
    onError(err) {
      this.$message.error(err.message)
    }
  },
  created() {
    findSelectCategory().then(res => {
      const d1 = res.data.data
      this.categoryOption = []
      d1.forEach(option => {
        this.categoryOption.push(option)
      })
    })
  }
}
</script>

<style lang="less" scoped>
.container{
  /deep/.el-breadcrumb{
    margin-bottom: 10px;
  }
  .func{
    margin-top: 10px;
    .search{
      width: 60%;
      margin: 0 auto;
      display: flex;
      /deep/.el-button{
        flex: 1;
        padding-left: 30px;
      }
    }
  }
  .infoBox{
    margin-top: 10px;
  }
}
</style>
