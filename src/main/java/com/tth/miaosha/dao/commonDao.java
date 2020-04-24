package com.tth.miaosha.dao;

import org.apache.ibatis.session.SqlSessionFactory;

import javax.annotation.Resource;

public class commonDao extends SqlSessionDaoSupport{

    @Override
    protected void checkDaoConfig() throws IllegalArgumentException {

    }

    @Resource
    public void setSqlSessionFactory(SqlSessionFactory sqlSessionFactory){
        super.setSqlSessionFactory(sqlSessionFactory);
    }

}
