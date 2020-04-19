package com.kathryn.kathryn.utills.Network;

import com.google.gson.JsonObject;

import java.util.List;
import java.util.Map;

import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.HTTP;
import retrofit2.http.HeaderMap;
import retrofit2.http.Multipart;
import retrofit2.http.POST;
import retrofit2.http.Part;

/**
 * Created by apple on 12/18/17.
 */

public interface RestService {

    //region AppCompatPreferenceActivity, Login, Fogot and Register EndPoints
    //get application settings
    @GET("settings/")
    Call<ResponseBody> getSettings(
            @HeaderMap Map<String, String> headers
    );

    @GET("app_extra/")
    Call<ResponseBody> getAppExtraSettings(
            @HeaderMap Map<String, String> headers
    );
    @GET("cart-empty/")
    Call<ResponseBody> emptyCart(
            @HeaderMap Map<String, String> headers
    );

    @POST("app_extra/feedback/")
    Call<ResponseBody> postSendFeedback(
            @Body JsonObject login,
            @HeaderMap Map<String, String> headers
    );


    //get login views
    @GET("login/")
    Call<ResponseBody> getLoginView(
            @HeaderMap Map<String, String> headers
    );

    //Login user
    @POST("login/")
    Call<ResponseBody> postLogin(
            @Body JsonObject login,
            @HeaderMap Map<String, String> headers
    );

    //Get Register View data
    @GET("register/")
    Call<ResponseBody> getRegisterView(
            @HeaderMap Map<String, String> headers
    );

    //Register New user
    @POST("register/")
    Call<ResponseBody> postRegister(
            @Body JsonObject register,
            @HeaderMap Map<String, String> headers
    );

    //Get forgot Views data
    @GET("forgot/")
    Call<ResponseBody> getForgotDataViewDetails(
            @HeaderMap Map<String, String> headers
    );

    //Send email to user
    @POST("forgot/")
    Call<ResponseBody> postForgotPassword(
            @Body JsonObject forgotPassword,
            @HeaderMap Map<String, String> headers
    );

    //Send email to user
    @POST("page/")
    Call<ResponseBody> postGetCustomePages(
            @Body JsonObject getCustomPage,
            @HeaderMap Map<String, String> headers
    );

    //Get Verify account views data
    @GET("login/confirm/")
    Call<ResponseBody> getVerifyAccountViewDetails(
            @HeaderMap Map<String, String> headers
    );

    //Confirm user account
    @POST("login/confirm/")
    Call<ResponseBody> postConfirmAccount(
            @Body JsonObject getCustomPage,
            @HeaderMap Map<String, String> headers
    );

    //endregion


    //region Blogs EndPoints

    //get all blog detials
    @GET("posts/")
    Call<ResponseBody> getBlogsDetails(
            @HeaderMap Map<String, String> headers
    );

    //load more blogs on scroll down
    @POST("posts/")
    Call<ResponseBody> postLoadMoreBlogs(
            @Body JsonObject loadMoreBlogs,
            @HeaderMap Map<String, String> headers
    );

    //get single blog details
    @POST("posts/detail/")
    Call<ResponseBody> postGetBlogDetail(
            @Body JsonObject blogDetail,
            @HeaderMap Map<String, String> headers
    );

    //post comment in blog detials
    @POST("posts/comments/")
    Call<ResponseBody> postComments(
            @Body JsonObject postcomment,
            @HeaderMap Map<String, String> headers
    );

    //Load more cooments in blog details
    @POST("posts/comments/get/")
    Call<ResponseBody> postLoadMoreComments(
            @Body JsonObject loadMore,
            @HeaderMap Map<String, String> headers
    );
//endregion


    //region Related To new AdPost Endpoints
    @POST("post_ad/")
    //Post New Ad
    Call<ResponseBody> postAdNewPost(
            @Body JsonObject adNewPost,
            @HeaderMap Map<String, String> headers
    );

    //Get New Ad views or edit post views
    @POST("post_ad/is_update/")
    Call<ResponseBody> postGetAdNewPostViews(
            @Body JsonObject adNewViews,
            @HeaderMap Map<String, String> headers
    );

