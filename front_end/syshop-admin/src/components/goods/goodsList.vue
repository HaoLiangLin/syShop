<template>
  <div class="container">
    <el-breadcrumb separator="/">
      <el-breadcrumb-item>商品管理</el-breadcrumb-item>
      <el-breadcrumb-item>商品列表</el-breadcrumb-item>
    </el-breadcrumb>
    <div class="func">
      <table class="input">
        <tr>
          <td>
            <el-cascader
              size="small"
              v-model="cid"
              placeholder="商品分类"
              :options="categoryOption"
              :props="{ value: 'id', label: 'name', children: 'children', checkStrictly: true }">
            </el-cascader>
          </td>
          <td>
            <el-input
              size="small"
              placeholder="发货省份"
              v-model="province">
            </el-input>
          </td>
          <td>
            <el-input
              size="small"
              placeholder="发货城市"
              v-model="city">
            </el-input>
          </td>
          <td>
            <el-input
              size="small"
              placeholder="发货区县"
              v-model="district">
            </el-input>
          </td>
          <td>
            <el-select v-model="status" placeholder="请选择" size="small">
              <el-option
                v-for="item in statusOption"
                :key="item.value"
                :label="item.label"
                :value="item.value">
              </el-option>
            </el-select>
          </td>
        </tr>
        <tr>
          <td>
            <el-input
              size="small"
              placeholder="保修期限"
              v-model="warrantyTime">
            </el-input>
          </td>
          <td>
            <el-input
              size="small"
              placeholder="包退期限"
              v-model="refundTime">
            </el-input>
          </td>
          <td>
            <el-input
              size="small"
              placeholder="包换期限"
              v-model="changerTime">
            </el-input>
          </td>
          <td>
            <el-select v-model="recommend" placeholder="请选择" size="small">
              <el-option
                v-for="item in recommendOption"
                :key="item.value"
                :label="item.label"
                :value="item.value">
              </el-option>
            </el-select>
          </td>
        </tr>
      </table>
      <div class="btn">
        <el-button type="success" icon="el-icon-search" size="small" @click="onSearch">搜索</el-button>
      </div>
    </div>
    <div class="table">
      <el-table
        :data="goodsData"
        stripe
        :border="true"
        ref="goodsTabel"
        style="width: 100%">
        <el-table-column type="expand">
          <template slot-scope="props">
          <el-descriptions title="商品详细信息" :column="4" style="margin: 0 10px">
            <el-descriptions-item label="商品封面" :span="4">
              <div v-if="!props.row.isUpdate">
                <el-image
                  v-for="(image, index) in props.row.images" :key="index"
                  style="width: 100px; height: 100px"
                  :src="setIcon(image)"
                  fit="fit"></el-image>
              </div>
              <el-upload class="upload-demo"
                v-if="props.row.isUpdate"
                :action="uploadIcon(props.row)"
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
            <el-descriptions-item label="商品名称" :span="2">
              <div v-if="!props.row.isUpdate">{{ props.row.name }}</div>
              <el-input v-if="props.row.isUpdate" v-model="props.row.name" placeholder="请输入商品名称"></el-input>
            </el-descriptions-item>
            <el-descriptions-item label="商品分类" :span="2">
              <div v-if="!props.row.isUpdate">{{ props.row.cname }}</div>
              <el-cascader
                v-if="props.row.isUpdate"
                v-model="props.row.select"
                :options="categoryOption"
                :props="{ value: 'id', label: 'name', children: 'children', checkStrictly: true }">
              </el-cascader>
            </el-descriptions-item>
            <el-descriptions-item label="发货省份">
              <div v-if="!props.row.isUpdate">{{ props.row.province }}</div>
              <el-input v-if="props.row.isUpdate" v-model="props.row.province" placeholder="请输入商品发货省份"></el-input>
            </el-descriptions-item>
            <el-descriptions-item label="发货城市">
              <div v-if="!props.row.isUpdate">{{ props.row.city }}</div>
              <el-input v-if="props.row.isUpdate" v-model="props.row.city" placeholder="请输入商品发货城市"></el-input>
            </el-descriptions-item>
            <el-descriptions-item label="发货区/县">
              <div v-if="!props.row.isUpdate">{{ props.row.district }}</div>
              <el-input v-if="props.row.isUpdate" v-model="props.row.district" placeholder="请输入商品发货区县"></el-input>
            </el-descriptions-item>
            <el-descriptions-item label="发货详细地址">
              <div v-if="!props.row.isUpdate">{{ props.row.address }}</div>
              <el-input v-if="props.row.isUpdate" v-model="props.row.address" placeholder="请输入商品发货详细地址"></el-input>
            </el-descriptions-item>
            <el-descriptions-item label="物流运费(元)">
              <div v-if="!props.row.isUpdate">{{ props.row.postage }}</div>
              <el-input v-if="props.row.isUpdate" v-model="props.row.postage" placeholder="请输入商品物流运费"></el-input>
            </el-descriptions-item>
            <el-descriptions-item label="保修期(天)">
              <div v-if="!props.row.isUpdate">{{ props.row.warrantyTime }}</div>
              <el-input v-if="props.row.isUpdate" v-model="props.row.warrantyTime" placeholder="请输入商品保修期"></el-input>
            </el-descriptions-item>
            <el-descriptions-item label="包退期(天)">
              <div v-if="!props.row.isUpdate">{{ props.row.refundTime }}</div>
              <el-input v-if="props.row.isUpdate" v-model="props.row.refundTime" placeholder="请输入商品包退期"></el-input>
            </el-descriptions-item>
            <el-descriptions-item label="包换期(天)">
              <div v-if="!props.row.isUpdate">{{ props.row.changerTime }}</div>
              <el-input v-if="props.row.isUpdate" v-model="props.row.changerTime" placeholder="请输入商品包换期"></el-input>
            </el-descriptions-item>
            <el-descriptions-item label="商品上架" :span="2">
              <div v-if="!props.row.isUpdate">
                <el-tag size="small" type="success" v-if="props.row.status === 1">已上架</el-tag>
                <el-tag size="small" type="info" v-else-if="props.row.status === 0">未上架</el-tag>
                <el-tag size="small" type="danger" v-else-if="props.row.status === 2">已下架</el-tag>
              </div>
              <div v-if="props.row.isUpdate">
                <el-radio-group v-model="props.row.isStatus" size="mini">
                  <el-radio-button label="上架"></el-radio-button>
                  <el-radio-button label="下架"></el-radio-button>
                </el-radio-group>
              </div>
            </el-descriptions-item>
            <el-descriptions-item label="是否推荐" :span="2">
              <div v-if="!props.row.isUpdate">
                <el-tag size="small" v-if="props.row.recommend" type="success">推荐商品</el-tag>
                <el-tag size="small" v-if="!props.row.recommend">未推荐</el-tag>
              </div>
              <div v-if="props.row.isUpdate">
                <el-switch
                  :disabled="!(props.row.status === 1)"
                  style="display: block"
                  v-model="props.row.recommend"
                  active-color="#13ce66"
                  inactive-color="#ff4949"
                  active-text="推荐"
                  inactive-text="不推荐">
                </el-switch>
              </div>
            </el-descriptions-item>
            <el-descriptions-item label="上架时间" :span="2">{{ props.row.shelvesTime }}</el-descriptions-item>
            <el-descriptions-item label="最后修改时间" :span="2">{{ props.row.updateTime }}</el-descriptions-item>
          </el-descriptions>
          </template>
        </el-table-column>
        <el-table-column
          prop="id"
          label="ID"
          width="180">
        </el-table-column>
        <el-table-column
          label="商品封面"
          width="130">
          <template slot-scope="scope">
            <el-image
              style="width: 100px; height: 100px"
              :src="setIcon(scope.row.firstImage)"
              fit="fit"></el-image>
          </template>
        </el-table-column>
        <el-table-column
          prop="name"
          label="商品名称">
        </el-table-column>
        <el-table-column
          prop="postage"
          label="物流运费">
        </el-table-column>
        <el-table-column
          prop="sale"
          label="商品销量"
          width="100px">
        </el-table-column>
        <el-table-column label="SKU库存" align="center" width="120">
          <template slot-scope="scope">
            <el-button type="primary" icon="el-icon-edit" circle @click="onStock(scope.row)"></el-button>
          </template>
        </el-table-column>
        <el-table-column
          prop="status"
          label="商品状态 "
          width="80px">
          <template slot-scope="scope">
            <el-tag type="success" v-if="scope.row.status === 1">已上架</el-tag>
            <el-tag type="info" v-else-if="scope.row.status === 0">未上架</el-tag>
            <el-tag type="danger" v-else-if="scope.row.status === 2">已下架</el-tag>
          </template>
        </el-table-column>
        <el-table-column
          fixed="right"
          label="操作"
          width="120">
          <template slot-scope="scope">
            <el-button
              v-if="!scope.row.isUpdate"
              @click="onUpdate(scope.row)"
              type="text"
              size="small">
              修改
            </el-button>
            <el-button
              v-if="!scope.row.isUpdate"
              @click="onDelete(scope.row)"
              type="text"
              size="small">
              删除
            </el-button>
            <el-button
              v-if="scope.row.isUpdate"
              @click="onSave(scope.row)"
              type="text"
              size="small">
              保存
            </el-button>
            <el-button
              v-if="scope.row.isUpdate"
              @click="onCancel(scope.row)"
              type="text"
              size="small">
              取消
            </el-button>
          </template>
        </el-table-column>
      </el-table>
    </div>
    <div class="block">
      <el-pagination
        layout="total, prev, pager, next, jumper"
        background
        :total="total"
        :page-size="size"
        v-show="isHidePage"
        :current-page="current"
        @current-change="onChangePage">
      </el-pagination>
    </div>
  </div>
