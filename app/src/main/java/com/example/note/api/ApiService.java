package com.example.note.api;

import androidx.lifecycle.LiveData;

import com.example.note.model.BillCategory;
import com.example.note.model.BillRecord;
import com.example.note.model.BudgetInfo;
import com.example.note.model.ChartCategoryRatioItem;
import com.example.note.model.ChartTrendItem;
import com.example.note.model.GoodsCategory;
import com.example.note.model.GoodsInfo;
import com.example.note.model.LoginUser;
import com.example.note.model.Note;
import com.example.note.model.RequestType;
import com.example.note.model.UpdateInfoReq;
import com.example.note.model.UpdatePwdReq;

import java.util.List;

import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.DELETE;
import retrofit2.http.GET;
import retrofit2.http.HTTP;
import retrofit2.http.POST;
import retrofit2.http.PUT;
import retrofit2.http.Path;
import retrofit2.http.Query;

/**
 * api接口统一管理
 * 鉴权：基于 Cookie/Session（JSESSIONID）。200 成功，400 业务错误，401 未登录/会话过期，500 系统错误。
 */
public interface ApiService {

    // ==================== 用户相关（与后端文档一致） ====================

    /** 用户注册，无需登录 */
    @POST("user/register")
    LiveData<RequestType<LoginUser>> register(@Body RequestBody body);

    /** 用户登录，无需登录；成功时后端 Set-Cookie: JSESSIONID。请求体：{"phone":"xxx","password":"xxx"} */
    @POST("user/login")
    LiveData<RequestType<LoginUser>> login(@Body RequestBody body);

    /** “我的”页面 获取当前登录用户信息，需要登录 */
    @GET("user/info")
    LiveData<RequestType<LoginUser>> getUserInfo();

    /** 退出登录，需要登录；成功后本地需 cookieJar.clear() */
    @DELETE("user/logout")
    LiveData<RequestType<Void>> logout();

    /** 注销账号（物理删除），需要登录；成功后本地需 cookieJar.clear() */
    @DELETE("user/cancel")
    LiveData<RequestType<Void>> cancel();

    /** 修改基本信息，需要登录 */
    @PUT("user/update-info")
    LiveData<RequestType<Void>> updateInfo(@Body UpdateInfoReq body);

    /** 修改密码，需要登录 */
    @PUT("user/update-pwd")
    LiveData<RequestType<Void>> updatePwd(@Body UpdatePwdReq body);

    // ==================== 日程 Note ====================

    /** 获取全部日程（需要登录） */
    @GET("note/list")
    LiveData<RequestType<List<Note>>> getNoteList();

    /** 新增日程（需要登录） */
    @POST("note/add")
    LiveData<RequestType<Note>> addNote(@Body RequestBody body);

    /** 编辑日程（需要登录） */
    @PUT("note/update")
    LiveData<RequestType<Note>> updateNote(@Body RequestBody body);

    /** 更新完成状态（需要登录） */
    @PUT("note/updateStatus")
    LiveData<RequestType<Void>> updateNoteStatus(@Body RequestBody body);

    /** 删除日程（需要登录） */
    @DELETE("note/delete")
    LiveData<RequestType<Void>> deleteNote(@Query("noteId") int noteId);

    // ==================== 物品分类 /goods/ /category/ ====================

    /** 分类列表：GET /category/list，需要登录 */
    @GET("category/list")
    LiveData<RequestType<List<GoodsCategory>>> getCategoryList();

    /** 新增分类：POST /category/add，body: {\"categoryName\": \"食品\"} */
    @POST("category/add")
    LiveData<RequestType<GoodsCategory>> addCategory(@Body RequestBody body);

    /** 修改分类：PUT /category/update，body: {\"categoryId\":1,\"categoryName\":\"饮料\"} */
    @PUT("category/update")
    LiveData<RequestType<Void>> updateCategory(@Body RequestBody body);

    /** 删除分类：DELETE /category/delete?categoryId=1 */
    @DELETE("category/delete")
    LiveData<RequestType<Void>> deleteCategory(@Query("categoryId") long categoryId);

