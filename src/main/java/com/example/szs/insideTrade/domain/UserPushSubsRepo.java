package com.example.szs.insideTrade.domain;

import com.example.szs.model.queryDSLSearch.UserPushSubsSearchCondition;

import java.util.List;

public interface UserPushSubsRepo {
    List<UserPushSubs> getUserPushSubsListBy(UserPushSubsSearchCondition condition);
}
