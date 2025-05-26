package com.both.testing_pilot_backend.repository;

import com.both.testing_pilot_backend.dto.request.PublicShareLinkRequest;
import com.both.testing_pilot_backend.model.PublicShareLink;
import org.apache.ibatis.annotations.*;

import java.util.List;
import java.util.UUID;

@Mapper
public interface PublicShareLinkRepository {

    @Results(id = "ShareLinkMapper", value = {
            @Result(property = "shareLinkId", column = "share_link_id"),
            @Result(property = "token", column = "token"),
            @Result(property = "sharedItemType", column = "shared_item_type"),
            @Result(property = "sharedItemId", column = "shared_item_id"),
            @Result(property = "expireAt", column = "expire_at"),
            @Result(property = "createdByUserId", column = "created_by_user_id"),
            @Result(property = "createdAt", column = "created_at"),
            @Result(property = "updatedAt", column = "updated_at")})
    @Select("SELECT * FROM public_share_link")
    List<PublicShareLink> getAllPublicShareLinks();

    @Select("SELECT * FROM public_share_link WHERE share_link_id = #{id}")
    PublicShareLink getPublicShareLinkById(@Param("id") UUID id);

    @Select("""
            INSERT INTO public_share_link (token, shared_item_type, shared_item_id, expire_at, created_by_user_id)
            VALUES (#{req.token}, #{req.sharedItemType}, #{req.sharedItemId}, #{req.expireAt}, #{req.createdByUserId})
            RETURNING *;
            """)
    PublicShareLink createPublicShareLink(@Param("req") PublicShareLinkRequest request);

    @Select("""
            UPDATE public_share_link SET
                token = #{req.token},
                shared_item_type = #{req.sharedItemType},
                shared_item_id = #{req.sharedItemId}
            WHERE share_link_id = #{req.shareLinkId}
            RETURNING *;
            
            """)
    PublicShareLink updatePublicShareLink(@Param("id") UUID id,@Param("req") PublicShareLinkRequest request);

    @Select("DELETE FROM public_share_link WHERE share_link_id = #{id}, RETURNING *;")
    PublicShareLink deletePublicShareLink(@Param("id") UUID id);
}