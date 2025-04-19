package com.example.KaizenStream_BE.mapper;

import com.example.KaizenStream_BE.dto.request.authen.RegisterRequest;
import com.example.KaizenStream_BE.dto.respone.channel.ChannelResponse;
import com.example.KaizenStream_BE.entity.User;
import javax.annotation.processing.Generated;
import org.springframework.stereotype.Component;

@Generated(
    value = "org.mapstruct.ap.MappingProcessor",
    comments = "version: 1.5.5.Final, compiler: javac, environment: Java 21.0.4 (Oracle Corporation)"
)
@Component
public class UserMapperImpl implements UserMapper {

    @Override
    public User toUser(RegisterRequest registerRequest) {
        if ( registerRequest == null ) {
            return null;
        }

        User.UserBuilder user = User.builder();

        user.userName( registerRequest.getUserName() );
        user.password( registerRequest.getPassword() );
        user.email( registerRequest.getEmail() );

        return user.build();
    }

    @Override
    public ChannelResponse toChannelResponse(User user) {
        if ( user == null ) {
            return null;
        }

        ChannelResponse.ChannelResponseBuilder channelResponse = ChannelResponse.builder();

        channelResponse.userId( user.getUserId() );
        channelResponse.userName( user.getUserName() );
        channelResponse.channelName( user.getChannelName() );

        return channelResponse.build();
    }
}
