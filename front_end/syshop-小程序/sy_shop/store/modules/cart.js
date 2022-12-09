export default{
	state:{
		list:[
			{
				checked:false,
				id:1,
				price:"4999.00",
				num:1
			},
			{
				checked:false,
				id:2,
				price:"2002.00",
				num:1
			},
			{
				checked:false,
				id:3,
				price:"59.00",
				num:1
			}
		],
		// 定义一个空数组用于判断与所以选中的内容是否相等
		selectedList:[]
	},
	getters:{
		// 判断是否全选
		checkedAll(state){
			return state.list.length === state.selectedList.length;
		},
		// 合计+结算数量
		totalCount(state){
			let total = {
				price:0,
				num:0
			}
			state.list.forEach(v=>{
				// 判断是否选中
				if(state.selectedList.indexOf(v.id)>-1)
				{
					// 合计
					total.price += v.price*v.num;
					// 结算数量 [获取到selectedList中含有多少个内容]
					total.num = state.selectedList.length;
				}
			})
			return total;
		}
	},
	mutations:{
		// 全选
		checkAll(state){
			state.selectedList = state.list.map(v=>{
				v.checked = true;
				return v.id;
			})
		},
		// 全不选
		uncheckAll(state){
			state.list.forEach(v=>{
				v.checked = false;
			})
			state.selectedList = [];
		},
		// 控制商品内容单选
		selectedItem(state,index){
			// 获取到点击单选商品的id
			let id = state.list[index].id;
			// 回到selectedList里面查找id
			let i = state.selectedList.indexOf(id);
			// 如果selectedList已经存在就代表是已经选中的状态checked = false,并且在selectedList删除
			if(i>-1){
				state.list[index].checked = false;
				return state.selectedList.splice(i,1);
			}
			// 如果之前没有选中checked = false,把当前的id添加到selectedList中
			state.list[index].checked = true;
			state.selectedList.push(id);
		},
		// 移除商品
		delGoods(state){
			state.list = state.list.filter(v=>{
				return state.selectedList.indexOf(v.id) === -1;
			})
		}
	},
	actions:{
		checkedAllFn({commit,getters}){
			getters.checkedAll ? commit("uncheckAll") : commit("checkAll");
		},
		delGoodsFn({commit}){
			commit('delGoods');
			commit('uncheckAll');
			uni.showToast({
				title:"商品删除成功！",
				icon:"none"
			})
		}
	}
}