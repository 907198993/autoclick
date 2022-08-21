package com.hysa.auto.service;

import com.fengnan.newzdzf.dynamic.entity.StoreEntity;
import com.fengnan.newzdzf.entity.DynamicEntity;
import com.fengnan.newzdzf.entity.DynamicSearchCodeResult;
import com.fengnan.newzdzf.entity.DynamicSearchResult;
import com.fengnan.newzdzf.entity.DynamicVo;
import com.fengnan.newzdzf.me.entity.LabelEntity;
import com.fengnan.newzdzf.me.entity.PictureUploadAuthorize;
import com.fengnan.newzdzf.me.entity.UploadGoodVo;
import com.fengnan.newzdzf.me.publish.entity.AddDynamicParams;
import com.fengnan.newzdzf.me.publish.entity.CategoryCrowBean;
import com.fengnan.newzdzf.me.publish.entity.CategroyIdVo;
import com.fengnan.newzdzf.me.publish.entity.SaveResultEntity;
import com.fengnan.newzdzf.me.publish.entity.VerifyPicEntity;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import io.reactivex.Observable;
import me.goldze.mvvmhabit.http.BaseResponse;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.Multipart;
import retrofit2.http.POST;
import retrofit2.http.Part;
import retrofit2.http.PartMap;
import retrofit2.http.Query;
import retrofit2.http.Url;

public interface GoodService {
    //我的相册列表
    @POST("personProduct/getPersonProductByType.action?marketCode=gz")
    Observable<BaseResponse<DynamicVo>> postStoreGood(@Body HashMap<String, Object> params);

    //我的相册根据标签筛选列表
    @POST("personProduct/getPersonProductByLabelV2.action")
    Observable<BaseResponse<DynamicVo>> postLabelStoreGood(@Body HashMap<String, Object> params);

    //在我的相册筛选
    @POST("personProduct/getPersonProductByLabelV3.action")
    Observable<BaseResponse<DynamicVo>> postMyStoreGood(@Body HashMap<String, Object> params);

    //在我的相册筛选可搜索关键字
    @POST("personProduct/getPersonProductByLabelByCloudAlbum.action")
    Observable<BaseResponse<DynamicVo>> postMyStoreGoodWithKey(@Body HashMap<String, Object> params);

    //我的相册根据权限查看列表  仅自己可见0  所有人可见1
    @POST("personProduct/searchMainandAgency.action")
    Observable<BaseResponse<DynamicVo>> postStateGood(@Body HashMap<String, Object> params);

    //我的相册根据关键字筛选列表
    @POST("personProduct/searchByText.action")
    Observable<BaseResponse<DynamicVo>> postTextStoreGood(@Body HashMap<String, Object> params);

    //我的相册根据图片筛选列表
    @POST("personProduct/searchPicWithUrl.action?marketCode=gz")
    Observable<BaseResponse<List<DynamicEntity>>> postImageStoreGood(@Body HashMap<String, Object> params);

    //新的根据图片筛选列表
    @Multipart
    @POST("personProduct/getImgSearchResult.action?size=30")
    Observable<BaseResponse<List<DynamicEntity>>> postImageStoreGoodNew(@Part MultipartBody.Part file, @PartMap Map<String, Object> param);


    @POST("personProduct/findMarketCategoryAndCrowdHasTypeName.action")
    Observable<BaseResponse<CategroyIdVo>> postCategoryAndCrowd(@Body HashMap<String, Object> params);

    @POST("personProduct/findLabelByCode.action")
    Observable<BaseResponse<List<LabelEntity>>> postLabelText(@Body HashMap<String, Object> params);

    //查看商品来源
    @POST("user/V2/friendsTimeLineByCode.action")
    Observable<BaseResponse<List<DynamicEntity>>> selectSource(@Body HashMap<String, Object> params);

    //发布商品
    @POST("personProduct/V4/addProduct.action?marketCode=gz")
    Observable<BaseResponse<PictureUploadAuthorize>> publishGood(@Body UploadGoodVo params);

    //同步商品
    @POST("personProduct/V3/collectNewProductBrother?marketCode=gz")
    Observable<BaseResponse<String>> addSelectPhoto(@Body HashMap<String, Object> params);

    //加入相册
    @POST("personProduct/V4/collectBrother.action?marketCode=gz")
    Observable<BaseResponse<SaveResultEntity>> addPhoto(@Body HashMap<String, Object> params);

    //修改动态
    @POST("personProduct/updateByAlbum.action")
    Observable<BaseResponse> editPhoto(@Body AddDynamicParams params);

    //获取分类   getAllTypes原来的
    @GET("marketTypeStore/getAllTypeAndCrowdAttr.action?marketCode=gz")
    Observable<BaseResponse<CategoryCrowBean>> getCatePhoto();

    //上传搜图图片
    @Multipart
    @POST("personProduct/searchPicWithFileV2.action?marketCode=gz")
    Observable<BaseResponse> uploadSearchImage(@Part MultipartBody.Part file);

    //上传图片
    @Multipart
    @POST
    Observable<BaseResponse<ResponseBody>> uploadImage(@Url String url, @Part MultipartBody.Part file);

    //上传视频图片
    @POST
    Observable<BaseResponse> uploadVideoImage(@Url String url, @Body RequestBody params);

    //上传视频
    @POST
    Observable<BaseResponse> uploadVideo(@Url String url, @Body RequestBody params);

    /**
     * 验证图片上传
     *
     * @return
     */
    @GET("fileUpload/verifyUpload.action?")
    Observable<BaseResponse<VerifyPicEntity>> verifyImage(@Query("pid") String pid, @Query("type") int type);

    /**
     * 修改产品状态
     */
    @POST("personProduct/updateStatus.action")
    Observable<BaseResponse<Object>> updateStatus(@Body HashMap<String, Object> params);

    /**
     * 修改纯文字产品状态
     */
    @POST("personProduct/updateStatusByPlainText.action")
    Observable<BaseResponse<Object>> updateTextStatus(@Body HashMap<String, Object> params);

    /**
     * 商家相册页获取商家信息
     *
     * @param params
     * @return
     */
    @POST("myStoreV3.action")
    Observable<BaseResponse<StoreEntity>> getMerchantInfo(@Body HashMap<String, Object> params);

    //商家相册根据标签筛选列表
    @POST("personProduct/getPersonProductByLabel.action")
    Observable<BaseResponse<DynamicVo>> postStoreGoodForLabel(@Body HashMap<String, Object> params);

    //商家相册根据关键字筛选列表
    @POST("personProduct/searchByTextV3.action")
    Observable<BaseResponse<DynamicSearchResult>> postSearchStoreGoodWithText(@Body HashMap<String, Object> params);

    //商家相册根据搜索码筛选列表
    @POST("personProduct/getPersonProductByShopCode.action")
    Observable<BaseResponse<DynamicSearchCodeResult>> postSearchStoreGoodWithSearchCode(@Body HashMap<String, Object> params);

    //增加产品浏览次数
    @POST("personProduct/V4/downloadPersonProductV2.action")
    Observable<BaseResponse> postDownloadNum(@Body HashMap<String, Object> params);

    //获取AI识别价格的正则表达式
    @GET("emoji/getEmoji.action?type=0")
    Observable<BaseResponse<String>> getRegex();

    //提交AI识别价格的正则表达式
    @POST("emoji/updateEmoji.action")
    Observable<BaseResponse<String>> postRegex(@Body HashMap<String, String> params);

    //获取匹配规格文件
    @GET
    Observable<ResponseBody> getMatchSpecFile(@Url String url);

}
