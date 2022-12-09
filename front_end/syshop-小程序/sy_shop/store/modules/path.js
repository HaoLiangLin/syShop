export default {
	state:{
		list:[]
	},
	getters:{
		// 筛选isDefault为 true 的数据
		defaultPath(state){
			return state.list.filter(v=>v.isDefault)
		}
	},
	mutations:{
		//获取当前用户收货地址
		getUserPath(state,obj){
			state.list = obj
		},
		//新建地址
		createPath(state , obj){
			state.list.unshift(obj)
		},
		//修改地址
		updatePath(state,{index,item}){
			
			for(let key in item){
				state.list[index][key]= item[key];
			}
		},
		//把之前选中的变成未选中
		removePath(state){
			state.list.forEach(v=>{
				if(v.isDefault){
					v.isDefault = false;
				}
			})
		}
	},
	actions:{
		//此obj为上面state.list
		createPathFn({commit},obj){
			if(obj.isDefault){
				commit("removePath")
			}
			commit('createPath',obj)
		},
		//此obj为从 myAddress页面传递给add-address的数据，即add-address页的pathObj
		updatePathFn({commit},obj){
			if(obj.item.isDefault){
				commit("removePath")
			}
			commit('updatePath',obj);
		}
	}
}