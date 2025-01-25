package com.seecoder.BlueWhale;

import com.seecoder.BlueWhale.exception.BlueWhaleException;
import com.seecoder.BlueWhale.po.Comment;
import com.seecoder.BlueWhale.po.Product;
import com.seecoder.BlueWhale.po.Store;
import com.seecoder.BlueWhale.repository.CommentRepository;
import com.seecoder.BlueWhale.repository.ProductRepository;
import com.seecoder.BlueWhale.repository.StoreRepository;
import com.seecoder.BlueWhale.service.ProductService;
import com.seecoder.BlueWhale.vo.ProductVO;
import com.seecoder.BlueWhale.vo.RatingVO;
import org.junit.jupiter.api.Assertions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;

import org.junit.jupiter.api.Test;
import java.util.*;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@SpringBootTest
@AutoConfigureMockMvc
public class ProductServiceTest {
    @Autowired
    private ProductService productService;

    @MockBean
    private ProductRepository productRepository;

    @MockBean
    private StoreRepository storeRepository;

    @MockBean
    private CommentRepository commentRepository;

    @Test
    public void testCreateProduct() {
        ProductVO productVO = new ProductVO();
        productVO.setStoreId(1);
        productVO.setName("Test Product");

        when(productRepository.findByStoreIdAndName(1, "Test Product")).thenReturn(null);
        when(productRepository.save(any(Product.class))).thenReturn(new Product());

        Boolean result = productService.create(productVO);

        verify(productRepository, times(1)).findByStoreIdAndName(1, "Test Product");
        verify(productRepository, times(1)).save(any(Product.class));
        Assertions.assertTrue(result);
    }

    @Test
    public void testCreateProduct_NameAlreadyExists() {
        ProductVO productVO = new ProductVO();
        productVO.setStoreId(1);
        productVO.setName("Test Product");

        when(productRepository.findByStoreIdAndName(1, "Test Product")).thenReturn(new Product());

        Assertions.assertThrows(BlueWhaleException.class, () -> productService.create(productVO));
    }

    @Test
    public void testAddStock() {
        Product product = new Product();
        product.setId(1);
        product.setStock(10);

        when(productRepository.findById(1)).thenReturn(java.util.Optional.of(product));
        when(productRepository.save(any(Product.class))).thenReturn(new Product());

        Boolean result = productService.addStock(1, 5);

        verify(productRepository, times(1)).findById(1);
        verify(productRepository, times(1)).save(any(Product.class));
        Assertions.assertTrue(result);
        Assertions.assertEquals(15, product.getStock());
    }

    @Test
    public void testAddStock_ProductNotExists() {
        when(productRepository.findById(1)).thenReturn(Optional.empty());

        Assertions.assertThrows(BlueWhaleException.class, () -> productService.addStock(1, 5));
    }

    @Test
    public void testGetAllProducts() {
        Store store = new Store();
        store.setId(1);
        List<Product> products = new ArrayList<>();
        products.add(new Product());

        when(storeRepository.findById(1)).thenReturn(java.util.Optional.of(store));
        when(productRepository.findAllByStoreId(1)).thenReturn(products);

        List<ProductVO> result = productService.getAllProducts(1);

        verify(storeRepository, times(1)).findById(1);
        verify(productRepository, times(1)).findAllByStoreId(1);
        Assertions.assertEquals(1, result.size());
    }

    @Test
    public void testGetAllProducts_StoreNotExists() {
        when(storeRepository.findById(1)).thenReturn(Optional.empty());

        Assertions.assertThrows(BlueWhaleException.class, () -> productService.getAllProducts(1));
    }

    @Test
    public void testGetProduct() {
        Product product = new Product();
        product.setId(1);

        when(productRepository.findById(1)).thenReturn(java.util.Optional.of(product));

        ProductVO result = productService.getProduct(1);

        verify(productRepository, times(1)).findById(1);
        Assertions.assertEquals(1, result.getId());
    }

    @Test
    public void testGetProduct_ProductNotExists() {
        when(productRepository.findById(1)).thenReturn(Optional.empty());

        Assertions.assertThrows(BlueWhaleException.class, () -> productService.getProduct(1));
    }

    @Test
    public void testGetRating() {
        Comment comment1 = new Comment();
        comment1.setRating(4.5);
        Comment comment2 = new Comment();
        comment2.setRating(3.5);
        List<Comment> comments = Arrays.asList(comment1, comment2);

        when(commentRepository.findAllByProductId(1)).thenReturn(comments);

        RatingVO result = productService.getRating(1);

        verify(commentRepository, times(1)).findAllByProductId(1);
        Assertions.assertEquals(4.0, result.getRating());
        Assertions.assertEquals(2, result.getNumRated());
    }

    @Test
    public void testGetRating_NoComments() {
        when(commentRepository.findAllByProductId(1)).thenReturn(Collections.emptyList());

        RatingVO result = productService.getRating(1);

        verify(commentRepository, times(1)).findAllByProductId(1);
        Assertions.assertEquals(0.0, result.getRating());
        Assertions.assertEquals(0, result.getNumRated());
    }

    @Test
    public void testGetComments() {
        List<Comment> comments = new ArrayList<>();
        comments.add(new Comment());

        when(commentRepository.findAllByProductId(1)).thenReturn(comments);

        List<Comment> result = productService.getComments(1);

        verify(commentRepository, times(1)).findAllByProductId(1);
        Assertions.assertEquals(1, result.size());
    }
}
