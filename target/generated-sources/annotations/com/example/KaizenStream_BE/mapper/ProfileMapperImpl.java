package com.example.KaizenStream_BE.mapper;

import com.example.KaizenStream_BE.dto.request.profile.CreateProfileRequest;
import com.example.KaizenStream_BE.dto.request.profile.UpdateProfileRequest;
import com.example.KaizenStream_BE.dto.respone.profile.ProfileResponse;
import com.example.KaizenStream_BE.entity.Profile;
import javax.annotation.processing.Generated;
import org.springframework.stereotype.Component;

@Generated(
    value = "org.mapstruct.ap.MappingProcessor",
    comments = "version: 1.5.5.Final, compiler: javac, environment: Java 21.0.4 (Oracle Corporation)"
)
@Component
public class ProfileMapperImpl implements ProfileMapper {

    @Override
    public ProfileResponse toProfileRespone(Profile profile) {
        if ( profile == null ) {
            return null;
        }

        ProfileResponse.ProfileResponseBuilder profileResponse = ProfileResponse.builder();

        profileResponse.profileId( profile.getProfileId() );
        profileResponse.fullName( profile.getFullName() );
        profileResponse.phoneNumber( profile.getPhoneNumber() );
        profileResponse.address( profile.getAddress() );
        profileResponse.bio( profile.getBio() );
        profileResponse.avatarUrl( profile.getAvatarUrl() );
        profileResponse.gender( profile.getGender() );
        profileResponse.dateOfBirth( profile.getDateOfBirth() );
        profileResponse.bankAccountNumber( profile.getBankAccountNumber() );
        profileResponse.bankName( profile.getBankName() );
        profileResponse.description( profile.getDescription() );

        return profileResponse.build();
    }

    @Override
    public Profile toProfile(CreateProfileRequest request) {
        if ( request == null ) {
            return null;
        }

        Profile.ProfileBuilder profile = Profile.builder();

        profile.fullName( request.getFullName() );
        profile.phoneNumber( request.getPhoneNumber() );
        profile.address( request.getAddress() );
        profile.bio( request.getBio() );
        profile.avatarUrl( request.getAvatarUrl() );
        profile.gender( request.getGender() );
        profile.dateOfBirth( request.getDateOfBirth() );
        profile.bankAccountNumber( request.getBankAccountNumber() );
        profile.bankName( request.getBankName() );
        profile.description( request.getDescription() );

        return profile.build();
    }

    @Override
    public void updateProfile(Profile profile, UpdateProfileRequest request) {
        if ( request == null ) {
            return;
        }

        if ( request.getFullName() != null ) {
            profile.setFullName( request.getFullName() );
        }
        if ( request.getPhoneNumber() != null ) {
            profile.setPhoneNumber( request.getPhoneNumber() );
        }
        if ( request.getAddress() != null ) {
            profile.setAddress( request.getAddress() );
        }
        if ( request.getBio() != null ) {
            profile.setBio( request.getBio() );
        }
        if ( request.getGender() != null ) {
            profile.setGender( request.getGender() );
        }
        if ( request.getDateOfBirth() != null ) {
            profile.setDateOfBirth( request.getDateOfBirth() );
        }
        if ( request.getBankAccountNumber() != null ) {
            profile.setBankAccountNumber( request.getBankAccountNumber() );
        }
        if ( request.getBankName() != null ) {
            profile.setBankName( request.getBankName() );
        }
        if ( request.getDescription() != null ) {
            profile.setDescription( request.getDescription() );
        }
    }
}