    /** 新增物品：POST /goods/add */
    @POST("goods/add")
    LiveData<RequestType<GoodsInfo>> addGoods(@Body RequestBody body);

    /** 修改物品：PUT /goods/update */
    @PUT("goods/update")
    LiveData<RequestType<GoodsInfo>> updateGoods(@Body RequestBody body);

    /** 删除物品：DELETE /goods/delete?goodsId=10 */
    @DELETE("goods/delete")
    LiveData<RequestType<Void>> deleteGoods(@Query("goodsId") long goodsId);

    /** 按分类查询物品列表：GET /goods/listByCategory?categoryId=1 */
    @GET("goods/listByCategory")
    LiveData<RequestType<List<GoodsInfo>>> listGoodsByCategory(@Query("categoryId") long categoryId);

    /** 按提醒类型查询物品列表：GET /goods/listByRemind?type=1|3|7 */
    @GET("goods/listByRemind")
    LiveData<RequestType<List<GoodsInfo>>> listGoodsByRemind(@Query("type") int type);

    // ==================== 记账模块 /bill/* ====================

    /** 记账分类列表 GET /bill/category/list */
    @GET("bill/category/list")
    LiveData<RequestType<List<BillCategory>>> getBillCategoryList();

    /** 新增记账分类 POST /bill/category/add */
    @POST("bill/category/add")
    LiveData<RequestType<BillCategory>> addBillCategory(@Body RequestBody body);

    /** 编辑记账分类 PUT /bill/category/update */
    @PUT("bill/category/update")
    LiveData<RequestType<BillCategory>> updateBillCategory(@Body RequestBody body);

    /** 删除记账分类 DELETE /bill/category/delete，请求体 {"categoryId":1} */
    @HTTP(method = "DELETE", path = "bill/category/delete", hasBody = true)
    LiveData<RequestType<Void>> deleteBillCategory(@Body RequestBody body);

    /** 最近收支记录 GET /bill/record/recent?limit=10 */
    @GET("bill/record/recent")
    LiveData<RequestType<List<BillRecord>>> getBillRecordRecent(@Query("limit") int limit);

    /** 按时间范围查询收支 GET /bill/record/listByTime?startTime=xxx&endTime=xxx */
    @GET("bill/record/listByTime")
    LiveData<RequestType<List<BillRecord>>> getBillRecordListByTime(@Query("startTime") String startTime, @Query("endTime") String endTime);

    /** 新增收支记录 POST /bill/record/add */
    @POST("bill/record/add")
    LiveData<RequestType<BillRecord>> addBillRecord(@Body RequestBody body);

    /** 编辑收支记录 PUT /bill/record/update */
    @PUT("bill/record/update")
    LiveData<RequestType<BillRecord>> updateBillRecord(@Body RequestBody body);

    /** 删除收支记录 DELETE /bill/record/delete，请求体 {"recordId":1} */
    @HTTP(method = "DELETE", path = "bill/record/delete", hasBody = true)
    LiveData<RequestType<Void>> deleteBillRecord(@Body RequestBody body);

    /** 月度预算查询 GET /bill/budget/get?yearMonth=yyyy-MM */
    @GET("bill/budget/get")
    LiveData<RequestType<BudgetInfo>> getBudget(@Query("yearMonth") String yearMonth);

    /** 设置月度预算 POST /bill/budget/set */
    @POST("bill/budget/set")
    LiveData<RequestType<BudgetInfo>> setBudget(@Body RequestBody body);

    /** 收支趋势 GET /bill/chart/trend?timeType=month&yearMonth=yyyy-MM */
    @GET("bill/chart/trend")
    LiveData<RequestType<List<ChartTrendItem>>> getChartTrend(@Query("timeType") String timeType, @Query("yearMonth") String yearMonth);

    /** 支出分类占比 GET /bill/chart/categoryRatio?yearMonth=yyyy-MM */
    @GET("bill/chart/categoryRatio")
    LiveData<RequestType<List<ChartCategoryRatioItem>>> getChartCategoryRatio(@Query("yearMonth") String yearMonth);

}
