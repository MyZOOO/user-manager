package top.myzo.backend.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import top.myzo.backend.entity.Menu;
import top.myzo.backend.service.MenuService;
import top.myzo.backend.utils.Result;

import java.util.List;

@RestController
@RequestMapping("/menus")
public class MenuController {

    @Autowired
    private MenuService menuService;

    // 列出所有菜单（扁平）
    @GetMapping
    public Result list() {
        List<Menu> menus = menuService.listAll();
        return Result.success(menus);
    }

    // 列出菜单树
    @GetMapping("/tree")
    public Result tree() {
        return Result.success(menuService.listTree());
    }

    // 根据ID获取菜单
    @GetMapping("/{id}")
    public Result get(@PathVariable Long id) {
        Menu menu = menuService.getByIdOrNull(id);
        return menu != null ? Result.success(menu) : Result.error("菜单不存在");
    }

    // 创建菜单
    @PostMapping
    public Result create(@RequestBody Menu menu) {
        return menuService.create(menu) ? Result.success("创建成功", menu) : Result.error("创建失败");
    }

    // 更新菜单
    @PutMapping("/{id}")
    public Result update(@PathVariable Long id, @RequestBody Menu menu) {
        return menuService.update(id, menu) ? Result.success("更新成功", menu) : Result.error("更新失败");
    }

    // 逻辑删除菜单
    @DeleteMapping("/{id}")
    public Result remove(@PathVariable Long id) {
        return menuService.removeByIdLogical(id) ? Result.success("删除成功") : Result.error("删除失败");
    }
}
