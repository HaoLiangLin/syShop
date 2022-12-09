<template>
  <div class="container">
    <van-search
      v-model="keyWord"
      placeholder="请输入搜索关键词"
      shape="round"
      @search="onSearch"
    >
      <template #left>
        <div class="left">
          <van-icon @click="onBack" name="arrow-left" />
        </div>
      </template>
      <template #right-icon>
        <div class="textBtn" @click="onSearch">搜 索</div>
      </template>
    </van-search>
    <div class="history" v-if="historyLength">
      <div class="header">
        <span>历史搜索</span>
        <van-icon name="delete-o" color="#999999" size="18" @click="onDelete"/>
      </div>
      <div class="content">
        <div>
          <span v-for="(item, index) in history" :key="index" @click="onClick(item)">{{item}}</span>
        </div>
      </div>
    </div>
  </div>
</template>

<script>
export default {
  name: 'Home-Search',
  data() {
    return {
      keyWord: '',
      history: []
    }
  },
  methods: {
    onBack() {
      this.$router.back()
    },
    onSearch() {
      let search = window.localStorage.getItem('search')
      if (search != null) {
        search = search.split(',')
        const auth = search.some(item => {
          return item === this.keyWord
        })
        if (!auth) {
          search.unshift(this.keyWord)
        }
        window.localStorage.setItem('search', search)
      } else {
        if (this.keyWord) {
          window.localStorage.setItem('search', [this.keyWord])
        }
      }
      this.$router.push({
        name: 'goodsList',
        query: {
          name: this.keyWord
        }
      })
    },
    initData() {
      let search = window.localStorage.getItem('search')
      if (search != null) {
        search = search.split(',')
        this.history = search
      }
    },
    onDelete() {
      this.$dialog.confirm({
        title: '提示',
        message: '确认删除全部历史记录？'
      })
        .then(() => {
          window.localStorage.removeItem('search')
          this.history = []
        })
        .catch(() => {
          // on cancel
        })
    },
    onClick(val) {
      this.$router.push({
        name: 'goodsList',
        query: {
          name: val
        }
      })
    }
  },
  computed: {
    historyLength() {
      return this.history.length
    }
  },
  created() {
    this.initData()
  }
}
</script>

<style lang="less" scoped>
.container{
  .left{
    margin-right: 20px;
  }
  .textBtn{
    color: rgb(36, 36, 36);
  }
  .history{
    padding-top: 5px;
    background-color: #fff;
    border-radius: 15px 15px 0 0;
    padding: 15px;
    .header{
      /deep/.van-icon{
        float: right;
      }
    }
    .content{
      padding: 5px 0;
      div{
        span{
          display: inline-block;
          margin: 5px 0;
          background-color: #e7e7e7;
          font-size: 14px;
          padding: 5px 10px;
          border-radius: 20px;
          color: #707070;
          margin-right: 10px;
          max-width: 94%;
          // 强制不换行
          white-space:nowrap;
          // 溢出隐藏
          overflow: hidden;
          // 文本溢出显示...
          text-overflow: ellipsis;
        }
      }
    }
  }
}
</style>