    //Get Sub Categories Details on Spinner click
    @POST("post_ad/subcats/")
    Call<ResponseBody> postGetSubCategories(
            @Body JsonObject getSubCat,
            @HeaderMap Map<String, String> headers
    );

    //Get Sub Location Details on Spinner click
    @POST("post_ad/sublocations/")
    Call<ResponseBody> postGetSubLocations(
            @Body JsonObject getSubLocations,
            @HeaderMap Map<String, String> headers
    );

    //Get Dynamic Field on Spinner click if Category template is on
    @POST("post_ad/dynamic_fields/")
    Call<ResponseBody> postGetDynamicFields(
            @Body JsonObject getDynamicFields,
            @HeaderMap Map<String, String> headers
    );

    //Delete image from New Ad post
    @POST("post_ad/image/delete/")
    Call<ResponseBody> postDeleteImages(
            @Body JsonObject deleteImages,
            @HeaderMap Map<String, String> headers
    );

    //Ad new Images in New Ad post
    @Multipart
    @POST("post_ad/image/")
    Call<ResponseBody> postUploadImage(
            @Part("ad_id") RequestBody description,
            @Part("is_required") RequestBody requestBody,
            @Part List<MultipartBody.Part> parts,
            @HeaderMap Map<String, String> headers
    );

    //Get images if there is any exception
    @POST("post_ad/image/get/")
    Call<ResponseBody> getAllImages(
            @Body JsonObject postBid,
            @HeaderMap Map<String, String> headers
    );

    //endregion


    //region Ad Details and Bids EndPoints
    //Get all data for Specific Ad
    @POST("ad_post/")
    Call<ResponseBody> getAdsDetail(
            @Body JsonObject postBid,
            @HeaderMap Map<String, String> headers
    );

    //send report from Ad Details Activity
    @POST("ad_post/report/")
    Call<ResponseBody> postSendReport(
            @Body JsonObject sendReport,
            @HeaderMap Map<String, String> headers
    );

    //Ad is added in favourite from Ad Details Activity
    @POST("ad_post/favourite/")
    Call<ResponseBody> postAddToFavourite(
            @Body JsonObject favourite,
            @HeaderMap Map<String, String> headers
    );

    //Ad is added in featured from Ad Details Activity
    @POST("ad_post/featured/")
    Call<ResponseBody> postMakeFeatured(
            @Body JsonObject featured,
            @HeaderMap Map<String, String> headers
    );

    //Get Bid Details from Ad Details
    @POST("ad_post/bid/")
    Call<ResponseBody> getBidDetails(
            @Body JsonObject BidDetails,
            @HeaderMap Map<String, String> headers
    );

    //Post New Bid
    @POST("ad_post/bid/post/")
    Call<ResponseBody> postBid(
            @Body JsonObject postBid,
            @HeaderMap Map<String, String> headers
    );

    //Post Ad Rating
    @POST("ad_post/ad_rating/new/")
    Call<ResponseBody> postRating(
            @Body JsonObject postRating,
            @HeaderMap Map<String, String> headers
    );
    //Post Ad Comment Rating
    @POST("ad_post/ad_rating/rating_emojies/")
    Call<ResponseBody> postCommentRating(
            @Body JsonObject postRating,
            @HeaderMap Map<String, String> headers
    );

    //post get more Ad Rating
    @POST("ad_post/ad_rating/")
    Call<ResponseBody> postGetMoreAdRating(
            @Body JsonObject postGetMoreRatings,
            @HeaderMap Map<String, String> headers
    );
    //endregion


    //region Menu and Dynamic Search Endpoints
    //Get all Search Dynmaic Views
    @GET("ad_post/search/")
    Call<ResponseBody> getSearchDetails(
            @HeaderMap Map<String, String> headers
    );

    //Get load more ADs or search in Search Activity
    @POST("ad_post/search/")
    Call<ResponseBody> postGetSearchNdLoadMore(
            @Body JsonObject searchNdMore,
            @HeaderMap Map<String, String> headers
    );

    //Get Sub Categories Details on spinner click
    @POST("ad_post/subcats/")
    Call<ResponseBody> postGetSearcSubCats(
            @Body JsonObject searchSubCats,
            @HeaderMap Map<String, String> headers
    );

