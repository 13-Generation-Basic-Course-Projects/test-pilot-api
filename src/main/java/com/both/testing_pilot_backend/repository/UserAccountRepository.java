package com.both.testing_pilot_backend.repository;

import com.both.testing_pilot_backend.model.UserAccount;
import org.apache.ibatis.annotations.*;

import java.util.UUID;

@Mapper
public interface UserAccountRepository {
    @Results(id = "userAccountMapper", value = {
            @Result(property = "userAccountId", column = "id"),
            @Result(property = "providerId", column = "provider_id"),
    })
    @Select("""
            SELECT * FROM user_accounts
            WHERE provider_id = #{providerId}
            AND provider = #{provider}
            """)
    UserAccount findByProviderNameAndProviderId(String provider, String providerId);

    @Insert("""
            INSERT INTO user_accounts (user_id, provider, provider_id)
            VALUES (#{userId}, #{userAccount.provider}, #{userAccount.providerId})
    """)
    void saveUserAccount(UserAccount userAccount, UUID userId);
}
