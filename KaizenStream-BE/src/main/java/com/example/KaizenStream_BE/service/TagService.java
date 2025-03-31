package com.example.KaizenStream_BE.service;

import com.example.KaizenStream_BE.dto.request.tag.CreateTagRequest;
import com.example.KaizenStream_BE.dto.respone.tag.TagRespone;
import com.example.KaizenStream_BE.entity.Tag;
import com.example.KaizenStream_BE.mapper.TagMapper;
import com.example.KaizenStream_BE.repository.TagRepository;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE,makeFinal = true)
@Slf4j
public class TagService {
    TagRepository tagRepository;
    TagMapper tagMapper;

    public TagRespone createTag(CreateTagRequest request) {
        Tag tag=tagMapper.toTag(request);
        tag.setName(request.getName());
        tagRepository.save(tag);
        return  tagMapper.toTagRespone(tag);

    }

    public List<TagRespone> getAll() {
        return  tagRepository.findAll().stream().map(t->tagMapper.toTagRespone(t)).toList();
    }
}
