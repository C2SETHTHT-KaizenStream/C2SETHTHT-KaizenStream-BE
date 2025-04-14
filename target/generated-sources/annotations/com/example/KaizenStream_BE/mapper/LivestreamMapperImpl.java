package com.example.KaizenStream_BE.mapper;

import com.example.KaizenStream_BE.dto.request.livestream.CreateLivestreamRequest;
import com.example.KaizenStream_BE.dto.request.livestream.UpdateLivestreamRequest;
import com.example.KaizenStream_BE.dto.respone.livestream.LivestreamRespone;
import com.example.KaizenStream_BE.entity.Livestream;
import javax.annotation.processing.Generated;
import org.springframework.stereotype.Component;

@Generated(
    value = "org.mapstruct.ap.MappingProcessor",
    comments = "version: 1.5.5.Final, compiler: javac, environment: Java 21.0.4 (Oracle Corporation)"
)
@Component
public class LivestreamMapperImpl implements LivestreamMapper {

    @Override
    public LivestreamRespone toLivestreamRespone(Livestream livestream) {
        if ( livestream == null ) {
            return null;
        }

        LivestreamRespone.LivestreamResponeBuilder livestreamRespone = LivestreamRespone.builder();

        livestreamRespone.livestreamId( livestream.getLivestreamId() );
        livestreamRespone.title( livestream.getTitle() );
        livestreamRespone.description( livestream.getDescription() );
        livestreamRespone.thumbnail( livestream.getThumbnail() );
        livestreamRespone.startTime( livestream.getStartTime() );
        livestreamRespone.viewerCount( livestream.getViewerCount() );
        livestreamRespone.endTime( livestream.getEndTime() );
        livestreamRespone.status( livestream.getStatus() );
        livestreamRespone.duration( livestream.getDuration() );

        return livestreamRespone.build();
    }

    @Override
    public Livestream toLivestream(CreateLivestreamRequest livestream) {
        if ( livestream == null ) {
            return null;
        }

        Livestream livestream1 = new Livestream();

        livestream1.setTitle( livestream.getTitle() );
        livestream1.setDescription( livestream.getDescription() );
        livestream1.setThumbnail( livestream.getThumbnail() );
        livestream1.setViewerCount( livestream.getViewerCount() );
        livestream1.setStartTime( livestream.getStartTime() );
        livestream1.setEndTime( livestream.getEndTime() );
        livestream1.setStatus( livestream.getStatus() );

        return livestream1;
    }

    @Override
    public void updateLivestream(Livestream livestream, UpdateLivestreamRequest request) {
        if ( request == null ) {
            return;
        }

        livestream.setLivestreamId( request.getLivestreamId() );
        livestream.setTitle( request.getTitle() );
        livestream.setDescription( request.getDescription() );
        livestream.setThumbnail( request.getThumbnail() );
        livestream.setViewerCount( request.getViewerCount() );
        livestream.setStartTime( request.getStartTime() );
        livestream.setEndTime( request.getEndTime() );
        livestream.setStatus( request.getStatus() );
    }
}