    //Get Sub Location Details on spinner click
    @POST("ad_post/sublocations/")
    Call<ResponseBody> postGetSearcSubLocation(
            @Body JsonObject searchSubCats,
            @HeaderMap Map<String, String> headers
    );

    //get Dynamic Fields data for search activity using categories_id
    @POST("ad_post/dynamic_widget/")
    Call<ResponseBody> postGetSearchDynamicFields(
            @Body JsonObject searchSubCats,
            @HeaderMap Map<String, String> headers
    );

    //Get search data from menu bar
    @POST("ad_post/category/")
    Call<ResponseBody> postGetMenuSearchData(
            @Body JsonObject menuSearch,
            @HeaderMap Map<String, String> headers
    );
    //endregion


    //region Home and Profile Endpoints
    //Get Home Details
    @GET("home")
    Call<ResponseBody> getHomeDetails(
            @HeaderMap Map<String, String> headers
    );

    //Set firebase id with server
    @POST("home")
    Call<ResponseBody> postFirebaseId(
            @Body JsonObject firebaseId,
            @HeaderMap Map<String, String> headers
    );

    //change NearBy Status and update lcoation
    @POST("profile/nearby/")
    Call<ResponseBody> postChangeNearByStatus(
            @Body JsonObject changeStatus,
            @HeaderMap Map<String, String> headers
    );

    //Get All locations and Categories
    @POST("terms/")
    Call<ResponseBody> getAllLocAndCat(
            @Body JsonObject jsonObject,
            @HeaderMap Map<String, String> headers
    );
    //Change location of app
    @POST("site-location/")
    Call<ResponseBody> postLocationID(
            @Body JsonObject jsonObject,
            @HeaderMap Map<String, String> headers

    );
    //Get Profile Details
    @GET("profile/")
    Call<ResponseBody> getProfileDetails(
            @HeaderMap Map<String, String> headers
    );

    //Get Edit Profile Details
    @GET("profile/")
    Call<ResponseBody> getEditProfileDetails(
            @HeaderMap Map<String, String> headers
    );

    @POST("profile/")
    Call<ResponseBody> postUpdateProfile(
            @Body JsonObject editProfile,
            @HeaderMap Map<String, String> headers
    );

    @POST("profile/delete/user_account/")
    Call<ResponseBody> postDeleteAccount(
            @Body JsonObject deleteAccount,
            @HeaderMap Map<String, String> headers
    );

    @POST("profile/public/")
    Call<ResponseBody> postGetPublicProfile(
            @Body JsonObject editProfile,
            @HeaderMap Map<String, String> headers
    );

    @GET("profile/phone_number/")
    Call<ResponseBody> getVerifyCode(
            @HeaderMap Map<String, String> headers
    );

    @POST("profile/phone_number/verify/")
    Call<ResponseBody> postVerifyPhoneNumber(
            @Body JsonObject editProfile,
            @HeaderMap Map<String, String> headers
    );

    //Upload image in Edit page
    @Multipart
    @POST("profile/image/")
    Call<ResponseBody> postUploadProfileImage(
            @Part MultipartBody.Part file,
            @HeaderMap Map<String, String> headers
    );

    //Change password in Edit Profile
    @POST("profile/reset_pass")
    Call<ResponseBody> postChangePasswordEditProfile(
            @Body JsonObject retingDetails,
            @HeaderMap Map<String, String> headers
    );

    //Get ratting Details
    @POST("profile/ratting_get/")
    Call<ResponseBody> postGetRatingDetails(
            @Body JsonObject retingDetails,
            @HeaderMap Map<String, String> headers
    );

    //post profile rating
    @POST("profile/ratting/")
    Call<ResponseBody> postProfileRating(
            @Body JsonObject profileRating,
            @HeaderMap Map<String, String> headers
    );
    //endregion


