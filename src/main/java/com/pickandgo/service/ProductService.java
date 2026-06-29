package com.pickandgo.service;

import com.pickandgo.domain.Category;
import com.pickandgo.domain.Product;
import com.pickandgo.repository.ProductRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.List;
import java.util.UUID;

@Service
@Transactional(readOnly = true)
public class ProductService {

    private static final int PAGE_SIZE = 8;
    private final ProductRepository productRepository;

    public ProductService(ProductRepository productRepository) {
        this.productRepository = productRepository;
    }

    /** 중고판매(판매하기) 페이지 - 다중 조건 필터링 + 페이지네이션 */
    public Page<Product> search(String location, String keyword, Category category, Integer maxPrice, int page) {
        Pageable pageable = PageRequest.of(Math.max(page - 1, 0), PAGE_SIZE);

        String loc = StringUtils.hasText(location) ? location : null;
        String kw = StringUtils.hasText(keyword) ? keyword : null;

        return productRepository.search(loc, kw, category, maxPrice, pageable);
    }

    @Transactional
    public Product findById(Long id) {
        Product product = productRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 상품입니다. id=" + id));
        if ("수거대기".equals(product.getStatus()) && product.getCreatedAt() != null &&
                product.getCreatedAt().isBefore(java.time.LocalDateTime.now().minusMinutes(1))) {
            product.setStatus("수거완료");
        }
        return product;
    }

    /** 랜딩페이지 Sale 슬라이더용 최신 상품 목록 */
    public Page<Product> findLatest(int count) {
        return productRepository.findByOnSaleTrueOrderByIdDesc(PageRequest.of(0, count));
    }

    /** 수거용 등록 - onSale=false로 저장해서 판매 목록에 노출되지 않음 */
    @Transactional
    public Product registerForCollection(String name, int price, String description, Category category,
                                          String location, MultipartFile imageFile, String sellerEmail, String uploadDir) {
        String imageUrl = saveImage(imageFile, uploadDir);
        Product product = new Product(name, price, description, category, location, imageUrl, 0.0);
        product.setOnSale(false);
        product.setSellerEmail(sellerEmail);
        product.setStatus("수거대기");
        return productRepository.save(product);
    }

    @Transactional
    public Product register(String name, int price, String description, Category category,
                             String location, MultipartFile imageFile, String sellerEmail, String uploadDir) {
        String imageUrl = saveImage(imageFile, uploadDir);
        Product product = new Product(name, price, description, category, location, imageUrl, 0.0);
        product.setSellerEmail(sellerEmail);
        product.setStatus("판매중");
        return productRepository.save(product);
    }

    @Transactional
    public void completeSale(Long id) {
        Product product = findById(id);
        product.setOnSale(false);
        product.setStatus("판매완료");
    }

    @Transactional
    public void autoUpdateStatus(String sellerEmail) {
        if (sellerEmail == null) return;
        List<Product> products = productRepository.findBySellerEmail(sellerEmail);
        java.time.LocalDateTime oneMinuteAgo = java.time.LocalDateTime.now().minusMinutes(1);
        for (Product p : products) {
            if ("수거대기".equals(p.getStatus()) && p.getCreatedAt() != null && p.getCreatedAt().isBefore(oneMinuteAgo)) {
                p.setStatus("수거완료");
            }
        }
    }

    @Transactional
    public List<Product> findBySellerEmail(String sellerEmail) {
        autoUpdateStatus(sellerEmail);
        return productRepository.findBySellerEmail(sellerEmail);
    }

    @Transactional
    public Product update(Long id, String name, int price, String description, Category category,
                          String location, MultipartFile imageFile, String uploadDir) {
        Product product = findById(id);
        product.setName(name);
        product.setPrice(price);
        product.setDescription(description);
        product.setCategory(category);
        product.setLocation(location);
        if (imageFile != null && !imageFile.isEmpty()) {
            String imageUrl = saveImage(imageFile, uploadDir);
            product.setImageUrl(imageUrl);
        }
        return product;
    }

    @Transactional
    public void relist(Long id) {
        Product product = findById(id);
        if (!"판매완료".equals(product.getStatus())) {
            throw new IllegalStateException("판매완료 상태의 상품만 다시 올릴 수 있습니다.");
        }
        product.setStatus("판매중");
        product.setOnSale(true);
        product.setCreatedAt(java.time.LocalDateTime.now());
    }

    @Transactional
    public void recollect(Long id) {
        Product product = findById(id);
        if (!"수거완료".equals(product.getStatus())) {
            throw new IllegalStateException("수거완료 상태의 신청만 다시 신청할 수 있습니다.");
        }
        product.setStatus("수거대기");
        product.setCreatedAt(java.time.LocalDateTime.now());
    }

    @Transactional
    public void delete(Long id) {
        productRepository.deleteById(id);
    }

    /**
     * 드래그앤드롭/파일선택으로 업로드된 이미지를 서버 디스크에 저장하고
     * 웹에서 접근 가능한 경로(/uploads/xxx.jpg)를 반환한다.
     */
    private String saveImage(MultipartFile file, String uploadDir) {
        if (file == null || file.isEmpty()) {
            return null;
        }
        try {
            Path dirPath = Paths.get(uploadDir);
            Files.createDirectories(dirPath);

            String original = StringUtils.cleanPath(file.getOriginalFilename() == null ? "image" : file.getOriginalFilename());
            String ext = original.contains(".") ? original.substring(original.lastIndexOf('.')) : "";
            String savedName = UUID.randomUUID() + ext;

            Path target = dirPath.resolve(savedName);
            try (InputStream in = file.getInputStream()) {
                Files.copy(in, target, StandardCopyOption.REPLACE_EXISTING);
            }
            return "/uploads/" + savedName;
        } catch (IOException e) {
            throw new RuntimeException("이미지 저장 중 오류가 발생했습니다.", e);
        }
    }
}
