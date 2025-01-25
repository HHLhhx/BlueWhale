package com.seecoder.BlueWhale.serviceImpl;

import com.seecoder.BlueWhale.enums.CategoryEnum;
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
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.persistence.EntityManager;
import javax.persistence.Query;
import java.util.List;
import java.util.Objects;
import java.util.OptionalDouble;
import java.util.stream.Collectors;
import java.util.stream.DoubleStream;

@Service
public class ProductServiceImpl implements ProductService {

    @Autowired
    ProductRepository productRepository;

    @Autowired
    StoreRepository storeRepository;

    @Autowired
    CommentRepository commentRepository;


    @Autowired
    EntityManager entityManager;
    private static final Logger logger = LoggerFactory.getLogger(ProductServiceImpl.class);

    @Override
    public Boolean create(ProductVO productVO) {
        if (productRepository.findByStoreIdAndName(productVO.getStoreId(), productVO.getName()) != null) {
            throw BlueWhaleException.nameAlreadyExists();
        }
        Product product = productVO.toPO();
        product.setSalesAmount(0);
        product.setStock(0);
        product.setPendingNum(0);
        productRepository.save(product);
        logger.info(String.format("Product %s created", productVO.getName()));
        return true;
    }

    @Override
    public Boolean addStock(Integer id, Integer number) {
        Product product = productRepository.findById(id).orElse(null);
        if (product == null) {
            throw BlueWhaleException.productNotExists();
        }
        product.setStock(product.getStock() + number);
        productRepository.save(product);
        logger.info(String.format("product %s add stock: %d", product.getName(), number));
        return true;
    }

    @Override
    public List<ProductVO> getAllProducts(Integer storeId) {
        Store store = storeRepository.findById(storeId).orElse(null);
        if (store == null) {
            throw BlueWhaleException.storeNotExists();
        }
        return productRepository.findAllByStoreId(storeId).stream().map(Product::toVO).collect(Collectors.toList());
    }

    @Override
    public ProductVO getProduct(Integer id) {
        Product product = productRepository.findById(id).orElse(null);
        if (product == null) {
            throw BlueWhaleException.productNotExists();
        }
        return product.toVO();
    }

    @Override
    public RatingVO getRating(Integer id) {
        RatingVO result = new RatingVO();
        List<Comment> comments = commentRepository.findAllByProductId(id);
        DoubleStream ratingStream = comments.stream().filter(Objects::nonNull).mapToDouble(Comment::getRating);
        OptionalDouble averageRating = ratingStream.filter(Objects::nonNull).average();
        ratingStream = comments.stream().filter(Objects::nonNull).mapToDouble(Comment::getRating);
        Integer numRated = Math.toIntExact(ratingStream.filter(Objects::nonNull).count());
        // 默认值为0.0
        result.setRating(averageRating.orElse(0.0));
        result.setNumRated(numRated);
        return result;
    }

    @Override
    public List<Comment> getComments(Integer id) {
        return commentRepository.findAllByProductId(id);
    }

    @Override
    public List<ProductVO> searchFor(String storeName, String name, Double minPrice, Double maxPrice, CategoryEnum category) {
        String condition = "SELECT distinct p FROM Product p, Store s WHERE 1=1";
        if (storeName != null && storeName.length() > 0) {
            condition = condition.concat(" AND s.name LIKE :store_name");
            condition = condition.concat(" AND s.id = p.storeId");
        }

        if (name != null && name.length() > 0)
            condition = condition.concat(" AND p.name LIKE :name");

        if (category != null)
            condition = condition.concat(" AND p.category = :category");

        if (minPrice != null && minPrice >= 0)
            condition = condition.concat(" AND p.price >= :min_price");

        if (maxPrice != null && maxPrice > 0)
            condition = condition.concat(" AND p.price <= :max_price");


        Query query = entityManager.createQuery(condition);

        if (storeName != null && storeName.length() > 0)
            query.setParameter("store_name", "%" + storeName + "%");

        if (name != null && name.length() > 0)
            query.setParameter("name", "%" + name + "%");

        if (category != null)
            query.setParameter("category", category);

        if (minPrice != null && minPrice >= 0)
            query.setParameter("min_price", minPrice);

        if (maxPrice != null && maxPrice >= 0)
            query.setParameter("max_price", maxPrice);

        logger.info(String.format("%f %f", minPrice, maxPrice));
        List<Product> products = query.getResultList();
        logger.info(String.format("Return number: %d", products.size()));
        return products.stream().map(Product::toVO).collect(Collectors.toList());
    }

}