    //region Related To Ads Endpoints
    //Get My Ad details
    @GET("ad/")
    Call<ResponseBody> getMyAdsDetails(
            @HeaderMap Map<String, String> headers
    );@GET("ad/most-visited/")
    Call<ResponseBody> getMyMostViewedDetails(
            @HeaderMap Map<String, String> headers
    );
    @GET("ad/expire-sold/")
    Call<ResponseBody> getExpiredandSoldAds(
            @HeaderMap Map<String, String> headers
    );

    //Load More Myds
    @POST("ad/")
    Call<ResponseBody> postGetLoadMoreMyAds(
            @Body JsonObject loadMoreMyAds,
            @HeaderMap Map<String, String> headers
    );
    @POST("ad/most-visited/")
    Call<ResponseBody> postGetLoadMoregetMyMostViewedDetails(
            @Body JsonObject loadMoreMyAds,
            @HeaderMap Map<String, String> headers
    );

    //Delete My ads
    @HTTP(method = "DELETE", path = "ad/delete/", hasBody = true)
    Call<ResponseBody> deleteMyAds(
            @Body JsonObject jsonObject,
            @HeaderMap Map<String, String> headers
    );

    //Get Featured AD details
    @GET("ad/featured/")
    Call<ResponseBody> getFeaturedAdsDetails(
            @HeaderMap Map<String, String> headers
    );

    //Load more Favourite Ads
    @POST("ad/featured/")
    Call<ResponseBody> postGetLoadMoreFeaturedAds(
            @Body JsonObject loadMoreFavourite,
            @HeaderMap Map<String, String> headers
    );

    //Get In AD details
    @GET("ad/inactive/")
    Call<ResponseBody> getInactiveAdsDetails(
            @HeaderMap Map<String, String> headers
    );

    //Get Inactive AD details
    @POST("ad/inactive/")
    Call<ResponseBody> postGetLoadMoreInactiveAds(
            @Body JsonObject loadMoreInactive,
            @HeaderMap Map<String, String> headers
    );
    //Get Rejected AD details

    @GET("ad/rejected/")
    Call<ResponseBody> getRejectedAdsDetails(
            @HeaderMap Map<String, String> headers
    );
    //Get Favourite AD details
    @GET("ad/favourite/")
    Call<ResponseBody> getFavouriteAdsDetails(
            @HeaderMap Map<String, String> headers
    );

    //Load more Favourite Ads
    @POST("ad/favourite/")
    Call<ResponseBody> postGetLoadMoreFavouriteAds(
            @Body JsonObject loadMoreFavourite,
            @HeaderMap Map<String, String> headers
    );

    //Remove ad From Favourite
    @POST("ad/favourite/remove/")
    Call<ResponseBody> postRemoveFavAd(
            @Body JsonObject removeFavAd,
            @HeaderMap Map<String, String> headers
    );

    //Update Ad Status
    @POST("ad/update/status/")
    Call<ResponseBody> postUpdateAdStatus(
            @Body JsonObject updateStatus,
            @HeaderMap Map<String, String> headers
    );
    //endregion


    //region Related to packages and payment Endpoints
    //Get packages Details
    @GET("packages/")
    Call<ResponseBody> getPackagesDetails(
            @HeaderMap Map<String, String> headers
    );

    //Get Stripe Payment View Details
    @GET("payment/card/")
    Call<ResponseBody> getStripeDetailsView(
            @HeaderMap Map<String, String> headers
    );

    //Checkout endpoint in checkout proocess activity
        @POST("payment/")
    Call<ResponseBody> postCheckout(
            @Body JsonObject updateStatus,
            @HeaderMap Map<String, String> headers
    );

    //Get Data When Payment is Completed Successfully
    @GET("payment/complete")
    Call<ResponseBody> getPaymentCompleteData(
            @HeaderMap Map<String, String> headers
    );


    //endregion

    //region Message EndPoints
    //Get Received offers details
    @GET("message/inbox/")
    Call<ResponseBody> getRecievedOffers(
            @HeaderMap Map<String, String> headers
    );

    //Load More messages in Recieved offers
    @POST("message/inbox/")
    Call<ResponseBody> postLoadMoreRecievedOffer(
            @Body JsonObject loadMoreReceieved,
            @HeaderMap Map<String, String> headers
    );

