package alcoholboot.toastit.feature.user.entity;

import alcoholboot.toastit.feature.image.entity.ImageEntity;
import alcoholboot.toastit.feature.craftcocktail.entity.CraftCocktailEntity;
import alcoholboot.toastit.feature.user.domain.User;
import alcoholboot.toastit.feature.user.type.Authority;
import alcoholboot.toastit.global.entity.JpaAuditingFields;

import jakarta.persistence.*;

import lombok.*;

import java.util.ArrayList;
import java.util.List;

@Entity
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "users")
public class UserEntity extends JpaAuditingFields {
    @Id
    @Setter
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private String email;

    @Setter
    @Column(nullable = false, unique = true)
    private String nickname;

    @Setter
    @Column(nullable = false)
    private String password;

    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private Authority authority;

    @Column(nullable = false, name = "profile_image_url")
    private String profileImageUrl;

    @Column(name = "provider_type")
    private String providerType;

    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL)
    private List<CraftCocktailEntity> cocktails = new ArrayList<>();

    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL)
    private List<ImageEntity> imageEntities = new ArrayList<>();

    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL)
    private List<LikeEntity> likes = new ArrayList<>();

    @Setter
    @OneToMany(mappedBy = "follower", cascade = CascadeType.ALL)
    private List<FollowEntity> following = new ArrayList<>();

    @Setter
    @OneToMany(mappedBy = "followee", cascade = CascadeType.ALL)
    private List<FollowEntity> followers = new ArrayList<>();


    public UserEntity(String email, String nickname, String password, Authority authority) {
        this.email = email;
        this.nickname = nickname;
        this.password = password;
        this.authority = authority;
    }

    public User convertToDomain() {
        return User.builder()
                .id(this.id)
                .email(this.email)
                .nickname(this.nickname)
                .password(this.password)
                .authority(this.authority)
                .profileImageUrl(this.profileImageUrl)
                .createDate(this.createDate)
                .providerType(this.providerType)
                .build();
    }
}