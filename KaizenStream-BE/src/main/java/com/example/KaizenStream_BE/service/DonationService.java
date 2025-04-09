package com.example.KaizenStream_BE.service;

import com.example.KaizenStream_BE.dto.request.donation.DonationRequest;
import com.example.KaizenStream_BE.dto.respone.donation.DonationNotification;
import com.example.KaizenStream_BE.dto.respone.donation.ViewerNotification;
import com.example.KaizenStream_BE.entity.*;
import com.example.KaizenStream_BE.enums.ErrorCode;
import com.example.KaizenStream_BE.exception.AppException;
import com.example.KaizenStream_BE.repository.*;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.transaction.Transactional;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Slf4j
@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class DonationService {
    UserRepository userRepository;
    ItemRepository itemRepository;
    LivestreamRepository livestreamRepository;
    WalletRepository walletRepository;
    DonationRepository donationRepository;
    SimpMessagingTemplate messagingTemplate;


    @Transactional
    public void donate(DonationRequest requestDTO){
        String userId = requestDTO.getUserId();
        User sender = userRepository.findById(userId).orElseThrow(()-> new AppException(ErrorCode.USER_NOT_EXIST));
        Livestream receiver = livestreamRepository.findById(requestDTO.getLivestreamId()).orElseThrow(()-> new AppException(ErrorCode.LIVESTREAM_NOT_EXIST));
        Item item = itemRepository.findById(requestDTO.getItemId()).orElseThrow(()-> new AppException(ErrorCode.ITEM_NOT_EXIST));
        String streamerId = receiver.getUser().getUserId();


        int totalPrice = Integer.parseInt(item.getPrice()) * requestDTO.getAmount();

        Wallet senderWallet = walletRepository.findByUser(sender).orElseThrow(()-> new AppException(ErrorCode.WALLET_NOT_EXIST));
        Wallet receiverWallet = walletRepository.findByUser(receiver.getUser()).orElseThrow(()-> new AppException(ErrorCode.WALLET_NOT_EXIST));
        if(senderWallet.getBalance() < totalPrice){
            throw new AppException(ErrorCode.INSUFFICIENT_BALANCE);
        }

        //Trừ tiền người gửi
        senderWallet.setBalance(senderWallet.getBalance() - totalPrice);
        walletRepository.save(senderWallet);
        //Cộng tiền cho streamer
        receiverWallet.setBalance(receiverWallet.getBalance() + totalPrice);
        walletRepository.save(receiverWallet);

        // Tạo thông báo hiệu ứng đến streamer
        DonationNotification notification = new DonationNotification(
                sender.getUserName(),
                item.getName(),
                requestDTO.getAmount()
        );
        // Tạo thông báo hiệu ứng đến Viewer khác
        ViewerNotification viewerNotification = new ViewerNotification(
                streamerId,
                sender.getUserId(),
                sender.getUserName(),
                item.getName(),
                item.getImage(),
                requestDTO.getAmount()
        );

        // Gửi thông báo đến streamer
        messagingTemplate.convertAndSend("/queue/donate/"+streamerId, notification);
        //Gửi thông báo viewer khác
        messagingTemplate.convertAndSend("/queue/donate/"+receiver.getLivestreamId(),viewerNotification);

        Donation donation = new Donation();
        donation.setUser(sender); // Người gửi quà
        donation.setItem(item); // Món quà được tặng
        donation.setLivestream(receiver); // Buổi livestream mà quà được gửi
        donation.setQuantityItems(requestDTO.getAmount()); // Số lượng quà tặng
        donation.setPointSpent(totalPrice); // Số điểm đã tiêu
        donation.setTimestamp(LocalDateTime.now()); // Thời gian tặng quà

        donationRepository.save(donation);
    }
}
