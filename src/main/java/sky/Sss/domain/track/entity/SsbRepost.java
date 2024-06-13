package sky.Sss.domain.track.entity;


import static jakarta.persistence.GenerationType.IDENTITY;
import static lombok.AccessLevel.PRIVATE;
import static lombok.AccessLevel.PROTECTED;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import sky.Sss.domain.track.dto.common.rep.TargetInfoDto;
import sky.Sss.domain.user.entity.User;
import sky.Sss.domain.user.model.ContentsType;
import sky.Sss.domain.user.utili.TokenUtil;
import sky.Sss.global.base.BaseTimeEntity;
import sky.Sss.global.utili.JsEscape;


@Getter
@Setter(PRIVATE)
@NoArgsConstructor(access = PROTECTED)
@Entity
public class SsbRepost extends BaseTimeEntity {


    @Id
    @GeneratedValue(strategy = IDENTITY)
    private Long id;

    @Column(unique = true, nullable = false)
    private String token;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "uid", nullable = false)
    private User user;

    @Column(nullable = false)
    private Long contentsId;
    // 140자 이하
    private String comment;
    //
    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private ContentsType contentsType;

    @Column(nullable = false)
    private Boolean isPrivacy;

    public static SsbRepost create(TargetInfoDto targetInfoDto, ContentsType contentsType, User user) {
        SsbRepost ssbRepost = new SsbRepost();

        ssbRepost.setUser(user);
        ssbRepost.setToken(TokenUtil.getToken());
        ssbRepost.setContentsId(targetInfoDto.getTargetId());
        ssbRepost.setContentsType(contentsType);
        return ssbRepost;
    }

    public static void updateComment (SsbRepost ssbRepost,String comment) {
        ssbRepost.setComment(JsEscape.escapeJS(comment));
    }
    public static void updateIsPrivacy (SsbRepost ssbRepost,boolean isPrivacy) {
        ssbRepost.setIsPrivacy(isPrivacy);
    }

}
