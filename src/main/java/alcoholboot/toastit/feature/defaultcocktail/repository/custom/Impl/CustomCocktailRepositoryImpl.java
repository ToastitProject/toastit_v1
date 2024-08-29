package alcoholboot.toastit.feature.defaultcocktail.repository.custom.Impl;

import alcoholboot.toastit.feature.defaultcocktail.entity.CocktailDocument;
import alcoholboot.toastit.feature.defaultcocktail.repository.custom.CustomCocktailRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.aggregation.Aggregation;
import org.springframework.data.mongodb.core.aggregation.AggregationResults;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;

import java.util.List;


/**
 * CustomCocktailRepository의 구현 클래스입니다.
 * 이 클래스는 칵테일 검색과 관련된 사용자 정의 쿼리 메서드를 제공합니다.
 */
@RequiredArgsConstructor
public class CustomCocktailRepositoryImpl implements CustomCocktailRepository {
    private final MongoTemplate mongoTemplate;

    /**
     * 복수의 재료 검색을 위한 Criteria를 생성합니다.
     * 이 메서드는 주어진 재료 목록에 대해 AND 조건으로 Criteria를 생성합니다.
     * 각 재료는 strIngredient1부터 strIngredient11까지의 필드 중 하나와 일치해야 합니다.
     *
     * @param ingredients 검색할 복수의 재료 이름
     * @return 생성된 Criteria 객체
     */
    private Criteria createIngredientCriteria(List<String> ingredients) {
        if (ingredients == null || ingredients.isEmpty()) {
            return new Criteria();
        }

        // 복수 키워드를 적용하기 위해, 단일 키워드를 연결하는 방식으로 진행
        List<Criteria> allIngredientsCriteria = ingredients.stream()
                .map(this::createSingleIngredientCriteria)
                .toList();

        // 크기를 0으로 하면 Java에서 반환될 크기를 자동으로 결정한다.
        return new Criteria().andOperator(allIngredientsCriteria.toArray(new Criteria[0]));
    }

    /**
     * 재료 검색을 위한 Criteria를 생성합니다.
     * 이 메서드는 strIngredient1부터 strIngredient11까지의 필드에 대해 검색 조건을 생성합니다.
     *
     * @param ingredient 검색할 재료 이름
     * @return 생성된 Criteria 객체
     */
    private Criteria createSingleIngredientCriteria(String ingredient) {
        if (ingredient == null || ingredient.isEmpty()) {
            return new Criteria();
        }
        return new Criteria().orOperator(
                Criteria.where("strIngredient1").regex(ingredient, "i"),
                Criteria.where("strIngredient2").regex(ingredient, "i"),
                Criteria.where("strIngredient3").regex(ingredient, "i"),
                Criteria.where("strIngredient4").regex(ingredient, "i"),
                Criteria.where("strIngredient5").regex(ingredient, "i"),
                Criteria.where("strIngredient6").regex(ingredient, "i"),
                Criteria.where("strIngredient7").regex(ingredient, "i"),
                Criteria.where("strIngredient8").regex(ingredient, "i"),
                Criteria.where("strIngredient9").regex(ingredient, "i"),
                Criteria.where("strIngredient10").regex(ingredient, "i"),
                Criteria.where("strIngredient11").regex(ingredient, "i")
        );
    }

    /**
     * 잔 종류 검색을 위한 Criteria를 생성합니다.
     *
     * @param glass 검색할 잔 종류
     * @return 생성된 Criteria 객체
     */
    private Criteria createGlassCriteria(String glass) {
        if (glass == null || glass.trim().isEmpty()) {
            return new Criteria();
        }
        return new Criteria().orOperator(
                // regex -> in 으로 수정
                Criteria.where("strGlass").in(glass, "i")
        );
    }

    /**
     * 카테고리 검색을 위한 Criteria를 생성합니다.
     *
     * @param category 검색할 카테고리
     * @return 생성된 Criteria 객체
     */
    private Criteria createCategoryCriteria(String category) {
        if (category == null || category.trim().isEmpty()) {
            return new Criteria();
        }
        return new Criteria().orOperator(
                Criteria.where("strCategory").in(category, "i")
        );
    }

