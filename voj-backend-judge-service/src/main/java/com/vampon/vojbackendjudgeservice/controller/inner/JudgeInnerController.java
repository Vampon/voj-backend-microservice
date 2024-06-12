package com.vampon.vojbackendjudgeservice.controller.inner;

import com.vampon.vojbackendjudgeservice.judge.JudgeService;
import com.vampon.vojbackendmodel.model.entity.QuestionSubmit;
import com.vampon.vojbackendserviceclient.service.JudgeFeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;

/**
 * 服务内部互相调用的接口，不是给前端的
 */
@RestController
@RequestMapping("/inner")
public class JudgeInnerController implements JudgeFeignClient {

    @Resource
    private JudgeService judgeService;

    /**
     * 判题
     * @param questionSubmitId
     * @return
     */
    @Override
    @PostMapping("/do")
    public QuestionSubmit doJudge(@RequestParam("questionSubmitId") Long questionSubmitId){
        return judgeService.doJudge(questionSubmitId);
    }

}
