package com.seecoder.BlueWhale.serviceImpl;

import com.seecoder.BlueWhale.enums.CategoryEnum;
import com.seecoder.BlueWhale.exception.BlueWhaleException;
import com.seecoder.BlueWhale.po.Comment;
import com.seecoder.BlueWhale.po.Product;
import com.seecoder.BlueWhale.po.Store;
import com.seecoder.BlueWhale.repository.CommentRepository;
import com.seecoder.BlueWhale.repository.StoreRepository;
import com.seecoder.BlueWhale.service.StoreService;
import com.seecoder.BlueWhale.vo.ProductVO;
import com.seecoder.BlueWhale.vo.RatingVO;
import com.seecoder.BlueWhale.vo.StoreVO;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import javax.persistence.EntityManager;
import javax.persistence.Query;
import java.util.List;
import java.util.Objects;
import java.util.OptionalDouble;
import java.util.stream.Collectors;
import java.util.stream.DoubleStream;

@Service
public class StoreServiceImpl implements StoreService {

    @Autowired
    StoreRepository storeRepository;

    @Autowired
    CommentRepository commentRepository;

    @Autowired
    EntityManager entityManager;

    @Resource
    RedisTemplate<String, Object> redisTemplate;

    private static final Logger logger = LoggerFactory.getLogger(StoreServiceImpl.class);

    @Override
    public Boolean create(StoreVO storeVO) {
        Store store = storeRepository.findByName(storeVO.getName());
        if (store != null) {
            throw BlueWhaleException.nameAlreadyExists();
        }
        Store newStore = storeVO.toPO();
        storeRepository.save(newStore);
        redisTemplate.opsForValue().set("store:" + newStore.getId(), newStore);
        logger.info("store {} created", storeVO.getName());
        return true;
    }

    @Override
    public StoreVO getStore(Integer id) {
        String key = "store:" + id;
        Store storeCache = (Store) redisTemplate.opsForValue().get(key);
        if (storeCache != null) {
            return storeCache.toVO();
        }
        Store store = storeRepository.findById(id).orElse(null);
        if (store == null) {
            throw BlueWhaleException.storeNotExists();
        }
        redisTemplate.opsForValue().set(key, store);
        return store.toVO();
    }

    @Override
    public List<StoreVO> getAllStores() {
        List<Store> stores = storeRepository.findAll();
        return stores.stream().map(Store::toVO).collect(Collectors.toList());
    }

    @Override
    public RatingVO getRating(Integer id) {
        RatingVO result = new RatingVO();
        List<Comment> comments = commentRepository.findAllByStoreId(id);
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
    public List<ProductVO> searchProducts(Integer storeId, String name, Double minPrice, Double maxPrice, CategoryEnum category) {
        String condition = "SELECT p FROM Product as p , Store as s WHERE 1=1";
        if (storeId != null) {
            condition = condition.concat(" AND p.storeId = :store_id");
        }

        if (name != null && !name.isEmpty())
            condition = condition.concat(" AND p.name LIKE :name");

        if (category != null)
            condition = condition.concat(" AND p.category = :category");

        if (minPrice != null && minPrice >= 0)
            condition = condition.concat(" AND p.price >= :min_price");

        if (maxPrice != null && maxPrice > 0)
            condition = condition.concat(" AND p.price <= :max_price");

        Query query = entityManager.createQuery(condition);

        if (storeId != null)
            query.setParameter("store_id", storeId);

        if (name != null && !name.isEmpty())
            query.setParameter("name", "%" + name + "%");

        if (category != null)
            query.setParameter("category", category);

        if (minPrice != null && minPrice >= 0)
            query.setParameter("min_price", minPrice);

        if (maxPrice != null && maxPrice >= 0)
            query.setParameter("max_price", maxPrice);

        logger.info(String.format("%f %f", minPrice, maxPrice));
        List<Product> products = query.getResultList();
        return products.stream().map(Product::toVO).collect(Collectors.toList());
    }

}
