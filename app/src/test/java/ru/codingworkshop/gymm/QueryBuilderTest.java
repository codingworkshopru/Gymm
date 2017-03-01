package ru.codingworkshop.gymm;

import org.junit.Test;

import static org.junit.Assert.*;

import ru.codingworkshop.gymm.data.QueryBuilder;

/**
 * Created by Радик on 21.02.2017.
 */

public class QueryBuilderTest {
    @Test
    public void testQueryBuilder() {
        QueryBuilder.QueryPart personPart = new QueryBuilder.QueryPartBuilder()
                .setSelection(new String[] {"id", "group_concat(DISTINCT name)", "surname"})
                .setDistinct(true)
                .setTable("person")
                .setGroup(new String[] {"surname"})
                .build();

        QueryBuilder.QueryPart orderPart = new QueryBuilder.QueryPartBuilder()
                .setTable("order")
                .setThisJoinColumn("person_id")
                .setOrder(new String[] {"price"})
                .build();


        QueryBuilder.QueryPart itemPart = new QueryBuilder.QueryPartBuilder()
                .setSelection(new String[] {"name", "count", "shift"})
                .setTable("item")
                .setThisJoinColumn("order_id")
                .setOrder(new String[] {"shift", "count"})
                .build();

        String resultQuery = QueryBuilder.build(new QueryBuilder.QueryPart[] {personPart, orderPart, itemPart});

        String expectedQuery = "SELECT DISTINCT person.id,group_concat(DISTINCT person.name),person.surname,item.name,item.count,item.shift FROM person JOIN order ON order.person_id=person._id JOIN item ON item.order_id=order._id GROUP BY person.surname ORDER BY order.price,item.shift,item.count";

        assertEquals("Query builder error", expectedQuery, resultQuery);
    }
}
