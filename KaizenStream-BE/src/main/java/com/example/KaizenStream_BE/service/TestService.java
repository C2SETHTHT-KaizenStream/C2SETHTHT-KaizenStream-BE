package com.example.KaizenStream_BE.service;


import com.example.KaizenStream_BE.dto.request.test.TestRequest;
import com.example.KaizenStream_BE.entity.Test;
import com.example.KaizenStream_BE.mapper.TestMapper;
import com.example.KaizenStream_BE.repository.TestRepository;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE,makeFinal = true)
@Slf4j
public class TestService {

    TestRepository testRepository;
    TestMapper testMapper;

    public Test createTest(TestRequest testRequest){
        if(testRepository.existsByName(testRequest.getName())){
            System.out.println("existed");
        }
        Test test=testMapper.toTest(testRequest);
        return testRepository.save(test);

    }

}
