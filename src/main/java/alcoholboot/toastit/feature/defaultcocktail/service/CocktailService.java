package alcoholboot.toastit.feature.defaultcocktail.service;

import alcoholboot.toastit.feature.defaultcocktail.domain.Cocktail;
import org.bson.types.ObjectId;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.Optional;

public interface CocktailService {
    // 카테고리 검색
    List<Cocktail> getAllCocktails();
    Page<Cocktail> getAllCocktailsPaged(Pageable pageable);
    Page<Cocktail> getCocktailsByIngredientPaged(String ingredient, Pageable pageable);
    Page<Cocktail> getCocktailsByGlassPaged(String glass, Pageable pageable);
    Page<Cocktail> getCocktailsByTypePaged(String type, Pageable pageable);
    Page<Cocktail> getCocktailsByFilterPaged(String ingredient, String glass, String type, Pageable pageable);
    Optional<Cocktail> getCocktailById(ObjectId id);
}