    /**
     * 주어진 Criteria와 Pageable 객체를 사용하여 칵테일을 검색하고 Page 객체로 반환합니다.
     * 이 메서드는 실제 데이터베이스 쿼리를 수행하고 결과를 페이징 처리합니다.
     *
     * @param criteria 검색 조건
     * @param pageable 페이징 정보
     * @return 검색 결과를 담은 Page 객체
     */
    private Page<CocktailDocument> findCocktails(Criteria criteria, Pageable pageable) {
        // 쿼리 생성
        Query query = new Query(criteria).with(pageable);

        // 쿼리 실행
        List<CocktailDocument> cocktails = mongoTemplate.find(query, CocktailDocument.class);

        // 쿼리 조건에 맞는 전체 문서의 수를 계산
        long total = mongoTemplate.count(query.skip(-1).limit(-1), CocktailDocument.class);

        return new PageImpl<>(cocktails, pageable, total);
    }

//    /**
//     * 주어진 재료로 칵테일을 검색합니다.
//     */
//    @Override
//    public Page<CocktailDocument> findCocktailsByIngredientPage(List<String> ingredient, Pageable pageable) {
//
//        return findCocktails(createIngredientCriteria(ingredient), pageable);
//    }

//    /**
//     * 주어진 잔 종류로 칵테일을 검색합니다.
//     */
//    @Override
//    public Page<CocktailDocument> findCocktailsByGlassPage(String glass, Pageable pageable) {
//
//        return findCocktails(createGlassCriteria(glass), pageable);
//    }

//    /**
//     * 주어진 카테고리로 칵테일을 검색합니다.
//     */
//    @Override
//    public Page<CocktailDocument> findCocktailsByCategoryPage(String category, Pageable pageable) {
//
//        return findCocktails(createCategoryCriteria(category), pageable);
//    }

//    /**
//     * 주어진 재료와 잔 종류로 칵테일을 검색합니다.
//     */
//    @Override
//    public Page<CocktailDocument> findByIngredientAndGlass(List<String> ingredient, String glass, Pageable pageable) {
//
//        // Criteria 생성
//        Criteria combinedCriteria = new Criteria().andOperator(
//                createIngredientCriteria(ingredient),
//                createGlassCriteria(glass)
//        );
//
//        return findCocktails(combinedCriteria, pageable);
//    }

//    /**
//     * 주어진 재료와 카테고리로 칵테일을 검색합니다.
//     */
//    @Override
//    public Page<CocktailDocument> findByIngredientAndCategoryPage(List<String> ingredient, String category, Pageable pageable) {
//
//        // Criteria 생성
//        Criteria combinedCriteria = new Criteria().andOperator(
//                createIngredientCriteria(ingredient),
//                createCategoryCriteria(category)
//        );
//
//        return findCocktails(combinedCriteria, pageable);
//    }

//    /**
//     * 주어진 잔 종류와 카테고리로 칵테일을 검색합니다.
//     */
//    @Override
//    public Page<CocktailDocument> findByGlassAndCategoryPage(String glass, String category, Pageable pageable) {
//
//        // Criteria 생성
//        Criteria combinedCriteria = new Criteria().andOperator(
//                createGlassCriteria(glass),
//                createCategoryCriteria(category)
//        );
//
//        return findCocktails(combinedCriteria, pageable);
//    }

    /**
     * 주어진 재료, 잔 종류, 카테고리로 칵테일을 검색합니다.
     */
    @Override
    public Page<CocktailDocument> findByIngredientAndGlassAndCategoryPage(List<String> ingredient, String glass, String category, Pageable pageable) {

        // Criteria 생성
        Criteria combinedCriteria = new Criteria().andOperator(
                createIngredientCriteria(ingredient),
                createGlassCriteria(glass),
                createCategoryCriteria(category)
        );

        return findCocktails(combinedCriteria, pageable);
    }

    /**
     *  랜덤한 칵테일을 반환한다.
     * @param count 반환할 칵테일 수
     * @return 랜덤한 칵테일
     */
    @Override
    public List<CocktailDocument> findRandomCocktails(int count) {
        Aggregation aggregation = Aggregation.newAggregation(
                Aggregation.sample(count)
        );
        AggregationResults<CocktailDocument> results = mongoTemplate.aggregate(
                aggregation, "cocktails", CocktailDocument.class
        );
        return results.getMappedResults();
    }
}