package com.svj.utilities;

import com.svj.dto.PreferenceRequestDTO;
import com.svj.dto.PreferenceResponseDTO;
import com.svj.entity.TradeEntry;
import com.svj.dto.TradeEntryRequestDTO;
import com.svj.dto.TradeEntryResponseDTO;
import com.svj.entity.TraderPreference;

public class EntityDTOConverter {

    public static TradeEntry convertDTOToEntity(TradeEntryRequestDTO requestDTO){
        TradeEntry tradeEntry = new TradeEntry();
        copyReqToEntity(requestDTO, tradeEntry);
        return tradeEntry;

    }

    public static void copyReqToEntity(TradeEntryRequestDTO requestDTO, TradeEntry tradeEntry) {
        tradeEntry.setTraderName(requestDTO.getTraderName());
        tradeEntry.setPosition(requestDTO.getPosition());
        tradeEntry.setProduct(requestDTO.getProduct());
        tradeEntry.setCapital(requestDTO.getCapital());
        tradeEntry.setSymbol(requestDTO.getSymbol());
        tradeEntry.setQuantity(requestDTO.getQuantity());
        tradeEntry.setEntryDate(requestDTO.getEntryDate());
        tradeEntry.setExitDate(requestDTO.getExitDate());
        tradeEntry.setEntryPrice(requestDTO.getEntryPrice());
        tradeEntry.setSL(requestDTO.getSL());
        tradeEntry.setSLPercent(getSLPercent(requestDTO));
        tradeEntry.setRiskPercent(getRiskPercent(requestDTO));
        tradeEntry.setRewardRiskRatio(getRewardRiskRatio(requestDTO));
        tradeEntry.setT1(requestDTO.getT1());
        tradeEntry.setT1Percent(getT1Percent(requestDTO));
        tradeEntry.setT2(requestDTO.getT2());
        tradeEntry.setT2Percent(getT2Percent(requestDTO));
        tradeEntry.setExitPrice(requestDTO.getExitPrice());
        tradeEntry.setProfit(calculateProfit(requestDTO));
        tradeEntry.setProfitPercent(getProfitPercent(tradeEntry));
        tradeEntry.setEntryComments(requestDTO.getEntryComments());
        tradeEntry.setExitComments(requestDTO.getExitComments());
        tradeEntry.setRemarks(requestDTO.getRemarks());
    }

    private static double getRewardRiskRatio(TradeEntryRequestDTO requestDTO) {
        return Math.abs(requestDTO.getEntryPrice()- requestDTO.getT1())/(Math.abs(requestDTO.getEntryPrice()- requestDTO.getSL()))*100;
    }

    // Profit depends on position chosen
    private static Double calculateProfit(TradeEntryRequestDTO requestDTO) {
        if(requestDTO.getExitPrice()!= null){
            if("LONG".equals(requestDTO.getPosition().toString()))
                return requestDTO.getExitPrice()- requestDTO.getEntryPrice();
            else
                return requestDTO.getEntryPrice()- requestDTO.getExitPrice();
        }
        return null;
    }

    private static Double getProfitPercent(TradeEntry requestDTO) {
        if(requestDTO.getProfit()!= null){
            return requestDTO.getProfit()/requestDTO.getEntryPrice()*100;
        }
        return null;
    }

    private static Double getT2Percent(TradeEntryRequestDTO requestDTO) {
        if(requestDTO.getT2()== null)
            return null;
        else
            return Math.abs(requestDTO.getT2()-requestDTO.getT1())/requestDTO.getT1()*100;
    }

    private static double getT1Percent(TradeEntryRequestDTO requestDTO) {
        return Math.abs(requestDTO.getT1()- requestDTO.getEntryPrice())/requestDTO.getEntryPrice() *100;
    }

    private static double getRiskPercent(TradeEntryRequestDTO requestDTO) {
        return 100*requestDTO.getQuantity()*Math.abs(requestDTO.getSL()-requestDTO.getEntryPrice())/requestDTO.getCapital();
    }

    private static double getSLPercent(TradeEntryRequestDTO requestDTO) {
        return Math.abs(requestDTO.getSL()- requestDTO.getEntryPrice())/requestDTO.getEntryPrice() *100;
    }

    public static TradeEntryResponseDTO entityToDTO(TradeEntry entry){
        TradeEntryResponseDTO tradeEntryResponseDTO = new TradeEntryResponseDTO();
        tradeEntryResponseDTO.setId(entry.getId());
        tradeEntryResponseDTO.setProduct(entry.getProduct());
        tradeEntryResponseDTO.setPosition(entry.getPosition());
        tradeEntryResponseDTO.setTraderName(entry.getTraderName());
        tradeEntryResponseDTO.setCapital(entry.getCapital());
        tradeEntryResponseDTO.setSymbol(entry.getSymbol());
        tradeEntryResponseDTO.setQuantity(entry.getQuantity());
        tradeEntryResponseDTO.setEntryDate(entry.getEntryDate());
        tradeEntryResponseDTO.setExitDate(entry.getExitDate());
        tradeEntryResponseDTO.setEntryPrice(entry.getEntryPrice());
        tradeEntryResponseDTO.setSL(entry.getSL());
        tradeEntryResponseDTO.setSLPercent(entry.getSLPercent());
        tradeEntryResponseDTO.setRiskPercent(entry.getRiskPercent());
        tradeEntryResponseDTO.setRewardRiskRatio(entry.getRewardRiskRatio());
        tradeEntryResponseDTO.setT1(entry.getT1());
        tradeEntryResponseDTO.setT1Percent(entry.getT1Percent());
        tradeEntryResponseDTO.setT2(entry.getT2());
        tradeEntryResponseDTO.setT2Percent(entry.getT2Percent());
        tradeEntryResponseDTO.setExitPrice(entry.getExitPrice());
        tradeEntryResponseDTO.setProfit(entry.getProfit());
        tradeEntryResponseDTO.setProfitPercent(entry.getProfitPercent());
        tradeEntryResponseDTO.setEntryComments(entry.getEntryComments());
        tradeEntryResponseDTO.setExitComments(entry.getExitComments());
        tradeEntryResponseDTO.setRemarks(entry.getRemarks());
        return tradeEntryResponseDTO;
    }

    public static PreferenceResponseDTO entityToDTO(TraderPreference entry){
        PreferenceResponseDTO preferenceResponseDTO = PreferenceResponseDTO.builder()
        		.id(entry.getId())
        		.traderName(entry.getTraderName())
        		.capital(entry.getCapital())
        		.position(entry.getPosition())
        		.product(entry.getProduct())
        		.build();
        return preferenceResponseDTO;
    }

    public static TraderPreference convertDTOToEntity(PreferenceRequestDTO requestDTO){
        TraderPreference traderPreference = new TraderPreference();
        copyReqToEntity(requestDTO, traderPreference);
        return traderPreference;
    }

    public static void copyReqToEntity(PreferenceRequestDTO requestDTO, TraderPreference traderPreference) {
        traderPreference.setTraderName(requestDTO.getTraderName());
        traderPreference.setCapital(requestDTO.getCapital());
        traderPreference.setProduct(String.valueOf(requestDTO.getProduct()));
        traderPreference.setPosition(String.valueOf(requestDTO.getPosition()));
    }
}
