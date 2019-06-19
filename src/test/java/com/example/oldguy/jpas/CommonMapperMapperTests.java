package com.example.oldguy.jpas;

import com.example.oldguy.modules.model1.dao.entities.Entity2;
import com.example.oldguy.modules.model1.dao.jpas.Entity2Mapper;
import com.example.oldguy.modules.model2.dao.entities.Entity1;
import com.example.oldguy.modules.model2.dao.jpas.Entity1Mapper;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

/**
 * @ClassName: CommonMapperMapperTests
 * @Author: ren
 * @Description:
 * @CreateTIme: 2019/6/18 0018 下午 2:19
 **/
@RunWith(SpringRunner.class)
@SpringBootTest
public class CommonMapperMapperTests {

    @Autowired
    private Entity2Mapper entity2Mapper;
    @Autowired
    private Entity1Mapper entity1Mapper;

    @Test
    public void testInsert(){

        Entity2 entity2 = new Entity2();
        entity2.setPassword(Entity2.class.getCanonicalName());
        entity2.setUsername(Entity2.class.getCanonicalName());

        entity2Mapper.insert(entity2);

        Entity1 entity1 = new Entity1();
        entity1.setUsername(Entity1.class.getCanonicalName());
        entity1.setUsername(Entity1.class.getCanonicalName());
        entity1Mapper.insert(entity1);
    }
}
