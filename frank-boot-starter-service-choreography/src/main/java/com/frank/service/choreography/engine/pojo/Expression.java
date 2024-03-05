package com.frank.service.choreography.engine.pojo;

import lombok.Data;

/**
 * @author francis
 * @version 2022-04-29
 */
@Data
public class Expression {
    /**
     * 原始表达式
     */
    private String originExpression;
    /**
     * 真实表达式
     */
    private String realExpression;
    /**
     * 表达式所属alias
     */
    private String alias;
    /**
     * 表达式的结果值
     */
    private Object expressionValue;
    /**
     * 意外表达式
     */
    private String unexpectExpression;
    /**
     * 表达式意外结果值
     */
    private Object UnexpectExpressionValue;

    public Expression() {

    }

    public Expression(String originExpression, String realExpression, String alias) {
        this.originExpression = originExpression;
        this.realExpression = realExpression;
        this.alias = alias;
    }

}