    //Get Send Offers Details
    @GET("message/")
    Call<ResponseBody> getSendOffers(
            @HeaderMap Map<String, String> headers
    );

    //Load more messgae list in send offeres
    @POST("message_post/")
    Call<ResponseBody> postLoadMoreSendOffers(
            @Body JsonObject loadSendOffers,
            @HeaderMap Map<String, String> headers
    );

    //Message popup view in Ad Details
    @POST("message/popup/")
    Call<ResponseBody> postSendMessageFromAd(
            @Body JsonObject postBid,
            @HeaderMap Map<String, String> headers
    );

    //Get complete chat or load more chat in
    @POST("message/chat/post/")
    Call<ResponseBody> postGetChatORLoadMore(
            @Body JsonObject getChat,
            @HeaderMap Map<String, String> headers
    );
    @POST("message/chat/userblock/")
    Call<ResponseBody> postUserBlock(
            @Body JsonObject Block,
            @HeaderMap Map<String, String> headers);
@POST("message/chat/userUnblock/")
    Call<ResponseBody> postUserUnBlock(
            @Body JsonObject UnBlock,
            @HeaderMap Map<String, String> headers);

    //Send Message
    @POST("message/chat/")
    Call<ResponseBody> postSendMessage(
            @Body JsonObject sendMessage,
            @HeaderMap Map<String, String> headers
    );

    //Get Recieved offers list or Load More
    @POST("message/offers")
    Call<ResponseBody> postGetRecievedOffersList(
            @Body JsonObject recievedList,
            @HeaderMap Map<String, String> headers
    );


    //endregion

    //region User block and UnBlock
    @POST("user/block/")
    Call<ResponseBody> postBlockUser(
            @Body JsonObject blockUser,
            @HeaderMap Map<String, String> headers
    );

    @GET("user/block/")
    Call<ResponseBody> getBlockedUsers(
            @HeaderMap Map<String, String> headers
    );
    @GET("message/chat/userblocklist/")
    Call<ResponseBody> getMessageBlockedUsers(
            @HeaderMap Map<String, String> headers
    );

    @POST("user/unblock/")
    Call<ResponseBody> postUnblockUser(
            @Body JsonObject unblockUser,
            @HeaderMap Map<String, String> headers
    );
    //endregion

    //region Shop

    //Get Shop items
    @GET("shop/")
    Call<ResponseBody> getShopItemDetails(
            @HeaderMap Map<String, String> headers
    );

    //load more shop items and sort shop
    @POST("shop/")
    Call<ResponseBody> getMoreShopItems(
            @Body JsonObject jsonObject,
            @HeaderMap Map<String, String> headers
    );

    //Get shop item detail
    @POST("shop/detail")
    Call<ResponseBody> getShopProductDetail(
            @Body JsonObject jsonObject,
            @HeaderMap Map<String, String> headers
    );

    //Get shop item reviews
    @POST("shop/get_review")
    Call<ResponseBody> getShopItemReviews(
            @Body JsonObject jsonObject,
            @HeaderMap Map<String, String> headers
    );

    //Post review on shop item
    @POST("shop/submit_review")
    Call<ResponseBody> postShopItemReviews(
            @Body JsonObject jsonObject,
            @HeaderMap Map<String, String> headers
    );

    //Get Cart
    @GET("cart/")
    Call<ResponseBody> getCartList(
            @HeaderMap Map<String, String> headers
    );

    //Remove item from Cart
    @POST("cart/remove_item")
    Call<ResponseBody> postRemoveCartItem(
            @Body JsonObject jsonObject,
            @HeaderMap Map<String, String> headers
    );

    //Add item to cart
    @POST("cart/add")
    Call<ResponseBody> postAdToCart(
            @Body JsonObject jsonObject,
            @HeaderMap Map<String, String> headers
    );
    //endregion

    //region sellers
    @GET("sellers/")
    Call<ResponseBody> getSellersList(
            @HeaderMap Map<String, String> headers
    );

    //Get more Sellers
    @POST("sellers/")
    Call<ResponseBody> getMoreSellersList(
            @Body JsonObject jsonObject,
            @HeaderMap Map<String, String> headers
    );
    //endregion
}
