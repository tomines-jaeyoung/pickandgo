package com.pickandgo.config;

import com.pickandgo.domain.Category;
import com.pickandgo.domain.Product;
import com.pickandgo.repository.ProductRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.util.Map;

/**
 * 서버 최초 구동 시 샘플 상품 데이터를 채워 넣는다.
 * React 프로젝트의 더미 데이터(basicProducts)를 DB 데이터로 옮긴 것.
 * 페이지네이션(현재페이지 ±2) 화면을 제대로 보여주기 위해 충분한 수량을 등록한다.
 */
@Component
public class DataInitializer implements CommandLineRunner {

    private final ProductRepository productRepository;

    public DataInitializer(ProductRepository productRepository) {
        this.productRepository = productRepository;
    }

    /** 카테고리별 로컬 일러스트 이미지 경로 */
    private static final Map<Category, String> IMG_KEY = Map.ofEntries(
            Map.entry(Category.BED, "bed"),
            Map.entry(Category.DESK, "desk"),
            Map.entry(Category.STORAGE, "storage"),
            Map.entry(Category.CHAIR, "chair"),
            Map.entry(Category.TABLE, "table"),
            Map.entry(Category.SOFA, "sofa"),
            Map.entry(Category.STYLER, "styler"),
            Map.entry(Category.TV, "tv"),
            Map.entry(Category.WARDROBE, "wardrobe"),
            Map.entry(Category.DINING_TABLE, "dining_table"),
            Map.entry(Category.WASHER, "washer"),
            Map.entry(Category.ETC, "etc")
    );

    private String imageFor(Category category, int index) {
        String key = IMG_KEY.getOrDefault(category, "etc");
        int variant = (index % 3) + 1;
        return "/img/products/" + key + "-" + variant + ".jpg";
    }

