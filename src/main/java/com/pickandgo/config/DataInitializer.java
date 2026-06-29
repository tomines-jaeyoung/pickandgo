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
            Map.entry(Category.STORAGE, "shelf"),
            Map.entry(Category.CHAIR, "chair"),
            Map.entry(Category.TABLE, "table"),
            Map.entry(Category.SOFA, "sofa"),
            Map.entry(Category.STYLER, "clothingsteamer"),
            Map.entry(Category.TV, "tv"),
            Map.entry(Category.WARDROBE, "wardrobe"),
            Map.entry(Category.DINING_TABLE, "diningtable"),
            Map.entry(Category.WASHER, "washingmachine"),
            Map.entry(Category.ETC, "mirrorfurniture")
    );

    private String imageFor(Category category, int variant) {
        String key = IMG_KEY.getOrDefault(category, "etc");
        return "/img/products/" + key + "_" + variant + ".jpg";
    }

    @Override
    public void run(String... args) {
        if (productRepository.count() > 30) {
            return; // 이미 데이터가 충분히 세팅되어 있으면 스킵
        }

        productRepository.deleteAll(); // 기존 소량 데이터 삭제 후 재세팅

        Object[][] samples = {
                {"럭셔리 클래식 가죽 퀼팅 킹사이즈 침대", 480000, Category.BED, "편안하고 실용적인 디자인의 중고 가구입니다."},
                {"내추럴 원목 통나무 평상형 퀸 침대", 720000, Category.BED, "고급스러운 패브릭 헤드보드가 매력적인 침대입니다."},
                {"보헤미안 감성 원목 저상형 더블 침대", 380000, Category.BED, "수납공간이 넉넉한 실용적인 침대입니다."},
                {"럭셔리 레더 퀼팅 패밀리 침대", 560000, Category.BED, "넓은 수납공간을 갖춘 화이트톤 침대입니다."},
                {"통원목 친환경 평상형 퀸 침대", 780000, Category.BED, "원가 200만원인 에이스 그린 화이트 우드톤 침대입니다. 거의 새상품이에요."},
                {"내추럴 원목 저상형 더블 헤드 침대", 410000, Category.BED, "은은한 그레이톤의 패브릭 침대입니다."},
                {"고급 가죽 프레임 킹사이즈 침대", 650000, Category.BED, "묵직한 월넛 우드톤의 클래식 침대입니다."},
                {"모던 솔리드 우드 월 쉘프 대형 책장", 150000, Category.STORAGE, "튼튼하고 실용적인 화이트 책장입니다."},
                {"북유럽풍 내추럴 스트링 벽선반", 90000, Category.STORAGE, "인테리어 소품으로도 좋은 낮은 책장입니다."},
                {"자작나무 레이어드 무지주 3단 벽선반", 130000, Category.STORAGE, "북유럽 감성의 책장입니다."},
                {"모던 솔리드 우드 5단 원목 책장", 110000, Category.STORAGE, "다용도로 활용 가능한 5단 수납장입니다."},
                {"내추럴 스트링 인테리어 벽선반", 175000, Category.STORAGE, "슬라이딩 도어로 공간 활용이 좋은 수납장입니다."},
                {"디자이너 감성 미니멀 원목 워크데스크", 140000, Category.DESK, "깔끔한 화이트 사무용 책상입니다."},
                {"모던 심플 원목 컴퓨터 데스크", 200000, Category.DESK, "수납이 편리한 원목 책상입니다."},
                {"프리미엄 묵직한 월넛 원목 데스크", 75000, Category.DESK, "원룸에 적합한 컴팩트 책상입니다."},
                {"미니멀 원목 높이조절 스탠딩 책상", 260000, Category.DESK, "건강을 생각한 높이조절 책상입니다."},
                {"북유럽풍 회전형 패브릭 라운지 체어", 35000, Category.CHAIR, "포인트가 되는 RED 컬러 의자입니다."},
                {"모던 화이트 원목 바스툴 의자", 65000, Category.CHAIR, "편안한 착석감의 사무용 의자입니다."},
                {"클래식 앤티크 브라운 원목 다이닝 체어", 48000, Category.CHAIR, "감성적인 라탄 소재 의자입니다."},
                {"인체공학 회전형 패브릭 오피스 체어", 89000, Category.CHAIR, "통풍이 잘 되는 메쉬 의자입니다."},
                {"초고화질 4K 울트라 슬림 스마트 TV", 980000, Category.TV, "거의 새상품인 대형 TV입니다."},
                {"벽걸이형 슬림 베젤 LED TV", 420000, Category.TV, "깨끗하게 사용한 스마트 TV입니다."},
                {"모던 시크 대화면 스마트 OLED TV", 220000, Category.TV, "자취방에 적합한 사이즈의 TV입니다."},
                {"럭셔리 LED 빌트인 슬라이딩 옷장", 350000, Category.WARDROBE, "수납력이 좋은 대형 옷장입니다."},
                {"모던 벤치형 시스템 붙박이장", 190000, Category.WARDROBE, "심플한 화이트 2도어 옷장입니다."},
                {"북유럽풍 클래식 원목 옷장", 420000, Category.WARDROBE, "튼튼한 원목 소재의 장롱입니다."},
                {"비비드 딥그린 벨벳 3인용 소파", 880000, Category.SOFA, "넓고 편안한 3인용 소파입니다."},
                {"북유럽풍 멜란지 그레이 패브릭 소파", 480000, Category.SOFA, "아늑한 베이지톤 2인 소파입니다."},
                {"모던 다크그레이 L자 코너 카우치 소파", 320000, Category.SOFA, "편안한 가죽 리클라이너 소파입니다."},
                {"모던 북유럽풍 4인 원목 식탁 세트", 230000, Category.DINING_TABLE, "4인 가족이 사용하기 좋은 식탁입니다."},
                {"럭셔리 딥브라운 6인 원목 다이닝 식탁 세트", 580000, Category.DINING_TABLE, "모던한 디자인의 식탁 세트입니다."},
                {"에메랄드 벨벳 골드 프레임 6인 다이닝 식탁 세트", 150000, Category.DINING_TABLE, "소형 가구에 적합한 원형 식탁입니다."},
                {"친환경 에코버블 화이트 드럼세탁기", 220000, Category.WASHER, "1인 가구에 적합한 미니 세탁기입니다."},
                {"컴팩트 빌트인 화이트 드럼세탁기", 310000, Category.WASHER, "대용량 통돌이 세탁기입니다."},
                {"유러피안 프리미엄 화이트 드럼세탁기", 650000, Category.WASHER, "세탁+건조 일체형 세탁기입니다."},
                {"프리미엄 미러도어 스마트 의류관리기", 410000, Category.STYLER, "냄새와 주름을 관리해주는 스타일러입니다."},
                {"빌트인 스타일 모던 의류관리기", 280000, Category.STYLER, "공간 효율이 좋은 미니 스타일러입니다."},
                {"오브제 감성 화이트 도어 스팀 스타일러", 780000, Category.STYLER, "화사한 화이트 글래스 전면 패널의 최신형 스팀 의류관리기입니다."},
                {"빈티지 인더스트리얼 우드 테이블", 45000, Category.TABLE, "공간 활용이 좋은 접이식 테이블입니다."},
                {"내추럴 원목 원형 사이드 티테이블", 130000, Category.TABLE, "깔끔한 유리 상판 테이블입니다."},
                {"카페 감성 내추럴 스퀘어 우드 테이블", 80000, Category.TABLE, "따뜻한 느낌의 원목 좌식 테이블입니다."},
                {"모던 빈티지 원목 서랍형 화장대 거울 세트", 300000, Category.ETC, "전신 거울이 부착된 실용적인 가구입니다."},
                {"엔티크 조각 프레임 벽걸이 거울", 180000, Category.ETC, "세월의 흔적이 아름다운 빈티지 벽걸이형 거울입니다."},
                {"유럽풍 빈티지 골드 전신 스탠드 거울", 250000, Category.ETC, "카페나 매장에 잘 어울리는 화려한 프레임의 대형 전신거울입니다."}
        };

        java.util.Map<Category, Integer> categoryCounters = new java.util.HashMap<>();
        int totalItems = 150;
        int i = 0;
        while (i < totalItems) {
            Object[] s = samples[i % samples.length];
            String name = (String) s[0];
            String finalName = name + " (" + (i / samples.length + 1) + ")";
            int price = (Integer) s[1];
            Category category = (Category) s[2];
            String desc = (String) s[3];

            // 드롭다운 및 요구사항에 맞게 각 지역별 가구 수 분배
            String location;
            if (i < 45) {
                location = "서울특별시 중구 신당동"; // 45개 (신당동 30개 내외)
            } else if (i < 75) {
                location = "경기도 의정부시 가능동"; // 30개 (의정부 20개 내외)
            } else if (i < 100) {
                location = "서울특별시 용산구 보광동";
            } else if (i < 125) {
                location = "서울특별시 강남구 역삼동";
            } else if (i < 140) {
                location = "서울특별시 마포구 서교동";
            } else {
                location = "서울특별시 종로구 효자동";
            }

            double rating = 3.5 + (i % 3) * 0.5;
            int count = categoryCounters.getOrDefault(category, 0);
            int variant = (count % 3) + 1;
            categoryCounters.put(category, count + 1);

            String imageUrl = imageFor(category, variant);

            Product p = new Product(finalName, price, desc, category, location, imageUrl, rating);
            productRepository.save(p);
            i++;
        }
    }
}
