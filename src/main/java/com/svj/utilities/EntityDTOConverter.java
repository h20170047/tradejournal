package com.svj.utilities;

import com.svj.dto.PreferenceRequestDTO;
import com.svj.dto.PreferenceResponseDTO;
import com.svj.dto.TradeEntryRequestDTO;
import com.svj.dto.TradeEntryResponseDTO;
import com.svj.entity.TradeEntry;
import com.svj.entity.TraderPreference;

public class EntityDTOConverter {

    public static com.svj.entity.TradeEntry convertDTOToEntity(TradeEntryRequestDTO requestDTO){
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
        tradeEntry.setT1(requestDTO.getT1());
        tradeEntry.setT2(requestDTO.getT2());
        tradeEntry.setExitPrice(requestDTO.getExitPrice());
        tradeEntry.setEntryComments(requestDTO.getEntryComments());
        tradeEntry.setExitComments(requestDTO.getExitComments());
        tradeEntry.setRemarks(requestDTO.getRemarks());
        computeDerivedValues(tradeEntry);
    }

    protected static void computeDerivedValues(TradeEntry tradeEntry) {
        tradeEntry.setSLPercent(getSLPercent(tradeEntry));
        tradeEntry.setRiskPercent(getRiskPercent(tradeEntry));
        if(tradeEntry.getRewardRiskRatio()== null)
            tradeEntry.setRewardRiskRatio(getRewardRiskRatio(tradeEntry));
        tradeEntry.setT1Percent(getT1Percent(tradeEntry));
        tradeEntry.setT2Percent(getT2Percent(tradeEntry));
        if(tradeEntry.getProfit()== null)
            tradeEntry.setProfit(calculateProfit(tradeEntry));
        if(tradeEntry.getProfitPercent()== null)
            tradeEntry.setProfitPercent(getProfitPercent(tradeEntry));
    }

    private static double getRewardRiskRatio(com.svj.entity.TradeEntry tradeEntry) {
        return Math.abs(tradeEntry.getEntryPrice()- tradeEntry.getT1())/(Math.abs(tradeEntry.getEntryPrice()- tradeEntry.getSL()));
    }

    // Profit depends on position chosen
    private static Double calculateProfit(TradeEntry tradeEntry) {
        if(tradeEntry.getExitPrice()!= null){
            if("LONG".equals(tradeEntry.getPosition()))
                return tradeEntry.getExitPrice()- tradeEntry.getEntryPrice();
            else
                return tradeEntry.getEntryPrice()- tradeEntry.getExitPrice();
        }
        return null;
    }

    private static Double getProfitPercent(TradeEntry tradeEntry) {
        if(tradeEntry.getProfit()!= null){
            return tradeEntry.getProfit()/tradeEntry.getCapital()*100;
        }
        return null;
    }

    private static Double getT2Percent(TradeEntry tradeEntry) {
        if(tradeEntry.getT2()== null)
            return null;
        else
            return Math.abs(tradeEntry.getT2()-tradeEntry.getT1())/tradeEntry.getT1()*100;
    }

    private static double getT1Percent(TradeEntry tradeEntry) {
        return Math.abs(tradeEntry.getT1()- tradeEntry.getEntryPrice())/tradeEntry.getEntryPrice() *100;
    }

    private static double getRiskPercent(TradeEntry tradeEntry) {
        return 100*tradeEntry.getQuantity()*Math.abs(tradeEntry.getSL()-tradeEntry.getEntryPrice())/tradeEntry.getCapital();
    }

    private static double getSLPercent(TradeEntry tradeEntry) {
        return Math.abs(tradeEntry.getSL()- tradeEntry.getEntryPrice())/tradeEntry.getEntryPrice() *100;
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
