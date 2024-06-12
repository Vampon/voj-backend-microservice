package com.vampon.vojbackendmodel.model.codesandbox;

import com.vampon.vojbackendmodel.model.dto.questionsubmit.JudgeInfo;
import lombok.Data;

import java.util.List;

@Data
public class ExecuteCodeResponse {
    /**
     * 对应输入用例的输出结果
     */
    private List<String> outputList;

    /**
     * 接口信息
     */
    private String message;

    /**
     * 执行状态
     */
    private Integer status;

    /**
     * 执行信息
     */
    private JudgeInfo judgeInfo;

}
