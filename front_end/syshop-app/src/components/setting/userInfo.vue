<template>
  <div class="container">
    <van-nav-bar title="个人信息" left-arrow @click-left="onClickLeft" :fixed="true" :placeholder="true" />
    <van-cell-group>
      <div class="avaratBox">
        <van-cell title="头像" is-link center @click="onAvatar">
          <template #default>
            <div class="avarat">
              <van-image :src="icon ? getIcon(icon) : icon" alt="头像" height="50px" width="50px" :round="true" />
            </div>
          </template>
        </van-cell>
      </div>
      <van-popup v-model="avatarPopup" round position="bottom">
        <van-button type="default" block>拍照上传</van-button>
        <div class="box">
          <van-uploader :after-read="onUploadIcon" result-type="file">
            <van-button type="default">本地上传</van-button>
          </van-uploader>
        </div>
        <div class="cancelAvarat">
          <van-button type="default" block @click="onCancelAvatar">取消</van-button>
        </div>
      </van-popup>

      <van-cell title="用户名" :value="username" is-link :to="{path: `/updateUsername/${this.username}`}"></van-cell>
      <van-cell title="昵称" :value="nickname" is-link :to="{path: `/updateNickname/${this.nickname}`}"></van-cell>
      <van-cell title="名字" :value="name" is-link :to="{path: `/updateName/${this.name != '' ? this.name : '我的名字'}`}"></van-cell>

      <van-cell title="年龄" :value="age" @click="onAge" is-link></van-cell>
      <van-popup v-model="agePopup" round position="bottom">
        <van-picker
          title="修改年龄"
          show-toolbar
          :columns="ages"
          @confirm="onAgeConfirm"
          @cancel="onCancel"
        />
      </van-popup>

      <van-cell title="性别" :value="gender" is-link @click="onGender"></van-cell>
      <van-popup v-model="genderPopup" round position="bottom">
        <van-picker title="修改性别" show-toolbar :columns="genders" @confirm="onGenderConfirm" @cancel="onCancel"/>
      </van-popup>

      <van-cell title="邮箱" :value="email" is-link :to="{path: `/updateEmail/${this.email != '' ? this.email : 'myEmail'}`}"></van-cell>
      <van-cell title="QQ" :value="qq" is-link :to="{path: `/updateQQ/${this.qq != '' ? this.qq : 'myQQ'}`}"></van-cell>

      <van-cell title="生日" :value="birthday" is-link @click="onBirthday"></van-cell>
      <van-popup v-model="birthdayPopup" round position="bottom">
        <van-datetime-picker v-model="currentDate" type="date" title="选择年月日" @cancel="onCancel" @confirm="onBirthdayConfirm" :min-date="minDate" :max-date="maxDate"/>
      </van-popup>
    </van-cell-group>
  </div>
</template>

