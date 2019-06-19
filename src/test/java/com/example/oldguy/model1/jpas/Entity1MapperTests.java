package com.example.oldguy.model1.jpas;

import com.example.oldguy.modules.model2.dao.entities.Entity1;
import com.example.oldguy.modules.model2.dao.jpas.Entity1Mapper;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

@RunWith(SpringRunner.class)
@SpringBootTest
public class Entity1MapperTests {

    @Autowired
    private Entity1Mapper entity1Mapper;

    @Test
    public void testInsert() {

        Entity1 entity = new Entity1();
        entity.setPassword("123456");
        entity.setUsername("456");
        entity1Mapper.insert(entity);

    }

}
