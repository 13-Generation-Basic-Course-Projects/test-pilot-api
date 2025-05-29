package com.both.testing_pilot_backend.repository;

import com.both.testing_pilot_backend.dto.request.PublicShareLinkItemRequest;
import com.both.testing_pilot_backend.model.PublicShareLinkItem;
import org.apache.ibatis.annotations.*;

import java.util.List;
import java.util.UUID;

@Mapper
public interface PublicShareLinkItemRepository {
    @Results(id = "ShareLinkItemMapper", value = {
            @Result(property = "shareLinkItemId", column = "share_link_item_id"),
            @Result(property = "itemType", column = "item_type"),
            @Result(property = "itemId", column = "item_id"),
            @Result(property = "shareLinkId", column = "share_link_id", one = @One(select =  "com.both.testing_pilot_backend.repository.PublicShareLinkRepository.getPublicShareLinkById")),
            @Result(property = "createdAt", column = "created_at"),
            @Result(property = "updatedAt", column = "updated_at")})
    @Select("""
            SELECT * from public_share_link_item;
            """)
    List<PublicShareLinkItem> getAllPublicShareLinkItems();


    @ResultMap("ShareLinkItemMapper")
    @Select("""
            SELECT * from public_share_link_item WHERE share_link_item_id = {id};
            """)
    PublicShareLinkItem getPublicShareLinkItemById(@Param("id") UUID id);

    @ResultMap("ShareLinkItemMapper")
    @Select("""
            INSERT INTO public_share_link_item (item_type, item_id, share_link_id, created_at, updated_at)
            VALUES (#{req.itemType}, #{req.itemId}, #{req.shareLinkId}, NOW(), NOW())
            RETURNING *;
            """)
    PublicShareLinkItem createPublicShareLinkItem(@Param("req") PublicShareLinkItemRequest request);

    @ResultMap("ShareLinkItemMapper")
    @Select("""
            UPDATE public_share_link_item SET
            item_type = #{req.itemType},
            item_id = #{req.itemId},
            share_link_id = #{req.shareLinkId},
            updated_at = NOW()
            WHERE share_link_item_id = #{id}
            RETURNING *;
            """)
    PublicShareLinkItem updatePublicShareLinkItem(@Param("id") UUID id,@Param("req") PublicShareLinkItemRequest request);

    @ResultMap("ShareLinkItemMapper")
    @Select(""" 
            DELETE FROM public_share_link_item WHERE share_link_item_id = #{id}
            RETURNING *;
            """)
    PublicShareLinkItem deletePublicShareLinkItemById(@Param("id") UUID id);
}