<script>
import { me, userInfoMe, uploadIcon, updateIcon, updateUserInfo } from '@/api/user.js'
import { getImage } from '@/utils/resources.js'
export default {
  name: 'UserInfo',
  data() {
    return {
      // 用户头像
      icon: '',
      username: '',
      nickname: '',
      name: '',
      age: '',
      gender: '',
      email: '',
      qq: '',
      birthday: '',
      // 默认当前时间
      currentDate: new Date(),
      avatarPopup: false,
      agePopup: false,
      ages: [],
      genderPopup: false,
      genders: ['男', '女', '保密'],
      birthdayPopup: false,
      minDate: new Date(2020, 0, 1),
      maxDate: new Date(2025, 11, 31)
    }
  },
  methods: {
    onClickLeft() {
      this.$router.back()
    },
    getIcon(icon) {
      return getImage(icon)
    },
    onAvatar() {
      this.avatarPopup = true
    },
    onUploadIcon(file) {
      // 新建表单对象
      const formdata = new FormData()
      // 向表单添加数据
      formdata.append('file', file.file)
      uploadIcon(formdata).then(res => {
        const data = res.data
        if (!data.success) {
          this.avatarPopup = false
          this.$notify({ type: 'warning', message: data.message })
        } else {
          this.icon = data.data
          console.log(this.icon)
          this.avatarPopup = false
          updateIcon(this.icon).then(res => {
            const result = res.data
            if (!result.success) {
              this.$notify({ type: 'warning', message: result.message })
            }
          }).catch(err => {
            this.$notify({ type: 'danger', message: err.message })
          })
        }
      }).catch(err => {
        this.$notify({ type: 'danger', message: err.message })
      })
    },
    onCancelAvatar() {
      this.avatarPopup = false
    },
    // 年龄点击事件
    onAge() {
      this.agePopup = true
    },
    onAgeConfirm(value) {
      updateUserInfo(null, null, value, null, null, null).then(res => {
        const data = res.data
        if (!data.success) {
          this.$notify({ type: 'warning', message: data.message })
          console.log(data.message)
        } else {
          this.age = value
          this.$notify({ type: 'success', message: '年龄修改成功' })
        }
      }).catch(err => {
        this.$notify({ type: 'danger', message: err.message })
      })
      this.agePopup = false
    },
    // 性别点击事件
    onGender() {
      this.genderPopup = true
    },
    // 修改性别确认事件
    onGenderConfirm(value) {
      updateUserInfo(null, value, null, null, null, null).then(res => {
        const data = res.data
        if (!data.success) {
          this.$notify({ type: 'warning', message: data.message })
          console.log(data.message)
        } else {
          this.gender = value
          this.$notify({ type: 'success', message: '性别修改成功' })
        }
      }).catch(err => {
        this.$notify({ type: 'danger', message: err.message })
      })
      this.genderPopup = false
    },
    // 生日点击事件
    onBirthday() {
      this.birthdayPopup = true
    },
    // 修改生日确认事件
    onBirthdayConfirm(value) {
      const year = value.getFullYear()
      let month = value.getMonth() + 1
      let day = value.getDate()

      if (month < 10) {
        month = `0${month}`
      }

      if (day < 10) {
        day = `0${day}`
      }

      const birthday = `${year}-${month}-${day}`

      updateUserInfo(null, null, null, null, (value.getTime() + (24 * 60 * 60 * 1000)), null).then(res => {
        const data = res.data
        if (!data.success) {
          this.$notify({ type: 'warning', message: data.message })
          console.log(data.message)
        } else {
          this.birthday = birthday
          this.$notify({ type: 'success', message: '生日修改成功' })
        }
      }).catch(err => {
        this.$notify({ type: 'danger', message: err.message })
      })
      this.birthdayPopup = false
    },

    // 修改弹框取消事件
    onCancel() {
      this.genderPopup = false
      this.agePopup = false
      this.birthdayPopup = false
    }
  },
  created() {
    me().then(res => {
      const data = res.data.data
      if (data) {
        this.icon = data.icon
        this.nickname = data.nickname
        this.username = data.username
      }
    })
    userInfoMe().then(res => {
      const data = res.data.data
      if (data) {
        this.name = data.fullName
        this.gender = data.gender
        this.age = data.age
        this.gender = data.gender
        this.birthday = data.birthday
        this.email = data.email
        this.qq = data.qq
      }
    })
  },
  mounted() {
    const date = new Date()
    const year = date.getFullYear()
    const minYear = year - 100
    const maxYear = year + 100
    this.minDate = new Date(minYear, 0, 1)
    this.maxDate = new Date(maxYear, 11, 31)

    for (let i = 1; i <= 100; i++) {
      this.ages.push(i)
    }
  }
}
</script>

<style lang="less" scoped>
.container {
  .avaratBox {
    .avarat {
      margin-top: 6px;
    }
    /deep/.van-cell {
      padding-top: 0;
      padding-bottom: 0;
    }
  }
  .box {
    width: 100%;
  }
  /deep/.van-button {
    width: 100%;
  }
  // 修改van-uploader宽度
  /deep/.van-uploader {
    width: 100%;
    .van-uploader__wrapper {
      width: 100%;
      display: block;
      -webkit-flex-wrap: wrap;
      flex-wrap: wrap;
    }
  }
  .cancelAvarat {
    padding-top: 10px;
    background-color: rgba(0, 0, 0, 0.3);
  }
}
</style>
