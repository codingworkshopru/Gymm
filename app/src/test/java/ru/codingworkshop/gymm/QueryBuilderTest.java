package ru.codingworkshop.gymm;

import org.junit.Test;

import ru.codingworkshop.gymm.data.QueryBuilder;

import static org.junit.Assert.assertEquals;

/**
 * Created by Радик on 21.02.2017.
 */

public class QueryBuilderTest {
    @Test
    public void testQueryBuilder() {
        QueryBuilder.QueryPart personPart = new QueryBuilder.QueryPart("person")
                .setColumns("id", "group_concat(DISTINCT name)", "surname")
                .setDistinct(true)
                .setGroup("surname");

        QueryBuilder.QueryPart orderPart = new QueryBuilder.QueryPart("order")
                .setThisJoinColumn("person_id")
                .setOrder("price");


        QueryBuilder.QueryPart itemPart = new QueryBuilder.QueryPart("item")
                .setColumns("name", "count", "shift")
                .setThisJoinColumn("order_id")
                .setOrder("shift", "count");

        String resultQuery = QueryBuilder.build(personPart, orderPart, itemPart);

        String expectedQuery = "SELECT DISTINCT person.id,group_concat(DISTINCT person.name),person.surname,item.name,item.count,item.shift FROM person JOIN order ON order.person_id=person._id JOIN item ON item.order_id=order._id GROUP BY person.surname ORDER BY order.price,item.shift,item.count";

        assertEquals("Query builder error", expectedQuery, resultQuery);
    }
}
