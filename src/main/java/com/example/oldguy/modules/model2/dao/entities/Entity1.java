package com.example.oldguy.modules.model2.dao.entities;

import com.baomidou.mybatisplus.annotation.TableId;
import lombok.Data;

/**
 * @ClassName: Entity1
 * @Author: ren
 * @Description:
 * @CreateTIme: 2019/6/18 0018 上午 9:52
 **/
@Data
public class Entity1 {

    @TableId
    private Long id;

    private String username;

    private String password;
}
