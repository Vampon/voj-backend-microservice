package com.vampon.vojbackendjudgeservice.judge.codesandbox.impl;


import com.vampon.vojbackendjudgeservice.judge.codesandbox.CodeSandBox;
import com.vampon.vojbackendmodel.model.codesandbox.ExecuteCodeRequest;
import com.vampon.vojbackendmodel.model.codesandbox.ExecuteCodeResponse;
import com.vampon.vojbackendmodel.model.dto.questionsubmit.JudgeInfo;
import com.vampon.vojbackendmodel.model.enums.JudgeInfoMessageEnum;
import com.vampon.vojbackendmodel.model.enums.QuestionSubmitStatusEnum;

import java.util.List;

/**
 * 示例代码沙箱
 */
public class ExampleCodeSandBox implements CodeSandBox {
    @Override
    public ExecuteCodeResponse executeCode(ExecuteCodeRequest executeCodeRequest) {
        List<String> inputList = executeCodeRequest.getInputList();
        ExecuteCodeResponse executeCodeResponse = new ExecuteCodeResponse();
        executeCodeResponse.setOutputList(inputList);
        executeCodeResponse.setMessage("测试执行成功");
        executeCodeResponse.setStatus(QuestionSubmitStatusEnum.SUCCEED.getValue());
        JudgeInfo judgeInfo = new JudgeInfo();
        judgeInfo.setMessage(JudgeInfoMessageEnum.ACCEPTED.getText());
        judgeInfo.setTime(100L);
        judgeInfo.setMemory(100L);
        executeCodeResponse.setJudgeInfo(judgeInfo);
        return executeCodeResponse;
    }
}
