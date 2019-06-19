package com.example.oldguy.model2.jpas;

import com.example.oldguy.modules.model1.dao.entities.Entity2;
import com.example.oldguy.modules.model1.dao.jpas.Entity2Mapper;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

@RunWith(SpringRunner.class)
@SpringBootTest
public class Entity2MapperTests {

    @Autowired
    private Entity2Mapper entity1Mapper;

    @Test
    public void testInsert() {

        Entity2 entity = new Entity2();
        entity.setPassword("123456");
        entity.setUsername("456");
        entity1Mapper.insert(entity);

    }

}
