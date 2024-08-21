package alcoholboot.toastit.feature.defaultcocktail.controller;

import alcoholboot.toastit.feature.defaultcocktail.domain.Cocktail;
import alcoholboot.toastit.feature.defaultcocktail.service.CocktailService;
import alcoholboot.toastit.feature.user.domain.User;
import alcoholboot.toastit.feature.user.entity.LikeEntity;
import alcoholboot.toastit.feature.user.service.LikeService;
import alcoholboot.toastit.feature.user.service.UserService;
import lombok.RequiredArgsConstructor;
import org.bson.types.ObjectId;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.Optional;

@Controller
@RequiredArgsConstructor
@RequestMapping("/cocktails")
public class CocktailController {
    private final CocktailService cocktailService;
    private final LikeService likeService;
    private final UserService userService;

    @GetMapping("/all")
    public String getAllCocktails(
            @RequestParam(defaultValue = "0") int page,
            Model model) {
        Page<Cocktail> cocktails = cocktailService.getAllCocktailsPaged(PageRequest.of(page, 20)); // 20개씩 페이징
        model.addAttribute("cocktails", cocktails);
        model.addAttribute("currentPage", page);
        model.addAttribute("totalPages", cocktails.getTotalPages());
        return "/feature/defaultcocktail/cocktailList";
    }

    @GetMapping("/all/ingredient")
    public String getCocktailsByIngredient(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam String ingredient,
            Model model) {

        Page<Cocktail> cocktailPage = cocktailService.getCocktailsByIngredientPaged(ingredient, PageRequest.of(page, 20));

        model.addAttribute("cocktails", cocktailPage.getContent());

        model.addAttribute("currentPage", page);

        model.addAttribute("totalPages", cocktailPage.getTotalPages());

        model.addAttribute("ingredient", ingredient);

        return "feature/defaultcocktail/cocktailIngredient";
    }

    @GetMapping("/all/glass")
    public String getCocktailsByGlass(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam String glass,
            Model model) {
        Page<Cocktail> cocktails = cocktailService.getCocktailsByGlassPaged(glass, PageRequest.of(page, 20));
        model.addAttribute("cocktails", cocktails);
        model.addAttribute("page", page);
        model.addAttribute("glass", glass);
        return "feature/defaultcocktail/cocktailGlass";
    }

    @GetMapping("/all/type")
    public String getCocktailsByType(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam String type,
            Model model) {
        Page<Cocktail> cocktails = cocktailService.getCocktailsByTypePaged(type, PageRequest.of(page, 20));
        model.addAttribute("cocktails", cocktails);
        model.addAttribute("page", page);
        model.addAttribute("type", type);
        return "feature/defaultcocktail/cocktailType";
    }

    @GetMapping("/all/mult")
    public String getCocktailsByMult(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(required = false) String ingredient,
            @RequestParam(required = false) String glass,
            @RequestParam(required = false) String type,
            Model model) {
        Page<Cocktail> cocktails = cocktailService.getCocktailsByFilterPaged(ingredient, glass, type, PageRequest.of(page, 20));
        model.addAttribute("page", page);
        model.addAttribute("cocktails", cocktails);
        model.addAttribute("ingredient", ingredient);
        model.addAttribute("glass", glass);
        model.addAttribute("type", type);

        return "feature/defaultcocktail/cocktailComplex";
    }

    @GetMapping("/id")
    public String getCocktailById(
            @RequestParam("id") String id,
            Model model,
            RedirectAttributes redirectAttributes) {
        Optional<Cocktail> cocktail = cocktailService.getCocktailById(new ObjectId(id));
        model.addAttribute("cocktail", cocktail);

        ObjectId defaultCocktailId = new ObjectId(id);
        int likeCount = likeService.countByDefaultCocktailsId(defaultCocktailId);
        model.addAttribute("likeCount", likeCount);
//        log.info(defaultCocktailId + " 의 좋아요 갯 수 : "+likeCount);

        // 로그인한 사용자의 좋아요 상태 확인
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication != null && authentication.isAuthenticated()) {
            String loginUserEmail = authentication.getName();
            Optional<User> loginUser = userService.findByEmail(loginUserEmail);

            if (loginUser.isPresent()) {
                LikeEntity existingLike = likeService.findByUserIdAndDefaultCocktailsId(loginUser.get().getId(), defaultCocktailId);
                model.addAttribute("isLiked", existingLike != null); // 좋아요 여부 추가
            } else {
                model.addAttribute("isLiked", false); // 로그인 사용자 없음
            }
        } else {
            model.addAttribute("isLiked", false); // 로그인하지 않은 경우
        }

        return "feature/defaultcocktail/cocktailDetails";
    }
}
