package jpabook2.jpashop2.service;

import jpabook2.jpashop2.domain.Item.Book;
import jpabook2.jpashop2.domain.Item.Item;
import jpabook2.jpashop2.repository.ItemRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional
@RequiredArgsConstructor
public class ItemService {

    private final ItemRepository ir;

    public void saveItem(Item item) {
        ir.save(item);
    }

    @Transactional(readOnly = true)
    public List<Item> findItems() {
        return ir.findAll();
    }

    @Transactional(readOnly = true)
    public Item findOne(Long id) {
        return ir.findOne(id);
    }

    @Transactional
    public void updateItem(Long itemId, String name, int price, int stockQuantity) {

        Item findOne = ir.findOne(itemId);

        findOne.setPrice(price);
        findOne.setName(name);
        findOne.setStockQuantity(stockQuantity);


    }
}
