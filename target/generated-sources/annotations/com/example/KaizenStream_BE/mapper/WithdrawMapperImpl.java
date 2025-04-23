package com.example.KaizenStream_BE.mapper;

import com.example.KaizenStream_BE.dto.respone.withdraw.WithdrawResponse;
import com.example.KaizenStream_BE.entity.User;
import com.example.KaizenStream_BE.entity.Withdraw;
import javax.annotation.processing.Generated;
import org.springframework.stereotype.Component;

@Generated(
    value = "org.mapstruct.ap.MappingProcessor",
    comments = "version: 1.5.5.Final, compiler: javac, environment: Java 21.0.4 (Oracle Corporation)"
)
@Component
public class WithdrawMapperImpl implements WithdrawMapper {

    @Override
    public WithdrawResponse toDto(Withdraw withdraw) {
        if ( withdraw == null ) {
            return null;
        }

        WithdrawResponse.WithdrawResponseBuilder withdrawResponse = WithdrawResponse.builder();

        withdrawResponse.userId( withdrawUserUserId( withdraw ) );
        withdrawResponse.withdrawId( withdraw.getWithdrawId() );
        withdrawResponse.pointsRequested( withdraw.getPointsRequested() );
        withdrawResponse.usdExpected( withdraw.getUsdExpected() );
        withdrawResponse.bankName( withdraw.getBankName() );
        withdrawResponse.bankAccount( withdraw.getBankAccount() );
        withdrawResponse.bankHolder( withdraw.getBankHolder() );
        withdrawResponse.status( withdraw.getStatus() );
        withdrawResponse.note( withdraw.getNote() );
        withdrawResponse.createdAt( withdraw.getCreatedAt() );
        withdrawResponse.updatedAt( withdraw.getUpdatedAt() );

        return withdrawResponse.build();
    }

    private String withdrawUserUserId(Withdraw withdraw) {
        if ( withdraw == null ) {
            return null;
        }
        User user = withdraw.getUser();
        if ( user == null ) {
            return null;
        }
        String userId = user.getUserId();
        if ( userId == null ) {
            return null;
        }
        return userId;
    }
}
