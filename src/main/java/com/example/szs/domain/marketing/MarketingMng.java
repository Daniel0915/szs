package com.example.szs.domain.marketing;

import com.example.szs.domain.embedded.Time;
import com.example.szs.model.dto.MarketingMngDetailDTO;
import com.example.szs.model.dto.MarketingMngDTO;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Builder
@Entity
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(name = "marketing_mng")
@Getter
public class MarketingMng {
    @Id @GeneratedValue
    private Long seq;
    private Long pSeq;
    private Integer page;
    private String title;
    private String imgPath;
    private String question;
    @Column(name = "answer_1")
    private String answer1;
    @Column(name = "answer_1_score")
    private String answer1Score;
    @Column(name = "answer_2")
    private String answer2;
    @Column(name = "answer_2_score")
    private String answer2Score;
    private String userToken;
    @Embedded
    @AttributeOverrides({
            @AttributeOverride(name = "regDt", column = @Column(name = "reg_dt")),
            @AttributeOverride(name = "modDt", column = @Column(name = "mod_dt"))
    })
    private Time time;

    public void updateParent(String title) {
        this.title = title;
        this.time.updateModDt();
    }

    public void updateChild(String question, String imgPath, String answer1, String answer1Score, String answer2, String answer2Score) {
        this.question = question;
        this.imgPath = imgPath;
        this.answer1 = answer1;
        this.answer1Score = answer1Score;
        this.answer2 = answer2;
        this.answer2Score = answer2Score;
        this.time.updateModDt();
    }

    public static MarketingMng createMarketingMngChild(Long pSeq, MarketingMngDetailDTO req) {
        return MarketingMng.builder()
                           .pSeq(pSeq)
                           .page(req.getPage())
                           .question(req.getQuestion())
                           .imgPath(req.getImg())
                           .answer1(req.getAnswer1())
                           .answer1Score(req.getAnswer1Score())
                           .answer2(req.getAnswer2())
                           .answer2Score(req.getAnswer2Score())
                           .time(new Time())
                           .build();
    }

    public MarketingMngDTO toDtoByParent() {
        return MarketingMngDTO.builder()
                              .title(title)
                              .build();
    }

    public MarketingMngDetailDTO toDtoByChild() {
        return MarketingMngDetailDTO.builder()
                                    .page(page)
                                    .img(imgPath)
                                    .question(question)
                                    .answer1(answer1)
                                    .answer1Score(answer1Score)
                                    .answer2(answer2)
                                    .answer2Score(answer2Score)
                                    .build();
    }
}