    @Override
    public void run(String... args) {
        if (productRepository.count() > 0) {
            return; // 이미 데이터가 있으면 스킵
        }

        String[] locations = {
                "서울특별시 용산구 보광동", "서울특별시 마포구 서교동",
                "서울특별시 강남구 역삼동", "경기도 의정부시 가능동",
                "서울특별시 종로구 효자동"
        };

        Object[][] samples = {
                {"내추럴 소나무 원목 침대", 480000, Category.BED, "편안하고 실용적인 디자인의 중고 가구입니다."},
                {"호텔식 그린 패브릭 헤드 침대", 720000, Category.BED, "고급스러운 패브릭 헤드보드가 매력적인 침대입니다."},
                {"심플 화이트 싱글 서랍 침대", 380000, Category.BED, "수납공간이 넉넉한 실용적인 침대입니다."},
                {"대용량 화이트 원 수납 침대", 560000, Category.BED, "넓은 수납공간을 갖춘 화이트톤 침대입니다."},
                {"그린 화이트 우드 톤 침대", 780000, Category.BED, "원가 200만원인 에이스 그린 화이트 우드톤 침대입니다. 거의 새상품이에요."},
                {"라이트 그레이 패브릭 침대", 410000, Category.BED, "은은한 그레이톤의 패브릭 침대입니다."},
                {"클래식 월넛 원목 침대", 650000, Category.BED, "묵직한 월넛 우드톤의 클래식 침대입니다."},
                {"높은 화이트 전면 책장", 150000, Category.STORAGE, "튼튼하고 실용적인 화이트 책장입니다."},
                {"3단 낮은 인테리어 책장", 90000, Category.STORAGE, "인테리어 소품으로도 좋은 낮은 책장입니다."},
                {"북유럽풍 화이트&원목 책장", 130000, Category.STORAGE, "북유럽 감성의 책장입니다."},
                {"5단 멀티 수납장", 110000, Category.STORAGE, "다용도로 활용 가능한 5단 수납장입니다."},
                {"슬라이딩 도어 수납장", 175000, Category.STORAGE, "슬라이딩 도어로 공간 활용이 좋은 수납장입니다."},
                {"화이트 미니멀 사무용 책상", 140000, Category.DESK, "깔끔한 화이트 사무용 책상입니다."},
                {"원목 상판 수납 책상", 200000, Category.DESK, "수납이 편리한 원목 책상입니다."},
                {"컴팩트 1인 책상", 75000, Category.DESK, "원룸에 적합한 컴팩트 책상입니다."},
                {"높이조절 스탠딩 책상", 260000, Category.DESK, "건강을 생각한 높이조절 책상입니다."},
                {"스틱 레드 식탁 의자", 35000, Category.CHAIR, "포인트가 되는 레드 컬러 의자입니다."},
                {"사무용형 회전 의자", 65000, Category.CHAIR, "편안한 착석감의 사무용 의자입니다."},
                {"북유럽풍 라탄 의자", 48000, Category.CHAIR, "감성적인 라탄 소재 의자입니다."},
                {"메쉬 등받이 사무 의자", 89000, Category.CHAIR, "통풍이 잘 되는 메쉬 의자입니다."},
                {"85인치 대형 QLED TV", 980000, Category.TV, "거의 새상품인 대형 TV입니다."},
                {"55인치 스마트 TV", 420000, Category.TV, "깨끗하게 사용한 스마트 TV입니다."},
                {"43인치 보급형 TV", 220000, Category.TV, "자취방에 적합한 사이즈의 TV입니다."},
                {"6도어 대형 옷장", 350000, Category.WARDROBE, "수납력이 좋은 대형 옷장입니다."},
                {"화이트 2도어 옷장", 190000, Category.WARDROBE, "심플한 화이트 2도어 옷장입니다."},
                {"원목 4도어 장롱", 420000, Category.WARDROBE, "튼튼한 원목 소재의 장롱입니다."},
                {"리빙 그레이 패브릭 소파", 880000, Category.SOFA, "넓고 편안한 3인용 소파입니다."},
                {"베이지 2인용 소파", 480000, Category.SOFA, "아늑한 베이지톤 2인 소파입니다."},
                {"가죽 1인 리클라이너", 320000, Category.SOFA, "편안한 가죽 리클라이너 소파입니다."},
                {"북유럽풍 화이트&원목 식탁", 230000, Category.DINING_TABLE, "4인 가족이 사용하기 좋은 식탁입니다."},
                {"모던 그레이 4인 식탁", 580000, Category.DINING_TABLE, "모던한 디자인의 식탁 세트입니다."},
                {"2인용 원형 식탁", 150000, Category.DINING_TABLE, "소형 가구에 적합한 원형 식탁입니다."},
                {"미니 드럼 세탁기", 220000, Category.WASHER, "1인 가구에 적합한 미니 세탁기입니다."},
                {"대용량 통돌이 세탁기", 310000, Category.WASHER, "대용량 통돌이 세탁기입니다."},
                {"건조기 일체형 세탁기", 650000, Category.WASHER, "세탁+건조 일체형 세탁기입니다."},
                {"스팀 의류관리기", 410000, Category.STYLER, "냄새와 주름을 관리해주는 스타일러입니다."},
                {"소형 미니 스타일러", 280000, Category.STYLER, "공간 효율이 좋은 미니 스타일러입니다."},
                {"접이식 좌식 테이블", 45000, Category.TABLE, "공간 활용이 좋은 접이식 테이블입니다."},
                {"유리 상판 거실 테이블", 130000, Category.TABLE, "깔끔한 유리 상판 테이블입니다."},
                {"원목 좌식 테이블", 80000, Category.TABLE, "따뜻한 느낌의 원목 좌식 테이블입니다."},
                {"전신 거울 옷장", 300000, Category.ETC, "전신 거울이 부착된 실용적인 가구입니다."}
        };

        int i = 0;
        for (Object[] s : samples) {
            String name = (String) s[0];
            int price = (Integer) s[1];
            Category category = (Category) s[2];
            String desc = (String) s[3];
            String location = locations[i % locations.length];
            double rating = 3.5 + (i % 3) * 0.5;
            String imageUrl = imageFor(category, i);

            Product p = new Product(name, price, desc, category, location, imageUrl, rating);
            productRepository.save(p);
            i++;
        }
    }
}
