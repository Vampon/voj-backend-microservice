package com.vampon.vojbackendjudgeservice.judge.codesandbox.impl;


import com.vampon.vojbackendjudgeservice.judge.codesandbox.CodeSandBox;
import com.vampon.vojbackendmodel.model.codesandbox.ExecuteCodeRequest;
import com.vampon.vojbackendmodel.model.codesandbox.ExecuteCodeResponse;

/**
 * 第三方代码沙箱
 */
public class ThirdPartyCodeSandBox implements CodeSandBox {
    @Override
    public ExecuteCodeResponse executeCode(ExecuteCodeRequest executeCodeRequest) {
        return null;
    }
}
