package alcoholboot.toastit.feature.user.repository;

import alcoholboot.toastit.feature.user.entity.LikeEntity;

import org.bson.types.ObjectId;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface LikeRepository extends JpaRepository<LikeEntity, Long> {
    LikeEntity findByUserIdAndCraftCocktailId (Long userId, Long customCocktailId);
    LikeEntity findByUserIdAndDefaultCocktailsId(Long userId, ObjectId objectId);

    @Query("SELECT COUNT(l) FROM LikeEntity l WHERE l.defaultCocktailsId = :defaultCocktailId")
    int countByDefaultCocktailsId(@Param("defaultCocktailId") ObjectId defaultCocktailId);
}