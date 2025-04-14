package com.example.KaizenStream_BE.mapper;

import com.example.KaizenStream_BE.dto.respone.suggestion.SuggestionResponse;
import com.example.KaizenStream_BE.entity.Livestream;
import com.example.KaizenStream_BE.entity.User;
import javax.annotation.processing.Generated;
import org.springframework.stereotype.Component;

@Generated(
    value = "org.mapstruct.ap.MappingProcessor",
    comments = "version: 1.5.5.Final, compiler: javac, environment: Java 21.0.4 (Oracle Corporation)"
)
@Component
public class SuggestionMapperImpl implements SuggestionMapper {

    @Override
    public SuggestionResponse toSuggestionResponse(Livestream livestream) {
        if ( livestream == null ) {
            return null;
        }

        SuggestionResponse.SuggestionResponseBuilder suggestionResponse = SuggestionResponse.builder();

        suggestionResponse.streamerId( livestreamUserUserId( livestream ) );
        suggestionResponse.streamerName( livestreamUserUserName( livestream ) );
        suggestionResponse.thumbnailUrl( livestream.getThumbnail() );
        suggestionResponse.viewerCount( livestream.getViewerCount() );
        suggestionResponse.livestreamId( livestream.getLivestreamId() );
        suggestionResponse.title( livestream.getTitle() );
        suggestionResponse.description( livestream.getDescription() );

        suggestionResponse.startTime( livestream.getStartTime().toInstant().atZone(java.time.ZoneId.systemDefault()).toLocalDateTime() );
        suggestionResponse.tags( convertTagsToStringSet(livestream.getTags().stream().map(t -> t.getName()).collect(java.util.stream.Collectors.toSet())) );
        suggestionResponse.categories( convertCategoriesToStringSet(livestream.getCategories().stream().map(c -> c.getName()).collect(java.util.stream.Collectors.toSet())) );
        suggestionResponse.endTime( livestream.getEndTime() != null ? livestream.getEndTime().toInstant().atZone(java.time.ZoneId.systemDefault()).toLocalDateTime() : null );

        return suggestionResponse.build();
    }

    private String livestreamUserUserId(Livestream livestream) {
        if ( livestream == null ) {
            return null;
        }
        User user = livestream.getUser();
        if ( user == null ) {
            return null;
        }
        String userId = user.getUserId();
        if ( userId == null ) {
            return null;
        }
        return userId;
    }

    private String livestreamUserUserName(Livestream livestream) {
        if ( livestream == null ) {
            return null;
        }
        User user = livestream.getUser();
        if ( user == null ) {
            return null;
        }
        String userName = user.getUserName();
        if ( userName == null ) {
            return null;
        }
        return userName;
    }
}
