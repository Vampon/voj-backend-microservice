package com.vampon.vojbackendjudgeservice.judge;

import cn.hutool.json.JSONUtil;
import com.vampon.vojbackendcommon.common.ErrorCode;
import com.vampon.vojbackendcommon.exception.BusinessException;
import com.vampon.vojbackendjudgeservice.judge.codesandbox.CodeSandBox;
import com.vampon.vojbackendjudgeservice.judge.codesandbox.CodeSandBoxFactory;
import com.vampon.vojbackendjudgeservice.judge.codesandbox.CodeSandBoxProxy;
import com.vampon.vojbackendjudgeservice.judge.strategy.JudgeContext;
import com.vampon.vojbackendmodel.model.codesandbox.ExecuteCodeRequest;
import com.vampon.vojbackendmodel.model.codesandbox.ExecuteCodeResponse;
import com.vampon.vojbackendmodel.model.dto.question.JudgeCase;
import com.vampon.vojbackendmodel.model.dto.questionsubmit.JudgeInfo;
import com.vampon.vojbackendmodel.model.entity.Question;
import com.vampon.vojbackendmodel.model.entity.QuestionSubmit;
import com.vampon.vojbackendmodel.model.enums.QuestionSubmitStatusEnum;
import com.vampon.vojbackendserviceclient.service.QuestionFeignClient;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class JudgeServiceImpl implements JudgeService {

    @Resource
    private QuestionFeignClient questionFeignClient;


    @Resource
    private JudgeManager judgeManager;

    @Value("${codesandbox.type:example}")
    private String type;

    @Override
    public QuestionSubmit doJudge(Long questionSubmitId) {
        // 根据提交题目的id拿到提交信息，题目信息
        QuestionSubmit questionSubmit = questionFeignClient.getQuestionSubmitById(questionSubmitId);
        if(questionSubmit==null){
            throw new BusinessException(ErrorCode.NOT_FOUND_ERROR,"提交信息不存在");
        }
        Long questionId = questionSubmit.getQuestionId();
        Question question = questionFeignClient.getQuestionById(questionId);
        if(question==null){
            throw new BusinessException(ErrorCode.NOT_FOUND_ERROR,"题目信息不存在");
        }
        // 如果题目的提交状态不为等待中，就不需要重复执行了
        // todo：是否需要在外层再加个锁，事务？
        if(!questionSubmit.getStatus().equals((QuestionSubmitStatusEnum.WAITING.getValue()))){
            throw new BusinessException(ErrorCode.OPERATION_ERROR,"题目正在判题中");
        }
        // 更改判题的状态为“判题中”
        QuestionSubmit questionSubmitUpdate = new QuestionSubmit();
        questionSubmitUpdate.setId(questionSubmitId);
        questionSubmitUpdate.setStatus(QuestionSubmitStatusEnum.RUNNING.getValue());
        boolean update = questionFeignClient.updateQuestionSubmitById(questionSubmitUpdate);
        if(!update){
            throw new BusinessException(ErrorCode.SYSTEM_ERROR,"题目状态更新错误");
        }
        // 调用代码沙箱，获取执行结果
        CodeSandBox codeSandBox = CodeSandBoxFactory.newInstance(type);
        codeSandBox = new CodeSandBoxProxy(codeSandBox);
        String code = questionSubmit.getCode();
        String codeLanguage = questionSubmit.getCodeLanguage();
        String judgeCaseStr = question.getJudgeCase();
        List<JudgeCase> judgeCaseList = JSONUtil.toList(judgeCaseStr, JudgeCase.class);
        List<String> inputList = judgeCaseList.stream().map(JudgeCase::getInput).collect(Collectors.toList());
        ExecuteCodeRequest executeCodeRequest = ExecuteCodeRequest.builder()
                .code(code)
                .language(codeLanguage)
                .inputList(inputList).build();
        ExecuteCodeResponse executeCodeResponse = codeSandBox.executeCode(executeCodeRequest);
        // 根据沙箱执行结果判题
        JudgeContext judgeContext = new JudgeContext();
        judgeContext.setJudgeInfo(executeCodeResponse.getJudgeInfo());
        judgeContext.setInputList(inputList);
        judgeContext.setOutputList(executeCodeResponse.getOutputList());
        judgeContext.setJudgeCaseList(judgeCaseList);
        judgeContext.setQuestion(question);
        judgeContext.setQuestionSubmit(questionSubmit);
        JudgeInfo judgeInfo = judgeManager.doJudge(judgeContext);

        // 修改数据库中的判题结果
        // todo:后续需要改造为异步的
        questionSubmitUpdate = new QuestionSubmit();
        questionSubmitUpdate.setId(questionSubmitId);
        questionSubmitUpdate.setStatus(QuestionSubmitStatusEnum.SUCCEED.getValue());
        questionSubmitUpdate.setJudgeInfo(JSONUtil.toJsonStr(judgeInfo));
        update = questionFeignClient.updateQuestionSubmitById(questionSubmitUpdate);
        if(!update){
            throw new BusinessException(ErrorCode.SYSTEM_ERROR,"题目状态更新错误");
        }

        QuestionSubmit questionSubmitResult = questionFeignClient.getQuestionSubmitById(questionSubmitId);
        return questionSubmitResult;
    }
}
