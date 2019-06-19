package com.example.oldguy.modules.model1.dao.entities;

import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

/**
 * @ClassName: Entity2
 * @Author: ren
 * @Description:
 * @CreateTIme: 2019/6/18 0018 上午 9:53
 **/
@Data
public class Entity2 {

    @TableId
    private Long id;

    private String username;

    private String password;
}