</template>

<script>
import { findSelectCategory, queryGoodsPage, deleteGoods, updateGoods } from '@/api/goods.js'
import { getImage, uploadGoodIcon } from '@/utils/resources.js'
export default {
  name: 'GoodsList',
  data() {
    return {
      size: 9,
      goodsData: [],
      categoryOption: [],
      // 搜索
      isSearch: false,
      goodsId: null,
      cid: null,
      province: null,
      city: null,
      district: null,
      recommend: 1,
      recommendOption: [{ value: 0, label: '未推荐' }, { value: 1, label: '推荐' }],
      warrantyTime: null,
      refundTime: null,
      changerTime: null,
      status: 1,
      statusOption: [{ value: 0, label: '未上架' }, { value: 1, label: '已上架' }, { value: 2, label: '已下架' }],
      // 修改
      updateId: '',
      isHidePage: true,
      total: 0,
      current: 1,
      authorization: window.sessionStorage.getItem('authorization')
    }
  },
  methods: {
    setIcon(icon) {
      return getImage(icon)
    },
    setGoodsData(res) {
      const data = res.data
      if (data.success) {
        this.goodsData = []
        data.data.records.forEach(item => {
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

          this.goodsData.push({
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
          })
        })
        this.total = data.data.total
        this.current = data.data.current
      }
    },
    uploadIcon(row) {
      if (row) {
        this.updateId = row.id
      }
      return uploadGoodIcon()
    },
    initData() {
      this.isSearch = false
      this.goodsId = null
      this.cid = null
      this.province = null
      this.city = null
      this.district = null
      this.recommend = 1
      this.warrantyTime = null
      this.refundTime = null
      this.changerTime = null
      this.status = 1
      const promiseList = [
        findSelectCategory(),
        queryGoodsPage(1, this.size, null, null, null, null, null, null, null, null, null, null)
      ]
      Promise.all(promiseList).then(([r1, r2]) => {
        const d1 = r1.data.data
        this.categoryOption = []
        d1.forEach(option => {
          this.categoryOption.push(option)
        })
        this.setGoodsData(r2)
      }).catch(err => {
        this.$message.error(err.message)
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
    onSearch() {
      this.isSearch = true
      const select = this.cid
      const cid = select[select.length - 1]
      queryGoodsPage(1, this.size, this.goodsId, cid, this.province, this.city, this.district, this.recommend, this.warrantyTime, this.refundTime, this.changerTime, this.status).then(res => {
        this.setGoodsData(res)
      })
    },
    onStock(row) {
      this.$router.push(`/home/goodsItemList/${row.id}`)
    },
    onUpdate(row) {
      this.goodsData.forEach(item => {
        item.isUpdate = false
      })
      row.isUpdate = true
      this.$refs.goodsTabel.toggleRowExpansion(row, true)
    },
    onDelete(row) {
      this.$confirm('此操作将删除该商品, 是否继续?', '提示', {
        confirmButtonText: '确定',
        cancelButtonText: '取消',
        type: 'warning'
      }).then(() => {
        deleteGoods(row.id).then(res => {
          const data = res.data
          if (data.success) {
            this.goodsData.forEach((item, index) => {
              if (item.id === row.id) {
                this.goodsData.splice(index, 1)
              }
            })
            this.$notify({
              title: '成功',
              message: data.message,
              type: 'success'
            })
          } else {
            this.$notify({
              title: '失败',
              message: data.message,
              type: 'warning'
            })
          }
        }).catch(err => {
          this.$message.error(err.message)
        })
      }).catch(() => {
        this.$message({
          type: 'info',
          message: '已取消删除'
        })
      })
    },
    onSave(row) {
      row.isUpdate = false
      let status = null
      if (row.isStatus === '上架') {
        status = 1
      }
      if (row.isStatus === '下架') {
        status = 2
      }
      const select = row.select
      const cid = select[select.length - 1]
      updateGoods(row.id, row.name, null, cid, row.province, row.city, row.district, row.address, row.postage, row.recommend ? 1 : 0, row.warrantyTime, row.refundTime, row.changerTime, status).then(res => {
        const data = res.data
        if (data.success) {
          this.$notify({
            title: '成功',
            message: data.message,
            type: 'success'
          })
        } else {
          this.$notify({
            title: '失败',
            message: data.message,
            type: 'warning'
          })
        }
      }).catch(err => {
        this.$message.error(err.message)
      })
    },
    onCancel(row) {
      row.isUpdate = false
      this.$refs.goodsTabel.toggleRowExpansion(row, false)
    },
    onChangePage(page) {
      if (this.isSearch) {
        const select = this.cid
        const cid = select[select.length - 1]
        queryGoodsPage(page, this.size, this.goodsId, cid, this.province, this.city, this.district, this.recommend, this.warrantyTime, this.refundTime, this.changerTime, this.status).then(res => {
          this.setGoodsData(res)
        })
        return
      }
      queryGoodsPage(page, this.size).then(res => {
        this.setGoodsData(res)
      })
    },
    onSuccess(result) {
      if (result.success) {
        updateGoods(this.updateId, null, result.data, null, null, null, null, null, null, null, null, null, null, null).then(res => {
          const data = res.data
          if (data.success) {
            this.goodsData.forEach(item => {
              if (item.id === this.updateId) {
                const images = result.data.split(',')
                const firstImage = images[0]
                item.images = images
                item.firstImage = firstImage
                item.isUpdate = false
              }
            })
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
    this.initData()
  },
  mounted() {
    this.$bus.$on('init', (val) => {
      if (val) {
        this.goodsData = []
        this.initData()
      }
    })
  }
}
</script>

<style lang="less" scoped>
.container{
  /deep/.el-breadcrumb{
    margin-bottom: 5px;
  }
  .func{
    display: flex;
    .input{
      width: 80%;
    }
    .btn{
      width: 20%;
    }
  }
}
</style>
