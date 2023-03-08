package com.jy2b.zxxfd.controller;

import com.jy2b.zxxfd.domain.dto.ShoppingCartSaveDTO;
import com.jy2b.zxxfd.domain.dto.ShoppingCartDTO;
import com.jy2b.zxxfd.domain.vo.ResultVO;
import com.jy2b.zxxfd.service.IShoppingCartService;
import io.swagger.annotations.Api;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.util.List;

@RestController
@RequestMapping("/shoppingCart")
@Api(tags = "购物车相关接口")
public class ShoppingCartController {
    @Resource
    private IShoppingCartService shoppingCartService;

    @PostMapping("/save")
    @PreAuthorize("hasAnyAuthority('shoppingcart:save')")
    public ResultVO saveCart(@RequestBody ShoppingCartSaveDTO cartDTO) {
        return shoppingCartService.saveCart(cartDTO);
    }

    @DeleteMapping("/delete/{id}")
    @PreAuthorize("hasAnyAuthority('shoppingcart:delete')")
    public ResultVO delCart(@PathVariable("id") Long id) {
        return shoppingCartService.delCart(id);
    }

    @DeleteMapping("/deletes")
    @PreAuthorize("hasAnyAuthority('shoppingcart:delete')")
    public ResultVO bulkDelCart(@RequestParam List<Long> ids) {
        return shoppingCartService.bulkDelCart(ids);
    }

    @DeleteMapping("/empty")
    @PreAuthorize("hasAnyAuthority('shoppingcart:delete')")
    public ResultVO emptyCart() {
        return shoppingCartService.emptyCart();
    }

    @PutMapping("/update")
    @PreAuthorize("hasAnyAuthority('shoppingcart:update')")
    public ResultVO updateCart(@RequestBody ShoppingCartDTO cartDTO) {
        return shoppingCartService.updateCart(cartDTO);
    }

    @GetMapping("/query/{id}")
    @PreAuthorize("hasAnyAuthority('shoppingcart:query')")
    public ResultVO queryCartById(@PathVariable("id") Long id) {
        return shoppingCartService.queryCartById(id);
    }

    @GetMapping("/query")
    @PreAuthorize("hasAnyAuthority('shoppingcart:query')")
    public ResultVO queryCart() {
        return shoppingCartService.queryCart();
    }
}
