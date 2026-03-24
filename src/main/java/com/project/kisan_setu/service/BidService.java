package com.project.kisan_setu.service;

import com.project.kisan_setu.dto.ResponseDto.MyBiddingsResponseDto;
import com.project.kisan_setu.entity.Bid;

import java.util.List;

public interface BidService {
    public List<MyBiddingsResponseDto> getAllMyBids();
    public List<MyBiddingsResponseDto> getMyPendingBids();
    public List<MyBiddingsResponseDto> getMyAcceptedBids();
    public List<MyBiddingsResponseDto> getMyRejectedBids();
    public List<MyBiddingsResponseDto> getMyOutbidBids();
}
