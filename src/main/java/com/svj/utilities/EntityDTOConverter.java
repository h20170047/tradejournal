package com.svj.utilities;

import com.svj.entity.TradeEntry;
import com.svj.dto.TradeEntryRequestDTO;
import com.svj.dto.TradeEntryResponseDTO;

public class EntityDTOConverter {

    public static TradeEntry convertDTOToEntity(TradeEntryRequestDTO requestDTO){
        TradeEntry tradeEntry = new TradeEntry();
        copyReqToEntity(requestDTO, tradeEntry);
        return tradeEntry;
    }

    public static void copyReqToEntity(TradeEntryRequestDTO requestDTO, TradeEntry tradeEntry) {
        tradeEntry.setSymbol(requestDTO.getSymbol());
        tradeEntry.setBuy(requestDTO.getBuy());
        tradeEntry.setSell(requestDTO.getSell());
        tradeEntry.setStopLoss(requestDTO.getStopLoss());
        tradeEntry.setTarget(requestDTO.getTarget());
        tradeEntry.setProfit(requestDTO.getProfit());
        tradeEntry.setBuyPrice(requestDTO.getBuyPrice());
        tradeEntry.setSellPrice(requestDTO.getSellPrice());
        tradeEntry.setComments(requestDTO.getComments());
    }

    public static TradeEntryResponseDTO entityToDTO(TradeEntry entry){
        TradeEntryResponseDTO tradeEntryResponseDTO = new TradeEntryResponseDTO();
        tradeEntryResponseDTO.setId(entry.getId());
        tradeEntryResponseDTO.setSymbol(entry.getSymbol());
        tradeEntryResponseDTO.setBuy(entry.getBuy());
        tradeEntryResponseDTO.setSell(entry.getSell());
        tradeEntryResponseDTO.setStopLoss(entry.getStopLoss());
        tradeEntryResponseDTO.setTarget(entry.getTarget());
        tradeEntryResponseDTO.setProfit(entry.getProfit());
        tradeEntryResponseDTO.setBuyPrice(entry.getBuyPrice());
        tradeEntryResponseDTO.setSellPrice(entry.getSellPrice());
        tradeEntryResponseDTO.setComments(entry.getComments());
        return tradeEntryResponseDTO;
    }
}
