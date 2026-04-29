package com.project.kisan_setu.service;
import com.project.kisan_setu.dto.ResponseDto.MyBiddingsResponseDto;
import java.util.List;

public interface BidService {
     List<MyBiddingsResponseDto> getAllMyBids();
     List<MyBiddingsResponseDto> getMyPendingBids();
     List<MyBiddingsResponseDto> getMyAcceptedBids();
     List<MyBiddingsResponseDto> getMyRejectedBids();
     List<MyBiddingsResponseDto> getMyOutbidBids();
}
