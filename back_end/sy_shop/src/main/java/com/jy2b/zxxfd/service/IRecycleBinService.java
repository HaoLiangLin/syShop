package com.jy2b.zxxfd.service;

import com.jy2b.zxxfd.domain.vo.ResultVO;

import java.util.function.Function;

/**
 * @author 林武泰
 * 回收站业务接口
 */
public interface IRecycleBinService {
    /**
     * 查看回收站
     * @param type 资源类型
     * @param <T> 定义泛型
     * @return ResultVO
     */
    <T> ResultVO selectRecycleBin(Class<T> type);

    /**
     * 根据名称搜索回收站
     * @param type 资源类型
     * @param name 资源名称
     * @param <T> 定义泛型
     * @return ResultVO
     */
    <T> ResultVO findRecycleBinByName(Class<T> type, String name);

    /**
     * 根据名字删除回收站中资源
     * @param type 资源类型
     * @param name 资源名称
     * @param <T> 定义泛型
     * @return ResultVO
     */
    <T> ResultVO delRecycleBinByName(Class<T> type, String name);

    /**
     * 根据名称从回收站恢复资源
     * @param type 资源类型
     * @param name 资源名称
     * @param <T> 定义泛型
     * @return ResultVO
     */
    <T> ResultVO regainRecycleBinByName(Class<T> type, String name, Function<T, Boolean> dbFallback);

}
