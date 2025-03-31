package com.example.KaizenStream_BE.mapper;

import com.example.KaizenStream_BE.dto.request.tag.CreateTagRequest;
import com.example.KaizenStream_BE.dto.respone.tag.TagRespone;
import com.example.KaizenStream_BE.entity.Tag;
import javax.annotation.processing.Generated;
import org.springframework.stereotype.Component;

@Generated(
    value = "org.mapstruct.ap.MappingProcessor",
    comments = "version: 1.5.5.Final, compiler: javac, environment: Java 21.0.4 (Oracle Corporation)"
)
@Component
public class TagMapperImpl implements TagMapper {

    @Override
    public TagRespone toTagRespone(Tag tag) {
        if ( tag == null ) {
            return null;
        }

        TagRespone.TagResponeBuilder tagRespone = TagRespone.builder();

        tagRespone.tagId( tag.getTagId() );
        tagRespone.name( tag.getName() );

        return tagRespone.build();
    }

    @Override
    public Tag toTag(CreateTagRequest tagRequest) {
        if ( tagRequest == null ) {
            return null;
        }

        Tag tag = new Tag();

        tag.setName( tagRequest.getName() );

        return tag;
    }
}
