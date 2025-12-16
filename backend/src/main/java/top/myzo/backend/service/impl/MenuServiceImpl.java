package top.myzo.backend.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;
import top.myzo.backend.entity.Menu;
import top.myzo.backend.mapper.MenuMapper;
import top.myzo.backend.service.MenuService;

import java.util.*;
import java.util.stream.Collectors;

@Service
public class MenuServiceImpl extends ServiceImpl<MenuMapper, Menu> implements MenuService {

    @Override
    public List<Menu> listAll() {
        return list(new LambdaQueryWrapper<Menu>().orderByAsc(Menu::getSort));
    }

    @Override
    public Menu getByIdOrNull(Long id) {
        return getById(id);
    }

    @Override
    public boolean create(Menu menu) {
        if (menu.getHidden() == null) menu.setHidden(false);
        return save(menu);
    }

    @Override
    public boolean update(Long id, Menu menu) {
        menu.setId(id);
        return saveOrUpdate(menu);
    }

    @Override
    public boolean removeByIdLogical(Long id) {
        return removeById(id);
    }

    @Override
    public List<Menu> buildMenuTree(List<Menu> flatMenus) {
        Map<Long, Menu> map = flatMenus.stream()
                .filter(m -> m.getId() != null)
                .collect(Collectors.toMap(Menu::getId, m -> m, (a,b)->a));
        List<Menu> roots = new ArrayList<>();
        for (Menu m : flatMenus) {
            Long pid = m.getParentId();
            if (pid == null || pid == 0L || !map.containsKey(pid)) {
                roots.add(m);
            } else {
                Menu parent = map.get(pid);
                if (parent.getChildren() == null) parent.setChildren(new ArrayList<>());
                parent.getChildren().add(m);
            }
        }
        // 每个节点子列表按 sort 排序
        flatMenus.forEach(m -> {
            if (m.getChildren() != null) {
                m.getChildren().sort(Comparator.comparing(Menu::getSort, Comparator.nullsLast(Integer::compareTo)));
            }
        });
        roots.sort(Comparator.comparing(Menu::getSort, Comparator.nullsLast(Integer::compareTo)));
        return roots;
    }

    @Override
    public List<Menu> listTree() {
        return buildMenuTree(listAll());
    }
}
