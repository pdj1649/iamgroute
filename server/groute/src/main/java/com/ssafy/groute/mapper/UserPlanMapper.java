package com.ssafy.groute.mapper;

import com.ssafy.groute.dto.PlanLike;
import com.ssafy.groute.dto.UserPlan;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface UserPlanMapper {
    void insertUserPlan(UserPlan userPlan) throws Exception;
    UserPlan selectUserPlan(int id) throws Exception;
    List<UserPlan> selectAllUserPlan() throws Exception;
    List<UserPlan> selectTUserPlan() throws Exception;
    void deleteUserPlan(int id) throws Exception;
    void updateUserPlan(UserPlan userPlan) throws Exception;
    void deleteAllUserPlanByUId(String userId) throws Exception;
    List<Integer> findAllPlanIdsByUId(String userId) throws Exception;
    void likePlan(PlanLike planLike) throws Exception;
    void unLikePlan(int id) throws Exception;
    PlanLike isLike(PlanLike planLike) throws Exception;
    List<Integer> selectAllUserPlanByTotalDate(int totalDate);
    List<Integer> selectPlaceListByPlanId(int id);
    List<UserPlan> bestPlanList() throws Exception;
    List<Integer> selectThemeListByPlanId(int id) throws Exception;
    List<UserPlan> findEndPlanById(String userId) throws Exception;
    List<UserPlan> findNotEndPlanById(String userId) throws Exception;
    void deleteUserPlanByUSerId(String userId) throws Exception;
    List<UserPlan> selectAllPlanByUserId(String userId) throws Exception;
    List<UserPlan> selectAllUserPlanByUserId(String userId) throws Exception;
}