package com.seecoder.BlueWhale;

import com.seecoder.BlueWhale.enums.CategoryEnum;
import com.seecoder.BlueWhale.exception.BlueWhaleException;
import com.seecoder.BlueWhale.po.Comment;
import com.seecoder.BlueWhale.po.Product;
import com.seecoder.BlueWhale.po.Store;
import com.seecoder.BlueWhale.repository.CommentRepository;
import com.seecoder.BlueWhale.repository.ProductRepository;
import com.seecoder.BlueWhale.repository.StoreRepository;
import com.seecoder.BlueWhale.service.StoreService;
import com.seecoder.BlueWhale.vo.ProductVO;
import com.seecoder.BlueWhale.vo.RatingVO;
import com.seecoder.BlueWhale.vo.StoreVO;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.util.*;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

import org.junit.jupiter.api.Test;

@ExtendWith(SpringExtension.class)
@SpringBootTest
@AutoConfigureMockMvc
public class StoreTest {
    @Autowired
    private StoreService storeService;

    @MockBean
    private StoreRepository storeRepository;

    @MockBean
    private CommentRepository commentRepository;

    @MockBean
    private ProductRepository productRepository;

    @Test
    public void testCreateStore() {
        StoreVO storeVO = new StoreVO();
        storeVO.setName("Test Store");

        when(storeRepository.findByName("Test Store")).thenReturn(null);
        when(storeRepository.save(any(Store.class))).thenReturn(new Store());

        Boolean result = storeService.create(storeVO);

        verify(storeRepository, times(1)).findByName("Test Store");
        verify(storeRepository, times(1)).save(any(Store.class));
        Assertions.assertTrue(result);
    }

    @Test
    public void testCreateStore_NameAlreadyExists() {
        StoreVO storeVO = new StoreVO();
        storeVO.setName("Test Store");

        when(storeRepository.findByName("Test Store")).thenReturn(new Store());

        Assertions.assertThrows(BlueWhaleException.class, () -> storeService.create(storeVO));
    }

    @Test
    public void testGetStore() {
        Store store = new Store();
        store.setId(1);

        when(storeRepository.findById(1)).thenReturn(java.util.Optional.of(store));

        StoreVO result = storeService.getStore(1);

        verify(storeRepository, times(1)).findById(1);
        Assertions.assertEquals(1, result.getId());
    }

    @Test
    public void testGetStore_StoreNotExists() {
        when(storeRepository.findById(1)).thenReturn(Optional.empty());

        Assertions.assertThrows(BlueWhaleException.class, () -> storeService.getStore(1));
    }

    @Test
    public void testGetAllStores() {
        List<Store> stores = new ArrayList<>();
        stores.add(new Store());

        when(storeRepository.findAll()).thenReturn(stores);

        List<StoreVO> result = storeService.getAllStores();

        verify(storeRepository, times(1)).findAll();
        Assertions.assertEquals(1, result.size());
    }

    @Test
    public void testGetRating() {
        Comment comment1 = new Comment();
        comment1.setRating(4.5);
        Comment comment2 = new Comment();
        comment2.setRating(3.5);
        List<Comment> comments = Arrays.asList(comment1, comment2);

        when(commentRepository.findAllByStoreId(1)).thenReturn(comments);

        RatingVO result = storeService.getRating(1);

        verify(commentRepository, times(1)).findAllByStoreId(1);
        Assertions.assertEquals(4.0, result.getRating());
        Assertions.assertEquals(2, result.getNumRated());
    }

    @Test
    public void testGetRating_NoComments() {
        when(commentRepository.findAllByStoreId(1)).thenReturn(Collections.emptyList());

        RatingVO result = storeService.getRating(1);

        verify(commentRepository, times(1)).findAllByStoreId(1);
        Assertions.assertEquals(0.0, result.getRating());
        Assertions.assertEquals(0, result.getNumRated());
    }

    @Test
    public void testSearchProducts() {
        List<Product> products = new ArrayList<>();
        products.add(new Product());

        when(productRepository.findByStoreIdAndMultipleCondtions(1, "Test Product", 10.0, 100.0, "CLOTHES")).thenReturn(products);

        List<ProductVO> result = storeService.searchProducts(1, "Test Product", 10.0, 100.0, CategoryEnum.CLOTHES);

        verify(productRepository, times(1)).findByStoreIdAndMultipleCondtions(1, "Test Product", 10.0, 100.0, "CLOTHES");
        Assertions.assertEquals(1, result.size());
    }
}
