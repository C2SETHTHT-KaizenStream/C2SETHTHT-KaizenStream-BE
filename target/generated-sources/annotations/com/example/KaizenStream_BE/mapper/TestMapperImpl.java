package com.example.KaizenStream_BE.mapper;

import com.example.KaizenStream_BE.dto.request.test.TestRequest;
import com.example.KaizenStream_BE.entity.Test;
import javax.annotation.processing.Generated;
import org.springframework.stereotype.Component;

@Generated(
    value = "org.mapstruct.ap.MappingProcessor",
    comments = "version: 1.5.5.Final, compiler: javac, environment: Java 21.0.4 (Oracle Corporation)"
)
@Component
public class TestMapperImpl implements TestMapper {

    @Override
    public Test toTest(TestRequest test) {
        if ( test == null ) {
            return null;
        }

        Test test1 = new Test();

        test1.setName( test.getName() );
        test1.setDes( test.getDes() );

        return test1;
    }
}
