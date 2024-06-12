package com.vampon.vojbackendjudgeservice.judge;


import com.vampon.vojbackendjudgeservice.judge.strategy.DefaultJudgeStrategy;
import com.vampon.vojbackendjudgeservice.judge.strategy.JavaLanguageJudgeStrategy;
import com.vampon.vojbackendjudgeservice.judge.strategy.JudgeContext;
import com.vampon.vojbackendjudgeservice.judge.strategy.JudgeStrategy;
import com.vampon.vojbackendmodel.model.dto.questionsubmit.JudgeInfo;
import com.vampon.vojbackendmodel.model.entity.QuestionSubmit;
import org.springframework.stereotype.Service;

/**
 * 判题管理（简化调用）
 */
@Service
public class JudgeManager {
    /**
     * 执行判题
     *
     * @param judgeContext
     * @return
     */
    JudgeInfo doJudge(JudgeContext judgeContext) {
        QuestionSubmit questionSubmit = judgeContext.getQuestionSubmit();
        String codeLanguage = questionSubmit.getCodeLanguage();
        JudgeStrategy judgeStrategy = new DefaultJudgeStrategy();
        if ("java".equals(codeLanguage)){
           judgeStrategy = new JavaLanguageJudgeStrategy();
        }
        return judgeStrategy.doJudge(judgeContext);
    }
}
