package top.myzo.backend.service;

import com.baomidou.mybatisplus.extension.service.IService;
import top.myzo.backend.entity.Menu;

import java.util.List;

public interface MenuService extends IService<Menu> {
    List<Menu> listAll();
    Menu getByIdOrNull(Long id);
    boolean create(Menu menu);
    boolean update(Long id, Menu menu);
    boolean removeByIdLogical(Long id);
    List<Menu> buildMenuTree(List<Menu> flatMenus);
    List<Menu> listTree();
}
